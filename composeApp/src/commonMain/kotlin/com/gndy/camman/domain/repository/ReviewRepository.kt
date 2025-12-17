package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.CreateReviewRequest
import com.gndy.camman.domain.model.PendingReviewPrompt
import com.gndy.camman.domain.model.Review
import com.gndy.camman.domain.model.ReviewFilter
import com.gndy.camman.domain.model.ReviewResponseRequest
import com.gndy.camman.domain.model.ReviewSortOption
import com.gndy.camman.domain.model.ReviewStatistics
import com.gndy.camman.domain.model.UpdateReviewRequest
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for review operations
 */
interface ReviewRepository {

    // ============== Create/Update Operations ==============

    /**
     * Create a new review
     * @return Resource with the created review or error
     */
    suspend fun createReview(request: CreateReviewRequest, clientId: String): Resource<Review>

    /**
     * Update an existing review (within 48 hours)
     * @return Resource with the updated review or error
     */
    suspend fun updateReview(request: UpdateReviewRequest): Resource<Review>

    /**
     * Add photographer response to a review
     * @return Resource with success or error
     */
    suspend fun addPhotographerResponse(request: ReviewResponseRequest): Resource<Unit>

    /**
     * Mark review as helpful
     * @return Resource with updated helpful count
     */
    suspend fun markReviewAsHelpful(reviewId: String): Resource<Int>

    // ============== Read Operations ==============

    /**
     * Get all reviews for a photographer
     * @param photographerId The photographer's ID
     * @param sortOption How to sort the reviews
     * @param filter Optional filter criteria
     * @return Flow of reviews list wrapped in Resource
     */
    fun getReviewsForPhotographer(
        photographerId: String,
        sortOption: ReviewSortOption = ReviewSortOption.MOST_RECENT,
        filter: ReviewFilter? = null
    ): Flow<Resource<List<Review>>>

    /**
     * Get a specific review by ID
     * @return Flow of the review wrapped in Resource
     */
    fun getReviewById(reviewId: String): Flow<Resource<Review>>

    /**
     * Get review for a specific booking
     * @return Flow of the review (or null) wrapped in Resource
     */
    fun getReviewByBookingId(bookingId: String): Flow<Resource<Review?>>

    /**
     * Get all reviews written by a client
     * @return Flow of reviews list wrapped in Resource
     */
    fun getReviewsByClient(clientId: String): Flow<Resource<List<Review>>>

    /**
     * Check if a booking already has a review
     * @return true if review exists
     */
    suspend fun hasReviewForBooking(bookingId: String): Boolean

    // ============== Statistics ==============

    /**
     * Get review statistics for a photographer
     * @return Resource with statistics
     */
    suspend fun getReviewStatistics(photographerId: String): Resource<ReviewStatistics>

    /**
     * Get review statistics as a flow for real-time updates
     * @return Flow of statistics wrapped in Resource
     */
    fun observeReviewStatistics(photographerId: String): Flow<Resource<ReviewStatistics>>

    // ============== Pending Reviews ==============

    /**
     * Get pending review prompts for a client
     * Bookings that are completed but not yet reviewed
     * @return Flow of pending review prompts
     */
    fun getPendingReviewPrompts(clientId: String): Flow<Resource<List<PendingReviewPrompt>>>

    /**
     * Dismiss a review prompt (client chose to skip)
     */
    suspend fun dismissReviewPrompt(bookingId: String): Resource<Unit>

    // ============== Search ==============

    /**
     * Search reviews by text content
     * @return Flow of matching reviews
     */
    fun searchReviews(photographerId: String, query: String): Flow<Resource<List<Review>>>

    // ============== Validation ==============

    /**
     * Check if review can still be edited
     * @return true if within edit window (48 hours)
     */
    suspend fun canEditReview(reviewId: String): Boolean

    /**
     * Validate review content for moderation
     * @return List of issues found, empty if valid
     */
    suspend fun validateReviewContent(reviewText: String): List<String>
}
