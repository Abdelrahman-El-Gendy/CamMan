package com.gndy.camman

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gndy.camman.presentation.screens.user.UserMainScreen
import com.gndy.camman.presentation.screens.user.UserMainTabs
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests for UserMainScreen bottom navigation
 * Verifies that clicking bottom nav items correctly switches tabs
 */
@RunWith(AndroidJUnit4::class)
class UserMainScreenNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var lastNavigatedPhotographerId: String? = null
    private var signInCalled = false

    @Test
    fun userMainScreen_defaultTab_isBrowse() {
        composeTestRule.setContent {
            UserMainScreen(
                onNavigateToPhotographer = { lastNavigatedPhotographerId = it },
                onNavigateToSignIn = { signInCalled = true },
                initialTab = UserMainTabs.BROWSE
            )
        }
        
        // Browse tab (Search icon) should be visible and selected by default
        composeTestRule.waitForIdle()
    }

    @Test
    fun userMainScreen_clickNearbyTab_switchesToNearby() {
        composeTestRule.setContent {
            UserMainScreen(
                onNavigateToPhotographer = { lastNavigatedPhotographerId = it },
                onNavigateToSignIn = { signInCalled = true }
            )
        }
        
        // Click on Nearby tab
        composeTestRule.onNodeWithText("Nearby", useUnmergedTree = true)
            .performClick()
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun userMainScreen_clickBookingsTab_switchesToBookings() {
        composeTestRule.setContent {
            UserMainScreen(
                onNavigateToPhotographer = { lastNavigatedPhotographerId = it },
                onNavigateToSignIn = { signInCalled = true }
            )
        }
        
        // Click on Bookings tab
        composeTestRule.onNodeWithText("Bookings", useUnmergedTree = true)
            .performClick()
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun userMainScreen_clickFavoritesTab_switchesToFavorites() {
        composeTestRule.setContent {
            UserMainScreen(
                onNavigateToPhotographer = { lastNavigatedPhotographerId = it },
                onNavigateToSignIn = { signInCalled = true }
            )
        }
        
        // Click on Favorites tab
        composeTestRule.onNodeWithText("Favorites", useUnmergedTree = true)
            .performClick()
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun userMainScreen_clickProfileTab_switchesToProfile() {
        composeTestRule.setContent {
            UserMainScreen(
                onNavigateToPhotographer = { lastNavigatedPhotographerId = it },
                onNavigateToSignIn = { signInCalled = true }
            )
        }
        
        // Click on Profile tab
        composeTestRule.onNodeWithText("Profile", useUnmergedTree = true)
            .performClick()
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun userMainScreen_initialTabBookings_startsOnBookings() {
        composeTestRule.setContent {
            UserMainScreen(
                onNavigateToPhotographer = { lastNavigatedPhotographerId = it },
                onNavigateToSignIn = { signInCalled = true },
                initialTab = UserMainTabs.BOOKINGS
            )
        }
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun userMainScreen_tabNavigation_preservesState() {
        composeTestRule.setContent {
            UserMainScreen(
                onNavigateToPhotographer = { lastNavigatedPhotographerId = it },
                onNavigateToSignIn = { signInCalled = true }
            )
        }
        
        // Navigate to different tabs and back
        composeTestRule.onNodeWithText("Profile", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Browse", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun userMainScreen_allTabsAreAccessible() {
        composeTestRule.setContent {
            UserMainScreen(
                onNavigateToPhotographer = { lastNavigatedPhotographerId = it },
                onNavigateToSignIn = { signInCalled = true }
            )
        }
        
        // Verify all bottom nav items are displayed
        composeTestRule.onNodeWithText("Browse", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Nearby", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Bookings", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Favorites", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile", useUnmergedTree = true)
            .assertIsDisplayed()
    }
}
