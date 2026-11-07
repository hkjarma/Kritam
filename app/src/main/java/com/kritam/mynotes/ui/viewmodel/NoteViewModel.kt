package com.kritam.mynotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kritam.mynotes.data.Note
import com.kritam.mynotes.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NoteFilter {
    ALL,
    PENDING,
    DONE
}

data class NoteUiState(
    val notes: List<Note> = emptyList(),
    val selectedFilter: NoteFilter = NoteFilter.ALL,
    val searchQuery: String = "",
    val pendingCount: Int = 0
)

class NoteViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val selectedFilterFlow = MutableStateFlow(NoteFilter.ALL)
    private val searchQueryFlow = MutableStateFlow("")

    val selectedFilter: StateFlow<NoteFilter> = selectedFilterFlow
    val searchQuery: StateFlow<String> = searchQueryFlow

    val uiState: StateFlow<NoteUiState> = combine(
        repository.getAllNotes(),
        selectedFilterFlow,
        searchQueryFlow
    ) { notes, filter, query ->
        val filteredNotes = notes
            .filter { note ->
                val matchesQuery = note.title.contains(query, ignoreCase = true) ||
                    note.description.contains(query, ignoreCase = true)
                val matchesFilter = when (filter) {
                    NoteFilter.ALL -> true
                    NoteFilter.PENDING -> !note.isDone
                    NoteFilter.DONE -> note.isDone
                }
                matchesQuery && matchesFilter
            }
            .sortedByDescending { it.createdAt }

        NoteUiState(
            notes = filteredNotes,
            selectedFilter = filter,
            searchQuery = query,
            pendingCount = notes.count { !it.isDone }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteUiState()
    )

    fun setFilter(filter: NoteFilter) {
        selectedFilterFlow.value = filter
    }

    fun setSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun saveNote(note: Note) = viewModelScope.launch {
        if (note.id == 0) {
            repository.insertNote(note)
        } else {
            repository.updateNote(note)
        }
    }

    fun toggleStatus(note: Note) = viewModelScope.launch {
        val updatedNote = note.copy(
            isDone = !note.isDone,
            doneAt = if (!note.isDone) System.currentTimeMillis() else null
        )
        repository.updateNote(updatedNote)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
    }
}

class NoteViewModelFactory(
    private val repository: NoteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
