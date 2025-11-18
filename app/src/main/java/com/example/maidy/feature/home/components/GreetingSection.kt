package com.example.maidy.feature.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.HomeGreetingText
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun GreetingSection(
    userName: String,
    modifier: Modifier = Modifier
) {
    val displayName = userName.substringBefore(" ")
    Text(
        text = "Hello, $displayName!",
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        color = HomeGreetingText,
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingSectionPreview() {
    MaidyTheme {
        GreetingSection(userName = "Sarah")
    }
}


