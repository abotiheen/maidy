package com.example.maidy.feature.booking

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.maidy.core.model.BookingStatus
import com.example.maidy.feature.booking.components.CancelBookingDialog
import com.example.maidy.feature.booking.components.CancelledBookingCard
import com.example.maidy.feature.booking.components.DebugStatusChanger
import com.example.maidy.feature.booking.components.MaidCard
import com.example.maidy.feature.booking.components.RecurringCard
import com.example.maidy.feature.booking.components.ServiceInfoCard
import com.example.maidy.feature.booking.components.StatusActionButton
import com.example.maidy.feature.booking.components.StatusIndicator
import com.example.maidy.ui.theme.BookingDetailsDateIcon
import com.example.maidy.ui.theme.BookingStatusBackground
import com.example.maidy.ui.theme.MaidyTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BookingStatusScreen(
    bookingId: String,
    onNavigateToSOS: () -> Unit = {},
    onNavigateToAdjustRecurring: () -> Unit = {},
    onNavigateToRating: () -> Unit = {},
    viewModel: BookingStatusViewModel = koinViewModel(parameters = { parametersOf(bookingId) })
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

    // Handle SOS navigation
    LaunchedEffect(uiState.shouldNavigateToSOS) {
        if (uiState.shouldNavigateToSOS) {
            onNavigateToSOS()
            viewModel.onSOSNavigationHandled()
        }
    }

    BookingStatusContent(
        uiState = uiState,
        onCancelOrderClick = viewModel::onCancelOrderClick,
        onConfirmCancel = viewModel::onConfirmCancelOrder,
        onDismissCancelDialog = viewModel::onDismissCancelDialog,
        onContactMaid = viewModel::onContactMaid,
        onSOSClicked = viewModel::onSOSClicked,
        onRateMaid = onNavigateToRating,
        onDebugStatusChange = viewModel::changeBookingStatus,
        onManageRecurring = onNavigateToAdjustRecurring
    )
}

@Composable
private fun BookingStatusContent(
    uiState: BookingStatusUiState,
    onCancelOrderClick: () -> Unit,
    onConfirmCancel: () -> Unit,
    onDismissCancelDialog: () -> Unit,
    onContactMaid: () -> Unit,
    onSOSClicked: () -> Unit,
    onRateMaid: () -> Unit,
    onDebugStatusChange: (BookingStatus) -> Unit,
    onManageRecurring: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BookingDetailsDateIcon)
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.errorMessage)
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BookingStatusBackground)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ServiceInfoCard(bookingDetails = uiState.bookingDetails)

                    if (uiState.currentStatus == BookingStatus.CANCELLED) {
                        CancelledBookingCard()
                    } else {
                        StatusIndicator(currentStatus = uiState.currentStatus)
                    }

                    MaidCard(
                        maidInfo = uiState.maidInfo,
                        statusMessage = uiState.statusMessage,
                        currentStatus = uiState.currentStatus
                    )

                    if (uiState.isRecurring && uiState.recurringDetails != null && uiState.currentStatus != BookingStatus.CANCELLED) {
                        RecurringCard(
                            details = uiState.recurringDetails,
                            onManageBooking = onManageRecurring
                        )
                    }

                    if (uiState.currentStatus != BookingStatus.CANCELLED) {
                        StatusActionButton(
                            currentStatus = uiState.currentStatus,
                            maidName = uiState.maidInfo.name,
                            onCancelOrder = onCancelOrderClick,
                            onContactMaid = onContactMaid,
                            onSOSClicked = onSOSClicked,
                            onRateMaid = onRateMaid
                        )
                    }

                    // DEBUG: Status changer (to be removed later)
//                    DebugStatusChanger(
//                        currentStatus = uiState.currentStatus,
//                        onStatusChange = onDebugStatusChange
//                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Cancel booking dialog
        CancelBookingDialog(
            isVisible = uiState.showCancelDialog,
            onConfirmCancel = onConfirmCancel,
            onDismiss = onDismissCancelDialog
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookingStatusScreenPreview() {
    MaidyTheme {
        BookingStatusContent(
            uiState = BookingStatusUiState(
                isLoading = false,
                bookingDetails = BookingServiceDetails(
                    service = "Standard Cleaning",
                    date = "Apr 02, 2024",
                    time = "2:00 PM"
                ),
                currentStatus = BookingStatus.ON_THE_WAY,
                maidInfo = MaidInfo(
                    name = "Jane Doe",
                    rating = 4.9f,
                    arrivingInMinutes = 15,
                    profileImageUrl = "",
                    description = "Experienced professional cleaner with 5+ years expertise in residential cleaning."
                ),
                statusMessage = "Your maid is on the way.",
                isRecurring = true,
                recurringDetails = RecurringBookingDetails(
                    frequency = "Weekly",
                    day = "Wednesday",
                    time = "10:00 AM"
                )
            ),
            onCancelOrderClick = {},
            onConfirmCancel = {},
            onDismissCancelDialog = {},
            onContactMaid = {},
            onSOSClicked = {},
            onRateMaid = {},
            onDebugStatusChange = {},
            onManageRecurring = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookingStatusCancelledPreview() {
    MaidyTheme {
        BookingStatusContent(
            uiState = BookingStatusUiState(
                isLoading = false,
                bookingDetails = BookingServiceDetails(
                    service = "Move-out Clean",
                    date = "Apr 18, 2024",
                    time = "9:00 AM"
                ),
                currentStatus = BookingStatus.CANCELLED,
                maidInfo = MaidInfo(name = "Ana P.", rating = 4.7f),
                statusMessage = "This booking has been cancelled.",
                isRecurring = true,
                recurringDetails = RecurringBookingDetails(
                    frequency = "Weekly",
                    day = "Wednesday",
                    time = "10:00 AM"
                )
            ),
            onCancelOrderClick = {},
            onConfirmCancel = {},
            onDismissCancelDialog = {},
            onContactMaid = {},
            onSOSClicked = {},
            onRateMaid = {},
            onDebugStatusChange = {},
            onManageRecurring = {}
        )
    }
}
