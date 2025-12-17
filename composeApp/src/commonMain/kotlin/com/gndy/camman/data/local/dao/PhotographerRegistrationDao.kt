package com.gndy.camman.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gndy.camman.data.local.entity.PhotographerRegistrationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotographerRegistrationDao {

    @Query("SELECT * FROM photographer_registration WHERE id = :id")
    fun getPhotographerById(id: String): Flow<PhotographerRegistrationEntity?>

    @Query("SELECT * FROM photographer_registration LIMIT 1")
    fun getCurrentPhotographerProfile(): Flow<PhotographerRegistrationEntity?>

    @Query("SELECT * FROM photographer_registration WHERE isProfileComplete = 1")
    fun getAllRegisteredPhotographers(): Flow<List<PhotographerRegistrationEntity>>

    @Query("SELECT * FROM photographer_registration WHERE isProfileComplete = 1 AND isAvailable = 1")
    fun getAvailablePhotographers(): Flow<List<PhotographerRegistrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(registration: PhotographerRegistrationEntity)

    @Update
    suspend fun update(registration: PhotographerRegistrationEntity)

    @Query("UPDATE photographer_registration SET isAvailable = :isAvailable, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateAvailability(id: String, isAvailable: Boolean, updatedAt: Long)

    @Query("DELETE FROM photographer_registration WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM photographer_registration")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM photographer_registration WHERE isProfileComplete = 1 LIMIT 1)")
    suspend fun hasCompletedRegistration(): Boolean

    @Query("SELECT * FROM photographer_registration WHERE fullName LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' OR specialties LIKE '%' || :query || '%'")
    fun searchPhotographers(query: String): Flow<List<PhotographerRegistrationEntity>>

    @Query("SELECT * FROM photographer_registration WHERE specialties LIKE '%' || :specialty || '%' AND isProfileComplete = 1")
    fun filterBySpecialty(specialty: String): Flow<List<PhotographerRegistrationEntity>>
}
