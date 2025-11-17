package com.example.maidy.feature.maid_details

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Placeholder data models
data class MaidProfile(
    val id: String,
    val name: String,
    val isVerified: Boolean,
    val rating: Float,
    val reviewCount: Int,
    val about: String,
    val services: List<ServiceOffered>,
    val reviews: List<CustomerReview>,
    val pricePerHour: Int,
    val profileImageUrl: String = "" // Placeholder
)

data class ServiceOffered(
    val id: String,
    val name: String,
    val iconType: ServiceIconType
)

enum class ServiceIconType {
    KITCHEN_CLEANING,
    BATHROOM_CLEANING,
    LAUNDRY,
    DUSTING,
    VACUUMING,
    WINDOW_WASHING
}

data class CustomerReview(
    val id: String,
    val reviewerName: String,
    val reviewerImageUrl: String = "", // Placeholder
    val date: String,
    val rating: Float,
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

class MaidProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MaidProfileUiState())
    val uiState: StateFlow<MaidProfileUiState> = _uiState.asStateFlow()

    init {
        loadPlaceholderData()
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

    private fun loadPlaceholderData() {
        // Placeholder data - will be replaced with API calls
        val placeholderProfile = MaidProfile(
            id = "1",
            name = "Elena Rodriguez",
            isVerified = true,
            rating = 4.9f,
            reviewCount = 125,
            about = "Hello! I'm Elena, and I've been a professional cleaner for over 5 years. I take great pride in making homes sparkle and creating a fresh, relaxing environment for my clients. I'm reliable, thorough, and always bring a positive attitude.",
            services = listOf(
                ServiceOffered("1", "Kitchen Cleaning", ServiceIconType.KITCHEN_CLEANING),
                ServiceOffered("2", "Bathroom Cleaning", ServiceIconType.BATHROOM_CLEANING),
                ServiceOffered("3", "Laundry", ServiceIconType.LAUNDRY),
                ServiceOffered("4", "Dusting", ServiceIconType.DUSTING),
                ServiceOffered("5", "Vacuuming", ServiceIconType.VACUUMING),
                ServiceOffered("6", "Window Washing", ServiceIconType.WINDOW_WASHING)
            ),
            reviews = listOf(
                CustomerReview(
                    id = "1",
                    reviewerName = "Mark Johnson",
                    date = "June 15, 2024",
                    rating = 5f,
                    comment = "Elena was fantastic! Our house has never looked this clean. She was professional, punctual, and incredibly thorough. Highly recommend!"
                ),
                CustomerReview(
                    id = "2",
                    reviewerName = "Sarah Lee",
                    date = "June 12, 2024",
                    rating = 5f,
                    comment = "Absolutely amazing service. Elena paid attention to every little detail. I'm so happy with the result and will definitely be booking her again."
                )
            ),
            pricePerHour = 25
        )

        _uiState.update {
            it.copy(
                isLoading = false,
                maidProfile = placeholderProfile
            )
        }
    }
}

