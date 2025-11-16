package com.example.maidy.feature.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import com.example.maidy.feature.booking.BookingStatus
import com.example.maidy.ui.theme.*

@Composable
fun StatusIndicator(
    currentStatus: BookingStatus,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Confirmed
        StatusStep(
            label = "Confirmed",
            isActive = currentStatus == BookingStatus.CONFIRMED,
            isCompleted = currentStatus.ordinal >= BookingStatus.CONFIRMED.ordinal,
            activeColor = BookingStatusConfirmedIcon,
            modifier = Modifier.weight(1f)
        )
        
        // Connecting Line
        StatusLine(
            isCompleted = currentStatus.ordinal >= BookingStatus.ON_THE_WAY.ordinal
        )
        
        // On the Way
        StatusStep(
            label = "On the Way",
            isActive = currentStatus == BookingStatus.ON_THE_WAY,
            isCompleted = currentStatus.ordinal >= BookingStatus.ON_THE_WAY.ordinal,
            activeColor = BookingStatusOnWayIcon,
            modifier = Modifier.weight(1f)
        )
        
        // Connecting Line
        StatusLine(
            isCompleted = currentStatus.ordinal >= BookingStatus.IN_PROGRESS.ordinal
        )
        
        // In Progress
        StatusStep(
            label = "In Progress",
            isActive = currentStatus == BookingStatus.IN_PROGRESS,
            isCompleted = currentStatus.ordinal >= BookingStatus.IN_PROGRESS.ordinal,
            activeColor = BookingStatusInProgressIcon,
            modifier = Modifier.weight(1f)
        )
        
        // Connecting Line
        StatusLine(
            isCompleted = currentStatus == BookingStatus.COMPLETED
        )
        
        // Completed
        StatusStep(
            label = "Completed",
            isActive = currentStatus == BookingStatus.COMPLETED,
            isCompleted = currentStatus == BookingStatus.COMPLETED,
            activeColor = BookingStatusCompletedIcon,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatusLine(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(24.dp)
            .height(2.dp)
            .background(
                if (isCompleted) BookingStatusOnWayIcon
                else BookingStatusInactiveIcon
            )
    )
}

@Composable
fun StatusStep(
    label: String,
    isActive: Boolean,
    isCompleted: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isActive || isCompleted) activeColor
                    else BookingStatusInactiveIcon
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted && !isActive) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else if (isActive) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) activeColor else BookingStatusInactiveIcon
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatusIndicatorPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Confirmed Status", fontWeight = FontWeight.Bold)
        StatusIndicator(currentStatus = BookingStatus.CONFIRMED)
        
        Text("On the Way Status", fontWeight = FontWeight.Bold)
        StatusIndicator(currentStatus = BookingStatus.ON_THE_WAY)
        
        Text("In Progress Status", fontWeight = FontWeight.Bold)
        StatusIndicator(currentStatus = BookingStatus.IN_PROGRESS)
        
        Text("Completed Status", fontWeight = FontWeight.Bold)
        StatusIndicator(currentStatus = BookingStatus.COMPLETED)
    }
}

