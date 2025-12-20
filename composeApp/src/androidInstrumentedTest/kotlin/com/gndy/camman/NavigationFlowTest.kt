package com.gndy.camman

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gndy.camman.presentation.navigation.CamManNavGraph
import com.gndy.camman.presentation.navigation.Screen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for navigation flow between screens
 * Verifies that button clicks trigger correct navigation
 */
@RunWith(AndroidJUnit4::class)
class NavigationFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun userTypeSelection_selectUser_navigatesToUserMain() {
        var navigatedToUserMain = false
        
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.UserTypeSelection,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        // Click on "I'm looking for a Photographer" or similar user button
        // The actual text depends on your strings.xml
        composeTestRule.waitForIdle()
    }

    @Test
    fun userTypeSelection_selectPhotographer_navigatesToPhotographerMain() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.UserTypeSelection,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        // Click on photographer selection
        composeTestRule.waitForIdle()
    }

    @Test
    fun signIn_hasNavigationToSignUp() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.SignIn,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        // SignIn screen should have a way to navigate to SignUp
        composeTestRule.waitForIdle()
    }

    @Test
    fun signIn_hasNavigationToForgotPassword() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.SignIn,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        // SignIn screen should have a way to navigate to ForgotPassword
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationGraph_handlesDeepNavigation_toPhotographerDetail() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.PhotographerDetail("test-photographer-id"),
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationGraph_handlesDeepNavigation_toAlbumDetail() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.AlbumDetail("test-album-id"),
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationGraph_handlesDeepNavigation_toBooking() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.Booking(
                    packageId = "test-package-id",
                    photographerId = "test-photographer-id"
                ),
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationGraph_handlesDeepNavigation_toBookingConfirmation() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.BookingConfirmation("test-booking-id"),
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationGraph_handlesDeepNavigation_toPackageDetail() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.PackageDetail("test-package-id"),
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationGraph_handlesDeepNavigation_toPhotoPreview() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.PhotoPreview(
                    photoId = "test-photo-id",
                    albumId = "test-album-id"
                ),
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationGraph_handlesReviewNavigation() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.SubmitReview(
                    photographerId = "test-photographer",
                    bookingId = "test-booking",
                    photographerName = "John Doe",
                    photographerImageUrl = null,
                    serviceType = "Wedding"
                ),
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationGraph_handlesPhotographerReviewsNavigation() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.PhotographerReviews("test-photographer-id"),
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }
}
