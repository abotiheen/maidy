package com.example.maidy.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.maidy.ui.theme.MaidyTextPrimary

/**
 * Bottom navigation bar item data
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomNavItem(
        route = Screen.Home.route,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    
    data object Profile : BottomNavItem(
        route = Screen.Settings.route,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

/**
 * Bottom Navigation Bar for the Maidy app
 * Displayed only on Home and Profile screens
 * 
 * @param currentRoute The current navigation route
 * @param onNavigateToRoute Callback when a bottom nav item is clicked
 */
@Composable
fun MaidyBottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String, String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile
    )
    
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute?.startsWith(item.route) == true
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
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
                    selectedIconColor = MaidyTextPrimary,
                    selectedTextColor = MaidyTextPrimary,
                    indicatorColor = Color(0xFFF5F5F5),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

