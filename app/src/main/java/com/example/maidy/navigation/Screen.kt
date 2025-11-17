package com.example.maidy.navigation

/**
 * Sealed class representing all navigation destinations in the app.
 * Each destination defines its route and any required arguments.
 */
sealed class Screen(val route: String) {
    
    /**
     * Authentication screen (includes login, register, and OTP verification)
     */
    data object Auth : Screen("auth")
    
    /**
     * Home screen - main dashboard
     */
    data object Home : Screen("home")
    
    /**
     * Maid list screen - browse available maids
     */
    data object MaidList : Screen("maid_list")
    
    /**
     * Maid profile screen - view detailed profile of a specific maid
     * Route includes maidId parameter: maid_profile/{maidId}
     */
    data object MaidProfile : Screen("maid_profile/{maidId}") {
        fun createRoute(maidId: String): String {
            return "maid_profile/$maidId"
        }
    }
    
    /**
     * Booking status screen - track current booking status
     * Route includes bookingId parameter: booking_status/{bookingId}
     */
    data object BookingStatus : Screen("booking_status/{bookingId}") {
        fun createRoute(bookingId: String): String {
            return "booking_status/$bookingId"
        }
    }
    
    /**
     * Emergency/SOS screen - emergency help button
     */
    data object Emergency : Screen("emergency")
    
    /**
     * Notifications screen - view user notifications
     * (Screen not yet implemented, route reserved)
     */
    data object Notifications : Screen("notifications")
    
    /**
     * Rating screen - rate a maid after service completion
     * Route includes bookingId parameter: rating/{bookingId}
     * (Screen not yet implemented, route reserved)
     */
    data object Rating : Screen("rating/{bookingId}") {
        fun createRoute(bookingId: String): String {
            return "rating/$bookingId"
        }
    }
    
    /**
     * Settings screen - user preferences and app settings
     * (Screen not yet implemented, route reserved)
     */
    data object Settings : Screen("settings")
}

