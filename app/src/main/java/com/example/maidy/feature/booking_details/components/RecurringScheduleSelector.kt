package com.example.maidy.feature.booking_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.feature.booking_details.Day
import com.example.maidy.ui.theme.*

/**
 * Component for selecting recurring schedule
 * Shows day selector and time picker for recurring bookings
 * Note: Only allows single day selection for recurring bookings
 */
@Composable
fun RecurringScheduleSelector(
    selectedDay: Day?,
    selectedTime: String,
    onDaySelected: (Day) -> Unit,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Day Selector Section
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Preferred Day",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = BookingDetailsSectionTitle
            )
            
            DaySelector(
                selectedDay = selectedDay,
                onDaySelected = onDaySelected
            )
        }
        
        // Time Selector Section
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Preferred Time",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = BookingDetailsSectionTitle
            )
            
            // Time Selector Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BookingDetailsTimeCardBackground)
                    .clickable(onClick = onTimeClick)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.time),
                    contentDescription = "Select time",
                    tint = BookingDetailsTimeIcon,
                    modifier = Modifier.size(24.dp)
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Select Time",
                        fontSize = 14.sp,
                        color = BookingDetailsTimeLabel
                    )
                    Text(
                        text = selectedTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BookingDetailsTimeValue
                    )
                }
            }
        }
    }
}

@Composable
private fun DaySelector(
    selectedDay: Day?,
    onDaySelected: (Day) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BookingDetailsDaySelectorBackground)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Day.values().forEach { day ->
            DayChip(
                day = day,
                isSelected = selectedDay == day,
                onClick = { onDaySelected(day) }
            )
        }
    }
}

@Composable
private fun DayChip(
    day: Day,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        BookingDetailsDaySelectedBackground
    } else {
        BookingDetailsDayUnselectedBackground
    }
    
    val textColor = if (isSelected) {
        BookingDetailsDaySelectedText
    } else {
        BookingDetailsDayUnselectedText
    }
    
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (!isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = BookingDetailsDayUnselectedBorder,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.shortName,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Preview(name = "Recurring Schedule Selector - No Selection", showBackground = true)
@Composable
private fun RecurringScheduleSelectorEmptyPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            RecurringScheduleSelector(
                selectedDay = null,
                selectedTime = "10:00 AM",
                onDaySelected = {},
                onTimeClick = {}
            )
        }
    }
}

@Preview(name = "Recurring Schedule Selector - With Selection", showBackground = true)
@Composable
private fun RecurringScheduleSelectorWithSelectionPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            RecurringScheduleSelector(
                selectedDay = Day.TUESDAY,
                selectedTime = "10:00 AM",
                onDaySelected = {},
                onTimeClick = {}
            )
        }
    }
}

@Preview(name = "Day Selector", showBackground = true)
@Composable
private fun DaySelectorPreview() {
    MaidyTheme {
        DaySelector(
            selectedDay = Day.MONDAY,
            onDaySelected = {}
        )
    }
}

@Preview(name = "Day Chip - Selected", showBackground = true)
@Composable
private fun DayChipSelectedPreview() {
    MaidyTheme {
        DayChip(
            day = Day.TUESDAY,
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(name = "Day Chip - Unselected", showBackground = true)
@Composable
private fun DayChipUnselectedPreview() {
    MaidyTheme {
        DayChip(
            day = Day.MONDAY,
            isSelected = false,
            onClick = {}
        )
    }
}

