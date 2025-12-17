package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.PhotographerProfile
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getPhotographerProfile(): Flow<Resource<PhotographerProfile>>
    suspend fun refreshProfile(): Resource<Unit>
    suspend fun sendContactMessage(
        name: String,
        email: String,
        subject: String,
        message: String
    ): Resource<Unit>
}
