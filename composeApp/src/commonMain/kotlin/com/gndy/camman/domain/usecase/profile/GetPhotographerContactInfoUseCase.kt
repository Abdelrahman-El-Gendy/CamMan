package com.gndy.camman.domain.usecase.profile

import com.gndy.camman.domain.model.PhotographerProfile
import com.gndy.camman.domain.repository.ProfileRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class GetPhotographerContactInfoUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<Resource<PhotographerProfile>> {
        return profileRepository.getPhotographerProfile()
    }

    suspend fun sendContactMessage(
        name: String,
        email: String,
        subject: String,
        message: String
    ): Resource<Unit> {
        return profileRepository.sendContactMessage(
            name = name,
            email = email,
            subject = subject,
            message = message
        )
    }
}
