package com.gndy.camman.presentation.screens.review

import kotlinx.datetime.toLocalDateTime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gndy.camman.domain.model.Review
import com.gndy.camman.domain.model.ReviewSortOption
import com.gndy.camman.domain.model.ReviewStatistics
import com.gndy.camman.domain.model.ReviewTag
import org.koin.compose.viewmodel.koinViewModel

/**
 * Complete reviews section for photographer profile
 */
@Composable
fun ReviewsSection(
    photographerId: String,
    onWriteReviewClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: ReviewViewModel = koinViewModel()
) {
    val reviewsState by viewModel.reviewsState.collectAsState()
    val statistics by viewModel.statisticsState.collectAsState()

    LaunchedEffect(photographerId) {
        viewModel.loadReviews(photographerId)
        viewModel.loadStatistics(photographerId)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Text(
            text = "Reviews",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Summary
        statistics?.let { stats ->
            ReviewStatisticsCard(
                statistics = stats,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sort and Filter Options
        ReviewSortAndFilter(
            currentSort = reviewsState.sortOption,
            currentRatingFilter = reviewsState.filter?.rating,
            onSortChange = { viewModel.setSortOption(it, photographerId) },
            onRatingFilterChange = { viewModel.filterByRating(it, photographerId) },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reviews List
        when {
            reviewsState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            reviewsState.error != null -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = reviewsState.error ?: "An error occurred",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            reviewsState.reviews.isEmpty() -> {
                EmptyReviewsState(
                    onWriteReviewClick = onWriteReviewClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            else -> {
                Column {
                    reviewsState.reviews.forEach { review ->
                        ReviewCard(
                            review = review,
                            onHelpfulClick = { viewModel.markAsHelpful(review.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Write Review Button
        onWriteReviewClick?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Write a Review")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Statistics card showing rating breakdown
 */
@Composable
fun ReviewStatisticsCard(
    statistics: ReviewStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Average Rating Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (statistics.shouldShowPublicRating()) {
                    Text(
                        text = formatRating(statistics.averageRating),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB800)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (index < statistics.averageRating.toInt()) {
                                    Color(0xFFFFB800)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                }
                            )
                        }
                    }
                    Text(
                        text = "${statistics.totalReviews} reviews",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "New",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Not enough reviews yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Rating Breakdown
            if (statistics.totalReviews > 0) {
                Column(
                    modifier = Modifier.weight(1f).padding(start = 24.dp)
                ) {
                    RatingBar(5, statistics.fiveStarPercentage, statistics.fiveStarCount)
                    RatingBar(4, statistics.fourStarPercentage, statistics.fourStarCount)
                    RatingBar(3, statistics.threeStarPercentage, statistics.threeStarCount)
                    RatingBar(2, statistics.twoStarPercentage, statistics.twoStarCount)
                    RatingBar(1, statistics.oneStarPercentage, statistics.oneStarCount)
                }
            }
        }

        // Additional Stats
        if (statistics.totalReviews > 0) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Verified",
                    value = "${statistics.verifiedReviewCount}"
                )
                StatItem(
                    label = "Response Rate",
                    value = "${statistics.responseRate.toInt()}%"
                )
            }
        }
    }
}

@Composable
private fun RatingBar(
    stars: Int,
    percentage: Float,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$stars",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(16.dp)
        )
        Icon(
            Icons.Filled.Star,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = Color(0xFFFFB800)
        )
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFFFFB800),
            trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(24.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Sort and filter controls
 */
@Composable
fun ReviewSortAndFilter(
    currentSort: ReviewSortOption,
    currentRatingFilter: Int?,
    onSortChange: (ReviewSortOption) -> Unit,
    onRatingFilterChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Sort Dropdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                OutlinedButton(onClick = { showSortMenu = true }) {
                    Text(currentSort.displayName)
                    Icon(
                        if (showSortMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    ReviewSortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                onSortChange(option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rating Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = currentRatingFilter == null,
                    onClick = { onRatingFilterChange(null) },
                    label = { Text("All") }
                )
            }
            items((5 downTo 1).toList()) { rating ->
                FilterChip(
                    selected = currentRatingFilter == rating,
                    onClick = { onRatingFilterChange(rating) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("$rating")
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFFFFB800)
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * Individual review card
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewCard(
    review: Review,
    onHelpfulClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                    if (review.clientAvatarUrl != null && !review.isAnonymous) {
                        AsyncImage(
                            model = review.clientAvatarUrl,
                            contentDescription = review.clientName,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (review.isAnonymous) "?" else (review.clientName?.firstOrNull()?.toString() ?: "?"),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (review.isAnonymous) "Anonymous" else (review.clientName ?: "Guest"),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            if (review.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Text(
                            text = formatReviewDate(review.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Star Rating
                Row {
                    repeat(5) { index ->
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (index < review.rating) {
                                Color(0xFFFFB800)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            }
                        )
                    }
                }
            }

            // Review Text
            if (review.reviewText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = review.reviewText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (review.reviewText.length > 150) {
                    TextButton(
                        onClick = { isExpanded = !isExpanded },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(if (isExpanded) "Show less" else "Read more")
                    }
                }
            }

            // Tags
            if (review.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    review.tags.forEach { tag ->
                        ReviewTagBadge(tag = tag)
                    }
                }
            }

            // Photos
            if (review.photoUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(review.photoUrls) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Review photo",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Photographer Response
            AnimatedVisibility(
                visible = review.photographerResponse != null,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                review.photographerResponse?.let { response ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Photographer's Response",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = response,
                                style = MaterialTheme.typography.bodySmall
                            )
                            review.responseDate?.let { date ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatReviewDate(date),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Footer (Helpful button)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onHelpfulClick,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        if (review.helpfulCount > 0) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Mark as helpful",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (review.helpfulCount > 0) "Helpful (${review.helpfulCount})" else "Helpful",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewTagBadge(tag: ReviewTag) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = tag.displayName,
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (tag.isPositive) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            },
            labelColor = if (tag.isPositive) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onErrorContainer
            }
        ),
        border = null
    )
}

@Composable
private fun EmptyReviewsState(
    onWriteReviewClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No reviews yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Be the first to share your experience!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            onWriteReviewClick?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = it) {
                    Text("Write a Review")
                }
            }
        }
    }
}

/**
 * Helper function to format review dates
 */
private fun formatReviewDate(date: kotlinx.datetime.LocalDateTime): String {
    val now = kotlinx.datetime.Clock.System.now()
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())

    val daysDiff = (now.date.toEpochDays() - date.date.toEpochDays()).toInt()

    return when {
        daysDiff == 0 -> "Today"
        daysDiff == 1 -> "Yesterday"
        daysDiff < 7 -> "$daysDiff days ago"
        daysDiff < 30 -> "${daysDiff / 7} weeks ago"
        daysDiff < 365 -> "${daysDiff / 30} months ago"
        else -> "${daysDiff / 365} years ago"
    }
}

/**
 * Helper function to format rating with one decimal place
 */
private fun formatRating(rating: Float): String {
    val intPart = rating.toInt()
    val decPart = ((rating - intPart) * 10).toInt()
    return "$intPart.$decPart"
}
