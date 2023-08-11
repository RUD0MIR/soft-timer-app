package com.softtimer.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    //data store keys for Clock.kt
    val CLOCK_INITIAL_START_KEY = booleanPreferencesKey("clockInitialStart")
    val CLOCK_PROGRESS_BAR_SWEEP_ANGLE_KEY = floatPreferencesKey("progressBarSweepAngleTarget")
    val CLOCK_SIZE_MODIFIER_KEY = floatPreferencesKey("clockSizeModifier")

    //data store keys for PickerSection.kt
    val PICKER_VISIBILITY_KEY = floatPreferencesKey("pickerVisibilityValue")
    val PICKER_IS_VISIBLE_KEY = booleanPreferencesKey("isVisible")

    val TIMER_STATE_KEY = stringPreferencesKey("timerState")
}