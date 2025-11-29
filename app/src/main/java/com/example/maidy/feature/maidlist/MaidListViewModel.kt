package com.example.maidy.feature.maidlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.model.Maid
import com.example.maidy.core.model.SpecialtyTags
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FilterOption(
    val id: String,
    val label: String,
    val isSelected: Boolean = false
)

data class MaidListUiState(
    val allMaids: List<Maid> = emptyList(), // All maids from Firebase
    val filteredMaids: List<Maid> = emptyList(), // Filtered maids based on selected filters
    val filters: List<FilterOption> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class MaidListUiEvent {
    object OnBackClick : MaidListUiEvent()
    data class OnFilterChipClick(val filterId: String) : MaidListUiEvent()
    data class OnSelectMaidClick(val maidId: String) : MaidListUiEvent()
    data class OnViewDetailsClick(val maidId: String) : MaidListUiEvent()
    data class OnDeleteMaidClick(val maidId: String) : MaidListUiEvent()
}

class MaidListViewModel(
    private val maidRepository: MaidRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MaidListUiState())
    val uiState: StateFlow<MaidListUiState> = _uiState.asStateFlow()

    init {
        loadFilters()
        loadMaids()
    }

    fun onEvent(event: MaidListUiEvent) {
        when (event) {
            is MaidListUiEvent.OnBackClick -> {
                // TODO: Navigate back
            }
            is MaidListUiEvent.OnFilterChipClick -> {
                toggleFilter(event.filterId)
            }
            is MaidListUiEvent.OnSelectMaidClick -> {
                // TODO: Navigate to booking confirmation
            }
            is MaidListUiEvent.OnViewDetailsClick -> {
                // TODO: Navigate to maid details screen
            }
            is MaidListUiEvent.OnDeleteMaidClick -> {
                deleteMaid(event.maidId)
            }
        }
    }

    private fun loadFilters() {
        // Initialize filters from SpecialtyTags
        val filters = SpecialtyTags.tags.map { tag ->
            FilterOption(
                id = tag,
                label = tag,
                isSelected = false
            )
        }
        _uiState.update { it.copy(filters = filters) }
    }

    private fun loadMaids() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            maidRepository.getAllMaids()
                .onSuccess { maids ->
                    // Show all maids regardless of availability
                    _uiState.update {
                        it.copy(
                            allMaids = maids,
                            filteredMaids = maids,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load maids: ${error.message}"
                        )
                    }
                }
        }
    }

    private fun toggleFilter(filterId: String) {
        _uiState.update { currentState ->
            // Update filter selection
            val updatedFilters = currentState.filters.map { filter ->
                if (filter.id == filterId) {
                    filter.copy(isSelected = !filter.isSelected)
                } else {
                    filter
                }
            }
            
            // Get selected filter tags
            val selectedTags = updatedFilters.filter { it.isSelected }.map { it.id }
            
            // Filter maids by selected specialty tags
            val filteredMaids = if (selectedTags.isEmpty()) {
                // No filters selected, show all maids
                currentState.allMaids
            } else {
                // Show maids that match any of the selected specialty tags
                currentState.allMaids.filter { maid ->
                    selectedTags.contains(maid.specialtyTag)
                }
            }
            
            currentState.copy(
                filters = updatedFilters,
                filteredMaids = filteredMaids
            )
        }
    }

    private fun deleteMaid(maidId: String) {
        viewModelScope.launch {
            maidRepository.deleteMaid(maidId)
                .onSuccess {
                    loadMaids()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = "Failed to delete maid: ${error.message}"
                        )
                    }
                }
        }
    }
}
