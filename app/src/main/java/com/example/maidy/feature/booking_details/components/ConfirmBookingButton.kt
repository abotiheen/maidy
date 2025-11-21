package com.example.maidy.feature.booking_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

/**
 * Bottom confirmation button for booking
 * Shows loading state when processing
 */
@Composable
fun ConfirmBookingButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BookingDetailsTopBarBackground)
            .padding(16.dp)
    ) {
        Button(
            onClick = onClick,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BookingDetailsConfirmButton,
                contentColor = BookingDetailsConfirmButtonText,
                disabledContainerColor = BookingDetailsConfirmButton.copy(alpha = 0.5f),
                disabledContentColor = BookingDetailsConfirmButtonText.copy(alpha = 0.5f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = BookingDetailsConfirmButtonText,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Confirm Booking",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(name = "Normal State", showBackground = true)
@Composable
private fun ConfirmBookingButtonNormalPreview() {
    MaidyTheme {
        ConfirmBookingButton(
            onClick = {},
            isLoading = false
        )
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
private fun ConfirmBookingButtonLoadingPreview() {
    MaidyTheme {
        ConfirmBookingButton(
            onClick = {},
            isLoading = true
        )
    }
}

