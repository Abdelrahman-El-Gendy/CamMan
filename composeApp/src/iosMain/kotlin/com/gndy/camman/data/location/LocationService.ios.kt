@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.gndy.camman.data.location

import com.gndy.camman.domain.model.UserLocation
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject

/**
 * iOS implementation of LocationService using CoreLocation
 */
class IosLocationService : LocationService {
    
    private val locationManager = CLLocationManager()
    private val locationFlow = MutableStateFlow<UserLocation?>(null)
    private var lastKnownLocation: UserLocation? = null
    
    private val locationDelegate = object : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            val location = didUpdateLocations.lastOrNull() as? CLLocation
            location?.let { loc ->
                val userLocation = loc.coordinate.useContents {
                    UserLocation(
                        latitude = latitude,
                        longitude = longitude,
                        accuracy = loc.horizontalAccuracy.toFloat()
                    )
                }
                lastKnownLocation = userLocation
                locationFlow.value = userLocation
            }
        }
        
        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            // Handle location error
            println("Location error: ${didFailWithError.localizedDescription}")
        }
        
        override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
            if (hasLocationPermission()) {
                startLocationUpdates()
            }
        }
    }
    
    init {
        locationManager.delegate = locationDelegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
    }

    override fun observeLocation(): Flow<UserLocation?> {
        if (hasLocationPermission()) {
            startLocationUpdates()
        }
        return locationFlow.asStateFlow()
    }

    override suspend fun getCurrentLocation(): UserLocation? {
        if (!hasLocationPermission()) return null
        
        // Request single location update
        locationManager.requestLocation()
        
        // Return last known location or wait for update
        return lastKnownLocation ?: locationFlow.value
    }

    override suspend fun getLastKnownLocation(): UserLocation? {
        if (lastKnownLocation != null) return lastKnownLocation
        
        val clLocation = locationManager.location
        return clLocation?.let { loc ->
            loc.coordinate.useContents {
                UserLocation(
                    latitude = latitude,
                    longitude = longitude,
                    accuracy = loc.horizontalAccuracy.toFloat()
                )
            }
        }
    }

    override fun hasLocationPermission(): Boolean {
        val status = locationManager.authorizationStatus
        return status == kCLAuthorizationStatusAuthorizedWhenInUse ||
               status == kCLAuthorizationStatusAuthorizedAlways
    }

    override fun isLocationEnabled(): Boolean {
        return CLLocationManager.locationServicesEnabled()
    }

    override fun startLocationUpdates() {
        if (hasLocationPermission()) {
            locationManager.startUpdatingLocation()
        } else {
            locationManager.requestWhenInUseAuthorization()
        }
    }

    override fun stopLocationUpdates() {
        locationManager.stopUpdatingLocation()
    }
}
