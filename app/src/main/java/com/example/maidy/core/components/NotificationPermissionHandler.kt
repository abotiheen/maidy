package com.example.maidy.core.components

import android.Manifest
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.maidy.core.util.NotificationHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

/**
 * Composable that handles notification permission for Android 13+
 * Automatically requests permission when first needed
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionHandler(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current

    // Only request permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        )

        LaunchedEffect(notificationPermissionState.status) {
            when {
                notificationPermissionState.status.isGranted -> {
                    println("✅ Notification permission granted")
                    onPermissionGranted()
                }

                notificationPermissionState.status.shouldShowRationale -> {
                    println("⚠️ Should show rationale for notification permission")
                    // You can show a dialog here explaining why you need the permission
                }

                else -> {
                    println("📋 Requesting notification permission")
                    notificationPermissionState.launchPermissionRequest()
                }
            }
        }

        // Handle permission denial
        LaunchedEffect(notificationPermissionState.status.isGranted) {
            if (!notificationPermissionState.status.isGranted &&
                !notificationPermissionState.status.shouldShowRationale
            ) {
                onPermissionDenied()
            }
        }
    } else {
        // Android 12 and below - no permission needed
        LaunchedEffect(Unit) {
            onPermissionGranted()
        }
    }
}

/**
 * Check if notification permission is granted
 */
@Composable
fun rememberNotificationPermissionGranted(): Boolean {
    val context = LocalContext.current
    return remember {
        NotificationHelper.hasNotificationPermission(context)
    }
}
