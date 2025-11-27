package com.example.maidy.feature.all_bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import com.example.maidy.feature.all_bookings.components.BookingFilterChip
import com.example.maidy.feature.all_bookings.components.DateFilterButton
import com.example.maidy.feature.all_bookings.components.RecurringFilterChip
import com.example.maidy.feature.home.BookingItem
import com.example.maidy.feature.home.BookingStatus
import com.example.maidy.feature.home.components.BookingCard
import com.example.maidy.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllBookingsScreen(
    viewModel: AllBookingsViewModel = koinViewModel(),
    onNavigateToBookingDetails: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    AllBookingsScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToBookingDetails = onNavigateToBookingDetails,
        modifier = modifier
    )

    // Date Range Picker Dialog
    if (uiState.showDateRangePicker) {
        val dateRangePickerState = rememberDateRangePickerState(
            initialDisplayMode = DisplayMode.Picker
        )

        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = {
                viewModel.onEvent(AllBookingsUiEvent.OnDismissDatePicker)
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis
                        if (start != null && end != null) {
                            viewModel.onEvent(
                                AllBookingsUiEvent.OnDateRangeSelected(start, end)
                            )
                        }
                    },
                    enabled = dateRangePickerState.selectedStartDateMillis != null &&
                            dateRangePickerState.selectedEndDateMillis != null
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        viewModel.onEvent(AllBookingsUiEvent.OnDismissDatePicker)
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = "Select Date Range",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun AllBookingsScreenContent(
    uiState: AllBookingsUiState,
    onEvent: (AllBookingsUiEvent) -> Unit,
    onNavigateToBookingDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Content
    when {
        uiState.isLoading -> {
            // Loading State
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaidyBackgroundLight)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaidyBlue
                )
            }
        }

        uiState.error != null -> {
            // Error State
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaidyBackgroundLight)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error,
                    fontSize = 16.sp,
                    color = MaidyErrorRed,
                    textAlign = TextAlign.Center
                )
            }
        }

        uiState.filteredBookings.isEmpty() -> {
            // Empty State with Filters
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaidyBackgroundLight)
            ) {
                // Filters Section
                item {
                    FiltersSection(
                        statusFilters = uiState.statusFilters,
                        recurringFilter = uiState.recurringFilter,
                        startDateMillis = uiState.startDateMillis,
                        endDateMillis = uiState.endDateMillis,
                        onEvent = onEvent
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Empty Message
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📭",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No bookings found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaidyTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Try adjusting your filters",
                                fontSize = 14.sp,
                                color = MaidyTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        else -> {
            // Bookings List with Filters
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaidyBackgroundLight)
            ) {
                // Filters Section
                item {
                    FiltersSection(
                        statusFilters = uiState.statusFilters,
                        recurringFilter = uiState.recurringFilter,
                        startDateMillis = uiState.startDateMillis,
                        endDateMillis = uiState.endDateMillis,
                        onEvent = onEvent
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Results Count
                item {
                    Text(
                        text = "${uiState.filteredBookings.size} ${if (uiState.filteredBookings.size == 1) "booking" else "bookings"}",
                        fontSize = 14.sp,
                        color = MaidyTextSecondary,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                // Bookings
                items(
                    items = uiState.filteredBookings,
                    key = { booking -> booking.id }
                ) { booking ->
                    BookingCard(
                        booking = booking,
                        onClick = {
                            onEvent(AllBookingsUiEvent.OnBookingClick(booking.id))
                            onNavigateToBookingDetails(booking.id)
                        },
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                    )
                }

                // Bottom Spacing
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun FiltersSection(
    statusFilters: List<BookingFilterOption>,
    recurringFilter: BookingFilterOption,
    startDateMillis: Long?,
    endDateMillis: Long?,
    onEvent: (AllBookingsUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaidyBackgroundWhite)
            .padding(vertical = 12.dp)
    ) {
        // Status Filters
        Text(
            text = "Status",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaidyTextPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = statusFilters,
                key = { it.id }
            ) { filter ->
                BookingFilterChip(
                    label = filter.label,
                    isSelected = filter.isSelected,
                    onClick = {
                        onEvent(AllBookingsUiEvent.OnStatusFilterClick(filter.id))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Type & Date Filters
        Text(
            text = "Type & Date",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaidyTextPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RecurringFilterChip(
                isSelected = recurringFilter.isSelected,
                onClick = {
                    onEvent(AllBookingsUiEvent.OnRecurringFilterToggle)
                }
            )

            DateFilterButton(
                startDateMillis = startDateMillis,
                endDateMillis = endDateMillis,
                onClick = {
                    onEvent(AllBookingsUiEvent.OnDateFilterClick)
                },
                onClear = {
                    onEvent(AllBookingsUiEvent.OnClearDateFilter)
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AllBookingsScreenPreview() {
    MaidyTheme {
        AllBookingsScreenContent(
            uiState = AllBookingsUiState(
                filteredBookings = listOf(
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
                        dateTime = "Nov 18, 2024 at 9:00 AM",
                        status = BookingStatus.COMPLETED,
                        profileImageUrl = ""
                    ),
                    BookingItem(
                        id = "4",
                        serviceName = "Kitchen Deep Clean",
                        maidName = "Sarah K.",
                        dateTime = "Nov 15, 2024 at 2:00 PM",
                        status = BookingStatus.CANCELLED,
                        profileImageUrl = ""
                    )
                )
            ),
            onEvent = {},
            onNavigateToBookingDetails = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AllBookingsScreenEmptyPreview() {
    MaidyTheme {
        AllBookingsScreenContent(
            uiState = AllBookingsUiState(
                filteredBookings = emptyList(),
                statusFilters = listOf(
                    BookingFilterOption("completed", "Completed", isSelected = true)
                )
            ),
            onEvent = {},
            onNavigateToBookingDetails = {}
        )
    }
}
