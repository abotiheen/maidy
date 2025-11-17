package com.example.maidy.feature.maidlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.maidlist.FilterOption
import com.example.maidy.ui.theme.*

@Composable
fun FilterChipsRow(
    filters: List<FilterOption>,
    onFilterClick: (String) -> Unit,
    onLocationClick: () -> Unit,
    onDateTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Filters chip with icon
        item {
            FilterChip(
                label = "Filters",
                isSelected = false,
                onClick = { /* TODO: Open filters bottom sheet */ },
                showIcon = true
            )
        }
        
        // Location selector chip
        item {
            FilterChip(
                label = "Location",
                isSelected = false,
                onClick = onLocationClick
            )
        }
        
        // Date & Time selector chip
        item {
            FilterChip(
                label = "Date & Time",
                isSelected = false,
                onClick = onDateTimeClick
            )
        }
        
        // Dynamic filter chips from backend
        items(
            items = filters,
            key = { filter -> filter.id }
        ) { filter ->
            FilterChip(
                label = filter.label,
                isSelected = filter.isSelected,
                onClick = { onFilterClick(filter.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipsRowPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaidListBackground)
    ) {
        Text(
            "Filter Chips Row - Dynamic Filters",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
        
        // Preview with empty filters (will be populated from backend)
        FilterChipsRow(
            filters = emptyList(),
            onFilterClick = {},
            onLocationClick = {},
            onDateTimeClick = {}
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Filter Chips Row - With Sample Filters",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
        
        // Preview with sample filters to show how it looks
        FilterChipsRow(
            filters = listOf(
                FilterOption("1", "Pet Friendly", isSelected = false),
                FilterOption("2", "Available Tomorrow", isSelected = true),
                FilterOption("3", "Eco-Friendly", isSelected = false),
            ),
            onFilterClick = {},
            onLocationClick = {},
            onDateTimeClick = {}
        )
    }
}

