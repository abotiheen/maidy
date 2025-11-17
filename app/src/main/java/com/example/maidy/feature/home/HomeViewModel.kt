package com.example.maidy.feature.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BookingItem(
    val id: String,
    val serviceName: String,
    val maidName: String,
    val date: String,
    val time: String,
    val status: BookingStatus,
    val profileImageUrl: String = "" // Placeholder
)

enum class BookingStatus {
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED
}

data class HomeUiState(
    val userName: String = "Sarah",
    val searchQuery: String = "",
    val recentBookings: List<BookingItem> = emptyList(),
    val hasNotifications: Boolean = false,
    val profileImageUrl: String = "" // Placeholder
)

sealed class HomeUiEvent {
    data class OnSearchQueryChange(val query: String) : HomeUiEvent()
    object OnBookNowClick : HomeUiEvent()
    object OnScheduleClick : HomeUiEvent()
    object OnNotificationClick : HomeUiEvent()
    data class OnBookingClick(val bookingId: String) : HomeUiEvent()
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPlaceholderData()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnSearchQueryChange -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }
            is HomeUiEvent.OnBookNowClick -> {
                // TODO: Navigate to booking screen
            }
            is HomeUiEvent.OnScheduleClick -> {
                // TODO: Navigate to schedule screen
            }
            is HomeUiEvent.OnNotificationClick -> {
                // TODO: Navigate to notifications screen
            }
            is HomeUiEvent.OnBookingClick -> {
                // TODO: Navigate to booking details screen
            }
        }
    }

    private fun loadPlaceholderData() {
        // Placeholder data - will be replaced with API calls
        _uiState.update {
            it.copy(
                userName = "Sarah",
                hasNotifications = true,
                recentBookings = listOf(
                    BookingItem(
                        id = "1",
                        serviceName = "Deep Cleaning",
                        maidName = "Maria G.",
                        date = "Tomorrow",
                        time = "10:00 AM",
                        status = BookingStatus.CONFIRMED
                    ),
                    BookingItem(
                        id = "2",
                        serviceName = "Standard Home Clean",
                        maidName = "Jessica L.",
                        date = "Today",
                        time = "2:00 PM",
                        status = BookingStatus.IN_PROGRESS
                    ),
                    BookingItem(
                        id = "3",
                        serviceName = "Move-out Clean",
                        maidName = "Ana P.",
                        date = "Oct 28",
                        time = "9:00 AM",
                        status = BookingStatus.COMPLETED
                    )
                )
            )
        }
    }
}


