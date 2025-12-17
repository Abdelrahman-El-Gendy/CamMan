package com.gndy.camman.domain.usecase.review

import com.gndy.camman.domain.model.CreateReviewRequest
import com.gndy.camman.domain.model.Review
import com.gndy.camman.domain.model.ReviewValidationResult
import com.gndy.camman.domain.repository.ReviewRepository
import com.gndy.camman.domain.util.Resource

/**
 * Use case for submitting a new review
 */
class SubmitReviewUseCase(
    private val reviewRepository: ReviewRepository
) {
    /**
     * Submit a new review for a photographer
     *
     * @param request The review creation request
     * @param clientId The ID of the client submitting the review
     * @return Resource with the created review or error
     */
    suspend operator fun invoke(
        request: CreateReviewRequest,
        clientId: String
    ): Resource<Review> {
        // Validate the request
        when (val validation = request.validate()) {
            is ReviewValidationResult.Invalid -> {
                return Resource.Error(validation.errors.joinToString(", "))
            }
            ReviewValidationResult.Valid -> {
                // Continue with creation
            }
        }

        // Check if booking already has a review
        if (reviewRepository.hasReviewForBooking(request.bookingId)) {
            return Resource.Error("You have already reviewed this booking")
        }

        return reviewRepository.createReview(request, clientId)
    }
}
