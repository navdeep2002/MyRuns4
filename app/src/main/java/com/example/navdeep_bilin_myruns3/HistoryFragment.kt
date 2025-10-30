package com.example.navdeep_bilin_myruns3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

        // lecture reference: standard fragment inflation pattern from week 4
    ): View? = inflater.inflate(R.layout.fragment_history, container, false)
}
