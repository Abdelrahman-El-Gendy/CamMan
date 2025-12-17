package com.gndy.camman.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    // Onboarding & Auth Screens
    @Serializable
    data object Splash : Screen()

    @Serializable
    data object Onboarding : Screen()

    @Serializable
    data object UserTypeSelection : Screen()

    @Serializable
    data object SignIn : Screen()

    @Serializable
    data object SignUp : Screen()

    @Serializable
    data object ForgotPassword : Screen()

    // ===== USER (Client) Bottom Navigation =====
    @Serializable
    data object UserMain : Screen()  // Container for user bottom nav

    @Serializable
    data object BrowsePhotographers : Screen()  // Home tab - browse all photographers

    @Serializable
    data object UserBookings : Screen()  // My Bookings tab

    @Serializable
    data object Favorites : Screen()  // Favorites tab

    @Serializable
    data object UserProfile : Screen()  // Profile tab

    // ===== PHOTOGRAPHER Bottom Navigation =====
    @Serializable
    data object PhotographerMain : Screen()  // Container for photographer bottom nav

    @Serializable
    data object PhotographerHome : Screen()  // Dashboard/Home

    @Serializable
    data object PhotographerPortfolio : Screen()  // My Portfolio

    @Serializable
    data object PhotographerBookings : Screen()  // My Bookings

    @Serializable
    data object PhotographerProfile : Screen()  // Profile/Settings

    @Serializable
    data object EditProfile : Screen()  // Edit photographer profile

    // ===== Shared Screens =====
    @Serializable
    data class PhotographerDetail(val photographerId: String) : Screen()

    @Serializable
    data object PortfolioAlbums : Screen()

    @Serializable
    data class AlbumDetail(val albumId: String) : Screen()

    @Serializable
    data class PhotoPreview(val photoId: String, val albumId: String) : Screen()

    @Serializable
    data object Packages : Screen()

    @Serializable
    data class PackageDetail(val packageId: String) : Screen()

    @Serializable
    data class Booking(val packageId: String, val photographerId: String? = null) : Screen()

    @Serializable
    data object Contact : Screen()

    @Serializable
    data class BookingConfirmation(val bookingId: String) : Screen()

    // ===== Review Screens =====
    @Serializable
    data class SubmitReview(
        val photographerId: String,
        val bookingId: String,
        val photographerName: String,
        val photographerImageUrl: String?,
        val serviceType: String
    ) : Screen()

    @Serializable
    data class EditReview(val reviewId: String) : Screen()

    @Serializable
    data class PhotographerReviews(val photographerId: String) : Screen()

    // Legacy - kept for compatibility
    @Serializable
    data object Home : Screen()
}
