package com.gndy.camman.domain.usecase.review

import com.gndy.camman.domain.model.Review
import com.gndy.camman.domain.model.ReviewResponseRequest
import com.gndy.camman.domain.model.ReviewValidationResult
import com.gndy.camman.domain.repository.ReviewRepository
import com.gndy.camman.domain.util.Resource

/**
 * Use case for photographer to respond to a review
 */
class RespondToReviewUseCase(
    private val reviewRepository: ReviewRepository
) {
    /**
     * Add photographer's response to a review
     *
     * @param request The response request
     * @return Resource with success or error
     */
    suspend operator fun invoke(request: ReviewResponseRequest): Resource<Unit> {
        // Validate the response
        when (val validation = request.validate()) {
            is ReviewValidationResult.Invalid -> {
                return Resource.Error(validation.errors.joinToString(", "))
            }
            ReviewValidationResult.Valid -> {
                // Continue
            }
        }

        return reviewRepository.addPhotographerResponse(request)
    }
}
