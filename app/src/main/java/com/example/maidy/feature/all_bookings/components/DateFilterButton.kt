package com.example.maidy.feature.all_bookings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DateFilterButton(
    startDateMillis: Long?,
    endDateMillis: Long?,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasDateFilter = startDateMillis != null && endDateMillis != null

    val backgroundColor = if (hasDateFilter) {
        MaidListFilterChipSelectedBackground
    } else {
        MaidListFilterChipUnselectedBackground
    }

    val textColor = if (hasDateFilter) {
        MaidListFilterChipSelectedText
    } else {
        MaidListFilterChipUnselectedText
    }

    val borderColor = if (hasDateFilter) {
        MaidListFilterChipSelectedBorder
    } else {
        MaidListFilterChipUnselectedBorder
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Date Range",
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = if (hasDateFilter) {
                formatDateRange(startDateMillis!!, endDateMillis!!)
            } else {
                "Date Range"
            },
            fontSize = 14.sp,
            fontWeight = if (hasDateFilter) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )

        if (hasDateFilter) {
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear date filter",
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun formatDateRange(startMillis: Long, endMillis: Long): String {
    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val startDate = dateFormat.format(Date(startMillis))
    val endDate = dateFormat.format(Date(endMillis))
    return "$startDate - $endDate"
}

@Preview(showBackground = true)
@Composable
fun DateFilterButtonPreview() {
    MaidyTheme {
        DateFilterButton(
            startDateMillis = null,
            endDateMillis = null,
            onClick = {},
            onClear = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DateFilterButtonSelectedPreview() {
    MaidyTheme {
        DateFilterButton(
            startDateMillis = System.currentTimeMillis(),
            endDateMillis = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000),
            onClick = {},
            onClear = {}
        )
    }
}
