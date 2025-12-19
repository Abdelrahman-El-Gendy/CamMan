package com.gndy.camman.presentation.screens.permission

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.gndy.camman.presentation.permission.hasLocationPermission

/**
 * Android-specific wrapper for LocationPermissionScreen that handles
 * the actual permission request using Activity Result API
 */
@Composable
actual fun PlatformLocationPermissionScreen(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    var isPermissionGranted by remember { mutableStateOf(hasLocationPermission(context)) }
    var hasRequestedPermission by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        isPermissionGranted = fineLocationGranted || coarseLocationGranted
        
        if (isPermissionGranted) {
            onPermissionGranted()
        }
        // Don't automatically navigate on denial - let user choose to skip
    }
    
    // Check permission status when screen loads
    LaunchedEffect(Unit) {
        isPermissionGranted = hasLocationPermission(context)
    }
    
    LocationPermissionScreen(
        onAllowLocation = {
            if (isPermissionGranted) {
                onPermissionGranted()
            } else {
                hasRequestedPermission = true
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        },
        onSkip = onSkip,
        isPermissionGranted = isPermissionGranted
    )
}
