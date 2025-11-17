package com.example.maidy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.maidy.feature.auth.AuthScreen
import com.example.maidy.feature.booking.BookingStatusScreen
import com.example.maidy.feature.home.HomeScreen
import com.example.maidy.feature.maid_details.MaidProfileScreen
import com.example.maidy.feature.maidlist.MaidListScreen
import com.example.maidy.feature.sos.EmergencyScreen

/**
 * Main navigation host for the Maidy app.
 * Defines all navigation routes and their corresponding screens.
 * 
 * @param navController The navigation controller for managing navigation
 * @param modifier Optional modifier for the NavHost
 * @param startDestination The initial destination when the app starts
 */
@Composable
fun MaidyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Auth.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        
        // Auth Screen - Login/Register/OTP Verification
        composable(route = Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route)
                }
            )
        }
        
        // Home Screen - Main Dashboard
        composable(route = Screen.Home.route) {
            HomeScreen()
        }
        
        // Maid List Screen - Browse Available Maids
        composable(route = Screen.MaidList.route) {
            MaidListScreen()
        }
        
        // Maid Profile Screen - View Detailed Maid Profile
        composable(
            route = Screen.MaidProfile.route,
            arguments = listOf(
                navArgument("maidId") {
                    type = NavType.StringType
                }
            )
        ) {
            MaidProfileScreen()
        }
        
        // Booking Status Screen - Track Current Booking
        composable(
            route = Screen.BookingStatus.route,
            arguments = listOf(
                navArgument("bookingId") {
                    type = NavType.StringType
                }
            )
        ) {
            BookingStatusScreen()
        }
        
        // Emergency Screen - SOS/Emergency Help
        composable(route = Screen.Emergency.route) {
            EmergencyScreen()
        }
        
        // Notifications Screen - View User Notifications
        // TODO: Implement NotificationsScreen
        composable(route = Screen.Notifications.route) {
            // Placeholder for future implementation
            PlaceholderScreen(screenName = "Notifications")
        }
        
        // Rating Screen - Rate Maid After Service
        // TODO: Implement RatingScreen
        composable(
            route = Screen.Rating.route,
            arguments = listOf(
                navArgument("bookingId") {
                    type = NavType.StringType
                }
            )
        ) {
            // Placeholder for future implementation
            PlaceholderScreen(screenName = "Rating")
        }
        
        // Settings Screen - App Settings and Preferences
        // TODO: Implement SettingsScreen
        composable(route = Screen.Settings.route) {
            // Placeholder for future implementation
            PlaceholderScreen(screenName = "Settings")
        }
    }
}

