package com.example.navdeep_bilin_myruns3.viewmodel

import androidx.lifecycle.*
import com.example.navdeep_bilin_myruns3.data.ExerciseEntryEntity
import com.example.navdeep_bilin_myruns3.data.ExerciseRepository
import kotlinx.coroutines.flow.map

class HistoryViewModel(private val repo: ExerciseRepository) : ViewModel() {
    val entriesLiveData: LiveData<List<ExerciseEntryEntity>> =
        repo.allEntries.map { it }.asLiveData() // convert flow to livedata for frag observe
}

class HistoryViewModelFactory(private val repo: ExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // safe cast verified by a check
            return HistoryViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class") // fail on wrong type
    }
}

//* Responsibilities:
// *  - Convert DAO Flow<List<ExerciseEntryEntity>> to LiveData for the UI

