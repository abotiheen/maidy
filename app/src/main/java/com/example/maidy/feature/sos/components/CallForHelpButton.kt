package com.example.maidy.feature.sos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.EmergencyCallButton
import com.example.maidy.ui.theme.EmergencyCallButtonText
import com.example.maidy.ui.theme.EmergencyScreenBackground
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun CallForHelpButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = EmergencyCallButton,
            contentColor = EmergencyCallButtonText
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = "Call for Help",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CallForHelpButtonPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .background(EmergencyScreenBackground)
                .padding(16.dp)
        ) {
            CallForHelpButton(onClick = {})
        }
    }
}

