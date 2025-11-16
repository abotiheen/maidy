package com.example.maidy.feature.home

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
import com.example.maidy.feature.home.components.*
import com.example.maidy.ui.theme.MaidyBackgroundWhite
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToBooking: () -> Unit = {},
    onNavigateToSchedule: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToBookingDetails: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToBooking = onNavigateToBooking,
        onNavigateToSchedule = onNavigateToSchedule,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToBookingDetails = onNavigateToBookingDetails
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    onNavigateToBooking: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToBookingDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaidyBackgroundWhite)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with Profile, Title, and Notifications
        HomeHeader(
            profileImageUrl = uiState.profileImageUrl,
            hasNotifications = uiState.hasNotifications,
            onNotificationClick = {
                onEvent(HomeUiEvent.OnNotificationClick)
                onNavigateToNotifications()
            }
        )

        // Greeting
        GreetingSection(userName = uiState.userName)

        // Search Bar
        HomeSearchBar(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = { query ->
                onEvent(HomeUiEvent.OnSearchQueryChange(query))
            }
        )

        // Action Buttons
        ActionButtons(
            onBookNowClick = {
                onEvent(HomeUiEvent.OnBookNowClick)
                onNavigateToBooking()
            },
            onScheduleClick = {
                onEvent(HomeUiEvent.OnScheduleClick)
                onNavigateToSchedule()
            }
        )

        // Recent Bookings Section
        RecentBookingsSection(
            bookings = uiState.recentBookings,
            onBookingClick = { bookingId ->
                onEvent(HomeUiEvent.OnBookingClick(bookingId))
                onNavigateToBookingDetails(bookingId)
            }
        )

        // Add spacing at the bottom for better scroll experience
        Spacer(modifier = Modifier.height(32.dp))
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
                        date = "Tomorrow",
                        time = "10:00 AM",
                        status = BookingStatus.CONFIRMED
                    ),
                    BookingItem(
                        id = "2",
                        serviceName = "Standard Home Clean",
                        maidName = "Jessica L.",
                        date = "Today",
                        time = "2:00 PM",
                        status = BookingStatus.IN_PROGRESS
                    ),
                    BookingItem(
                        id = "3",
                        serviceName = "Move-out Clean",
                        maidName = "Ana P.",
                        date = "Oct 28",
                        time = "9:00 AM",
                        status = BookingStatus.COMPLETED
                    )
                )
            ),
            onEvent = {},
            onNavigateToBooking = {},
            onNavigateToSchedule = {},
            onNavigateToNotifications = {},
            onNavigateToBookingDetails = {}
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
            onNavigateToSchedule = {},
            onNavigateToNotifications = {},
            onNavigateToBookingDetails = {}
        )
    }
}

