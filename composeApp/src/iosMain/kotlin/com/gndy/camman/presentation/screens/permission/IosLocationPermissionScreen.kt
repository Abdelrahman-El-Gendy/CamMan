package com.gndy.camman.presentation.screens.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse

/**
 * iOS-specific wrapper for LocationPermissionScreen that handles
 * the actual permission request using CoreLocation
 */
@Composable
actual fun PlatformLocationPermissionScreen(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onSkip: () -> Unit
) {
    val locationManager = remember { CLLocationManager() }
    var isPermissionGranted by remember { 
        mutableStateOf(hasIosLocationPermission(locationManager)) 
    }
    
    // Check permission status when screen loads
    LaunchedEffect(Unit) {
        isPermissionGranted = hasIosLocationPermission(locationManager)
    }
    
    LocationPermissionScreen(
        onAllowLocation = {
            if (isPermissionGranted) {
                onPermissionGranted()
            } else {
                // Request permission
                locationManager.requestWhenInUseAuthorization()
                // iOS will show system dialog
                // Permission result is handled asynchronously
                // For now, just proceed (user can grant in system dialog)
                onPermissionGranted()
            }
        },
        onSkip = onSkip,
        isPermissionGranted = isPermissionGranted
    )
}

private fun hasIosLocationPermission(locationManager: CLLocationManager): Boolean {
    val status = locationManager.authorizationStatus
    return status == kCLAuthorizationStatusAuthorizedWhenInUse ||
           status == kCLAuthorizationStatusAuthorizedAlways
}
