package com.example.maidy.feature_maid.all_bookings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MaidDateFilterButton(
    startDateMillis: Long?,
    endDateMillis: Long?,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasDateRange = startDateMillis != null && endDateMillis != null
    val dateRangeText = if (hasDateRange) {
        val format = SimpleDateFormat("MMM d", Locale.getDefault())
        "${format.format(Date(startDateMillis!!))} - ${format.format(Date(endDateMillis!!))}"
    } else {
        "Date Range"
    }

    FilterChip(
        selected = hasDateRange,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = dateRangeText,
                    fontSize = 14.sp,
                    fontWeight = if (hasDateRange) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        },
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFE3F2FD),
            selectedLabelColor = Color(0xFF2196F3),
            containerColor = Color.White,
            labelColor = Color(0xFF6B7280)
        ),
        border = if (hasDateRange) {
            BorderStroke(1.dp, Color(0xFF2196F3))
        } else {
            BorderStroke(1.dp, Color(0xFFE5E7EB))
        },
        trailingIcon = if (hasDateRange) {
            {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear date filter",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF2196F3)
                    )
                }
            }
        } else null
    )
}
