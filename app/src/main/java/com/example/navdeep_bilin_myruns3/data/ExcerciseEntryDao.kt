package com.example.navdeep_bilin_myruns3.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseEntryDao {
    @Insert suspend fun insert(entry: ExerciseEntryEntity): Long

    @Query("SELECT * FROM exercise_entries ORDER BY dateTimeMillis DESC")
    fun getAll(): Flow<List<ExerciseEntryEntity>>

    @Query("SELECT * FROM exercise_entries WHERE id = :id")
    suspend fun getById(id: Long): ExerciseEntryEntity?

    @Query("DELETE FROM exercise_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete suspend fun delete(entry: ExerciseEntryEntity)
}
