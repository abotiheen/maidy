package com.example.maidy.feature.sos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.EmergencyIconBackground
import com.example.maidy.ui.theme.EmergencyIconBorder
import com.example.maidy.ui.theme.EmergencyIconText
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun SOSIcon(
    modifier: Modifier = Modifier,
    size: Int = 120
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(EmergencyIconBackground)
            .border(
                width = 4.dp,
                color = EmergencyIconBorder,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SOS",
            color = EmergencyIconText,
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SOSIconPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(EmergencyIconBackground),
            contentAlignment = Alignment.Center
        ) {
            SOSIcon()
        }
    }
}

