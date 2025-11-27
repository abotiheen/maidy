package com.example.maidy.feature.all_bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.model.Booking
import com.example.maidy.core.util.BookingDateUtils
import com.example.maidy.feature.home.BookingItem
import com.example.maidy.feature.home.BookingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingFilterOption(
    val id: String,
    val label: String,
    val isSelected: Boolean = false
)

data class AllBookingsUiState(
    val allBookings: List<BookingItem> = emptyList(),
    val filteredBookings: List<BookingItem> = emptyList(),
    val statusFilters: List<BookingFilterOption> = listOf(
        BookingFilterOption("all", "All", isSelected = true),
        BookingFilterOption("pending", "Pending", isSelected = false),
        BookingFilterOption("confirmed", "Confirmed", isSelected = false),
        BookingFilterOption("on_the_way", "On The Way", isSelected = false),
        BookingFilterOption("in_progress", "In Progress", isSelected = false),
        BookingFilterOption("completed", "Completed", isSelected = false),
        BookingFilterOption("cancelled", "Cancelled", isSelected = false)
    ),
    val recurringFilter: BookingFilterOption = BookingFilterOption(
        "recurring",
        "Recurring Only",
        isSelected = false
    ),
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null,
    val showDateRangePicker: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class AllBookingsUiEvent {
    data class OnStatusFilterClick(val filterId: String) : AllBookingsUiEvent()
    object OnRecurringFilterToggle : AllBookingsUiEvent()
    data class OnBookingClick(val bookingId: String) : AllBookingsUiEvent()
    object OnDateFilterClick : AllBookingsUiEvent()
    data class OnDateRangeSelected(val startMillis: Long, val endMillis: Long) :
        AllBookingsUiEvent()

    object OnClearDateFilter : AllBookingsUiEvent()
    object OnDismissDatePicker : AllBookingsUiEvent()
}

class AllBookingsViewModel(
    private val bookingRepository: BookingRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AllBookingsUiState())
    val uiState: StateFlow<AllBookingsUiState> = _uiState.asStateFlow()

    init {
        loadAllBookings()
    }

    fun onEvent(event: AllBookingsUiEvent) {
        when (event) {
            is AllBookingsUiEvent.OnStatusFilterClick -> {
                toggleStatusFilter(event.filterId)
            }

            AllBookingsUiEvent.OnRecurringFilterToggle -> {
                toggleRecurringFilter()
            }

            is AllBookingsUiEvent.OnBookingClick -> {
                // Navigation handled by screen
            }

            AllBookingsUiEvent.OnDateFilterClick -> {
                _uiState.update { it.copy(showDateRangePicker = true) }
            }

            is AllBookingsUiEvent.OnDateRangeSelected -> {
                _uiState.update {
                    it.copy(
                        startDateMillis = event.startMillis,
                        endDateMillis = event.endMillis,
                        showDateRangePicker = false
                    )
                }
                applyFilters()
            }

            AllBookingsUiEvent.OnClearDateFilter -> {
                _uiState.update {
                    it.copy(
                        startDateMillis = null,
                        endDateMillis = null
                    )
                }
                applyFilters()
            }

            AllBookingsUiEvent.OnDismissDatePicker -> {
                _uiState.update { it.copy(showDateRangePicker = false) }
            }
        }
    }

    private fun loadAllBookings() {
        viewModelScope.launch {
            println("📚 AllBookingsViewModel: Loading all bookings...")
            _uiState.update { it.copy(isLoading = true) }

            val userId = sessionManager.getCurrentUserId()

            if (userId == null) {
                println("❌ AllBookingsViewModel: User not logged in")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "User not logged in"
                    )
                }
                return@launch
            }

            try {
                // Load all user bookings
                val result = bookingRepository.getUserBookings(userId)

                if (!result.isSuccess) {
                    val error = result.exceptionOrNull()?.message ?: "Failed to load bookings"
                    println("❌ AllBookingsViewModel: $error")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error
                        )
                    }
                    return@launch
                }

                val allBookings = result.getOrNull() ?: emptyList()
                println("✅ AllBookingsViewModel: Loaded ${allBookings.size} bookings")

                val bookingItems = allBookings.map { booking ->
                    mapBookingToItem(booking)
                }

                // Sort by date (most recent first)
                val sortedBookings = bookingItems.sortedByDescending { it.id }

                _uiState.update {
                    it.copy(
                        allBookings = sortedBookings,
                        filteredBookings = sortedBookings,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                println("❌ AllBookingsViewModel: Exception - ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An error occurred"
                    )
                }
            }
        }
    }

    private fun toggleStatusFilter(filterId: String) {
        val currentFilters = _uiState.value.statusFilters

        // Update filters
        val updatedFilters = currentFilters.map { filter ->
            if (filter.id == filterId) {
                filter.copy(isSelected = true)
            } else {
                filter.copy(isSelected = false)
            }
        }

        _uiState.update { it.copy(statusFilters = updatedFilters) }

        // Apply filtering
        applyFilters()
    }

    private fun toggleRecurringFilter() {
        val currentFilter = _uiState.value.recurringFilter
        _uiState.update {
            it.copy(recurringFilter = currentFilter.copy(isSelected = !currentFilter.isSelected))
        }

        // Apply filtering
        applyFilters()
    }

    private fun applyFilters() {
        val allBookings = _uiState.value.allBookings
        val selectedStatusFilter = _uiState.value.statusFilters.find { it.isSelected }?.id
        val recurringOnly = _uiState.value.recurringFilter.isSelected
        val startDate = _uiState.value.startDateMillis
        val endDate = _uiState.value.endDateMillis

        var filtered = allBookings

        // Apply status filter
        if (selectedStatusFilter != null && selectedStatusFilter != "all") {
            filtered = filtered.filter { booking ->
                when (selectedStatusFilter) {
                    "pending" -> booking.status == BookingStatus.PENDING
                    "confirmed" -> booking.status == BookingStatus.CONFIRMED
                    "on_the_way" -> booking.status == BookingStatus.ON_THE_WAY
                    "in_progress" -> booking.status == BookingStatus.IN_PROGRESS
                    "completed" -> booking.status == BookingStatus.COMPLETED
                    "cancelled" -> booking.status == BookingStatus.CANCELLED
                    else -> true
                }
            }
        }

        // Apply recurring filter
        if (recurringOnly) {
            filtered = filtered.filter { it.isRecurring }
        }

        // Apply date range filter
        if (startDate != null && endDate != null) {
            filtered = filtered.filter { booking ->
                booking.timestamp in startDate..endDate
            }
        }

        println("📚 AllBookingsViewModel: Filtered to ${filtered.size} bookings")
        _uiState.update { it.copy(filteredBookings = filtered) }
    }

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

    fun refreshBookings() {
        loadAllBookings()
    }
}
