package com.example.maidy.feature.maidlist.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
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
import com.example.maidy.ui.theme.*

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showIcon: Boolean = false
) {
    // Get specialty-specific colors if this is a specialty tag filter
    val (specialtyBg, specialtyText) = getFilterChipColors(label)
    
    val backgroundColor = if (isSelected) {
        specialtyBg
    } else {
        MaidListFilterChipUnselectedBackground
    }
    
    val textColor = if (isSelected) {
        specialtyText
    } else {
        MaidListFilterChipUnselectedText
    }
    
    val borderColor = if (isSelected) {
        specialtyText.copy(alpha = 0.3f)
    } else {
        MaidListFilterChipUnselectedBorder
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (showIcon) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Filter Icon",
                    tint = if (isSelected) textColor else MaidListFilterIconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

/**
 * Returns the background and text colors for filter chips based on maid class
 */
@Composable
private fun getFilterChipColors(label: String): Pair<Color, Color> {
    return when (label) {
        "Gold" -> Pair(
            Color(0xFFFFF9E6),  // Light gold/cream background
            Color(0xFFD4AF37)   // Rich gold text
        )

        "Silver" -> Pair(
            Color(0xFFF5F5F5),  // Light silver/gray background
            Color(0xFF9E9E9E)   // Silver gray text
        )

        "Bronze" -> Pair(
            Color(0xFFFFF3E0),  // Light bronze/peach background
            Color(0xFFCD7F32)   // Bronze text
        )
        else -> Pair(
            MaidListFilterChipSelectedBackground,
            MaidListFilterChipSelectedText
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaidListBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Maid Classes - Unselected", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(label = "Gold", isSelected = false, onClick = {})
            FilterChip(label = "Silver", isSelected = false, onClick = {})
            FilterChip(label = "Bronze", isSelected = false, onClick = {})
        }

        Text("Maid Classes - Selected", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(label = "Gold", isSelected = true, onClick = {})
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(label = "Silver", isSelected = true, onClick = {})
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(label = "Bronze", isSelected = true, onClick = {})
        }
        
        Text("Generic Filter Chip with Icon", fontWeight = FontWeight.Bold)
        FilterChip(
            label = "Filters",
            isSelected = false,
            onClick = {},
            showIcon = true
        )
    }
}

