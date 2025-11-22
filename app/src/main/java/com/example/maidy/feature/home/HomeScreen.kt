package com.example.maidy.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.maidy.feature.home.components.*
import com.example.maidy.ui.theme.MaidyBackgroundWhite
import com.example.maidy.ui.theme.MaidyTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToBooking: () -> Unit = {},
    onNavigateToMyBookings: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToBookingDetails: (String) -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToBooking = onNavigateToBooking,
        onNavigateToMyBookings = onNavigateToMyBookings,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToBookingDetails = onNavigateToBookingDetails,
        onNavigateToAdmin = onNavigateToAdmin,
        onNavigateToSearch = onNavigateToSearch
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    onNavigateToBooking: () -> Unit,
    onNavigateToMyBookings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToBookingDetails: (String) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaidyBackgroundWhite)
    ) {
        // Header with Profile, Title, and Notifications
        item {
            HomeHeader(
                profileImageUrl = uiState.profileImageUrl,
                hasNotifications = uiState.hasNotifications,
                onNotificationClick = {
                    onEvent(HomeUiEvent.OnNotificationClick)
                    onNavigateToNotifications()
                },
                onAdminClick = onNavigateToAdmin
            )
        }

        // Greeting
        item {
            GreetingSection(userName = uiState.userName)
        }

        // Search Bar
        item {
            HomeSearchBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { query ->
                    onEvent(HomeUiEvent.OnSearchQueryChange(query))
                },
                onClick = onNavigateToSearch
            )
        }

        // Action Buttons
        item {
            ActionButtons(
                onBookNowClick = {
                    onEvent(HomeUiEvent.OnBookNowClick)
                    onNavigateToBooking()
                },
                onMyBookingsClick = {
                    onNavigateToMyBookings()
                }
            )
        }

        // Recent Bookings Section Header
        item {
            RecentBookingsSectionHeader()
        }

        // Recent Bookings List (dynamic)
        items(
            items = uiState.recentBookings,
            key = { booking -> booking.id }
        ) { booking ->
            BookingCard(
                booking = booking,
                onClick = {
                    onEvent(HomeUiEvent.OnBookingClick(booking.id))
                    onNavigateToBookingDetails(booking.id)
                },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }

        // Add spacing at the bottom for better scroll experience
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaidyTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                userName = "Sarah",
                searchQuery = "",
                hasNotifications = true,
                recentBookings = listOf(
                    BookingItem(
                        id = "1",
                        serviceName = "Deep Cleaning",
                        maidName = "Maria G.",
                        dateTime = "Nov 25, 2024 at 10:00 AM",
                        status = BookingStatus.CONFIRMED,
                        profileImageUrl = ""
                    ),
                    BookingItem(
                        id = "2",
                        serviceName = "Standard Cleaning",
                        maidName = "Jessica L.",
                        dateTime = "Nov 21, 2024 at 2:00 PM",
                        status = BookingStatus.IN_PROGRESS,
                        profileImageUrl = "",
                        isRecurring = true
                    ),
                    BookingItem(
                        id = "3",
                        serviceName = "Move-out Clean",
                        maidName = "Ana P.",
                        dateTime = "Nov 28, 2024 at 9:00 AM",
                        status = BookingStatus.ON_THE_WAY,
                        profileImageUrl = ""
                    )
                )
            ),
            onEvent = {},
            onNavigateToBooking = {},
            onNavigateToMyBookings = {},
            onNavigateToNotifications = {},
            onNavigateToBookingDetails = {},
            onNavigateToAdmin = {},
            onNavigateToSearch = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenEmptyPreview() {
    MaidyTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                userName = "Sarah",
                searchQuery = "",
                hasNotifications = false,
                recentBookings = emptyList()
            ),
            onEvent = {},
            onNavigateToBooking = {},
            onNavigateToMyBookings = {},
            onNavigateToNotifications = {},
            onNavigateToBookingDetails = {},
            onNavigateToAdmin = {},
            onNavigateToSearch = {}
        )
    }
}

