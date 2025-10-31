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
        @Volatile private var INSTANCE: MyRunsDatabase? = null // enforces single instance

        fun getInstance(context: Context): MyRunsDatabase =
            INSTANCE ?: synchronized(this) { // double check locking pattern
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MyRunsDatabase::class.java,
                    "myruns3.db"
                ).build().also { INSTANCE = it }
            }
    }
}

// * Responsibilities:
// *  - Builds and exposes the database and DAO
// *  - Enforces single instance via @Volatile + synchronized getInstance
// *
// * Migration:
// *  - Bump version when schema changes and define proper migrations
