package com.gndy.camman.presentation.screens.review

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

/**
 * Full-screen reviews page for a photographer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotographerReviewsScreen(
    photographerId: String,
    onNavigateBack: () -> Unit,
    onNavigateToWriteReview: (
        photographerId: String,
        bookingId: String,
        photographerName: String,
        photographerImageUrl: String?,
        serviceType: String
    ) -> Unit,
    viewModel: ReviewViewModel = koinViewModel()
) {
    val reviewsState by viewModel.reviewsState.collectAsState()
    val statistics by viewModel.statisticsState.collectAsState()

    LaunchedEffect(photographerId) {
        viewModel.loadReviews(photographerId)
        viewModel.loadStatistics(photographerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Reviews",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        statistics?.let { stats ->
                            if (stats.shouldShowPublicRating()) {
                                Text(
                                    text = "${stats.totalReviews} reviews • ${String.format("%.1f", stats.averageRating)} average",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Statistics Card
            item {
                statistics?.let { stats ->
                    ReviewStatisticsCard(
                        statistics = stats,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Sort and Filter
            item {
                ReviewSortAndFilter(
                    currentSort = reviewsState.sortOption,
                    currentRatingFilter = reviewsState.filter?.rating,
                    onSortChange = { viewModel.setSortOption(it, photographerId) },
                    onRatingFilterChange = { viewModel.filterByRating(it, photographerId) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Loading State
            if (reviewsState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Reviews List
            items(reviewsState.reviews) { review ->
                ReviewCard(
                    review = review,
                    onHelpfulClick = { viewModel.markAsHelpful(review.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Empty State
            if (!reviewsState.isLoading && reviewsState.reviews.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No reviews yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
