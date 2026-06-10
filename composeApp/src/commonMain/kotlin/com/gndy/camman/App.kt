package com.gndy.camman

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.gndy.camman.data.local.preferences.AppPreferencesHolder
import com.gndy.camman.presentation.navigation.CamManNavGraph
import com.gndy.camman.presentation.navigation.ModularCamManNavGraph
import com.gndy.camman.presentation.theme.CamManTheme

// ============== Navigation Options ==============
// Choose which navigation approach to use:
// 1. CamManNavGraph - Original monolithic navigation (current)
// 2. ModularCamManNavGraph - Modular with entry builders (recommended)
// 3. DICamManNavGraph - DI-based for large apps (advanced)

private const val USE_MODULAR_NAVIGATION = false // Set to true to use modular navigation

@Composable
fun App() {
    val themeMode by AppPreferencesHolder.instance.themeMode.collectAsState()
    
    CamManTheme(themeMode = themeMode) {
        val navController = rememberNavController()
        val hasSeenOnboarding by AppPreferencesHolder.instance.hasSeenOnboarding.collectAsState()

        if (USE_MODULAR_NAVIGATION) {
            // ============== Modular Navigation ==============
            // Uses entry builder pattern from Navigation 3 concepts
            // Reference: https://developer.android.com/guide/navigation/navigation-3/modularize
            ModularCamManNavGraph(
                navController = navController,
                hasSeenOnboarding = hasSeenOnboarding,
                onOnboardingComplete = {
                    AppPreferencesHolder.instance.setHasSeenOnboarding(true)
                }
            )
        } else {
            // ============== Original Navigation ==============
            // Monolithic NavGraph (kept for compatibility)
            CamManNavGraph(
                navController = navController,
                hasSeenOnboarding = hasSeenOnboarding,
                onOnboardingComplete = {
                    AppPreferencesHolder.instance.setHasSeenOnboarding(true)
                }
            )
        }
    }
}
