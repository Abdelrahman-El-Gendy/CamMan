package com.gndy.camman.presentation.navigation.builders

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.gndy.camman.domain.repository.AuthRepository
import com.gndy.camman.presentation.navigation.Screen
import com.gndy.camman.presentation.screens.onboarding.OnboardingScreen
import com.gndy.camman.presentation.screens.onboarding.UserTypeSelectionScreen
import com.gndy.camman.presentation.screens.splash.SplashScreen
import org.koin.compose.koinInject

/**
 * Entry builder for the Onboarding feature module.
 * Contains navigation entries for: Splash, Onboarding, UserTypeSelection
 *
 * This follows the Navigation 3 modularization pattern where each feature
 * module provides its own navigation entries through extension functions.
 */
class OnboardingEntryBuilder(
    private val hasSeenOnboarding: Boolean,
    private val onOnboardingComplete: () -> Unit
) : NavEntryBuilder {

    override fun NavGraphBuilder.buildEntries(navController: NavHostController) {
        // Splash Screen Entry
        composable<Screen.Splash> {
            // Check if user is authenticated
            val authRepository: AuthRepository = koinInject()
            val isAuthenticated = authRepository.isLoggedIn
            
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                onNavigateToUserTypeSelection = {
                    navController.navigate(Screen.UserTypeSelection) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                hasSeenOnboarding = hasSeenOnboarding,
                isAuthenticated = isAuthenticated
            )
        }

        // Onboarding Screen Entry
        composable<Screen.Onboarding> {
            OnboardingScreen(
                onComplete = {
                    onOnboardingComplete()
                    // Navigate to SignIn after onboarding (auth required)
                    navController.navigate(Screen.SignIn) {
                        popUpTo(Screen.Onboarding) { inclusive = true }
                    }
                },
                onSkip = {
                    onOnboardingComplete()
                    // Navigate to SignIn after onboarding (auth required)
                    navController.navigate(Screen.SignIn) {
                        popUpTo(Screen.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        // User Type Selection Entry
        composable<Screen.UserTypeSelection> {
            UserTypeSelectionScreen(
                onUserSelected = {
                    navController.navigate(Screen.UserMain) {
                        popUpTo(Screen.UserTypeSelection) { inclusive = true }
                    }
                },
                onPhotographerSelected = {
                    navController.navigate(Screen.PhotographerMain) {
                        popUpTo(Screen.UserTypeSelection) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Extension function to add onboarding entries to NavGraphBuilder.
 * This can be called directly from the main NavGraph.
 */
fun NavGraphBuilder.onboardingEntries(
    navController: NavHostController,
    hasSeenOnboarding: Boolean,
    onOnboardingComplete: () -> Unit
) {
    with(OnboardingEntryBuilder(hasSeenOnboarding, onOnboardingComplete)) {
        buildEntries(navController)
    }
}
