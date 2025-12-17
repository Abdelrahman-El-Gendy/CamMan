package com.gndy.camman.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.gndy.camman.presentation.screens.auth.authscreens.ForgotPasswordScreen
import com.gndy.camman.presentation.screens.auth.authscreens.SignInScreen
import com.gndy.camman.presentation.screens.auth.authscreens.SignUpScreen
import com.gndy.camman.presentation.screens.photographer.EditProfileScreen
import com.gndy.camman.presentation.screens.booking.BookingScreen
import com.gndy.camman.presentation.screens.contact.ContactScreen
import com.gndy.camman.presentation.screens.home.HomeScreen
import com.gndy.camman.presentation.screens.onboarding.OnboardingScreen
import com.gndy.camman.presentation.screens.onboarding.UserTypeSelectionScreen
import com.gndy.camman.presentation.screens.packages.PackageDetailScreen
import com.gndy.camman.presentation.screens.packages.PackagesScreen
import com.gndy.camman.presentation.screens.photographer.PhotographerMainScreen
import com.gndy.camman.presentation.screens.portfolio.AlbumDetailScreen
import com.gndy.camman.presentation.screens.portfolio.PhotoPreviewScreen
import com.gndy.camman.presentation.screens.portfolio.PortfolioAlbumsScreen
import com.gndy.camman.presentation.screens.splash.SplashScreen
import com.gndy.camman.presentation.screens.user.PhotographerDetailScreen
import com.gndy.camman.presentation.screens.user.UserMainScreen

@Composable
fun CamManNavGraph(
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
        // ============== Splash Screen ==============
        composable<Screen.Splash> {
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
                hasSeenOnboarding = hasSeenOnboarding
            )
        }

        // ============== Onboarding Screen ==============
        composable<Screen.Onboarding> {
            OnboardingScreen(
                onComplete = {
                    onOnboardingComplete()
                    navController.navigate(Screen.UserTypeSelection) {
                        popUpTo(Screen.Onboarding) { inclusive = true }
                    }
                },
                onSkip = {
                    onOnboardingComplete()
                    navController.navigate(Screen.UserTypeSelection) {
                        popUpTo(Screen.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        // ============== User Type Selection ==============
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

        // ============== Auth Screens ==============
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

        composable<Screen.ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ============== User (Client) Main Screen with Bottom Nav ==============
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

        // ============== Photographer Main Screen with Bottom Nav ==============
        composable<Screen.PhotographerMain> { backStackEntry ->
            // Observe profile update result from EditProfile screen
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
                    // Clear the flag after handling
                    backStackEntry.savedStateHandle.remove<Boolean>("profile_updated")
                }
            )
        }

        // ============== Edit Profile Screen ==============
        composable<Screen.EditProfile> {
            EditProfileScreen(
                onNavigateBack = {
                    // Set a result to trigger refresh in PhotographerMainScreen
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "profile_updated",
                        true
                    )
                    navController.popBackStack()
                }
            )
        }

        // ============== Photographer Detail (for users viewing a photographer) ==============
        composable<Screen.PhotographerDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PhotographerDetail>()
            PhotographerDetailScreen(
                photographerId = route.photographerId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPackages = { photographerId ->
                    // Store photographer ID for booking flow
                    navController.currentBackStackEntry?.savedStateHandle?.set("photographerId", photographerId)
                    navController.navigate(Screen.Packages)
                },
                onNavigateToContact = { photographerId ->
                    navController.navigate(Screen.Contact)
                },
                onNavigateToPortfolio = { photographerId ->
                    navController.navigate(Screen.PortfolioAlbums)
                }
            )
        }

        // ============== Legacy Home Screen (kept for compatibility) ==============
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

        // ============== Portfolio Screens ==============
        composable<Screen.PortfolioAlbums> {
            PortfolioAlbumsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAlbum = { albumId -> navController.navigate(Screen.AlbumDetail(albumId)) }
            )
        }

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

        composable<Screen.PhotoPreview> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PhotoPreview>()
            PhotoPreviewScreen(
                photoId = route.photoId,
                albumId = route.albumId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ============== Packages & Booking ==============
        composable<Screen.Packages> { backStackEntry ->
            // Get photographerId from previous screen's savedStateHandle
            val photographerId = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("photographerId") ?: "mock_photographer"
            
            // Store for package detail screen
            backStackEntry.savedStateHandle["photographerId"] = photographerId
            
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

        composable<Screen.PackageDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PackageDetail>()
            // Get photographerId from previous screen's savedStateHandle
            val photographerId = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("photographerId") ?: "mock_photographer"
            
            PackageDetailScreen(
                packageId = route.packageId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBooking = {
                    navController.navigate(Screen.Booking(route.packageId, photographerId))
                }
            )
        }

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

        composable<Screen.BookingConfirmation> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.BookingConfirmation>()
            com.gndy.camman.presentation.screens.booking.BookingConfirmationScreen(
                bookingId = route.bookingId,
                onNavigateToHome = {
                    navController.navigate(Screen.UserMain) {
                        popUpTo(Screen.UserMain) { inclusive = true }
                    }
                },
                onNavigateToBookings = {
                    navController.navigate(Screen.UserMain) {
                        popUpTo(Screen.UserMain) { inclusive = true }
                    }
                    // TODO: Navigate to bookings tab within UserMain
                }
            )
        }

        composable<Screen.Contact> {
            ContactScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ============== Review Screens ==============
        composable<Screen.SubmitReview> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.SubmitReview>()
            com.gndy.camman.presentation.screens.review.ReviewSubmissionScreen(
                photographerId = route.photographerId,
                bookingId = route.bookingId,
                photographerName = route.photographerName,
                photographerImageUrl = route.photographerImageUrl,
                serviceType = route.serviceType,
                onNavigateBack = { navController.popBackStack() },
                onReviewSubmitted = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.EditReview> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.EditReview>()
            // Load review for editing - reuses ReviewSubmissionScreen
            // The ViewModel handles loading the existing review
            com.gndy.camman.presentation.screens.review.ReviewSubmissionScreen(
                photographerId = "", // Will be loaded from review
                bookingId = "", // Will be loaded from review
                photographerName = "", // Will be loaded from review
                photographerImageUrl = null,
                serviceType = "",
                onNavigateBack = { navController.popBackStack() },
                onReviewSubmitted = { navController.popBackStack() }
            )
        }

        composable<Screen.PhotographerReviews> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PhotographerReviews>()
            // Full reviews screen for a photographer
            com.gndy.camman.presentation.screens.review.PhotographerReviewsScreen(
                photographerId = route.photographerId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToWriteReview = { photographerId, bookingId, name, imageUrl, serviceType ->
                    navController.navigate(
                        Screen.SubmitReview(
                            photographerId = photographerId,
                            bookingId = bookingId,
                            photographerName = name,
                            photographerImageUrl = imageUrl,
                            serviceType = serviceType
                        )
                    )
                }
            )
        }
    }
}
