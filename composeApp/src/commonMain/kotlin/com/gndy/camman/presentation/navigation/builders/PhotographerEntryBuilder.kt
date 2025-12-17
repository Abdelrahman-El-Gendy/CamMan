package com.gndy.camman.presentation.navigation.builders

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.gndy.camman.presentation.navigation.Screen
import com.gndy.camman.presentation.screens.home.HomeScreen
import com.gndy.camman.presentation.screens.photographer.EditProfileScreen
import com.gndy.camman.presentation.screens.photographer.PhotographerMainScreen

/**
 * Entry builder for the Photographer feature module.
 * Contains navigation entries for: PhotographerMain, EditProfile, PhotographerDetail
 *
 * This follows the Navigation 3 modularization pattern where each feature
 * module provides its own navigation entries through extension functions.
 */
class PhotographerEntryBuilder : NavEntryBuilder {

    override fun NavGraphBuilder.buildEntries(navController: NavHostController) {
        // Photographer Main Screen (Container with Bottom Nav)
        composable<Screen.PhotographerMain> { backStackEntry ->
            val profileUpdated =
                backStackEntry.savedStateHandle.get<Boolean>("profile_updated") ?: false

            PhotographerMainScreen(
                onNavigateToAlbum = { albumId ->
                    navController.navigate(Screen.AlbumDetail(albumId))
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.UserTypeSelection) {
                        popUpTo(Screen.PhotographerMain) { inclusive = true }
                    }
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile)
                },
                profileUpdated = profileUpdated,
                onProfileUpdateHandled = {
                    backStackEntry.savedStateHandle.remove<Boolean>("profile_updated")
                }
            )
        }

        // Edit Profile Screen
        composable<Screen.EditProfile> {
            EditProfileScreen(
                onNavigateBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "profile_updated",
                        true
                    )
                    navController.popBackStack()
                }
            )
        }

        // Photographer Detail (for users viewing a photographer)
        composable<Screen.PhotographerDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PhotographerDetail>()
            HomeScreen(
                onNavigateToPortfolio = { navController.navigate(Screen.PortfolioAlbums) },
                onNavigateToPackages = { navController.navigate(Screen.Packages) },
                onNavigateToContact = { navController.navigate(Screen.Contact) },
                onNavigateToAlbum = { albumId -> navController.navigate(Screen.AlbumDetail(albumId)) },
                onNavigateToSignIn = {
                    navController.navigate(Screen.UserTypeSelection) {
                        popUpTo(Screen.UserMain) { inclusive = true }
                    }
                }
            )
        }

        // Legacy Home Screen
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToPortfolio = { navController.navigate(Screen.PortfolioAlbums) },
                onNavigateToPackages = { navController.navigate(Screen.Packages) },
                onNavigateToContact = { navController.navigate(Screen.Contact) },
                onNavigateToAlbum = { albumId -> navController.navigate(Screen.AlbumDetail(albumId)) },
                onNavigateToSignIn = {
                    navController.navigate(Screen.UserTypeSelection) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Extension function to add photographer entries to NavGraphBuilder.
 * This can be called directly from the main NavGraph.
 */
fun NavGraphBuilder.photographerEntries(navController: NavHostController) {
    with(PhotographerEntryBuilder()) {
        buildEntries(navController)
    }
}
