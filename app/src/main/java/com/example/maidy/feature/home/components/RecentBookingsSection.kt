package com.example.maidy.feature.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.HomeSectionTitle
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun RecentBookingsSectionHeader(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Your Upcoming Bookings",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = HomeSectionTitle,
        modifier = modifier.padding(horizontal = 24.dp, vertical = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun RecentBookingsSectionHeaderPreview() {
    MaidyTheme {
        RecentBookingsSectionHeader()
    }
}

