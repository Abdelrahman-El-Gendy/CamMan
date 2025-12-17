package com.gndy.camman.presentation.screens.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gndy.camman.domain.model.ReviewStatistics
import com.gndy.camman.domain.model.ReviewTag

/**
 * Analytics dashboard for photographers to view their review performance
 */
@Composable
fun ReviewAnalyticsDashboard(
    statistics: ReviewStatistics,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Overall Rating Card
        OverallRatingCard(statistics = statistics)

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                title = "Total Reviews",
                value = "${statistics.totalReviews}",
                icon = Icons.Outlined.ChatBubbleOutline,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "Response Rate",
                value = "${statistics.responseRate.toInt()}%",
                icon = Icons.Default.Check,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                title = "Verified",
                value = "${statistics.verifiedReviewCount}",
                icon = Icons.Outlined.ThumbUp,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "Avg Response",
                value = statistics.averageResponseTimeHours?.let { "${it.toInt()}h" } ?: "N/A",
                icon = Icons.Outlined.Schedule,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rating Distribution
        RatingDistributionCard(statistics = statistics)

        Spacer(modifier = Modifier.height(16.dp))

        // Top Tags
        if (statistics.mostCommonTags.isNotEmpty()) {
            TopTagsCard(tags = statistics.mostCommonTags)
        }
    }
}

@Composable
private fun OverallRatingCard(statistics: ReviewStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (statistics.shouldShowPublicRating()) {
                    Text(
                        text = formatRating(statistics.averageRating),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB800)
                    )
                    Row {
                        repeat(5) { index ->
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (index < statistics.averageRating.toInt()) {
                                    Color(0xFFFFB800)
                                } else if (index.toFloat() < statistics.averageRating) {
                                    Color(0xFFFFB800).copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                }
                            )
                        }
                    }
                } else {
                    Icon(
                        Icons.Outlined.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Getting Started",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Performance Indicator
            Column(modifier = Modifier.weight(1f)) {
                if (statistics.shouldShowPublicRating()) {
                    val performanceText = when {
                        statistics.averageRating >= 4.5 -> "Excellent!"
                        statistics.averageRating >= 4.0 -> "Great job!"
                        statistics.averageRating >= 3.5 -> "Good"
                        statistics.averageRating >= 3.0 -> "Average"
                        else -> "Needs Improvement"
                    }

                    val performanceColor = when {
                        statistics.averageRating >= 4.0 -> MaterialTheme.colorScheme.primary
                        statistics.averageRating >= 3.0 -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.error
                    }

                    Text(
                        text = performanceText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = performanceColor
                    )
                    Text(
                        text = "Based on ${statistics.totalReviews} reviews",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Need ${3 - statistics.totalReviews} more reviews",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { statistics.totalReviews / 3f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RatingDistributionCard(statistics: ReviewStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Rating Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rating bars
            RatingDistributionBar(5, statistics.fiveStarPercentage, statistics.fiveStarCount)
            Spacer(modifier = Modifier.height(8.dp))
            RatingDistributionBar(4, statistics.fourStarPercentage, statistics.fourStarCount)
            Spacer(modifier = Modifier.height(8.dp))
            RatingDistributionBar(3, statistics.threeStarPercentage, statistics.threeStarCount)
            Spacer(modifier = Modifier.height(8.dp))
            RatingDistributionBar(2, statistics.twoStarPercentage, statistics.twoStarCount)
            Spacer(modifier = Modifier.height(8.dp))
            RatingDistributionBar(1, statistics.oneStarPercentage, statistics.oneStarCount)
        }
    }
}

@Composable
private fun RatingDistributionBar(
    stars: Int,
    percentage: Float,
    count: Int
) {
    val barColor = when (stars) {
        5 -> Color(0xFF4CAF50)
        4 -> Color(0xFF8BC34A)
        3 -> Color(0xFFFFEB3B)
        2 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.width(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$stars",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFFFFB800)
            )
        }

        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$count",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(32.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TopTagsCard(tags: List<ReviewTag>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Most Common Feedback",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { tag ->
                    Surface(
                        color = if (tag.isPositive) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = tag.displayName,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (tag.isPositive) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact review summary for use in photographer dashboard
 */
@Composable
fun CompactReviewSummary(
    statistics: ReviewStatistics,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onViewAllClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rating
                if (statistics.shouldShowPublicRating()) {
                    Text(
                        text = formatRating(statistics.averageRating),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB800)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "New",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "${statistics.totalReviews} Reviews",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${statistics.responseRate.toInt()}% response rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "View All",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
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
