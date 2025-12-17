package com.gndy.camman.data.repository

import com.gndy.camman.data.local.dao.BookingDao
import com.gndy.camman.data.local.dao.NotificationDao
import com.gndy.camman.data.local.entity.BookingEntity
import com.gndy.camman.data.local.entity.NotificationEntity
import com.gndy.camman.domain.model.Booking
import com.gndy.camman.domain.model.BookingStatus
import com.gndy.camman.domain.model.ClientInfo
import com.gndy.camman.domain.model.NotificationType
import com.gndy.camman.domain.model.PaymentMethod
import com.gndy.camman.domain.model.PhotographerNotification
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ClientBookingRepositoryImpl(
    private val bookingDao: BookingDao,
    private val notificationDao: NotificationDao
) {

    fun getAllClientBookings(): Flow<Resource<List<Booking>>> = flow {
        emit(Resource.Loading())
        try {
            bookingDao.getAllBookings().collect { entities ->
                val bookings = entities.map { it.toBooking() }
                emit(Resource.Success(bookings))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load bookings"))
        }
    }

    fun getUpcomingBookings(): Flow<Resource<List<Booking>>> = flow {
        emit(Resource.Loading())
        try {
            val today =
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
            bookingDao.getUpcomingBookings(today).collect { entities ->
                val bookings = entities.map { it.toBooking() }
                emit(Resource.Success(bookings))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load bookings"))
        }
    }

    fun getBookingById(bookingId: String): Flow<Resource<Booking?>> = flow {
        emit(Resource.Loading())
        try {
            bookingDao.getBookingById(bookingId).collect { entity ->
                emit(Resource.Success(entity?.toBooking()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load booking"))
        }
    }

    suspend fun createBooking(
        photographerId: String,
        photographerName: String,
        packageId: String,
        packageName: String,
        sessionDate: LocalDate,
        sessionTime: LocalTime,
        location: String,
        clientInfo: ClientInfo,
        totalAmount: Double,
        paymentMethod: PaymentMethod?,
        notes: String?
    ): Resource<Booking> {
        return try {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val bookingId = "BK${Clock.System.now().toEpochMilliseconds()}"

            val booking = Booking(
                id = bookingId,
                packageId = packageId,
                packageName = packageName,
                clientInfo = clientInfo,
                sessionDate = sessionDate,
                sessionTime = sessionTime,
                location = location,
                notes = notes,
                status = BookingStatus.PENDING,
                totalAmount = totalAmount,
                depositAmount = totalAmount * 0.3,
                depositPaid = false,
                paymentMethod = paymentMethod,
                createdAt = now,
                updatedAt = now,
                addOns = emptyList()
            )

            // Save booking to database
            bookingDao.insertBooking(BookingEntity.fromBooking(booking))

            // Create notification for photographer
            val notification = PhotographerNotification(
                id = "NOTIF_${Clock.System.now().toEpochMilliseconds()}",
                photographerId = photographerId,
                type = NotificationType.NEW_BOOKING,
                title = "New Booking Request!",
                message = "${clientInfo.fullName} booked $packageName for ${
                    formatDateForNotification(
                        sessionDate
                    )
                } at ${formatTimeForNotification(sessionTime)}",
                bookingId = bookingId,
                clientName = clientInfo.fullName,
                isRead = false,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )

            notificationDao.insert(NotificationEntity.fromNotification(notification))

            Resource.Success(booking)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create booking")
        }
    }

    suspend fun cancelBooking(bookingId: String, photographerId: String): Resource<Unit> {
        return try {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            bookingDao.updateBookingStatus(bookingId, BookingStatus.CANCELLED.name, now.toString())

            // Get booking details for notification
            val booking = bookingDao.getBookingById(bookingId).first()

            if (booking != null) {
                val notification = PhotographerNotification(
                    id = "NOTIF_${Clock.System.now().toEpochMilliseconds()}",
                    photographerId = photographerId,
                    type = NotificationType.BOOKING_CANCELLED,
                    title = "Booking Cancelled",
                    message = "${booking.clientInfo.fullName} cancelled their booking for ${booking.packageName}",
                    bookingId = bookingId,
                    clientName = booking.clientInfo.fullName,
                    isRead = false,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
                notificationDao.insert(NotificationEntity.fromNotification(notification))
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to cancel booking")
        }
    }

    // Notification methods for photographer side
    fun getPhotographerNotifications(photographerId: String): Flow<Resource<List<PhotographerNotification>>> =
        flow {
            emit(Resource.Loading())
            try {
                notificationDao.getNotificationsForPhotographer(photographerId)
                    .collect { entities ->
                        val notifications = entities.map { it.toNotification() }
                        emit(Resource.Success(notifications))
                    }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to load notifications"))
            }
        }

    fun getUnreadNotificationCount(photographerId: String): Flow<Int> {
        return notificationDao.getUnreadCount(photographerId)
    }

    suspend fun markNotificationAsRead(notificationId: String) {
        notificationDao.markAsRead(notificationId)
    }

    suspend fun markAllNotificationsAsRead(photographerId: String) {
        notificationDao.markAllAsRead(photographerId)
    }

    private fun formatDateForNotification(date: LocalDate): String {
        return "${
            date.month.name.lowercase().replaceFirstChar { it.uppercase() }
        } ${date.dayOfMonth}, ${date.year}"
    }

    private fun formatTimeForNotification(time: LocalTime): String {
        val hour = if (time.hour > 12) time.hour - 12 else if (time.hour == 0) 12 else time.hour
        val amPm = if (time.hour >= 12) "PM" else "AM"
        return "$hour:${time.minute.toString().padStart(2, '0')} $amPm"
    }
}
