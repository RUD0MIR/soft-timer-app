package com.softtimer

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softtimer.util.pad
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "TimerViewModel"

class TimerViewModel : ViewModel() {
//    val hState = PickerState()
//    val minState = PickerState()
//    val sState = PickerState()
//
//    fun getH(): String {
//        return hState.selectedItem.pad()
//    }
//
//    fun getMin(): String {
//        return minState.selectedItem.pad()
//    }
//
//    fun getS(): String {
//        return sState.selectedItem.pad()
//    }
//
//    var remainTime by mutableStateOf(0)
//
//
//    var timerState by mutableStateOf<TimerState>(TimerState.Initial)
//
//    fun startTimer() {
//        remainTime = getDurationInSec(
//            h = hState.selectedItem,
//            min = minState.selectedItem,
//            s = sState.selectedItem
//        )
//
//        if (remainTime != 0) {
//            viewModelScope.launch {
//                if(timerState == TimerState.Initial) {
//                    timerState = TimerState.Started
//                    delay(progressBarDelay)
//                }
//
//                timerState = TimerState.Running
//                while (remainTime >= 0 && timerState == TimerState.Running) {
//                    hState.selectedItem = remainTime / 3600
//                    minState.selectedItem = (remainTime % 3600) / 60
//                    sState.selectedItem = remainTime % 60
//
//                    delay(1000) // Delay for 1 second
//
//                    remainTime--
//
//                    if (remainTime == 0 && timerState == TimerState.Running) {
//                        onTimerExpired()
//                    }
//                }
//            }
//        }
//    }
//
//    fun pauseTimer() {
//        timerState = TimerState.Paused
//    }
//
//    fun restartTimer() {
//        viewModelScope.launch {
//            timerState = TimerState.Restarted
//
//            delay(progressBarDelay)
//
//            timerState = TimerState.Initial
//            hState.selectedItem = 0
//            minState.selectedItem = 0
//            sState.selectedItem = 0
//            remainTime = 0
//        }
//
//    }
}
