package com.example.maidy.feature_maid.home

import com.example.maidy.core.model.Booking
import com.example.maidy.core.model.Maid

/**
 * UI State for the Maid Home Screen
 */
data class MaidHomeUiState(
    // Maid profile data
    val maid: Maid? = null,
    val isAvailable: Boolean = false,

    // Bookings data
    val recentBookings: List<Booking> = emptyList(),
    val isLoadingBookings: Boolean = false,

    // UI states
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUpdatingAvailability: Boolean = false
)

/**
 * Events for Maid Home Screen
 */
sealed class MaidHomeEvent {
    data object LoadMaidData : MaidHomeEvent()
    data object LoadRecentBookings : MaidHomeEvent()
    data class ToggleAvailability(val isAvailable: Boolean) : MaidHomeEvent()
    data object ClearError : MaidHomeEvent()
}
