package com.example.maidy.feature_maid.edit_profile

/**
 * UI State for the Maid Edit Profile screen
 */
data class MaidEditProfileUiState(
    // Profile Image
    val selectedImageUri: String? = null,
    val isUploadingImage: Boolean = false,

    // Core Information
    val fullName: String = "",
    val bio: String = "",

    // Pricing
    val hourlyRate: String = "",

    // Services
    val selectedServices: List<String> = emptyList(),

    // Specialty Tag
    val specialtyTag: String = "",

    // UI State
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/**
 * Sealed class representing user actions/events
 */
sealed class MaidEditProfileEvent {
    data class ImageSelected(val uri: String) : MaidEditProfileEvent()
    data class FullNameChanged(val name: String) : MaidEditProfileEvent()
    data class BioChanged(val bio: String) : MaidEditProfileEvent()
    data class HourlyRateChanged(val rate: String) : MaidEditProfileEvent()
    data class ServiceToggled(val service: String) : MaidEditProfileEvent()
    data class SpecialtyTagChanged(val tag: String) : MaidEditProfileEvent()
    object SaveProfile : MaidEditProfileEvent()
    object ClearMessages : MaidEditProfileEvent()
}
