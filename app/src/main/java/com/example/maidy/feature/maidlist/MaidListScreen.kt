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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maidy.feature.maidlist.components.FilterChipsRow
import com.example.maidy.feature.maidlist.components.MaidListCard
import com.example.maidy.ui.theme.*

@Composable
fun MaidListScreen(
    viewModel: MaidListViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MaidListScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
private fun MaidListScreenContent(
    uiState: MaidListUiState,
    onEvent: (MaidListUiEvent) -> Unit,
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
            },
            onLocationClick = {
                onEvent(MaidListUiEvent.OnLocationClick)
            },
            onDateTimeClick = {
                onEvent(MaidListUiEvent.OnDateTimeClick)
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

            uiState.maids.isEmpty() -> {
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
                        items = uiState.maids,
                        key = { maid -> maid.id }
                    ) { maid ->
                        MaidListCard(
                            maidProfile = maid,
                            onSelectClick = {
                                onEvent(MaidListUiEvent.OnSelectMaidClick(maid.id))
                            },
                            onViewDetailsClick = {
                                onEvent(MaidListUiEvent.OnViewDetailsClick(maid.id))
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
            FilterOption("1", "Pet Friendly", isSelected = false),
            FilterOption("2", "Available Tomorrow", isSelected = true),
            FilterOption("3", "Eco-Friendly", isSelected = false),
            FilterOption("4", "Top Rated", isSelected = false),
        ),
        maids = listOf(
            MaidProfile(
                id = "1",
                name = "Maria S.",
                rating = 4.9f,
                reviewCount = 120,
                services = listOf(ServiceTag.DEEP_CLEANING)
            ),
            MaidProfile(
                id = "2",
                name = "Isabella R.",
                rating = 4.8f,
                reviewCount = 95,
                services = listOf(ServiceTag.ECO_FRIENDLY)
            ),
            MaidProfile(
                id = "3",
                name = "Chloe T.",
                rating = 4.7f,
                reviewCount = 88,
                services = listOf(ServiceTag.PET_FRIENDLY)
            ),
            MaidProfile(
                id = "4",
                name = "Sophie M.",
                rating = 4.8f,
                reviewCount = 103,
                services = listOf(
                    ServiceTag.PET_FRIENDLY,
                    ServiceTag.ECO_FRIENDLY,
                    ServiceTag.LAUNDRY
                )
            ),
        )
    )

    MaidListScreenContent(
        uiState = previewState,
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MaidListScreenLoadingPreview() {
    MaidListScreenContent(
        uiState = MaidListUiState(isLoading = true),
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MaidListScreenEmptyPreview() {
    MaidListScreenContent(
        uiState = MaidListUiState(
            maids = emptyList(),
            filters = listOf(
                FilterOption("1", "Pet Friendly", isSelected = true),
            )
        ),
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MaidListScreenErrorPreview() {
    MaidListScreenContent(
        uiState = MaidListUiState(
            errorMessage = "Failed to load maids. Please try again."
        ),
        onEvent = {}
    )
}

