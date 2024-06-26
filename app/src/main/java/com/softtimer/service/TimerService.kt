package com.softtimer.service

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.softtimer.MainActivity
import com.softtimer.util.Constants.ACTION_SERVICE_RESET
import com.softtimer.util.Constants.ACTION_SERVICE_START
import com.softtimer.util.Constants.ACTION_SERVICE_STOP
import com.softtimer.util.Constants.NOTIFICATION_CHANNEL_ID
import com.softtimer.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.softtimer.util.Constants.NOTIFICATION_ID
import com.softtimer.util.Constants.STOPWATCH_STATE
import com.softtimer.util.absPad
import com.softtimer.util.formatTime
import com.softtimer.util.getDurationInSec
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


private const val TAG = "TimerService"

@AndroidEntryPoint
class TimerService : Service() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = StopwatchBinder()

    private lateinit var timer: Timer

    private val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    lateinit var ringtone: Ringtone

    var timerState by mutableStateOf(TimerState.Idle)

    var duration: Duration = Duration.ZERO

    var showOvertime by mutableStateOf(false)
    var secondReset by mutableStateOf(false)

    var overtimeDuration: Duration = Duration.ZERO
        private set

    var overtimeMins by mutableIntStateOf(0)
    var overtimeSecs by mutableIntStateOf(0)
    var overtimeMillis by mutableIntStateOf(0)

    fun getOvertimeMillis(): String {
        return String.format("%02d", overtimeMillis / 10000000)
    }

    var hState by mutableIntStateOf(0)
    var minState by mutableIntStateOf(0)
    var sState by mutableIntStateOf(0)

    lateinit var powerManager: PowerManager
    var isScreenOffWhenTimeExpired = false

    private val scope = TimerServiceScope()

    override fun onCreate() {
        super.onCreate()
        ringtone = RingtoneManager.getRingtone(applicationContext, alarmSoundUri)
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
    }

    override fun onBind(p0: Intent?) = binder

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopForegroundService()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(STOPWATCH_STATE)) {
            TimerState.Running.name -> {
                setStopButton()
                startForegroundService()
                startTimer { hours, minutes, seconds, millis ->
                    updateNotification(
                        hours = hours,
                        minutes = minutes,
                        seconds = seconds,
                        millis = millis
                    )
                }
            }

            TimerState.Paused.name -> {
                stopTimer()
                setResumeButton()
            }

            TimerState.Reset.name -> {
                stopTimer()
                resetTimer()
                stopForegroundService()
            }
        }
        intent?.action.let {
            when (it) {
                ACTION_SERVICE_START -> {
                    setStopButton()
                    startForegroundService()
                    startTimer { hours, minutes, seconds, millis ->
                        updateNotification(
                            hours = hours,
                            minutes = minutes,
                            seconds = seconds,
                            millis = millis
                        )
                    }
                }

                ACTION_SERVICE_STOP -> {
                    stopTimer()
                    setResumeButton()
                }

                ACTION_SERVICE_RESET -> {
                    stopTimer()
                    resetTimer()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        timer.cancel()
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startTimer(onTick: (h: String, m: String, s: String, millis: String) -> Unit) {
        showOvertime = false
        overtimeDuration = Duration.ZERO

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ringtone.isLooping = true
        }

        duration = getDurationInSec(
            h = hState,
            min = minState,
            s = sState
        )

        if (duration != Duration.ZERO) {
            this@TimerService.timerState = TimerState.Running

            timer = fixedRateTimer(initialDelay = 1600L, period = 1000L) {//1000 1000
                duration = duration.minus(1.seconds)
                updateTimeUnits()
                onTick(hState.absPad(), minState.absPad(), sState.absPad(), getOvertimeMillis())

                if (duration.inWholeSeconds < 0 && timerState == TimerState.Running) {
                    onTimerExpired()
                    timer.cancel()
                    duration = Duration.ZERO

                    timer = fixedRateTimer(initialDelay = 1L, period = 1L) {
                        duration = duration.plus(1.milliseconds)
                        updateTimeUnits(isTimeExpired = true)
                        onTick(
                            hState.absPad(),
                            overtimeMins.absPad(),
                            overtimeSecs.absPad(),
                            getOvertimeMillis()
                        )

                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                            if (timerState == TimerState.Ringing && !ringtone.isPlaying) ringtone.play()
                        }
                    }
                }
            }
        }
    }

    private fun onTimerExpired() {
        this@TimerService.timerState = TimerState.Ringing
        ringtone.play()
        showOvertime = true

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        isScreenOffWhenTimeExpired = !powerManager.isInteractive

        Log.d(TAG, keyguardManager.isKeyguardLocked.toString())

        if (isScreenOffWhenTimeExpired && keyguardManager.isKeyguardLocked) {
            Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(this)
            }
        }
    }

    private fun stopTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        this.timerState = TimerState.Paused
    }

    private fun resetTimer() {
        ringtone.stop()
        this@TimerService.timerState = TimerState.Idle

        if (timerState == TimerState.Ringing) {
            secondReset = true
        }
    }

    private fun updateTimeUnits(isTimeExpired: Boolean = false) {
        if (isTimeExpired) {
            duration.toComponents { _, minutes, seconds, milliseconds ->
                this@TimerService.overtimeMins = minutes
                this@TimerService.overtimeSecs = seconds
                this@TimerService.overtimeMillis = milliseconds
            }
        } else {
            duration.toComponents { hours, minutes, seconds, _ ->
                this@TimerService.hState = hours.toInt()
                this@TimerService.minState = minutes
                this@TimerService.sState = seconds
            }
        }
    }

    private fun updateNotification(
        hours: String,
        minutes: String,
        seconds: String,
        millis: String
    ) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                formatTime(
                    isTimeExpired = timerState == TimerState.Ringing,
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    millis = millis
                )
            ).build()
        )
    }

    @SuppressLint("RestrictedApi")
    private fun setStopButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Stop",
                ServiceHelper.stopPendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun setResumeButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Resume",
                ServiceHelper.resumePendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun resetState() {
        hState = 0
        minState = 0
        sState = 0
        showOvertime = false
    }

    inner class StopwatchBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }
}

class TimerServiceScope : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    fun cancel() {
        job.cancel()
    }
}

enum class TimerState {
    Idle,
    Running,
    Paused,
    Reset,
    Ringing
}