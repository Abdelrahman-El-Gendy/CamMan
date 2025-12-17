package com.gndy.camman.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gndy.camman.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE photographerId = :photographerId ORDER BY createdAt DESC")
    fun getNotificationsForPhotographer(photographerId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE photographerId = :photographerId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotifications(photographerId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE photographerId = :photographerId AND isRead = 0")
    fun getUnreadCount(photographerId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Update
    suspend fun update(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE photographerId = :photographerId")
    suspend fun markAllAsRead(photographerId: String)

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun delete(notificationId: String)

    @Query("DELETE FROM notifications WHERE photographerId = :photographerId")
    suspend fun deleteAllForPhotographer(photographerId: String)
}
