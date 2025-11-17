package com.example.maidy.feature.sos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.EmergencyScreenBackground
import com.example.maidy.ui.theme.EmergencyTitleText
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun EmergencyTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        color = EmergencyTitleText,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        letterSpacing = 0.5.sp
    )
}

@Preview(showBackground = true)
@Composable
fun EmergencyTitlePreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmergencyScreenBackground)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            EmergencyTitle(text = "Confirm Emergency")
        }
    }
}


