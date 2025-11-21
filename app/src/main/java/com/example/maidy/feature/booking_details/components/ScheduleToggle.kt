package com.example.maidy.feature.booking_details.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.booking_details.ScheduleType
import com.example.maidy.ui.theme.*

/**
 * Animated toggle component for switching between One-Time and Recurring schedules
 * Uses smooth sliding animation similar to AuthTabRow
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScheduleToggle(
    selectedScheduleType: ScheduleType,
    onScheduleTypeSelected: (ScheduleType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Title
        Text(
            text = "Schedule",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BookingDetailsSectionTitle
        )
        
        // Toggle
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BookingDetailsToggleBackground)
                .padding(4.dp)
        ) {
            val tabWidth = (maxWidth - 4.dp) / 2
            
            // Animated indicator offset
            val indicatorOffset by animateDpAsState(
                targetValue = if (selectedScheduleType == ScheduleType.ONE_TIME) 0.dp else tabWidth + 4.dp,
                animationSpec = tween(durationMillis = 300),
                label = "schedule_toggle_indicator"
            )
            
            // Sliding white background indicator
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(tabWidth)
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BookingDetailsToggleSelected)
            )
            
            // Toggle content
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ToggleItem(
                    text = "One-Time",
                    isSelected = selectedScheduleType == ScheduleType.ONE_TIME,
                    onClick = { onScheduleTypeSelected(ScheduleType.ONE_TIME) },
                    modifier = Modifier.weight(1f)
                )
                ToggleItem(
                    text = "Recurring",
                    isSelected = selectedScheduleType == ScheduleType.RECURRING,
                    onClick = { onScheduleTypeSelected(ScheduleType.RECURRING) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ToggleItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) BookingDetailsToggleTextSelected else BookingDetailsToggleTextUnselected
        )
    }
}

@Preview(name = "One-Time Selected", showBackground = true)
@Composable
private fun ScheduleToggleOneTimePreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            ScheduleToggle(
                selectedScheduleType = ScheduleType.ONE_TIME,
                onScheduleTypeSelected = {}
            )
        }
    }
}

@Preview(name = "Recurring Selected", showBackground = true)
@Composable
private fun ScheduleToggleRecurringPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            ScheduleToggle(
                selectedScheduleType = ScheduleType.RECURRING,
                onScheduleTypeSelected = {}
            )
        }
    }
}

