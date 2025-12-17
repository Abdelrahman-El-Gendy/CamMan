package com.gndy.camman.presentation.navigation.keys

import kotlinx.serialization.Serializable

/**
 * Navigation keys for the Portfolio feature module
 * Following Navigation 3 modularization pattern - these represent the "api" layer
 */
@Serializable
sealed interface PortfolioKey {
    @Serializable
    data object Albums : PortfolioKey

    @Serializable
    data class AlbumDetail(val albumId: String) : PortfolioKey

    @Serializable
    data class PhotoPreview(val photoId: String, val albumId: String) : PortfolioKey
}
