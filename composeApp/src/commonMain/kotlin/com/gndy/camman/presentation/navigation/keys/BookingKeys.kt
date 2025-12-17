package com.gndy.camman.presentation.navigation.keys

import kotlinx.serialization.Serializable

/**
 * Navigation keys for the Booking feature module
 * Following Navigation 3 modularization pattern - these represent the "api" layer
 */
@Serializable
sealed interface BookingKey {
    @Serializable
    data object Packages : BookingKey

    @Serializable
    data class PackageDetail(val packageId: String) : BookingKey

    @Serializable
    data class Booking(val packageId: String, val photographerId: String? = null) : BookingKey

    @Serializable
    data class BookingConfirmation(val bookingId: String) : BookingKey

    @Serializable
    data object Contact : BookingKey
}
