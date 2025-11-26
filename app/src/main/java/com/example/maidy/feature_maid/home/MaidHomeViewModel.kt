package com.example.maidy.feature_maid.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Maid Home Screen
 * Manages maid profile and recent bookings
 */
class MaidHomeViewModel(
    private val maidRepository: MaidRepository,
    private val bookingRepository: BookingRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaidHomeUiState())
    val uiState: StateFlow<MaidHomeUiState> = _uiState.asStateFlow()

    init {
        loadMaidData()
        loadRecentBookings()
    }

    fun onEvent(event: MaidHomeEvent) {
        when (event) {
            is MaidHomeEvent.LoadMaidData -> loadMaidData()
            is MaidHomeEvent.LoadRecentBookings -> loadRecentBookings()
            is MaidHomeEvent.ToggleAvailability -> toggleAvailability(event.isAvailable)
            is MaidHomeEvent.ClearError -> _uiState.value = _uiState.value.copy(errorMessage = null)
        }
    }

    /**
     * Load maid profile data
     */
    private fun loadMaidData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val maidId = sessionManager.getCurrentUserId()
            if (maidId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Session expired. Please log in again."
                )
                return@launch
            }

            val result = maidRepository.getMaidById(maidId)
            result.onSuccess { maid ->
                _uiState.value = _uiState.value.copy(
                    maid = maid,
                    isAvailable = maid.available,
                    isLoading = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load profile: ${error.message}"
                )
            }
        }
    }

    /**
     * Load recent bookings for the maid
     */
    private fun loadRecentBookings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingBookings = true)

            val maidId = sessionManager.getCurrentUserId()
            if (maidId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoadingBookings = false,
                    errorMessage = "Session expired"
                )
                return@launch
            }

            val result = bookingRepository.getMaidBookings(maidId)
            result.onSuccess { bookings ->
                // Sort by nextScheduledDate, most recent first, limit to 5
                val recentBookings = bookings
                    .filter { it.nextScheduledDate != null }
                    .sortedByDescending { it.nextScheduledDate }
                    .take(5)

                _uiState.value = _uiState.value.copy(
                    recentBookings = recentBookings,
                    isLoadingBookings = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoadingBookings = false,
                    errorMessage = "Failed to load bookings: ${error.message}"
                )
            }
        }
    }

    /**
     * Toggle maid's availability status
     */
    private fun toggleAvailability(isAvailable: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingAvailability = true)

            val maidId = sessionManager.getCurrentUserId()
            if (maidId == null) {
                _uiState.value = _uiState.value.copy(
                    isUpdatingAvailability = false,
                    errorMessage = "Session expired"
                )
                return@launch
            }

            // Update availability in Firestore
            val result = maidRepository.updateMaidAvailability(maidId, isAvailable)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isAvailable = isAvailable,
                    isUpdatingAvailability = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isUpdatingAvailability = false,
                    errorMessage = "Failed to update availability: ${error.message}"
                )
            }
        }
    }
}
