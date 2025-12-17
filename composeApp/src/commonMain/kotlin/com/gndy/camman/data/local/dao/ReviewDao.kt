package com.gndy.camman.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gndy.camman.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Review operations
 */
@Dao
interface ReviewDao {

    // ============== Basic CRUD Operations ==============

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Update
    suspend fun updateReview(review: ReviewEntity)

    @Delete
    suspend fun deleteReview(review: ReviewEntity)

    // ============== Query Operations ==============

    /**
     * Get all reviews for a photographer (not hidden)
     */
    @Query("""
        SELECT * FROM reviews 
        WHERE photographerId = :photographerId AND isHidden = 0
        ORDER BY createdAt DESC
    """)
    fun getReviewsByPhotographerId(photographerId: String): Flow<List<ReviewEntity>>

    /**
     * Get review by ID
     */
    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    fun getReviewById(reviewId: String): Flow<ReviewEntity?>

    /**
     * Get review by booking ID (one review per booking)
     */
    @Query("SELECT * FROM reviews WHERE bookingId = :bookingId")
    fun getReviewByBookingId(bookingId: String): Flow<ReviewEntity?>

    /**
     * Check if a booking already has a review
     */
    @Query("SELECT EXISTS(SELECT 1 FROM reviews WHERE bookingId = :bookingId)")
    suspend fun hasReviewForBooking(bookingId: String): Boolean

    /**
     * Get all reviews by a client
     */
    @Query("SELECT * FROM reviews WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getReviewsByClientId(clientId: String): Flow<List<ReviewEntity>>

    // ============== Filtered Queries ==============

    /**
     * Get reviews by photographer with specific rating
     */
    @Query("""
        SELECT * FROM reviews 
        WHERE photographerId = :photographerId 
        AND rating = :rating 
        AND isHidden = 0
        ORDER BY createdAt DESC
    """)
    fun getReviewsByRating(photographerId: String, rating: Int): Flow<List<ReviewEntity>>

    /**
     * Get verified reviews only
     */
    @Query("""
        SELECT * FROM reviews 
        WHERE photographerId = :photographerId 
        AND isVerified = 1 
        AND isHidden = 0
        ORDER BY createdAt DESC
    """)
    fun getVerifiedReviews(photographerId: String): Flow<List<ReviewEntity>>

    /**
     * Get reviews with photos
     */
    @Query("""
        SELECT * FROM reviews 
        WHERE photographerId = :photographerId 
        AND photoUrls != '' 
        AND isHidden = 0
        ORDER BY createdAt DESC
    """)
    fun getReviewsWithPhotos(photographerId: String): Flow<List<ReviewEntity>>

    /**
     * Get reviews with photographer response
     */
    @Query("""
        SELECT * FROM reviews 
        WHERE photographerId = :photographerId 
        AND photographerResponse IS NOT NULL 
        AND isHidden = 0
        ORDER BY createdAt DESC
    """)
    fun getReviewsWithResponse(photographerId: String): Flow<List<ReviewEntity>>

    // ============== Sorted Queries ==============

    /**
     * Get reviews sorted by rating (highest first)
     */
    @Query("""
        SELECT * FROM reviews 
        WHERE photographerId = :photographerId AND isHidden = 0
        ORDER BY rating DESC, createdAt DESC
    """)
    fun getReviewsByHighestRating(photographerId: String): Flow<List<ReviewEntity>>

    /**
     * Get reviews sorted by rating (lowest first)
     */
    @Query("""
        SELECT * FROM reviews 
        WHERE photographerId = :photographerId AND isHidden = 0
        ORDER BY rating ASC, createdAt DESC
    """)
    fun getReviewsByLowestRating(photographerId: String): Flow<List<ReviewEntity>>

    /**
     * Get reviews sorted by helpful count
     */
    @Query("""
        SELECT * FROM reviews 
        WHERE photographerId = :photographerId AND isHidden = 0
        ORDER BY helpfulCount DESC, createdAt DESC
    """)
    fun getReviewsByMostHelpful(photographerId: String): Flow<List<ReviewEntity>>

    // ============== Statistics Queries ==============

    /**
     * Get count of reviews for a photographer
     */
    @Query("SELECT COUNT(*) FROM reviews WHERE photographerId = :photographerId AND isHidden = 0")
    suspend fun getReviewCount(photographerId: String): Int

    /**
     * Get average rating for a photographer
     */
    @Query("SELECT AVG(rating) FROM reviews WHERE photographerId = :photographerId AND isHidden = 0")
    suspend fun getAverageRating(photographerId: String): Float?

    /**
     * Get count of reviews by rating
     */
    @Query("""
        SELECT COUNT(*) FROM reviews 
        WHERE photographerId = :photographerId 
        AND rating = :rating 
        AND isHidden = 0
    """)
    suspend fun getCountByRating(photographerId: String, rating: Int): Int

    /**
     * Get count of verified reviews
     */
    @Query("""
        SELECT COUNT(*) FROM reviews 
        WHERE photographerId = :photographerId 
        AND isVerified = 1 
        AND isHidden = 0
    """)
    suspend fun getVerifiedReviewCount(photographerId: String): Int

    /**
     * Get count of reviews with photographer response
     */
    @Query("""
        SELECT COUNT(*) FROM reviews 
        WHERE photographerId = :photographerId 
        AND photographerResponse IS NOT NULL 
        AND isHidden = 0
    """)
    suspend fun getResponseCount(photographerId: String): Int

    // ============== Update Operations ==============

    /**
     * Update photographer response
     */
    @Query("""
        UPDATE reviews 
        SET photographerResponse = :response, 
            responseDate = :responseDate, 
            updatedAt = :updatedAt 
        WHERE id = :reviewId
    """)
    suspend fun updatePhotographerResponse(
        reviewId: String,
        response: String,
        responseDate: String,
        updatedAt: String
    )

    /**
     * Increment helpful count
     */
    @Query("UPDATE reviews SET helpfulCount = helpfulCount + 1 WHERE id = :reviewId")
    suspend fun incrementHelpfulCount(reviewId: String)

    /**
     * Hide review (admin moderation)
     */
    @Query("UPDATE reviews SET isHidden = :isHidden WHERE id = :reviewId")
    suspend fun setReviewHidden(reviewId: String, isHidden: Boolean)

    // ============== Admin/Moderation Queries ==============

    /**
     * Get flagged reviews for admin review
     */
    @Query("SELECT * FROM reviews WHERE flaggedForReview = 1 ORDER BY createdAt DESC")
    fun getFlaggedReviews(): Flow<List<ReviewEntity>>

    /**
     * Clear flag after admin review
     */
    @Query("UPDATE reviews SET flaggedForReview = 0 WHERE id = :reviewId")
    suspend fun clearReviewFlag(reviewId: String)

    // ============== Search ==============

    /**
     * Search reviews by text content
     */
    @Query("""
        SELECT * FROM reviews 
        WHERE photographerId = :photographerId 
        AND (reviewText LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
        AND isHidden = 0
        ORDER BY createdAt DESC
    """)
    fun searchReviews(photographerId: String, query: String): Flow<List<ReviewEntity>>
}
