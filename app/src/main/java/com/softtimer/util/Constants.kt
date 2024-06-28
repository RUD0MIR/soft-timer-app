package com.softtimer.util

object Constants {
    const val ACTION_SERVICE_START = "ACTION_SERVICE_START"
    const val ACTION_SERVICE_STOP = "ACTION_SERVICE_STOP"
    const val ACTION_SERVICE_RESET = "ACTION_SERVICE_RESET"

    const val STOPWATCH_STATE = "STOPWATCH_STATE"

    const val NOTIFICATION_CHANNEL_ID = "STOPWATCH_NOTIFICATION_ID"
    const val NOTIFICATION_CHANNEL_NAME = "STOPWATCH_NOTIFICATION"
    const val NOTIFICATION_ID = 10

    const val CLICK_REQUEST_CODE = 100
    const val CANCEL_REQUEST_CODE = 101
    const val STOP_REQUEST_CODE = 102
    const val RESUME_REQUEST_CODE = 103

    const val CLOCK_MIN_SIZE = 1.1f
    const val CLOCK_MAX_SIZE = 1.4f

    const val MID_ANIMATION_DELAY = 1000L
    const val MID_ANIMATION_DURATION = MID_ANIMATION_DELAY.toInt() - 150
    const val SHORT_ANIMATION_DURATION = 500
}