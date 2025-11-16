package com.example.maidy.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI State
data class AuthUiState(
    val isLoading: Boolean = false,
    val phoneNumber: String = "",
    val password: String = "",
    val fullName: String = "",
    val otpCode: String = "",
    val passwordVisible: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val isOtpVerified: Boolean = false,
    val currentScreen: AuthScreen = AuthScreen.LOGIN
)

enum class AuthScreen {
    LOGIN, REGISTER, OTP_VERIFICATION
}

// UI Events
sealed class AuthEvent {
    data class PhoneNumberChanged(val phone: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data class FullNameChanged(val name: String) : AuthEvent()
    data class OtpCodeChanged(val code: String) : AuthEvent()
    object TogglePasswordVisibility : AuthEvent()
    object LoginClicked : AuthEvent()
    object RegisterClicked : AuthEvent()
    object ConfirmOtpClicked : AuthEvent()
    object ResendCodeClicked : AuthEvent()
    object ForgotPasswordClicked : AuthEvent()
    data class NavigateToScreen(val screen: AuthScreen) : AuthEvent()
    object ClearError : AuthEvent()
}

class AuthViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.PhoneNumberChanged -> {
                _uiState.update { it.copy(phoneNumber = event.phone) }
            }
            is AuthEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }
            is AuthEvent.FullNameChanged -> {
                _uiState.update { it.copy(fullName = event.name) }
            }
            is AuthEvent.OtpCodeChanged -> {
                _uiState.update { it.copy(otpCode = event.code) }
            }
            AuthEvent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
            }
            AuthEvent.LoginClicked -> {
                performLogin()
            }
            AuthEvent.RegisterClicked -> {
                performRegistration()
            }
            AuthEvent.ConfirmOtpClicked -> {
                verifyOtp()
            }
            AuthEvent.ResendCodeClicked -> {
                resendOtp()
            }
            AuthEvent.ForgotPasswordClicked -> {
                handleForgotPassword()
            }
            is AuthEvent.NavigateToScreen -> {
                _uiState.update { it.copy(currentScreen = event.screen) }
            }
            AuthEvent.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }
    
    private fun performLogin() {
        viewModelScope.launch {
            // Validate inputs
            if (_uiState.value.phoneNumber.isEmpty()) {
                _uiState.update { it.copy(errorMessage = "Please enter phone number") }
                return@launch
            }
            if (_uiState.value.password.isEmpty()) {
                _uiState.update { it.copy(errorMessage = "Please enter password") }
                return@launch
            }
            
            // Simulate API call
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1500)
            
            // Placeholder: Navigate to OTP screen
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    currentScreen = AuthScreen.OTP_VERIFICATION
                )
            }
        }
    }
    
    private fun performRegistration() {
        viewModelScope.launch {
            // Validate inputs
            if (_uiState.value.fullName.isEmpty()) {
                _uiState.update { it.copy(errorMessage = "Please enter your full name") }
                return@launch
            }
            if (_uiState.value.phoneNumber.isEmpty()) {
                _uiState.update { it.copy(errorMessage = "Please enter phone number") }
                return@launch
            }
            if (_uiState.value.password.isEmpty()) {
                _uiState.update { it.copy(errorMessage = "Please enter password") }
                return@launch
            }
            
            // Simulate API call
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1500)
            
            // Placeholder: Navigate to OTP screen
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    currentScreen = AuthScreen.OTP_VERIFICATION
                )
            }
        }
    }
    
    private fun verifyOtp() {
        viewModelScope.launch {
            if (_uiState.value.otpCode.length != 4) {
                _uiState.update { it.copy(errorMessage = "Please enter 4-digit code") }
                return@launch
            }
            
            // Simulate API call
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1500)
            
            // Placeholder: Set success
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    isOtpVerified = true
                )
            }
        }
    }
    
    private fun resendOtp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000)
            
            // Placeholder: Show success message
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    errorMessage = "Code resent successfully"
                )
            }
        }
    }
    
    private fun handleForgotPassword() {
        // Placeholder: Navigate to forgot password flow
        _uiState.update { 
            it.copy(errorMessage = "Forgot password functionality coming soon")
        }
    }
}

