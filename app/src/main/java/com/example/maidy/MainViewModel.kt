package com.example.maidy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maidy.core.data.SessionManager
import com.example.maidy.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class StartDestination {
    Home,
    Auth
}

data class MainUiState(
    val isLoading: Boolean = true,
    val startDestination: StartDestination? = null
)

class MainViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val isLoggedIn = sessionManager.isLoggedIn().first()
            _uiState.value = MainUiState(
                isLoading = false,
                startDestination = if (isLoggedIn) {
                    StartDestination.Home
                } else {
                    StartDestination.Auth
                }
            )
        }
    }
}
