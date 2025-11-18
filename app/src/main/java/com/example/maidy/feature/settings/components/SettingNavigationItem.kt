package com.example.maidy.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

/**
 * Setting item that navigates to another screen
 */
@Composable
fun SettingNavigationItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ButtonDefaults.MinHeight)
            .padding(horizontal = 12.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false,
                ambientColor = Color(0x14000000), // Soft ambient shadow (8% opacity)
                spotColor = Color(0x1F000000) // Soft spot shadow (12% opacity)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(ProfileSettingItemBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = ProfileSettingIconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = ProfileSettingTitleText
                )
            }
        }

        Icon(
            imageVector = Icons.Default.AddCircle,
            contentDescription = "Navigate",
            tint = ProfileSettingChevronTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingNavigationItemPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProfileScreenBackground)
            .padding(vertical = 16.dp)
    ) {
        SettingNavigationItem(
            icon = Icons.Outlined.Home,
            title = "Language",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingNavigationItem(
            icon = Icons.Outlined.Check,
            title = "Booking History",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingNavigationItem(
            icon = Icons.Outlined.Home,
            title = "Payment History",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingNavigationItem(
            icon = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
            title = "Help & Support",
            onClick = {}
        )
    }
}

