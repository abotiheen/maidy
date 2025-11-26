package com.example.maidy.feature_maid.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.feature_maid.auth.components.*
import com.example.maidy.ui.theme.*

@Composable
fun MaidOtpVerificationScreen(
    uiState: MaidAuthUiState,
    onEvent: (MaidAuthEvent) -> Unit,
    onNavigateToTerms: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Back button
        IconButton(
            onClick = { onEvent(MaidAuthEvent.NavigateToScreen(MaidAuthScreen.LOGIN)) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 24.dp, start = 12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = "Back to login",
                tint = MaidAppTextPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            MaidLogo()

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            MaidTitle()

            Spacer(modifier = Modifier.height(48.dp))

            // Instruction Text
            Text(
                text = "Enter 6-digit code sent to ${uiState.phoneNumber}",
                fontSize = 16.sp,
                color = MaidAppTextPrimary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // OTP Input
            MaidOtpInputField(
                otpValue = uiState.otpCode,
                onOtpChange = { onEvent(MaidAuthEvent.OtpCodeChanged(it)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // Error Message
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    fontSize = 14.sp,
                    color = if (uiState.errorMessage.contains("success", ignoreCase = true))
                        MaidAppSuccessGreen
                    else
                        MaidAppErrorRed,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Confirm Button
            MaidPrimaryButton(
                text = "Verify Code",
                onClick = { onEvent(MaidAuthEvent.VerifyOtpClicked) },
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Terms and Conditions
            MaidTermsAndConditionsText(
                onTermsClick = onNavigateToTerms
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidOtpVerificationScreenPreview() {
    MaidyTheme {
        MaidOtpVerificationScreen(
            uiState = MaidAuthUiState(
                otpCode = ""
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidOtpVerificationScreenPartialPreview() {
    MaidyTheme {
        MaidOtpVerificationScreen(
            uiState = MaidAuthUiState(
                otpCode = "12"
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidOtpVerificationScreenFilledPreview() {
    MaidyTheme {
        MaidOtpVerificationScreen(
            uiState = MaidAuthUiState(
                otpCode = "123456"
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidOtpVerificationScreenLoadingPreview() {
    MaidyTheme {
        MaidOtpVerificationScreen(
            uiState = MaidAuthUiState(
                otpCode = "123456",
                isLoading = true
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidOtpVerificationScreenErrorPreview() {
    MaidyTheme {
        MaidOtpVerificationScreen(
            uiState = MaidAuthUiState(
                otpCode = "123456",
                errorMessage = "Invalid code. Please try again."
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidOtpVerificationScreenSuccessPreview() {
    MaidyTheme {
        MaidOtpVerificationScreen(
            uiState = MaidAuthUiState(
                otpCode = "123456",
                errorMessage = "Code resent successfully"
            ),
            onEvent = {}
        )
    }
}
