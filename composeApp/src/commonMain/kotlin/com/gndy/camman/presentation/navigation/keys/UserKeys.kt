package com.gndy.camman.presentation.navigation.keys

import kotlinx.serialization.Serializable

/**
 * Navigation keys for the User (Client) feature module
 * Following Navigation 3 modularization pattern - these represent the "api" layer
 */
@Serializable
sealed interface UserKey {
    @Serializable
    data object Main : UserKey  // Container for bottom nav

    @Serializable
    data object BrowsePhotographers : UserKey

    @Serializable
    data object Bookings : UserKey

    @Serializable
    data object Favorites : UserKey

    @Serializable
    data object Profile : UserKey
}
