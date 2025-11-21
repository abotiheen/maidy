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
import com.example.maidy.core.components.ConfirmationDialog
import com.example.maidy.feature.settings.components.*
import com.example.maidy.ui.theme.ProfileScreenBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.graphics.Color

/**
 * Profile/Settings Screen
 * Displays user profile information and app settings
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onNavigateToAuth: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Load user profile on first composition
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    // Handle logout navigation
    LaunchedEffect(uiState.shouldNavigateToAuth) {
        if (uiState.shouldNavigateToAuth) {
            onNavigateToAuth()
            viewModel.onLogoutNavigationHandled()
        }
    }
    
    ProfileScreenContent(
        uiState = uiState,
        onEditProfileImageClick = onNavigateToEditProfile,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )

    // Logout Confirmation Dialog
    ConfirmationDialog(
        isVisible = uiState.showLogoutDialog,
        icon = Icons.AutoMirrored.Outlined.ExitToApp,
        iconBackgroundColor = Color(0xFFFCE7E7),
        iconTint = Color(0xFFDC2626),
        title = "Log Out?",
        description = "Are you sure you want to log out? You'll need to log in again to access your account.",
        confirmButtonText = "Yes, Log Out",
        confirmButtonColor = Color(0xFFDC2626),
        cancelButtonText = "Go Back",
        onConfirm = viewModel::onConfirmLogout,
        onDismiss = viewModel::onDismissLogoutDialog
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

        Spacer(modifier = Modifier.height(12.dp))

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

