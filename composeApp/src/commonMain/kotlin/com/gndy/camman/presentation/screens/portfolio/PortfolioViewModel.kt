package com.gndy.camman.presentation.screens.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.Album
import com.gndy.camman.domain.model.AlbumCategory
import com.gndy.camman.domain.model.Photo
import com.gndy.camman.domain.usecase.album.GetAlbumPhotosUseCase
import com.gndy.camman.domain.usecase.album.GetAlbumsUseCase
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.LocalDate

// ============== Portfolio Albums Screen ==============

data class PortfolioAlbumsUiState(
    val isLoading: Boolean = true,
    val albums: List<Album> = emptyList(),
    val selectedCategory: AlbumCategory? = null,
    val categories: List<AlbumCategory> = AlbumCategory.entries,
    val error: String? = null
)

sealed class PortfolioAlbumsUiEvent {
    data class NavigateToAlbum(val albumId: String) : PortfolioAlbumsUiEvent()
    data class ShowError(val message: String) : PortfolioAlbumsUiEvent()
}

class PortfolioAlbumsViewModel(
    private val getAlbumsUseCase: GetAlbumsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioAlbumsUiState())
    val uiState: StateFlow<PortfolioAlbumsUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<PortfolioAlbumsUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        loadAlbums()
    }

    private fun loadAlbums() {
        // ===== MOCK DATA IMPLEMENTATION (for demo/testing) =====
        // Remove this block and uncomment the original implementation below when backend is ready
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val albums = tryLoadAlbums()
            val filteredAlbums = _uiState.value.selectedCategory?.let { category ->
                albums.filter { it.category == category }
            } ?: albums

            _uiState.update {
                it.copy(
                    albums = filteredAlbums.ifEmpty { getMockAlbums() },
                    isLoading = false
                )
            }
        }

        // ===== ORIGINAL IMPLEMENTATION (uncomment when backend is ready) =====
        /*
        viewModelScope.launch {
            val flow = _uiState.value.selectedCategory?.let { category ->
                getAlbumsUseCase.getByCategory(category)
            } ?: getAlbumsUseCase()
            
            flow.collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                albums = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
        */
    }

    // ===== HELPER METHODS FOR MOCK DATA FALLBACK =====
    private suspend fun tryLoadAlbums(): List<Album> {
        return try {
            withTimeoutOrNull(3000) {
                val flow = _uiState.value.selectedCategory?.let { category ->
                    getAlbumsUseCase.getByCategory(category)
                } ?: getAlbumsUseCase()

                val result = flow.first { it !is Resource.Loading }
                (result as? Resource.Success)?.data ?: emptyList()
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ===== MOCK DATA (for demo purposes - remove when backend is ready) =====
    private fun getMockAlbums(): List<Album> {
        val allAlbums = listOf(
            Album(
                "1", "Wedding Collection", "Beautiful wedding moments",
                "https://images.unsplash.com/photo-1519741497674-611481863552?w=600",
                AlbumCategory.WEDDING, 45, LocalDate(2024, 6, 15), true
            ),
            Album(
                "2", "Portrait Sessions", "Professional portraits",
                "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=600",
                AlbumCategory.PORTRAIT, 32, LocalDate(2024, 5, 20), true
            ),
            Album(
                "3", "Nature & Landscapes", "Breathtaking scenery",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                AlbumCategory.LANDSCAPE, 28, LocalDate(2024, 4, 10), true
            ),
            Album(
                "4", "Corporate Events", "Professional coverage",
                "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=600",
                AlbumCategory.EVENT, 56, LocalDate(2024, 3, 5), false
            ),
            Album(
                "5", "Family Moments", "Cherished family memories",
                "https://images.unsplash.com/photo-1511895426328-dc8714191300?w=600",
                AlbumCategory.FAMILY, 24, LocalDate(2024, 2, 14), false
            ),
            Album(
                "6", "Product Photography", "Commercial product shots",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
                AlbumCategory.PRODUCT, 40, LocalDate(2024, 1, 8), false
            )
        )

        return _uiState.value.selectedCategory?.let { category ->
            allAlbums.filter { it.category == category }
        } ?: allAlbums
    }
    // ===== END MOCK DATA =====

    fun onCategorySelected(category: AlbumCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadAlbums()
    }

    fun onAlbumClick(albumId: String) {
        viewModelScope.launch {
            _uiEvents.emit(PortfolioAlbumsUiEvent.NavigateToAlbum(albumId))
        }
    }

    fun refresh() {
        loadAlbums()
    }
}

// ============== Album Detail Screen ==============

data class AlbumDetailUiState(
    val isLoading: Boolean = true,
    val album: Album? = null,
    val photos: List<Photo> = emptyList(),
    val error: String? = null
)

sealed class AlbumDetailUiEvent {
    data class NavigateToPhoto(val photoId: String) : AlbumDetailUiEvent()
    data class ShowError(val message: String) : AlbumDetailUiEvent()
}

class AlbumDetailViewModel(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getAlbumPhotosUseCase: GetAlbumPhotosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetailUiState())
    val uiState: StateFlow<AlbumDetailUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<AlbumDetailUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun loadAlbum(albumId: String) {
        // ===== MOCK DATA IMPLEMENTATION (for demo/testing) =====
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val photos = tryLoadPhotos(albumId)

            _uiState.update {
                it.copy(
                    photos = photos.ifEmpty { getMockPhotos(albumId) },
                    isLoading = false
                )
            }
        }

        // ===== ORIGINAL IMPLEMENTATION (uncomment when backend is ready) =====
        /*
        viewModelScope.launch {
            getAlbumPhotosUseCase(albumId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                photos = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
        */
    }

    // ===== HELPER METHODS FOR MOCK DATA FALLBACK =====
    private suspend fun tryLoadPhotos(albumId: String): List<Photo> {
        return try {
            withTimeoutOrNull(3000) {
                val result = getAlbumPhotosUseCase(albumId).first { it !is Resource.Loading }
                (result as? Resource.Success)?.data ?: emptyList()
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ===== MOCK DATA (for demo purposes - remove when backend is ready) =====
    private fun getMockPhotos(albumId: String): List<Photo> {
        val baseUrls = listOf(
            "https://images.unsplash.com/photo-1519741497674-611481863552",
            "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04",
            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4",
            "https://images.unsplash.com/photo-1540575467063-178a50c2df87",
            "https://images.unsplash.com/photo-1511895426328-dc8714191300",
            "https://images.unsplash.com/photo-1523275335684-37898b6baf30",
            "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            "https://images.unsplash.com/photo-1517841905240-472988babdf9"
        )

        return baseUrls.mapIndexed { index, url ->
            Photo(
                id = "${albumId}_$index",
                albumId = albumId,
                imageUrl = "$url?w=1200",
                thumbnailUrl = "$url?w=400",
                title = "Photo ${index + 1}",
                description = null,
                width = 1200,
                height = 800,
                takenAt = null,
                location = null,
                tags = emptyList(),
                isFeatured = index < 3,
                order = index
            )
        }
    }
    // ===== END MOCK DATA =====

    fun onPhotoClick(photoId: String) {
        viewModelScope.launch {
            _uiEvents.emit(AlbumDetailUiEvent.NavigateToPhoto(photoId))
        }
    }
}

// ============== Photo Preview Screen ==============

data class PhotoPreviewUiState(
    val isLoading: Boolean = true,
    val currentPhoto: Photo? = null,
    val allPhotos: List<Photo> = emptyList(),
    val currentIndex: Int = 0,
    val error: String? = null
)

class PhotoPreviewViewModel(
    private val getAlbumPhotosUseCase: GetAlbumPhotosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotoPreviewUiState())
    val uiState: StateFlow<PhotoPreviewUiState> = _uiState.asStateFlow()

    fun loadPhoto(photoId: String, albumId: String) {
        // ===== MOCK DATA IMPLEMENTATION (for demo/testing) =====
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val photos = tryLoadPhotos(albumId).ifEmpty { getMockPhotos(albumId) }
            val index = photos.indexOfFirst { it.id == photoId }.coerceAtLeast(0)

            _uiState.update {
                it.copy(
                    allPhotos = photos,
                    currentPhoto = photos.getOrNull(index),
                    currentIndex = index,
                    isLoading = false
                )
            }
        }

        // ===== ORIGINAL IMPLEMENTATION (uncomment when backend is ready) =====
        /*
        viewModelScope.launch {
            getAlbumPhotosUseCase(albumId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val photos = result.data ?: emptyList()
                        val index = photos.indexOfFirst { it.id == photoId }
                        _uiState.update { 
                            it.copy(
                                allPhotos = photos,
                                currentPhoto = photos.getOrNull(index),
                                currentIndex = maxOf(0, index),
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
        */
    }

    // ===== HELPER METHODS FOR MOCK DATA FALLBACK =====
    private suspend fun tryLoadPhotos(albumId: String): List<Photo> {
        return try {
            withTimeoutOrNull(3000) {
                val result = getAlbumPhotosUseCase(albumId).first { it !is Resource.Loading }
                (result as? Resource.Success)?.data ?: emptyList()
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ===== MOCK DATA (for demo purposes - remove when backend is ready) =====
    private fun getMockPhotos(albumId: String): List<Photo> {
        val baseUrls = listOf(
            "https://images.unsplash.com/photo-1519741497674-611481863552",
            "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04",
            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4",
            "https://images.unsplash.com/photo-1540575467063-178a50c2df87",
            "https://images.unsplash.com/photo-1511895426328-dc8714191300",
            "https://images.unsplash.com/photo-1523275335684-37898b6baf30"
        )

        return baseUrls.mapIndexed { index, url ->
            Photo(
                id = "${albumId}_$index",
                albumId = albumId,
                imageUrl = "$url?w=1200",
                thumbnailUrl = "$url?w=400",
                title = "Photo ${index + 1}",
                description = "A beautiful captured moment",
                width = 1200,
                height = 800,
                takenAt = null,
                location = null,
                tags = emptyList(),
                isFeatured = index < 3,
                order = index
            )
        }
    }
    // ===== END MOCK DATA =====

    fun onPageChanged(index: Int) {
        _uiState.update {
            it.copy(
                currentIndex = index,
                currentPhoto = it.allPhotos.getOrNull(index)
            )
        }
    }
}
