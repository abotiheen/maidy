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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.core.model.BookingStatus
import com.example.maidy.ui.theme.*

@Composable
fun StatusIndicator(
    currentStatus: BookingStatus,
    modifier: Modifier = Modifier
) {
    val statusSteps = listOf(
        BookingStatus.PENDING,
        BookingStatus.CONFIRMED,
        BookingStatus.ON_THE_WAY,
        BookingStatus.IN_PROGRESS,
        BookingStatus.COMPLETED
    )
    val currentIndex = statusSteps.indexOf(currentStatus).coerceAtLeast(0)
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        statusSteps.forEachIndexed { index, status ->
            StatusStep(
                label = statusLabel(status),
                isActive = index == currentIndex,
                isCompleted = currentIndex > index,
                activeColor = statusColor(status),
                modifier = Modifier.weight(1f)
            )
            
            if (index < statusSteps.lastIndex) {
                StatusLine(
                    isCompleted = currentIndex > index,
                    color = statusColor(statusSteps[index + 1]),
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(top = 11.dp)
                )
            }
        }
    }
}

private fun statusLabel(status: BookingStatus): String {
    return when (status) {
        BookingStatus.PENDING -> "Pending"
        BookingStatus.CONFIRMED -> "Confirmed"
        BookingStatus.ON_THE_WAY -> "On Way"
        BookingStatus.IN_PROGRESS -> "Progress"
        BookingStatus.COMPLETED -> "Completed"
        BookingStatus.CANCELLED -> "Cancelled"
    }
}

private fun statusColor(status: BookingStatus): Color {
    return when (status) {
        BookingStatus.PENDING -> BookingStatusPendingIcon
        BookingStatus.CONFIRMED -> BookingStatusConfirmedIcon
        BookingStatus.ON_THE_WAY -> BookingStatusOnWayIcon
        BookingStatus.IN_PROGRESS -> BookingStatusInProgressIcon
        BookingStatus.COMPLETED -> BookingStatusCompletedIcon
        BookingStatus.CANCELLED -> BookingStatusInactiveIcon
    }
}

@Composable
fun StatusLine(
    isCompleted: Boolean,
    color: Color = BookingStatusOnWayIcon,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(2.dp)
            .background(
                if (isCompleted) color
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
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
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
                    modifier = Modifier.size(16.dp)
                )
            } else if (isActive) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive || isCompleted) activeColor else BookingStatusInactiveIcon,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
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
        Text("Pending Status", fontWeight = FontWeight.Bold)
        StatusIndicator(currentStatus = BookingStatus.PENDING)
        
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
