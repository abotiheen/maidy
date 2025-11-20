package com.example.maidy.feature.maid_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.MaidRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Maid profile data for UI
data class MaidProfile(
    val id: String,
    val name: String,
    val isVerified: Boolean,
    val rating: Double,
    val reviewCount: Int,
    val about: String,
    val services: List<String>,
    val reviews: List<CustomerReview>,
    val pricePerHour: Double,
    val profileImageUrl: String = "",
    val specialtyTag: String = "",
    val isAvailable: Boolean = true
)

data class CustomerReview(
    val id: String,
    val reviewerName: String,
    val reviewerImageUrl: String = "",
    val date: String,
    val rating: Int,
    val comment: String
)

enum class ProfileTab {
    ABOUT,
    SERVICES,
    REVIEWS
}

data class MaidProfileUiState(
    val isLoading: Boolean = true,
    val maidProfile: MaidProfile? = null,
    val selectedTab: ProfileTab = ProfileTab.ABOUT,
    val error: String? = null
)

sealed class MaidProfileUiEvent {
    data class OnTabSelected(val tab: ProfileTab) : MaidProfileUiEvent()
    object OnBookNowClick : MaidProfileUiEvent()
    object OnBackClick : MaidProfileUiEvent()
    object OnBookmarkClick : MaidProfileUiEvent()
}

class MaidProfileViewModel(
    private val maidRepository: MaidRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MaidProfileUiState())
    val uiState: StateFlow<MaidProfileUiState> = _uiState.asStateFlow()

    fun loadMaid(maidId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Fetch maid details
                val maidResult = maidRepository.getMaidById(maidId)
                if (maidResult.isFailure) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to load maid: ${maidResult.exceptionOrNull()?.message}"
                        )
                    }
                    return@launch
                }
                
                val maid = maidResult.getOrNull()!!
                
                // Fetch reviews
                val reviewsResult = maidRepository.getMaidReviews(maidId)
                val reviews = reviewsResult.getOrNull() ?: emptyList()
                
                // Map to UI model
                val maidProfile = MaidProfile(
                    id = maid.id,
                    name = maid.fullName,
                    isVerified = maid.verified,
                    rating = maid.averageRating,
                    reviewCount = maid.reviewCount,
                    about = maid.bio,
                    services = maid.services,
                    reviews = reviews.map { review ->
                        CustomerReview(
                            id = review.id,
                            reviewerName = review.reviewerName,
                            date = review.date,
                            rating = review.rating,
                            comment = review.comment
                        )
                    },
                    pricePerHour = maid.hourlyRate,
                    profileImageUrl = maid.profileImageUrl,
                    specialtyTag = maid.specialtyTag,
                    isAvailable = maid.available
                )
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        maidProfile = maidProfile
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun onEvent(event: MaidProfileUiEvent) {
        when (event) {
            is MaidProfileUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tab) }
            }
            is MaidProfileUiEvent.OnBookNowClick -> {
                // TODO: Navigate to booking screen
            }
            is MaidProfileUiEvent.OnBackClick -> {
                // TODO: Navigate back
            }
            is MaidProfileUiEvent.OnBookmarkClick -> {
                // TODO: Toggle bookmark
            }
        }
    }
}



