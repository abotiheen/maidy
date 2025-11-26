package com.example.maidy.feature_maid.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.AuthRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.OtpState
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.model.Maid
import com.example.maidy.core.service.FcmTokenManager
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI State
data class MaidAuthUiState(
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
    val currentScreen: MaidAuthScreen = MaidAuthScreen.LOGIN
)

enum class MaidAuthScreen {
    LOGIN, REGISTER, OTP_VERIFICATION
}

// UI Events
sealed class MaidAuthEvent {
    data class PhoneNumberChanged(val phone: String) : MaidAuthEvent()
    data class PasswordChanged(val password: String) : MaidAuthEvent()
    data class FullNameChanged(val name: String) : MaidAuthEvent()
    data class OtpCodeChanged(val code: String) : MaidAuthEvent()
    object TogglePasswordVisibility : MaidAuthEvent()
    object LoginClicked : MaidAuthEvent()
    data class SendOtpForRegistration(val activity: Activity) : MaidAuthEvent()
    object VerifyOtpClicked : MaidAuthEvent()
    object ResendCodeClicked : MaidAuthEvent()
    data class NavigateToScreen(val screen: MaidAuthScreen) : MaidAuthEvent()
    object ClearError : MaidAuthEvent()
}

class MaidAuthViewModel(
    private val authRepository: AuthRepository,
    private val maidRepository: MaidRepository,
    private val sessionManager: SessionManager,
    private val fcmTokenManager: FcmTokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaidAuthUiState())
    val uiState: StateFlow<MaidAuthUiState> = _uiState.asStateFlow()

    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun onEvent(event: MaidAuthEvent) {
        when (event) {
            is MaidAuthEvent.PhoneNumberChanged -> {
                _uiState.update { it.copy(phoneNumber = event.phone) }
            }

            is MaidAuthEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }

            is MaidAuthEvent.FullNameChanged -> {
                _uiState.update { it.copy(fullName = event.name) }
            }

            is MaidAuthEvent.OtpCodeChanged -> {
                _uiState.update { it.copy(otpCode = event.code) }
            }

            MaidAuthEvent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
            }

            MaidAuthEvent.LoginClicked -> {
                performLogin()
            }

            is MaidAuthEvent.SendOtpForRegistration -> {
                sendOtpForRegistration(event.activity)
            }

            MaidAuthEvent.VerifyOtpClicked -> {
                verifyOtp()
            }

            MaidAuthEvent.ResendCodeClicked -> {
                // TODO: Implement resend with token
            }

            is MaidAuthEvent.NavigateToScreen -> {
                _uiState.update { it.copy(currentScreen = event.screen) }
            }

            MaidAuthEvent.ClearError -> {
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

            // Query Firestore for maid with phone + password
            val result = maidRepository.loginWithPassword(
                phoneNumber = formattedPhone,
                password = _uiState.value.password
            )

            result.onSuccess { maid ->
                // Check if already signed in to Firebase Auth (from registration)
                val currentFirebaseUser = authRepository.getCurrentUserId()
                println("🔵 MaidAuthViewModel: Password verified! Firebase Auth UID: $currentFirebaseUser")

                // Save session
                sessionManager.saveUserId(maid.id)

                // Register FCM token for notifications (maid collection)
                viewModelScope.launch {
                    fcmTokenManager.refreshToken(maid.id, isCustomer = false)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        errorMessage = null
                    )
                }

                if (currentFirebaseUser == null) {
                    println("⚠️ MaidAuthViewModel: WARNING - Not signed into Firebase Auth! Storage uploads won't work.")
                    println("⚠️ Maid needs to re-register to use image upload features.")
                } else {
                    println("✅ Login successful! Maid: ${maid.fullName}, Firebase UID: $currentFirebaseUser")
                }
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
                                currentScreen = MaidAuthScreen.OTP_VERIFICATION,
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
            val verificationId = this@MaidAuthViewModel.verificationId

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

    // After OTP verified, create maid profile with password
    private suspend fun handleRegistrationSuccess(userId: String) {
        // Save user ID to session
        sessionManager.saveUserId(userId)

        // Create maid profile with password
        val newMaid = Maid(
            id = userId,
            fullName = _uiState.value.fullName,
            phoneNumber = _uiState.value.phoneNumber,
            password = _uiState.value.password,  // Store password
            phoneVerified = true,  // Mark phone as verified with OTP
            createdAt = System.currentTimeMillis(),
            bio = "",
            verified = false,
            averageRating = 0.0,
            reviewCount = 0,
            services = emptyList(),
            specialtyTag = "",
            hourlyRate = 0.0,
            available = true
        )

        maidRepository.createMaidProfile(userId, newMaid)

        // Register FCM token for notifications (maid collection)
        fcmTokenManager.refreshToken(userId, isCustomer = false)

        _uiState.update {
            it.copy(
                isLoading = false,
                isOtpVerified = true,
                errorMessage = null
            )
        }
        println("✅ Maid Registration successful! User ID: $userId, Name: ${newMaid.fullName}")
    }
}
