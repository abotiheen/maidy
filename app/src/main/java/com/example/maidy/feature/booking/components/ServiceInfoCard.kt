package com.example.maidy.feature.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.booking.BookingServiceDetails
import com.example.maidy.ui.theme.*

@Composable
fun ServiceInfoCard(
    bookingDetails: BookingServiceDetails,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BookingStatusCardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Service Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Service",
                    fontSize = 14.sp,
                    color = BookingStatusServiceLabel,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = bookingDetails.service,
                    fontSize = 16.sp,
                    color = BookingStatusServiceValue,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Date & Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Date & Time",
                    fontSize = 14.sp,
                    color = BookingStatusServiceLabel,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "${bookingDetails.date} | ${bookingDetails.time}",
                    fontSize = 16.sp,
                    color = BookingStatusDateValue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceInfoCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BookingStatusBackground)
            .padding(16.dp)
    ) {
        ServiceInfoCard(
            bookingDetails = BookingServiceDetails(
                service = "Standard Cleaning",
                date = "Nov 18, 2023",
                time = "10:00 AM"
            )
        )
    }
}



