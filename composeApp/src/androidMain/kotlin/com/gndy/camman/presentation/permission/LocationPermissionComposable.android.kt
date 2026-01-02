package com.gndy.camman.presentation.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

@Composable
actual fun rememberCrossPlatformLocationPermissionState(
    onPermissionResult: (Boolean) -> Unit
): CrossPlatformLocationPermissionHandler {
    val context = LocalContext.current
    
    var state by remember {
        mutableStateOf(
            CrossPlatformLocationPermissionState(
                hasPermission = checkLocationPermission(context)
            )
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val isGranted = fineLocationGranted || coarseLocationGranted
        
        state = state.copy(
            hasPermission = isGranted,
            isPermanentlyDenied = !isGranted
        )
        
        onPermissionResult(isGranted)
    }
    
    return remember(state) {
        CrossPlatformLocationPermissionHandler(
            state = state,
            requestPermissionAction = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            openSettingsAction = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        )
    }
}

actual class CrossPlatformLocationPermissionHandler(
    actual val state: CrossPlatformLocationPermissionState,
    private val requestPermissionAction: () -> Unit,
    private val openSettingsAction: () -> Unit
) {
    actual fun requestPermission() {
        requestPermissionAction()
    }
    
    actual fun openSettings() {
        openSettingsAction()
    }
}

private fun checkLocationPermission(context: android.content.Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}
