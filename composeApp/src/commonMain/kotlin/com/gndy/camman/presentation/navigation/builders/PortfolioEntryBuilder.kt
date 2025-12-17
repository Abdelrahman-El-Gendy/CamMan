package com.gndy.camman.presentation.navigation.builders

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.gndy.camman.presentation.navigation.Screen
import com.gndy.camman.presentation.screens.portfolio.AlbumDetailScreen
import com.gndy.camman.presentation.screens.portfolio.PhotoPreviewScreen
import com.gndy.camman.presentation.screens.portfolio.PortfolioAlbumsScreen

/**
 * Entry builder for the Portfolio feature module.
 * Contains navigation entries for: PortfolioAlbums, AlbumDetail, PhotoPreview
 *
 * This follows the Navigation 3 modularization pattern where each feature
 * module provides its own navigation entries through extension functions.
 */
class PortfolioEntryBuilder : NavEntryBuilder {

    override fun NavGraphBuilder.buildEntries(navController: NavHostController) {
        // Portfolio Albums Entry
        composable<Screen.PortfolioAlbums> {
            PortfolioAlbumsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAlbum = { albumId -> navController.navigate(Screen.AlbumDetail(albumId)) }
            )
        }

        // Album Detail Entry
        composable<Screen.AlbumDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.AlbumDetail>()
            AlbumDetailScreen(
                albumId = route.albumId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPhoto = { photoId ->
                    navController.navigate(Screen.PhotoPreview(photoId, route.albumId))
                }
            )
        }

        // Photo Preview Entry
        composable<Screen.PhotoPreview> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PhotoPreview>()
            PhotoPreviewScreen(
                photoId = route.photoId,
                albumId = route.albumId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Extension function to add portfolio entries to NavGraphBuilder.
 * This can be called directly from the main NavGraph.
 */
fun NavGraphBuilder.portfolioEntries(navController: NavHostController) {
    with(PortfolioEntryBuilder()) {
        buildEntries(navController)
    }
}
