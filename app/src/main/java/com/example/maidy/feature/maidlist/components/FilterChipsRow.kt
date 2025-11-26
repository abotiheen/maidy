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
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Specialty tag filter chips
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
        
        // Preview with empty filters
        FilterChipsRow(
            filters = emptyList(),
            onFilterClick = {}
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Filter Chips Row - With Maid Classes",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )

        // Preview with maid class filters
        FilterChipsRow(
            filters = listOf(
                FilterOption("1", "Gold", isSelected = false),
                FilterOption("2", "Silver", isSelected = true),
                FilterOption("3", "Bronze", isSelected = false),
            ),
            onFilterClick = {}
        )
    }
}

