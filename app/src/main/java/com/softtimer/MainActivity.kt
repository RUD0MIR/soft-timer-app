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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
        } else {
            window.apply {
                addFlags(
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                )
            }
        }
        setContent {
            SoftTImerTheme {
                if (isBound) {
                    TimerScreen(
                        timerService = timerService,
                        viewModel = viewModel
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
    }
}