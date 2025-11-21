package com.example.maidy.feature.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.maidy.feature.rating.components.*
import com.example.maidy.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RatingScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: RatingViewModel = koinViewModel(
        parameters = { parametersOf(bookingId) }
    )

    val uiState by viewModel.uiState.collectAsState()

    // Handle success navigation
    LaunchedEffect(uiState.reviewSubmitted) {
        if (uiState.reviewSubmitted) {
            kotlinx.coroutines.delay(500) // Small delay to show success
            onNavigateBack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RatingScreenBackground)
    ) {
        if (uiState.isLoading) {
            // Loading State
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RatingSubmitButton)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Maid Header
                    RatingMaidHeader(
                        maidName = uiState.maidName,
                        profileImageUrl = uiState.maidProfileImageUrl
                    )

                    // Star Rating
                    RatingStarSelector(
                        selectedRating = uiState.selectedRating,
                        onRatingSelected = { rating ->
                            viewModel.onEvent(RatingEvent.RatingSelected(rating))
                        }
                    )

                    // Review Input
                    RatingReviewInput(
                        reviewText = uiState.reviewText,
                        onReviewTextChanged = { text ->
                            viewModel.onEvent(RatingEvent.ReviewTextChanged(text))
                        }
                    )

                    // Bottom spacing for button
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Submit Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(RatingScreenBackground)
                    .padding(bottom = 16.dp)
            ) {
                RatingSubmitButton(
                    onClick = { viewModel.onEvent(RatingEvent.SubmitReview) },
                    isLoading = uiState.isSubmitting,
                    enabled = uiState.selectedRating > 0
                )
            }
        }

        // Error Snackbar
        uiState.errorMessage?.let { errorMessage ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Snackbar(
                    action = {
                        TextButton(
                            onClick = { viewModel.onEvent(RatingEvent.DismissError) }
                        ) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(errorMessage)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingScreenPreview() {
    MaidyTheme {
        RatingScreen(
            bookingId = "preview_booking_id",
            onNavigateBack = {}
        )
    }
}
