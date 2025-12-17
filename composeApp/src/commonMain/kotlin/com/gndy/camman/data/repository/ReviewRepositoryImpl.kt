package com.gndy.camman.data.repository

import com.gndy.camman.data.local.dao.ReviewDao
import com.gndy.camman.data.local.entity.ReviewEntity
import com.gndy.camman.domain.model.CreateReviewRequest
import com.gndy.camman.domain.model.PendingReviewPrompt
import com.gndy.camman.domain.model.Review
import com.gndy.camman.domain.model.ReviewFilter
import com.gndy.camman.domain.model.ReviewResponseRequest
import com.gndy.camman.domain.model.ReviewSortOption
import com.gndy.camman.domain.model.ReviewStatistics
import com.gndy.camman.domain.model.ReviewTag
import com.gndy.camman.domain.model.UpdateReviewRequest
import com.gndy.camman.domain.repository.ReviewRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Implementation of ReviewRepository using Room database
 */
class ReviewRepositoryImpl(
    private val reviewDao: ReviewDao
) : ReviewRepository {

    // ============== Create/Update Operations ==============

    override suspend fun createReview(
        request: CreateReviewRequest,
        clientId: String
    ): Resource<Review> {
        return try {
            // Check if booking already has a review
            if (reviewDao.hasReviewForBooking(request.bookingId)) {
                return Resource.Error("A review for this booking already exists")
            }

            // Validate content
            val contentIssues = validateReviewContent(request.reviewText)
            if (contentIssues.isNotEmpty()) {
                return Resource.Error("Review contains invalid content: ${contentIssues.joinToString(", ")}")
            }

            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val reviewId = "review_${Clock.System.now().toEpochMilliseconds()}"

            val review = Review(
                id = reviewId,
                clientId = clientId,
                photographerId = request.photographerId,
                bookingId = request.bookingId,
                rating = request.rating,
                reviewText = request.reviewText,
                tags = request.tags,
                photoUrls = request.photoUrls,
                isAnonymous = request.isAnonymous,
                isVerified = true, // From booking = verified
                createdAt = now,
                updatedAt = now,
                photographerResponse = null,
                responseDate = null,
                helpfulCount = 0,
                clientName = if (request.isAnonymous) null else "Client", // TODO: Get from client profile
                clientAvatarUrl = null,
                bookingServiceType = null, // TODO: Get from booking
                bookingDate = null // TODO: Get from booking
            )

            val entity = ReviewEntity.fromReview(review)
            reviewDao.insertReview(entity)

            Resource.Success(review)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create review")
        }
    }

    override suspend fun updateReview(request: UpdateReviewRequest): Resource<Review> {
        return try {
            val existingEntity = reviewDao.getReviewById(request.reviewId).first()
                ?: return Resource.Error("Review not found")

            val existingReview = existingEntity.toReview()

            // Check if within edit window
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            if (!existingReview.canEdit(now)) {
                return Resource.Error("Review can no longer be edited (48 hour window expired)")
            }

            // Validate content
            val contentIssues = validateReviewContent(request.reviewText)
            if (contentIssues.isNotEmpty()) {
                return Resource.Error("Review contains invalid content: ${contentIssues.joinToString(", ")}")
            }

            val updatedReview = existingReview.copy(
                rating = request.rating,
                reviewText = request.reviewText,
                tags = request.tags,
                photoUrls = request.photoUrls,
                isAnonymous = request.isAnonymous,
                updatedAt = now
            )

            val updatedEntity = ReviewEntity.fromReview(updatedReview)
            reviewDao.updateReview(updatedEntity)

            Resource.Success(updatedReview)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update review")
        }
    }

    override suspend fun addPhotographerResponse(request: ReviewResponseRequest): Resource<Unit> {
        return try {
            val existingEntity = reviewDao.getReviewById(request.reviewId).first()
                ?: return Resource.Error("Review not found")

            val existingReview = existingEntity.toReview()

            if (!existingReview.canPhotographerRespond()) {
                return Resource.Error("Photographer has already responded to this review")
            }

            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            reviewDao.updatePhotographerResponse(
                reviewId = request.reviewId,
                response = request.responseText,
                responseDate = now.toString(),
                updatedAt = now.toString()
            )

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add response")
        }
    }

    override suspend fun markReviewAsHelpful(reviewId: String): Resource<Int> {
        return try {
            reviewDao.incrementHelpfulCount(reviewId)
            val review = reviewDao.getReviewById(reviewId).first()
            Resource.Success(review?.helpfulCount ?: 0)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark as helpful")
        }
    }

    // ============== Read Operations ==============

    override fun getReviewsForPhotographer(
        photographerId: String,
        sortOption: ReviewSortOption,
        filter: ReviewFilter?
    ): Flow<Resource<List<Review>>> {
        val baseFlow = when (sortOption) {
            ReviewSortOption.MOST_RECENT -> reviewDao.getReviewsByPhotographerId(photographerId)
            ReviewSortOption.HIGHEST_RATED -> reviewDao.getReviewsByHighestRating(photographerId)
            ReviewSortOption.LOWEST_RATED -> reviewDao.getReviewsByLowestRating(photographerId)
            ReviewSortOption.MOST_HELPFUL -> reviewDao.getReviewsByMostHelpful(photographerId)
        }

        return baseFlow
            .map { entities ->
                var reviews = entities.map { it.toReview() }

                // Apply filters
                filter?.let { f ->
                    if (f.rating != null) {
                        reviews = reviews.filter { it.rating == f.rating }
                    }
                    if (f.verifiedOnly) {
                        reviews = reviews.filter { it.isVerified }
                    }
                    if (f.withPhotosOnly) {
                        reviews = reviews.filter { it.photoUrls.isNotEmpty() }
                    }
                    if (f.withResponseOnly) {
                        reviews = reviews.filter { it.photographerResponse != null }
                    }
                }

                Resource.Success(reviews) as Resource<List<Review>>
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to load reviews")) }
    }

    override fun getReviewById(reviewId: String): Flow<Resource<Review>> = flow {
        emit(Resource.Loading())
        reviewDao.getReviewById(reviewId).collect { entity ->
            if (entity != null) {
                emit(Resource.Success(entity.toReview()))
            } else {
                emit(Resource.Error("Review not found"))
            }
        }
    }.catch { e -> emit(Resource.Error(e.message ?: "Failed to load review")) }

    override fun getReviewByBookingId(bookingId: String): Flow<Resource<Review?>> =
        reviewDao.getReviewByBookingId(bookingId)
            .map { entity ->
                Resource.Success(entity?.toReview()) as Resource<Review?>
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to load review")) }

    override fun getReviewsByClient(clientId: String): Flow<Resource<List<Review>>> =
        reviewDao.getReviewsByClientId(clientId)
            .map { entities ->
                Resource.Success(entities.map { it.toReview() }) as Resource<List<Review>>
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to load reviews")) }

    override suspend fun hasReviewForBooking(bookingId: String): Boolean {
        return try {
            reviewDao.hasReviewForBooking(bookingId)
        } catch (e: Exception) {
            false
        }
    }

    // ============== Statistics ==============

    override suspend fun getReviewStatistics(photographerId: String): Resource<ReviewStatistics> {
        return try {
            val totalReviews = reviewDao.getReviewCount(photographerId)
            val averageRating = reviewDao.getAverageRating(photographerId) ?: 0f
            val fiveStarCount = reviewDao.getCountByRating(photographerId, 5)
            val fourStarCount = reviewDao.getCountByRating(photographerId, 4)
            val threeStarCount = reviewDao.getCountByRating(photographerId, 3)
            val twoStarCount = reviewDao.getCountByRating(photographerId, 2)
            val oneStarCount = reviewDao.getCountByRating(photographerId, 1)
            val verifiedCount = reviewDao.getVerifiedReviewCount(photographerId)
            val responseCount = reviewDao.getResponseCount(photographerId)

            val responseRate = if (totalReviews > 0) {
                (responseCount.toFloat() / totalReviews) * 100
            } else 0f

            // Get most common tags
            val reviews = reviewDao.getReviewsByPhotographerId(photographerId).first()
            val tagCounts = mutableMapOf<ReviewTag, Int>()
            reviews.forEach { entity ->
                entity.toReview().tags.forEach { tag ->
                    tagCounts[tag] = (tagCounts[tag] ?: 0) + 1
                }
            }
            val mostCommonTags = tagCounts.entries
                .sortedByDescending { it.value }
                .take(5)
                .map { it.key }

            Resource.Success(
                ReviewStatistics(
                    photographerId = photographerId,
                    averageRating = averageRating,
                    totalReviews = totalReviews,
                    fiveStarCount = fiveStarCount,
                    fourStarCount = fourStarCount,
                    threeStarCount = threeStarCount,
                    twoStarCount = twoStarCount,
                    oneStarCount = oneStarCount,
                    verifiedReviewCount = verifiedCount,
                    responseRate = responseRate,
                    averageResponseTimeHours = null, // TODO: Calculate from response dates
                    mostCommonTags = mostCommonTags
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load statistics")
        }
    }

    override fun observeReviewStatistics(photographerId: String): Flow<Resource<ReviewStatistics>> =
        reviewDao.getReviewsByPhotographerId(photographerId)
            .map { _ ->
                // Recalculate stats whenever reviews change
                getReviewStatistics(photographerId)
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to observe statistics")) }

    // ============== Pending Reviews ==============

    override fun getPendingReviewPrompts(clientId: String): Flow<Resource<List<PendingReviewPrompt>>> {
        // TODO: Implement by checking completed bookings without reviews
        return flow {
            emit(Resource.Success(emptyList<PendingReviewPrompt>()))
        }
    }

    override suspend fun dismissReviewPrompt(bookingId: String): Resource<Unit> {
        // TODO: Implement by storing dismissed prompts
        return Resource.Success(Unit)
    }

    // ============== Search ==============

    override fun searchReviews(
        photographerId: String,
        query: String
    ): Flow<Resource<List<Review>>> =
        reviewDao.searchReviews(photographerId, query)
            .map { entities ->
                Resource.Success(entities.map { it.toReview() }) as Resource<List<Review>>
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Search failed")) }

    // ============== Validation ==============

    override suspend fun canEditReview(reviewId: String): Boolean {
        return try {
            val entity = reviewDao.getReviewById(reviewId).first() ?: return false
            val review = entity.toReview()
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            review.canEdit(now)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun validateReviewContent(reviewText: String): List<String> {
        val issues = mutableListOf<String>()

        // Check for personal information patterns
        val emailPattern = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        val phonePattern = Regex("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b")

        if (emailPattern.containsMatchIn(reviewText)) {
            issues.add("Personal email addresses are not allowed")
        }
        if (phonePattern.containsMatchIn(reviewText)) {
            issues.add("Phone numbers are not allowed")
        }

        // Check for profanity (simplified - in production use a profanity filter library)
        val profanityWords = listOf("spam", "fake", "scam") // Simplified list
        profanityWords.forEach { word ->
            if (reviewText.lowercase().contains(word)) {
                issues.add("Review may contain inappropriate content")
            }
        }

        return issues
    }
}
