package com.example.navdeep_bilin_myruns3.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExerciseRepository(
    private val dao: ExerciseEntryDao,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {
    val allEntries: Flow<List<ExerciseEntryEntity>> = dao.getAll()

    suspend fun insert(entry: ExerciseEntryEntity): Long = withContext(io) { dao.insert(entry) }
    suspend fun getById(id: Long): ExerciseEntryEntity? = withContext(io) { dao.getById(id) }
    suspend fun deleteById(id: Long) = withContext(io) { dao.deleteById(id) }
}


// * Responsibilities:
// *  - Provide a clean API to ViewModels
// *  - Keep coroutines off the main thread (Dispatchers.IO)
// * Returned streams:
// *  - allEntries: Flow<List<ExerciseEntryEntity>> directly from DAO
// * Why repository:
// *  - Maintains MVVM boundaries and is ready for adding a remote source later