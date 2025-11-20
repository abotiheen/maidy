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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

@Composable
fun BottomBookingBar(
    pricePerHour: Int,
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
                text = "From $$pricePerHour/hour",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaidProfilePriceValue
            )
        }

        Button(
            onClick = onBookNowClick,
            modifier = Modifier
                .height(50.dp)
                .widthIn(min = 160.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaidProfileBookButton,
                contentColor = MaidProfileBookButtonText
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Book This Maid",
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
        BottomBookingBar(
            pricePerHour = 25,
            onBookNowClick = {}
        )
    }
}


