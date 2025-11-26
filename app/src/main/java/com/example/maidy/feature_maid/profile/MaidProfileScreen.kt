package com.example.maidy.feature_maid.profile

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
 * Placeholder profile screen for maids
 * This will be expanded with profile editing, settings, and other features
 */
@Composable
fun MaidProfileScreen(
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
                text = "Maid Profile Settings 👤",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaidAppTextPrimary
            )

            Text(
                text = "Profile editing screen coming soon!",
                fontSize = 16.sp,
                color = MaidAppTextPrimary
            )

            Text(
                text = "You'll be able to edit your details, services, and more here.",
                fontSize = 14.sp,
                color = MaidAppTextPrimary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidProfileScreenPreview() {
    MaidyTheme {
        MaidProfileScreen()
    }
}
