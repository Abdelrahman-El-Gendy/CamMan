package com.gndy.camman.presentation.screens.nearby

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.gndy.camman.domain.model.NearbyPhotographer
import com.gndy.camman.domain.model.UserLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
actual fun NearbyMapView(
    modifier: Modifier,
    userLocation: UserLocation?,
    photographers: List<NearbyPhotographer>,
    selectedPhotographer: NearbyPhotographer?,
    onPhotographerSelected: (NearbyPhotographer?) -> Unit,
    onPhotographerClick: (NearbyPhotographer) -> Unit
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    
    // Check if location permission is granted before enabling my location
    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.latitude, it.longitude),
                14f
            )
        }
    }

    val mapProperties = remember(hasLocationPermission) {
        MapProperties(
            isMyLocationEnabled = hasLocationPermission,
            mapStyleOptions = null // We can add a dark mode style here
        )
    }
    
    val uiSettings = remember(hasLocationPermission) {
        MapUiSettings(
            myLocationButtonEnabled = hasLocationPermission,
            zoomControlsEnabled = false,
            mapToolbarEnabled = false
        )
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapClick = { onPhotographerSelected(null) }
    ) {
        photographers.forEach { photographer ->
            val position = LatLng(photographer.latitude, photographer.longitude)
            
            // Use standard Marker for stability
            Marker(
                state = rememberMarkerState(position = position),
                title = photographer.name,
                snippet = "$${photographer.startingPrice?.toInt() ?: 0}/hr",
                onClick = {
                    onPhotographerSelected(photographer)
                    true
                }
            )
        }
    }
}
