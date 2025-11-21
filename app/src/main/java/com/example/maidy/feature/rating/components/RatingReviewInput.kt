package com.example.maidy.feature.rating.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

@Composable
fun RatingReviewInput(
    reviewText: String,
    onReviewTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Your experience",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = RatingFeedbackLabel,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        TextField(
            value = reviewText,
            onValueChange = onReviewTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(RatingInputBackground, RoundedCornerShape(12.dp))
                .border(1.dp, RatingInputBorder, RoundedCornerShape(12.dp)),
            placeholder = {
                Text(
                    text = "Tell us about your experience...\n(Optional)",
                    color = RatingInputHint,
                    fontSize = 15.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = RatingInputBackground,
                unfocusedContainerColor = RatingInputBackground,
                disabledContainerColor = RatingInputBackground,
                focusedTextColor = RatingInputText,
                unfocusedTextColor = RatingInputText,
                cursorColor = RatingSubmitButton,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            maxLines = 8
        )
    }
}

@Preview(name = "Empty Input", showBackground = true)
@Composable
fun RatingReviewInputPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RatingScreenBackground)
                .padding(24.dp)
        ) {
            RatingReviewInput(
                reviewText = "",
                onReviewTextChanged = {}
            )
        }
    }
}

@Preview(name = "With Text", showBackground = true)
@Composable
fun RatingReviewInputWithTextPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RatingScreenBackground)
                .padding(24.dp)
        ) {
            RatingReviewInput(
                reviewText = "Maria did an excellent job! Very thorough and professional.",
                onReviewTextChanged = {}
            )
        }
    }
}
