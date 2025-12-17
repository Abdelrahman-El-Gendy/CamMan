package com.gndy.camman.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.LocalDateTime

data class Booking(
    val id: String,
    val packageId: String,
    val packageName: String,
    val clientInfo: ClientInfo,
    val sessionDate: LocalDate,
    val sessionTime: LocalTime,
    val location: String,
    val notes: String?,
    val status: BookingStatus,
    val totalAmount: Double,
    val depositAmount: Double,
    val depositPaid: Boolean,
    val paymentMethod: PaymentMethod?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val addOns: List<BookingAddOn> = emptyList()
)

data class ClientInfo(
    val fullName: String,
    val email: String,
    val phone: String,
    val address: String?,
    val specialRequests: String?
)

enum class BookingStatus(val displayName: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    DEPOSIT_PAID("Deposit Paid"),
    FULLY_PAID("Fully Paid"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    RESCHEDULED("Rescheduled")
}

data class BookingAddOn(
    val addOnId: String,
    val name: String,
    val price: Double
)

data class BookingSummary(
    val booking: Booking,
    val packageDetails: PhotographyPackage,
    val confirmationNumber: String,
    val estimatedDeliveryDate: LocalDate
)

data class TimeSlot(
    val time: LocalTime,
    val isAvailable: Boolean
)

data class AvailableDate(
    val date: LocalDate,
    val timeSlots: List<TimeSlot>
)
