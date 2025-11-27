package com.example.maidy.feature_maid.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.maidy.R
import com.example.maidy.feature_maid.auth.MaidAppBackgroundLight
import com.example.maidy.feature_maid.auth.MaidAppTextPrimary
import com.example.maidy.feature_maid.all_bookings.MaidAllBookingsScreen
import com.example.maidy.feature_maid.auth.MaidAuthScreen
import com.example.maidy.feature_maid.booking_details.MaidBookingDetailsScreen
import com.example.maidy.feature_maid.edit_profile.MaidEditProfileScreen
import com.example.maidy.feature_maid.home.MaidHomeScreen
import com.example.maidy.feature_maid.profile.MaidProfileScreen
import com.example.maidy.feature_maid.terms.MaidTermsAndConditionsScreen

/**
 * Main navigation host for the Maid app.
 * Defines all navigation routes and their corresponding screens.
 *
 * @param navController The navigation controller for managing navigation
 * @param modifier Optional modifier for the NavHost
 * @param startDestination The initial destination when the app starts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaidNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = MaidScreen.Auth.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = getMaidScreenFromRoute(currentRoute)

    Scaffold(
        containerColor = MaidAppBackgroundLight,
        topBar = {
            if (currentScreen.showTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            text = currentScreen.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaidAppTextPrimary
                        )
                    },
                    navigationIcon = {
                        if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_back),
                                    contentDescription = "Back",
                                    tint = MaidAppTextPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen.showBottomBar) {
                MaidBottomBar(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route, currentRoute ->
                        navController.navigate(route) {
                            // Pop up to home screen and clear the back stack
                            // This ensures home and profile don't stack on each other
                            popUpTo(currentRoute) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Auth Screen - Login/Register/OTP Verification for Maids
            composable(route = MaidScreen.Auth.route) {
                MaidAuthScreen(
                    onAuthSuccess = {
                        navController.navigate(MaidScreen.Home.route) {
                            popUpTo(MaidScreen.Auth.route) { inclusive = true }
                        }
                    },
                    onNavigateToTerms = {
                        navController.navigate(MaidScreen.TermsAndConditions.route)
                    }
                )
            }

            // Home Screen - Main Dashboard for Maids
            composable(route = MaidScreen.Home.route) {
                MaidHomeScreen(
                    onBookingClick = { bookingId ->
                        navController.navigate(MaidScreen.bookingDetails(bookingId))
                    },
                    onNavigateToAllBookings = {
                        navController.navigate(MaidScreen.AllBookings.route)
                    }
                )
            }

            // Profile Screen - Maid Profile and Settings
            composable(route = MaidScreen.Profile.route) {
                MaidProfileScreen(
                    onNavigateToAuth = {
                        navController.navigate(MaidScreen.Auth.route) {
                            // Clear all back stack entries
                            popUpTo(0) {
                                inclusive = true
                            }
                            // Single instance of auth screen
                            launchSingleTop = true
                        }
                    },
                    onNavigateToEditProfile = {
                        navController.navigate(MaidScreen.EditProfile.route)
                    }
                )
            }

            // Edit Profile Screen - Edit Maid Profile Details
            composable(route = MaidScreen.EditProfile.route) {
                MaidEditProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // All Bookings Screen - View All Bookings with Filters
            composable(route = MaidScreen.AllBookings.route) {
                MaidAllBookingsScreen(
                    onNavigateToBookingDetails = { bookingId ->
                        navController.navigate(MaidScreen.bookingDetails(bookingId))
                    }
                )
            }

            // Terms and Conditions Screen - Service Provider Terms
            composable(route = MaidScreen.TermsAndConditions.route) {
                MaidTermsAndConditionsScreen()
            }

            // Booking Details Screen - View and Manage Booking
            composable(
                route = MaidScreen.BOOKING_DETAILS_ROUTE,
                arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                MaidBookingDetailsScreen(
                    bookingId = bookingId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
