package com.example.navdeep_bilin_myruns3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.navdeep_bilin_myruns3.data.ExerciseEntryEntity
import com.example.navdeep_bilin_myruns3.data.ExerciseRepository
import com.example.navdeep_bilin_myruns3.data.MyRunsDatabase
import com.example.navdeep_bilin_myruns3.util.Formatters
import com.example.navdeep_bilin_myruns3.util.Units
import com.example.navdeep_bilin_myruns3.viewmodel.HistoryViewModel
import com.example.navdeep_bilin_myruns3.viewmodel.HistoryViewModelFactory
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


class HistoryFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<ExerciseEntryEntity>
    private lateinit var viewModel: HistoryViewModel

    private lateinit var prefs: SharedPreferences

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "pref_key_use_metric") {
            // Rebind rows so Units.formatDistance() re-runs with new preference
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        listView = view.findViewById(R.id.history_list)

        // Initialize database + repository + ViewModel
        val db = MyRunsDatabase.getInstance(requireContext())
        val repo = ExerciseRepository(db.exerciseEntryDao())
        val factory = HistoryViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        // Adapter with two-line layout
        adapter = object : ArrayAdapter<ExerciseEntryEntity>(
            requireContext(),
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            mutableListOf()
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = super.getView(position, convertView, parent)
                val title = row.findViewById<TextView>(android.R.id.text1)
                val subtitle = row.findViewById<TextView>(android.R.id.text2)
                val entry = getItem(position)

                if (entry != null) {
                    title.text = Formatters.titleLine(
                        requireContext(),
                        entry.inputType,
                        activityName(entry.activityType),
                        entry.dateTimeMillis
                    )
                    subtitle.text = "${Units.formatDistance(requireContext(), entry.distanceMeters)}, " +
                            Units.formatDurationFromSeconds(entry.durationSec)
                }
                return row
            }
        }

        listView.adapter = adapter

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())


        // Observe DB changes and update UI automatically
        viewModel.entriesLiveData.observe(viewLifecycleOwner) { entries ->
            adapter.clear()
            adapter.addAll(entries)
            adapter.notifyDataSetChanged()
        }

        // Click listener → open DisplayEntryActivity or MapActivity
        listView.setOnItemClickListener { _, _, position, _ ->
            val entry = adapter.getItem(position)
            if (entry != null) {
                val intent = if (entry.inputType == 0)
                    Intent(requireContext(), DisplayEntryActivity::class.java)
                else
                    Intent(requireContext(), MapActivity::class.java)

                intent.putExtra("ENTRY_ID", entry.id)
                startActivity(intent)
            }
        }

        return view
    }

    private fun activityName(type: Int): String {
        val activities = resources.getStringArray(R.array.activity_type_entries)
        return activities.getOrNull(type) ?: "Unknown"
    }

    override fun onResume() {
        super.onResume()
        prefs.registerOnSharedPreferenceChangeListener(prefListener)
        // defensively rebind in case we switched tabs right at insert commit time
        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(prefListener)
        super.onPause()
    }

}
