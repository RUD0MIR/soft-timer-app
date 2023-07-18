package com.softtimer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.softtimer.ui.theme.MidAnimationDelay
import com.softtimer.util.Constants.ACTION_SERVICE_RESET
import com.softtimer.util.Constants.ACTION_SERVICE_START
import com.softtimer.util.Constants.ACTION_SERVICE_STOP
import com.softtimer.util.Constants.NOTIFICATION_CHANNEL_ID
import com.softtimer.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.softtimer.util.Constants.NOTIFICATION_ID
import com.softtimer.util.Constants.STOPWATCH_STATE
import com.softtimer.util.formatTime
import com.softtimer.util.getDurationInSec
import com.softtimer.util.pad
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
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

    var timerState by mutableStateOf(TimerState.Idle)
        private set

    var hState by mutableStateOf(0)
    var minState by mutableStateOf(0)//always zero
    var sState by mutableStateOf(0)

    var duration: Duration = ZERO
        private set


    fun getH(): String {
        return hState.pad()
    }

    fun getMin(): String {
        return minState.pad()
    }

    fun getS(): String {
        return sState.pad()
    }

    override fun onBind(p0: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(STOPWATCH_STATE)) {
            TimerState.Started.name -> {
                setStopButton()
                startForegroundService()
                startTimer { hours, minutes, seconds ->
                    updateNotification(hours = hours, minutes = minutes, seconds = seconds)
                }
            }

            TimerState.Paused.name -> {
                stopTimer()
                setResumeButton()
            }

            TimerState.Reset.name -> {
                stopTimer()
                cancelTimer()
                stopForegroundService()
            }
        }
        intent?.action.let {
            when (it) {
                ACTION_SERVICE_START -> {
                    setStopButton()
                    startForegroundService()
                    startTimer { hours, minutes, seconds ->
                        updateNotification(hours = hours, minutes = minutes, seconds = seconds)
                    }
                }

                ACTION_SERVICE_STOP -> {
                    stopTimer()
                    setResumeButton()
                }

                ACTION_SERVICE_RESET -> {
                    stopTimer()
                    cancelTimer()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer(onTick: (h: String, m: String, s: String) -> Unit) {
        duration = getDurationInSec(
            h = hState,
            min = minState,
            s = sState
        )

        val isTimeSet = duration != ZERO

        if (isTimeSet) {
            this@TimerService.timerState = TimerState.Started
            Log.d(TAG, "before")
            runBlocking {
                launch {
                    delay(MidAnimationDelay)
                    Log.d(TAG, "after")
                    this@TimerService.timerState = TimerState.Running
                    timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
                        duration = duration.minus(1.seconds)
                        updateTimeUnits()

                        onTick(getH(), getMin(), getS())
                    }
                }
            }

//            if (duration.inWholeSeconds == 0L && timerState == TimerState.Running) {
//                onTimerExpired()
//            }
        }

    }

    private fun stopTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        this.timerState = TimerState.Paused
    }

    private fun cancelTimer() {
        duration = Duration.ZERO
        this.timerState = TimerState.Idle
        updateTimeUnits()
    }

    private fun updateTimeUnits() {
        duration.toComponents { hours, minutes, seconds, _ ->
            this@TimerService.hState = hours.toInt()
            this@TimerService.minState = minutes
            this@TimerService.sState = seconds
        }
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
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

    private fun updateNotification(hours: String, minutes: String, seconds: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                formatTime(
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                )
            ).build()
        )
    }

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

    inner class StopwatchBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    private fun onTimerExpired() {
        runBlocking {
            this@TimerService.timerState = TimerState.Reset

            delay(MidAnimationDelay)

            this@TimerService.timerState = TimerState.Idle
            hState = 0
            minState = 0
            sState = 0
            duration = ZERO
        }
        //TODO some sound
    }
}


enum class TimerState {
    Idle,
    Started,
    Running,
    Paused,
    Reset,
    Ringing
}