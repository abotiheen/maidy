package com.example.maidy.feature.adjust_recurring

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.maidy.core.model.BookingType
import com.example.maidy.core.model.RecurringType
import com.example.maidy.feature.adjust_recurring.components.*
import com.example.maidy.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustRecurringScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    onNavigateToRating: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: AdjustRecurringViewModel = koinViewModel(
        parameters = { parametersOf(bookingId) }
    )

    val uiState by viewModel.uiState.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    // Handle success navigation
    LaunchedEffect(uiState.changesSaved) {
        if (uiState.changesSaved) {
            onNavigateBack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AdjustRecurringScreenBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Maid Card
                if (!uiState.isLoading) {
                    AdjustRecurringMaidCard(
                        maidName = uiState.maidName,
                        rating = uiState.maidRating,
                        reviewsCount = uiState.maidReviewsCount,
                        profileImageUrl = uiState.maidProfileImageUrl,
                        showRateButton = uiState.hasCompletedBefore,
                        onRateClick = onNavigateToRating
                    )
                    // Frequency Selector
                    AdjustRecurringFrequencySelector(
                        selectedType = uiState.recurringType,
                        onTypeSelected = { type ->
                            viewModel.onEvent(AdjustRecurringEvent.RecurringTypeSelected(type))
                        }
                    )

                    // Day Selector
                    AdjustRecurringDaySelector(
                        selectedDay = uiState.selectedDay,
                        onDaySelected = { day ->
                            viewModel.onEvent(AdjustRecurringEvent.DaySelected(day))
                        }
                    )

                    // Time Selector
                    AdjustRecurringTimeSelector(
                        selectedTime = uiState.selectedTime,
                        onTimeClick = { showTimePicker = true }
                    )

                    // Service Selector
                    AdjustRecurringServiceSelector(
                        selectedService = uiState.selectedServiceType,
                        onServiceSelected = { service ->
                            viewModel.onEvent(AdjustRecurringEvent.ServiceTypeSelected(service))
                        }
                    )

                    // Bottom spacing for button
                    Spacer(modifier = Modifier.height(80.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AdjustRecurringSaveButton)
                    }
                }
            }
        }

        // Save Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(AdjustRecurringScreenBackground)
        ) {
            AdjustRecurringSaveButton(
                onClick = { viewModel.onEvent(AdjustRecurringEvent.SaveChanges) },
                isLoading = uiState.isSaving
            )
        }

        // Loading State
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AdjustRecurringSaveButton)
            }
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                val formatter = SimpleDateFormat("hh:mm a", Locale.US)
                val formattedTime = formatter.format(calendar.time)
                viewModel.onEvent(AdjustRecurringEvent.TimeSelected(formattedTime))
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(
                    color = AdjustRecurringCardBackground,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Time",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AdjustRecurringSectionTitle,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TimePicker(
                state = timePickerState,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        onConfirm(timePickerState.hour, timePickerState.minute)
                    }
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdjustRecurringScreenPreview() {
    MaidyTheme {
        AdjustRecurringScreen(
            bookingId = "preview_booking_id",
            onNavigateBack = {}
        )
    }
}
