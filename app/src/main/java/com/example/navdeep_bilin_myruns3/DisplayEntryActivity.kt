package com.example.navdeep_bilin_myruns3

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.navdeep_bilin_myruns3.data.ExerciseEntryEntity
import com.example.navdeep_bilin_myruns3.data.ExerciseRepository
import com.example.navdeep_bilin_myruns3.data.MyRunsDatabase
import com.example.navdeep_bilin_myruns3.util.Formatters
import com.example.navdeep_bilin_myruns3.util.Units
import com.example.navdeep_bilin_myruns3.viewmodel.DisplayEntryVMFactory
import com.example.navdeep_bilin_myruns3.viewmodel.DisplayEntryViewModel

class DisplayEntryActivity : AppCompatActivity() {

    private lateinit var vm: DisplayEntryViewModel
    private var entryId: Long = -1L

    // Views
    private lateinit var tvInputType: TextView
    private lateinit var tvActivityType: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvCalorie: TextView
    private lateinit var tvHeartRate: TextView
    private lateinit var btnDelete: Button
    private lateinit var btnBack: Button

    // Prefs for unit changes
    private lateinit var prefs: SharedPreferences
    private var currentEntry: ExerciseEntryEntity? = null
    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "pref_key_use_metric") {
            currentEntry?.let { bindUi(it) } // re-render with new units
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_entry)
        supportActionBar?.title = getString(R.string.app_name)

        // Read id passed from History list
        entryId = intent.getLongExtra("ENTRY_ID", -1L)

        // Views
        tvInputType  = findViewById(R.id.tvInputType)
        tvActivityType = findViewById(R.id.tvActivityType)
        tvDateTime   = findViewById(R.id.tvDateTime)
        tvDuration   = findViewById(R.id.tvDuration)
        tvDistance   = findViewById(R.id.tvDistance)
        tvCalorie    = findViewById(R.id.tvCalorie)
        tvHeartRate  = findViewById(R.id.tvHeartRate)
        btnDelete    = findViewById(R.id.btnDelete)
        btnBack      = findViewById(R.id.btnBack)

        // Prefs
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // VM wiring
        val dao = MyRunsDatabase.getInstance(this).exerciseEntryDao()
        val repo = ExerciseRepository(dao)
        val factory = DisplayEntryVMFactory(repo, entryId)
        vm = ViewModelProvider(this, factory)[DisplayEntryViewModel::class.java]

        // Observe and populate UI (single observer)
        vm.entry.observe(this) { e ->
            currentEntry = e
            if (e != null) bindUi(e)
        }
        vm.load()

        // Delete → remove from DB then finish back to History
        btnDelete.setOnClickListener {
            vm.delete { runOnUiThread { finish() } }
        }

        // Back → just finish
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

    private fun bindUi(e: ExerciseEntryEntity) {
        tvInputType.text = when (e.inputType) {
            0 -> "Manual Entry"
            1 -> "GPS"
            else -> "Automatic"
        }
        tvActivityType.text = activityName(e.activityType)
        tvDateTime.text = Formatters.dateThenTime(e.dateTimeMillis)      // Date then time
        tvDuration.text   = Units.formatDurationFromSeconds(e.durationSec)
        tvDistance.text   = Units.formatDistance(this, e.distanceMeters) // Miles/Km by preference
        tvCalorie.text    = "${e.calorie?.toInt() ?: 0} cals"
        tvHeartRate.text  = "${e.heartRate?.toInt() ?: 0} BPM"
    }

    private fun activityName(type: Int): String {
        val names = resources.getStringArray(R.array.activity_type_entries)
        return names.getOrNull(type) ?: "Unknown"
    }
}
