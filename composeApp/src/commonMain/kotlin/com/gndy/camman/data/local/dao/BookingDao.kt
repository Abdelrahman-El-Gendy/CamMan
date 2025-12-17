package com.gndy.camman.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gndy.camman.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    fun getBookingById(bookingId: String): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE status = :status ORDER BY sessionDate ASC")
    fun getBookingsByStatus(status: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE sessionDate >= :fromDate ORDER BY sessionDate ASC")
    fun getUpcomingBookings(fromDate: String): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status, updatedAt = :updatedAt WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: String, status: String, updatedAt: String)
}
