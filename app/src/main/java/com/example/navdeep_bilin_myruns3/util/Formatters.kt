package com.example.navdeep_bilin_myruns3.util

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object Formatters {
    private fun dateTimeFormatter(): SimpleDateFormat =
        SimpleDateFormat("HH:mm:ss MMM dd yyyy", Locale.getDefault())

    fun titleLine(context: Context, inputType: Int, activityType: String, timeMillis: Long): String {
        val inputLabel = when (inputType) { 0 -> "Manual Entry"; 1 -> "GPS"; else -> "Automatic" }
        val time = dateTimeFormatter().format(Date(timeMillis))
        return "$inputLabel: $activityType, $time"
    }

    fun subtitleLine(context: Context, distanceMeters: Double, durationSec: Double): String {
        val dist = Units.formatDistance(context, distanceMeters)
        val dur = Units.formatDurationFromSeconds(durationSec)
        return "$dist, $dur"
    }

    fun dateThenTime(millis: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd yyyy, HH:mm:ss", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(millis))
    }
}
