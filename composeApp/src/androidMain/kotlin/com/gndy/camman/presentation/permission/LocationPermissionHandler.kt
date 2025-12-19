package com.gndy.camman.presentation.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * State holder for location permission
 */
data class LocationPermissionState(
    val hasPermission: Boolean = false,
    val shouldShowRationale: Boolean = false,
    val isPermanentlyDenied: Boolean = false
)

/**
 * Composable that handles location permission request flow
 */
@Composable
fun rememberLocationPermissionState(
    onPermissionResult: (Boolean) -> Unit = {}
): LocationPermissionHandler {
    val context = LocalContext.current
    
    var permissionState by remember {
        mutableStateOf(
            LocationPermissionState(
                hasPermission = hasLocationPermission(context)
            )
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val isGranted = fineLocationGranted || coarseLocationGranted
        
        permissionState = permissionState.copy(
            hasPermission = isGranted,
            isPermanentlyDenied = !isGranted && !permissionState.shouldShowRationale
        )
        
        onPermissionResult(isGranted)
    }
    
    return remember(permissionState) {
        LocationPermissionHandler(
            state = permissionState,
            requestPermission = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            openSettings = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            },
            updateState = { newState ->
                permissionState = newState
            }
        )
    }
}

/**
 * Handler class for location permission operations
 */
class LocationPermissionHandler(
    val state: LocationPermissionState,
    private val requestPermission: () -> Unit,
    private val openSettings: () -> Unit,
    private val updateState: (LocationPermissionState) -> Unit
) {
    fun request() {
        requestPermission()
    }
    
    fun openAppSettings() {
        openSettings()
    }
    
    fun refresh(context: Context) {
        updateState(
            state.copy(
                hasPermission = hasLocationPermission(context)
            )
        )
    }
}

/**
 * Check if location permission is granted
 */
fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Composable that automatically requests location permission on first composition
 */
@Composable
fun RequestLocationPermissionOnStart(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    
    val permissionHandler = rememberLocationPermissionState { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }
    
    LaunchedEffect(Unit) {
        if (!permissionHandler.state.hasPermission) {
            permissionHandler.request()
        } else {
            onPermissionGranted()
        }
    }
}
