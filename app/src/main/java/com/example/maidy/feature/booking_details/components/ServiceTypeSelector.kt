package com.example.maidy.feature.booking_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.feature.booking_details.ServiceType
import com.example.maidy.ui.theme.*

/**
 * Component for selecting service type
 * Displays service options as cards with radio button selection
 */
@Composable
fun ServiceTypeSelector(
    selectedServiceType: ServiceType,
    onServiceTypeSelected: (ServiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Title
        Text(
            text = "Select Service Type",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BookingDetailsSectionTitle
        )

        // Service Type Cards
        ServiceType.entries.forEach { serviceType ->
            ServiceTypeCard(
                serviceType = serviceType,
                isSelected = selectedServiceType == serviceType,
                onClick = { onServiceTypeSelected(serviceType) }
            )
        }
    }
}

@Composable
private fun ServiceTypeCard(
    serviceType: ServiceType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) {
        BookingDetailsServiceCardSelectedBorder
    } else {
        BookingDetailsServiceCardUnselectedBorder
    }

    val titleColor = if (isSelected) {
        BookingDetailsServiceCardSelectedBorder // Use blue color for selected text
    } else {
        BookingDetailsServiceCardUnselectedTitle
    }

    val subtitleColor = if (isSelected) {
        BookingDetailsServiceCardSelectedBorder.copy(alpha = 0.7f)
    } else {
        BookingDetailsServiceCardUnselectedSubtitle
    }

    val icon = when (serviceType) {
        ServiceType.DEEP_CLEANING -> painterResource(R.drawable.deep_cleaning)

        ServiceType.STANDARD_CLEANING -> painterResource(R.drawable.standard_cleaning)

        ServiceType.MOVE_OUT_CLEAN -> painterResource(R.drawable.move_out_cleaning)
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Service Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = serviceType.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            Text(
                text = serviceType.description,
                fontSize = 14.sp,
                color = subtitleColor
            )
        }

        // Radio Button
        Icon(
            painter = icon,
            contentDescription = if (isSelected) "Selected" else "Not selected",
            tint = if (isSelected) titleColor else subtitleColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(name = "Service Type Selector", showBackground = true)
@Composable
private fun ServiceTypeSelectorPreview() {
    MaidyTheme {
        Column(
            modifier = Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            ServiceTypeSelector(
                selectedServiceType = ServiceType.DEEP_CLEANING,
                onServiceTypeSelected = {}
            )
        }
    }
}

@Preview(name = "Selected Deep Cleaning", showBackground = true)
@Composable
private fun ServiceTypeCardSelectedPreview() {
    MaidyTheme {
        ServiceTypeCard(
            serviceType = ServiceType.DEEP_CLEANING,
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(name = "Unselected Standard Cleaning", showBackground = true)
@Composable
private fun ServiceTypeCardUnselectedPreview() {
    MaidyTheme {
        ServiceTypeCard(
            serviceType = ServiceType.STANDARD_CLEANING,
            isSelected = false,
            onClick = {}
        )
    }
}

