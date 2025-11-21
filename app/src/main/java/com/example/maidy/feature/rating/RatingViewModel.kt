package com.example.maidy.feature.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import com.example.maidy.core.model.MaidReview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class RatingUiState(
    val isLoading: Boolean = true,

    // Maid Info
    val maidId: String = "",
    val maidName: String = "",
    val maidProfileImageUrl: String = "",

    // User Info (for review)
    val userName: String = "",

    // Rating
    val selectedRating: Int = 0,
    val reviewText: String = "",

    // UI State
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val reviewSubmitted: Boolean = false
)

sealed class RatingEvent {
    data class RatingSelected(val rating: Int) : RatingEvent()
    data class ReviewTextChanged(val text: String) : RatingEvent()
    object SubmitReview : RatingEvent()
    object DismissError : RatingEvent()
}

class RatingViewModel(
    private val bookingId: String,
    private val bookingRepository: BookingRepository,
    private val maidRepository: MaidRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState.asStateFlow()

    init {
        loadBookingAndMaidDetails()
    }

    private fun loadBookingAndMaidDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                println("🔄 RatingViewModel: Loading booking details - ID: $bookingId")

                // 1. Load booking to get maidId
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
                val maidId = booking.maidId
                println("✅ RatingViewModel: Booking loaded - Maid ID: $maidId")

                // 2. Load maid details
                val maidResult = maidRepository.getMaidById(maidId)
                if (maidResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load maid details: ${maidResult.exceptionOrNull()?.message}"
                        )
                    }
                    return@launch
                }

                val maid = maidResult.getOrNull()!!
                println("✅ RatingViewModel: Maid loaded - ${maid.fullName}")

                // 3. Load current user details
                val userId = sessionManager.getCurrentUserId()
                var userName = "Anonymous"

                if (userId != null) {
                    val userResult = userRepository.getUserById(userId)
                    val user = userResult.getOrNull()
                    userName = user?.fullName ?: "Anonymous"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        maidId = maidId,
                        maidName = maid.fullName,
                        maidProfileImageUrl = maid.profileImageUrl,
                        userName = userName
                    )
                }

                println("✅ RatingViewModel: All details loaded successfully")
            } catch (e: Exception) {
                println("❌ RatingViewModel: Failed to load details - ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load details: ${e.message}"
                    )
                }
            }
        }
    }

    fun onEvent(event: RatingEvent) {
        when (event) {
            is RatingEvent.RatingSelected -> {
                _uiState.update { it.copy(selectedRating = event.rating) }
                println("⭐ RatingViewModel: Rating selected - ${event.rating} stars")
            }

            is RatingEvent.ReviewTextChanged -> {
                _uiState.update { it.copy(reviewText = event.text) }
            }

            is RatingEvent.SubmitReview -> {
                submitReview()
            }

            is RatingEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun submitReview() {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState.selectedRating == 0) {
                _uiState.update { it.copy(errorMessage = "Please select a rating") }
                return@launch
            }

            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            try {
                println("💾 RatingViewModel: Submitting review")
                println("💾 Maid ID: ${currentState.maidId}")
                println("💾 Rating: ${currentState.selectedRating}")
                println("💾 Review: ${currentState.reviewText}")

                // Create review object
                val timestamp = System.currentTimeMillis()
                val dateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
                val formattedDate = dateFormatter.format(Date(timestamp))

                val review = MaidReview(
                    id = UUID.randomUUID().toString(),
                    reviewerName = currentState.userName,
                    rating = currentState.selectedRating,
                    comment = currentState.reviewText,
                    date = formattedDate,
                    timestamp = timestamp
                )

                println("💾 RatingViewModel: Review object created - ID: ${review.id}")

                // Submit review to Firestore
                val result = maidRepository.addReview(currentState.maidId, review)

                if (result.isSuccess) {
                    println("✅ RatingViewModel: Review submitted successfully")

                    // Update maid's average rating and review count
                    println("💾 RatingViewModel: Updating maid's average rating and review count")
                    val updateResult =
                        updateMaidRating(currentState.maidId, currentState.selectedRating)

                    if (updateResult.isFailure) {
                        println("⚠️ RatingViewModel: Failed to update maid rating: ${updateResult.exceptionOrNull()?.message}")
                        // Don't fail the whole operation, review was already saved
                    } else {
                        println("✅ RatingViewModel: Maid rating updated successfully")
                    }

                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            reviewSubmitted = true
                        )
                    }
                } else {
                    println("❌ RatingViewModel: Failed to submit review")
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = "Failed to submit review: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                println("❌ RatingViewModel: Exception while submitting - ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = "Failed to submit review: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Update maid's average rating and review count
     * Calculates new average based on existing reviews
     */
    private suspend fun updateMaidRating(maidId: String, newRating: Int): Result<Unit> {
        return try {
            println("🔄 RatingViewModel: Fetching current maid data...")

            // Get current maid data
            val maidResult = maidRepository.getMaidById(maidId)
            if (maidResult.isFailure) {
                return Result.failure(maidResult.exceptionOrNull()!!)
            }

            val currentMaid = maidResult.getOrNull()!!
            val currentAverageRating = currentMaid.averageRating
            val currentReviewCount = currentMaid.reviewCount

            println("📊 RatingViewModel: Current average: $currentAverageRating, Count: $currentReviewCount")

            // Calculate new average rating
            // Formula: ((currentAverage * currentCount) + newRating) / (currentCount + 1)
            val totalRating = (currentAverageRating * currentReviewCount) + newRating
            val newReviewCount = currentReviewCount + 1
            val newAverageRating = totalRating / newReviewCount

            println("📊 RatingViewModel: New average: $newAverageRating, Count: $newReviewCount")

            // Update Firestore
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("maids")
                .document(maidId)
                .update(
                    mapOf(
                        "averageRating" to newAverageRating,
                        "reviewCount" to newReviewCount
                    )
                )
                .await()

            println("✅ RatingViewModel: Maid rating updated in Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ RatingViewModel: Failed to update maid rating - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
