package com.gndy.camman.domain.usecase.auth

import com.gndy.camman.domain.repository.AuthRepository

/**
 * Use case for signing out the current user
 */
class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}
