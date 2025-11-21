package com.example.maidy.feature.sos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EmergencyUiState(
    val showConfirmationDialog: Boolean = false,
    val isEmergencyTriggered: Boolean = false,
    val emergencyContactNumber: String = "911",
    val userLocation: String = "", // Placeholder for location data
    val errorMessage: String? = null,
    val shouldOpenDialer: String? = null // Phone number to dial, null when not needed
)

sealed class EmergencyUiEvent {
    object OnCallForHelpClick : EmergencyUiEvent()
    object OnCancelClick : EmergencyUiEvent()
    object OnConfirmEmergency : EmergencyUiEvent()
    object OnDismissError : EmergencyUiEvent()
}

class EmergencyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    fun onEvent(event: EmergencyUiEvent) {
        when (event) {
            is EmergencyUiEvent.OnCallForHelpClick -> {
                // Open dialer with emergency number (911)
                val emergencyNumber = _uiState.value.emergencyContactNumber
                _uiState.update { it.copy(shouldOpenDialer = emergencyNumber) }
            }

            is EmergencyUiEvent.OnCancelClick -> {
                // Cancel and go back to previous screen
                // Navigation will be handled by the screen
                _uiState.update { it.copy(showConfirmationDialog = false) }
            }

            is EmergencyUiEvent.OnConfirmEmergency -> {
                _uiState.update { it.copy(showConfirmationDialog = true) }
            }

            is EmergencyUiEvent.OnDismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    fun onDialerHandled() {
        _uiState.update { it.copy(shouldOpenDialer = null) }
    }

    private fun triggerEmergency() {
        // Placeholder for emergency trigger logic
        // In a real app, this would:
        // 1. Get user's current location
        // 2. Send location to emergency contacts and services
        // 3. Send notification to support team
        // 4. Possibly trigger phone call to emergency services
        // 5. Log the emergency event
        
        _uiState.update { 
            it.copy(isEmergencyTriggered = true) 
        }
    }

    fun resetEmergencyState() {
        _uiState.update { EmergencyUiState() }
    }
}

