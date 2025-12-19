package com.gndy.camman.data.repository

import com.gndy.camman.data.location.LocationService
import com.gndy.camman.domain.model.UserLocation
import com.gndy.camman.domain.repository.LocationRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class LocationRepositoryImpl(
    private val locationService: LocationService
) : LocationRepository {
    
    private var cachedLocation: UserLocation? = null

    override fun observeUserLocation(): Flow<Resource<UserLocation?>> {
        return locationService.observeLocation()
            .map<UserLocation?, Resource<UserLocation?>> { location ->
                cachedLocation = location
                Resource.Success(location)
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(Resource.Error(e.message ?: "Failed to get location"))
            }
    }

    override suspend fun getLastKnownLocation(): UserLocation? {
        return cachedLocation ?: locationService.getLastKnownLocation()
    }

    override suspend fun hasLocationPermission(): Boolean {
        return locationService.hasLocationPermission()
    }

    override suspend fun isLocationEnabled(): Boolean {
        return locationService.isLocationEnabled()
    }

    override suspend fun getCurrentLocation(): Resource<UserLocation> {
        return try {
            val location = locationService.getCurrentLocation()
            if (location != null) {
                cachedLocation = location
                Resource.Success(location)
            } else {
                Resource.Error("Could not get current location")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get current location")
        }
    }
}
