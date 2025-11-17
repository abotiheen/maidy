package com.example.maidy.feature.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

/**
 * 4-digit OTP input field with separate boxes for each digit
 * 
 * @param otpValue Current OTP value (0-4 digits)
 * @param onOtpChange Callback when OTP changes
 * @param modifier Modifier to be applied to the field container
 */
@Composable
fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        repeat(4) { index ->
            val digit = otpValue.getOrNull(index)?.toString() ?: ""
            OtpDigitBox(
                digit = digit,
                isFocused = otpValue.length == index,
                onClick = { /* Handle click */ }
            )
        }
    }
    
    // Hidden text field for input
    BasicTextField(
        value = otpValue,
        onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) onOtpChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.size(0.dp)
    )
}

@Composable
private fun OtpDigitBox(
    digit: String,
    isFocused: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) MaidyBorderFocused else MaidyBorderLight,
                shape = RoundedCornerShape(12.dp)
            )
            .background(MaidyBackgroundWhite, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaidyTextPrimary
        )
    }
}

@Preview(name = "Empty OTP", showBackground = true)
@Composable
private fun OtpInputFieldEmptyPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            OtpInputField(
                otpValue = "",
                onOtpChange = {}
            )
        }
    }
}

@Preview(name = "One Digit", showBackground = true)
@Composable
private fun OtpInputFieldOneDigitPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            OtpInputField(
                otpValue = "1",
                onOtpChange = {}
            )
        }
    }
}

@Preview(name = "Two Digits", showBackground = true)
@Composable
private fun OtpInputFieldTwoDigitsPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            OtpInputField(
                otpValue = "12",
                onOtpChange = {}
            )
        }
    }
}

@Preview(name = "Three Digits", showBackground = true)
@Composable
private fun OtpInputFieldThreeDigitsPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            OtpInputField(
                otpValue = "123",
                onOtpChange = {}
            )
        }
    }
}

@Preview(name = "Complete OTP", showBackground = true)
@Composable
private fun OtpInputFieldCompletePreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            OtpInputField(
                otpValue = "1234",
                onOtpChange = {}
            )
        }
    }
}


