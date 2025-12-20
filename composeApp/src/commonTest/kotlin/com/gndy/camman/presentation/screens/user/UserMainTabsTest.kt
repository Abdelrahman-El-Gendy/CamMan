package com.gndy.camman.presentation.screens.user

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for UserMainTabs constants
 * Ensures tab indices match the expected order
 */
class UserMainTabsTest {

    @Test
    fun `BROWSE tab index should be 0`() {
        assertEquals(0, UserMainTabs.BROWSE)
    }

    @Test
    fun `NEARBY tab index should be 1`() {
        assertEquals(1, UserMainTabs.NEARBY)
    }

    @Test
    fun `BOOKINGS tab index should be 2`() {
        assertEquals(2, UserMainTabs.BOOKINGS)
    }

    @Test
    fun `FAVORITES tab index should be 3`() {
        assertEquals(3, UserMainTabs.FAVORITES)
    }

    @Test
    fun `PROFILE tab index should be 4`() {
        assertEquals(4, UserMainTabs.PROFILE)
    }

    @Test
    fun `all tab indices should be sequential starting from 0`() {
        val indices = listOf(
            UserMainTabs.BROWSE,
            UserMainTabs.NEARBY,
            UserMainTabs.BOOKINGS,
            UserMainTabs.FAVORITES,
            UserMainTabs.PROFILE
        )
        
        indices.forEachIndexed { index, tabIndex ->
            assertEquals(index, tabIndex, "Tab at position $index should have index $index")
        }
    }

    @Test
    fun `total number of tabs should be 5`() {
        val tabCount = listOf(
            UserMainTabs.BROWSE,
            UserMainTabs.NEARBY,
            UserMainTabs.BOOKINGS,
            UserMainTabs.FAVORITES,
            UserMainTabs.PROFILE
        ).distinct().size
        
        assertEquals(5, tabCount)
    }
}
