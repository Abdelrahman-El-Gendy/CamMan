package com.gndy.camman.presentation.navigation.keys

import kotlinx.serialization.Serializable

/**
 * Navigation keys for the Photographer feature module
 * Following Navigation 3 modularization pattern - these represent the "api" layer
 */
@Serializable
sealed interface PhotographerKey {
    @Serializable
    data object Main : PhotographerKey  // Container for bottom nav

    @Serializable
    data object Home : PhotographerKey  // Dashboard

    @Serializable
    data object Portfolio : PhotographerKey

    @Serializable
    data object Bookings : PhotographerKey

    @Serializable
    data object Profile : PhotographerKey

    @Serializable
    data object EditProfile : PhotographerKey

    @Serializable
    data class Detail(val photographerId: String) : PhotographerKey
}
