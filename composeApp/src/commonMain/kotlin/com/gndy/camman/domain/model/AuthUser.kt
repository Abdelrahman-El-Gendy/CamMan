package com.gndy.camman.domain.model

/**
 * Domain model representing an authenticated user
 * 
 * Role is stored in auth.users.raw_user_meta_data for simplicity and reliability
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean,
    val providerId: String?,
    val role: UserType? = null  // Role stored in user metadata
) {
    /**
     * Check if user has selected a role
     */
    val hasRole: Boolean get() = role != null
    
    /**
     * Check if user is a client
     */
    val isClient: Boolean get() = role == UserType.USER
    
    /**
     * Check if user is a photographer
     */
    val isPhotographer: Boolean get() = role == UserType.PHOTOGRAPHER
}

/**
 * Authentication state
 */
sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: AuthUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * Authentication result for sign in/sign up operations
 */
sealed class AuthResult {
    data class Success(val user: AuthUser) : AuthResult()
    data class Error(val message: String, val exception: Exception? = null) : AuthResult()
}
