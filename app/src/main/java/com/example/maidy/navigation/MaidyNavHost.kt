package com.example.maidy.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.maidy.R
import com.example.maidy.feature.admin.AdminAddMaidScreen
import com.example.maidy.feature.adjust_recurring.AdjustRecurringScreen
import com.example.maidy.feature.auth.AuthScreen
import com.example.maidy.feature.booking.BookingStatusScreen
import com.example.maidy.feature.booking_details.BookingDetailsScreen
import com.example.maidy.feature.edit_profile.EditProfileScreen
import com.example.maidy.feature.home.HomeScreen
import com.example.maidy.feature.maid_details.MaidProfileScreen
import com.example.maidy.feature.maidlist.MaidListScreen
import com.example.maidy.feature.rating.RatingScreen
import com.example.maidy.feature.settings.ProfileScreen
import com.example.maidy.feature.sos.EmergencyScreen
import com.example.maidy.feature.terms.TermsAndConditionsScreen
import com.example.maidy.ui.theme.MaidListBackground
import com.example.maidy.ui.theme.MaidyBackgroundWhite
import com.example.maidy.ui.theme.MaidyTextPrimary

/**
 * Main navigation host for the Maidy app.
 * Defines all navigation routes and their corresponding screens.
 * 
 * @param navController The navigation controller for managing navigation
 * @param modifier Optional modifier for the NavHost
 * @param startDestination The initial destination when the app starts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaidyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Auth.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = getScreenFromRoute(currentRoute)
    
    Scaffold(
        containerColor = MaidListBackground,
        topBar = {
            if (currentScreen.showTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            text = currentScreen.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaidyTextPrimary
                        )
                    },
                    navigationIcon = {
                        if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_back),
                                    contentDescription = "Back",
                                    tint = MaidyTextPrimary
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
                MaidyBottomBar(
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
        
        // Auth Screen - Login/Register/OTP Verification
        composable(route = Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onNavigateToTerms = {
                    navController.navigate(Screen.TermsAndConditions.route)
                }
            )
        }
        
        // Home Screen - Main Dashboard
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToBooking = {
                    navController.navigate(Screen.MaidList.route)
                },
                onNavigateToMyBookings = {
                    navController.navigate(Screen.AllBookings.route)
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminAddMaid.route)
                },
                onNavigateToBookingDetails = { bookingId ->
                    navController.navigate(Screen.BookingStatus.createRoute(bookingId))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

            // Search Screen - Search for Maids
            composable(route = Screen.Search.route) {
                com.example.maidy.feature.search.SearchScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onMaidClick = { maidId ->
                        navController.navigate(Screen.MaidProfile.createRoute(maidId))
                    }
                )
            }

            // All Bookings Screen - View All User Bookings
            composable(route = Screen.AllBookings.route) {
                com.example.maidy.feature.all_bookings.AllBookingsScreen(
                    onNavigateToBookingDetails = { bookingId ->
                        navController.navigate(Screen.BookingStatus.createRoute(bookingId))
                }
            )
        }
        
        // Maid List Screen - Browse Available Maids
        composable(route = Screen.MaidList.route) {
            MaidListScreen(
                onNavigateToMaidDetails = { maidId ->
                    navController.navigate(Screen.MaidProfile.createRoute(maidId))
                }
            )
        }
        
        // Maid Profile Screen - View Detailed Maid Profile
        composable(
            route = Screen.MaidProfile.route,
            arguments = listOf(
                navArgument("maidId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val maidId = backStackEntry.arguments?.getString("maidId") ?: ""
            MaidProfileScreen(
                maidId = maidId,
                onBookNowClick = {
                    navController.navigate(Screen.BookingDetails.createRoute(maidId))
                }
            )
        }
        
        // Booking Details Screen - Book Service with a Maid
        composable(
            route = Screen.BookingDetails.route,
            arguments = listOf(
                navArgument("maidId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val maidId = backStackEntry.arguments?.getString("maidId") ?: ""
            BookingDetailsScreen(
                maidId = maidId,
                onBackClick = {
                    navController.popBackStack()
                },
                onBookingConfirmed = {
                    // Navigate to home after booking is confirmed, clearing the back stack
                    navController.navigate(Screen.Home.route) {
                        // Clear all back stack entries up to and including the booking details
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = false
                        }
                        // Avoid multiple copies of home in back stack
                        launchSingleTop = true
                    }
                }
            )
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
            val bookingId = it.arguments?.getString("bookingId").orEmpty()
            BookingStatusScreen(
                bookingId = bookingId,
                onNavigateToSOS = {
                    navController.navigate(Screen.Emergency.route)
                },
                onNavigateToAdjustRecurring = {
                    navController.navigate(Screen.AdjustRecurring.createRoute(bookingId))
                },
                onNavigateToRating = {
                    navController.navigate(Screen.Rating.createRoute(bookingId))
                }
            )
        }

            // Adjust Recurring Booking Screen - Manage Recurring Booking
            composable(
                route = Screen.AdjustRecurring.route,
                arguments = listOf(
                    navArgument("bookingId") {
                        type = NavType.StringType
                    }
                )
            ) {
                val bookingId = it.arguments?.getString("bookingId").orEmpty()
                AdjustRecurringScreen(
                    bookingId = bookingId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToRating = {
                        navController.navigate(Screen.Rating.createRoute(bookingId))
                    }
                )
            }

            // Emergency Screen - SOS/Emergency Help
            composable(route = Screen.Emergency.route) {
                EmergencyScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
        }
        
        // Notifications Screen - View User Notifications
        // TODO: Implement NotificationsScreen
        composable(route = Screen.Notifications.route) {
            // Placeholder for future implementation
            PlaceholderScreen(screenName = "Notifications")
        }
        
        // Rating Screen - Rate Maid After Service
            composable(
                route = Screen.Rating.route,
                arguments = listOf(
                    navArgument("bookingId") {
                        type = NavType.StringType
                    }
                )
            ) {
                val bookingId = it.arguments?.getString("bookingId").orEmpty()
                RatingScreen(
                    bookingId = bookingId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
        }
        
        // Settings Screen - App Settings and Preferences
        composable(route = Screen.Settings.route) {
            ProfileScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        // Clear all back stack entries
                        popUpTo(0) {
                            inclusive = true
                        }
                        // Single instance of auth screen
                        launchSingleTop = true
                    }
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                }
            )
        }

            // Edit Profile Screen - Edit User Profile
            composable(route = Screen.EditProfile.route) {
                EditProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                }
            )
        }
        
        // Terms and Conditions Screen
        composable(route = Screen.TermsAndConditions.route) {
            TermsAndConditionsScreen()
        }
        
        // Admin Add Maid Screen - For populating Firebase
        composable(route = Screen.AdminAddMaid.route) {
            AdminAddMaidScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        }
    }
}
