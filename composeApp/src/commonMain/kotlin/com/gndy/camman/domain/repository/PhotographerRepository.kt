package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.Photographer
import com.gndy.camman.domain.model.PhotographerRegistration
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface PhotographerRepository {
    /**
     * Get all photographers for browsing (client side)
     */
    fun getAllPhotographers(): Flow<Resource<List<Photographer>>>

    /**
     * Get a specific photographer by ID
     */
    suspend fun getPhotographerById(id: String): Resource<Photographer>

    /**
     * Get current photographer's registration/profile (photographer side)
     */
    fun getCurrentPhotographerProfile(): Flow<Resource<PhotographerRegistration?>>

    /**
     * Check if current user has completed registration
     */
    suspend fun hasCompletedRegistration(): Boolean

    /**
     * Save photographer registration (create or update)
     */
    suspend fun savePhotographerRegistration(registration: PhotographerRegistration): Resource<PhotographerRegistration>

    /**
     * Update photographer profile
     */
    suspend fun updatePhotographerProfile(registration: PhotographerRegistration): Resource<Unit>

    /**
     * Update availability status
     */
    suspend fun updateAvailability(isAvailable: Boolean): Resource<Unit>

    /**
     * Search photographers by query
     */
    fun searchPhotographers(query: String): Flow<Resource<List<Photographer>>>

    /**
     * Filter photographers by specialty
     */
    fun filterBySpecialty(specialty: String): Flow<Resource<List<Photographer>>>
}
