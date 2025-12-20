package com.gndy.camman.presentation.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

/**
 * Unit tests for Bottom Navigation Items
 * Tests verify route uniqueness, correct item count, and properties
 */
class BottomNavItemTest {

    // ============== UserBottomNavItem Tests ==============

    @Test
    fun `UserBottomNavItem should have exactly 5 items`() {
        assertEquals(5, UserBottomNavItem.items.size)
    }

    @Test
    fun `UserBottomNavItem Browse should have correct route`() {
        assertEquals("browse_photographers", UserBottomNavItem.Browse.route)
    }

    @Test
    fun `UserBottomNavItem Nearby should have correct route`() {
        assertEquals("nearby_photographers", UserBottomNavItem.Nearby.route)
    }

    @Test
    fun `UserBottomNavItem Bookings should have correct route`() {
        assertEquals("user_bookings", UserBottomNavItem.Bookings.route)
    }

    @Test
    fun `UserBottomNavItem Favorites should have correct route`() {
        assertEquals("favorites", UserBottomNavItem.Favorites.route)
    }

    @Test
    fun `UserBottomNavItem Profile should have correct route`() {
        assertEquals("user_profile", UserBottomNavItem.Profile.route)
    }

    @Test
    fun `All UserBottomNavItem routes should be unique`() {
        val routes = UserBottomNavItem.items.map { it.route }
        assertEquals(routes.size, routes.distinct().size)
    }

    @Test
    fun `UserBottomNavItem items should be in correct order`() {
        val items = UserBottomNavItem.items
        assertEquals(UserBottomNavItem.Browse, items[0])
        assertEquals(UserBottomNavItem.Nearby, items[1])
        assertEquals(UserBottomNavItem.Bookings, items[2])
        assertEquals(UserBottomNavItem.Favorites, items[3])
        assertEquals(UserBottomNavItem.Profile, items[4])
    }

    @Test
    fun `UserBottomNavItem should have non-null title resources`() {
        UserBottomNavItem.items.forEach { item ->
            assertNotNull(item.titleRes, "Title resource should not be null for ${item.route}")
        }
    }

    @Test
    fun `UserBottomNavItem should have different selected and unselected icons`() {
        UserBottomNavItem.items.forEach { item ->
            assertNotEquals(
                item.selectedIcon,
                item.unselectedIcon,
                "Selected and unselected icons should differ for ${item.route}"
            )
        }
    }

    // ============== PhotographerBottomNavItem Tests ==============

    @Test
    fun `PhotographerBottomNavItem should have exactly 4 items`() {
        assertEquals(4, PhotographerBottomNavItem.items.size)
    }

    @Test
    fun `PhotographerBottomNavItem Home should have correct route`() {
        assertEquals("photographer_home", PhotographerBottomNavItem.Home.route)
    }

    @Test
    fun `PhotographerBottomNavItem Portfolio should have correct route`() {
        assertEquals("photographer_portfolio", PhotographerBottomNavItem.Portfolio.route)
    }

    @Test
    fun `PhotographerBottomNavItem Bookings should have correct route`() {
        assertEquals("photographer_bookings", PhotographerBottomNavItem.Bookings.route)
    }

    @Test
    fun `PhotographerBottomNavItem Profile should have correct route`() {
        assertEquals("photographer_profile", PhotographerBottomNavItem.Profile.route)
    }

    @Test
    fun `All PhotographerBottomNavItem routes should be unique`() {
        val routes = PhotographerBottomNavItem.items.map { it.route }
        assertEquals(routes.size, routes.distinct().size)
    }

    @Test
    fun `PhotographerBottomNavItem items should be in correct order`() {
        val items = PhotographerBottomNavItem.items
        assertEquals(PhotographerBottomNavItem.Home, items[0])
        assertEquals(PhotographerBottomNavItem.Portfolio, items[1])
        assertEquals(PhotographerBottomNavItem.Bookings, items[2])
        assertEquals(PhotographerBottomNavItem.Profile, items[3])
    }

    @Test
    fun `PhotographerBottomNavItem should have non-null title resources`() {
        PhotographerBottomNavItem.items.forEach { item ->
            assertNotNull(item.titleRes, "Title resource should not be null for ${item.route}")
        }
    }

    @Test
    fun `PhotographerBottomNavItem should have different selected and unselected icons`() {
        PhotographerBottomNavItem.items.forEach { item ->
            assertNotEquals(
                item.selectedIcon,
                item.unselectedIcon,
                "Selected and unselected icons should differ for ${item.route}"
            )
        }
    }

    // ============== Route Collision Tests ==============

    @Test
    fun `User and Photographer routes should not collide`() {
        val userRoutes = UserBottomNavItem.items.map { it.route }.toSet()
        val photographerRoutes = PhotographerBottomNavItem.items.map { it.route }.toSet()
        
        val intersection = userRoutes.intersect(photographerRoutes)
        assertTrue(
            intersection.isEmpty(),
            "User and Photographer routes should not overlap. Collisions: $intersection"
        )
    }

    // ============== Navigation Tab Index Tests ==============

    @Test
    fun `User Browse tab should be at index 0`() {
        assertEquals(0, UserBottomNavItem.items.indexOf(UserBottomNavItem.Browse))
    }

    @Test
    fun `User Nearby tab should be at index 1`() {
        assertEquals(1, UserBottomNavItem.items.indexOf(UserBottomNavItem.Nearby))
    }

    @Test
    fun `User Bookings tab should be at index 2`() {
        assertEquals(2, UserBottomNavItem.items.indexOf(UserBottomNavItem.Bookings))
    }

    @Test
    fun `User Favorites tab should be at index 3`() {
        assertEquals(3, UserBottomNavItem.items.indexOf(UserBottomNavItem.Favorites))
    }

    @Test
    fun `User Profile tab should be at index 4`() {
        assertEquals(4, UserBottomNavItem.items.indexOf(UserBottomNavItem.Profile))
    }

    @Test
    fun `Photographer Home tab should be at index 0`() {
        assertEquals(0, PhotographerBottomNavItem.items.indexOf(PhotographerBottomNavItem.Home))
    }

    @Test
    fun `Photographer Portfolio tab should be at index 1`() {
        assertEquals(1, PhotographerBottomNavItem.items.indexOf(PhotographerBottomNavItem.Portfolio))
    }

    @Test
    fun `Photographer Bookings tab should be at index 2`() {
        assertEquals(2, PhotographerBottomNavItem.items.indexOf(PhotographerBottomNavItem.Bookings))
    }

    @Test
    fun `Photographer Profile tab should be at index 3`() {
        assertEquals(3, PhotographerBottomNavItem.items.indexOf(PhotographerBottomNavItem.Profile))
    }
}
