package com.gndy.camman.domain.usecase.auth

import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.repository.AuthRepository

/**
 * Use case for signing in with email and password
 */
class SignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        // Validate inputs
        if (email.isBlank()) {
            return AuthResult.Error("Email is required")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Password is required")
        }
        if (!isValidEmail(email)) {
            return AuthResult.Error("Invalid email format")
        }

        return authRepository.signInWithEmail(email.trim(), password)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return email.matches(emailRegex.toRegex())
    }
}
