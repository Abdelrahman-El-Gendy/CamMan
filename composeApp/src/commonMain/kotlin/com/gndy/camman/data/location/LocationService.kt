package com.gndy.camman.data.location

import com.gndy.camman.domain.model.UserLocation
import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic location service interface
 * Implementations are provided for Android and iOS
 */
interface LocationService {
    /**
     * Observe location updates as a Flow
     */
    fun observeLocation(): Flow<UserLocation?>
    
    /**
     * Get current location (single shot)
     */
    suspend fun getCurrentLocation(): UserLocation?
    
    /**
     * Get the last known location (cached)
     */
    suspend fun getLastKnownLocation(): UserLocation?
    
    /**
     * Check if location permission is granted
     */
    fun hasLocationPermission(): Boolean
    
    /**
     * Check if location services are enabled on the device
     */
    fun isLocationEnabled(): Boolean
    
    /**
     * Start listening for location updates
     */
    fun startLocationUpdates()
    
    /**
     * Stop listening for location updates
     */
    fun stopLocationUpdates()
}


