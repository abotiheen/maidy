package com.example.maidy.feature.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maidy.feature.auth.components.AuthTabRow
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    
    // Handle successful authentication
    LaunchedEffect(uiState.isOtpVerified) {
        if (uiState.isOtpVerified) {
            onAuthSuccess()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState.currentScreen) {
            AuthScreen.LOGIN, AuthScreen.REGISTER -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 48.dp)
                ) {
                    // Tab Row
                    AuthTabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            selectedTab = tab
                            viewModel.onEvent(
                                AuthEvent.NavigateToScreen(
                                    if (tab == 0) AuthScreen.LOGIN else AuthScreen.REGISTER
                                )
                            )
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Content
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            // Determine slide direction based on target vs initial state
                            if (targetState > initialState) {
                                // Moving to Register (right): slide in from right, slide out to left
                                slideInHorizontally(
                                    initialOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(300)
                                ) togetherWith slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(300)
                                )
                            } else {
                                // Moving to Login (left): slide in from left, slide out to right
                                slideInHorizontally(
                                    initialOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(300)
                                ) togetherWith slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(300)
                                )
                            }
                        },
                        label = "auth_content"
                    ) { tab ->
                        when (tab) {
                            0 -> LoginScreen(
                                uiState = uiState,
                                onEvent = viewModel::onEvent,
                                modifier = Modifier.fillMaxSize()
                            )
                            1 -> RegisterScreen(
                                uiState = uiState,
                                onEvent = viewModel::onEvent,
                                onNavigateToLogin = { selectedTab = 0 },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
            AuthScreen.OTP_VERIFICATION -> {
                OtpVerificationScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AuthScreenLoginPreview() {
    MaidyTheme {
        // Preview without ViewModel
        AuthScreenContent(
            uiState = AuthUiState(
                currentScreen = AuthScreen.LOGIN,
                phoneNumber = "",
                password = ""
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AuthScreenRegisterPreview() {
    MaidyTheme {
        AuthScreenContent(
            uiState = AuthUiState(
                currentScreen = AuthScreen.REGISTER,
                fullName = "",
                phoneNumber = "",
                password = "",
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AuthScreenOtpPreview() {
    MaidyTheme {
        AuthScreenContent(
            uiState = AuthUiState(
                currentScreen = AuthScreen.OTP_VERIFICATION,
                otpCode = ""
            ),
            onEvent = {}
        )
    }
}

@Composable
private fun AuthScreenContent(
    uiState: AuthUiState,
    onEvent: (AuthEvent) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (uiState.currentScreen == AuthScreen.LOGIN) 0 else 1) }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState.currentScreen) {
            AuthScreen.LOGIN, AuthScreen.REGISTER -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 48.dp)
                ) {
                    // Tab Row
                    AuthTabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            selectedTab = tab
                            onEvent(
                                AuthEvent.NavigateToScreen(
                                    if (tab == 0) AuthScreen.LOGIN else AuthScreen.REGISTER
                                )
                            )
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Content
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            // Determine slide direction based on target vs initial state
                            if (targetState > initialState) {
                                // Moving to Register (right): slide in from right, slide out to left
                                slideInHorizontally(
                                    initialOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(300)
                                ) togetherWith slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(300)
                                )
                            } else {
                                // Moving to Login (left): slide in from left, slide out to right
                                slideInHorizontally(
                                    initialOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(300)
                                ) togetherWith slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(300)
                                )
                            }
                        },
                        label = "auth_content"
                    ) { tab ->
                        when (tab) {
                            0 -> LoginScreen(
                                uiState = uiState,
                                onEvent = onEvent,
                                modifier = Modifier.fillMaxSize()
                            )
                            1 -> RegisterScreen(
                                uiState = uiState,
                                onEvent = onEvent,
                                onNavigateToLogin = { selectedTab = 0 },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
            AuthScreen.OTP_VERIFICATION -> {
                OtpVerificationScreen(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

