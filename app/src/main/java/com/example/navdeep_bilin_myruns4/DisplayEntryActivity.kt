package com.example.navdeep_bilin_myruns4

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.navdeep_bilin_myruns4.util.Formatters
import com.example.navdeep_bilin_myruns4.util.Units
import com.example.navdeep_bilin_myruns4.viewmodel.DisplayEntryVMFactory
import com.example.navdeep_bilin_myruns4.viewmodel.DisplayEntryViewModel
import com.example.navdeep_bilin_myruns4.data.ExerciseEntryEntity
import com.example.navdeep_bilin_myruns4.data.ExerciseRepository
import com.example.navdeep_bilin_myruns4.data.MyRunsDatabase
import com.example.navdeep_bilin_myruns4.util.LocationCodec
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

// activity that replay's a saved workout or activity type on the google map or as a manual entry
class DisplayEntryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var vm: DisplayEntryViewModel
    private var entryId: Long = -1L

    // Info views
    private lateinit var tvInputType: TextView
    private lateinit var tvActivityType: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvCalorie: TextView
    private lateinit var tvHeartRate: TextView
    private lateinit var btnDelete: Button
    private lateinit var btnBack: Button

    // Map + stats
    private var tvMapStats: TextView? = null
    private var mapContainer: View? = null
    private var gMap: GoogleMap? = null
    private var routePoints: List<LatLng> = emptyList()

    // Prefs
    private lateinit var prefs: SharedPreferences
    private var currentEntry: ExerciseEntryEntity? = null
    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "pref_key_use_metric") {
            currentEntry?.let { e ->
                bindUi(e)
                updateMapStats(e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_entry)
        supportActionBar?.title = getString(R.string.app_name)

        // load the excerciseentry id from the intent extras
        entryId = intent.getLongExtra("ENTRY_ID", -1L)

        // Info views
        tvInputType    = findViewById(R.id.tvInputType)
        tvActivityType = findViewById(R.id.tvActivityType)
        tvDateTime     = findViewById(R.id.tvDateTime)
        tvDuration     = findViewById(R.id.tvDuration)
        tvDistance     = findViewById(R.id.tvDistance)
        tvCalorie      = findViewById(R.id.tvCalorie)
        tvHeartRate    = findViewById(R.id.tvHeartRate)
        btnDelete      = findViewById(R.id.btnDelete)
        btnBack        = findViewById(R.id.btnBack)

        // Map + overlay
        tvMapStats = findViewById(R.id.tvMapStats)
        mapContainer = findViewById(R.id.mapFrame)

        // set up the map fragment for the replay mode
        val mapFrag = supportFragmentManager
            .findFragmentById(R.id.detailMap) as? SupportMapFragment
        mapFrag?.getMapAsync(this)

        // Hide map by default (manual / automatic entries)
        mapContainer?.visibility = View.GONE
        tvMapStats?.visibility = View.GONE

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val dao = MyRunsDatabase.getInstance(this).exerciseEntryDao()
        val repo = ExerciseRepository(dao)
        val factory = DisplayEntryVMFactory(repo, entryId)
        vm = ViewModelProvider(this, factory)[DisplayEntryViewModel::class.java]

        // observe the loaded exerciseentry and update the user interface and map when its ready
        vm.entry.observe(this) { e ->
            currentEntry = e
            if (e != null) {

                // populate the stats textview with saved entry data
                bindUi(e)

                if (e.inputType == 1 && e.locationBlob != null) {
                    // GPS entry with stored route
                    routePoints = try {
                        LocationCodec.decode(e.locationBlob)
                    } catch (_: Exception) {
                        emptyList()
                    }

                    if (routePoints.isNotEmpty()) {
                        mapContainer?.visibility = View.VISIBLE
                        tvMapStats?.visibility = View.VISIBLE
                        drawRouteIfReady() // draw the stored polyline path for the exercise
                        updateMapStats(e)
                    } else {
                        mapContainer?.visibility = View.GONE
                        tvMapStats?.visibility = View.GONE
                    }
                } else {
                    // Manual / automatic or GPS with no route
                    routePoints = emptyList()
                    mapContainer?.visibility = View.GONE
                    tvMapStats?.visibility = View.GONE
                }
            }
        }
        vm.load()

        btnDelete.setOnClickListener {
            vm.delete { runOnUiThread { finish() } }
        }
        btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        prefs.registerOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(prefListener)
        super.onPause()
    }

    // Map callback when map is ready to draw saved route
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap?.uiSettings?.isZoomControlsEnabled = true
        drawRouteIfReady()
    }

    // populate the stats textviews with saved entry data
    private fun bindUi(e: ExerciseEntryEntity) {
        tvInputType.text = when (e.inputType) {
            0 -> "Manual Entry"
            1 -> "GPS"
            else -> "Automatic"
        }
        tvActivityType.text = activityName(e.activityType)
        tvDateTime.text   = Formatters.dateThenTime(e.dateTimeMillis)
        tvDuration.text   = Units.formatDurationFromSeconds(e.durationSec)
        tvDistance.text   = Units.formatDistance(this, e.distanceMeters)
        tvCalorie.text    = "${e.calorie?.toInt() ?: 0} cals"
        tvHeartRate.text  = "${e.heartRate?.toInt() ?: 0} BPM"
    }

    private fun activityName(type: Int): String {
        val names = resources.getStringArray(R.array.activity_type_entries)
        return names.getOrNull(type) ?: "Unknown"
    }

    // Draw saved GPS route
    private fun drawRouteIfReady() {
        val map = gMap ?: return
        if (routePoints.isEmpty()) return

        map.clear()

        map.addPolyline(
            PolylineOptions()
                .addAll(routePoints)
                .color(Color.BLUE)
                .width(8f)
        )

        val start = routePoints.first()
        val end   = routePoints.last()

        map.addMarker(
            MarkerOptions()
                .position(start)
                .title("Start")
                .icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN
                    )
                )
        )

        map.addMarker(
            MarkerOptions()
                .position(end)
                .title("End")
                .icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED
                    )
                )
        )


        // move the camera to show the route on screen
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 16f))
    }

    // Stats overlay for GPS entries
    private fun updateMapStats(e: ExerciseEntryEntity) {
        val statsView = tvMapStats ?: return
        if (e.inputType != 1) {
            statsView.text = ""
            return
        }

        val distanceM = e.distanceMeters ?: 0.0
        val durationSec = e.durationSec ?: 0.0
        val useMetric = prefs.getBoolean("pref_key_use_metric", false)

        val (dist, distUnit) = if (useMetric) {
            String.format("%.2f", distanceM / 1000.0) to "Kilometers"
        } else {
            String.format("%.2f", distanceM * 0.000621371) to "Miles"
        }

        val avgMps = if (durationSec > 0.0) distanceM / durationSec else 0.0
        val (avg, speedUnit) = if (useMetric) {
            String.format("%.2f", avgMps * 3.6) to "km/h"
        } else {
            String.format("%.2f", avgMps * 2.23694) to "mph"
        }

        val cur = "N/A"  // history view isn't live-tracking

        val typeLabel = activityName(e.activityType)

        statsView.text =
            "Type: $typeLabel\n" +
                    "Avg speed: $avg $speedUnit\n" +
                    "Cur speed: $cur $speedUnit\n" +
                    "Climb: 0 Miles\n" +
                    "Calorie: ${e.calorie ?: 0.0}\n" +
                    "Distance: $dist $distUnit"
    }
}


