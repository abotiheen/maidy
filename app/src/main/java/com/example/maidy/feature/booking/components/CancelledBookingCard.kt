package com.example.maidy.feature.booking.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.BookingStatusCancelButton
import com.example.maidy.ui.theme.BookingStatusCancelButtonText
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun CancelledBookingCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BookingStatusCancelButton
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Booking Cancelled",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BookingStatusCancelButtonText
            )
            Text(
                text = "This booking was cancelled and is no longer active.",
                fontSize = 14.sp,
                color = BookingStatusCancelButtonText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CancelledBookingCardPreview() {
    MaidyTheme {
        CancelledBookingCard()
    }
}
