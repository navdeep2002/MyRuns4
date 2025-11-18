package com.example.navdeep_bilin_myruns4.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.navdeep_bilin_myruns4.R
import com.example.navdeep_bilin_myruns4.MapActivity


// This is the foreground service that receives location and updates during a workout

class TrackingService : Service(), LocationListener {

    companion object {
        const val ACTION_START = "mr4.START"
        const val ACTION_STOP  = "mr4.STOP"
        const val ACTION_LOC_BROADCAST = "mr4.LOCATION"

        const val EXTRA_LAT = "lat"
        const val EXTRA_LNG = "lng"
        const val EXTRA_TIME = "time"
        const val EXTRA_SPEED = "speed"

        private const val CHANNEL_ID = "mr4_tracking"
        private const val NOTIFY_ID = 1001
    }

    private lateinit var lm: LocationManager

    override fun onCreate() {
        super.onCreate()
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        createChannel() // creates the notification
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // handles the start and stop commands from MapActivity
        when (intent?.action) {
            ACTION_START -> {

                // start service in foreground
                startForeground(NOTIFY_ID, buildNotification("Service has started (tap to return)"))
                try {
                    lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000L, // 2 second interval
                        0f,
                        this
                    )
                } catch (_: SecurityException) {
                    // no location permission, run without updates
                }
            }
            ACTION_STOP -> {

                // stop receivng the updates and shut down the service
                try { lm.removeUpdates(this) } catch (_: Exception) {}
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {

        // clean up resources when seervice is destroyed
        try { lm.removeUpdates(this) } catch (_: Exception) {}
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ---- LocationListener ----
    override fun onLocationChanged(loc: Location) {

        //broadcast the latest location and stats back to the MapActivity
        sendBroadcast(
            Intent(ACTION_LOC_BROADCAST)
                .setPackage(packageName)
                .putExtra(EXTRA_LAT, loc.latitude)
                .putExtra(EXTRA_LNG, loc.longitude)
                .putExtra(EXTRA_TIME, loc.time)
                .putExtra(EXTRA_SPEED, loc.speed)
        )
    }

    // ---- Notification helpers ----
    private fun buildNotification(text: String): Notification {
        val tapIntent = Intent(this, MapActivity::class.java).apply {
            // Reuse the existing MapActivity instead of creating a new one
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val tap = PendingIntent.getActivity(
            this,
            0,
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setContentIntent(tap)
            .setOngoing(true)
            .build()
    }

    // create the required notification
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(
                CHANNEL_ID,
                "MR4 Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            mgr.createNotificationChannel(ch)
        }
    }
}

