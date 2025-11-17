package com.example.maidy.feature.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.AuthRepository
import com.example.maidy.core.data.OtpState
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import com.example.maidy.core.model.User
import com.google.firebase.auth.PhoneAuthProvider
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
    val passwordVisible: Boolean = false,
    val fullName: String = "",
    val otpCode: String = "",
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
    data class SendOtpForRegistration(val activity: Activity) : AuthEvent()
    object VerifyOtpClicked : AuthEvent()
    object ResendCodeClicked : AuthEvent()
    data class NavigateToScreen(val screen: AuthScreen) : AuthEvent()
    object ClearError : AuthEvent()
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    
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
            is AuthEvent.SendOtpForRegistration -> {
                sendOtpForRegistration(event.activity)
            }
            AuthEvent.VerifyOtpClicked -> {
                verifyOtp()
            }
            AuthEvent.ResendCodeClicked -> {
                // TODO: Implement resend with token
            }
            is AuthEvent.NavigateToScreen -> {
                _uiState.update { it.copy(currentScreen = event.screen) }
            }
            AuthEvent.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }
    
    // Login with phone + password (NO OTP)
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
            
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Format phone number
            val formattedPhone = if (!_uiState.value.phoneNumber.startsWith("+")) {
                "+964${_uiState.value.phoneNumber}"
            } else {
                _uiState.value.phoneNumber
            }
            
            // Query Firestore for user with phone + password
            val result = userRepository.loginWithPassword(
                phoneNumber = formattedPhone,
                password = _uiState.value.password
            )
            
            result.onSuccess { user ->
                // Save session
                sessionManager.saveUserId(user.id)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        errorMessage = null
                    )
                }
                println("✅ Login successful! User: ${user.fullName}")
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed"
                    )
                }
                println("❌ Login failed: ${error.message}")
            }
        }
    }
    
    // Registration: Send OTP to verify phone number
    private fun sendOtpForRegistration(activity: Activity) {
        viewModelScope.launch {
            // Validate all fields including password
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
            if (_uiState.value.password.length < 6) {
                _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
                return@launch
            }
            
            // Format phone number
            val formattedPhone = if (!_uiState.value.phoneNumber.startsWith("+")) {
                "+964${_uiState.value.phoneNumber}"
            } else {
                _uiState.value.phoneNumber
            }
            
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Send OTP to verify phone
            authRepository.sendOtp(formattedPhone, activity).collect { state ->
                when (state) {
                    is OtpState.CodeSent -> {
                        verificationId = state.verificationId
                        resendToken = state.token
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                currentScreen = AuthScreen.OTP_VERIFICATION,
                                phoneNumber = formattedPhone
                            )
                        }
                        println("✅ OTP sent to $formattedPhone")
                    }
                    is OtpState.AutoVerified -> {
                        // Rare: auto-verification happened
                        handleAutoVerification(state.credential)
                    }
                    is OtpState.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = state.message
                            )
                        }
                        println("❌ OTP send failed: ${state.message}")
                    }
                }
            }
        }
    }
    
    // Verify OTP code during registration
    private fun verifyOtp() {
        viewModelScope.launch {
            val otpCode = _uiState.value.otpCode.trim()
            val verificationId = this@AuthViewModel.verificationId
            
            if (verificationId == null) {
                _uiState.update { it.copy(errorMessage = "Please request OTP first") }
                return@launch
            }
            
            if (otpCode.length != 6) {
                _uiState.update { it.copy(errorMessage = "Please enter 6-digit code") }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = authRepository.verifyOtp(verificationId, otpCode)
            
            result.onSuccess { userId ->
                handleRegistrationSuccess(userId)
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Invalid code: ${error.message}"
                    )
                }
                println("❌ OTP verification failed: ${error.message}")
            }
        }
    }
    
    private fun handleAutoVerification(credential: com.google.firebase.auth.PhoneAuthCredential) {
        viewModelScope.launch {
            val result = authRepository.signInWithCredential(credential)
            result.onSuccess { userId ->
                handleRegistrationSuccess(userId)
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }
    
    // After OTP verified, create user profile with password
    private suspend fun handleRegistrationSuccess(userId: String) {
        // Save user ID to session
        sessionManager.saveUserId(userId)
        
        // Create user profile with password
        val newUser = User(
            id = userId,
            fullName = _uiState.value.fullName,
            phoneNumber = _uiState.value.phoneNumber,
            password = _uiState.value.password,  // Store password
            phoneVerified = true,  // Mark phone as verified with OTP
            createdAt = System.currentTimeMillis(),
            role = "customer"
        )
        
        userRepository.createUserProfile(userId, newUser)
        
        _uiState.update { 
            it.copy(
                isLoading = false,
                isOtpVerified = true,
                errorMessage = null
            )
        }
        println("✅ Registration successful! User ID: $userId, Name: ${newUser.fullName}")
    }
}
