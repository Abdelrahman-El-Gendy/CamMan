package com.gndy.camman.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.gndy.camman.presentation.navigation.builders.NavEntryBuilder
import com.gndy.camman.presentation.navigation.builders.OnboardingEntryBuilder

/**
 * Dependency Injection based Navigation Graph.
 *
 * This follows the Navigation 3 modularization pattern with DI:
 * https://developer.android.com/guide/navigation/navigation-3/modularize
 *
 * This approach allows feature modules to contribute their navigation entries
 * through dependency injection, without the main NavGraph needing to know
 * about each individual feature.
 *
 * Benefits:
 * - Maximum decoupling between feature modules
 * - Adding a new feature doesn't require changes to NavGraph
 * - Each feature module can be developed/tested independently
 * - Perfect for large apps with many features
 *
 * Usage with Koin:
 * ```kotlin
 * @Composable
 * fun App() {
 *     val entryBuilders: Set<NavEntryBuilder> by inject()
 *
 *     DICamManNavGraph(
 *         navController = navController,
 *         entryBuilders = entryBuilders,
 *         hasSeenOnboarding = hasSeenOnboarding,
 *         onOnboardingComplete = { /* ... */ }
 *     )
 * }
 * ```
 */
@Composable
fun DICamManNavGraph(
    navController: NavHostController,
    entryBuilders: Set<NavEntryBuilder>,
    startDestination: Screen = Screen.Splash,
    hasSeenOnboarding: Boolean = false,
    onOnboardingComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // ============== Onboarding (Special case - needs runtime params) ==============
        // Onboarding has runtime configuration, so we handle it separately
        with(OnboardingEntryBuilder(hasSeenOnboarding, onOnboardingComplete)) {
            buildEntries(navController)
        }

        // ============== Injected Entry Builders ==============
        // All other feature modules are injected via DI
        entryBuilders.forEach { builder ->
            with(builder) {
                buildEntries(navController)
            }
        }
    }
}

/**
 * Variant using an ordered list instead of a set.
 * Use this if the order of navigation entry registration matters.
 */
@Composable
fun DICamManNavGraphOrdered(
    navController: NavHostController,
    entryBuilders: List<NavEntryBuilder>,
    startDestination: Screen = Screen.Splash,
    hasSeenOnboarding: Boolean = false,
    onOnboardingComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Onboarding first
        with(OnboardingEntryBuilder(hasSeenOnboarding, onOnboardingComplete)) {
            buildEntries(navController)
        }

        // Then all injected builders in order
        entryBuilders.forEach { builder ->
            with(builder) {
                buildEntries(navController)
            }
        }
    }
}
