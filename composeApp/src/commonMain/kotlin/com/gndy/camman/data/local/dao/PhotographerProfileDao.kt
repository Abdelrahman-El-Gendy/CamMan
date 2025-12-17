package com.gndy.camman.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gndy.camman.data.local.entity.PhotographerProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotographerProfileDao {
    @Query("SELECT * FROM photographer_profile LIMIT 1")
    fun getPhotographerProfile(): Flow<PhotographerProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: PhotographerProfileEntity)

    @Update
    suspend fun updateProfile(profile: PhotographerProfileEntity)

    @Query("DELETE FROM photographer_profile")
    suspend fun deleteProfile()
}
