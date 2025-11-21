package com.example.maidy.feature.adjust_recurring.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.ui.theme.*

@Composable
fun AdjustRecurringTimeSelector(
    selectedTime: String,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "What time?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AdjustRecurringSectionTitle,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdjustRecurringTimeCardBg, RoundedCornerShape(12.dp))
                .border(1.dp, AdjustRecurringTimeCardBorder, RoundedCornerShape(12.dp))
                .clickable(
                    onClick = onTimeClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedTime,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AdjustRecurringTimeText
            )

            Icon(
                painter = painterResource(R.drawable.time),
                contentDescription = "Select time",
                tint = BookingDetailsDateIcon,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdjustRecurringTimeSelectorPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdjustRecurringScreenBackground)
                .padding(16.dp)
        ) {
            AdjustRecurringTimeSelector(
                selectedTime = "10:00 AM",
                onTimeClick = {}
            )
        }
    }
}
