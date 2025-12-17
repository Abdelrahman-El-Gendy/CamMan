package com.gndy.camman.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Domain model representing a client review for a photographer
 */
data class Review(
    val id: String,
    val clientId: String,
    val photographerId: String,
    val bookingId: String,
    val rating: Int, // 1-5 stars
    val reviewText: String,
    val tags: List<ReviewTag>,
    val photoUrls: List<String>,
    val isAnonymous: Boolean,
    val isVerified: Boolean, // True if from confirmed booking
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val photographerResponse: String?,
    val responseDate: LocalDateTime?,
    val helpfulCount: Int,
    val clientName: String?, // Null if anonymous
    val clientAvatarUrl: String?, // Null if anonymous
    val bookingServiceType: String?, // Service type from booking for context
    val bookingDate: LocalDateTime? // Date of the booking for context
) {
    /**
     * Check if review can still be edited (within 48 hours of creation)
     */
    fun canEdit(currentTime: LocalDateTime): Boolean {
        // Simplified check - in production use proper duration calculation
        val hoursSinceCreation = (currentTime.date.toEpochDays() - createdAt.date.toEpochDays()) * 24 +
                (currentTime.hour - createdAt.hour)
        return hoursSinceCreation <= 48
    }

    /**
     * Check if photographer can respond (only once per review)
     */
    fun canPhotographerRespond(): Boolean = photographerResponse == null

    companion object {
        const val MAX_REVIEW_TEXT_LENGTH = 500
        const val MAX_PHOTOS = 3
        const val MIN_REVIEWS_FOR_PUBLIC_RATING = 3
    }
}

/**
 * Predefined tags for review feedback
 */
enum class ReviewTag(val displayName: String, val isPositive: Boolean) {
    // Positive tags
    PROFESSIONAL("Professional", true),
    ON_TIME("On Time", true),
    GREAT_QUALITY("Great Quality", true),
    GOOD_COMMUNICATION("Good Communication", true),
    CREATIVE("Creative", true),
    FRIENDLY("Friendly", true),
    PATIENT("Patient", true),
    EXCEEDED_EXPECTATIONS("Exceeded Expectations", true),
    QUICK_DELIVERY("Quick Delivery", true),
    GREAT_VALUE("Great Value", true),

    // Neutral/Negative tags
    LATE("Late", false),
    POOR_COMMUNICATION("Poor Communication", false),
    UNPROFESSIONAL("Unprofessional", false),
    SLOW_DELIVERY("Slow Delivery", false),
    OVERPRICED("Overpriced", false),
    QUALITY_ISSUES("Quality Issues", false)
}

/**
 * Statistics for a photographer's reviews
 */
data class ReviewStatistics(
    val photographerId: String,
    val averageRating: Float,
    val totalReviews: Int,
    val fiveStarCount: Int,
    val fourStarCount: Int,
    val threeStarCount: Int,
    val twoStarCount: Int,
    val oneStarCount: Int,
    val verifiedReviewCount: Int,
    val responseRate: Float, // Percentage of reviews with photographer response
    val averageResponseTimeHours: Float?, // Average time to respond to reviews
    val mostCommonTags: List<ReviewTag>
) {
    val fiveStarPercentage: Float get() = if (totalReviews > 0) (fiveStarCount * 100f) / totalReviews else 0f
    val fourStarPercentage: Float get() = if (totalReviews > 0) (fourStarCount * 100f) / totalReviews else 0f
    val threeStarPercentage: Float get() = if (totalReviews > 0) (threeStarCount * 100f) / totalReviews else 0f
    val twoStarPercentage: Float get() = if (totalReviews > 0) (twoStarCount * 100f) / totalReviews else 0f
    val oneStarPercentage: Float get() = if (totalReviews > 0) (oneStarCount * 100f) / totalReviews else 0f

    /**
     * Check if rating should be publicly displayed (requires minimum reviews)
     */
    fun shouldShowPublicRating(): Boolean = totalReviews >= Review.MIN_REVIEWS_FOR_PUBLIC_RATING

    companion object {
        fun empty(photographerId: String) = ReviewStatistics(
            photographerId = photographerId,
            averageRating = 0f,
            totalReviews = 0,
            fiveStarCount = 0,
            fourStarCount = 0,
            threeStarCount = 0,
            twoStarCount = 0,
            oneStarCount = 0,
            verifiedReviewCount = 0,
            responseRate = 0f,
            averageResponseTimeHours = null,
            mostCommonTags = emptyList()
        )
    }
}

/**
 * Sort options for reviews
 */
enum class ReviewSortOption(val displayName: String) {
    MOST_RECENT("Most Recent"),
    HIGHEST_RATED("Highest Rated"),
    LOWEST_RATED("Lowest Rated"),
    MOST_HELPFUL("Most Helpful")
}

/**
 * Filter options for reviews
 */
data class ReviewFilter(
    val rating: Int? = null, // Filter by specific rating (1-5)
    val verifiedOnly: Boolean = false,
    val withPhotosOnly: Boolean = false,
    val withResponseOnly: Boolean = false
)

/**
 * Request model for creating a new review
 */
data class CreateReviewRequest(
    val photographerId: String,
    val bookingId: String,
    val rating: Int,
    val reviewText: String,
    val tags: List<ReviewTag>,
    val photoUrls: List<String>,
    val isAnonymous: Boolean
) {
    fun validate(): ReviewValidationResult {
        val errors = mutableListOf<String>()

        if (rating !in 1..5) {
            errors.add("Rating must be between 1 and 5")
        }
        if (reviewText.length > Review.MAX_REVIEW_TEXT_LENGTH) {
            errors.add("Review text exceeds ${Review.MAX_REVIEW_TEXT_LENGTH} characters")
        }
        if (photoUrls.size > Review.MAX_PHOTOS) {
            errors.add("Maximum ${Review.MAX_PHOTOS} photos allowed")
        }

        return if (errors.isEmpty()) {
            ReviewValidationResult.Valid
        } else {
            ReviewValidationResult.Invalid(errors)
        }
    }
}

/**
 * Request model for updating a review
 */
data class UpdateReviewRequest(
    val reviewId: String,
    val rating: Int,
    val reviewText: String,
    val tags: List<ReviewTag>,
    val photoUrls: List<String>,
    val isAnonymous: Boolean
)

/**
 * Request model for photographer response
 */
data class ReviewResponseRequest(
    val reviewId: String,
    val responseText: String
) {
    fun validate(): ReviewValidationResult {
        val errors = mutableListOf<String>()

        if (responseText.isBlank()) {
            errors.add("Response cannot be empty")
        }
        if (responseText.length > Review.MAX_REVIEW_TEXT_LENGTH) {
            errors.add("Response exceeds ${Review.MAX_REVIEW_TEXT_LENGTH} characters")
        }

        return if (errors.isEmpty()) {
            ReviewValidationResult.Valid
        } else {
            ReviewValidationResult.Invalid(errors)
        }
    }
}

/**
 * Validation result for review operations
 */
sealed class ReviewValidationResult {
    data object Valid : ReviewValidationResult()
    data class Invalid(val errors: List<String>) : ReviewValidationResult()
}

/**
 * Model for pending review prompts
 */
data class PendingReviewPrompt(
    val bookingId: String,
    val photographerId: String,
    val photographerName: String,
    val photographerImageUrl: String?,
    val serviceType: String,
    val sessionDate: LocalDateTime,
    val promptReason: ReviewPromptReason
)

/**
 * Reasons for showing review prompt
 */
enum class ReviewPromptReason {
    BOOKING_COMPLETED,
    TIME_ELAPSED_AFTER_SESSION,
    PHOTOS_DELIVERED
}
