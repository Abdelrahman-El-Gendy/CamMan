package com.gndy.camman.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PhotographerNotification(
    val id: String,
    val photographerId: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val bookingId: String?,
    val clientName: String?,
    val isRead: Boolean = false,
    val createdAt: Long
)

enum class NotificationType(val displayName: String) {
    NEW_BOOKING("New Booking"),
    BOOKING_CANCELLED("Booking Cancelled"),
    BOOKING_UPDATED("Booking Updated"),
    PAYMENT_RECEIVED("Payment Received"),
    MESSAGE("Message"),
    REMINDER("Reminder")
}
