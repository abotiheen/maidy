package com.example.maidy.navigation

/**
 * Sealed class representing all navigation destinations in the app.
 * Each destination defines its route, screen title, and any required arguments.
 */
sealed class Screen(
    val route: String, 
    val title: String, 
    val showTopBar: Boolean = true,
    val showBottomBar: Boolean = false
) {
    
    /**
     * Authentication screen (includes login, register, and OTP verification)
     */
    data object Auth : Screen("auth", "Maidy", showTopBar = false, showBottomBar = false)
    
    /**
     * Home screen - main dashboard
     */
    data object Home : Screen("home", "Home", showTopBar = false, showBottomBar = true)
    
    /**
     * Search screen - search for maids
     */
    data object Search : Screen("search", "Search", showTopBar = false)
    
    /**
     * All Bookings screen - view all user bookings with filters
     */
    data object AllBookings : Screen("all_bookings", "My Bookings")
    
    /**
     * Maid list screen - browse available maids
     */
    data object MaidList : Screen("maid_list", "Available Maids")
    
    /**
     * Maid profile screen - view detailed profile of a specific maid
     * Route includes maidId parameter: maid_profile/{maidId}
     */
    data object MaidProfile : Screen("maid_profile/{maidId}", "Maid Profile") {
        fun createRoute(maidId: String): String {
            return "maid_profile/$maidId"
        }
    }
    
    /**
     * Booking details screen - book a service with a specific maid
     * Route includes maidId parameter: booking_details/{maidId}
     */
    data object BookingDetails : Screen("booking_details/{maidId}", "Book Service") {
        fun createRoute(maidId: String): String {
            return "booking_details/$maidId"
        }
    }
    
    /**
     * Booking status screen - track current booking status
     * Route includes bookingId parameter: booking_status/{bookingId}
     */
    data object BookingStatus : Screen("booking_status/{bookingId}", "Booking Status") {
        fun createRoute(bookingId: String): String {
            return "booking_status/$bookingId"
        }
    }
    
    /**
     * Emergency/SOS screen - emergency help button
     */
    data object Emergency : Screen("emergency", "Emergency",
        showTopBar = false,
        showBottomBar = false)
    
    /**
     * Notifications screen - view user notifications
     * (Screen not yet implemented, route reserved)
     */
    data object Notifications : Screen("notifications", "Notifications")
    
    /**
     * Rating screen - rate a maid after service completion
     * Route includes bookingId parameter: rating/{bookingId}
     */
    data object Rating : Screen(
        "rating/{bookingId}",
        "Rate Service",
        showTopBar = true,
        showBottomBar = false
    ) {
        fun createRoute(bookingId: String): String {
            return "rating/$bookingId"
        }
    }
    
    /**
     * Settings screen - user preferences and app settings
     */
    data object Settings : Screen("settings", "Profile", showTopBar = false, showBottomBar = true)
    
    /**
     * Chat screen - AI assistant powered by Gemini
     */
    data object Chat : Screen("chat", "AI Assistant")
    
    /**
     * Edit Profile screen - edit user profile information
     */
    data object EditProfile : Screen("edit_profile", "Edit Profile")
    
    /**
     * Terms and Conditions screen - display app terms and conditions
     */
    data object TermsAndConditions : Screen("terms_and_conditions", "Terms and Conditions")
    
    /**
     * Admin Add Maid screen - for populating Firebase with maid data
     */
    data object AdminAddMaid : Screen("admin_add_maid", "Add Maid")

    /**
     * Adjust Recurring Booking screen - manage recurring booking settings
     * Route includes bookingId parameter: adjust_recurring/{bookingId}
     */
    data object AdjustRecurring : Screen("adjust_recurring/{bookingId}", "Edit Recurring Booking") {
        fun createRoute(bookingId: String): String {
            return "adjust_recurring/$bookingId"
        }
    }
}

/**
 * Helper function to get Screen object from route string
 */
fun getScreenFromRoute(route: String?): Screen {
    return when {
        route == null -> Screen.Auth
        route.startsWith("auth") -> Screen.Auth
        route.startsWith("home") -> Screen.Home
        route.startsWith("search") -> Screen.Search
        route.startsWith("all_bookings") -> Screen.AllBookings
        route.startsWith("maid_list") -> Screen.MaidList
        route.startsWith("maid_profile") -> Screen.MaidProfile
        route.startsWith("booking_details") -> Screen.BookingDetails
        route.startsWith("booking_status") -> Screen.BookingStatus
        route.startsWith("adjust_recurring") -> Screen.AdjustRecurring
        route.startsWith("emergency") -> Screen.Emergency
        route.startsWith("notifications") -> Screen.Notifications
        route.startsWith("rating") -> Screen.Rating
        route.startsWith("settings") -> Screen.Settings
        route.startsWith("edit_profile") -> Screen.EditProfile
        route.startsWith("terms_and_conditions") -> Screen.TermsAndConditions
        route.startsWith("admin_add_maid") -> Screen.AdminAddMaid
        route.startsWith("chat") -> Screen.Chat
        else -> Screen.Auth
    }
}

