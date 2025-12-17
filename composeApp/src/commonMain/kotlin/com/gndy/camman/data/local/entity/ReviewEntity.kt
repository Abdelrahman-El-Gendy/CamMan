package com.gndy.camman.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gndy.camman.domain.model.Review
import com.gndy.camman.domain.model.ReviewTag
import kotlinx.datetime.LocalDateTime

/**
 * Room entity for storing reviews
 */
@Entity(
    tableName = "reviews",
    indices = [
        Index(value = ["photographerId"]),
        Index(value = ["clientId"]),
        Index(value = ["bookingId"], unique = true),
        Index(value = ["rating"])
    ]
)
data class ReviewEntity(
    @PrimaryKey
    val id: String,
    val clientId: String,
    val photographerId: String,
    val bookingId: String,
    val rating: Int,
    val reviewText: String,
    val tags: String, // Comma-separated list of ReviewTag names
    val photoUrls: String, // Pipe-separated list of URLs
    val isAnonymous: Boolean,
    val isVerified: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val photographerResponse: String?,
    val responseDate: String?,
    val helpfulCount: Int,
    val clientName: String?,
    val clientAvatarUrl: String?,
    val bookingServiceType: String?,
    val bookingDate: String?,
    val isHidden: Boolean = false, // For admin moderation
    val flaggedForReview: Boolean = false // Auto-flagged for admin review
) {
    /**
     * Convert entity to domain model
     */
    fun toReview(): Review = Review(
        id = id,
        clientId = clientId,
        photographerId = photographerId,
        bookingId = bookingId,
        rating = rating,
        reviewText = reviewText,
        tags = parseTags(tags),
        photoUrls = parsePhotoUrls(photoUrls),
        isAnonymous = isAnonymous,
        isVerified = isVerified,
        createdAt = LocalDateTime.parse(createdAt),
        updatedAt = LocalDateTime.parse(updatedAt),
        photographerResponse = photographerResponse,
        responseDate = responseDate?.let { LocalDateTime.parse(it) },
        helpfulCount = helpfulCount,
        clientName = if (isAnonymous) null else clientName,
        clientAvatarUrl = if (isAnonymous) null else clientAvatarUrl,
        bookingServiceType = bookingServiceType,
        bookingDate = bookingDate?.let { LocalDateTime.parse(it) }
    )

    companion object {
        /**
         * Create entity from domain model
         */
        fun fromReview(review: Review): ReviewEntity = ReviewEntity(
            id = review.id,
            clientId = review.clientId,
            photographerId = review.photographerId,
            bookingId = review.bookingId,
            rating = review.rating,
            reviewText = review.reviewText,
            tags = review.tags.joinToString(",") { it.name },
            photoUrls = review.photoUrls.joinToString("|"),
            isAnonymous = review.isAnonymous,
            isVerified = review.isVerified,
            createdAt = review.createdAt.toString(),
            updatedAt = review.updatedAt.toString(),
            photographerResponse = review.photographerResponse,
            responseDate = review.responseDate?.toString(),
            helpfulCount = review.helpfulCount,
            clientName = review.clientName,
            clientAvatarUrl = review.clientAvatarUrl,
            bookingServiceType = review.bookingServiceType,
            bookingDate = review.bookingDate?.toString(),
            isHidden = false,
            flaggedForReview = review.rating <= 2 // Auto-flag low ratings
        )

        private fun parseTags(tagsString: String): List<ReviewTag> {
            if (tagsString.isBlank()) return emptyList()
            return tagsString.split(",").mapNotNull { tagName ->
                try {
                    ReviewTag.valueOf(tagName.trim())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }

        private fun parsePhotoUrls(urlsString: String): List<String> {
            if (urlsString.isBlank()) return emptyList()
            return urlsString.split("|").filter { it.isNotBlank() }
        }
    }
}
