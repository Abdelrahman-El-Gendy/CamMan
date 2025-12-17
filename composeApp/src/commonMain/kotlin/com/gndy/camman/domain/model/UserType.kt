package com.gndy.camman.domain.model

/**
 * Represents the type of user in the app
 */
enum class UserType {
    USER,           // Client who browses and books photographers
    PHOTOGRAPHER    // Photographer who manages their portfolio
}

/**
 * App user with type information
 */
data class AppUser(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val userType: UserType,
    val isEmailVerified: Boolean = false
)
