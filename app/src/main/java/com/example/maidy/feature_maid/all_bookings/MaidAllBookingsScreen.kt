package com.example.maidy.feature_maid.all_bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature_maid.all_bookings.components.MaidBookingFilterChip
import com.example.maidy.feature_maid.all_bookings.components.MaidDateFilterButton
import com.example.maidy.feature_maid.all_bookings.components.MaidRecurringFilterChip
import com.example.maidy.feature_maid.auth.MaidAppBackgroundLight
import com.example.maidy.feature_maid.auth.MaidAppGreen
import com.example.maidy.feature_maid.auth.MaidAppTextPrimary
import com.example.maidy.feature_maid.auth.MaidAppTextSecondary
import com.example.maidy.feature_maid.home.MaidBookingItem
import com.example.maidy.ui.theme.MaidyTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaidAllBookingsScreen(
    viewModel: MaidAllBookingsViewModel = koinViewModel(),
    onNavigateToBookingDetails: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MaidAllBookingsScreenContent(
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

        DatePickerDialog(
            onDismissRequest = {
                viewModel.onEvent(MaidAllBookingsEvent.OnDismissDatePicker)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis
                        if (start != null && end != null) {
                            viewModel.onEvent(
                                MaidAllBookingsEvent.OnDateRangeSelected(start, end)
                            )
                        }
                    },
                    enabled = dateRangePickerState.selectedStartDateMillis != null &&
                            dateRangePickerState.selectedEndDateMillis != null
                ) {
                    Text("OK", color = MaidAppGreen)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(MaidAllBookingsEvent.OnDismissDatePicker)
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
private fun MaidAllBookingsScreenContent(
    uiState: MaidAllBookingsUiState,
    onEvent: (MaidAllBookingsEvent) -> Unit,
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
                    .background(MaidAppBackgroundLight)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaidAppGreen
                )
            }
        }

        uiState.error != null -> {
            // Error State
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaidAppBackgroundLight)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error,
                    fontSize = 16.sp,
                    color = Color(0xFFC62828),
                    textAlign = TextAlign.Center
                )
            }
        }

        uiState.filteredBookings.isEmpty() -> {
            // Empty State with Filters
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaidAppBackgroundLight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filters Section
                item {
                    MaidFiltersSection(
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
                                color = MaidAppTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Try adjusting your filters",
                                fontSize = 14.sp,
                                color = MaidAppTextSecondary,
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
                    .background(MaidAppBackgroundLight),
            ) {
                // Filters Section
                item {
                    MaidFiltersSection(
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
                        color = MaidAppTextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Bookings
                items(
                    items = uiState.filteredBookings,
                    key = { booking -> booking.id }
                ) { booking ->
                    MaidBookingItem(
                        booking = booking,
                        onClick = {
                            onEvent(MaidAllBookingsEvent.OnBookingClick(booking.id))
                            onNavigateToBookingDetails(booking.id)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
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
private fun MaidFiltersSection(
    statusFilters: List<MaidBookingFilterOption>,
    recurringFilter: MaidBookingFilterOption,
    startDateMillis: Long?,
    endDateMillis: Long?,
    onEvent: (MaidAllBookingsEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp)
    ) {
        // Status Filters
        Text(
            text = "Status",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaidAppTextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = statusFilters,
                key = { it.id }
            ) { filter ->
                MaidBookingFilterChip(
                    label = filter.label,
                    isSelected = filter.isSelected,
                    onClick = {
                        onEvent(MaidAllBookingsEvent.OnStatusFilterClick(filter.id))
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
            color = MaidAppTextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MaidRecurringFilterChip(
                isSelected = recurringFilter.isSelected,
                onClick = {
                    onEvent(MaidAllBookingsEvent.OnRecurringFilterToggle)
                }
            )

            MaidDateFilterButton(
                startDateMillis = startDateMillis,
                endDateMillis = endDateMillis,
                onClick = {
                    onEvent(MaidAllBookingsEvent.OnDateFilterClick)
                },
                onClear = {
                    onEvent(MaidAllBookingsEvent.OnClearDateFilter)
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MaidAllBookingsScreenEmptyPreview() {
    MaidyTheme {
        MaidAllBookingsScreenContent(
            uiState = MaidAllBookingsUiState(
                filteredBookings = emptyList(),
                statusFilters = listOf(
                    MaidBookingFilterOption("completed", "Completed", isSelected = true)
                )
            ),
            onEvent = {},
            onNavigateToBookingDetails = {}
        )
    }
}
