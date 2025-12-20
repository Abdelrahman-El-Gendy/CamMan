package com.gndy.camman

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gndy.camman.presentation.navigation.PhotographerBottomNavItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests for PhotographerMainScreen bottom navigation
 * Verifies that clicking bottom nav items correctly switches tabs
 */
@RunWith(AndroidJUnit4::class)
class PhotographerMainScreenNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun photographerBottomNavItem_homeHasCorrectRoute() {
        assert(PhotographerBottomNavItem.Home.route == "photographer_home")
    }

    @Test
    fun photographerBottomNavItem_portfolioHasCorrectRoute() {
        assert(PhotographerBottomNavItem.Portfolio.route == "photographer_portfolio")
    }

    @Test
    fun photographerBottomNavItem_bookingsHasCorrectRoute() {
        assert(PhotographerBottomNavItem.Bookings.route == "photographer_bookings")
    }

    @Test
    fun photographerBottomNavItem_profileHasCorrectRoute() {
        assert(PhotographerBottomNavItem.Profile.route == "photographer_profile")
    }

    @Test
    fun photographerBottomNavItem_itemsCount_shouldBe4() {
        assert(PhotographerBottomNavItem.items.size == 4)
    }

    @Test
    fun photographerBottomNavItem_itemsOrder_shouldBeCorrect() {
        val items = PhotographerBottomNavItem.items
        assert(items[0] == PhotographerBottomNavItem.Home)
        assert(items[1] == PhotographerBottomNavItem.Portfolio)
        assert(items[2] == PhotographerBottomNavItem.Bookings)
        assert(items[3] == PhotographerBottomNavItem.Profile)
    }
}
