package com.example.maidy.feature.adjust_recurring.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

@Composable
fun AdjustRecurringDaySelector(
    selectedDay: String,
    onDaySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf("S", "M", "T", "W", "T", "F", "S")
    val dayNames =
        listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Which day of the week?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AdjustRecurringSectionTitle,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEachIndexed { index, day ->
                DayButton(
                    letter = day,
                    dayName = dayNames[index],
                    isSelected = selectedDay == dayNames[index],
                    onClick = { onDaySelected(dayNames[index]) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DayButton(
    letter: String,
    dayName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AdjustRecurringDaySelectedBg else AdjustRecurringDayUnselectedBg,
        animationSpec = tween(300),
        label = "background"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) AdjustRecurringDaySelectedText else AdjustRecurringDayUnselectedText,
        animationSpec = tween(300),
        label = "text"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AdjustRecurringDaySelectedBg else AdjustRecurringDayBorder,
        animationSpec = tween(300),
        label = "border"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(backgroundColor, CircleShape)
            .border(1.5.dp, borderColor, CircleShape)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdjustRecurringDaySelectorPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdjustRecurringScreenBackground)
                .padding(16.dp)
        ) {
            AdjustRecurringDaySelector(
                selectedDay = "Tuesday",
                onDaySelected = {}
            )
        }
    }
}
