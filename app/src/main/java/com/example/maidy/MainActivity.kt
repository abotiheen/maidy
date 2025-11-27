package com.example.maidy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.maidy.core.components.NotificationPermissionHandler
import com.example.maidy.feature.auth.AuthScreen
import com.example.maidy.feature_maid.navigation.MaidNavHost
import com.example.maidy.navigation.MaidyNavHost
import com.example.maidy.ui.theme.MaidyTheme
import com.example.maidy.BuildConfig

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

                // Check the flavor and show the correct UI
                when (BuildConfig.FLAVOR) {
                    "maid" -> {
                        // If the flavor is 'maid', show the MaidNavHost
                        MaidNavHost(navController = navController)
                    }
                    "customer" -> {
                        // If the flavor is 'customer', show the MaidyNavHost
                        MaidyNavHost(navController = navController)
                    }
                    else -> {
                        // As a fallback, default to the customer UI
                        MaidyNavHost(navController = navController)
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