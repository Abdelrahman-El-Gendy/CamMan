package com.gndy.camman.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gndy.camman.data.local.dao.AlbumDao
import com.gndy.camman.data.local.dao.BookingDao
import com.gndy.camman.data.local.dao.NotificationDao
import com.gndy.camman.data.local.dao.PackageDao
import com.gndy.camman.data.local.dao.PaymentMethodDao
import com.gndy.camman.data.local.dao.PhotoDao
import com.gndy.camman.data.local.dao.PhotographerProfileDao
import com.gndy.camman.data.local.dao.PhotographerRegistrationDao
import com.gndy.camman.data.local.entity.AlbumEntity
import com.gndy.camman.data.local.entity.BookingEntity
import com.gndy.camman.data.local.entity.NotificationEntity
import com.gndy.camman.data.local.entity.PackageEntity
import com.gndy.camman.data.local.entity.PaymentMethodEntity
import com.gndy.camman.data.local.entity.PhotoEntity
import com.gndy.camman.data.local.entity.PhotographerProfileEntity
import com.gndy.camman.data.local.entity.PhotographerRegistrationEntity

@Database(
    entities = [
        AlbumEntity::class,
        PhotoEntity::class,
        PackageEntity::class,
        BookingEntity::class,
        PaymentMethodEntity::class,
        PhotographerProfileEntity::class,
        PhotographerRegistrationEntity::class,
        NotificationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CamManDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun photoDao(): PhotoDao
    abstract fun packageDao(): PackageDao
    abstract fun bookingDao(): BookingDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun photographerProfileDao(): PhotographerProfileDao
    abstract fun photographerRegistrationDao(): PhotographerRegistrationDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        const val DATABASE_NAME = "camman_database"
    }
}
