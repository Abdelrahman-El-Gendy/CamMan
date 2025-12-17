package com.gndy.camman.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gndy.camman.domain.model.Booking
import com.gndy.camman.domain.model.BookingAddOn
import com.gndy.camman.domain.model.BookingStatus
import com.gndy.camman.domain.model.ClientInfo
import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.domain.model.PaymentType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    val id: String,
    val packageId: String,
    val packageName: String,
    @Embedded(prefix = "client_")
    val clientInfo: ClientInfoEmbedded,
    val sessionDate: String,
    val sessionTime: String,
    val location: String,
    val notes: String?,
    val status: String,
    val totalAmount: Double,
    val depositAmount: Double,
    val depositPaid: Boolean,
    val paymentMethodId: String?,
    val paymentMethodType: String?,
    val paymentMethodDisplayName: String?,
    val createdAt: String,
    val updatedAt: String,
    val addOns: String
) {
    fun toBooking(): Booking = Booking(
        id = id,
        packageId = packageId,
        packageName = packageName,
        clientInfo = clientInfo.toClientInfo(),
        sessionDate = LocalDate.parse(sessionDate),
        sessionTime = LocalTime.parse(sessionTime),
        location = location,
        notes = notes,
        status = BookingStatus.entries.find { it.name == status } ?: BookingStatus.PENDING,
        totalAmount = totalAmount,
        depositAmount = depositAmount,
        depositPaid = depositPaid,
        paymentMethod = if (paymentMethodId != null && paymentMethodType != null && paymentMethodDisplayName != null) {
            PaymentMethod(
                id = paymentMethodId,
                type = PaymentType.entries.find { it.name == paymentMethodType }
                    ?: PaymentType.CREDIT_CARD,
                displayName = paymentMethodDisplayName,
                details = null
            )
        } else null,
        createdAt = LocalDateTime.parse(createdAt),
        updatedAt = LocalDateTime.parse(updatedAt),
        addOns = parseAddOns(addOns)
    )

    companion object {
        fun fromBooking(booking: Booking): BookingEntity = BookingEntity(
            id = booking.id,
            packageId = booking.packageId,
            packageName = booking.packageName,
            clientInfo = ClientInfoEmbedded.fromClientInfo(booking.clientInfo),
            sessionDate = booking.sessionDate.toString(),
            sessionTime = booking.sessionTime.toString(),
            location = booking.location,
            notes = booking.notes,
            status = booking.status.name,
            totalAmount = booking.totalAmount,
            depositAmount = booking.depositAmount,
            depositPaid = booking.depositPaid,
            paymentMethodId = booking.paymentMethod?.id,
            paymentMethodType = booking.paymentMethod?.type?.name,
            paymentMethodDisplayName = booking.paymentMethod?.displayName,
            createdAt = booking.createdAt.toString(),
            updatedAt = booking.updatedAt.toString(),
            addOns = booking.addOns.joinToString("|") { "${it.addOnId}:${it.name}:${it.price}" }
        )

        private fun parseAddOns(addOnsString: String): List<BookingAddOn> {
            if (addOnsString.isBlank()) return emptyList()
            return addOnsString.split("|").mapNotNull { item ->
                val parts = item.split(":")
                if (parts.size == 3) {
                    BookingAddOn(
                        addOnId = parts[0],
                        name = parts[1],
                        price = parts[2].toDoubleOrNull() ?: 0.0
                    )
                } else null
            }
        }
    }
}

data class ClientInfoEmbedded(
    val fullName: String,
    val email: String,
    val phone: String,
    val address: String?,
    val specialRequests: String?
) {
    fun toClientInfo(): ClientInfo = ClientInfo(
        fullName = fullName,
        email = email,
        phone = phone,
        address = address,
        specialRequests = specialRequests
    )

    companion object {
        fun fromClientInfo(clientInfo: ClientInfo): ClientInfoEmbedded = ClientInfoEmbedded(
            fullName = clientInfo.fullName,
            email = clientInfo.email,
            phone = clientInfo.phone,
            address = clientInfo.address,
            specialRequests = clientInfo.specialRequests
        )
    }
}
