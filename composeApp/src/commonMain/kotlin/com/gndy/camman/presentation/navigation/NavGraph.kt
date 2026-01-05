package com.gndy.camman.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.gndy.camman.util.WhatsAppUtils
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
import com.gndy.camman.presentation.screens.chat.ChatScreen
import com.gndy.camman.presentation.screens.chat.ConversationsListScreen
import com.gndy.camman.presentation.screens.splash.SplashScreen
import com.gndy.camman.presentation.screens.user.PhotographerDetailScreen
import com.gndy.camman.presentation.screens.user.UserMainScreen
import com.gndy.camman.presentation.screens.user.FilterScreen
import com.gndy.camman.presentation.screens.onboarding.RoleSelectionScreen
import com.gndy.camman.presentation.permission.rememberCrossPlatformLocationPermissionState

@Composable
fun CamManNavGraph(
    navController: NavHostController,
    startDestination: Screen = Screen.Splash,
    hasSeenOnboarding: Boolean = false,
    onOnboardingComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // URI handler for opening external links (WhatsApp, etc.)
    val uriHandler = LocalUriHandler.current
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // ============== Splash Screen ==============
        composable<Screen.Splash> {
            // Check if user is authenticated using Koin
            val authRepository: com.gndy.camman.domain.repository.AuthRepository = org.koin.compose.koinInject()
            val isAuthenticated = authRepository.isLoggedIn
            
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                onNavigateToUserTypeSelection = {
                    // Legacy - redirect to RoleSelection
                    navController.navigate(Screen.RoleSelection) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                onNavigateToRoleSelection = {
                    navController.navigate(Screen.RoleSelection) {
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

        // ============== Onboarding Screen ==============
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
                onNavigateToClientHome = {
                    navController.navigate(Screen.UserMain) {
                        popUpTo(Screen.SignIn) { inclusive = true }
                    }
                },
                onNavigateToPhotographerHome = {
                    navController.navigate(Screen.PhotographerMain) {
                        popUpTo(Screen.SignIn) { inclusive = true }
                    }
                },
                onNavigateToRoleSelection = {
                    // User has no role, go to role selection
                    navController.navigate(Screen.RoleSelection) {
                        popUpTo(Screen.SignIn) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword)
                },
                onContinueAsGuest = null  // No guest mode - auth is required
            )
        }

        composable<Screen.SignUp> {
            SignUpScreen(
                onNavigateToSignIn = {
                    // After successful signup, navigate to SignIn to login
                    navController.navigate(Screen.SignIn) {
                        popUpTo(Screen.SignUp) { inclusive = true }
                    }
                }
            )
        }
        
        // ============== Role Selection (Mandatory First Launch) ==============
        composable<Screen.RoleSelection> {
            RoleSelectionScreen(
                onNavigateToClientHome = {
                    navController.navigate(Screen.UserMain) {
                        popUpTo(Screen.RoleSelection) { inclusive = true }
                    }
                },
                onNavigateToPhotographerHome = {
                    navController.navigate(Screen.PhotographerMain) {
                        popUpTo(Screen.RoleSelection) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn) {
                        popUpTo(Screen.RoleSelection) { inclusive = true }
                    }
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

        composable<Screen.Filter> {
            FilterScreen(
                onNavigateBack = { navController.popBackStack() },
                onApplyFilters = { filters ->
                    // Serialize filter state to JSON string and set in savedStateHandle
                    val filtersJson = kotlinx.serialization.json.Json.encodeToString(
                        com.gndy.camman.presentation.screens.user.FilterState.serializer(),
                        filters
                    )
                    navController.previousBackStackEntry?.savedStateHandle?.set("filters_json", filtersJson)
                    navController.popBackStack()
                }
            )
        }

        // ============== User (Client) Main Screen with Bottom Nav ==============
        composable<Screen.UserMain> {
            // Store a reference to the permission result callback
            var permissionResultCallback: ((Boolean) -> Unit)? by remember { mutableStateOf(null) }
            
            // Setup cross-platform location permission handler
            val locationPermissionHandler = rememberCrossPlatformLocationPermissionState { isGranted ->
                // Permission result callback - notify the caller
                permissionResultCallback?.invoke(isGranted)
                permissionResultCallback = null
            }
            
            UserMainScreen(
                onNavigateToPhotographer = { photographerId ->
                    navController.navigate(Screen.PhotographerDetail(photographerId))
                },
                onNavigateToChat = { photographerId ->
                    navController.navigate(Screen.Chat(photographerId = photographerId))
                },
                onNavigateToBooking = { photographerId ->
                    navController.navigate(Screen.Booking(packageId = "default", photographerId = photographerId))
                },
                onNavigateToConversations = {
                    navController.navigate(Screen.ConversationsList)
                },
                onNavigateToFilter = {
                    navController.navigate(Screen.Filter)
                },
                onRequestLocationPermission = { callback ->
                    permissionResultCallback = callback
                    locationPermissionHandler.requestPermission()
                },
                onOpenLocationSettings = {
                    locationPermissionHandler.openSettings()
                },
                // Navigate to SignIn after sign out
                onSignOut = {
                    navController.navigate(Screen.SignIn) {
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
                onNavigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat(conversationId = conversationId))
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
                onNavigateToChat = { photographerId ->
                    // User is authenticated, go directly to chat
                    navController.navigate(Screen.Chat(photographerId = photographerId))
                },
                onWhatsAppClick = { phoneNumber, photographerName ->
                    val message = WhatsAppUtils.generatePhotographerMessage(photographerName)
                    val whatsappUri = WhatsAppUtils.generateWhatsAppUri(phoneNumber, message)
                    try {
                        uriHandler.openUri(whatsappUri)
                    } catch (e: Exception) {
                        // Handle error - WhatsApp not installed or URI failed
                        e.printStackTrace()
                    }
                },
                onNavigateToPortfolio = { photographerId ->
                    navController.navigate(Screen.PortfolioAlbums)
                },
                onNavigateToBooking = { photographerId ->
                    // User is authenticated, go directly to booking
                    navController.navigate(Screen.Booking(packageId = "default", photographerId = photographerId))
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

        // ============== Chat Screens ==============
        composable<Screen.ConversationsList> {
            ConversationsListScreen(
                onNavigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat(conversationId = conversationId))
                }
            )
        }

        composable<Screen.Chat> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.Chat>()
            ChatScreen(
                conversationId = route.conversationId,
                photographerId = route.photographerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
