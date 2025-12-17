package com.gndy.camman.presentation.navigation.builders

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.gndy.camman.presentation.navigation.Screen
import com.gndy.camman.presentation.screens.auth.authscreens.ForgotPasswordScreen
import com.gndy.camman.presentation.screens.auth.authscreens.SignInScreen
import com.gndy.camman.presentation.screens.auth.authscreens.SignUpScreen

/**
 * Entry builder for the Authentication feature module.
 * Contains navigation entries for: SignIn, SignUp, ForgotPassword
 *
 * This follows the Navigation 3 modularization pattern where each feature
 * module provides its own navigation entries through extension functions.
 */
class AuthEntryBuilder : NavEntryBuilder {

    override fun NavGraphBuilder.buildEntries(navController: NavHostController) {
        // Sign In Entry
        composable<Screen.SignIn> {
            SignInScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.SignIn) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword)
                }
            )
        }

        // Sign Up Entry
        composable<Screen.SignUp> {
            SignUpScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.SignIn) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                }
            )
        }

        // Forgot Password Entry
        composable<Screen.ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Extension function to add auth entries to NavGraphBuilder.
 * This can be called directly from the main NavGraph.
 */
fun NavGraphBuilder.authEntries(navController: NavHostController) {
    with(AuthEntryBuilder()) {
        buildEntries(navController)
    }
}
