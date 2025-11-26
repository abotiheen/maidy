package com.example.maidy.feature_maid.profile

/**
 * UI State for the Maid Profile/Settings screen
 */
data class MaidProfileUiState(
    val profileImageUri: String? = null,
    val fullName: String = "Loading...",
    val email: String = "",
    val phoneNumber: String = "+964 XXX XXX XXXX",
    val isDarkModeEnabled: Boolean = false,
    val areNotificationsEnabled: Boolean = true,
    val selectedLanguage: String = "English",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val shouldNavigateToAuth: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val toastMessage: String? = null
)

/**
 * Sealed class representing user actions/events in the Maid Profile screen
 */
sealed class MaidProfileEvent {
    data class UpdateProfileImage(val uri: String) : MaidProfileEvent()
    data class UpdateFullName(val name: String) : MaidProfileEvent()
    data class UpdatePhoneNumber(val phone: String) : MaidProfileEvent()
    data class ToggleDarkMode(val enabled: Boolean) : MaidProfileEvent()
    data class ToggleNotifications(val enabled: Boolean) : MaidProfileEvent()
    data class UpdateLanguage(val language: String) : MaidProfileEvent()
    data object LogOut : MaidProfileEvent()
}
