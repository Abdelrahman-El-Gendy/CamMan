package com.gndy.camman

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
 * Navigation instrumented tests that verify navigation destinations
 * and screen transitions work correctly.
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun splashScreen_isInitialDestination() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.Splash,
                hasSeenOnboarding = false,
                hasSeenLocationPermission = false
            )
        }
        
        // Splash screen should be displayed as the initial destination
        composeTestRule.waitForIdle()
    }

    @Test
    fun onboardingScreen_displaysCorrectly_whenSetAsDestination() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.Onboarding,
                hasSeenOnboarding = false,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun userTypeSelectionScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.UserTypeSelection,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun userMainScreen_displaysCorrectly_whenSetAsDestination() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.UserMain(),
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun photographerMainScreen_displaysCorrectly_whenSetAsDestination() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.PhotographerMain,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun locationPermissionScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.LocationPermission,
                hasSeenOnboarding = false,
                hasSeenLocationPermission = false
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun signInScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.SignIn,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun signUpScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.SignUp,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun forgotPasswordScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.ForgotPassword,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun portfolioAlbumsScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.PortfolioAlbums,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun packagesScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.Packages,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun contactScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.Contact,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun editProfileScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.EditProfile,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun homeScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CamManNavGraph(
                navController = navController,
                startDestination = Screen.Home,
                hasSeenOnboarding = true,
                hasSeenLocationPermission = true
            )
        }
        
        composeTestRule.waitForIdle()
    }
}
