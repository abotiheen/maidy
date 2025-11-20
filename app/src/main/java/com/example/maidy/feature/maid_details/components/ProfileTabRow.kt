package com.example.maidy.feature.maid_details.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.maid_details.ProfileTab
import com.example.maidy.ui.theme.*

@Composable
fun ProfileTabRow(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        ProfileTab.ABOUT to "About",
        ProfileTab.SERVICES to "Services",
        ProfileTab.REVIEWS to "Reviews"
    )

    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaidProfileContentBackground,
        contentColor = MaidProfileTabSelected,
        indicator = { tabPositions ->
            if (selectedTab.ordinal < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    height = 3.dp,
                    color = MaidProfileTabIndicator
                )
            }
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, (tab, title) ->
            val selected = selectedTab == tab
            Tab(
                selected = selected,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) MaidProfileTabSelected else MaidProfileTabUnselected,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileTabRowPreview() {
    MaidyTheme {
        Column(modifier = Modifier.background(MaidProfileBackground)) {
            ProfileTabRow(
                selectedTab = ProfileTab.ABOUT,
                onTabSelected = {}
            )
        }
    }
}


