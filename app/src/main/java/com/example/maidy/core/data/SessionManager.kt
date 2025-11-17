package com.example.maidy.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {
    
    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }
    
    // Save user ID after login
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }
    
    // Get current user ID as Flow (reactive)
    fun getUserId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }
    }
    
    // Get current user ID (one-time)
    suspend fun getCurrentUserId(): String? {
        var userId: String? = null
        context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }.collect {
            userId = it
        }
        return userId
    }
    
    // Check if user is logged in
    fun isLoggedIn(): Flow<Boolean> {
        return getUserId().map { it != null }
    }
    
    // Logout - clear user ID
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }
}

