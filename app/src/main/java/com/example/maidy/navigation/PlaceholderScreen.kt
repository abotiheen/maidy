package com.example.maidy.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.MaidyBackgroundWhite
import com.example.maidy.ui.theme.MaidyTextPrimary

/**
 * Placeholder screen for routes that have been defined but not yet implemented.
 * This allows the navigation graph to be complete while features are still in development.
 * 
 * @param screenName The name of the screen to display
 */
@Composable
fun PlaceholderScreen(
    screenName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaidyBackgroundWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = screenName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaidyTextPrimary
            )
            Text(
                text = "Coming Soon",
                fontSize = 16.sp,
                color = MaidyTextPrimary.copy(alpha = 0.6f)
            )
        }
    }
}

