package com.gndy.camman.data.repository

import com.gndy.camman.data.local.dao.PhotographerProfileDao
import com.gndy.camman.data.local.entity.PhotographerProfileEntity
import com.gndy.camman.data.remote.api.CamManApiService
import com.gndy.camman.data.remote.dto.ContactMessageRequest
import com.gndy.camman.domain.model.PhotographerProfile
import com.gndy.camman.domain.repository.ProfileRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfileRepositoryImpl(
    private val profileDao: PhotographerProfileDao,
    private val apiService: CamManApiService
) : ProfileRepository {

    override fun getPhotographerProfile(): Flow<Resource<PhotographerProfile>> = flow {
        emit(Resource.Loading())

        profileDao.getPhotographerProfile().collect { entity ->
            if (entity != null) {
                emit(Resource.Success(entity.toPhotographerProfile()))
            }
        }

        try {
            val response = apiService.getPhotographerProfile()
            val profile = response.toPhotographerProfile()

            profileDao.deleteProfile()
            profileDao.insertProfile(PhotographerProfileEntity.fromPhotographerProfile(profile))

            emit(Resource.Success(profile))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch profile"))
        }
    }

    override suspend fun refreshProfile(): Resource<Unit> {
        return try {
            val response = apiService.getPhotographerProfile()
            val profile = response.toPhotographerProfile()
            profileDao.deleteProfile()
            profileDao.insertProfile(PhotographerProfileEntity.fromPhotographerProfile(profile))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to refresh profile")
        }
    }

    override suspend fun sendContactMessage(
        name: String,
        email: String,
        subject: String,
        message: String
    ): Resource<Unit> {
        return try {
            val request = ContactMessageRequest(
                name = name,
                email = email,
                subject = subject,
                message = message
            )
            apiService.sendContactMessage(request)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send message")
        }
    }
}
