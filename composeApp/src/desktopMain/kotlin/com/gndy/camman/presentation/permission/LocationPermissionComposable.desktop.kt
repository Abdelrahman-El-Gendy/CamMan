package com.gndy.camman.presentation.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberCrossPlatformLocationPermissionState(
    onPermissionResult: (Boolean) -> Unit
): CrossPlatformLocationPermissionHandler {
    // Desktop doesn't have location permissions, so we assume granted
    return remember {
        CrossPlatformLocationPermissionHandler(
            state = CrossPlatformLocationPermissionState(
                hasPermission = true,
                isPermanentlyDenied = false
            ),
            requestPermissionAction = {
                // No-op on desktop
                onPermissionResult(true)
            },
            openSettingsAction = {
                // No-op on desktop
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
