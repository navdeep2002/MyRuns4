package com.example.navdeep_bilin_myruns3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ExerciseEntryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MyRunsDatabase : RoomDatabase() {
    abstract fun exerciseEntryDao(): ExerciseEntryDao

    companion object {
        @Volatile private var INSTANCE: MyRunsDatabase? = null

        fun getInstance(context: Context): MyRunsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MyRunsDatabase::class.java,
                    "myruns3.db"
                ).build().also { INSTANCE = it }
            }
    }
}
