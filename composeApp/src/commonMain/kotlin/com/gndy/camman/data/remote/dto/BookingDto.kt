package com.gndy.camman.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequest(
    val packageId: String,
    val clientFullName: String,
    val clientEmail: String,
    val clientPhone: String,
    val clientAddress: String?,
    val specialRequests: String?,
    val sessionDate: String,
    val sessionTime: String,
    val location: String,
    val notes: String?,
    val paymentMethodId: String?,
    val addOnIds: List<String>
)

@Serializable
data class BookingResponse(
    val id: String,
    val packageId: String,
    val packageName: String,
    val clientFullName: String,
    val clientEmail: String,
    val clientPhone: String,
    val clientAddress: String?,
    val specialRequests: String?,
    val sessionDate: String,
    val sessionTime: String,
    val location: String,
    val notes: String?,
    val status: String,
    val totalAmount: Double,
    val depositAmount: Double,
    val depositPaid: Boolean,
    val paymentMethodId: String?,
    val createdAt: String,
    val updatedAt: String,
    val confirmationNumber: String,
    val estimatedDeliveryDate: String
)

@Serializable
data class AvailableDateDto(
    val date: String,
    val timeSlots: List<TimeSlotDto>
)

@Serializable
data class TimeSlotDto(
    val time: String,
    val isAvailable: Boolean
)

@Serializable
data class AvailabilityResponse(
    val availableDates: List<AvailableDateDto>
)
