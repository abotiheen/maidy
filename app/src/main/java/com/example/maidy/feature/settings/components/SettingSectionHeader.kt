package com.example.maidy.feature.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.ProfileSectionHeaderText

/**
 * Section header component for Settings and History & Support sections
 */
@Composable
fun SettingSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ProfileSectionHeaderText,
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingSectionHeaderPreview() {
    SettingSectionHeader(title = "Settings")
}



