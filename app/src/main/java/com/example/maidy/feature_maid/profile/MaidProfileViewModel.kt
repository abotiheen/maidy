package com.example.maidy.feature_maid.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.NotificationPreferencesManager
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.service.FcmTokenManager
import com.example.maidy.core.util.ImageCompressor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Maid Profile/Settings screen
 * Manages maid profile data and settings preferences
 * Follows unidirectional data flow pattern
 */
class MaidProfileViewModel(
    private val maidRepository: MaidRepository,
    private val sessionManager: SessionManager,
    private val imageCompressor: ImageCompressor,
    private val notificationPreferences: NotificationPreferencesManager,
    private val fcmTokenManager: FcmTokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaidProfileUiState())
    val uiState: StateFlow<MaidProfileUiState> = _uiState.asStateFlow()

    /**
     * Handle user events from the UI
     */
    fun onEvent(event: MaidProfileEvent) {
        when (event) {
            is MaidProfileEvent.UpdateProfileImage -> updateProfileImage(event.uri)
            is MaidProfileEvent.UpdateFullName -> updateFullName(event.name)
            is MaidProfileEvent.UpdatePhoneNumber -> updatePhoneNumber(event.phone)
            is MaidProfileEvent.ToggleDarkMode -> toggleDarkMode(event.enabled)
            is MaidProfileEvent.ToggleNotifications -> toggleNotifications(event.enabled)
            is MaidProfileEvent.UpdateLanguage -> updateLanguage(event.language)
            is MaidProfileEvent.LogOut -> logOut()
        }
    }

    private fun updateProfileImage(uriString: String) {
        viewModelScope.launch {
            println("🔵 MaidProfileViewModel: Starting image upload...")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val maidId = sessionManager.getCurrentUserId()
            println("🔵 MaidProfileViewModel: Maid ID = $maidId")

            if (maidId == null) {
                println("❌ MaidProfileViewModel: Maid not logged in!")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Maid not logged in"
                    )
                }
                return@launch
            }

            try {
                val originalUri = Uri.parse(uriString)
                println("🔵 MaidProfileViewModel: Original image URI: $originalUri")

                // Compress image before upload
                println("🔵 MaidProfileViewModel: Compressing image...")
                val compressedUri = imageCompressor.compressImage(originalUri)

                if (compressedUri == null) {
                    println("⚠️ MaidProfileViewModel: Compression failed, using original image")
                    // Fall back to original if compression fails
                    uploadImage(maidId, originalUri)
                } else {
                    println("🔵 MaidProfileViewModel: Using compressed image")
                    uploadImage(maidId, compressedUri)
                }

            } catch (e: Exception) {
                println("❌ MaidProfileViewModel: Exception during upload: ${e.message}")
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

    private suspend fun uploadImage(maidId: String, uri: Uri) {
        println("🔵 MaidProfileViewModel: Uploading image from URI: $uri")
        val result = maidRepository.uploadAndUpdateProfileImage(maidId, uri)

        if (result.isSuccess) {
            val imageUrl = result.getOrNull()!!
            println("✅ MaidProfileViewModel: Upload successful! URL: $imageUrl")
            _uiState.update {
                it.copy(
                    profileImageUri = imageUrl,
                    isLoading = false
                )
            }
        } else {
            val error = result.exceptionOrNull()?.message ?: "Upload failed"
            println("❌ MaidProfileViewModel: Upload failed: $error")
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
            println("🔔 MaidProfileViewModel: Toggling notifications to: $enabled")

            // Update local preference
            notificationPreferences.setNotificationsEnabled(enabled)

            val maidId = sessionManager.getCurrentUserId()

            if (maidId != null) {
                if (enabled) {
                    // Enable notifications: Register FCM token (maid collection)
                    println("🔔 MaidProfileViewModel: Enabling notifications - registering FCM token")
                    val result = fcmTokenManager.refreshToken(maidId, isCustomer = false)

                    if (result.isSuccess) {
                        println("✅ MaidProfileViewModel: FCM token registered successfully")
                        _uiState.update {
                            it.copy(
                                areNotificationsEnabled = true,
                                toastMessage = "🔔 Notifications enabled"
                            )
                        }
                    } else {
                        println("❌ MaidProfileViewModel: Failed to register FCM token")
                        // Still update UI but show warning
                        _uiState.update {
                            it.copy(
                                areNotificationsEnabled = true,
                                toastMessage = "⚠️ Notifications enabled (token registration pending)"
                            )
                        }
                    }
                } else {
                    // Disable notifications: Clear FCM token (maid collection)
                    println("🔔 MaidProfileViewModel: Disabling notifications - clearing FCM token")
                    val result = fcmTokenManager.deleteToken(maidId, isCustomer = false)

                    if (result.isSuccess) {
                        println("✅ MaidProfileViewModel: FCM token cleared successfully")
                        _uiState.update {
                            it.copy(
                                areNotificationsEnabled = false,
                                toastMessage = "🔕 Notifications disabled"
                            )
                        }
                    } else {
                        println("❌ MaidProfileViewModel: Failed to clear FCM token")
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
                // No maid logged in, just update local preference
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

    private fun logOut() {
        // Show logout confirmation dialog
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    /**
     * Confirm logout - actually perform the logout
     */
    fun onConfirmLogout() {
        viewModelScope.launch {
            println("🚪 MaidProfileViewModel: Logging out maid...")
            _uiState.update { it.copy(showLogoutDialog = false, isLoading = true) }

            try {
                // Clear maid session
                sessionManager.clearSession()
                println("✅ MaidProfileViewModel: Session cleared successfully")

                // Trigger navigation to auth screen
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        shouldNavigateToAuth = true
                    )
                }
            } catch (e: Exception) {
                println("❌ MaidProfileViewModel: Logout failed - ${e.message}")
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
     * Load maid profile data
     * Called when screen is initialized
     */
    fun loadMaidProfile() {
        viewModelScope.launch {
            println("🔵 MaidProfileViewModel: Loading maid profile...")
            _uiState.update { it.copy(isLoading = true) }

            val maidId = sessionManager.getCurrentUserId()
            println("🔵 MaidProfileViewModel: Maid ID = $maidId")

            if (maidId == null) {
                println("❌ MaidProfileViewModel: Maid not logged in!")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Maid not logged in"
                    )
                }
                return@launch
            }

            try {
                val result = maidRepository.getMaidById(maidId)

                if (result.isSuccess) {
                    val maid = result.getOrNull()!!
                    println("✅ MaidProfileViewModel: Maid loaded - Name: ${maid.fullName}, Image: ${maid.profileImageUrl}")

                    // Load notification preference from local storage
                    val notificationsEnabled = notificationPreferences.areNotificationsEnabled()
                    println("🔔 MaidProfileViewModel: Notifications enabled: $notificationsEnabled")

                    _uiState.update {
                        it.copy(
                            fullName = maid.fullName,
                            phoneNumber = maid.phoneNumber,
                            profileImageUri = maid.profileImageUrl,
                            email = maid.phoneNumber, // Using phone as email for now
                            areNotificationsEnabled = notificationsEnabled,
                            isLoading = false
                        )
                    }
                } else {
                    val error = result.exceptionOrNull()?.message
                    println("❌ MaidProfileViewModel: Failed to load maid: $error")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error
                        )
                    }
                }
            } catch (e: Exception) {
                println("❌ MaidProfileViewModel: Exception loading maid: ${e.message}")
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
