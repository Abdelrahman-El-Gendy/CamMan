package com.gndy.camman.domain.usecase.review

import com.gndy.camman.domain.repository.ReviewRepository
import com.gndy.camman.domain.util.Resource

/**
 * Use case for marking a review as helpful
 */
class MarkReviewHelpfulUseCase(
    private val reviewRepository: ReviewRepository
) {
    /**
     * Mark a review as helpful
     *
     * @param reviewId The ID of the review
     * @return Resource with updated helpful count
     */
    suspend operator fun invoke(reviewId: String): Resource<Int> {
        return reviewRepository.markReviewAsHelpful(reviewId)
    }
}
