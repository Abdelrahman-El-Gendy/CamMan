package com.gndy.camman.presentation.screens.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.DistanceRadius
import com.gndy.camman.domain.model.NearbyPhotographer
import com.gndy.camman.domain.model.PhotographyType
import com.gndy.camman.domain.model.PriceRange
import com.gndy.camman.domain.model.UserLocation
import com.gndy.camman.domain.repository.LocationRepository
import com.gndy.camman.domain.repository.NearbyPhotographersRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * View mode for displaying photographers
 */
enum class ViewMode {
    LIST,
    MAP
}

/**
 * UI State for Nearby Photographers screen
 */
data class NearbyPhotographersUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val photographers: List<NearbyPhotographer> = emptyList(),
    val userLocation: UserLocation? = null,
    val hasLocationPermission: Boolean = false,
    val isLocationEnabled: Boolean = true,
    val selectedDistanceRadius: DistanceRadius = DistanceRadius.CLOSE,
    val selectedPhotographyType: PhotographyType = PhotographyType.ALL,
    val selectedPriceRange: PriceRange = PriceRange.ANY,
    val showAvailableOnly: Boolean = false,
    val viewMode: ViewMode = ViewMode.LIST,
    val selectedPhotographer: NearbyPhotographer? = null,
    val error: String? = null,
    val photographyTypes: List<PhotographyType> = PhotographyType.entries,
    val priceRanges: List<PriceRange> = PriceRange.entries,
    val distanceRadii: List<DistanceRadius> = DistanceRadius.entries
)

/**
 * One-time UI events
 */
sealed class NearbyPhotographersUiEvent {
    data class NavigateToPhotographer(val photographerId: String) : NearbyPhotographersUiEvent()
    data class NavigateToChat(val photographerId: String) : NearbyPhotographersUiEvent()
    data class NavigateToBooking(val photographerId: String) : NearbyPhotographersUiEvent()
    data class NavigateToQuoteRequest(val photographerId: String) : NearbyPhotographersUiEvent()
    data class ShowError(val message: String) : NearbyPhotographersUiEvent()
    data object RequestLocationPermission : NearbyPhotographersUiEvent()
    data object LocationPermissionDenied : NearbyPhotographersUiEvent()
    data object OpenLocationSettings : NearbyPhotographersUiEvent()
}

/**
 * ViewModel for Nearby Photographers screen
 */
class NearbyPhotographersViewModel(
    private val locationRepository: LocationRepository,
    private val nearbyPhotographersRepository: NearbyPhotographersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NearbyPhotographersUiState())
    val uiState: StateFlow<NearbyPhotographersUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<NearbyPhotographersUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var locationJob: Job? = null
    private var photographersJob: Job? = null

    init {
        checkLocationPermission()
    }

    /**
     * Check if location permission is granted
     */
    private fun checkLocationPermission() {
        viewModelScope.launch {
            val hasPermission = locationRepository.hasLocationPermission()
            val isEnabled = locationRepository.isLocationEnabled()
            
            _uiState.update { 
                it.copy(
                    hasLocationPermission = hasPermission,
                    isLocationEnabled = isEnabled
                ) 
            }
            
            if (hasPermission && isEnabled) {
                startLocationUpdates()
            } else if (!hasPermission) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvents.emit(NearbyPhotographersUiEvent.RequestLocationPermission)
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvents.emit(NearbyPhotographersUiEvent.OpenLocationSettings)
            }
        }
    }

    /**
     * Start observing user location
     */
    private fun startLocationUpdates() {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            locationRepository.observeUserLocation().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    
                    is Resource.Success -> {
                        resource.data?.let { location ->
                            _uiState.update { 
                                it.copy(
                                    userLocation = location,
                                    hasLocationPermission = true,
                                    isLocationEnabled = true
                                ) 
                            }
                            loadNearbyPhotographers(location)
                        } ?: run {
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    error = "Could not get your location"
                                ) 
                            }
                        }
                    }
                    
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = resource.message
                            ) 
                        }
                        _uiEvents.emit(
                            NearbyPhotographersUiEvent.ShowError(
                                resource.message ?: "Failed to get location"
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * Load nearby photographers based on user location
     */
    private fun loadNearbyPhotographers(location: UserLocation) {
        photographersJob?.cancel()
        photographersJob = viewModelScope.launch {
            val state = _uiState.value
            
            nearbyPhotographersRepository.getNearbyPhotographers(
                latitude = location.latitude,
                longitude = location.longitude,
                radiusKm = state.selectedDistanceRadius.radiusKm,
                photographyType = state.selectedPhotographyType.takeIf { it != PhotographyType.ALL },
                priceRange = state.selectedPriceRange.takeIf { it != PriceRange.ANY },
                availableOnly = state.showAvailableOnly
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                photographers = resource.data ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle location permission granted
     */
    fun onLocationPermissionGranted() {
        _uiState.update { it.copy(hasLocationPermission = true) }
        startLocationUpdates()
    }

    /**
     * Handle location permission denied
     */
    fun onLocationPermissionDenied() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    hasLocationPermission = false,
                    isLoading = false
                ) 
            }
            _uiEvents.emit(NearbyPhotographersUiEvent.LocationPermissionDenied)
        }
    }

    /**
     * Request location permission
     */
    fun requestLocationPermission() {
        viewModelScope.launch {
            _uiEvents.emit(NearbyPhotographersUiEvent.RequestLocationPermission)
        }
    }

    /**
     * Open location settings
     */
    fun openLocationSettings() {
        viewModelScope.launch {
            _uiEvents.emit(NearbyPhotographersUiEvent.OpenLocationSettings)
        }
    }

    /**
     * Handle distance radius change
     */
    fun onDistanceRadiusChanged(radius: DistanceRadius) {
        _uiState.update { it.copy(selectedDistanceRadius = radius) }
        _uiState.value.userLocation?.let { location ->
            loadNearbyPhotographers(location)
        }
    }

    /**
     * Handle photography type filter change
     */
    fun onPhotographyTypeChanged(type: PhotographyType) {
        _uiState.update { it.copy(selectedPhotographyType = type) }
        _uiState.value.userLocation?.let { location ->
            loadNearbyPhotographers(location)
        }
    }

    /**
     * Handle price range filter change
     */
    fun onPriceRangeChanged(priceRange: PriceRange) {
        _uiState.update { it.copy(selectedPriceRange = priceRange) }
        _uiState.value.userLocation?.let { location ->
            loadNearbyPhotographers(location)
        }
    }

    /**
     * Toggle available only filter
     */
    fun onAvailableOnlyChanged(availableOnly: Boolean) {
        _uiState.update { it.copy(showAvailableOnly = availableOnly) }
        _uiState.value.userLocation?.let { location ->
            loadNearbyPhotographers(location)
        }
    }

    /**
     * Toggle view mode between list and map
     */
    fun onViewModeChanged(viewMode: ViewMode) {
        _uiState.update { it.copy(viewMode = viewMode) }
    }

    /**
     * Select a photographer (for map view)
     */
    fun onPhotographerSelected(photographer: NearbyPhotographer?) {
        _uiState.update { it.copy(selectedPhotographer = photographer) }
    }

    /**
     * Navigate to photographer profile
     */
    fun onPhotographerClick(photographerId: String) {
        viewModelScope.launch {
            _uiEvents.emit(NearbyPhotographersUiEvent.NavigateToPhotographer(photographerId))
        }
    }

    /**
     * Start chat with photographer
     */
    fun onChatClick(photographerId: String) {
        viewModelScope.launch {
            _uiEvents.emit(NearbyPhotographersUiEvent.NavigateToChat(photographerId))
        }
    }

    /**
     * Book a session with photographer
     */
    fun onBookClick(photographerId: String) {
        viewModelScope.launch {
            _uiEvents.emit(NearbyPhotographersUiEvent.NavigateToBooking(photographerId))
        }
    }

    /**
     * Request a quote from photographer
     */
    fun onRequestQuoteClick(photographerId: String) {
        viewModelScope.launch {
            _uiEvents.emit(NearbyPhotographersUiEvent.NavigateToQuoteRequest(photographerId))
        }
    }

    /**
     * Refresh nearby photographers
     */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        _uiState.value.userLocation?.let { location ->
            loadNearbyPhotographers(location)
        } ?: run {
            checkLocationPermission()
        }
    }

    /**
     * Retry loading after error
     */
    fun retry() {
        _uiState.update { it.copy(error = null) }
        checkLocationPermission()
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
        photographersJob?.cancel()
    }
}
