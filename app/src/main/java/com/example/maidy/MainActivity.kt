package com.example.maidy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.maidy.core.components.NotificationPermissionHandler
import com.example.maidy.feature.auth.AuthScreen
import com.example.maidy.feature_maid.navigation.MaidNavHost
import com.example.maidy.navigation.MaidyNavHost
import com.example.maidy.ui.theme.MaidyTheme
import com.example.maidy.BuildConfig
import com.example.maidy.feature_maid.navigation.MaidScreen
import com.example.maidy.navigation.Screen
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaidyTheme {
                // Request notification permission
                NotificationPermissionHandler(
                    onPermissionGranted = {
                        println("✅ MainActivity: Notification permission granted")
                    },
                    onPermissionDenied = {
                        println("⚠️ MainActivity: Notification permission denied")
                    }
                )

                val navController = rememberNavController()

                // Check the aflavor and show the correct UI
                when (BuildConfig.FLAVOR) {
                    "maid" -> {
                        // If the flavor is 'maid', show the MaidNavHos

                        val mainViewModel: MainViewModel = koinViewModel()
                        val uiState by mainViewModel.uiState.collectAsState()

                        if (uiState.isLoading) {
                            // Show a loading indicator while checking login status
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            // Once the destination is determined, show the NavHost
                            uiState.startDestination?.let { startDestination ->
                                MaidNavHost(
                                    navController = navController,
                                    startDestination = when (startDestination) {
                                        StartDestination.Auth -> MaidScreen.Auth.route
                                        StartDestination.Home -> MaidScreen.Home.route
                                    }
                                )
                            }
                        }
                    }
                    "customer" -> {
                        // For customer flavor, check login status to determine start destination
                        val mainViewModel: MainViewModel = koinViewModel()
                        val uiState by mainViewModel.uiState.collectAsState()

                        if (uiState.isLoading) {
                            // Show a loading indicator while checking login status
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            // Once the destination is determined, show the NavHost
                            uiState.startDestination?.let { startDestination ->
                                MaidyNavHost(
                                    navController = navController,
                                    startDestination = when (startDestination) {
                                        StartDestination.Auth -> Screen.Auth.route
                                        StartDestination.Home -> Screen.Home.route
                                    }
                                )
                            }
                        }
                    }
                    else -> {
                        // As a fallback, default to the customer UI
                        val mainViewModel: MainViewModel = koinViewModel()
                        val uiState by mainViewModel.uiState.collectAsState()

                        if (uiState.isLoading) {
                            // Show a loading indicator while checking login status
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            // Once the destination is determined, show the NavHost
                            uiState.startDestination?.let { startDestination ->
                                MaidyNavHost(
                                    navController = navController,
                                    startDestination = when (startDestination) {
                                        StartDestination.Auth -> Screen.Auth.route
                                        StartDestination.Home -> Screen.Home.route
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MaidyTheme {
        Greeting("Android")
    }
}