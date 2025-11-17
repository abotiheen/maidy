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
    val backgroundColor = if (isSelected) {
        MaidListFilterChipSelectedBackground
    } else {
        MaidListFilterChipUnselectedBackground
    }
    
    val textColor = if (isSelected) {
        MaidListFilterChipSelectedText
    } else {
        MaidListFilterChipUnselectedText
    }
    
    val borderColor = if (isSelected) {
        MaidListFilterChipSelectedBorder
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
        Text("Filter Chip - Unselected", fontWeight = FontWeight.Bold)
        FilterChip(
            label = "Pet Friendly",
            isSelected = false,
            onClick = {}
        )
        
        Text("Filter Chip - Selected", fontWeight = FontWeight.Bold)
        FilterChip(
            label = "Pet Friendly",
            isSelected = true,
            onClick = {}
        )
        
        Text("Filter Chip with Icon - Unselected", fontWeight = FontWeight.Bold)
        FilterChip(
            label = "Filters",
            isSelected = false,
            onClick = {},
            showIcon = true
        )
        
        Text("Filter Chip with Icon - Selected", fontWeight = FontWeight.Bold)
        FilterChip(
            label = "Filters",
            isSelected = true,
            onClick = {},
            showIcon = true
        )
    }
}

