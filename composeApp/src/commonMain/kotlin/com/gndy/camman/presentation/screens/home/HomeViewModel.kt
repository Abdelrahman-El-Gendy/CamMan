package com.gndy.camman.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.Album
import com.gndy.camman.domain.model.AlbumCategory
import com.gndy.camman.domain.model.Photo
import com.gndy.camman.domain.model.PhotographerProfile
import com.gndy.camman.domain.model.SocialLinks
import com.gndy.camman.domain.usecase.album.GetAlbumsUseCase
import com.gndy.camman.domain.usecase.album.GetAlbumPhotosUseCase
import com.gndy.camman.domain.usecase.profile.GetPhotographerContactInfoUseCase
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

data class HomeUiState(
    val isLoading: Boolean = true,
    val profile: PhotographerProfile? = null,
    val featuredAlbums: List<Album> = emptyList(),
    val heroPhotos: List<Photo> = emptyList(),
    val error: String? = null
)

sealed class HomeUiEvent {
    data class NavigateToAlbum(val albumId: String) : HomeUiEvent()
    data object NavigateToPortfolio : HomeUiEvent()
    data object NavigateToPackages : HomeUiEvent()
    data object NavigateToContact : HomeUiEvent()
    data class ShowError(val message: String) : HomeUiEvent()
}

class HomeViewModel(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getAlbumPhotosUseCase: GetAlbumPhotosUseCase,
    private val getPhotographerContactInfoUseCase: GetPhotographerContactInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<HomeUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        // ===== MOCK DATA IMPLEMENTATION (for demo/testing) =====
        // Remove this block and uncomment the original implementation below when backend is ready
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Try to load real data with timeout, fallback to mock data
            val profile = tryLoadProfile()
            val albums = tryLoadAlbums()
            val photos = tryLoadPhotos()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    profile = profile ?: getMockProfile(),
                    featuredAlbums = albums.ifEmpty { getMockAlbums() },
                    heroPhotos = photos.ifEmpty { getMockPhotos() }
                )
            }
        }

        // ===== ORIGINAL IMPLEMENTATION (uncomment when backend is ready) =====
        /*
        loadProfile()
        loadFeaturedAlbums()
        loadHeroPhotos()
        */
    }

    // ===== ORIGINAL METHODS (uncomment when backend is ready) =====
    /*
    private fun loadProfile() {
        viewModelScope.launch {
            getPhotographerContactInfoUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(profile = result.data, isLoading = false) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message, isLoading = false) }
                    }
                }
            }
        }
    }

    private fun loadFeaturedAlbums() {
        viewModelScope.launch {
            getAlbumsUseCase.getFeatured().collect { result ->
                when (result) {
                    is Resource.Loading -> { /* Already handled */ }
                    is Resource.Success -> {
                        _uiState.update { it.copy(featuredAlbums = result.data ?: emptyList()) }
                    }
                    is Resource.Error -> {
                        _uiEvents.emit(HomeUiEvent.ShowError(result.message ?: "Failed to load albums"))
                    }
                }
            }
        }
    }

    private fun loadHeroPhotos() {
        viewModelScope.launch {
            getAlbumPhotosUseCase.getFeaturedPhotos().collect { result ->
                when (result) {
                    is Resource.Loading -> { /* Already handled */ }
                    is Resource.Success -> {
                        _uiState.update { it.copy(heroPhotos = result.data ?: emptyList()) }
                    }
                    is Resource.Error -> { /* Silent fail for hero photos */ }
                }
            }
        }
    }
    */

    // ===== HELPER METHODS FOR MOCK DATA FALLBACK =====
    private suspend fun tryLoadProfile(): PhotographerProfile? {
        return try {
            withTimeoutOrNull(3000) {
                val result = getPhotographerContactInfoUseCase().first { it !is Resource.Loading }
                (result as? Resource.Success)?.data
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun tryLoadAlbums(): List<Album> {
        return try {
            withTimeoutOrNull(3000) {
                val result = getAlbumsUseCase.getFeatured().first { it !is Resource.Loading }
                (result as? Resource.Success)?.data ?: emptyList()
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun tryLoadPhotos(): List<Photo> {
        return try {
            withTimeoutOrNull(3000) {
                val result =
                    getAlbumPhotosUseCase.getFeaturedPhotos().first { it !is Resource.Loading }
                (result as? Resource.Success)?.data ?: emptyList()
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ===== MOCK DATA (for demo purposes - remove when backend is ready) =====
    private fun getMockProfile(): PhotographerProfile {
        return PhotographerProfile(
            id = "1",
            name = "Alex Rivera",
            bio = "Professional photographer with 10+ years of experience specializing in weddings, portraits, and events. Let me capture your precious moments.",
            profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            coverImageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=1200",
            specialties = listOf("Wedding", "Portrait", "Event", "Landscape"),
            yearsOfExperience = 10,
            location = "New York, NY",
            email = "alex@camman.com",
            phone = "+1 (555) 123-4567",
            website = "https://camman.com",
            socialLinks = SocialLinks(
                instagram = "https://instagram.com/alexrivera",
                facebook = "https://facebook.com/alexrivera",
                twitter = null,
                linkedin = null,
                youtube = null
            ),
            rating = 4.9f,
            reviewCount = 127
        )
    }

    private fun getMockAlbums(): List<Album> {
        return listOf(
            Album(
                id = "1",
                title = "Wedding Collection",
                description = "Beautiful wedding moments captured with love",
                coverImageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?w=600",
                category = AlbumCategory.WEDDING,
                photoCount = 45,
                createdAt = LocalDate(2024, 6, 15),
                isFeatured = true
            ),
            Album(
                id = "2",
                title = "Portrait Sessions",
                description = "Professional portrait photography",
                coverImageUrl = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=600",
                category = AlbumCategory.PORTRAIT,
                photoCount = 32,
                createdAt = LocalDate(2024, 5, 20),
                isFeatured = true
            ),
            Album(
                id = "3",
                title = "Nature & Landscapes",
                description = "Breathtaking natural scenery",
                coverImageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                category = AlbumCategory.LANDSCAPE,
                photoCount = 28,
                createdAt = LocalDate(2024, 4, 10),
                isFeatured = true
            ),
            Album(
                id = "4",
                title = "Corporate Events",
                description = "Professional event coverage",
                coverImageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=600",
                category = AlbumCategory.EVENT,
                photoCount = 56,
                createdAt = LocalDate(2024, 3, 5),
                isFeatured = true
            )
        )
    }

    private fun getMockPhotos(): List<Photo> {
        return listOf(
            Photo(
                id = "1",
                albumId = "1",
                imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?w=1200",
                thumbnailUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?w=400",
                title = "Wedding Day",
                description = null,
                width = 1200,
                height = 800,
                takenAt = null,
                location = "New York",
                tags = listOf("wedding", "love"),
                isFeatured = true,
                order = 0
            ),
            Photo(
                id = "2",
                albumId = "2",
                imageUrl = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=1200",
                thumbnailUrl = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=400",
                title = "Portrait",
                description = null,
                width = 1200,
                height = 1600,
                takenAt = null,
                location = "Studio",
                tags = listOf("portrait"),
                isFeatured = true,
                order = 1
            ),
            Photo(
                id = "3",
                albumId = "3",
                imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1200",
                thumbnailUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400",
                title = "Mountain View",
                description = null,
                width = 1200,
                height = 800,
                takenAt = null,
                location = "Colorado",
                tags = listOf("landscape", "nature"),
                isFeatured = true,
                order = 2
            )
        )
    }
    // ===== END MOCK DATA =====

    fun onAlbumClick(albumId: String) {
        viewModelScope.launch {
            _uiEvents.emit(HomeUiEvent.NavigateToAlbum(albumId))
        }
    }

    fun onViewPortfolioClick() {
        viewModelScope.launch {
            _uiEvents.emit(HomeUiEvent.NavigateToPortfolio)
        }
    }

    fun onViewPackagesClick() {
        viewModelScope.launch {
            _uiEvents.emit(HomeUiEvent.NavigateToPackages)
        }
    }

    fun onContactClick() {
        viewModelScope.launch {
            _uiEvents.emit(HomeUiEvent.NavigateToContact)
        }
    }

    fun refresh() {
        loadHomeData()
    }
}
