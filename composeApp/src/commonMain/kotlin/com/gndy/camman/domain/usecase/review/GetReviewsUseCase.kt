package com.gndy.camman.domain.usecase.review

import com.gndy.camman.domain.model.Review
import com.gndy.camman.domain.model.ReviewFilter
import com.gndy.camman.domain.model.ReviewSortOption
import com.gndy.camman.domain.model.ReviewStatistics
import com.gndy.camman.domain.repository.ReviewRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving reviews and statistics
 */
class GetReviewsUseCase(
    private val reviewRepository: ReviewRepository
) {
    /**
     * Get all reviews for a photographer
     */
    fun getPhotographerReviews(
        photographerId: String,
        sortOption: ReviewSortOption = ReviewSortOption.MOST_RECENT,
        filter: ReviewFilter? = null
    ): Flow<Resource<List<Review>>> {
        return reviewRepository.getReviewsForPhotographer(photographerId, sortOption, filter)
    }

    /**
     * Get a specific review by ID
     */
    fun getReviewById(reviewId: String): Flow<Resource<Review>> {
        return reviewRepository.getReviewById(reviewId)
    }

    /**
     * Get review for a booking
     */
    fun getReviewByBookingId(bookingId: String): Flow<Resource<Review?>> {
        return reviewRepository.getReviewByBookingId(bookingId)
    }

    /**
     * Get all reviews written by a client
     */
    fun getClientReviews(clientId: String): Flow<Resource<List<Review>>> {
        return reviewRepository.getReviewsByClient(clientId)
    }

    /**
     * Get review statistics for a photographer
     */
    suspend fun getStatistics(photographerId: String): Resource<ReviewStatistics> {
        return reviewRepository.getReviewStatistics(photographerId)
    }

    /**
     * Observe review statistics for real-time updates
     */
    fun observeStatistics(photographerId: String): Flow<Resource<ReviewStatistics>> {
        return reviewRepository.observeReviewStatistics(photographerId)
    }

    /**
     * Search reviews
     */
    fun searchReviews(photographerId: String, query: String): Flow<Resource<List<Review>>> {
        return reviewRepository.searchReviews(photographerId, query)
    }

    /**
     * Check if booking has a review
     */
    suspend fun hasReviewForBooking(bookingId: String): Boolean {
        return reviewRepository.hasReviewForBooking(bookingId)
    }
}
