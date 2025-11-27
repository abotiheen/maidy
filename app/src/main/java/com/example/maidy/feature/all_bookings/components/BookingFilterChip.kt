package com.example.maidy.feature.all_bookings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

@Composable
fun BookingFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaidListFilterChipSelectedBackground
    } else {
        MaidListFilterChipUnselectedBackground
    }

    val textColor = if (isSelected) {
        MaidListFilterChipSelectedText
    } else {
        MaidListFilterChipUnselectedText
    }

    val borderColor = if (isSelected) {
        MaidListFilterChipSelectedBorder
    } else {
        MaidListFilterChipUnselectedBorder
    }

    Text(
        text = label,
        fontSize = 14.sp,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        color = textColor,
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
    )
}

@Preview(showBackground = true)
@Composable
fun BookingFilterChipPreview() {
    MaidyTheme {
        BookingFilterChip(
            label = "Confirmed",
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookingFilterChipUnselectedPreview() {
    MaidyTheme {
        BookingFilterChip(
            label = "Completed",
            isSelected = false,
            onClick = {}
        )
    }
}
