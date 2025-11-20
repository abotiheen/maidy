package com.example.maidy.feature.admin

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.core.model.ServiceOptions
import com.example.maidy.core.model.SpecialtyTags
import com.example.maidy.ui.theme.MaidyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AdminAddMaidScreen(
    viewModel: AdminAddMaidViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageSelected(it.toString())
        }
    }
    
    // Permission state for storage
    val permissionsState = rememberMultiplePermissionsState(
        permissions = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    )
    
    // Handle image picker
    val onSelectImage = {
        if (permissionsState.allPermissionsGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
    
    // Show snackbar for messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Add New Maid",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
            }
            
            // Profile Image Section
            item {
                SectionCard(title = "Profile Image") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Image preview
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0))
                                .border(2.dp, Color(0xFF4299E1), CircleShape)
                                .clickable(onClick = onSelectImage),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.selectedImageUri != null) {
                                AsyncImage(
                                    model = uiState.selectedImageUri,
                                    contentDescription = "Selected maid image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Select image",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }
                        
                        // Select image button
                        OutlinedButton(
                            onClick = onSelectImage,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.selectedImageUri != null) "Change Image" else "Select Image"
                            )
                        }
                        
                        if (uiState.isUploadingImage) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Compressing & uploading...",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4299E1)
                                )
                            }
                        }
                    }
                }
            }
            
            // Core Information Section
            item {
                SectionCard(title = "Core Information") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = uiState.fullName,
                            onValueChange = viewModel::onFullNameChange,
                            label = { Text("Full Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = uiState.phoneNumber,
                            onValueChange = viewModel::onPhoneNumberChange,
                            label = { Text("Phone Number *") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = uiState.bio,
                            onValueChange = viewModel::onBioChange,
                            label = { Text("Bio *") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                        )
                    }
                }
            }
            
            // Verification & Status Section
            item {
                SectionCard(title = "Verification & Status") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Is Verified (Green Badge)")
                            Switch(
                                checked = uiState.verified,
                                onCheckedChange = viewModel::onVerifiedChange
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Phone Verified")
                            Switch(
                                checked = uiState.phoneVerified,
                                onCheckedChange = viewModel::onPhoneVerifiedChange
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Is Available")
                            Switch(
                                checked = uiState.available,
                                onCheckedChange = viewModel::onAvailableChange
                            )
                        }
                    }
                }
            }
            
            // Pricing Section
            item {
                SectionCard(title = "Pricing") {
                    OutlinedTextField(
                        value = uiState.hourlyRate,
                        onValueChange = viewModel::onHourlyRateChange,
                        label = { Text("Hourly Rate ($) *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
            }
            
            // Services Section
            item {
                SectionCard(title = "Services *") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ServiceOptions.services.forEach { service ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(service)
                                Checkbox(
                                    checked = service in uiState.selectedServices,
                                    onCheckedChange = { viewModel.onServiceToggle(service) }
                                )
                            }
                        }
                    }
                }
            }
            
            // Specialty Tag Section
            item {
                SectionCard(title = "Specialty Tag *") {
                    SpecialtyTagDropdown(
                        selectedTag = uiState.specialtyTag,
                        onTagSelected = viewModel::onSpecialtyTagChange
                    )
                }
            }
            
            // Reviews Section
            item {
                SectionCard(title = "Add Reviews (Max 2)") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Existing reviews
                        if (uiState.reviews.isNotEmpty()) {
                            Text(
                                text = "Reviews Added (${uiState.reviews.size}/2)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF4A5568)
                            )
                            
                            uiState.reviews.forEach { review ->
                                ReviewItemCard(
                                    review = review,
                                    onRemove = { viewModel.removeReview(review.id) }
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        // Add review form
                        if (uiState.reviews.size < 2) {
                            Text(
                                text = "Add New Review",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF4A5568)
                            )
                            
                            OutlinedTextField(
                                value = uiState.reviewerName,
                                onValueChange = viewModel::onReviewerNameChange,
                                label = { Text("Reviewer Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            
                            // Rating selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Rating")
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    (1..5).forEach { rating ->
                                        FilterChip(
                                            selected = uiState.reviewRating == rating,
                                            onClick = { viewModel.onReviewRatingChange(rating) },
                                            label = { Text("$rating") }
                                        )
                                    }
                                }
                            }
                            
                            OutlinedTextField(
                                value = uiState.reviewComment,
                                onValueChange = viewModel::onReviewCommentChange,
                                label = { Text("Review Comment") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 5
                            )
                            
                            OutlinedTextField(
                                value = uiState.reviewDate,
                                onValueChange = viewModel::onReviewDateChange,
                                label = { Text("Date (e.g., June 15, 2024)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            
                            Button(
                                onClick = viewModel::addReview,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4299E1)
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Review")
                            }
                        }
                    }
                }
            }
            
            // Submit Button
            item {
                Button(
                    onClick = viewModel::submitMaid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF48BB78)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Submit Maid to Firebase",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3748)
            )
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialtyTagDropdown(
    selectedTag: String,
    onTagSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedTag,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Specialty Tag") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SpecialtyTags.tags.forEach { tag ->
                DropdownMenuItem(
                    text = { Text(tag) },
                    onClick = {
                        onTagSelected(tag)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ReviewItemCard(
    review: com.example.maidy.core.model.MaidReview,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7FAFC)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.reviewerName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Rating: ${review.rating}★",
                    fontSize = 12.sp,
                    color = Color(0xFF718096)
                )
                Text(
                    text = review.comment,
                    fontSize = 12.sp,
                    color = Color(0xFF4A5568),
                    maxLines = 2
                )
                Text(
                    text = review.date,
                    fontSize = 11.sp,
                    color = Color(0xFF718096)
                )
            }
            
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove review",
                    tint = Color(0xFFE53E3E)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminAddMaidScreenPreview() {
    MaidyTheme {
        // Preview would need a viewmodel instance
    }
}

