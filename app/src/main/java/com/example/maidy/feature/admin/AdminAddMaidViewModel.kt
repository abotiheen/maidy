package com.example.maidy.feature.admin

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.model.Maid
import com.example.maidy.core.model.MaidReview
import com.example.maidy.core.util.ImageCompressor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AdminAddMaidUiState(
    val fullName: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val hourlyRate: String = "",
    val selectedImageUri: Uri? = null,
    val uploadedImageUrl: String = "",
    val selectedServices: Set<String> = emptySet(),
    val specialtyTag: String = "",
    val verified: Boolean = false,
    val phoneVerified: Boolean = false,
    val available: Boolean = true,
    val reviews: List<MaidReview> = emptyList(),
    val isLoading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    
    // For adding reviews
    val reviewerName: String = "",
    val reviewRating: Int = 5,
    val reviewComment: String = "",
    val reviewDate: String = ""
)

class AdminAddMaidViewModel(
    private val maidRepository: MaidRepository,
    private val imageCompressor: ImageCompressor
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AdminAddMaidUiState())
    val uiState: StateFlow<AdminAddMaidUiState> = _uiState.asStateFlow()
    
    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value) }
    }
    
    fun onPhoneNumberChange(value: String) {
        _uiState.update { it.copy(phoneNumber = value) }
    }
    
    fun onBioChange(value: String) {
        _uiState.update { it.copy(bio = value) }
    }
    
    fun onHourlyRateChange(value: String) {
        _uiState.update { it.copy(hourlyRate = value) }
    }
    
    fun onImageSelected(uriString: String) {
        val uri = Uri.parse(uriString)
        _uiState.update { it.copy(selectedImageUri = uri) }
    }
    
    fun onServiceToggle(service: String) {
        _uiState.update { state ->
            val newServices = if (service in state.selectedServices) {
                state.selectedServices - service
            } else {
                state.selectedServices + service
            }
            state.copy(selectedServices = newServices)
        }
    }
    
    fun onSpecialtyTagChange(tag: String) {
        _uiState.update { it.copy(specialtyTag = tag) }
    }
    
    fun onVerifiedChange(value: Boolean) {
        _uiState.update { it.copy(verified = value) }
    }
    
    fun onPhoneVerifiedChange(value: Boolean) {
        _uiState.update { it.copy(phoneVerified = value) }
    }
    
    fun onAvailableChange(value: Boolean) {
        _uiState.update { it.copy(available = value) }
    }
    
    // Review form handlers
    fun onReviewerNameChange(value: String) {
        _uiState.update { it.copy(reviewerName = value) }
    }
    
    fun onReviewRatingChange(value: Int) {
        _uiState.update { it.copy(reviewRating = value) }
    }
    
    fun onReviewCommentChange(value: String) {
        _uiState.update { it.copy(reviewComment = value) }
    }
    
    fun onReviewDateChange(value: String) {
        _uiState.update { it.copy(reviewDate = value) }
    }
    
    fun addReview() {
        val state = _uiState.value
        
        // Validate review
        if (state.reviewerName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Reviewer name is required") }
            return
        }
        
        if (state.reviewComment.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Review comment is required") }
            return
        }
        
        if (state.reviewDate.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Review date is required") }
            return
        }
        
        // Check max 2 reviews
        if (state.reviews.size >= 2) {
            _uiState.update { it.copy(errorMessage = "Maximum 2 reviews allowed") }
            return
        }
        
        val newReview = MaidReview(
            id = UUID.randomUUID().toString(),
            reviewerName = state.reviewerName,
            rating = state.reviewRating,
            comment = state.reviewComment,
            date = state.reviewDate,
            timestamp = System.currentTimeMillis()
        )
        
        _uiState.update { 
            it.copy(
                reviews = it.reviews + newReview,
                // Clear review form
                reviewerName = "",
                reviewRating = 5,
                reviewComment = "",
                reviewDate = "",
                successMessage = "Review added successfully"
            )
        }
    }
    
    fun removeReview(reviewId: String) {
        _uiState.update { state ->
            state.copy(reviews = state.reviews.filter { it.id != reviewId })
        }
    }
    
    fun submitMaid() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // Validate required fields
            if (state.fullName.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Full name is required") }
                return@launch
            }
            
            if (state.phoneNumber.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Phone number is required") }
                return@launch
            }
            
            if (state.bio.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Bio is required") }
                return@launch
            }
            
            if (state.hourlyRate.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Hourly rate is required") }
                return@launch
            }
            
            val hourlyRateDouble = state.hourlyRate.toDoubleOrNull()
            if (hourlyRateDouble == null || hourlyRateDouble <= 0) {
                _uiState.update { it.copy(errorMessage = "Invalid hourly rate") }
                return@launch
            }
            
            if (state.selectedServices.isEmpty()) {
                _uiState.update { it.copy(errorMessage = "Select at least one service") }
                return@launch
            }
            
            if (state.specialtyTag.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Select a specialty tag") }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val maidId = UUID.randomUUID().toString()
                
                // Upload image if selected
                var imageUrl = ""
                if (state.selectedImageUri != null) {
                    _uiState.update { it.copy(isUploadingImage = true) }
                    
                    println("🔵 AdminViewModel: Compressing image...")
                    val compressedUri = imageCompressor.compressImage(state.selectedImageUri)
                    
                    val uriToUpload = compressedUri ?: state.selectedImageUri
                    println("🔵 AdminViewModel: Uploading maid profile image...")
                    
                    val uploadResult = maidRepository.uploadMaidProfileImage(maidId, uriToUpload)
                    if (uploadResult.isSuccess) {
                        imageUrl = uploadResult.getOrNull()!!
                        println("✅ AdminViewModel: Image uploaded successfully: $imageUrl")
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                isUploadingImage = false,
                                errorMessage = "Failed to upload image: ${uploadResult.exceptionOrNull()?.message}"
                            )
                        }
                        return@launch
                    }
                    
                    _uiState.update { it.copy(isUploadingImage = false) }
                }
                
                // Calculate average rating from reviews
                val averageRating = if (state.reviews.isNotEmpty()) {
                    state.reviews.map { it.rating }.average()
                } else {
                    0.0
                }
                
                // Create maid object
                val maid = Maid(
                    id = maidId,
                    createdAt = System.currentTimeMillis(),
                    fullName = state.fullName,
                    profileImageUrl = imageUrl,
                    phoneNumber = state.phoneNumber,
                    phoneVerified = state.phoneVerified,
                    bio = state.bio,
                    verified = state.verified,
                    averageRating = averageRating,
                    reviewCount = state.reviews.size,
                    services = state.selectedServices.toList(),
                    specialtyTag = state.specialtyTag,
                    hourlyRate = hourlyRateDouble,
                    available = state.available
                )
                
                // Add maid to Firebase
                val maidResult = maidRepository.addMaid(maid)
                if (maidResult.isFailure) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to add maid: ${maidResult.exceptionOrNull()?.message}"
                        )
                    }
                    return@launch
                }
                
                // Add reviews if any
                if (state.reviews.isNotEmpty()) {
                    val reviewsResult = maidRepository.addReviews(maidId, state.reviews)
                    if (reviewsResult.isFailure) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                errorMessage = "Maid added but failed to add reviews: ${reviewsResult.exceptionOrNull()?.message}"
                            )
                        }
                        return@launch
                    }
                }
                
                // Success - reset form
                _uiState.update { 
                    AdminAddMaidUiState(
                        successMessage = "Maid added successfully!",
                        isLoading = false
                    )
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}

