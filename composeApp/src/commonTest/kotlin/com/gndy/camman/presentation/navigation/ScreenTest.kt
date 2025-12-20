package com.gndy.camman.presentation.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for Screen sealed class hierarchy
 * Tests verify that all screen definitions are correctly structured
 */
class ScreenTest {

    // ============== Object Screen Tests ==============

    @Test
    fun `Splash screen should be a data object`() {
        val screen: Screen = Screen.Splash
        assertIs<Screen.Splash>(screen)
    }

    @Test
    fun `LocationPermission screen should be a data object`() {
        val screen: Screen = Screen.LocationPermission
        assertIs<Screen.LocationPermission>(screen)
    }

    @Test
    fun `Onboarding screen should be a data object`() {
        val screen: Screen = Screen.Onboarding
        assertIs<Screen.Onboarding>(screen)
    }

    @Test
    fun `UserTypeSelection screen should be a data object`() {
        val screen: Screen = Screen.UserTypeSelection
        assertIs<Screen.UserTypeSelection>(screen)
    }

    @Test
    fun `SignIn screen should be a data object`() {
        val screen: Screen = Screen.SignIn
        assertIs<Screen.SignIn>(screen)
    }

    @Test
    fun `SignUp screen should be a data object`() {
        val screen: Screen = Screen.SignUp
        assertIs<Screen.SignUp>(screen)
    }

    @Test
    fun `ForgotPassword screen should be a data object`() {
        val screen: Screen = Screen.ForgotPassword
        assertIs<Screen.ForgotPassword>(screen)
    }

    @Test
    fun `UserMain screen should be a data object`() {
        val screen: Screen = Screen.UserMain()
        assertIs<Screen.UserMain>(screen)
    }

    @Test
    fun `PhotographerMain screen should be a data object`() {
        val screen: Screen = Screen.PhotographerMain
        assertIs<Screen.PhotographerMain>(screen)
    }

    @Test
    fun `Home legacy screen should be a data object`() {
        val screen: Screen = Screen.Home
        assertIs<Screen.Home>(screen)
    }

    @Test
    fun `EditProfile screen should be a data object`() {
        val screen: Screen = Screen.EditProfile
        assertIs<Screen.EditProfile>(screen)
    }

    @Test
    fun `PortfolioAlbums screen should be a data object`() {
        val screen: Screen = Screen.PortfolioAlbums
        assertIs<Screen.PortfolioAlbums>(screen)
    }

    @Test
    fun `Packages screen should be a data object`() {
        val screen: Screen = Screen.Packages
        assertIs<Screen.Packages>(screen)
    }

    @Test
    fun `Contact screen should be a data object`() {
        val screen: Screen = Screen.Contact
        assertIs<Screen.Contact>(screen)
    }

    // ============== Data Class Screen Tests ==============

    @Test
    fun `PhotographerDetail screen should hold photographerId`() {
        val photographerId = "test-photographer-123"
        val screen = Screen.PhotographerDetail(photographerId)
        
        assertEquals(photographerId, screen.photographerId)
        assertIs<Screen.PhotographerDetail>(screen)
    }

    @Test
    fun `AlbumDetail screen should hold albumId`() {
        val albumId = "album-456"
        val screen = Screen.AlbumDetail(albumId)
        
        assertEquals(albumId, screen.albumId)
        assertIs<Screen.AlbumDetail>(screen)
    }

    @Test
    fun `PhotoPreview screen should hold photoId and albumId`() {
        val photoId = "photo-789"
        val albumId = "album-456"
        val screen = Screen.PhotoPreview(photoId, albumId)
        
        assertEquals(photoId, screen.photoId)
        assertEquals(albumId, screen.albumId)
    }

    @Test
    fun `PackageDetail screen should hold packageId`() {
        val packageId = "package-abc"
        val screen = Screen.PackageDetail(packageId)
        
        assertEquals(packageId, screen.packageId)
    }

    @Test
    fun `Booking screen should hold packageId and optional photographerId`() {
        val packageId = "package-123"
        val photographerId = "photographer-456"
        
        // With photographerId
        val screenWithPhotographer = Screen.Booking(packageId, photographerId)
        assertEquals(packageId, screenWithPhotographer.packageId)
        assertEquals(photographerId, screenWithPhotographer.photographerId)
        
        // Without photographerId (null default)
        val screenWithoutPhotographer = Screen.Booking(packageId)
        assertEquals(packageId, screenWithoutPhotographer.packageId)
        assertEquals(null, screenWithoutPhotographer.photographerId)
    }

    @Test
    fun `BookingConfirmation screen should hold bookingId`() {
        val bookingId = "booking-xyz"
        val screen = Screen.BookingConfirmation(bookingId)
        
        assertEquals(bookingId, screen.bookingId)
    }

    @Test
    fun `SubmitReview screen should hold all review parameters`() {
        val photographerId = "photographer-123"
        val bookingId = "booking-456"
        val photographerName = "John Doe"
        val photographerImageUrl = "https://example.com/image.jpg"
        val serviceType = "Wedding Photography"
        
        val screen = Screen.SubmitReview(
            photographerId = photographerId,
            bookingId = bookingId,
            photographerName = photographerName,
            photographerImageUrl = photographerImageUrl,
            serviceType = serviceType
        )
        
        assertEquals(photographerId, screen.photographerId)
        assertEquals(bookingId, screen.bookingId)
        assertEquals(photographerName, screen.photographerName)
        assertEquals(photographerImageUrl, screen.photographerImageUrl)
        assertEquals(serviceType, screen.serviceType)
    }

    @Test
    fun `SubmitReview screen should allow null photographerImageUrl`() {
        val screen = Screen.SubmitReview(
            photographerId = "photographer-123",
            bookingId = "booking-456",
            photographerName = "John Doe",
            photographerImageUrl = null,
            serviceType = "Portrait"
        )
        
        assertEquals(null, screen.photographerImageUrl)
    }

    @Test
    fun `EditReview screen should hold reviewId`() {
        val reviewId = "review-999"
        val screen = Screen.EditReview(reviewId)
        
        assertEquals(reviewId, screen.reviewId)
    }

    @Test
    fun `PhotographerReviews screen should hold photographerId`() {
        val photographerId = "photographer-abc"
        val screen = Screen.PhotographerReviews(photographerId)
        
        assertEquals(photographerId, screen.photographerId)
    }

    // ============== Screen Equality Tests ==============

    @Test
    fun `data object screens should be singleton equal`() {
        assertTrue(Screen.Splash === Screen.Splash)
        assertTrue(Screen.UserMain === Screen.UserMain)
        assertTrue(Screen.PhotographerMain === Screen.PhotographerMain)
    }

    @Test
    fun `data class screens with same parameters should be equal`() {
        val screen1 = Screen.PhotographerDetail("photographer-123")
        val screen2 = Screen.PhotographerDetail("photographer-123")
        
        assertEquals(screen1, screen2)
    }

    @Test
    fun `data class screens with different parameters should not be equal`() {
        val screen1 = Screen.PhotographerDetail("photographer-123")
        val screen2 = Screen.PhotographerDetail("photographer-456")
        
        assertTrue(screen1 != screen2)
    }

    // ============== User Tab Screens Tests ==============

    @Test
    fun `BrowsePhotographers screen should be a data object`() {
        val screen: Screen = Screen.BrowsePhotographers
        assertIs<Screen.BrowsePhotographers>(screen)
    }

    @Test
    fun `UserBookings screen should be a data object`() {
        val screen: Screen = Screen.UserBookings
        assertIs<Screen.UserBookings>(screen)
    }

    @Test
    fun `Favorites screen should be a data object`() {
        val screen: Screen = Screen.Favorites
        assertIs<Screen.Favorites>(screen)
    }

    @Test
    fun `UserProfile screen should be a data object`() {
        val screen: Screen = Screen.UserProfile
        assertIs<Screen.UserProfile>(screen)
    }

    @Test
    fun `NearbyPhotographers screen should be a data object`() {
        val screen: Screen = Screen.NearbyPhotographers
        assertIs<Screen.NearbyPhotographers>(screen)
    }

    // ============== Photographer Tab Screens Tests ==============

    @Test
    fun `PhotographerHome screen should be a data object`() {
        val screen: Screen = Screen.PhotographerHome
        assertIs<Screen.PhotographerHome>(screen)
    }

    @Test
    fun `PhotographerPortfolio screen should be a data object`() {
        val screen: Screen = Screen.PhotographerPortfolio
        assertIs<Screen.PhotographerPortfolio>(screen)
    }

    @Test
    fun `PhotographerBookings screen should be a data object`() {
        val screen: Screen = Screen.PhotographerBookings
        assertIs<Screen.PhotographerBookings>(screen)
    }

    @Test
    fun `PhotographerProfile screen should be a data object`() {
        val screen: Screen = Screen.PhotographerProfile
        assertIs<Screen.PhotographerProfile>(screen)
    }
}
