package com.example.maidy.feature.adjust_recurring.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.core.model.RecurringType
import com.example.maidy.ui.theme.*

@Composable
fun AdjustRecurringFrequencySelector(
    selectedType: RecurringType,
    onTypeSelected: (RecurringType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "How often?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AdjustRecurringSectionTitle,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FrequencyOption(
                text = "Weekly",
                isSelected = selectedType == RecurringType.WEEKLY,
                onClick = { onTypeSelected(RecurringType.WEEKLY) },
                modifier = Modifier.weight(1f)
            )

            FrequencyOption(
                text = "Bi-weekly",
                isSelected = selectedType == RecurringType.BIWEEKLY,
                onClick = { onTypeSelected(RecurringType.BIWEEKLY) },
                modifier = Modifier.weight(1f)
            )

            FrequencyOption(
                text = "Monthly",
                isSelected = selectedType == RecurringType.MONTHLY,
                onClick = { onTypeSelected(RecurringType.MONTHLY) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FrequencyOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AdjustRecurringFrequencySelectedBg else AdjustRecurringFrequencyUnselectedBg,
        animationSpec = tween(300),
        label = "background"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) AdjustRecurringFrequencySelectedText else AdjustRecurringFrequencyUnselectedText,
        animationSpec = tween(300),
        label = "text"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AdjustRecurringFrequencySelectedBg else AdjustRecurringFrequencyBorder,
        animationSpec = tween(300),
        label = "border"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(56.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdjustRecurringFrequencySelectorPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdjustRecurringScreenBackground)
                .padding(16.dp)
        ) {
            AdjustRecurringFrequencySelector(
                selectedType = RecurringType.BIWEEKLY,
                onTypeSelected = {}
            )
        }
    }
}
