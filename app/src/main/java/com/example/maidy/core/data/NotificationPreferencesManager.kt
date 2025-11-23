package com.example.maidy.core.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager for local notification preferences
 * Stores user's notification settings on device
 */
class NotificationPreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "notification_preferences"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    /**
     * Check if notifications are enabled
     * Default is true (enabled)
     */
    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    /**
     * Set notification preference
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
        println("📝 NotificationPreferences: Notifications ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Toggle notifications on/off
     * Returns the new state
     */
    fun toggleNotifications(): Boolean {
        val newState = !areNotificationsEnabled()
        setNotificationsEnabled(newState)
        return newState
    }
}
