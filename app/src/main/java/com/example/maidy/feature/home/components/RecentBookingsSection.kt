package com.example.maidy.feature.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.home.BookingItem
import com.example.maidy.feature.home.BookingStatus
import com.example.maidy.ui.theme.HomeSectionTitle
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun RecentBookingsSection(
    bookings: List<BookingItem>,
    onBookingClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Your Recent Bookings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = HomeSectionTitle,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            bookings.forEach { booking ->
                BookingCard(
                    booking = booking,
                    onClick = { onBookingClick(booking.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecentBookingsSectionPreview() {
    MaidyTheme {
        RecentBookingsSection(
            bookings = listOf(
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
            ),
            onBookingClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecentBookingsSectionEmptyPreview() {
    MaidyTheme {
        RecentBookingsSection(
            bookings = emptyList(),
            onBookingClick = {}
        )
    }
}

