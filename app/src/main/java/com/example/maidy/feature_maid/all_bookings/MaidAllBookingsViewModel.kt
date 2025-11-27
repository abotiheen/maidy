package com.example.maidy.feature_maid.all_bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.model.Booking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaidAllBookingsViewModel(
    private val bookingRepository: BookingRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaidAllBookingsUiState())
    val uiState: StateFlow<MaidAllBookingsUiState> = _uiState.asStateFlow()

    init {
        loadAllBookings()
    }

    fun onEvent(event: MaidAllBookingsEvent) {
        when (event) {
            is MaidAllBookingsEvent.OnStatusFilterClick -> {
                toggleStatusFilter(event.filterId)
            }

            MaidAllBookingsEvent.OnRecurringFilterToggle -> {
                toggleRecurringFilter()
            }

            is MaidAllBookingsEvent.OnBookingClick -> {
                // Navigation handled by screen
            }

            MaidAllBookingsEvent.OnDateFilterClick -> {
                _uiState.update { it.copy(showDateRangePicker = true) }
            }

            is MaidAllBookingsEvent.OnDateRangeSelected -> {
                _uiState.update {
                    it.copy(
                        startDateMillis = event.startMillis,
                        endDateMillis = event.endMillis,
                        showDateRangePicker = false
                    )
                }
                applyFilters()
            }

            MaidAllBookingsEvent.OnClearDateFilter -> {
                _uiState.update {
                    it.copy(
                        startDateMillis = null,
                        endDateMillis = null
                    )
                }
                applyFilters()
            }

            MaidAllBookingsEvent.OnDismissDatePicker -> {
                _uiState.update { it.copy(showDateRangePicker = false) }
            }
        }
    }

    private fun loadAllBookings() {
        viewModelScope.launch {
            println("📚 MaidAllBookingsViewModel: Loading all maid bookings...")
            _uiState.update { it.copy(isLoading = true) }

            val maidId = sessionManager.getCurrentUserId()

            if (maidId == null) {
                println("❌ MaidAllBookingsViewModel: Maid not logged in")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Maid not logged in"
                    )
                }
                return@launch
            }

            try {
                // Load all maid bookings
                val result = bookingRepository.getMaidBookings(maidId)

                if (!result.isSuccess) {
                    val error = result.exceptionOrNull()?.message ?: "Failed to load bookings"
                    println("❌ MaidAllBookingsViewModel: $error")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error
                        )
                    }
                    return@launch
                }

                val allBookings = result.getOrNull() ?: emptyList()
                println("✅ MaidAllBookingsViewModel: Loaded ${allBookings.size} bookings")

                // Sort by date (most recent first)
                val sortedBookings = allBookings
                    .filter { it.nextScheduledDate != null }
                    .sortedByDescending { it.nextScheduledDate }

                _uiState.update {
                    it.copy(
                        allBookings = sortedBookings,
                        filteredBookings = sortedBookings,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                println("❌ MaidAllBookingsViewModel: Exception - ${e.message}")
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
                    "pending" -> booking.status == com.example.maidy.core.model.BookingStatus.PENDING
                    "confirmed" -> booking.status == com.example.maidy.core.model.BookingStatus.CONFIRMED
                    "on_the_way" -> booking.status == com.example.maidy.core.model.BookingStatus.ON_THE_WAY
                    "in_progress" -> booking.status == com.example.maidy.core.model.BookingStatus.IN_PROGRESS
                    "completed" -> booking.status == com.example.maidy.core.model.BookingStatus.COMPLETED
                    "cancelled" -> booking.status == com.example.maidy.core.model.BookingStatus.CANCELLED
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
                val timestamp = booking.nextScheduledDate?.toDate()?.time ?: 0L
                timestamp in startDate..endDate
            }
        }

        println("📚 MaidAllBookingsViewModel: Filtered to ${filtered.size} bookings")
        _uiState.update { it.copy(filteredBookings = filtered) }
    }

    fun refreshBookings() {
        loadAllBookings()
    }
}
