package com.gndy.camman.presentation.navigation.builders

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.gndy.camman.presentation.navigation.Screen
import com.gndy.camman.presentation.screens.user.UserMainScreen

/**
 * Entry builder for the User (Client) feature module.
 * Contains navigation entries for: UserMain and nested user screens
 *
 * This follows the Navigation 3 modularization pattern where each feature
 * module provides its own navigation entries through extension functions.
 */
class UserEntryBuilder : NavEntryBuilder {

    override fun NavGraphBuilder.buildEntries(navController: NavHostController) {
        // User Main Screen (Container with Bottom Nav)
        composable<Screen.UserMain> {
            UserMainScreen(
                onNavigateToPhotographer = { photographerId ->
                    navController.navigate(Screen.PhotographerDetail(photographerId))
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.UserTypeSelection) {
                        popUpTo(Screen.UserMain) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Extension function to add user entries to NavGraphBuilder.
 * This can be called directly from the main NavGraph.
 */
fun NavGraphBuilder.userEntries(navController: NavHostController) {
    with(UserEntryBuilder()) {
        buildEntries(navController)
    }
}
