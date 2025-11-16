package com.example.maidy.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.ui.theme.HomeSearchBackground
import com.example.maidy.ui.theme.HomeSearchHint
import com.example.maidy.ui.theme.HomeSearchIcon
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun HomeSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .background(
                color = HomeSearchBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // TODO: Replace with search icon
            contentDescription = "Search",
            tint = HomeSearchIcon,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Search for maids, services...",
                    fontSize = 16.sp,
                    color = HomeSearchHint
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeSearchBarPreview() {
    MaidyTheme {
        HomeSearchBar(
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeSearchBarWithTextPreview() {
    MaidyTheme {
        HomeSearchBar(
            searchQuery = "Deep cleaning",
            onSearchQueryChange = {}
        )
    }
}

