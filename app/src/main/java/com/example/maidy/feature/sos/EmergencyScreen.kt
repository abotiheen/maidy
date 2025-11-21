package com.example.maidy.feature.sos

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maidy.feature.sos.components.CallForHelpButton
import com.example.maidy.feature.sos.components.CancelButton
import com.example.maidy.feature.sos.components.EmergencyDescription
import com.example.maidy.feature.sos.components.EmergencyTitle
import com.example.maidy.feature.sos.components.SOSIcon
import com.example.maidy.ui.theme.EmergencyScreenBackground
import com.example.maidy.ui.theme.MaidyTheme

@Composable
fun EmergencyScreen(
    viewModel: EmergencyViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle dialer opening
    LaunchedEffect(uiState.shouldOpenDialer) {
        uiState.shouldOpenDialer?.let { phoneNumber ->
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
            viewModel.onDialerHandled()
        }
    }

    EmergencyScreenContent(
        uiState = uiState,
        onCallForHelpClick = {
            viewModel.onEvent(EmergencyUiEvent.OnCallForHelpClick)
        },
        onCancelClick = {
            viewModel.onEvent(EmergencyUiEvent.OnCancelClick)
            onNavigateBack()
        }
    )
}

@Composable
fun EmergencyScreenContent(
    uiState: EmergencyUiState,
    onCallForHelpClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EmergencyScreenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // SOS Icon
            SOSIcon(size = 120)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Title
            EmergencyTitle(text = "Confirm Emergency")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            EmergencyDescription(
                text = "Emergency assistance will be notified. Are you sure?"
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Buttons Section
            Column(
                modifier = Modifier.padding(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CallForHelpButton(onClick = onCallForHelpClick)
                
                CancelButton(onClick = onCancelClick)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmergencyScreenPreview() {
    MaidyTheme {
        EmergencyScreenContent(
            uiState = EmergencyUiState(),
            onCallForHelpClick = {},
            onCancelClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmergencyScreenTriggeredPreview() {
    MaidyTheme {
        EmergencyScreenContent(
            uiState = EmergencyUiState(isEmergencyTriggered = true),
            onCallForHelpClick = {},
            onCancelClick = {}
        )
    }
}

