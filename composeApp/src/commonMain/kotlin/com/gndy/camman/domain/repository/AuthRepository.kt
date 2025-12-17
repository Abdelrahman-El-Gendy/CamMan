package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.model.AuthState
import com.gndy.camman.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    /**
     * Flow that emits the current authentication state
     */
    val authState: Flow<AuthState>

    /**
     * Get the currently logged in user, or null if not authenticated
     */
    val currentUser: AuthUser?

    /**
     * Check if user is currently logged in
     */
    val isLoggedIn: Boolean

    /**
     * Sign in with email and password
     */
    suspend fun signInWithEmail(email: String, password: String): AuthResult

    /**
     * Create a new account with email and password
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String? = null
    ): AuthResult

    /**
     * Sign out the current user
     */
    suspend fun signOut()

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): AuthResult

    /**
     * Send email verification to current user
     */
    suspend fun sendEmailVerification(): AuthResult

    /**
     * Reload current user data
     */
    suspend fun reloadUser(): AuthResult

    /**
     * Update user profile (display name and/or photo URL)
     */
    suspend fun updateProfile(displayName: String?, photoUrl: String?): AuthResult

    /**
     * Delete the current user account
     */
    suspend fun deleteAccount(): AuthResult
}
