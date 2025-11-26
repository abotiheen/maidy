package com.example.maidy.feature_maid.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature_maid.auth.components.*
import com.example.maidy.ui.theme.*

@Composable
fun MaidLoginScreen(
    uiState: MaidAuthUiState,
    onEvent: (MaidAuthEvent) -> Unit,
    onNavigateToTerms: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
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

        Spacer(modifier = Modifier.height(32.dp))

        // Phone Number Field
        MaidPhoneNumberField(
            phoneNumber = uiState.phoneNumber,
            onPhoneNumberChange = { onEvent(MaidAuthEvent.PhoneNumberChanged(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Password Field
        MaidAuthTextField(
            value = uiState.password,
            onValueChange = { onEvent(MaidAuthEvent.PasswordChanged(it)) },
            label = "Password",
            placeholder = "Enter your password",
            isPassword = true,
            passwordVisible = uiState.passwordVisible,
            onPasswordVisibilityToggle = { onEvent(MaidAuthEvent.TogglePasswordVisibility) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Error Message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                fontSize = 14.sp,
                color = MaidAppErrorRed,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Login Button
        MaidPrimaryButton(
            text = "Login",
            onClick = { onEvent(MaidAuthEvent.LoginClicked) },
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidLoginScreenPreview() {
    MaidyTheme {
        MaidLoginScreen(
            uiState = MaidAuthUiState(
                phoneNumber = ""
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidLoginScreenFilledPreview() {
    MaidyTheme {
        MaidLoginScreen(
            uiState = MaidAuthUiState(
                phoneNumber = "770 123 4567"
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidLoginScreenLoadingPreview() {
    MaidyTheme {
        MaidLoginScreen(
            uiState = MaidAuthUiState(
                phoneNumber = "770 123 4567",
                isLoading = true
            ),
            onEvent = {}
        )
    }
}
