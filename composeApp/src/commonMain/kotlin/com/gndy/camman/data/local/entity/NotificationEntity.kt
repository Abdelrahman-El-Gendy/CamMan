package com.gndy.camman.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gndy.camman.domain.model.NotificationType
import com.gndy.camman.domain.model.PhotographerNotification

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val photographerId: String,
    val type: String,
    val title: String,
    val message: String,
    val bookingId: String?,
    val clientName: String?,
    val isRead: Boolean,
    val createdAt: Long
) {
    fun toNotification(): PhotographerNotification {
        return PhotographerNotification(
            id = id,
            photographerId = photographerId,
            type = NotificationType.entries.find { it.name == type } ?: NotificationType.MESSAGE,
            title = title,
            message = message,
            bookingId = bookingId,
            clientName = clientName,
            isRead = isRead,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromNotification(notification: PhotographerNotification): NotificationEntity {
            return NotificationEntity(
                id = notification.id,
                photographerId = notification.photographerId,
                type = notification.type.name,
                title = notification.title,
                message = notification.message,
                bookingId = notification.bookingId,
                clientName = notification.clientName,
                isRead = notification.isRead,
                createdAt = notification.createdAt
            )
        }
    }
}
