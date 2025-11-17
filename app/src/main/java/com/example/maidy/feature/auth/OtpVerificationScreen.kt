package com.example.maidy.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.maidy.feature.auth.components.*
import com.example.maidy.ui.theme.*

@Composable
fun OtpVerificationScreen(
    uiState: AuthUiState,
    onEvent: (AuthEvent) -> Unit,
    onNavigateToTerms: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Back button
        IconButton(
            onClick = { onEvent(AuthEvent.NavigateToScreen(AuthScreen.LOGIN)) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 24.dp, start = 12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = "Back to login",
                tint = MaidyTextPrimary
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
            MaidyLogo()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            MaidyTitle()
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Instruction Text
            Text(
                text = "Enter 6-digit code sent to ${uiState.phoneNumber}",
                fontSize = 16.sp,
                color = MaidyTextPrimary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // OTP Input
            OtpInputField(
                otpValue = uiState.otpCode,
                onOtpChange = { onEvent(AuthEvent.OtpCodeChanged(it)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Error Message
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    fontSize = 14.sp,
                    color = if (uiState.errorMessage.contains("success", ignoreCase = true))
                        MaidySuccessGreen
                    else
                        MaidyErrorRed,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Confirm Button
            PrimaryButton(
                text = "Verify Code",
                onClick = { onEvent(AuthEvent.VerifyOtpClicked) },
                isLoading = uiState.isLoading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Terms and Conditions
            TermsAndConditionsText(
                onTermsClick = onNavigateToTerms
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OtpVerificationScreenPreview() {
    MaidyTheme {
        OtpVerificationScreen(
            uiState = AuthUiState(
                otpCode = ""
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OtpVerificationScreenPartialPreview() {
    MaidyTheme {
        OtpVerificationScreen(
            uiState = AuthUiState(
                otpCode = "12"
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OtpVerificationScreenFilledPreview() {
    MaidyTheme {
        OtpVerificationScreen(
            uiState = AuthUiState(
                otpCode = "123456"
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OtpVerificationScreenLoadingPreview() {
    MaidyTheme {
        OtpVerificationScreen(
            uiState = AuthUiState(
                otpCode = "123456",
                isLoading = true
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OtpVerificationScreenErrorPreview() {
    MaidyTheme {
        OtpVerificationScreen(
            uiState = AuthUiState(
                otpCode = "123456",
                errorMessage = "Invalid code. Please try again."
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OtpVerificationScreenSuccessPreview() {
    MaidyTheme {
        OtpVerificationScreen(
            uiState = AuthUiState(
                otpCode = "123456",
                errorMessage = "Code resent successfully"
            ),
            onEvent = {}
        )
    }
}

