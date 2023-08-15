package com.softtimer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.softtimer.repository.DataStoreKeys
import com.softtimer.service.TimerService
import com.softtimer.ui.TimerScreen
import com.softtimer.ui.theme.SoftTImerTheme
import kotlinx.coroutines.launch

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
        lifecycleScope.launch {
            viewModel.readFromDataStore(key = DataStoreKeys.LAST_HOUR).collect { hours ->
                if (hours != null) viewModel.hPickerState = hours
            }
            viewModel.readFromDataStore(key = DataStoreKeys.LAST_MIN).collect { minutes ->
                if (minutes != null) viewModel.minPickerState = minutes
            }
            viewModel.readFromDataStore(key = DataStoreKeys.LAST_SEC).collect { seconds ->
                if (seconds != null) {
                    viewModel.sPickerState = seconds
                }
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

        viewModel.saveToDataStore(
            key = DataStoreKeys.LAST_HOUR,
            value = timerService.duration.inWholeHours.toInt()
        )
        viewModel.saveToDataStore(
            key = DataStoreKeys.LAST_MIN,
            value = timerService.duration.inWholeMinutes.toInt()
        )
        viewModel.saveToDataStore(
            key = DataStoreKeys.LAST_SEC,
            value = timerService.duration.inWholeSeconds.toInt()
        )
    }
}