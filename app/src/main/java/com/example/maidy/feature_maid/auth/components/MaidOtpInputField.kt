package com.example.maidy.feature_maid.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature_maid.auth.*
import com.example.maidy.ui.theme.*

/**
 * 6-digit OTP input field with separate boxes for each digit - Green themed with more rounded corners
 *
 * @param otpValue Current OTP value (0-6 digits)
 * @param onOtpChange Callback when OTP changes
 * @param modifier Modifier to be applied to the field container
 */
@Composable
fun MaidOtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Visible boxes
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            repeat(6) { index ->
                val digit = otpValue.getOrNull(index)?.toString() ?: ""
                MaidOtpDigitBox(
                    digit = digit,
                    isFocused = otpValue.length == index,
                    onClick = {
                        focusRequester.requestFocus()
                    }
                )
            }
        }

        // Hidden text field for actual input
        BasicTextField(
            value = otpValue,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    onOtpChange(it)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                color = Color.Transparent,
                fontSize = 1.sp
            ),
            decorationBox = { innerTextField ->
                // Empty decoration box - we're using the visual boxes above
                Box(modifier = Modifier.size(0.dp)) {
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun MaidOtpDigitBox(
    digit: String,
    isFocused: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) MaidAppBorderFocused else MaidAppBorderLight,
                shape = RoundedCornerShape(16.dp) // More rounded than customer app (12dp)
            )
            .background(MaidAppBackgroundWhite, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaidAppTextPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(name = "Empty OTP", showBackground = true)
@Composable
private fun MaidOtpInputFieldEmptyPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidOtpInputField(
                otpValue = "",
                onOtpChange = {}
            )
        }
    }
}

@Preview(name = "One Digit", showBackground = true)
@Composable
private fun MaidOtpInputFieldOneDigitPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidOtpInputField(
                otpValue = "1",
                onOtpChange = {}
            )
        }
    }
}

@Preview(name = "Two Digits", showBackground = true)
@Composable
private fun MaidOtpInputFieldTwoDigitsPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidOtpInputField(
                otpValue = "12",
                onOtpChange = {}
            )
        }
    }
}

@Preview(name = "Three Digits", showBackground = true)
@Composable
private fun MaidOtpInputFieldThreeDigitsPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidOtpInputField(
                otpValue = "123",
                onOtpChange = {}
            )
        }
    }
}

@Preview(name = "Complete OTP", showBackground = true)
@Composable
private fun MaidOtpInputFieldCompletePreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidOtpInputField(
                otpValue = "123456",
                onOtpChange = {}
            )
        }
    }
}
