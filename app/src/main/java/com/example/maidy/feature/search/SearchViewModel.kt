package com.example.maidy.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.model.Maid
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<Maid> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null
)

sealed class SearchUiEvent {
    data class OnSearchQueryChange(val query: String) : SearchUiEvent()
    data class OnMaidClick(val maidId: String) : SearchUiEvent()
    object OnClearSearch : SearchUiEvent()
}

class SearchViewModel(
    private val maidRepository: MaidRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private val debounceDelayMs = 500L // Wait 500ms after user stops typing

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.OnSearchQueryChange -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                performDebouncedSearch(event.query)
            }

            is SearchUiEvent.OnMaidClick -> {
                // Navigation will be handled by the screen
            }

            SearchUiEvent.OnClearSearch -> {
                searchJob?.cancel()
                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        searchResults = emptyList(),
                        isSearching = false,
                        error = null
                    )
                }
            }
        }
    }

    /**
     * Debounced search - waits for user to stop typing before searching
     */
    private fun performDebouncedSearch(query: String) {
        // Cancel any existing search job
        searchJob?.cancel()

        // If query is empty, clear results immediately
        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    searchResults = emptyList(),
                    isSearching = false,
                    error = null
                )
            }
            return
        }

        // Create new search job with debounce
        searchJob = viewModelScope.launch {
            // Wait for user to stop typing
            delay(debounceDelayMs)

            // Perform the search
            searchMaids(query)
        }
    }

    /**
     * Perform server-side search
     */
    private suspend fun searchMaids(query: String) {
        println("🔍 SearchViewModel: Searching for: $query")
        _uiState.update { it.copy(isSearching = true, error = null) }

        try {
            val result = maidRepository.searchMaidsByName(query)

            if (result.isSuccess) {
                val maids = result.getOrNull() ?: emptyList()
                println("✅ SearchViewModel: Found ${maids.size} maids")
                _uiState.update {
                    it.copy(
                        searchResults = maids,
                        isSearching = false,
                        error = null
                    )
                }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Search failed"
                println("❌ SearchViewModel: $error")
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        error = error
                    )
                }
            }
        } catch (e: Exception) {
            println("❌ SearchViewModel: Exception - ${e.message}")
            e.printStackTrace()
            _uiState.update {
                it.copy(
                    isSearching = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}
