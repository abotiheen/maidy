package com.example.maidy.feature.maid_details

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.maid_details.components.*
import com.example.maidy.ui.theme.MaidProfileBackground
import com.example.maidy.ui.theme.MaidProfileContentBackground
import com.example.maidy.ui.theme.MaidyErrorRed
import com.example.maidy.ui.theme.MaidyTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MaidProfileScreen(
    maidId: String,
    viewModel: MaidProfileViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(maidId) {
        viewModel.loadMaid(maidId)
    }

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
        when {
            uiState.isLoading -> {
                // Loading State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                // Error State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error,
                        fontSize = 16.sp,
                        color = MaidyErrorRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            uiState.maidProfile != null -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Scrollable content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        val profile = uiState.maidProfile
                        
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

                    // Fixed Bottom Booking Bar

                    BottomBookingBar(
                        pricePerHour = uiState.maidProfile.pricePerHour,
                        isAvailable = uiState.maidProfile.isAvailable,
                        onBookNowClick = onBookNowClick
                    )
                }
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
                rating = 4.9,
                reviewCount = 125,
                about = "Hello! I'm Elena, and I've been a professional cleaner for over 5 years. I take great pride in making homes sparkle and creating a fresh, relaxing environment for my clients. I'm reliable, thorough, and always bring a positive attitude.",
                services = listOf(
                    "Kitchen Cleaning",
                    "Bathroom Cleaning",
                    "Laundry",
                    "Dusting",
                    "Vacuuming",
                    "Window Washing"
                ),
                reviews = listOf(
                    CustomerReview(
                        id = "1",
                        reviewerName = "Mark Johnson",
                        date = "June 15, 2024",
                        rating = 5,
                        comment = "Elena was fantastic! Our house has never looked this clean. She was professional, punctual, and incredibly thorough. Highly recommend!"
                    ),
                    CustomerReview(
                        id = "2",
                        reviewerName = "Sarah Lee",
                        date = "June 12, 2024",
                        rating = 5,
                        comment = "Absolutely amazing service. Elena paid attention to every little detail. I'm so happy with the result and will definitely be booking her again."
                    )
                ),
                pricePerHour = 25.0,
                specialtyTag = "Deep Cleaning",
                isAvailable = true
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
                rating = 4.9,
                reviewCount = 125,
                about = "Hello! I'm Elena, and I've been a professional cleaner for over 5 years.",
                services = listOf(
                    "Kitchen Cleaning",
                    "Bathroom Cleaning",
                    "Laundry",
                    "Dusting",
                    "Vacuuming",
                    "Window Washing"
                ),
                reviews = listOf(),
                pricePerHour = 25.0,
                specialtyTag = "Deep Cleaning",
                isAvailable = true
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
                rating = 4.9,
                reviewCount = 125,
                about = "Hello! I'm Elena, and I've been a professional cleaner for over 5 years.",
                services = listOf(),
                reviews = listOf(
                    CustomerReview(
                        id = "1",
                        reviewerName = "Mark Johnson",
                        date = "June 15, 2024",
                        rating = 5,
                        comment = "Elena was fantastic! Our house has never looked this clean. She was professional, punctual, and incredibly thorough. Highly recommend!"
                    ),
                    CustomerReview(
                        id = "2",
                        reviewerName = "Sarah Lee",
                        date = "June 12, 2024",
                        rating = 5,
                        comment = "Absolutely amazing service. Elena paid attention to every little detail. I'm so happy with the result and will definitely be booking her again."
                    )
                ),
                pricePerHour = 25.0
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



