package com.example.maidy.feature.maidlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.maidlist.ServiceTag
import com.example.maidy.ui.theme.*

@Composable
fun ServiceChip(
    serviceTag: ServiceTag,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = getServiceChipColors(serviceTag)
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = textColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = serviceTag.displayName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun getServiceChipColors(serviceTag: ServiceTag): Pair<Color, Color> {
    return when (serviceTag) {
        ServiceTag.DEEP_CLEANING -> Pair(
            MaidListServiceChipDeepCleaningBg,
            MaidListServiceChipDeepCleaningText
        )
        ServiceTag.ECO_FRIENDLY -> Pair(
            MaidListServiceChipEcoFriendlyBg,
            MaidListServiceChipEcoFriendlyText
        )
        ServiceTag.PET_FRIENDLY -> Pair(
            MaidListServiceChipPetFriendlyBg,
            MaidListServiceChipPetFriendlyText
        )
        ServiceTag.MOVE_IN_OUT -> Pair(
            MaidListServiceChipMoveInOutBg,
            MaidListServiceChipMoveInOutText
        )
        ServiceTag.IRONING -> Pair(
            MaidListServiceChipDeepCleaningBg,
            MaidListServiceChipDeepCleaningText
        )
        ServiceTag.LAUNDRY -> Pair(
            MaidListServiceChipEcoFriendlyBg,
            MaidListServiceChipEcoFriendlyText
        )
        ServiceTag.WINDOW_CLEANING -> Pair(
            MaidListServiceChipPetFriendlyBg,
            MaidListServiceChipPetFriendlyText
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceChipPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaidListBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Service Chips", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        
        // Show all service tags
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ServiceChip(serviceTag = ServiceTag.DEEP_CLEANING)
            ServiceChip(serviceTag = ServiceTag.ECO_FRIENDLY)
            ServiceChip(serviceTag = ServiceTag.PET_FRIENDLY)
            ServiceChip(serviceTag = ServiceTag.MOVE_IN_OUT)
            ServiceChip(serviceTag = ServiceTag.IRONING)
            ServiceChip(serviceTag = ServiceTag.LAUNDRY)
            ServiceChip(serviceTag = ServiceTag.WINDOW_CLEANING)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Multiple Chips in a Row", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        
        // Show multiple chips together
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ServiceChip(serviceTag = ServiceTag.PET_FRIENDLY)
            ServiceChip(serviceTag = ServiceTag.ECO_FRIENDLY)
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ServiceChip(serviceTag = ServiceTag.DEEP_CLEANING)
            ServiceChip(serviceTag = ServiceTag.MOVE_IN_OUT)
            ServiceChip(serviceTag = ServiceTag.LAUNDRY)
        }
    }
}

