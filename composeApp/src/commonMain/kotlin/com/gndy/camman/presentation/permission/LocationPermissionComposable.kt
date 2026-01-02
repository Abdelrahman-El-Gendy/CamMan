package com.gndy.camman.presentation.permission

import androidx.compose.runtime.Composable

/**
 * Cross-platform location permission handler state
 */
data class CrossPlatformLocationPermissionState(
    val hasPermission: Boolean = false,
    val isPermanentlyDenied: Boolean = false
)

/**
 * Expect function to get platform-specific location permission composable
 */
@Composable
expect fun rememberCrossPlatformLocationPermissionState(
    onPermissionResult: (Boolean) -> Unit = {}
): CrossPlatformLocationPermissionHandler

/**
 * Handler for location permission actions
 */
expect class CrossPlatformLocationPermissionHandler {
    val state: CrossPlatformLocationPermissionState
    fun requestPermission()
    fun openSettings()
}
