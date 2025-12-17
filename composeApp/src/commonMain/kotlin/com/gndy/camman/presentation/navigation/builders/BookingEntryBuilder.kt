package com.gndy.camman.presentation.navigation.builders

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.gndy.camman.presentation.navigation.Screen
import com.gndy.camman.presentation.screens.booking.BookingScreen
import com.gndy.camman.presentation.screens.contact.ContactScreen
import com.gndy.camman.presentation.screens.packages.PackageDetailScreen
import com.gndy.camman.presentation.screens.packages.PackagesScreen

/**
 * Entry builder for the Booking feature module.
 * Contains navigation entries for: Packages, PackageDetail, Booking, BookingConfirmation, Contact
 *
 * This follows the Navigation 3 modularization pattern where each feature
 * module provides its own navigation entries through extension functions.
 */
class BookingEntryBuilder : NavEntryBuilder {

    override fun NavGraphBuilder.buildEntries(navController: NavHostController) {
        // Packages List Entry
        composable<Screen.Packages> {
            val photographerId = "mock_photographer" // TODO: Get from previous screen/state
            PackagesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPackageDetail = { packageId ->
                    navController.navigate(Screen.PackageDetail(packageId))
                },
                onNavigateToBooking = { packageId ->
                    navController.navigate(Screen.Booking(packageId, photographerId))
                }
            )
        }

        // Package Detail Entry
        composable<Screen.PackageDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PackageDetail>()
            val photographerId = "mock_photographer" // TODO: Get from state
            PackageDetailScreen(
                packageId = route.packageId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBooking = {
                    navController.navigate(Screen.Booking(route.packageId, photographerId))
                }
            )
        }

        // Booking Screen Entry
        composable<Screen.Booking> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.Booking>()
            BookingScreen(
                packageId = route.packageId,
                photographerId = route.photographerId,
                onNavigateBack = { navController.popBackStack() },
                onBookingComplete = { bookingId ->
                    navController.navigate(Screen.BookingConfirmation(bookingId)) {
                        popUpTo(Screen.Packages) { inclusive = false }
                    }
                }
            )
        }

        // Booking Confirmation Entry
        composable<Screen.BookingConfirmation> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.BookingConfirmation>()
            // BookingConfirmationScreen can be added here
        }

        // Contact Screen Entry
        composable<Screen.Contact> {
            ContactScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Extension function to add booking entries to NavGraphBuilder.
 * This can be called directly from the main NavGraph.
 */
fun NavGraphBuilder.bookingEntries(navController: NavHostController) {
    with(BookingEntryBuilder()) {
        buildEntries(navController)
    }
}
