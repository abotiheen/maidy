package com.example.maidy.feature_maid.booking_details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.core.model.Booking
import com.example.maidy.core.model.BookingStatus
import com.example.maidy.core.model.RecurringType
import com.example.maidy.feature_maid.auth.*
import com.google.firebase.Timestamp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

/**
 * Maid Booking Details Screen
 * Shows comprehensive booking information and status management
 */
@Composable
fun MaidBookingDetailsScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    viewModel: MaidBookingDetailsViewModel = koinViewModel { parametersOf(bookingId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar for messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(MaidBookingDetailsEvent.ClearMessages)
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(MaidBookingDetailsEvent.ClearMessages)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaidAppBackgroundLight
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaidAppGreen)
            }
        } else if (uiState.booking == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Booking not found",
                    fontSize = 16.sp,
                    color = Color(0xFF6B7280)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Card
                item {
                    BookingStatusCard(status = uiState.booking!!.status)
                }

                // Maid Details (own details)
                item {
                    MaidOwnDetailsCard(booking = uiState.booking!!)
                }

                // Recurrence Card (only if recurring)
                if (uiState.booking!!.isRecurring) {
                    item {
                        RecurrenceCard(
                            recurringType = uiState.booking!!.recurringType,
                            preferredDay = uiState.booking!!.preferredDay
                        )
                    }
                }

                // Customer Details
                item {
                    SectionHeader(title = "Customer Details")
                }

                item {
                    CustomerDetailsCard(
                        booking = uiState.booking!!,
                        onCallClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${uiState.booking!!.userPhoneNumber}")
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                // Schedule Details
                item {
                    SectionHeader(title = "Schedule Details")
                }

                item {
                    ScheduleDetailsCard(booking = uiState.booking!!)
                }

                // Special Instructions
                item {
                    SectionHeader(title = "Special Instructions")
                }

                item {
                    SpecialInstructionsCard(instructions = uiState.booking!!.specialInstructions)
                }

                // Action Buttons
                item {
                    ActionButtons(
                        status = uiState.booking!!.status,
                        isUpdating = uiState.isUpdatingStatus,
                        onAccept = { viewModel.onEvent(MaidBookingDetailsEvent.AcceptBooking) },
                        onReject = { viewModel.onEvent(MaidBookingDetailsEvent.RejectBooking) },
                        onCancel = { viewModel.onEvent(MaidBookingDetailsEvent.CancelBooking) },
                        onMarkOnTheWay = { viewModel.onEvent(MaidBookingDetailsEvent.MarkOnTheWay) },
                        onMarkInProgress = { viewModel.onEvent(MaidBookingDetailsEvent.MarkInProgress) },
                        onMarkCompleted = { viewModel.onEvent(MaidBookingDetailsEvent.MarkCompleted) }
                    )
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun BookingStatusCard(status: BookingStatus) {
    val (backgroundColor, textColor, title, subtitle) = when (status) {
        BookingStatus.PENDING -> Tuple4(
            Color(0xFFFFF3E0),
            Color(0xFFFF9800),
            "Pending",
            "This booking is awaiting your acceptance."
        )

        BookingStatus.CONFIRMED -> Tuple4(
            Color(0xFFE8F5E9),
            Color(0xFF4CAF50),
            "Confirmed",
            "Booking confirmed. Get ready for the job."
        )

        BookingStatus.ON_THE_WAY -> Tuple4(
            Color(0xFFE3F2FD),
            Color(0xFF2196F3),
            "On the Way",
            "You're heading to the customer's location."
        )

        BookingStatus.IN_PROGRESS -> Tuple4(
            Color(0xFFE1F5FE),
            Color(0xFF0288D1),
            "In Progress",
            "Job is currently in progress."
        )

        BookingStatus.COMPLETED -> Tuple4(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            "Completed",
            "This job has been completed successfully."
        )

        BookingStatus.CANCELLED -> Tuple4(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            "Cancelled",
            "This booking has been cancelled."
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun MaidOwnDetailsCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your Details",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = booking.maidFullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF000000)
                )
                Text(
                    text = "$${String.format("%.0f", booking.maidHourlyRate)}/hour",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }

            // Profile image
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                if (booking.maidProfileImageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = booking.maidProfileImageUrl,
                        contentDescription = "Maid profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaidAppGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun RecurrenceCard(recurringType: RecurringType?, preferredDay: String) {
    val recurringText = when (recurringType) {
        RecurringType.WEEKLY -> "This job repeats every week on $preferredDay."
        RecurringType.BIWEEKLY -> "This job repeats every two weeks on $preferredDay."
        RecurringType.MONTHLY -> "This job repeats every month on $preferredDay."
        null -> "Recurring booking"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = Color(0xFF2196F3).copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Recurring Booking",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2196F3)
                )
                Text(
                    text = recurringText,
                    fontSize = 14.sp,
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
}

@Composable
private fun CustomerDetailsCard(booking: Booking, onCallClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Customer profile image
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    if (booking.userProfileImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = booking.userProfileImageUrl,
                            contentDescription = "Customer profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaidAppGreen
                        )
                    }
                }

                Text(
                    text = booking.userFullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF000000)
                )
            }

            // Call button
            IconButton(
                onClick = onCallClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE3F2FD), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = "Call customer",
                    tint = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
private fun ScheduleDetailsCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date
            ScheduleDetailItem(
                icon = Icons.Outlined.DateRange,
                label = "Date",
                value = formatDate(booking.nextScheduledDate)
            )

            // Time
            ScheduleDetailItem(
                icon = Icons.Filled.Info,
                label = "Time",
                value = if (booking.isRecurring) booking.preferredHour else booking.bookingTime
            )

            // Service Type
            ScheduleDetailItem(
                icon = Icons.Outlined.Home,
                label = "Service",
                value = booking.bookingType.displayName()
            )
        }
    }
}

@Composable
private fun ScheduleDetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6B7280),
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
            )
        }

        Column {
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF000000)
            )
        }
    }
}

@Composable
private fun SpecialInstructionsCard(instructions: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = if (instructions.isBlank()) "No special instructions provided." else instructions,
            fontSize = 14.sp,
            color = if (instructions.isBlank()) Color(0xFF6B7280) else Color(0xFF000000),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun ActionButtons(
    status: BookingStatus,
    isUpdating: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCancel: () -> Unit,
    onMarkOnTheWay: () -> Unit,
    onMarkInProgress: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    when (status) {
        BookingStatus.PENDING -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = !isUpdating,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFC62828)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFFC62828),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Reject", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = !isUpdating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaidAppGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Accept Booking", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        BookingStatus.CONFIRMED -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = !isUpdating,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFC62828)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFFC62828),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onMarkOnTheWay,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = !isUpdating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("On the Way", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        BookingStatus.ON_THE_WAY -> {
            Button(
                onClick = onMarkInProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isUpdating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0288D1),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Start Job", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        BookingStatus.IN_PROGRESS -> {
            Button(
                onClick = onMarkCompleted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isUpdating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Complete Job", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        BookingStatus.COMPLETED, BookingStatus.CANCELLED -> {
            // No action buttons for completed or cancelled bookings
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF000000),
        modifier = Modifier.padding(top = 8.dp)
    )
}

private fun formatDate(timestamp: Timestamp?): String {
    if (timestamp == null) return "Date TBD"
    val date = timestamp.toDate()
    return SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(date)
}

// Helper data class for tuple
private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
