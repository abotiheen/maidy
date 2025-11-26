package com.example.maidy.feature_maid.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.maidy.core.components.ConfirmationDialog
import com.example.maidy.feature_maid.auth.MaidAppBackgroundLight
import com.example.maidy.feature_maid.auth.MaidAppErrorRed
import com.example.maidy.feature_maid.profile.components.*
import org.koin.androidx.compose.koinViewModel

/**
 * Maid Profile/Settings Screen - Green themed
 * Displays maid profile information and app settings
 */
@Composable
fun MaidProfileScreen(
    viewModel: MaidProfileViewModel = koinViewModel(),
    onNavigateToAuth: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Load maid profile on first composition
    LaunchedEffect(Unit) {
        viewModel.loadMaidProfile()
    }

    // Handle logout navigation
    LaunchedEffect(uiState.shouldNavigateToAuth) {
        if (uiState.shouldNavigateToAuth) {
            onNavigateToAuth()
            viewModel.onLogoutNavigationHandled()
        }
    }

    // Show toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onToastShown()
        }
    }

    MaidProfileScreenContent(
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
private fun MaidProfileScreenContent(
    uiState: MaidProfileUiState,
    onEditProfileImageClick: () -> Unit,
    onEvent: (MaidProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaidAppBackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        MaidProfileHeader(
            profileImageUri = uiState.profileImageUri,
            fullName = uiState.fullName,
            email = uiState.email,
            isLoading = uiState.isLoading,
            onEditProfileImageClick = onEditProfileImageClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Full Name Field
        MaidProfileInfoField(
            label = "Full Name",
            value = uiState.fullName
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number Field
        MaidProfileInfoField(
            label = "Phone Number",
            value = if (uiState.phoneNumber.startsWith("+964")) {
                "0${uiState.phoneNumber.substringAfter("4")}"
            } else {
                uiState.phoneNumber
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Settings Section
        MaidSettingSectionHeader(title = "Settings")

        // Dark Mode Toggle
        MaidSettingToggleItem(
            icon = Icons.Outlined.Home,
            title = "Dark Mode",
            isChecked = uiState.isDarkModeEnabled,
            onCheckedChange = { onEvent(MaidProfileEvent.ToggleDarkMode(it)) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Notifications Toggle
        MaidSettingToggleItem(
            icon = Icons.Outlined.Notifications,
            title = "Notifications",
            isChecked = uiState.areNotificationsEnabled,
            onCheckedChange = { onEvent(MaidProfileEvent.ToggleNotifications(it)) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Log Out Button
        MaidLogOutButton(
            onClick = { onEvent(MaidProfileEvent.LogOut) }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidProfileScreenPreview() {
    MaidProfileScreenContent(
        uiState = MaidProfileUiState(
            fullName = "Sarah Johnson",
            phoneNumber = "+964 770 123 4567",
            email = "+964 770 123 4567"
        ),
        onEditProfileImageClick = {},
        onEvent = {}
    )
}
