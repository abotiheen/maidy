package com.example.maidy.feature.edit_profile

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.feature.auth.components.AuthTextField
import com.example.maidy.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(EditProfileEvent.UpdateProfileImage(it.toString()))
        }
    }

    // Permission state for camera and storage
    val permissionsState = rememberMultiplePermissionsState(
        permissions = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    )

    // Handle profile image click
    val onEditProfileImage = {
        if (permissionsState.allPermissionsGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    // Handle successful save - navigate back
    LaunchedEffect(uiState.profileSaved) {
        if (uiState.profileSaved) {
            kotlinx.coroutines.delay(300)
            onNavigateBack()
        }
    }

    EditProfileContent(
        uiState = uiState,
        onEditProfileImage = onEditProfileImage,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
private fun EditProfileContent(
    uiState: EditProfileUiState,
    onEditProfileImage: () -> Unit,
    onEvent: (EditProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ProfileScreenBackground)
    ) {
        if (uiState.isLoading) {
            // Loading State
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaidyBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Profile Image
                Box(
                    modifier = Modifier.size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Image
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(ProfileImageBackground)
                            .border(4.dp, ProfileEditIconBackground, CircleShape)
                            .clickable(onClick = onEditProfileImage),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.profileImageUri != null) {
                            AsyncImage(
                                model = uiState.profileImageUri,
                                contentDescription = "Profile image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default profile",
                                tint = ProfileImagePlaceholderIcon,
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }

                    // Edit Icon Badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(ProfileEditIconBackground)
                            .clickable(onClick = onEditProfileImage),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Change photo",
                            tint = ProfileEditIconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Full Name Field
                AuthTextField(
                    value = uiState.fullName,
                    onValueChange = { onEvent(EditProfileEvent.UpdateFullName(it)) },
                    label = "Full Name",
                    placeholder = "Enter your full name",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))

                // Error Message
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaidyErrorRed,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                // Save Button
                Button(
                    onClick = { onEvent(EditProfileEvent.SaveProfile) },
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaidyBlue,
                        contentColor = Color.White,
                        disabledContainerColor = MaidyBlue.copy(alpha = 0.5f)
                    )
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Changes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    MaidyTheme {
        EditProfileContent(
            uiState = EditProfileUiState(
                isLoading = false,
                fullName = "John Doe",
                profileImageUri = null
            ),
            onEditProfileImage = {},
            onEvent = {}
        )
    }
}
