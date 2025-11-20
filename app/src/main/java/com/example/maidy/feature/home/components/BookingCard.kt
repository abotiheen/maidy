package com.example.maidy.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.home.BookingItem
import com.example.maidy.feature.home.BookingStatus
import com.example.maidy.ui.theme.*

@Composable
fun BookingCard(
    booking: BookingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = HomeBookingCardBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = HomeCardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image Placeholder
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            // TODO: Load actual image when API is connected
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Booking Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = booking.serviceName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = HomeBookingTitle
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${booking.maidName} • ${booking.date}, ${booking.time}",
                fontSize = 14.sp,
                color = HomeBookingDetails
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Status Badge
        BookingStatusBadge(status = booking.status)
    }
}

@Composable
private fun BookingStatusBadge(
    status: BookingStatus,
    modifier: Modifier = Modifier
) {
    val (statusText, textColor, backgroundColor) = when (status) {
        BookingStatus.CONFIRMED -> Triple(
            "Confirmed",
            HomeStatusConfirmed,
            HomeStatusConfirmedBg
        )
        BookingStatus.IN_PROGRESS -> Triple(
            "In Progress",
            HomeStatusInProgress,
            HomeStatusInProgressBg
        )
        BookingStatus.COMPLETED -> Triple(
            "Completed",
            HomeStatusCompleted,
            HomeStatusCompletedBg
        )
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = statusText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookingCardConfirmedPreview() {
    MaidyTheme {
        BookingCard(
            booking = BookingItem(
                id = "1",
                serviceName = "Deep Cleaning",
                maidName = "Maria G.",
                date = "Tomorrow",
                time = "10:00 AM",
                status = BookingStatus.CONFIRMED
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookingCardInProgressPreview() {
    MaidyTheme {
        BookingCard(
            booking = BookingItem(
                id = "2",
                serviceName = "Standard Home Clean",
                maidName = "Jessica L.",
                date = "Today",
                time = "2:00 PM",
                status = BookingStatus.IN_PROGRESS
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookingCardCompletedPreview() {
    MaidyTheme {
        BookingCard(
            booking = BookingItem(
                id = "3",
                serviceName = "Move-out Clean",
                maidName = "Ana P.",
                date = "Oct 28",
                time = "9:00 AM",
                status = BookingStatus.COMPLETED
            ),
            onClick = {}
        )
    }
}




