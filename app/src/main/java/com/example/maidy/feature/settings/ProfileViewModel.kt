package com.example.maidy.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.NotificationPreferencesManager
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import com.example.maidy.core.service.FcmTokenManager
import com.example.maidy.core.util.ImageCompressor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Profile/Settings screen
 * Manages user profile data and settings preferences
 * Follows unidirectional data flow pattern
 */
class ProfileViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val imageCompressor: ImageCompressor,
    private val notificationPreferences: NotificationPreferencesManager,
    private val fcmTokenManager: FcmTokenManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    /**
     * Handle user events from the UI
     */
    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.UpdateProfileImage -> updateProfileImage(event.uri)
            is ProfileEvent.UpdateFullName -> updateFullName(event.name)
            is ProfileEvent.UpdatePhoneNumber -> updatePhoneNumber(event.phone)
            is ProfileEvent.ToggleDarkMode -> toggleDarkMode(event.enabled)
            is ProfileEvent.ToggleNotifications -> toggleNotifications(event.enabled)
            is ProfileEvent.UpdateLanguage -> updateLanguage(event.language)
            is ProfileEvent.NavigateToBookingHistory -> navigateToBookingHistory()
            is ProfileEvent.NavigateToPaymentHistory -> navigateToPaymentHistory()
            is ProfileEvent.NavigateToHelpSupport -> navigateToHelpSupport()
            is ProfileEvent.LogOut -> logOut()
        }
    }
    
    private fun updateProfileImage(uriString: String) {
        viewModelScope.launch {
            println("🔵 ProfileViewModel: Starting image upload...")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val userId = sessionManager.getCurrentUserId()
            println("🔵 ProfileViewModel: User ID = $userId")
            
            if (userId == null) {
                println("❌ ProfileViewModel: User not logged in!")
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "User not logged in"
                    )
                }
                return@launch
            }
            
            try {
                val originalUri = Uri.parse(uriString)
                println("🔵 ProfileViewModel: Original image URI: $originalUri")
                
                // Compress image before upload
                println("🔵 ProfileViewModel: Compressing image...")
                val compressedUri = imageCompressor.compressImage(originalUri)
                
                if (compressedUri == null) {
                    println("⚠️ ProfileViewModel: Compression failed, using original image")
                    // Fall back to original if compression fails
                    uploadImage(userId, originalUri)
                } else {
                    println("🔵 ProfileViewModel: Using compressed image")
                    uploadImage(userId, compressedUri)
                }
                
            } catch (e: Exception) {
                println("❌ ProfileViewModel: Exception during upload: ${e.message}")
                e.printStackTrace()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error uploading image"
                    )
                }
            }
        }
    }
    
    private suspend fun uploadImage(userId: String, uri: Uri) {
        println("🔵 ProfileViewModel: Uploading image from URI: $uri")
        val result = userRepository.uploadAndUpdateProfileImage(userId, uri)
        
        if (result.isSuccess) {
            val imageUrl = result.getOrNull()!!
            println("✅ ProfileViewModel: Upload successful! URL: $imageUrl")
            _uiState.update { 
                it.copy(
                    profileImageUri = imageUrl,
                    isLoading = false
                )
            }
        } else {
            val error = result.exceptionOrNull()?.message ?: "Upload failed"
            println("❌ ProfileViewModel: Upload failed: $error")
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    errorMessage = error
                )
            }
        }
    }
    
    private fun updateFullName(name: String) {
        _uiState.update { it.copy(fullName = name) }
        // TODO: Save to backend
    }
    
    private fun updatePhoneNumber(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone) }
        // TODO: Save to backend
    }
    
    private fun toggleDarkMode(enabled: Boolean) {
        // Revert the toggle back to false and show "Coming Soon" message
        _uiState.update {
            it.copy(
                isDarkModeEnabled = false,
                toastMessage = "🌙 Dark Mode coming soon!"
            )
        }
    }

    /**
     * Clear toast message after it's been shown
     */
    fun onToastShown() {
        _uiState.update { it.copy(toastMessage = null) }
    }
    
    private fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            println("🔔 ProfileViewModel: Toggling notifications to: $enabled")

            // Update local preference
            notificationPreferences.setNotificationsEnabled(enabled)

            val userId = sessionManager.getCurrentUserId()

            if (userId != null) {
                if (enabled) {
                    // Enable notifications: Register FCM token
                    println("🔔 ProfileViewModel: Enabling notifications - registering FCM token")
                    val result = fcmTokenManager.refreshToken(userId)

                    if (result.isSuccess) {
                        println("✅ ProfileViewModel: FCM token registered successfully")
                        _uiState.update {
                            it.copy(
                                areNotificationsEnabled = true,
                                toastMessage = "🔔 Notifications enabled"
                            )
                        }
                    } else {
                        println("❌ ProfileViewModel: Failed to register FCM token")
                        // Still update UI but show warning
                        _uiState.update {
                            it.copy(
                                areNotificationsEnabled = true,
                                toastMessage = "⚠️ Notifications enabled (token registration pending)"
                            )
                        }
                    }
                } else {
                    // Disable notifications: Clear FCM token
                    println("🔔 ProfileViewModel: Disabling notifications - clearing FCM token")
                    val result = fcmTokenManager.deleteToken(userId)

                    if (result.isSuccess) {
                        println("✅ ProfileViewModel: FCM token cleared successfully")
                        _uiState.update {
                            it.copy(
                                areNotificationsEnabled = false,
                                toastMessage = "🔕 Notifications disabled"
                            )
                        }
                    } else {
                        println("❌ ProfileViewModel: Failed to clear FCM token")
                        // Still update UI
                        _uiState.update {
                            it.copy(
                                areNotificationsEnabled = false,
                                toastMessage = "🔕 Notifications disabled"
                            )
                        }
                    }
                }
            } else {
                // No user logged in, just update local preference
                _uiState.update {
                    it.copy(
                        areNotificationsEnabled = enabled,
                        toastMessage = if (enabled) "🔔 Notifications enabled" else "🔕 Notifications disabled"
                    )
                }
            }
        }
    }
    
    private fun updateLanguage(language: String) {
        _uiState.update { it.copy(selectedLanguage = language) }
        // TODO: Update app language
    }
    
    private fun navigateToBookingHistory() {
        // TODO: Trigger navigation to booking history
    }
    
    private fun navigateToPaymentHistory() {
        // TODO: Trigger navigation to payment history
    }
    
    private fun navigateToHelpSupport() {
        // TODO: Trigger navigation to help & support
    }
    
    private fun logOut() {
        // Show logout confirmation dialog
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    /**
     * Confirm logout - actually perform the logout
     */
    fun onConfirmLogout() {
        viewModelScope.launch {
            println("🚪 ProfileViewModel: Logging out user...")
            _uiState.update { it.copy(showLogoutDialog = false, isLoading = true) }

            try {
                // Clear user session
                sessionManager.clearSession()
                println("✅ ProfileViewModel: Session cleared successfully")

                // Trigger navigation to auth screen
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        shouldNavigateToAuth = true
                    )
                }
            } catch (e: Exception) {
                println("❌ ProfileViewModel: Logout failed - ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to logout: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Dismiss logout dialog
     */
    fun onDismissLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    /**
     * Reset navigation flag after navigation is handled
     */
    fun onLogoutNavigationHandled() {
        _uiState.update { it.copy(shouldNavigateToAuth = false) }
    }
    
    /**
     * Load user profile data
     * Called when screen is initialized
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            println("🔵 ProfileViewModel: Loading user profile...")
            _uiState.update { it.copy(isLoading = true) }
            
            val userId = sessionManager.getCurrentUserId()
            println("🔵 ProfileViewModel: User ID = $userId")
            
            if (userId == null) {
                println("❌ ProfileViewModel: User not logged in!")
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "User not logged in"
                    )
                }
                return@launch
            }
            
            try {
                val result = userRepository.getUserById(userId)
                
                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    println("✅ ProfileViewModel: User loaded - Name: ${user.fullName}, Image: ${user.profileImageUrl}")

                    // Load notification preference from local storage
                    val notificationsEnabled = notificationPreferences.areNotificationsEnabled()
                    println("🔔 ProfileViewModel: Notifications enabled: $notificationsEnabled")

                    _uiState.update {
                        it.copy(
                            fullName = user.fullName,
                            phoneNumber = user.phoneNumber,
                            profileImageUri = user.profileImageUrl,
                            email = user.phoneNumber, // Using phone as email for now
                            areNotificationsEnabled = notificationsEnabled,
                            isLoading = false
                        )
                    }
                } else {
                    val error = result.exceptionOrNull()?.message
                    println("❌ ProfileViewModel: Failed to load user: $error")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = error
                        )
                    }
                }
            } catch (e: Exception) {
                println("❌ ProfileViewModel: Exception loading user: ${e.message}")
                e.printStackTrace()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }
}

