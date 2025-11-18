package com.example.maidy.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val userName: String = "",
    val searchQuery: String = "",
    val recentBookings: List<BookingItem> = emptyList(),
    val hasNotifications: Boolean = false,
    val profileImageUrl: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class HomeUiEvent {
    data class OnSearchQueryChange(val query: String) : HomeUiEvent()
    object OnBookNowClick : HomeUiEvent()
    object OnScheduleClick : HomeUiEvent()
    object OnNotificationClick : HomeUiEvent()
    data class OnBookingClick(val bookingId: String) : HomeUiEvent()
}

class HomeViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
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
    
    private fun loadUserData() {
        viewModelScope.launch {
            println("🟢 HomeViewModel: Loading user data...")
            _uiState.update { it.copy(isLoading = true) }
            
            val userId = sessionManager.getCurrentUserId()
            println("🟢 HomeViewModel: User ID = $userId")
            
            if (userId == null) {
                println("❌ HomeViewModel: User not logged in, showing Guest")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        userName = "Guest"
                    )
                }
                return@launch
            }
            
            try {
                val result = userRepository.getUserById(userId)
                
                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    println("✅ HomeViewModel: User loaded - Name: ${user.fullName}, Image: ${user.profileImageUrl}")
                    _uiState.update { 
                        it.copy(
                            userName = user.fullName,
                            profileImageUrl = user.profileImageUrl,
                            isLoading = false
                        )
                    }
                } else {
                    val error = result.exceptionOrNull()?.message
                    println("❌ HomeViewModel: Failed to load user: $error")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = error
                        )
                    }
                }
            } catch (e: Exception) {
                println("❌ HomeViewModel: Exception loading user: ${e.message}")
                e.printStackTrace()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private fun loadPlaceholderData() {
        // Placeholder data - will be replaced with API calls
        _uiState.update {
            it.copy(
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


