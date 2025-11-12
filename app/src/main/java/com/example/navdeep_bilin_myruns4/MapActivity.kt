package com.example.navdeep_bilin_myruns4

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // referenced from lecture, but written in my own implementation
        // No data to save yet; just return to Start tab.
        findViewById<Button>(R.id.btnSave).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
    }
}