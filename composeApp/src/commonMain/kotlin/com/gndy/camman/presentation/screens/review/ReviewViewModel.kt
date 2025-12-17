package com.gndy.camman.presentation.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.CreateReviewRequest
import com.gndy.camman.domain.model.Review
import com.gndy.camman.domain.model.ReviewFilter
import com.gndy.camman.domain.model.ReviewResponseRequest
import com.gndy.camman.domain.model.ReviewSortOption
import com.gndy.camman.domain.model.ReviewStatistics
import com.gndy.camman.domain.model.ReviewTag
import com.gndy.camman.domain.model.UpdateReviewRequest
import com.gndy.camman.domain.usecase.review.GetReviewsUseCase
import com.gndy.camman.domain.usecase.review.MarkReviewHelpfulUseCase
import com.gndy.camman.domain.usecase.review.RespondToReviewUseCase
import com.gndy.camman.domain.usecase.review.SubmitReviewUseCase
import com.gndy.camman.domain.usecase.review.UpdateReviewUseCase
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing review-related operations
 */
class ReviewViewModel(
    private val submitReviewUseCase: SubmitReviewUseCase,
    private val getReviewsUseCase: GetReviewsUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
    private val respondToReviewUseCase: RespondToReviewUseCase,
    private val markReviewHelpfulUseCase: MarkReviewHelpfulUseCase
) : ViewModel() {

    // ============== Review Submission State ==============
    private val _submissionState = MutableStateFlow(ReviewSubmissionState())
    val submissionState: StateFlow<ReviewSubmissionState> = _submissionState.asStateFlow()

    // ============== Reviews List State ==============
    private val _reviewsState = MutableStateFlow(ReviewsListState())
    val reviewsState: StateFlow<ReviewsListState> = _reviewsState.asStateFlow()

    // ============== Statistics State ==============
    private val _statisticsState = MutableStateFlow<ReviewStatistics?>(null)
    val statisticsState: StateFlow<ReviewStatistics?> = _statisticsState.asStateFlow()

    // ============== Events ==============
    private val _events = MutableSharedFlow<ReviewEvent>()
    val events: SharedFlow<ReviewEvent> = _events.asSharedFlow()

    // ============== Review Submission ==============

    fun updateRating(rating: Int) {
        _submissionState.update { it.copy(rating = rating) }
    }

    fun updateReviewText(text: String) {
        if (text.length <= Review.MAX_REVIEW_TEXT_LENGTH) {
            _submissionState.update { it.copy(reviewText = text) }
        }
    }

    fun toggleTag(tag: ReviewTag) {
        _submissionState.update { state ->
            val currentTags = state.selectedTags.toMutableList()
            if (currentTags.contains(tag)) {
                currentTags.remove(tag)
            } else {
                currentTags.add(tag)
            }
            state.copy(selectedTags = currentTags)
        }
    }

    fun addPhoto(url: String) {
        _submissionState.update { state ->
            if (state.photoUrls.size < Review.MAX_PHOTOS) {
                state.copy(photoUrls = state.photoUrls + url)
            } else {
                state
            }
        }
    }

    fun removePhoto(index: Int) {
        _submissionState.update { state ->
            state.copy(photoUrls = state.photoUrls.filterIndexed { i, _ -> i != index })
        }
    }

    fun toggleAnonymous(isAnonymous: Boolean) {
        _submissionState.update { it.copy(isAnonymous = isAnonymous) }
    }

    fun submitReview(photographerId: String, bookingId: String, clientId: String) {
        viewModelScope.launch {
            _submissionState.update { it.copy(isSubmitting = true, error = null) }

            val state = _submissionState.value
            val request = CreateReviewRequest(
                photographerId = photographerId,
                bookingId = bookingId,
                rating = state.rating,
                reviewText = state.reviewText,
                tags = state.selectedTags,
                photoUrls = state.photoUrls,
                isAnonymous = state.isAnonymous
            )

            when (val result = submitReviewUseCase(request, clientId)) {
                is Resource.Success -> {
                    _submissionState.update { it.copy(isSubmitting = false, isSubmitted = true) }
                    result.data?.let { _events.emit(ReviewEvent.ReviewSubmitted(it)) }
                }
                is Resource.Error -> {
                    _submissionState.update { it.copy(isSubmitting = false, error = result.message) }
                    _events.emit(ReviewEvent.Error(result.message ?: "Failed to submit review"))
                }
                is Resource.Loading -> { /* Handled by state */ }
            }
        }
    }

    fun resetSubmissionState() {
        _submissionState.value = ReviewSubmissionState()
    }

    // ============== Review Editing ==============

    fun loadReviewForEditing(reviewId: String) {
        viewModelScope.launch {
            getReviewsUseCase.getReviewById(reviewId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { review ->
                            _submissionState.update {
                                it.copy(
                                    rating = review.rating,
                                    reviewText = review.reviewText,
                                    selectedTags = review.tags,
                                    photoUrls = review.photoUrls,
                                    isAnonymous = review.isAnonymous,
                                    editingReviewId = review.id
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _events.emit(ReviewEvent.Error(result.message ?: "Failed to load review"))
                    }
                    is Resource.Loading -> { /* Handled by state */ }
                }
            }
        }
    }

    fun updateReview() {
        viewModelScope.launch {
            val state = _submissionState.value
            val reviewId = state.editingReviewId ?: return@launch

            _submissionState.update { it.copy(isSubmitting = true, error = null) }

            val request = UpdateReviewRequest(
                reviewId = reviewId,
                rating = state.rating,
                reviewText = state.reviewText,
                tags = state.selectedTags,
                photoUrls = state.photoUrls,
                isAnonymous = state.isAnonymous
            )

            when (val result = updateReviewUseCase(request)) {
                is Resource.Success -> {
                    _submissionState.update { it.copy(isSubmitting = false, isSubmitted = true) }
                    result.data?.let { _events.emit(ReviewEvent.ReviewUpdated(it)) }
                }
                is Resource.Error -> {
                    _submissionState.update { it.copy(isSubmitting = false, error = result.message) }
                    _events.emit(ReviewEvent.Error(result.message ?: "Failed to update review"))
                }
                is Resource.Loading -> { /* Handled by state */ }
            }
        }
    }

    // ============== Reviews List ==============

    fun loadReviews(photographerId: String) {
        viewModelScope.launch {
            val state = _reviewsState.value
            getReviewsUseCase.getPhotographerReviews(
                photographerId = photographerId,
                sortOption = state.sortOption,
                filter = state.filter
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _reviewsState.update {
                            it.copy(
                                reviews = result.data ?: emptyList(),
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _reviewsState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                    }
                    is Resource.Loading -> {
                        _reviewsState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun setSortOption(option: ReviewSortOption, photographerId: String) {
        _reviewsState.update { it.copy(sortOption = option) }
        loadReviews(photographerId)
    }

    fun setFilter(filter: ReviewFilter?, photographerId: String) {
        _reviewsState.update { it.copy(filter = filter) }
        loadReviews(photographerId)
    }

    fun filterByRating(rating: Int?, photographerId: String) {
        val newFilter = _reviewsState.value.filter?.copy(rating = rating)
            ?: ReviewFilter(rating = rating)
        setFilter(if (rating == null) null else newFilter, photographerId)
    }

    // ============== Statistics ==============

    fun loadStatistics(photographerId: String) {
        viewModelScope.launch {
            when (val result = getReviewsUseCase.getStatistics(photographerId)) {
                is Resource.Success -> {
                    _statisticsState.value = result.data
                }
                is Resource.Error -> {
                    _events.emit(ReviewEvent.Error(result.message ?: "Failed to load statistics"))
                }
                is Resource.Loading -> { /* Handled by state */ }
            }
        }
    }

    fun observeStatistics(photographerId: String) {
        viewModelScope.launch {
            getReviewsUseCase.observeStatistics(photographerId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _statisticsState.value = result.data
                    }
                    is Resource.Error -> { /* Silently fail for observation */ }
                    is Resource.Loading -> { /* Loading state */ }
                }
            }
        }
    }

    // ============== Photographer Response ==============

    fun respondToReview(reviewId: String, responseText: String) {
        viewModelScope.launch {
            val request = ReviewResponseRequest(reviewId, responseText)
            when (val result = respondToReviewUseCase(request)) {
                is Resource.Success -> {
                    _events.emit(ReviewEvent.ResponseAdded)
                }
                is Resource.Error -> {
                    _events.emit(ReviewEvent.Error(result.message ?: "Failed to add response"))
                }
                is Resource.Loading -> { /* Handled by state */ }
            }
        }
    }

    // ============== Helpful ==============

    fun markAsHelpful(reviewId: String) {
        viewModelScope.launch {
            when (val result = markReviewHelpfulUseCase(reviewId)) {
                is Resource.Success -> {
                    // Update local state
                    result.data?.let { newCount ->
                        _reviewsState.update { state ->
                            state.copy(
                                reviews = state.reviews.map { review ->
                                    if (review.id == reviewId) {
                                        review.copy(helpfulCount = newCount)
                                    } else review
                                }
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _events.emit(ReviewEvent.Error(result.message ?: "Failed to mark as helpful"))
                }
                is Resource.Loading -> { /* Handled by state */ }
            }
        }
    }
}

/**
 * State for review submission form
 */
data class ReviewSubmissionState(
    val rating: Int = 0,
    val reviewText: String = "",
    val selectedTags: List<ReviewTag> = emptyList(),
    val photoUrls: List<String> = emptyList(),
    val isAnonymous: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null,
    val editingReviewId: String? = null
) {
    val isEditing: Boolean get() = editingReviewId != null
    val characterCount: Int get() = reviewText.length
    val maxCharacters: Int get() = Review.MAX_REVIEW_TEXT_LENGTH
    val canSubmit: Boolean get() = rating > 0 && !isSubmitting
}

/**
 * State for reviews list
 */
data class ReviewsListState(
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortOption: ReviewSortOption = ReviewSortOption.MOST_RECENT,
    val filter: ReviewFilter? = null
)

/**
 * Events emitted by the ViewModel
 */
sealed class ReviewEvent {
    data class ReviewSubmitted(val review: Review) : ReviewEvent()
    data class ReviewUpdated(val review: Review) : ReviewEvent()
    data object ResponseAdded : ReviewEvent()
    data class Error(val message: String) : ReviewEvent()
}
