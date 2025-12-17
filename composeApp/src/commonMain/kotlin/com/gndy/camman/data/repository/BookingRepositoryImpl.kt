package com.gndy.camman.data.repository

import com.gndy.camman.data.local.dao.BookingDao
import com.gndy.camman.data.local.entity.BookingEntity
import com.gndy.camman.data.remote.api.CamManApiService
import com.gndy.camman.data.remote.dto.CreateBookingRequest
import com.gndy.camman.domain.model.AvailableDate
import com.gndy.camman.domain.model.Booking
import com.gndy.camman.domain.model.BookingAddOn
import com.gndy.camman.domain.model.BookingStatus
import com.gndy.camman.domain.model.BookingSummary
import com.gndy.camman.domain.model.ClientInfo
import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.domain.model.PhotographyPackage
import com.gndy.camman.domain.model.TimeSlot
import com.gndy.camman.domain.repository.BookingRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class BookingRepositoryImpl(
    private val bookingDao: BookingDao,
    private val apiService: CamManApiService
) : BookingRepository {

    override suspend fun createBooking(
        packageId: String,
        clientInfo: ClientInfo,
        sessionDate: LocalDate,
        sessionTime: LocalTime,
        location: String,
        notes: String?,
        paymentMethod: PaymentMethod?,
        addOns: List<BookingAddOn>
    ): Resource<BookingSummary> {
        return try {
            val request = CreateBookingRequest(
                packageId = packageId,
                clientFullName = clientInfo.fullName,
                clientEmail = clientInfo.email,
                clientPhone = clientInfo.phone,
                clientAddress = clientInfo.address,
                specialRequests = clientInfo.specialRequests,
                sessionDate = sessionDate.toString(),
                sessionTime = sessionTime.toString(),
                location = location,
                notes = notes,
                paymentMethodId = paymentMethod?.id,
                addOnIds = addOns.map { it.addOnId }
            )

            val response = apiService.createBooking(request)

            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            val booking = Booking(
                id = response.id,
                packageId = response.packageId,
                packageName = response.packageName,
                clientInfo = ClientInfo(
                    fullName = response.clientFullName,
                    email = response.clientEmail,
                    phone = response.clientPhone,
                    address = response.clientAddress,
                    specialRequests = response.specialRequests
                ),
                sessionDate = LocalDate.parse(response.sessionDate),
                sessionTime = LocalTime.parse(response.sessionTime),
                location = response.location,
                notes = response.notes,
                status = BookingStatus.entries.find { it.name == response.status }
                    ?: BookingStatus.PENDING,
                totalAmount = response.totalAmount,
                depositAmount = response.depositAmount,
                depositPaid = response.depositPaid,
                paymentMethod = paymentMethod,
                createdAt = LocalDateTime.parse(response.createdAt),
                updatedAt = LocalDateTime.parse(response.updatedAt),
                addOns = addOns
            )

            // Cache booking locally
            bookingDao.insertBooking(BookingEntity.fromBooking(booking))

            // Create a placeholder package for summary (in real app, fetch from package repo)
            val summary = BookingSummary(
                booking = booking,
                packageDetails = PhotographyPackage(
                    id = packageId,
                    name = response.packageName,
                    description = "",
                    category = com.gndy.camman.domain.model.PackageCategory.PORTRAIT,
                    price = response.totalAmount,
                    currency = "USD",
                    duration = com.gndy.camman.domain.model.PackageDuration(1),
                    features = emptyList(),
                    deliverables = emptyList(),
                    maxPhotos = null,
                    includesEditing = true,
                    includesPrints = false,
                    turnaroundDays = 14
                ),
                confirmationNumber = response.confirmationNumber,
                estimatedDeliveryDate = LocalDate.parse(response.estimatedDeliveryDate)
            )

            Resource.Success(summary)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create booking")
        }
    }

    override fun getBookingById(bookingId: String): Flow<Resource<Booking>> = flow {
        emit(Resource.Loading())

        bookingDao.getBookingById(bookingId).collect { entity ->
            if (entity != null) {
                emit(Resource.Success(entity.toBooking()))
            } else {
                try {
                    val response = apiService.getBookingById(bookingId)
                    // Convert response to booking (simplified)
                    emit(Resource.Error("Booking not found in cache"))
                } catch (e: Exception) {
                    emit(Resource.Error(e.message ?: "Booking not found"))
                }
            }
        }
    }

    override fun getUserBookings(): Flow<Resource<List<Booking>>> = flow {
        emit(Resource.Loading())

        bookingDao.getAllBookings().collect { entities ->
            emit(Resource.Success(entities.map { it.toBooking() }))
        }
    }

    override fun getAvailableDates(
        packageId: String,
        month: Int,
        year: Int
    ): Flow<Resource<List<AvailableDate>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getAvailableDates(packageId, month, year)
            val availableDates = response.availableDates.map { dateDto ->
                AvailableDate(
                    date = LocalDate.parse(dateDto.date),
                    timeSlots = dateDto.timeSlots.map { slotDto ->
                        TimeSlot(
                            time = LocalTime.parse(slotDto.time),
                            isAvailable = slotDto.isAvailable
                        )
                    }
                )
            }
            emit(Resource.Success(availableDates))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch available dates"))
        }
    }

    override suspend fun cancelBooking(bookingId: String): Resource<Unit> {
        return try {
            apiService.cancelBooking(bookingId)
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            bookingDao.updateBookingStatus(bookingId, BookingStatus.CANCELLED.name, now.toString())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to cancel booking")
        }
    }

    override suspend fun rescheduleBooking(
        bookingId: String,
        newDate: LocalDate,
        newTime: LocalTime
    ): Resource<Booking> {
        return try {
            // In real implementation, call API to reschedule
            Resource.Error("Reschedule not implemented")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to reschedule booking")
        }
    }
}
