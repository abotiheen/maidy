package com.example.maidy.feature.booking_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.core.model.RecurringType
import com.example.maidy.ui.theme.*

/**
 * Component for selecting recurring type (Weekly, Biweekly, Monthly)
 * Displays cards with icons and descriptions
 */
@Composable
fun RecurringTypeSelector(
    selectedType: RecurringType,
    onTypeSelected: (RecurringType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Title
        Text(
            text = "How often would you like us to clean?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BookingDetailsSectionTitle
        )
        
        // Recurring Type Cards
        RecurringTypeCard(
            type = RecurringType.WEEKLY,
            isSelected = selectedType == RecurringType.WEEKLY,
            onClick = { onTypeSelected(RecurringType.WEEKLY) },
            icon = R.drawable.calendar,
            title = "Weekly",
            subtitle = "Most popular choice"
        )
        
        RecurringTypeCard(
            type = RecurringType.BIWEEKLY,
            isSelected = selectedType == RecurringType.BIWEEKLY,
            onClick = { onTypeSelected(RecurringType.BIWEEKLY) },
            icon = R.drawable.calendar,
            title = "Bi-weekly",
            subtitle = "Every 2 weeks"
        )
        
        RecurringTypeCard(
            type = RecurringType.MONTHLY,
            isSelected = selectedType == RecurringType.MONTHLY,
            onClick = { onTypeSelected(RecurringType.MONTHLY) },
            icon = R.drawable.calendar,
            title = "Monthly",
            subtitle = "Every 4 weeks"
        )
    }
}

@Composable
private fun RecurringTypeCard(
    type: RecurringType,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: Int,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) {
        BookingDetailsServiceCardSelectedBorder
    } else {
        BookingDetailsServiceCardUnselectedBorder
    }
    
    val titleColor = if (isSelected) {
        BookingDetailsServiceCardSelectedBorder
    } else {
        BookingDetailsServiceCardUnselectedTitle
    }
    
    val subtitleColor = if (isSelected) {
        BookingDetailsServiceCardSelectedBorder.copy(alpha = 0.7f)
    } else {
        BookingDetailsServiceCardUnselectedSubtitle
    }
    
    val iconTint = if (isSelected) {
        BookingDetailsServiceCardSelectedBorder
    } else {
        BookingDetailsServiceCardUnselectedSubtitle
    }
    
    val cardModifier = if (isSelected) {
        modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = BookingDetailsServiceCardSelectedBorder.copy(alpha = 0.5f),
                ambientColor = BookingDetailsServiceCardSelectedBorder.copy(alpha = 0.3f)
            )
    } else {
        modifier.fillMaxWidth()
    }
    
    Row(
        modifier = cardModifier
            .clip(RoundedCornerShape(12.dp))
            .background(BookingDetailsServiceCardUnselectedBackground)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon in Circle
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        BookingDetailsServiceCardSelectedBorder.copy(alpha = 0.1f)
                    } else {
                        BookingDetailsServiceCardUnselectedBorder.copy(alpha = 0.1f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
        }
        
        // Text Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
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
        
        // Checkmark for selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(BookingDetailsServiceCardSelectedBorder),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = BookingDetailsServiceCardUnselectedBackground,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(name = "Weekly Selected", showBackground = true)
@Composable
private fun RecurringTypeSelectorWeeklyPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            RecurringTypeSelector(
                selectedType = RecurringType.WEEKLY,
                onTypeSelected = {}
            )
        }
    }
}

@Preview(name = "Biweekly Selected", showBackground = true)
@Composable
private fun RecurringTypeSelectorBiweeklyPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            RecurringTypeSelector(
                selectedType = RecurringType.BIWEEKLY,
                onTypeSelected = {}
            )
        }
    }
}

@Preview(name = "Monthly Selected", showBackground = true)
@Composable
private fun RecurringTypeSelectorMonthlyPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            RecurringTypeSelector(
                selectedType = RecurringType.MONTHLY,
                onTypeSelected = {}
            )
        }
    }
}

