package com.gndy.camman.domain.usecase.auth

import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.repository.AuthRepository

/**
 * Use case for sending password reset email
 */
class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): AuthResult {
        if (email.isBlank()) {
            return AuthResult.Error("Email is required")
        }
        if (!isValidEmail(email)) {
            return AuthResult.Error("Invalid email format")
        }

        return authRepository.sendPasswordResetEmail(email.trim())
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return email.matches(emailRegex.toRegex())
    }
}
