package com.example.maidy.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class ProfileViewModel : ViewModel() {
    
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
    
    private fun updateProfileImage(uri: String) {
        _uiState.update { it.copy(profileImageUri = uri) }
        // TODO: Upload image to backend
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
        _uiState.update { it.copy(isDarkModeEnabled = enabled) }
        // TODO: Persist preference and apply theme
    }
    
    private fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(areNotificationsEnabled = enabled) }
        // TODO: Update notification preferences
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Clear user session and navigate to auth
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    
    /**
     * Load user profile data
     * Called when screen is initialized
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // TODO: Fetch user data from repository
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
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

