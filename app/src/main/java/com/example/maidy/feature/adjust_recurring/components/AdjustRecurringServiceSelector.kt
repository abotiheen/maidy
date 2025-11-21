package com.example.maidy.feature.adjust_recurring.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.core.model.BookingType
import com.example.maidy.ui.theme.*

@Composable
fun AdjustRecurringServiceSelector(
    selectedService: BookingType,
    onServiceSelected: (BookingType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Which service do you need?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AdjustRecurringSectionTitle,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ServiceOption(
                icon = R.drawable.deep_cleaning,
                title = "Deep Cleaning",
                subtitle = "Thorough and detailed",
                isSelected = selectedService == BookingType.DEEP_CLEANING,
                onClick = { onServiceSelected(BookingType.DEEP_CLEANING) }
            )

            ServiceOption(
                icon = R.drawable.standard_cleaning,
                title = "Standard Cleaning",
                subtitle = "Regular maintenance",
                isSelected = selectedService == BookingType.STANDARD_CLEANING,
                onClick = { onServiceSelected(BookingType.STANDARD_CLEANING) }
            )

            ServiceOption(
                icon = R.drawable.move_out_cleaning,
                title = "Move-out Clean",
                subtitle = "For empty homes",
                isSelected = selectedService == BookingType.MOVE_OUT_CLEAN,
                onClick = { onServiceSelected(BookingType.MOVE_OUT_CLEAN) }
            )
        }
    }
}

@Composable
private fun ServiceOption(
    icon: Int,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AdjustRecurringServiceSelectedBorder else AdjustRecurringServiceUnselectedBorder,
        animationSpec = tween(300),
        label = "border"
    )

    val titleColor by animateColorAsState(
        targetValue = if (isSelected) AdjustRecurringServiceSelectedBorder else AdjustRecurringServiceSelectedText,
        animationSpec = tween(300),
        label = "titleColor"
    )

    val subtitleColor by animateColorAsState(
        targetValue = if (isSelected) AdjustRecurringServiceSelectedBorder.copy(alpha = 0.7f) else AdjustRecurringServiceSelectedSubtext,
        animationSpec = tween(300),
        label = "subtitleColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AdjustRecurringServiceUnselectedBg, RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = subtitleColor
            )
        }

        Icon(
            painter = painterResource(icon),
            contentDescription = title,
            tint = if (isSelected) titleColor else subtitleColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdjustRecurringServiceSelectorPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdjustRecurringScreenBackground)
                .padding(16.dp)
        ) {
            AdjustRecurringServiceSelector(
                selectedService = BookingType.STANDARD_CLEANING,
                onServiceSelected = {}
            )
        }
    }
}
