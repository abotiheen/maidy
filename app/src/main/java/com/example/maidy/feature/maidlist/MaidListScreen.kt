package com.example.maidy.feature.maidlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.maidlist.components.FilterChipsRow
import com.example.maidy.feature.maidlist.components.MaidListCard
import com.example.maidy.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun MaidListScreen(
    viewModel: MaidListViewModel = koinViewModel(),
    onNavigateToMaidDetails: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MaidListScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToMaidDetails = onNavigateToMaidDetails,
        modifier = modifier
    )
}

@Composable
private fun MaidListScreenContent(
    uiState: MaidListUiState,
    onEvent: (MaidListUiEvent) -> Unit,
    onNavigateToMaidDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaidListBackground)
    ) {
        // Filter Chips Row
        FilterChipsRow(
            filters = uiState.filters,
            onFilterClick = { filterId ->
                onEvent(MaidListUiEvent.OnFilterChipClick(filterId))
            }
        )

        // Content
        when {
            uiState.isLoading -> {
                // Loading State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaidListFilterChipSelectedBackground
                    )
                }
            }

            uiState.errorMessage != null -> {
                // Error State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        fontSize = 16.sp,
                        color = MaidyErrorRed,
                        textAlign = TextAlign.Center
                    )
                }
            }

            uiState.filteredMaids.isEmpty() -> {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No maids available",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaidListTopBarTitle
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try adjusting your filters",
                            fontSize = 14.sp,
                            color = MaidListFilterChipUnselectedText
                        )
                    }
                }
            }

            else -> {
                // Maids List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.filteredMaids,
                        key = { maid -> maid.id }
                    ) { maid ->
                        MaidListCard(
                            maid = maid,
                            onSelectClick = {
                                if (maid.available) {
                                    onNavigateToMaidDetails(maid.id)
                                }
                            },
                            onViewDetailsClick = {
                                onNavigateToMaidDetails(maid.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MaidListScreenPreview() {
    // Preview with placeholder data
    val previewState = MaidListUiState(
        filters = listOf(
            FilterOption("1", "Deep Cleaning", isSelected = false),
            FilterOption("2", "Eco-Friendly", isSelected = true),
            FilterOption("3", "Pet-Friendly", isSelected = false),
        ),
        filteredMaids = listOf(
            com.example.maidy.core.model.Maid(
                id = "1",
                fullName = "Hala Al-Fahad",
                averageRating = 4.9,
                reviewCount = 120,
                specialtyTag = "Deep Cleaning",
                profileImageUrl = "",
                available = true
            ),
            com.example.maidy.core.model.Maid(
                id = "2",
                fullName = "Sara Mohammed",
                averageRating = 4.8,
                reviewCount = 95,
                specialtyTag = "Eco-Friendly",
                profileImageUrl = "",
                available = true
            ),
        )
    )

    MaidListScreenContent(
        uiState = previewState,
        onEvent = {},
        onNavigateToMaidDetails = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MaidListScreenLoadingPreview() {
    MaidListScreenContent(
        uiState = MaidListUiState(isLoading = true),
        onEvent = {},
        onNavigateToMaidDetails = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MaidListScreenEmptyPreview() {
    MaidListScreenContent(
        uiState = MaidListUiState(
            filteredMaids = emptyList(),
            filters = listOf(
                FilterOption("1", "Pet-Friendly", isSelected = true),
            )
        ),
        onEvent = {},
        onNavigateToMaidDetails = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MaidListScreenErrorPreview() {
    MaidListScreenContent(
        uiState = MaidListUiState(
            errorMessage = "Failed to load maids. Please try again."
        ),
        onEvent = {},
        onNavigateToMaidDetails = {}
    )
}

