package com.gndy.camman.data.local.preferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simple in-memory app preferences (for demo purposes)
 * In production, this should use DataStore or SharedPreferences
 */
class AppPreferences {
    private val _hasSeenOnboarding = MutableStateFlow(false)
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    private val _hasSeenLocationPermission = MutableStateFlow(false)
    val hasSeenLocationPermission: StateFlow<Boolean> = _hasSeenLocationPermission.asStateFlow()

    private val _userType = MutableStateFlow<String?>(null)
    val userType: StateFlow<String?> = _userType.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun setHasSeenOnboarding(hasSeen: Boolean) {
        _hasSeenOnboarding.value = hasSeen
    }

    fun setHasSeenLocationPermission(hasSeen: Boolean) {
        _hasSeenLocationPermission.value = hasSeen
    }

    fun setUserType(type: String) {
        _userType.value = type
    }

    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
    }

    fun clearAll() {
        _hasSeenOnboarding.value = false
        _hasSeenLocationPermission.value = false
        _userType.value = null
        _isLoggedIn.value = false
    }
}

// Singleton instance
object AppPreferencesHolder {
    val instance: AppPreferences = AppPreferences()
}
