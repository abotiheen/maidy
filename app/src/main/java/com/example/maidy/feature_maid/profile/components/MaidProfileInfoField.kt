package com.example.maidy.feature_maid.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature_maid.auth.MaidAppBackgroundLight
import com.example.maidy.feature_maid.auth.MaidAppTextPrimary
import com.example.maidy.feature_maid.auth.MaidAppTextSecondary

/**
 * Maid profile info field component - Green themed
 */
@Composable
fun MaidProfileInfoField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Label
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaidAppTextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Value Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp) // More rounded for maid app
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                color = MaidAppTextPrimary,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MaidProfileInfoFieldPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaidAppBackgroundLight)
            .padding(vertical = 16.dp)
    ) {
        MaidProfileInfoField(
            label = "Full Name",
            value = "Sarah Johnson"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MaidProfileInfoField(
            label = "Phone Number",
            value = "+964 770 123 4567"
        )
    }
}
