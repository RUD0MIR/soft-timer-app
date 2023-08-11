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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.softtimer.service.ServiceHelper
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.ui.TimerScreen
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.Constants.ACTION_SERVICE_RESET
import com.softtimer.util.DataStoreKeys
import com.softtimer.util.getNumbersWithPad
import com.softtimer.util.pad
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
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

    override fun onResume() {
        super.onResume()
//        lifecycleScope.launch {
//            if(isBound) {
//                viewModel.readFromDataStore(
//                    key = DataStoreKeys.TIMER_STATE_KEY,
//                ).collect {timerStateName ->
//                    timerService.timerState = timerService.getTimerStateByName(timerStateName)
//                }
//            }

//            viewModel.readFromDataStore(
//                key = DataStoreKeys.CLOCK_INITIAL_START_KEY,
//            ).collect {
//                viewModel.clockInitialStart = it ?: false
//            }
//
//            viewModel.readFromDataStore(
//                key = DataStoreKeys.CLOCK_PROGRESS_BAR_SWEEP_ANGLE_KEY,
//            ).collect {
//                viewModel.progressBarSweepAngleTarget = it ?: 0f
//            }
//            viewModel.readFromDataStore(
//                key = TimerViewModel.CLOCK_PROGRESS_BAR_SWEEP_ANGLE_KEY,
//            ).collect {
//                viewModel.progressBarSweepAngleTarget = it ?: viewModel.progressBarSweepAngleTarget
//            }
//
//            viewModel.readFromDataStore(
//                key = TimerViewModel.CLOCK_SIZE_MODIFIER_KEY,
//            ).collect {
//                viewModel.clockSizeModifier = it ?: viewModel.clockSizeModifier
//            }
//
//            viewModel.readFromDataStore(
//                key = TimerViewModel.PICKER_VISIBILITY_KEY,
//            ).collect {
//                viewModel.pickerVisibilityValue = it ?: viewModel.pickerVisibilityValue
//            }
//
//            viewModel.readFromDataStore(
//                key = TimerViewModel.PICKER_IS_VISIBLE_KEY,
//            ).collect {
//                viewModel.isVisible = it ?: viewModel.isVisible
//            }
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onPause() {
        super.onPause()
//        if(isBound) {
//            viewModel.saveToDataStore(
//                key = DataStoreKeys.TIMER_STATE_KEY,
//                value = timerService.timerState.name
//            )
//        }
//
//        viewModel.saveToDataStore(
//            key = DataStoreKeys.CLOCK_INITIAL_START_KEY,
//            value = viewModel.clockInitialStart
//        )
//        viewModel.saveToDataStore(
//            key = DataStoreKeys.CLOCK_PROGRESS_BAR_SWEEP_ANGLE_KEY,
//            value = viewModel.progressBarSweepAngleTarget
//        )
//        viewModel.saveToDataStore(
//            key = DataStoreKeys.CLOCK_SIZE_MODIFIER_KEY,
//            value = viewModel.clockSizeModifier
//        )
//        viewModel.saveToDataStore(
//            key = DataStoreKeys.PICKER_VISIBILITY_KEY,
//            value = viewModel.pickerVisibilityValue
//        )
//        viewModel.saveToDataStore(
//            key = DataStoreKeys.PICKER_IS_VISIBLE_KEY,
//            value = viewModel.isVisible
//        )
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}