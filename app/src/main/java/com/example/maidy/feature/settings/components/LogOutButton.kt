package com.example.maidy.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

/**
 * Log out button component
 */
@Composable
fun LogOutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ProfileLogOutButtonBackground)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Log Out",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = ProfileLogOutButtonText
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LogOutButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProfileScreenBackground)
    ) {
        LogOutButton(onClick = {})
    }
}



