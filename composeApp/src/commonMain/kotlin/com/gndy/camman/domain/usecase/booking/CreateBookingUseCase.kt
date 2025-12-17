package com.gndy.camman.domain.usecase.booking

import com.gndy.camman.domain.model.AvailableDate
import com.gndy.camman.domain.model.Booking
import com.gndy.camman.domain.model.BookingAddOn
import com.gndy.camman.domain.model.BookingSummary
import com.gndy.camman.domain.model.ClientInfo
import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.domain.repository.BookingRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class CreateBookingUseCase(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(
        packageId: String,
        clientInfo: ClientInfo,
        sessionDate: LocalDate,
        sessionTime: LocalTime,
        location: String,
        notes: String?,
        paymentMethod: PaymentMethod?,
        addOns: List<BookingAddOn>
    ): Resource<BookingSummary> {
        return bookingRepository.createBooking(
            packageId = packageId,
            clientInfo = clientInfo,
            sessionDate = sessionDate,
            sessionTime = sessionTime,
            location = location,
            notes = notes,
            paymentMethod = paymentMethod,
            addOns = addOns
        )
    }

    fun getAvailableDates(
        packageId: String,
        month: Int,
        year: Int
    ): Flow<Resource<List<AvailableDate>>> {
        return bookingRepository.getAvailableDates(packageId, month, year)
    }

    fun getBookingById(bookingId: String): Flow<Resource<Booking>> {
        return bookingRepository.getBookingById(bookingId)
    }

    fun getUserBookings(): Flow<Resource<List<Booking>>> {
        return bookingRepository.getUserBookings()
    }
}
