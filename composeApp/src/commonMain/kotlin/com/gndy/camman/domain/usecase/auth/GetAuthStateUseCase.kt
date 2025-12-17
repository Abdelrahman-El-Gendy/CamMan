package com.gndy.camman.domain.usecase.auth

import com.gndy.camman.domain.model.AuthState
import com.gndy.camman.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing authentication state changes
 */
class GetAuthStateUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AuthState> {
        return authRepository.authState
    }
}
