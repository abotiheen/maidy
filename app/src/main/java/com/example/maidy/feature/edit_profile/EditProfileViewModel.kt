package com.example.maidy.feature.edit_profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import com.example.maidy.core.util.ImageCompressor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val profileImageUri: String? = null,
    val fullName: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val profileSaved: Boolean = false
)

sealed class EditProfileEvent {
    data class UpdateProfileImage(val uri: String) : EditProfileEvent()
    data class UpdateFullName(val name: String) : EditProfileEvent()
    object SaveProfile : EditProfileEvent()
    object DismissError : EditProfileEvent()
}

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            println("🔄 EditProfileViewModel: Loading user profile...")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val userId = sessionManager.getCurrentUserId()
            if (userId == null) {
                println("❌ EditProfileViewModel: User not logged in!")
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
                    println("✅ EditProfileViewModel: User loaded - Name: ${user.fullName}")
                    _uiState.update {
                        it.copy(
                            fullName = user.fullName,
                            profileImageUri = user.profileImageUrl,
                            isLoading = false
                        )
                    }
                } else {
                    val error = result.exceptionOrNull()?.message
                    println("❌ EditProfileViewModel: Failed to load user: $error")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error
                        )
                    }
                }
            } catch (e: Exception) {
                println("❌ EditProfileViewModel: Exception loading user: ${e.message}")
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

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.UpdateProfileImage -> {
                _uiState.update { it.copy(profileImageUri = event.uri) }
            }

            is EditProfileEvent.UpdateFullName -> {
                _uiState.update { it.copy(fullName = event.name) }
            }

            is EditProfileEvent.SaveProfile -> {
                saveProfile()
            }

            is EditProfileEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Validation
            if (currentState.fullName.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Please enter your full name") }
                return@launch
            }

            println("💾 EditProfileViewModel: Saving profile...")
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val userId = sessionManager.getCurrentUserId()
            if (userId == null) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "User not logged in"
                    )
                }
                return@launch
            }

            try {
                // 1. Update full name in Firestore
                println("💾 EditProfileViewModel: Updating name to: ${currentState.fullName}")
                val nameUpdateResult = updateUserName(userId, currentState.fullName)

                if (nameUpdateResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Failed to update name: ${nameUpdateResult.exceptionOrNull()?.message}"
                        )
                    }
                    return@launch
                }

                // 2. Upload new profile image if changed
                val originalImageUri = _uiState.value.profileImageUri

                // Check if it's a new local URI (starts with content:// or file://)
                if (originalImageUri?.startsWith("content://") == true ||
                    originalImageUri?.startsWith("file://") == true
                ) {

                    println("💾 EditProfileViewModel: Uploading new profile image...")

                    val uri = Uri.parse(originalImageUri)

                    // Compress image before upload
                    val compressedUri = imageCompressor.compressImage(uri) ?: uri

                    val uploadResult =
                        userRepository.uploadAndUpdateProfileImage(userId, compressedUri)

                    if (uploadResult.isFailure) {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                errorMessage = "Failed to upload image: ${uploadResult.exceptionOrNull()?.message}"
                            )
                        }
                        return@launch
                    }

                    println("✅ EditProfileViewModel: Image uploaded successfully")
                }

                // Success
                println("✅ EditProfileViewModel: Profile saved successfully")
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        profileSaved = true
                    )
                }
            } catch (e: Exception) {
                println("❌ EditProfileViewModel: Exception saving profile: ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Failed to save profile: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun updateUserName(userId: String, newName: String): Result<Unit> {
        return try {
            // Use Firestore update method
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update("fullName", newName)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
