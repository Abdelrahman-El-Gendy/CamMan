package com.gndy.camman.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.gndy.camman.domain.model.UserLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android implementation of LocationService using FusedLocationProviderClient
 */
class AndroidLocationService(
    private val context: Context
) : LocationService {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private var lastKnownLocation: UserLocation? = null
    private var locationCallback: LocationCallback? = null
    
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L // Update interval: 10 seconds
    ).apply {
        setMinUpdateIntervalMillis(5000L) // Fastest interval: 5 seconds
        setMaxUpdateDelayMillis(15000L) // Max delay: 15 seconds
    }.build()

    override fun observeLocation(): Flow<UserLocation?> = callbackFlow {
        if (!hasLocationPermission()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val userLocation = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy
                    )
                    lastKnownLocation = userLocation
                    trySend(userLocation)
                }
            }
        }
        
        locationCallback = callback
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            trySend(null)
            close(e)
        }
        
        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
            locationCallback = null
        }
    }

    override suspend fun getCurrentLocation(): UserLocation? {
        if (!hasLocationPermission()) return null
        
        return suspendCancellableCoroutine { continuation ->
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val userLocation = UserLocation(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracy = location.accuracy
                            )
                            lastKnownLocation = userLocation
                            continuation.resume(userLocation)
                        } else {
                            // Try to get a fresh location
                            requestFreshLocation(continuation)
                        }
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            } catch (e: SecurityException) {
                continuation.resume(null)
            }
        }
    }
    
    private fun requestFreshLocation(continuation: kotlinx.coroutines.CancellableContinuation<UserLocation?>) {
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return
        }
        
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                result.lastLocation?.let { location ->
                    val userLocation = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy
                    )
                    lastKnownLocation = userLocation
                    continuation.resume(userLocation)
                } ?: continuation.resume(null)
            }
        }
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }

    override suspend fun getLastKnownLocation(): UserLocation? {
        if (lastKnownLocation != null) return lastKnownLocation
        
        if (!hasLocationPermission()) return null
        
        return suspendCancellableCoroutine { continuation ->
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val userLocation = UserLocation(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracy = location.accuracy
                            )
                            lastKnownLocation = userLocation
                            continuation.resume(userLocation)
                        } else {
                            continuation.resume(null)
                        }
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            } catch (e: SecurityException) {
                continuation.resume(null)
            }
        }
    }

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
               locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    }

    override fun startLocationUpdates() {
        // Location updates are started automatically when observeLocation() is collected
    }

    override fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
            locationCallback = null
        }
    }
}
