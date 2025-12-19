package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.NearbyPhotographer
import com.gndy.camman.domain.model.PhotographyType
import com.gndy.camman.domain.model.PriceRange
import com.gndy.camman.domain.model.UserLocation
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for nearby photographers feature
 */
interface NearbyPhotographersRepository {
    
    /**
     * Get photographers near the specified location
     * @param latitude User's current latitude
     * @param longitude User's current longitude
     * @param radiusKm Search radius in kilometers
     * @param photographyType Optional filter by photography type/specialty
     * @param priceRange Optional filter by price range
     * @param availableOnly If true, only return photographers marked as available
     */
    fun getNearbyPhotographers(
        latitude: Double,
        longitude: Double,
        radiusKm: Int = 10,
        photographyType: PhotographyType? = null,
        priceRange: PriceRange? = null,
        availableOnly: Boolean = false
    ): Flow<Resource<List<NearbyPhotographer>>>
    
    /**
     * Get a single photographer by ID with distance information
     * @param photographerId The photographer's ID
     * @param userLatitude User's current latitude for distance calculation
     * @param userLongitude User's current longitude for distance calculation
     */
    suspend fun getPhotographerWithDistance(
        photographerId: String,
        userLatitude: Double,
        userLongitude: Double
    ): Resource<NearbyPhotographer>
    
    /**
     * Refresh nearby photographers data (force network fetch)
     */
    suspend fun refreshNearbyPhotographers(
        latitude: Double,
        longitude: Double,
        radiusKm: Int
    ): Resource<Unit>
}

/**
 * Repository interface for location services
 */
interface LocationRepository {
    
    /**
     * Observe user's current location with continuous updates
     */
    fun observeUserLocation(): Flow<Resource<UserLocation?>>
    
    /**
     * Get the last known location (cached)
     */
    suspend fun getLastKnownLocation(): UserLocation?
    
    /**
     * Check if location permissions are granted
     */
    suspend fun hasLocationPermission(): Boolean
    
    /**
     * Check if location services are enabled on device
     */
    suspend fun isLocationEnabled(): Boolean
    
    /**
     * Request a single location update
     */
    suspend fun getCurrentLocation(): Resource<UserLocation>
}
