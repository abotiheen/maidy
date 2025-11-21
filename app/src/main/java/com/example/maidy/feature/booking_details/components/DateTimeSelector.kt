package com.example.maidy.feature.booking_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.ui.theme.*

/**
 * Component for selecting date and time for one-time bookings
 * Displays date and time pickers in cards
 */
@Composable
fun DateTimeSelector(
    selectedDate: String,
    selectedTime: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date Selector
        DateTimeSelectorCard(
            icon = painterResource(R.drawable.calendar),
            label = "Select Date",
            value = selectedDate,
            onClick = onDateClick
        )
        
        // Time Selector
        DateTimeSelectorCard(
            icon = painterResource(R.drawable.time),
            label = "Select Time",
            value = selectedTime,
            onClick = onTimeClick
        )
    }
}

@Composable
private fun DateTimeSelectorCard(
    icon: Painter,
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BookingDetailsDateCardBackground)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            painter = icon,
            contentDescription = label,
            tint = BookingDetailsDateIcon,
            modifier = Modifier.size(24.dp)
        )
        
        // Text Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = BookingDetailsDateLabel
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = BookingDetailsDateValue
            )
        }
        
        // Chevron indicator (optional - could add dropdown icon here)
    }
}

@Preview(name = "Date Time Selector", showBackground = true)
@Composable
private fun DateTimeSelectorPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            DateTimeSelector(
                selectedDate = "November 20, 2024",
                selectedTime = "10:00 AM",
                onDateClick = {},
                onTimeClick = {}
            )
        }
    }
}

@Preview(name = "Date Selector Card", showBackground = true)
@Composable
private fun DateSelectorCardPreview() {
    MaidyTheme {
        DateTimeSelectorCard(
            icon = painterResource(R.drawable.calendar),
            label = "Select Date",
            value = "November 20, 2024",
            onClick = {}
        )
    }
}

@Preview(name = "Time Selector Card", showBackground = true)
@Composable
private fun TimeSelectorCardPreview() {
    MaidyTheme {
        DateTimeSelectorCard(
            icon = painterResource(R.drawable.calendar),
            label = "Select Time",
            value = "10:00 AM",
            onClick = {}
        )
    }
}

