package com.example.maidy.feature.settings

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.maidy.feature.settings.components.*
import com.example.maidy.ui.theme.ProfileScreenBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

/**
 * Profile/Settings Screen
 * Displays user profile information and app settings
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(ProfileEvent.UpdateProfileImage(it.toString()))
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
    
    // Load user profile on first composition
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }
    
    ProfileScreenContent(
        uiState = uiState,
        onEditProfileImageClick = onEditProfileImage,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
private fun ProfileScreenContent(
    uiState: ProfileUiState,
    onEditProfileImageClick: () -> Unit,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ProfileScreenBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        ProfileHeader(
            profileImageUri = uiState.profileImageUri,
            fullName = uiState.fullName,
            email = uiState.email,
            isLoading = uiState.isLoading,
            onEditProfileImageClick = onEditProfileImageClick
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Full Name Field
        ProfileInfoField(
            label = "Full Name",
            value = uiState.fullName
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Phone Number Field
        ProfileInfoField(
            label = "Phone Number",
            value = "0${uiState.phoneNumber.substringAfter("4")}"
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Settings Section
        SettingSectionHeader(title = "Settings")
        
        // Dark Mode Toggle
        SettingToggleItem(
            icon = Icons.Outlined.Home,
            title = "Dark Mode",
            isChecked = uiState.isDarkModeEnabled,
            onCheckedChange = { onEvent(ProfileEvent.ToggleDarkMode(it)) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Notifications Toggle
        SettingToggleItem(
            icon = Icons.Outlined.Notifications,
            title = "Notifications",
            isChecked = uiState.areNotificationsEnabled,
            onCheckedChange = { onEvent(ProfileEvent.ToggleNotifications(it)) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Language Selection
        SettingNavigationItem(
            icon = Icons.Outlined.Create,
            title = uiState.selectedLanguage,
            onClick = { onEvent(ProfileEvent.UpdateLanguage("English")) }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // History & Support Section
        SettingSectionHeader(title = "History & Support")
        
        // Booking History
        SettingNavigationItem(
            icon = Icons.Outlined.AddCircle,
            title = "Booking History",
            onClick = { onEvent(ProfileEvent.NavigateToBookingHistory) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Payment History
        SettingNavigationItem(
            icon = Icons.Outlined.Check,
            title = "Payment History",
            onClick = { onEvent(ProfileEvent.NavigateToPaymentHistory) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Help & Support
        SettingNavigationItem(
            icon = Icons.AutoMirrored.Outlined.ExitToApp,
            title = "Help & Support",
            onClick = { onEvent(ProfileEvent.NavigateToHelpSupport) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Log Out Button
        LogOutButton(
            onClick = { onEvent(ProfileEvent.LogOut) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreenContent(
        uiState = ProfileUiState(),
        onEditProfileImageClick = {},
        onEvent = {}
    )
}

