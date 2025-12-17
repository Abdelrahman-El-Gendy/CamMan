package com.gndy.camman.domain.usecase.auth

import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.repository.AuthRepository

/**
 * Use case for creating a new account with email and password
 */
class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        displayName: String? = null
    ): AuthResult {
        // Validate inputs
        if (email.isBlank()) {
            return AuthResult.Error("Email is required")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Password is required")
        }
        if (confirmPassword.isBlank()) {
            return AuthResult.Error("Please confirm your password")
        }
        if (!isValidEmail(email)) {
            return AuthResult.Error("Invalid email format")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters")
        }
        if (password != confirmPassword) {
            return AuthResult.Error("Passwords do not match")
        }
        if (!isStrongPassword(password)) {
            return AuthResult.Error("Password must contain at least one letter and one number")
        }

        return authRepository.signUpWithEmail(
            email = email.trim(),
            password = password,
            displayName = displayName?.trim()?.takeIf { it.isNotEmpty() }
        )
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return email.matches(emailRegex.toRegex())
    }

    private fun isStrongPassword(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }
}
