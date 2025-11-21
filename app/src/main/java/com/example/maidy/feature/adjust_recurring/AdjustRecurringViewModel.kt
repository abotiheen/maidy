package com.example.maidy.feature.adjust_recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.model.BookingType
import com.example.maidy.core.model.RecurringType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdjustRecurringUiState(
    val isLoading: Boolean = true,
    val bookingId: String = "",

    // Maid Info
    val maidId: String = "",
    val maidName: String = "",
    val maidRating: Float = 0f,
    val maidReviewsCount: Int = 0,
    val maidProfileImageUrl: String = "",

    // Recurring Settings
    val recurringType: RecurringType = RecurringType.WEEKLY,
    val selectedDay: String = "Monday",
    val selectedTime: String = "10:00 AM",
    val selectedServiceType: BookingType = BookingType.STANDARD_CLEANING,

    // Booking Status
    val hasCompletedBefore: Boolean = false, // True if lastCompletedDate is not null

    // UI State
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val changesSaved: Boolean = false
)

sealed class AdjustRecurringEvent {
    data class RecurringTypeSelected(val type: RecurringType) : AdjustRecurringEvent()
    data class DaySelected(val day: String) : AdjustRecurringEvent()
    data class TimeSelected(val time: String) : AdjustRecurringEvent()
    data class ServiceTypeSelected(val serviceType: BookingType) : AdjustRecurringEvent()
    object SaveChanges : AdjustRecurringEvent()
    object DismissError : AdjustRecurringEvent()
}

class AdjustRecurringViewModel(
    private val bookingId: String,
    private val bookingRepository: BookingRepository,
    private val maidRepository: MaidRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdjustRecurringUiState(bookingId = bookingId))
    val uiState: StateFlow<AdjustRecurringUiState> = _uiState.asStateFlow()

    init {
        loadBookingDetails()
    }

    private fun loadBookingDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                println("🔄 AdjustRecurringViewModel: Loading booking details - ID: $bookingId")

                // Load booking data
                val bookingResult = bookingRepository.getBookingById(bookingId)
                if (bookingResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load booking: ${bookingResult.exceptionOrNull()?.message}"
                        )
                    }
                    return@launch
                }

                val booking = bookingResult.getOrNull()!!
                println("✅ AdjustRecurringViewModel: Booking loaded successfully")

                // Load maid details
                val maidResult = maidRepository.getMaidById(booking.maidId)
                val maid = maidResult.getOrNull()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        maidId = booking.maidId,
                        maidName = booking.maidFullName,
                        maidRating = maid?.averageRating?.toFloat() ?: 0f,
                        maidReviewsCount = maid?.reviewCount ?: 0,
                        maidProfileImageUrl = booking.maidProfileImageUrl,
                        recurringType = booking.recurringType ?: RecurringType.WEEKLY,
                        selectedDay = booking.preferredDay,
                        selectedTime = booking.preferredHour,
                        selectedServiceType = booking.bookingType,
                        hasCompletedBefore = booking.lastCompletedDate != null
                    )
                }

                println("✅ AdjustRecurringViewModel: UI state updated with booking data")
            } catch (e: Exception) {
                println("❌ AdjustRecurringViewModel: Failed to load booking - ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load booking: ${e.message}"
                    )
                }
            }
        }
    }

    fun onEvent(event: AdjustRecurringEvent) {
        when (event) {
            is AdjustRecurringEvent.RecurringTypeSelected -> {
                _uiState.update { it.copy(recurringType = event.type) }
            }

            is AdjustRecurringEvent.DaySelected -> {
                _uiState.update { it.copy(selectedDay = event.day) }
            }

            is AdjustRecurringEvent.TimeSelected -> {
                _uiState.update { it.copy(selectedTime = event.time) }
            }

            is AdjustRecurringEvent.ServiceTypeSelected -> {
                _uiState.update { it.copy(selectedServiceType = event.serviceType) }
            }

            is AdjustRecurringEvent.SaveChanges -> {
                saveChanges()
            }

            is AdjustRecurringEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                println("💾 AdjustRecurringViewModel: Saving changes for booking - ID: $bookingId")

                val currentState = _uiState.value

                val result = bookingRepository.updateRecurringBooking(
                    bookingId = bookingId,
                    recurringType = currentState.recurringType,
                    preferredDay = currentState.selectedDay,
                    preferredHour = currentState.selectedTime,
                    bookingType = currentState.selectedServiceType
                )

                if (result.isSuccess) {
                    println("✅ AdjustRecurringViewModel: Changes saved successfully")
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            changesSaved = true
                        )
                    }
                } else {
                    println("❌ AdjustRecurringViewModel: Failed to save changes")
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Failed to save changes: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                println("❌ AdjustRecurringViewModel: Exception while saving - ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Failed to save changes: ${e.message}"
                    )
                }
            }
        }
    }
}
