package com.example.maidy.feature_maid.edit_profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.util.ImageCompressor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel for Maid Edit Profile screen
 */
class MaidEditProfileViewModel(
    private val maidRepository: MaidRepository,
    private val sessionManager: SessionManager,
    private val imageCompressor: ImageCompressor,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaidEditProfileUiState())
    val uiState: StateFlow<MaidEditProfileUiState> = _uiState.asStateFlow()

    init {
        loadMaidProfile()
    }

    fun onEvent(event: MaidEditProfileEvent) {
        when (event) {
            is MaidEditProfileEvent.ImageSelected -> onImageSelected(event.uri)
            is MaidEditProfileEvent.FullNameChanged -> {
                _uiState.update { it.copy(fullName = event.name) }
            }

            is MaidEditProfileEvent.BioChanged -> {
                _uiState.update { it.copy(bio = event.bio) }
            }

            is MaidEditProfileEvent.HourlyRateChanged -> {
                _uiState.update { it.copy(hourlyRate = event.rate) }
            }

            is MaidEditProfileEvent.ServiceToggled -> toggleService(event.service)
            is MaidEditProfileEvent.SpecialtyTagChanged -> {
                _uiState.update { it.copy(specialtyTag = event.tag) }
            }

            MaidEditProfileEvent.SaveProfile -> saveProfile()
            MaidEditProfileEvent.ClearMessages -> {
                _uiState.update { it.copy(errorMessage = null, successMessage = null) }
            }
        }
    }

    private fun loadMaidProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val maidId = sessionManager.getCurrentUserId()
            if (maidId == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Not logged in"
                    )
                }
                return@launch
            }

            val result = maidRepository.getMaidById(maidId)
            if (result.isSuccess) {
                val maid = result.getOrNull()!!
                _uiState.update {
                    it.copy(
                        fullName = maid.fullName,
                        bio = maid.bio,
                        hourlyRate = maid.hourlyRate.toString(),
                        selectedServices = maid.services,
                        specialtyTag = maid.specialtyTag,
                        selectedImageUri = maid.profileImageUrl,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load profile"
                    )
                }
            }
        }
    }

    private fun onImageSelected(uriString: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingImage = true, errorMessage = null) }

            val maidId = sessionManager.getCurrentUserId()
            if (maidId == null) {
                _uiState.update {
                    it.copy(
                        isUploadingImage = false,
                        errorMessage = "Not logged in"
                    )
                }
                return@launch
            }

            try {
                val originalUri = Uri.parse(uriString)

                // Compress image
                val compressedUri = imageCompressor.compressImage(originalUri)
                val imageUri = compressedUri ?: originalUri

                // Upload image
                val result = maidRepository.uploadMaidProfileImage(maidId, imageUri)

                if (result.isSuccess) {
                    val imageUrl = result.getOrNull()!!
                    _uiState.update {
                        it.copy(
                            selectedImageUri = imageUrl,
                            isUploadingImage = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isUploadingImage = false,
                            errorMessage = "Failed to upload image"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUploadingImage = false,
                        errorMessage = e.message ?: "Error uploading image"
                    )
                }
            }
        }
    }

    private fun toggleService(service: String) {
        _uiState.update {
            val currentServices = it.selectedServices.toMutableList()
            if (service in currentServices) {
                currentServices.remove(service)
            } else {
                currentServices.add(service)
            }
            it.copy(selectedServices = currentServices)
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            // Validate inputs
            if (_uiState.value.fullName.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Please enter your full name") }
                return@launch
            }
            if (_uiState.value.bio.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Please enter a bio") }
                return@launch
            }
            if (_uiState.value.hourlyRate.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Please enter hourly rate") }
                return@launch
            }
            if (_uiState.value.selectedServices.isEmpty()) {
                _uiState.update { it.copy(errorMessage = "Please select at least one service") }
                return@launch
            }
            if (_uiState.value.specialtyTag.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Please select a specialty tag") }
                return@launch
            }

            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val maidId = sessionManager.getCurrentUserId()
            if (maidId == null) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Not logged in"
                    )
                }
                return@launch
            }

            try {
                val hourlyRate = _uiState.value.hourlyRate.toDoubleOrNull()
                if (hourlyRate == null || hourlyRate <= 0) {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Invalid hourly rate"
                        )
                    }
                    return@launch
                }

                // Update Firestore
                val updates = hashMapOf<String, Any>(
                    "fullName" to _uiState.value.fullName,
                    "bio" to _uiState.value.bio,
                    "hourlyRate" to hourlyRate,
                    "services" to _uiState.value.selectedServices,
                    "specialtyTag" to _uiState.value.specialtyTag
                )

                // Add profile image URL if changed
                _uiState.value.selectedImageUri?.let {
                    updates["profileImageUrl"] = it
                }

                firestore.collection("maids")
                    .document(maidId)
                    .update(updates)
                    .await()

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Profile updated successfully!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Failed to save profile: ${e.message}"
                    )
                }
            }
        }
    }
}
