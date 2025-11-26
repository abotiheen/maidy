package com.example.maidy.feature_maid.edit_profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.maidy.feature_maid.auth.*
import com.example.maidy.ui.theme.MaidyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MaidEditProfileScreen(
    viewModel: MaidEditProfileViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(MaidEditProfileEvent.ImageSelected(it.toString()))
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
            viewModel.onEvent(MaidEditProfileEvent.ClearMessages)
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
            viewModel.onEvent(MaidEditProfileEvent.ClearMessages)
            // Navigate back after successful save
            kotlinx.coroutines.delay(1000)
            onNavigateBack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaidAppBackgroundLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Edit Your Profile",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000)
                    )
                    Text(
                        text = "Update your information to attract more customers",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            // Profile Image Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Image preview with enhanced styling
                        Box(
                            modifier = Modifier.size(140.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape)
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFFE8F5E9),
                                                Color(0xFFC8E6C9)
                                            )
                                        )
                                    )
                                    .border(4.dp, MaidAppGreen, CircleShape)
                                    .clickable(onClick = onSelectImage),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.selectedImageUri != null) {
                                    AsyncImage(
                                        model = uiState.selectedImageUri,
                                        contentDescription = "Profile image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Select image",
                                        modifier = Modifier.size(56.dp),
                                        tint = MaidAppGreen
                                    )
                                }
                            }

                            // Camera icon badge
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaidAppGreen)
                                    .border(3.dp, Color.White, CircleShape)
                                    .clickable(onClick = onSelectImage),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Change photo",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        // Upload status
                        if (uiState.isUploadingImage) {
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaidAppGreen
                                    )
                                    Text(
                                        text = "Uploading image...",
                                        fontSize = 13.sp,
                                        color = MaidAppDarkGreen,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Tap the camera icon to update your photo",
                                fontSize = 13.sp,
                                color = MaidAppTextSecondary
                            )
                        }
                    }
                }
            }

            // Core Information Section
            item {
                MaidSectionCard(
                    title = "Core Information",
                    icon = Icons.Outlined.Person
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = uiState.fullName,
                            onValueChange = {
                                viewModel.onEvent(
                                    MaidEditProfileEvent.FullNameChanged(
                                        it
                                    )
                                )
                            },
                            label = { Text("Full Name *") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.AccountCircle,
                                    contentDescription = null,
                                    tint = MaidAppGreen
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaidAppGreen,
                                unfocusedBorderColor = Color(0xFFD1D5DB),
                                focusedLabelColor = MaidAppGreen,
                                unfocusedLabelColor = Color(0xFF6B7280),
                                cursorColor = MaidAppGreen,
                                focusedTextColor = Color(0xFF000000),
                                unfocusedTextColor = Color(0xFF000000)
                            )
                        )

                        OutlinedTextField(
                            value = uiState.bio,
                            onValueChange = { viewModel.onEvent(MaidEditProfileEvent.BioChanged(it)) },
                            label = { Text("Bio *") },
                            placeholder = { Text("Tell customers about yourself and your experience") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            maxLines = 6,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaidAppGreen,
                                unfocusedBorderColor = Color(0xFFD1D5DB),
                                focusedLabelColor = MaidAppGreen,
                                unfocusedLabelColor = Color(0xFF6B7280),
                                cursorColor = MaidAppGreen,
                                focusedTextColor = Color(0xFF000000),
                                unfocusedTextColor = Color(0xFF000000)
                            )
                        )
                    }
                }
            }

            // Pricing Section
            item {
                MaidSectionCard(
                    title = "Pricing",
                    icon = Icons.Filled.CheckCircle
                ) {
                    OutlinedTextField(
                        value = uiState.hourlyRate,
                        onValueChange = {
                            viewModel.onEvent(
                                MaidEditProfileEvent.HourlyRateChanged(
                                    it
                                )
                            )
                        },
                        label = { Text("Hourly Rate (USD) *") },
                        placeholder = { Text("e.g., 25") },
                        leadingIcon = {
                            Text(
                                text = "USD",
                                color = MaidAppGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaidAppGreen,
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedLabelColor = MaidAppGreen,
                            unfocusedLabelColor = Color(0xFF6B7280),
                            cursorColor = MaidAppGreen,
                            focusedTextColor = Color(0xFF000000),
                            unfocusedTextColor = Color(0xFF000000)
                        )
                    )
                }
            }

            // Services Section
            item {
                MaidSectionCard(
                    title = "Services Offered *",
                    icon = Icons.Outlined.CheckCircle
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Select all services you can provide",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )

                        ServiceOptions.services.forEach { service ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (service in uiState.selectedServices)
                                            Color(0xFFE8F5E9)
                                        else
                                            Color.Transparent
                                    )
                                    .clickable {
                                        viewModel.onEvent(
                                            MaidEditProfileEvent.ServiceToggled(
                                                service
                                            )
                                        )
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = service,
                                    color = Color(0xFF000000)
                                )
                                Checkbox(
                                    checked = service in uiState.selectedServices,
                                    onCheckedChange = {
                                        viewModel.onEvent(
                                            MaidEditProfileEvent.ServiceToggled(
                                                service
                                            )
                                        )
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaidAppGreen,
                                        checkmarkColor = Color.White,
                                        uncheckedColor = Color(0xFFD1D5DB)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Specialty Tag Section
            item {
                MaidSectionCard(
                    title = "Specialty Class *",
                    icon = Icons.Outlined.Star
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Select your service class level",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )

                        MaidSpecialtyTagDropdown(
                            selectedTag = uiState.specialtyTag,
                            onTagSelected = {
                                viewModel.onEvent(
                                    MaidEditProfileEvent.SpecialtyTagChanged(
                                        it
                                    )
                                )
                            }
                        )
                    }
                }
            }

            // Save Button
            item {
                Button(
                    onClick = { viewModel.onEvent(MaidEditProfileEvent.SaveProfile) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    enabled = !uiState.isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaidAppGreen,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFC8E6C9)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    if (uiState.isSaving) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Saving...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                            Text(
                                text = "Save Profile",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
fun MaidSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaidAppGreen,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
            }
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaidSpecialtyTagDropdown(
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
            label = { Text("Select Class") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaidAppGreen,
                unfocusedBorderColor = Color(0xFFD1D5DB),
                focusedLabelColor = MaidAppGreen,
                unfocusedLabelColor = Color(0xFF6B7280),
                focusedTextColor = Color(0xFF000000),
                unfocusedTextColor = Color(0xFF000000)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            SpecialtyTags.tags.forEach { tag ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = tag,
                            color = Color(0xFF000000)
                        )
                    },
                    onClick = {
                        onTagSelected(tag)
                        expanded = false
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MaidEditProfileScreenPreview() {
    MaidyTheme {
        // Preview would need a viewmodel instance
    }
}
