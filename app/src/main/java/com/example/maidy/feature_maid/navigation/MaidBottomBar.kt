package com.example.maidy.feature_maid.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.maidy.R
import com.example.maidy.feature_maid.auth.MaidAppGreen

/**
 * Bottom navigation bar item data for Maid app
 */
sealed class MaidBottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    data object Home : MaidBottomNavItem(
        route = MaidScreen.Home.route,
        title = "Home",
        selectedIcon = R.drawable.maid_home_filled,
        unselectedIcon = R.drawable.maid_home_unfilled
    )

    data object Profile : MaidBottomNavItem(
        route = MaidScreen.Profile.route,
        title = "Profile",
        selectedIcon = R.drawable.maid_profile_filled,
        unselectedIcon = R.drawable.maid_prorfile_unfiled
    )
}

/**
 * Bottom Navigation Bar for the Maid app - Green themed
 * Displayed only on Home and Profile screens
 *
 * @param currentRoute The current navigation route
 * @param onNavigateToRoute Callback when a bottom nav item is clicked
 */
@Composable
fun MaidBottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String, String) -> Unit
) {
    val items = listOf(
        MaidBottomNavItem.Home,
        MaidBottomNavItem.Profile
    )

    Column {
        // Top divider for separation
        HorizontalDivider(
            thickness = 0.5.dp,
            color = Color(0xFFE0F2E1)  // Light green divider
        )

        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
                val selected = currentRoute?.startsWith(item.route) == true

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(if (selected) item.selectedIcon else item.unselectedIcon),
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(text = item.title)
                    },
                    selected = selected,
                    onClick = {
                        if (!selected && currentRoute != null) {
                            onNavigateToRoute(item.route, currentRoute)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaidAppGreen,      // Green for selected icon
                        selectedTextColor = MaidAppGreen,       // Green for selected text
                        indicatorColor = Color(0xFFE8F5E9),     // Very light green indicator
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF)
                    )
                )
            }
        }
    }
}
