package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.AvailableDate
import com.gndy.camman.domain.model.Booking
import com.gndy.camman.domain.model.BookingAddOn
import com.gndy.camman.domain.model.BookingSummary
import com.gndy.camman.domain.model.ClientInfo
import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface BookingRepository {
    suspend fun createBooking(
        packageId: String,
        clientInfo: ClientInfo,
        sessionDate: LocalDate,
        sessionTime: LocalTime,
        location: String,
        notes: String?,
        paymentMethod: PaymentMethod?,
        addOns: List<BookingAddOn>
    ): Resource<BookingSummary>

    fun getBookingById(bookingId: String): Flow<Resource<Booking>>
    fun getUserBookings(): Flow<Resource<List<Booking>>>
    fun getAvailableDates(
        packageId: String,
        month: Int,
        year: Int
    ): Flow<Resource<List<AvailableDate>>>

    suspend fun cancelBooking(bookingId: String): Resource<Unit>
    suspend fun rescheduleBooking(
        bookingId: String,
        newDate: LocalDate,
        newTime: LocalTime
    ): Resource<Booking>
}
