package com.gndy.camman.data.local.preferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Theme mode options for the app
 */
enum class ThemeMode {
    SYSTEM, // Follow system setting
    LIGHT,  // Always light
    DARK    // Always dark
}

/**
 * Simple in-memory app preferences (for demo purposes)
 * In production, this should use DataStore or SharedPreferences
 */
class AppPreferences {
    private val _hasSeenOnboarding = MutableStateFlow(false)
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    private val _userType = MutableStateFlow<String?>(null)
    val userType: StateFlow<String?> = _userType.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setHasSeenOnboarding(hasSeen: Boolean) {
        _hasSeenOnboarding.value = hasSeen
    }

    fun setUserType(type: String) {
        _userType.value = type
    }

    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun toggleTheme() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
        }
    }

    fun clearAll() {
        _hasSeenOnboarding.value = false
        _userType.value = null
        _isLoggedIn.value = false
        _themeMode.value = ThemeMode.SYSTEM
    }
}

// Singleton instance
object AppPreferencesHolder {
    val instance: AppPreferences = AppPreferences()
}
