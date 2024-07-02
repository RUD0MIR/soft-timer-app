package com.softtimer

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.softtimer.service.TimerService
import com.softtimer.ui.TimerScreen
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.Constants


private const val TAG = "MainActivity"

//TODO: show dialog about app activity if unused
//TODO: write some tests

class MainActivity : ComponentActivity() {
    private val viewModel: TimerViewModel by viewModels()
    private var isBound by mutableStateOf(false)
    private lateinit var timerService: TimerService

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.StopwatchBinder
            timerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setSystemThemeFromDataStore()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
            setShowWhenLocked(true)
        } else {
            window.apply {
                addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
                addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                )
            }
        }

        setContent {
            SoftTImerTheme(dynamicColor = false) {
                if (isBound) {
                    TimerScreen(
                        timerState = timerService.timerState,
                        duration = timerService.duration,
                        hState = timerService.hState,
                        minState = timerService.minState,
                        sState = timerService.sState,
                        overtimeMins = timerService.overtimeMins,
                        overtimeSecs = timerService.overtimeSecs,
                        overtimeMillis = timerService.getOvertimeMillis(),
                        hPickerState = viewModel.hPickerState,
                        minPickerState = viewModel.minPickerState,
                        sPickerState = viewModel.sPickerState,
                        isDarkTheme = viewModel.isDarkTheme,
                        onSystemThemeChange = viewModel::changeSystemTheme,
                        onTimerServiceHStateChange = {
                            timerService.hState = viewModel.hPickerState
                        },
                        onTimerServiceMinStateChange = {
                            timerService.minState = viewModel.minPickerState
                        },
                        onTimerServiceSStateChange = {
                            timerService.sState = viewModel.sPickerState
                        },
                        onHPickerStateChanged = { selectedHour ->
                            timerService.hState = selectedHour
                            viewModel.hPickerState = selectedHour
                        },
                        onMinPickerStateChanged = { selectedMin ->
                            timerService.minState = selectedMin
                            viewModel.minPickerState = selectedMin
                        },
                        onSecPickerStateChanged = { selectedSec ->
                            timerService.sState = selectedSec
                            viewModel.sPickerState = selectedSec
                        },
                        onSecondReset = { timerService.secondReset = true },
                        onStateReset = {
                            viewModel.resetTimerState()
                            timerService.resetState()
                        }
                    )
                }
            }
        }

        requestPermissions(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun requestPermissions(vararg permissions: String) {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            result.entries.forEach {
                Log.d("MainActivity", "${it.key} = ${it.value}")
            }
        }
        requestPermissionLauncher.launch(permissions.asList().toTypedArray())
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false

        viewModel.saveThemeStateToDataStore()
    }
}