package com.example.maidy.feature.maid_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

@Composable
fun BottomBookingBar(
    pricePerHour: Double,
    isAvailable: Boolean = true,
    onBookNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaidProfileBottomBarBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Price",
                fontSize = 13.sp,
                color = MaidProfilePriceLabel
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "From $${pricePerHour.toInt()}/hour",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaidProfilePriceValue
            )
        }

        Button(
            onClick = onBookNowClick,
            enabled = isAvailable,
            modifier = Modifier
                .height(50.dp)
                .widthIn(min = 160.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAvailable) {
                    MaidProfileBookButton
                } else {
                    Color(0xFFE0E0E0) // Light gray for disabled
                },
                contentColor = if (isAvailable) {
                    MaidProfileBookButtonText
                } else {
                    Color(0xFF9E9E9E) // Gray text for disabled
                },
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color(0xFF9E9E9E)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isAvailable) "Book This Maid" else "Not Available",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBookingBarPreview() {
    MaidyTheme {
        Column {
            Text("Available Maid", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            BottomBookingBar(
                pricePerHour = 25.0,
                isAvailable = true,
                onBookNowClick = {}
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Not Available Maid", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            BottomBookingBar(
                pricePerHour = 25.0,
                isAvailable = false,
                onBookNowClick = {}
            )
        }
    }
}



