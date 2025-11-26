package com.example.maidy.feature_maid.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature_maid.auth.MaidAppBackgroundLight
import com.example.maidy.feature_maid.auth.MaidAppTextPrimary
import com.example.maidy.ui.theme.MaidyTheme

/**
 * Placeholder home screen for maids
 * This will be expanded with booking management, profile editing, and other features
 */
@Composable
fun MaidHomeScreen(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaidAppBackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Welcome to Maidy for Maids! 🧹",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaidAppTextPrimary
            )

            Text(
                text = "This is your home screen.",
                fontSize = 16.sp,
                color = MaidAppTextPrimary
            )

            Text(
                text = "Profile editing and booking management features coming soon!",
                fontSize = 14.sp,
                color = MaidAppTextPrimary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidHomeScreenPreview() {
    MaidyTheme {
        MaidHomeScreen()
    }
}
