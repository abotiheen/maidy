package com.example.maidy.feature.sos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.maidy.ui.theme.EmergencyCancelButton
import com.example.maidy.ui.theme.EmergencyCancelButtonBorder
import com.example.maidy.ui.theme.EmergencyCancelButtonText
import com.example.maidy.ui.theme.EmergencyScreenBackground
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 2.dp,
                color = EmergencyCancelButtonBorder,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = EmergencyCancelButton,
            contentColor = EmergencyCancelButtonText
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = "Cancel",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CancelButtonPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .background(EmergencyScreenBackground)
                .padding(16.dp)
        ) {
            CancelButton(onClick = {})
        }
    }
}

