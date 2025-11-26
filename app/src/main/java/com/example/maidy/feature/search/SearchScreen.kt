package com.example.maidy.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
import com.example.maidy.core.model.Maid
import com.example.maidy.feature.search.components.SearchMaidCard
import com.example.maidy.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onMaidClick: (String) -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onMaidClick = onMaidClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenContent(
    uiState: SearchUiState,
    onEvent: (SearchUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onMaidClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaidyBackgroundWhite)
    ) {
        // Top Bar with Search Field
        Surface(
            shadowElevation = 2.dp,
            color = MaidyBackgroundWhite
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaidyTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Search TextField
                    TextField(
                        value = uiState.searchQuery,
                        onValueChange = { query ->
                            onEvent(SearchUiEvent.OnSearchQueryChange(query))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        placeholder = {
                            Text(
                                text = "Search for maids...",
                                fontSize = 16.sp,
                                color = HomeSearchHint
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = HomeSearchIcon
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onEvent(SearchUiEvent.OnClearSearch) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = HomeSearchIcon
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = HomeSearchBackground,
                            unfocusedContainerColor = HomeSearchBackground,
                            disabledContainerColor = HomeSearchBackground,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        // Content
        when {
            uiState.isSearching -> {
                // Searching State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Searching...",
                            fontSize = 14.sp,
                            color = MaidyTextSecondary
                        )
                    }
                }
            }

            uiState.error != null -> {
                // Error State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error,
                        fontSize = 16.sp,
                        color = MaidyErrorRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            uiState.searchQuery.isEmpty() -> {
                // Empty Search State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaidyTextSecondary,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Search for maids",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaidyTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start typing to find maids by name, specialty, or service",
                            fontSize = 14.sp,
                            color = MaidyTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            uiState.searchResults.isEmpty() -> {
                // No Results State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "😔",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No maids found",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaidyTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try searching with different keywords",
                            fontSize = 14.sp,
                            color = MaidyTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                // Results List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Results Count
                    item {
                        Text(
                            text = "${uiState.searchResults.size} ${if (uiState.searchResults.size == 1) "maid" else "maids"} found",
                            fontSize = 14.sp,
                            color = MaidyTextSecondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Maids List
                    items(
                        items = uiState.searchResults,
                        key = { maid -> maid.id }
                    ) { maid ->
                        SearchMaidCard(
                            maid = maid,
                            onClick = {
                                onEvent(SearchUiEvent.OnMaidClick(maid.id))
                                onMaidClick(maid.id)
                            }
                        )
                    }

                    // Bottom Spacing
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchScreenEmptyPreview() {
    MaidyTheme {
        SearchScreenContent(
            uiState = SearchUiState(
                searchQuery = "",
                searchResults = emptyList(),
                isLoading = false,
                isSearching = false
            ),
            onEvent = {},
            onNavigateBack = {},
            onMaidClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchScreenWithResultsPreview() {
    MaidyTheme {
        SearchScreenContent(
            uiState = SearchUiState(
                searchQuery = "Elena",
                searchResults = listOf(
                    Maid(
                        id = "1",
                        fullName = "Elena Rodriguez",
                        profileImageUrl = "",
                        verified = true,
                        averageRating = 4.9,
                        reviewCount = 125,
                        specialtyTag = "Gold",
                        hourlyRate = 25.0,
                        available = true
                    ),
                    Maid(
                        id = "2",
                        fullName = "Elena Martinez",
                        profileImageUrl = "",
                        verified = false,
                        averageRating = 4.5,
                        reviewCount = 75,
                        specialtyTag = "Silver",
                        hourlyRate = 20.0,
                        available = true
                    )
                ),
                isLoading = false,
                isSearching = false
            ),
            onEvent = {},
            onNavigateBack = {},
            onMaidClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchScreenNoResultsPreview() {
    MaidyTheme {
        SearchScreenContent(
            uiState = SearchUiState(
                searchQuery = "xyz",
                searchResults = emptyList(),
                isLoading = false,
                isSearching = false
            ),
            onEvent = {},
            onNavigateBack = {},
            onMaidClick = {}
        )
    }
}
