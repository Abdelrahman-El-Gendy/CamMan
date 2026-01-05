package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.model.AuthState
import com.gndy.camman.domain.model.AuthUser
import com.gndy.camman.domain.model.UserType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 * 
 * Role Management:
 * - User role (CLIENT/PHOTOGRAPHER) is stored in auth.users.raw_user_meta_data
 * - Role is set during first signup and cannot be changed
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
     * Check if the current user has a role assigned
     */
    val hasRole: Boolean

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
    
    // ============== Role Management ==============
    
    /**
     * Get the user's role from metadata
     * @return UserType or null if not set
     */
    fun getUserRole(): UserType?
    
    /**
     * Set the user's role (one-time operation)
     * Role is stored in auth.users.raw_user_meta_data
     * 
     * @param role The role to assign (USER/CLIENT or PHOTOGRAPHER)
     * @return AuthResult indicating success or failure
     */
    suspend fun setUserRole(role: UserType): AuthResult
    
    /**
     * Check if user can change their role
     * Users can only set role once (during first login after signup)
     * 
     * @return true if role is not yet set, false otherwise
     */
    fun canSetRole(): Boolean
}
