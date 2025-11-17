package com.example.maidy.feature.maidlist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Placeholder data models
data class MaidProfile(
    val id: String,
    val name: String,
    val rating: Float,
    val reviewCount: Int,
    val services: List<ServiceTag>,
    val profileImageUrl: String = "" // Placeholder for future API
)

enum class ServiceTag(val displayName: String) {
    DEEP_CLEANING("Deep Cleaning"),
    ECO_FRIENDLY("Eco-Friendly"),
    PET_FRIENDLY("Pet-Friendly"),
    MOVE_IN_OUT("Move In/Out"),
    IRONING("Ironing"),
    LAUNDRY("Laundry"),
    WINDOW_CLEANING("Window Cleaning")
}

data class FilterOption(
    val id: String,
    val label: String,
    val isSelected: Boolean = false
)

data class MaidListUiState(
    val maids: List<MaidProfile> = emptyList(),
    val filters: List<FilterOption> = emptyList(),
    val selectedLocation: String = "Select Location",
    val selectedDateTime: String = "Select Date & Time",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class MaidListUiEvent {
    object OnBackClick : MaidListUiEvent()
    data class OnFilterChipClick(val filterId: String) : MaidListUiEvent()
    object OnLocationClick : MaidListUiEvent()
    object OnDateTimeClick : MaidListUiEvent()
    data class OnSelectMaidClick(val maidId: String) : MaidListUiEvent()
    data class OnViewDetailsClick(val maidId: String) : MaidListUiEvent()
}

class MaidListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MaidListUiState())
    val uiState: StateFlow<MaidListUiState> = _uiState.asStateFlow()

    init {
        loadPlaceholderData()
    }

    fun onEvent(event: MaidListUiEvent) {
        when (event) {
            is MaidListUiEvent.OnBackClick -> {
                // TODO: Navigate back
            }
            is MaidListUiEvent.OnFilterChipClick -> {
                toggleFilter(event.filterId)
            }
            is MaidListUiEvent.OnLocationClick -> {
                // TODO: Show location picker
            }
            is MaidListUiEvent.OnDateTimeClick -> {
                // TODO: Show date time picker
            }
            is MaidListUiEvent.OnSelectMaidClick -> {
                // TODO: Navigate to booking confirmation
            }
            is MaidListUiEvent.OnViewDetailsClick -> {
                // TODO: Navigate to maid details screen
            }
        }
    }

    private fun toggleFilter(filterId: String) {
        _uiState.update { currentState ->
            val updatedFilters = currentState.filters.map { filter ->
                if (filter.id == filterId) {
                    filter.copy(isSelected = !filter.isSelected)
                } else {
                    filter
                }
            }
            currentState.copy(filters = updatedFilters)
        }
        // In a real app, you would also filter the maids list here
    }

    private fun loadPlaceholderData() {
        // Placeholder data - will be replaced with API calls
        _uiState.update {
            it.copy(
                // Filters will come from backend API
                filters = emptyList(),
                maids = listOf(
                    MaidProfile(
                        id = "1",
                        name = "Maria S.",
                        rating = 4.9f,
                        reviewCount = 120,
                        services = listOf(ServiceTag.DEEP_CLEANING)
                    ),
                    MaidProfile(
                        id = "2",
                        name = "Isabella R.",
                        rating = 4.8f,
                        reviewCount = 95,
                        services = listOf(ServiceTag.ECO_FRIENDLY)
                    ),
                    MaidProfile(
                        id = "3",
                        name = "Chloe T.",
                        rating = 4.7f,
                        reviewCount = 88,
                        services = listOf(ServiceTag.PET_FRIENDLY)
                    ),
                    MaidProfile(
                        id = "4",
                        name = "David L.",
                        rating = 4.9f,
                        reviewCount = 150,
                        services = listOf(ServiceTag.MOVE_IN_OUT)
                    ),
                    MaidProfile(
                        id = "5",
                        name = "Emma W.",
                        rating = 4.6f,
                        reviewCount = 72,
                        services = listOf(ServiceTag.DEEP_CLEANING, ServiceTag.ECO_FRIENDLY)
                    ),
                    MaidProfile(
                        id = "6",
                        name = "Sophie M.",
                        rating = 4.8f,
                        reviewCount = 103,
                        services = listOf(ServiceTag.PET_FRIENDLY, ServiceTag.ECO_FRIENDLY, ServiceTag.LAUNDRY)
                    ),
                )
            )
        }
    }
}

