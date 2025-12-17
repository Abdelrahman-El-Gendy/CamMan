package com.gndy.camman.domain.usecase.review

import com.gndy.camman.domain.model.Review
import com.gndy.camman.domain.model.UpdateReviewRequest
import com.gndy.camman.domain.repository.ReviewRepository
import com.gndy.camman.domain.util.Resource

/**
 * Use case for updating an existing review (within 48 hours)
 */
class UpdateReviewUseCase(
    private val reviewRepository: ReviewRepository
) {
    /**
     * Update a review
     *
     * @param request The update request
     * @return Resource with the updated review or error
     */
    suspend operator fun invoke(request: UpdateReviewRequest): Resource<Review> {
        // Check if review can still be edited
        if (!reviewRepository.canEditReview(request.reviewId)) {
            return Resource.Error("Review can no longer be edited (48 hour window expired)")
        }

        // Validate content
        val issues = reviewRepository.validateReviewContent(request.reviewText)
        if (issues.isNotEmpty()) {
            return Resource.Error("Invalid content: ${issues.joinToString(", ")}")
        }

        return reviewRepository.updateReview(request)
    }

    /**
     * Check if a review can be edited
     */
    suspend fun canEdit(reviewId: String): Boolean {
        return reviewRepository.canEditReview(reviewId)
    }
}
