package com.example.navdeep_bilin_myruns3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_entries")
data class ExerciseEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val inputType: Int,          // 0 manual, 1 GPS, 2 automatic
    val activityType: Int,       // running, cycling, etc.
    val dateTimeMillis: Long,    // epoch millis
    val durationSec: Double,     // seconds
    val distanceMeters: Double,  // meters
    val avgPace: Double?,        // optional for MR3
    val avgSpeed: Double?,       // optional for MR3
    val calorie: Double?,
    val climbMeters: Double?,    // meters
    val heartRate: Double?,
    val comment: String?,
    val locationBlob: ByteArray? // keep for MR4, can be null in MR3
)
