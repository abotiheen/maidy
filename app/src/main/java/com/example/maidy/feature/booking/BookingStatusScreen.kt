package com.example.maidy.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maidy.feature.booking.components.*
import com.example.maidy.ui.theme.*

@Composable
fun BookingStatusScreen(
    viewModel: BookingStatusViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BookingStatusBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Service Info Card
        ServiceInfoCard(bookingDetails = uiState.bookingDetails)
        
        // Status Indicator
        StatusIndicator(currentStatus = uiState.currentStatus)
        
        // Maid Card
        MaidCard(
            maidInfo = uiState.maidInfo,
            statusMessage = uiState.statusMessage,
            currentStatus = uiState.currentStatus
        )
        
        // Action Button
        StatusActionButton(
            currentStatus = uiState.currentStatus,
            onCancelOrder = { viewModel.onCancelOrder() },
            onContactMaid = { viewModel.onContactMaid() },
            onSOSClicked = { viewModel.onSOSClicked() },
            onRateMaid = { viewModel.onRateMaid() }
        )
        
        // Bottom spacing
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun BookingStatusScreenPreview() {
    BookingStatusScreen()
}

@Preview(showBackground = true)
@Composable
fun BookingStatusScreenConfirmedPreview() {
    val viewModel = BookingStatusViewModel()
    viewModel.updateStatus(BookingStatus.CONFIRMED)
    BookingStatusScreen(viewModel = viewModel)
}

@Preview(showBackground = true)
@Composable
fun BookingStatusScreenOnTheWayPreview() {
    val viewModel = BookingStatusViewModel()
    viewModel.updateStatus(BookingStatus.ON_THE_WAY)
    BookingStatusScreen(viewModel = viewModel)
}

@Preview(showBackground = true)
@Composable
fun BookingStatusScreenInProgressPreview() {
    val viewModel = BookingStatusViewModel()
    viewModel.updateStatus(BookingStatus.IN_PROGRESS)
    BookingStatusScreen(viewModel = viewModel)
}

@Preview(showBackground = true)
@Composable
fun BookingStatusScreenCompletedPreview() {
    val viewModel = BookingStatusViewModel()
    viewModel.updateStatus(BookingStatus.COMPLETED)
    BookingStatusScreen(viewModel = viewModel)
}

