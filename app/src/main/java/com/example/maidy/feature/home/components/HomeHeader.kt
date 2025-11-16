package com.example.maidy.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.ui.theme.HomeNotificationBadge
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun HomeHeader(
    profileImageUrl: String,
    hasNotifications: Boolean,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            // Placeholder for profile image
            // TODO: Load actual image when API is connected
        }

        // Home Title
        Text(
            text = "Home",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Notification Bell
        BadgedBox(
            badge = {
                if (hasNotifications) {
                    Badge(
                        containerColor = HomeNotificationBadge,
                        modifier = Modifier.size(8.dp)
                    )
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // TODO: Replace with bell icon
                contentDescription = "Notifications",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onNotificationClick),
                tint = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeHeaderPreview() {
    MaidyTheme {
        HomeHeader(
            profileImageUrl = "",
            hasNotifications = true,
            onNotificationClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeHeaderNoNotificationPreview() {
    MaidyTheme {
        HomeHeader(
            profileImageUrl = "",
            hasNotifications = false,
            onNotificationClick = {}
        )
    }
}

