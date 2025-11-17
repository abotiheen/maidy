package com.example.maidy.feature.auth

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.auth.components.*
import com.example.maidy.ui.theme.*

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onEvent: (AuthEvent) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Title
        Text(
            text = "Create Your Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaidyTextPrimary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Full Name Field
        AuthTextField(
            value = uiState.fullName,
            onValueChange = { onEvent(AuthEvent.FullNameChanged(it)) },
            label = "Full Name",
            placeholder = "Enter your full name",
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Phone Number Field
        PhoneNumberField(
            phoneNumber = uiState.phoneNumber,
            onPhoneNumberChange = { onEvent(AuthEvent.PhoneNumberChanged(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Password Field
        AuthTextField(
            value = uiState.password,
            onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
            label = "Password",
            placeholder = "Create a password (min 6 characters)",
            isPassword = true,
            passwordVisible = uiState.passwordVisible,
            onPasswordVisibilityToggle = { onEvent(AuthEvent.TogglePasswordVisibility) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Info text
        Text(
            text = "We'll send you a verification code to verify your phone",
            fontSize = 14.sp,
            color = MaidyTextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Error Message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                fontSize = 14.sp,
                color = MaidyErrorRed,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Register Button - sends OTP
        PrimaryButton(
            text = "Register",
            onClick = { 
                activity?.let { onEvent(AuthEvent.SendOtpForRegistration(it)) }
            },
            isLoading = uiState.isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Terms and Conditions
        TermsAndConditionsText()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Already have account
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? ",
                fontSize = 14.sp,
                color = MaidyTextSecondary
            )
            Text(
                text = "Log In",
                fontSize = 14.sp,
                color = MaidyBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    MaidyTheme {
        RegisterScreen(
            uiState = AuthUiState(
                fullName = "",
                phoneNumber = ""
            ),
            onEvent = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenFilledPreview() {
    MaidyTheme {
        RegisterScreen(
            uiState = AuthUiState(
                fullName = "John Doe",
                phoneNumber = "770 123 4567"
            ),
            onEvent = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenLoadingPreview() {
    MaidyTheme {
        RegisterScreen(
            uiState = AuthUiState(
                fullName = "John Doe",
                phoneNumber = "770 123 4567",
                isLoading = true
            ),
            onEvent = {},
            onNavigateToLogin = {}
        )
    }
}

