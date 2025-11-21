package com.example.maidy.feature.settings

/**
 * UI State for the Profile/Settings screen
 */
data class ProfileUiState(
    val profileImageUri: String? = null,
    val fullName: String = "Amelia Kristensen",
    val email: String = "amelia.kris@email.com",
    val phoneNumber: String = "+1 123-456-7890",
    val isDarkModeEnabled: Boolean = false,
    val areNotificationsEnabled: Boolean = true,
    val selectedLanguage: String = "English",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val shouldNavigateToAuth: Boolean = false,
    val showLogoutDialog: Boolean = false
)

/**
 * Sealed class representing user actions/events in the Profile screen
 */
sealed class ProfileEvent {
    data class UpdateProfileImage(val uri: String) : ProfileEvent()
    data class UpdateFullName(val name: String) : ProfileEvent()
    data class UpdatePhoneNumber(val phone: String) : ProfileEvent()
    data class ToggleDarkMode(val enabled: Boolean) : ProfileEvent()
    data class ToggleNotifications(val enabled: Boolean) : ProfileEvent()
    data class UpdateLanguage(val language: String) : ProfileEvent()
    data object NavigateToBookingHistory : ProfileEvent()
    data object NavigateToPaymentHistory : ProfileEvent()
    data object NavigateToHelpSupport : ProfileEvent()
    data object LogOut : ProfileEvent()
}



