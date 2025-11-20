package com.example.maidy.feature.maid_details

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maidy.feature.maid_details.components.*
import com.example.maidy.ui.theme.MaidProfileBackground
import com.example.maidy.ui.theme.MaidProfileContentBackground
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun MaidProfileScreen(
    viewModel: MaidProfileViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MaidProfileContent(
        uiState = uiState,
        onTabSelected = { tab -> viewModel.onEvent(MaidProfileUiEvent.OnTabSelected(tab)) },
        onBookNowClick = { viewModel.onEvent(MaidProfileUiEvent.OnBookNowClick) },
        modifier = modifier
    )
}

@Composable
fun MaidProfileContent(
    uiState: MaidProfileUiState,
    onTabSelected: (ProfileTab) -> Unit,
    onBookNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaidProfileBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                uiState.maidProfile?.let { profile ->
                    // Profile Header
                    ProfileHeader(
                        name = profile.name,
                        isVerified = profile.isVerified,
                        rating = profile.rating,
                        reviewCount = profile.reviewCount,
                        profileImageUrl = profile.profileImageUrl,
                        modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
                    )

                    // Tab Row
                    ProfileTabRow(
                        selectedTab = uiState.selectedTab,
                        onTabSelected = onTabSelected
                    )

                    // Tab Content with Animation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaidProfileContentBackground)
                    ) {
                        AnimatedContent(
                            targetState = uiState.selectedTab,
                            transitionSpec = {
                                val direction = if (targetState.ordinal > initialState.ordinal) {
                                    // Sliding to the right (next tab)
                                    slideInHorizontally(
                                        initialOffsetX = { fullWidth -> fullWidth },
                                        animationSpec = tween(300)
                                    ) + fadeIn(animationSpec = tween(300)) togetherWith
                                            slideOutHorizontally(
                                                targetOffsetX = { fullWidth -> -fullWidth },
                                                animationSpec = tween(300)
                                            ) + fadeOut(animationSpec = tween(300))
                                } else {
                                    // Sliding to the left (previous tab)
                                    slideInHorizontally(
                                        initialOffsetX = { fullWidth -> -fullWidth },
                                        animationSpec = tween(300)
                                    ) + fadeIn(animationSpec = tween(300)) togetherWith
                                            slideOutHorizontally(
                                                targetOffsetX = { fullWidth -> fullWidth },
                                                animationSpec = tween(300)
                                            ) + fadeOut(animationSpec = tween(300))
                                }
                                direction
                            },
                            label = "tab_content_animation"
                        ) { selectedTab ->
                            when (selectedTab) {
                                ProfileTab.ABOUT -> {
                                    AboutSection(aboutText = profile.about)
                                }
                                ProfileTab.SERVICES -> {
                                    ServicesOfferedSection(services = profile.services)
                                }
                                ProfileTab.REVIEWS -> {
                                    CustomerReviewsSection(reviews = profile.reviews)
                                }
                            }
                        }
                    }

                    // Add bottom padding to account for the fixed bottom bar
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }

            // Fixed Bottom Booking Bar
            uiState.maidProfile?.let { profile ->
                BottomBookingBar(
                    pricePerHour = profile.pricePerHour,
                    onBookNowClick = onBookNowClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MaidProfileScreenPreview() {
    MaidyTheme {
        val previewState = MaidProfileUiState(
            isLoading = false,
            maidProfile = MaidProfile(
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
            ),
            selectedTab = ProfileTab.ABOUT
        )

        MaidProfileContent(
            uiState = previewState,
            onTabSelected = {},
            onBookNowClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Services Tab")
@Composable
fun MaidProfileScreenServicesPreview() {
    MaidyTheme {
        val previewState = MaidProfileUiState(
            isLoading = false,
            maidProfile = MaidProfile(
                id = "1",
                name = "Elena Rodriguez",
                isVerified = true,
                rating = 4.9f,
                reviewCount = 125,
                about = "Hello! I'm Elena, and I've been a professional cleaner for over 5 years.",
                services = listOf(
                    ServiceOffered("1", "Kitchen Cleaning", ServiceIconType.KITCHEN_CLEANING),
                    ServiceOffered("2", "Bathroom Cleaning", ServiceIconType.BATHROOM_CLEANING),
                    ServiceOffered("3", "Laundry", ServiceIconType.LAUNDRY),
                    ServiceOffered("4", "Dusting", ServiceIconType.DUSTING),
                    ServiceOffered("5", "Vacuuming", ServiceIconType.VACUUMING),
                    ServiceOffered("6", "Window Washing", ServiceIconType.WINDOW_WASHING)
                ),
                reviews = listOf(),
                pricePerHour = 25
            ),
            selectedTab = ProfileTab.SERVICES
        )

        MaidProfileContent(
            uiState = previewState,
            onTabSelected = {},
            onBookNowClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Reviews Tab")
@Composable
fun MaidProfileScreenReviewsPreview() {
    MaidyTheme {
        val previewState = MaidProfileUiState(
            isLoading = false,
            maidProfile = MaidProfile(
                id = "1",
                name = "Elena Rodriguez",
                isVerified = true,
                rating = 4.9f,
                reviewCount = 125,
                about = "Hello! I'm Elena, and I've been a professional cleaner for over 5 years.",
                services = listOf(),
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
            ),
            selectedTab = ProfileTab.REVIEWS
        )

        MaidProfileContent(
            uiState = previewState,
            onTabSelected = {},
            onBookNowClick = {}
        )
    }
}


