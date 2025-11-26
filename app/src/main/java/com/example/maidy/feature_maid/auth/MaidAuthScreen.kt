package com.example.maidy.feature_maid.auth

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
import com.example.maidy.feature_maid.auth.components.MaidAuthTabRow
import com.example.maidy.ui.theme.MaidyTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MaidAuthScreen(
    viewModel: MaidAuthViewModel = koinViewModel(),
    onAuthSuccess: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    // Handle successful authentication
    LaunchedEffect(uiState.isLoginSuccessful, uiState.isOtpVerified) {
        if (uiState.isLoginSuccessful || uiState.isOtpVerified) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState.currentScreen) {
            MaidAuthScreen.LOGIN, MaidAuthScreen.REGISTER -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 48.dp)
                ) {
                    // Tab Row
                    MaidAuthTabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            selectedTab = tab
                            viewModel.onEvent(
                                MaidAuthEvent.NavigateToScreen(
                                    if (tab == 0) MaidAuthScreen.LOGIN else MaidAuthScreen.REGISTER
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
                        label = "maid_auth_content"
                    ) { tab ->
                        when (tab) {
                            0 -> MaidLoginScreen(
                                uiState = uiState,
                                onEvent = viewModel::onEvent,
                                onNavigateToTerms = onNavigateToTerms,
                                modifier = Modifier.fillMaxSize()
                            )

                            1 -> MaidRegisterScreen(
                                uiState = uiState,
                                onEvent = viewModel::onEvent,
                                onNavigateToLogin = { selectedTab = 0 },
                                onNavigateToTerms = onNavigateToTerms,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            MaidAuthScreen.OTP_VERIFICATION -> {
                MaidOtpVerificationScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToTerms = onNavigateToTerms,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidAuthScreenLoginPreview() {
    MaidyTheme {
        // Preview without ViewModel
        MaidAuthScreenContent(
            uiState = MaidAuthUiState(
                currentScreen = MaidAuthScreen.LOGIN,
                phoneNumber = "",
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidAuthScreenRegisterPreview() {
    MaidyTheme {
        MaidAuthScreenContent(
            uiState = MaidAuthUiState(
                currentScreen = MaidAuthScreen.REGISTER,
                fullName = "",
                phoneNumber = "",
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidAuthScreenOtpPreview() {
    MaidyTheme {
        MaidAuthScreenContent(
            uiState = MaidAuthUiState(
                currentScreen = MaidAuthScreen.OTP_VERIFICATION,
                otpCode = ""
            ),
            onEvent = {}
        )
    }
}

@Composable
private fun MaidAuthScreenContent(
    uiState: MaidAuthUiState,
    onEvent: (MaidAuthEvent) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (uiState.currentScreen == MaidAuthScreen.LOGIN) 0 else 1) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState.currentScreen) {
            MaidAuthScreen.LOGIN, MaidAuthScreen.REGISTER -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 48.dp)
                ) {
                    // Tab Row
                    MaidAuthTabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            selectedTab = tab
                            onEvent(
                                MaidAuthEvent.NavigateToScreen(
                                    if (tab == 0) MaidAuthScreen.LOGIN else MaidAuthScreen.REGISTER
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
                        label = "maid_auth_content"
                    ) { tab ->
                        when (tab) {
                            0 -> MaidLoginScreen(
                                uiState = uiState,
                                onEvent = onEvent,
                                modifier = Modifier.fillMaxSize()
                            )

                            1 -> MaidRegisterScreen(
                                uiState = uiState,
                                onEvent = onEvent,
                                onNavigateToLogin = { selectedTab = 0 },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            MaidAuthScreen.OTP_VERIFICATION -> {
                MaidOtpVerificationScreen(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
