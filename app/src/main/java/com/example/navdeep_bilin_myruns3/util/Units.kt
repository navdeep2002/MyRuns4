package com.example.navdeep_bilin_myruns3.util

import android.content.Context
import androidx.preference.PreferenceManager
import kotlin.math.roundToInt

object Units {
    enum class System { METRIC, IMPERIAL }

    fun system(ctx: android.content.Context): System {
        val sp = androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx)

        // 1) Try string-based ListPreference (common): "km" or "mi"
        val str = sp.getString("pref_units", null) // <-- update key if different
        if (str != null) {
            return if (str.equals("km", true) || str.equals("metric", true)) System.METRIC else System.IMPERIAL
        }

        // 2) Fallback to boolean switch (your earlier version)
        val metric = sp.getBoolean("pref_key_use_metric", true)
        return if (metric) System.METRIC else System.IMPERIAL
    }

    fun metersToPreferred(context: Context, meters: Double): Pair<Double, String> {
        return if (system(context) == System.METRIC) metersToKm(meters) to "Kilometers"
        else metersToMiles(meters) to "Miles"
    }

    fun preferredToMeters(ctx: android.content.Context, value: Double): Double =
        if (system(ctx) == System.METRIC) value * 1000.0 else value * 1609.344

    private fun metersToKm(m: Double) = m / 1000.0
    private fun kmToMeters(km: Double) = km * 1000.0
    private fun metersToMiles(m: Double) = m / 1609.344
    private fun milesToMeters(mi: Double) = mi * 1609.344

    fun formatDistance(ctx: android.content.Context, meters: Double): String =
        if (system(ctx) == System.METRIC)
            String.format(java.util.Locale.getDefault(), "%.2f Kilometers", meters / 1000.0)
        else
            String.format(java.util.Locale.getDefault(), "%.2f Miles", meters / 1609.344)

    fun minutesSecondsFromDecimalMinutes(decimalMinutes: Double): Pair<Int, Int> {
        val mins = decimalMinutes.toInt()
        val secs = ((decimalMinutes - mins) * 60.0).roundToInt()
        return if (secs == 60) mins + 1 to 0 else mins to secs
    }

    fun formatDurationFromDecimalMinutes(decimalMinutes: Double): String {
        val (m, s) = minutesSecondsFromDecimalMinutes(decimalMinutes)
        return "${m}mins ${s}secs"
    }

    fun formatDurationFromSeconds(seconds: Double): String {
        val total = seconds.toInt()
        val m = total / 60
        val s = total % 60
        return "${m}mins ${s}secs"
    }
}
