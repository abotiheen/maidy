package com.example.maidy.feature_maid.navigation

/**
 * Sealed class representing all navigation destinations in the Maid app.
 * Each destination defines its route, screen title, and any required arguments.
 */
sealed class MaidScreen(
    val route: String,
    val title: String,
    val showTopBar: Boolean = true,
    val showBottomBar: Boolean = false
) {

    /**
     * Authentication screen (includes login, register, and OTP verification)
     */
    data object Auth :
        MaidScreen("maid_auth", "Maidy for Maids", showTopBar = false, showBottomBar = false)

    /**
     * Home screen - main dashboard for maids
     */
    data object Home : MaidScreen("maid_home", "Home", showTopBar = false, showBottomBar = true)

    /**
     * Profile/Settings screen - maid profile and settings
     */
    data object Profile :
        MaidScreen("maid_profile", "Profile", showTopBar = false, showBottomBar = true)

    /**
     * Terms and Conditions screen - display service provider terms
     */
    data object TermsAndConditions : MaidScreen("maid_terms_and_conditions", "Terms and Conditions")

    /**
     * Edit Profile screen - edit maid profile details
     */
    data object EditProfile : MaidScreen("maid_edit_profile", "Edit Profile")

    /**
     * Booking Details screen - view and manage specific booking
     */
    companion object {
        fun bookingDetails(bookingId: String) = "maid_booking_details/$bookingId"
        const val BOOKING_DETAILS_ROUTE = "maid_booking_details/{bookingId}"
    }
}

/**
 * Helper function to get MaidScreen object from route string
 */
fun getMaidScreenFromRoute(route: String?): MaidScreen {
    return when {
        route == null -> MaidScreen.Auth
        route.startsWith("maid_auth") -> MaidScreen.Auth
        route.startsWith("maid_home") -> MaidScreen.Home
        route.startsWith("maid_edit_profile") -> MaidScreen.EditProfile
        route.startsWith("maid_profile") -> MaidScreen.Profile
        route.startsWith("maid_booking_details") -> MaidScreen.EditProfile // Placeholder
        route.startsWith("maid_terms_and_conditions") -> MaidScreen.TermsAndConditions
        else -> MaidScreen.Auth
    }
}
