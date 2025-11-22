package com.example.maidy.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import com.example.maidy.core.model.Booking
import com.example.maidy.core.util.BookingDateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingItem(
    val id: String,
    val serviceName: String,
    val maidName: String,
    val dateTime: String,  // Formatted as "Nov 21, 2024 at 10:00 AM"
    val timestamp: Long = 0L,  // Timestamp for filtering/sorting
    val status: BookingStatus,
    val profileImageUrl: String = "",
    val isRecurring: Boolean = false
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    ON_THE_WAY,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
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
    private val bookingRepository: BookingRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadBookings()
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

    private fun loadBookings() {
        viewModelScope.launch {
            println("🟢 HomeViewModel: Loading bookings...")
            _uiState.update { it.copy(isLoading = true) }
            
            val userId = sessionManager.getCurrentUserId()
            
            if (userId == null) {
                println("❌ HomeViewModel: User not logged in, no bookings to load")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        recentBookings = emptyList()
                    )
                }
                return@launch
            }
            
            try {
                val result = bookingRepository.getActiveUserBookings(userId)
                
                if (result.isSuccess) {
                    val bookings = result.getOrNull()!!
                    println("✅ HomeViewModel: Loaded ${bookings.size} bookings")
                    
                    val bookingItems = bookings.map { booking ->
                        mapBookingToItem(booking)
                    }
                    
                    _uiState.update { 
                        it.copy(
                            recentBookings = bookingItems,
                            isLoading = false
                        )
                    }
                } else {
                    val error = result.exceptionOrNull()?.message
                    println("❌ HomeViewModel: Failed to load bookings: $error")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = error
                        )
                    }
                }
            } catch (e: Exception) {
                println("❌ HomeViewModel: Exception loading bookings: ${e.message}")
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
    
    /**
     * Map Firebase Booking model to UI BookingItem
     */
    private fun mapBookingToItem(booking: Booking): BookingItem {
        val time = BookingDateUtils.getBookingTime(booking)
        val formattedDateTime = BookingDateUtils.formatBookingDateTime(
            date = booking.nextScheduledDate,
            time = time
        )

        // Get timestamp for filtering - use nextScheduledDate or bookingDate
        val timestamp = booking.nextScheduledDate?.toDate()?.time
            ?: booking.bookingDate?.toDate()?.time
            ?: 0L

        return BookingItem(
            id = booking.id,
            serviceName = booking.bookingType.displayName(),
            maidName = booking.maidFullName,
            dateTime = formattedDateTime,
            timestamp = timestamp,
            status = mapFirebaseStatusToUiStatus(booking.status),
            profileImageUrl = booking.maidProfileImageUrl,
            isRecurring = booking.isRecurring
        )
    }
    
    /**
     * Map Firebase BookingStatus to UI BookingStatus
     */
    private fun mapFirebaseStatusToUiStatus(
        firebaseStatus: com.example.maidy.core.model.BookingStatus
    ): BookingStatus {
        return when (firebaseStatus) {
            com.example.maidy.core.model.BookingStatus.PENDING -> BookingStatus.PENDING
            com.example.maidy.core.model.BookingStatus.CONFIRMED -> BookingStatus.CONFIRMED
            com.example.maidy.core.model.BookingStatus.ON_THE_WAY -> BookingStatus.ON_THE_WAY
            com.example.maidy.core.model.BookingStatus.IN_PROGRESS -> BookingStatus.IN_PROGRESS
            com.example.maidy.core.model.BookingStatus.COMPLETED -> BookingStatus.COMPLETED
            com.example.maidy.core.model.BookingStatus.CANCELLED -> BookingStatus.CANCELLED
        }
    }
    
    /**
     * Refresh bookings (can be called when user pulls to refresh)
     */
    fun refreshBookings() {
        loadBookings()
    }
}


