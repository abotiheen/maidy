package com.example.maidy.feature_maid.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.core.model.Booking
import com.example.maidy.feature_maid.auth.*
import com.example.maidy.ui.theme.MaidyTheme
import com.google.firebase.Timestamp
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Maid Home Screen - Shows welcome, availability toggle, and recent bookings
 */
@Composable
fun MaidHomeScreen(
    viewModel: MaidHomeViewModel = koinViewModel(),
    onBookingClick: (String) -> Unit = {},
    onNavigateToAllBookings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaidAppBackgroundLight)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top spacing
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Welcome Header
        item {
            MaidWelcomeHeader(
                maidName = uiState.maid?.fullName ?: "",
                profileImageUrl = uiState.maid?.profileImageUrl ?: "",
                isLoading = uiState.isLoading
            )
        }

        // Availability Card
        item {
            MaidAvailabilityCard(
                isAvailable = uiState.isAvailable,
                isUpdating = uiState.isUpdatingAvailability,
                onToggle = { isAvailable ->
                    viewModel.onEvent(MaidHomeEvent.ToggleAvailability(isAvailable))
                }
            )
        }

        // Manage Bookings Button
        item {
            Button(
                onClick = onNavigateToAllBookings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Manage My Bookings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Recent Bookings Section
        item {
            Text(
                text = "Your Recent Bookings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Loading state
        if (uiState.isLoadingBookings) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaidAppGreen)
                }
            }
        }

        // Bookings list
        items(uiState.recentBookings) { booking ->
            MaidBookingItem(
                booking = booking,
                onClick = { onBookingClick(booking.id) }
            )
        }

        // Empty state
        if (!uiState.isLoadingBookings && uiState.recentBookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No bookings yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF000000)
                        )
                        Text(
                            text = "New bookings will appear here",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }

        // Bottom spacing
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    // Error handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Show error toast or snackbar
            kotlinx.coroutines.delay(3000)
            viewModel.onEvent(MaidHomeEvent.ClearError)
        }
    }
}

@Composable
private fun MaidWelcomeHeader(
    maidName: String,
    profileImageUrl: String,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile and welcome message
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image with online indicator
            Box {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaidAppGreen,
                            strokeWidth = 2.dp
                        )
                    } else if (profileImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaidAppGreen
                        )
                    }
                }

                // Online indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaidAppGreen)
                    )
                }
            }

            // Welcome text
            Column {
                Text(
                    text = "Welcome back,",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = if (isLoading) "Loading..." else maidName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
            }
        }

        // Notification icon
        Surface(
            color = Color.White,
            shape = CircleShape,
            shadowElevation = 2.dp
        ) {
            IconButton(onClick = { /* TODO: Show notifications */ }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun MaidAvailabilityCard(
    isAvailable: Boolean,
    isUpdating: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) MaidAppGreen else Color(0xFFE0E0E0)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isAvailable) "Available for Bookings" else "Currently Unavailable",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAvailable) Color.White else Color(0xFF6B7280)
                )
                Text(
                    text = if (isAvailable) "You are visible to customers" else "Turn on to receive bookings",
                    fontSize = 14.sp,
                    color = if (isAvailable) Color.White.copy(alpha = 0.9f) else Color(0xFF6B7280)
                )
            }

            if (isUpdating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = if (isAvailable) Color.White else MaidAppGreen,
                    strokeWidth = 3.dp
                )
            } else {
                Switch(
                    checked = isAvailable,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.White.copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFBDBDBD)
                    ),
                    modifier = Modifier.height(48.dp)
                )
            }
        }
    }
}

@Composable
internal fun MaidBookingItem(booking: Booking, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Service icon
            val iconColor = when (booking.bookingType.name) {
                "STANDARD_CLEANING" -> Color(0xFFFFF3E0)
                "DEEP_CLEANING" -> Color(0xFFE3F2FD)
                "MOVE_OUT_CLEAN" -> Color(0xFFE8F5E9)
                else -> Color(0xFFF5F5F5)
            }

            val iconTint = when (booking.bookingType.name) {
                "STANDARD_CLEANING" -> Color(0xFFFF9800)
                "DEEP_CLEANING" -> Color(0xFF2196F3)
                "MOVE_OUT_CLEAN" -> Color(0xFF4CAF50)
                else -> Color(0xFF9E9E9E)
            }

            Surface(
                color = iconColor,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Booking info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Service type name
                Text(
                    text = booking.bookingType.displayName(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF000000)
                )

                // Customer name and date
                Text(
                    text = "${booking.userFullName}, ${formatBookingDate(booking.nextScheduledDate)}",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )

                // Status and recurring badges row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status badge
                    BookingStatusBadge(status = booking.status)

                    // Recurring badge (only if recurring)
                    if (booking.isRecurring) {
                        RecurringBadge()
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingStatusBadge(status: com.example.maidy.core.model.BookingStatus) {
    val (backgroundColor, textColor, statusText) = when (status) {
        com.example.maidy.core.model.BookingStatus.PENDING -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFFF9800),
            "Pending"
        )

        com.example.maidy.core.model.BookingStatus.CONFIRMED -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF4CAF50),
            "Confirmed"
        )

        com.example.maidy.core.model.BookingStatus.ON_THE_WAY -> Triple(
            Color(0xFFE3F2FD),
            Color(0xFF2196F3),
            "On the Way"
        )

        com.example.maidy.core.model.BookingStatus.IN_PROGRESS -> Triple(
            Color(0xFFE1F5FE),
            Color(0xFF0288D1),
            "In Progress"
        )

        com.example.maidy.core.model.BookingStatus.COMPLETED -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            "Completed"
        )

        com.example.maidy.core.model.BookingStatus.CANCELLED -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            "Cancelled"
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = statusText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun RecurringBadge() {
    Surface(
        color = Color(0xFFF3E5F5),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                tint = Color(0xFF7B1FA2),
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "Recurring",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF7B1FA2)
            )
        }
    }
}

private fun formatBookingDate(timestamp: Timestamp?): String {
    if (timestamp == null) return "Date TBD"

    val date = timestamp.toDate()
    val now = Calendar.getInstance()
    val bookingCal = Calendar.getInstance().apply { time = date }

    return when {
        now.get(Calendar.YEAR) == bookingCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == bookingCal.get(Calendar.DAY_OF_YEAR) -> {
            "Today at ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)}"
        }

        now.get(Calendar.YEAR) == bookingCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) + 1 == bookingCal.get(Calendar.DAY_OF_YEAR) -> {
            "Tomorrow at ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)}"
        }

        now.get(Calendar.YEAR) == bookingCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - 1 == bookingCal.get(Calendar.DAY_OF_YEAR) -> {
            "Yesterday"
        }

        else -> {
            SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(date)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidHomeScreenPreview() {
    MaidyTheme {
        MaidHomeScreen()
    }
}
