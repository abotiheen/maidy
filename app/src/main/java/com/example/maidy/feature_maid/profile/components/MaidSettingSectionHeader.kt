package com.example.maidy.feature_maid.profile.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature_maid.auth.MaidAppTextPrimary

/**
 * Section header component for Settings - Green themed
 */
@Composable
fun MaidSettingSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaidAppTextPrimary,
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun MaidSettingSectionHeaderPreview() {
    MaidSettingSectionHeader(title = "Settings")
}
