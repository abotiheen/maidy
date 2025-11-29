package com.example.maidy.feature.booking_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.maidy.feature.booking_details.components.*
import com.example.maidy.core.model.RecurringType
import com.example.maidy.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Main screen for booking service details
 * Allows user to select service type, schedule (one-time or recurring), and add special instructions
 * 
 * @param maidId The ID of the maid to book
 * @param onBackClick Callback when back button is clicked
 * @param onBookingConfirmed Callback when booking is successfully confirmed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailsScreen(
    maidId: String,
    onBackClick: () -> Unit,
    onBookingConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Get ViewModel with maidId parameter from Koin
    val viewModel: BookingDetailsViewModel = org.koin.androidx.compose.koinViewModel(
        parameters = { org.koin.core.parameter.parametersOf(maidId) }
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    // Dialog states
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showRecurringTimePicker by remember { mutableStateOf(false) }
    
    // Handle booking confirmation navigation
    LaunchedEffect(uiState.bookingConfirmed) {
        if (uiState.bookingConfirmed) {
            onBookingConfirmed()
        }
    }
    
    // Show error message if any
    uiState.errorMessage?.let { error ->
        // TODO: Show error in a Snackbar or AlertDialog
        println("❌ BookingDetailsScreen: Error - $error")
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
                            val formattedDate = formatter.format(date)
                            viewModel.onEvent(BookingDetailsEvent.DateSelected(formattedDate))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Time Picker Dialog (for one-time bookings)
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
                viewModel.onEvent(BookingDetailsEvent.TimeSelected(formattedTime))
                showTimePicker = false
            }
        )
    }
    
    // Time Picker Dialog (for recurring bookings)
    if (showRecurringTimePicker) {
        TimePickerDialog(
            onDismiss = { showRecurringTimePicker = false },
            onConfirm = { hour, minute ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                val formatter = SimpleDateFormat("hh:mm a", Locale.US)
                val formattedTime = formatter.format(calendar.time)
                viewModel.onEvent(BookingDetailsEvent.RecurringTimeSelected(formattedTime))
                showRecurringTimePicker = false
            }
        )
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BookingDetailsBackground)
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
                // Maid Information Header
                if (uiState.isLoadingMaid) {
                    // Show loading state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp)
                            .background(BookingDetailsTopBarBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = BookingDetailsDateIcon,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    MaidInfoHeader(
                        maidName = uiState.maidName,
                        rating = uiState.maidRating,
                        reviewsCount = uiState.maidReviewsCount,
                        pricePerHour = "$${uiState.maidHourlyRate}/hour",
                        isVerified = uiState.isVerified,
                        profileImageUrl = uiState.maidProfileImageUrl
                    )
                }
                // Service Type Selector
                ServiceTypeSelector(
                    selectedServiceType = uiState.selectedServiceType,
                    onServiceTypeSelected = { serviceType ->
                        viewModel.onEvent(BookingDetailsEvent.ServiceTypeSelected(serviceType))
                    }
                )
                
                // Schedule Toggle (One-Time / Recurring)
                ScheduleToggle(
                    selectedScheduleType = uiState.scheduleType,
                    onScheduleTypeSelected = { scheduleType ->
                        viewModel.onEvent(BookingDetailsEvent.ScheduleTypeSelected(scheduleType))
                    }
                )
                
                // Date/Time Selector or Recurring Schedule Selector
                when (uiState.scheduleType) {
                    ScheduleType.ONE_TIME -> {
                        DateTimeSelector(
                            selectedDate = uiState.selectedDate,
                            selectedTime = uiState.selectedTime,
                            onDateClick = { showDatePicker = true },
                            onTimeClick = { showTimePicker = true }
                        )
                    }
                    ScheduleType.RECURRING -> {
                        // Recurring Type Selector (Weekly, Biweekly, Monthly)
                        RecurringTypeSelector(
                            selectedType = uiState.recurringType,
                            onTypeSelected = { recurringType ->
                                viewModel.onEvent(BookingDetailsEvent.RecurringTypeSelected(recurringType))
                            }
                        )
                        
                        // Day and Time Selector
                        RecurringScheduleSelector(
                            selectedDay = uiState.selectedDay,
                            selectedTime = uiState.recurringTime,
                            onDaySelected = { day ->
                                viewModel.onEvent(BookingDetailsEvent.DaySelected(day))
                            },
                            onTimeClick = { showRecurringTimePicker = true }
                        )
                    }
                }

                PaymentMethodSelector()

                // Special Instructions Field
                SpecialInstructionsField(
                    value = uiState.specialInstructions,
                    onValueChange = { notes ->
                        viewModel.onEvent(BookingDetailsEvent.NotesChanged(notes))
                    }
                )
                
                // Add some bottom spacing for the button
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        
        // Bottom Confirm Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            ConfirmBookingButton(
                onClick = { viewModel.onEvent(BookingDetailsEvent.ConfirmBooking) },
                isLoading = uiState.isLoading
            )
        }
    }
}

/**
 * Custom Time Picker Dialog
 */
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
                .background(
                    color = BookingDetailsDateCardBackground,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Time",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BookingDetailsSectionTitle,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            TimePicker(
                state = timePickerState,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
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

@Preview(name = "One-Time Booking", showBackground = true)
@Composable
private fun BookingDetailsScreenOneTimePreview() {
    MaidyTheme {
        BookingDetailsScreen(
            maidId = "preview_maid_id",
            onBackClick = {},
            onBookingConfirmed = {}
        )
    }
}

@Preview(name = "Recurring Booking", showBackground = true)
@Composable
private fun BookingDetailsScreenRecurringPreview() {
    MaidyTheme {
        BookingDetailsScreen(
            maidId = "preview_maid_id",
            onBackClick = {},
            onBookingConfirmed = {}
        )
    }
}

@Preview(name = "With Special Instructions", showBackground = true)
@Composable
private fun BookingDetailsScreenWithNotesPreview() {
    MaidyTheme {
        BookingDetailsScreen(
            maidId = "preview_maid_id",
            onBackClick = {},
            onBookingConfirmed = {}
        )
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
private fun BookingDetailsScreenLoadingPreview() {
    MaidyTheme {
        BookingDetailsScreen(
            maidId = "preview_maid_id",
            onBackClick = {},
            onBookingConfirmed = {}
        )
    }
}

