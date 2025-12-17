package com.gndy.camman.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.gndy.camman.presentation.navigation.builders.authEntries
import com.gndy.camman.presentation.navigation.builders.bookingEntries
import com.gndy.camman.presentation.navigation.builders.onboardingEntries
import com.gndy.camman.presentation.navigation.builders.photographerEntries
import com.gndy.camman.presentation.navigation.builders.portfolioEntries
import com.gndy.camman.presentation.navigation.builders.userEntries

/**
 * Modular Navigation Graph using the Entry Builder pattern.
 *
 * This follows the Navigation 3 modularization concepts from:
 * https://developer.android.com/guide/navigation/navigation-3/modularize
 *
 * Benefits:
 * - Clear separation of navigation logic per feature
 * - Each feature module owns its navigation entries
 * - Easy to add/remove feature modules
 * - Scales well as app grows
 * - Easier to test individual features
 *
 * Structure:
 * - keys/ - Contains navigation keys (api layer) for each feature
 * - builders/ - Contains entry builders (impl layer) for each feature
 */
@Composable
fun ModularCamManNavGraph(
    navController: NavHostController,
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
        // ============== Feature Module: Onboarding ==============
        // Screens: Splash, Onboarding, UserTypeSelection
        onboardingEntries(
            navController = navController,
            hasSeenOnboarding = hasSeenOnboarding,
            onOnboardingComplete = onOnboardingComplete
        )

        // ============== Feature Module: Authentication ==============
        // Screens: SignIn, SignUp, ForgotPassword
        authEntries(navController = navController)

        // ============== Feature Module: User (Client) ==============
        // Screens: UserMain (container with bottom nav)
        userEntries(navController = navController)

        // ============== Feature Module: Photographer ==============
        // Screens: PhotographerMain, EditProfile, PhotographerDetail, Home
        photographerEntries(navController = navController)

        // ============== Feature Module: Portfolio ==============
        // Screens: PortfolioAlbums, AlbumDetail, PhotoPreview
        portfolioEntries(navController = navController)

        // ============== Feature Module: Booking ==============
        // Screens: Packages, PackageDetail, Booking, BookingConfirmation, Contact
        bookingEntries(navController = navController)
    }
}
