package com.example.maidy.feature.rating.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

@Composable
fun RatingSubmitButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RatingSubmitButton,
            contentColor = RatingSubmitButtonText,
            disabledContainerColor = RatingSubmitButton.copy(alpha = 0.5f),
            disabledContentColor = RatingSubmitButtonText.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(vertical = 18.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Submit Review",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(name = "Enabled", showBackground = true)
@Composable
fun RatingSubmitButtonEnabledPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            RatingSubmitButton(
                onClick = {},
                isLoading = false,
                enabled = true
            )
        }
    }
}

@Preview(name = "Disabled", showBackground = true)
@Composable
fun RatingSubmitButtonDisabledPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            RatingSubmitButton(
                onClick = {},
                isLoading = false,
                enabled = false
            )
        }
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
fun RatingSubmitButtonLoadingPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            RatingSubmitButton(
                onClick = {},
                isLoading = true,
                enabled = true
            )
        }
    }
}
