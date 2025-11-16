package com.example.maidy.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.auth.components.*
import com.example.maidy.ui.theme.*

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
        
        Spacer(modifier = Modifier.height(32.dp))
        
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
            placeholder = "Enter your password",
            isPassword = true,
            passwordVisible = uiState.passwordVisible,
            onPasswordVisibilityToggle = { onEvent(AuthEvent.TogglePasswordVisibility) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot Password?",
                fontSize = 14.sp,
                color = MaidyBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { 
                    onEvent(AuthEvent.ForgotPasswordClicked) 
                }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Error Message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                fontSize = 14.sp,
                color = MaidyErrorRed,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Login Button
        PrimaryButton(
            text = "Login",
            onClick = { onEvent(AuthEvent.LoginClicked) },
            isLoading = uiState.isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Terms and Conditions
        TermsAndConditionsText()
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    MaidyTheme {
        LoginScreen(
            uiState = AuthUiState(
                phoneNumber = "",
                password = "",
                passwordVisible = false
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenFilledPreview() {
    MaidyTheme {
        LoginScreen(
            uiState = AuthUiState(
                phoneNumber = "770 123 4567",
                password = "password123",
                passwordVisible = false
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenLoadingPreview() {
    MaidyTheme {
        LoginScreen(
            uiState = AuthUiState(
                phoneNumber = "770 123 4567",
                password = "password123",
                isLoading = true
            ),
            onEvent = {}
        )
    }
}

