package com.example.maidy.feature.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.core.model.BookingStatus
import com.example.maidy.ui.theme.*

@Composable
fun StatusActionButton(
    currentStatus: BookingStatus,
    maidName: String,
    onCancelOrder: () -> Unit,
    onContactMaid: () -> Unit,
    onSOSClicked: () -> Unit,
    onRateMaid: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (currentStatus) {
        BookingStatus.PENDING, BookingStatus.CONFIRMED -> {
            ActionButton(
                text = "Cancel Booking",
                painter = painterResource(R.drawable.cancel),
                backgroundColor = BookingStatusCancelButton,
                textColor = BookingStatusCancelButtonText,
                onClick = onCancelOrder,
                modifier = modifier
            )
        }
        BookingStatus.ON_THE_WAY -> {
            ActionButton(
                text = if (maidName.isNotBlank()) "Contact $maidName" else "Contact Maid",
                painter = painterResource(R.drawable.phone),
                backgroundColor = BookingStatusContactButton,
                textColor = BookingStatusContactButtonText,
                onClick = onContactMaid,
                modifier = modifier
            )
        }
        BookingStatus.IN_PROGRESS -> {
            ActionButton(
                text = "SOS Emergency",
                painter = painterResource(R.drawable.emergency),
                backgroundColor = BookingStatusSOSButton,
                textColor = BookingStatusSOSButtonText,
                onClick = onSOSClicked,
                modifier = modifier
            )
        }
        BookingStatus.COMPLETED -> {
            ActionButton(
                text = "Rate the Maid",
                painter = painterResource(R.drawable.rate_star),
                backgroundColor = BookingStatusRateButton,
                textColor = BookingStatusRateButtonText,
                onClick = onRateMaid,
                modifier = modifier
            )
        }
        BookingStatus.CANCELLED -> { /* No action buttons for cancelled bookings */ }
    }
}

@Composable
private fun ActionButton(
    text: String,
    painter: Painter,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        )
    ) {
        Icon(
            painter = painter,
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatusActionButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BookingStatusBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Confirmed Status", fontWeight = FontWeight.Bold)
        StatusActionButton(
            currentStatus = BookingStatus.CONFIRMED,
            maidName = "Jane Doe",
            onCancelOrder = {},
            onContactMaid = {},
            onSOSClicked = {},
            onRateMaid = {}
        )
        
        Text("On the Way Status", fontWeight = FontWeight.Bold)
        StatusActionButton(
            currentStatus = BookingStatus.ON_THE_WAY,
            maidName = "Jane Doe",
            onCancelOrder = {},
            onContactMaid = {},
            onSOSClicked = {},
            onRateMaid = {}
        )
        
        Text("In Progress Status", fontWeight = FontWeight.Bold)
        StatusActionButton(
            currentStatus = BookingStatus.IN_PROGRESS,
            maidName = "Jane Doe",
            onCancelOrder = {},
            onContactMaid = {},
            onSOSClicked = {},
            onRateMaid = {}
        )
        
        Text("Completed Status", fontWeight = FontWeight.Bold)
        StatusActionButton(
            currentStatus = BookingStatus.COMPLETED,
            maidName = "Jane Doe",
            onCancelOrder = {},
            onContactMaid = {},
            onSOSClicked = {},
            onRateMaid = {}
        )
    }
}

