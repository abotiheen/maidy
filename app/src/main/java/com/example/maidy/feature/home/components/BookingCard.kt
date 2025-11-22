package com.example.maidy.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
        // Maid Profile Image
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (booking.profileImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = booking.profileImageUrl,
                    contentDescription = "Maid profile picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder icon when no image
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Booking Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Service name with recurring badge
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.serviceName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HomeBookingTitle
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.maidName,
                    fontSize = 14.sp,
                    color = HomeBookingDetails,
                    fontWeight = FontWeight.Medium
                )
                if (booking.isRecurring) {
                    Spacer(modifier = Modifier.width(8.dp))
                    RecurringBadge()
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = booking.dateTime,
                fontSize = 13.sp,
                color = HomeBookingDetails
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Status Badge
        BookingStatusBadge(status = booking.status)
    }
}

@Composable
private fun RecurringBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = HomeNotificationBadge,
                shape = CircleShape
            )
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Recurring",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
    }
}

@Composable
private fun BookingStatusBadge(
    status: BookingStatus,
    modifier: Modifier = Modifier
) {
    val (statusText, textColor, backgroundColor) = when (status) {
        BookingStatus.PENDING -> Triple(
            "Pending",
            BookingStatusPendingIcon,
            BookingStatusPendingIcon.copy(alpha = 0.15f)
        )

        BookingStatus.CONFIRMED -> Triple(
            "Confirmed",
            BookingStatusConfirmedIcon,
            BookingStatusConfirmedIcon.copy(alpha = 0.15f)
        )

        BookingStatus.ON_THE_WAY -> Triple(
            "On the Way",
            BookingStatusOnWayIcon,
            BookingStatusOnWayIcon.copy(alpha = 0.15f)
        )

        BookingStatus.IN_PROGRESS -> Triple(
            "In Progress",
            BookingStatusInProgressIcon,
            BookingStatusInProgressIcon.copy(alpha = 0.15f)
        )

        BookingStatus.COMPLETED -> Triple(
            "Completed",
            BookingStatusCompletedIcon,
            BookingStatusCompletedIcon.copy(alpha = 0.15f)
        )

        BookingStatus.CANCELLED -> Triple(
            "Cancelled",
            EmergencyIconBackground,
            BookingStatusCancelButton
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
                dateTime = "Nov 25, 2024 at 10:00 AM",
                status = BookingStatus.CONFIRMED,
                profileImageUrl = ""
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookingCardRecurringPreview() {
    MaidyTheme {
        BookingCard(
            booking = BookingItem(
                id = "2",
                serviceName = "Standard Cleaning",
                maidName = "Jessica L.",
                dateTime = "Nov 21, 2024 at 2:00 PM",
                status = BookingStatus.IN_PROGRESS,
                profileImageUrl = "",
                isRecurring = true
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookingCardOnTheWayPreview() {
    MaidyTheme {
        BookingCard(
            booking = BookingItem(
                id = "3",
                serviceName = "Move-out Clean",
                maidName = "Ana P.",
                dateTime = "Nov 21, 2024 at 9:00 AM",
                status = BookingStatus.CANCELLED,
                profileImageUrl = ""
            ),
            onClick = {}
        )
    }
}




