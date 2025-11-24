package com.example.maidy.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.R
import com.example.maidy.ui.theme.HomeNotificationBadge
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun HomeHeader(
    profileImageUrl: String,
    hasNotifications: Boolean,
    onNotificationClick: () -> Unit,
    onAdminClick: () -> Unit = {},
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
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Image",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Gray
                )
            }
        }

        // Home Title
        Text(
            text = "Home",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        // Action buttons row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Admin Icon
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Admin",
                modifier = Modifier
                    .size(28.dp)
                    .clickable(onClick = onAdminClick),
                tint = Color(0xFF4299E1)
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


