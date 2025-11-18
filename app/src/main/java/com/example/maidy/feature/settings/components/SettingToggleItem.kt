package com.example.maidy.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
 * Setting item with toggle switch
 */
@Composable
fun SettingToggleItem(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
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
            .padding(horizontal = 12.dp, vertical = 8.dp),
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
            
            Text(
                text = title,
                fontSize = 16.sp,
                color = ProfileSettingTitleText
            )
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ProfileSwitchThumbChecked,
                checkedTrackColor = ProfileSwitchTrackChecked,
                uncheckedThumbColor = ProfileSwitchThumbUnchecked,
                uncheckedTrackColor = ProfileSwitchTrackUnchecked
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingToggleItemPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProfileScreenBackground)
            .padding(vertical = 16.dp)
    ) {
        SettingToggleItem(
            icon = Icons.Outlined.Notifications,
            title = "Dark Mode",
            isChecked = false,
            onCheckedChange = {}
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingToggleItem(
            icon = Icons.Outlined.Notifications,
            title = "Notifications",
            isChecked = true,
            onCheckedChange = {}
        )
    }
}

