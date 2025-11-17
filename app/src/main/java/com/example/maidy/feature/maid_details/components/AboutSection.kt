package com.example.maidy.feature.maid_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

@Composable
fun AboutSection(
    aboutText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = aboutText,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            color = MaidProfileAboutText
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutSectionPreview() {
    MaidyTheme {
        Box(modifier = Modifier.background(MaidProfileContentBackground)) {
            AboutSection(
                aboutText = "Hello! I'm Elena, and I've been a professional cleaner for over 5 years. I take great pride in making homes sparkle and creating a fresh, relaxing environment for my clients. I'm reliable, thorough, and always bring a positive attitude."
            )
        }
    }
}

