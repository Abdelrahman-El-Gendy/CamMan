package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.UserType
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing user roles
 * Handles role assignment and validation via Supabase backend
 */
interface UserRoleRepository {
    
    /**
     * Get the user's role from the backend
     * @param userId The authenticated user's ID
     * @return Resource containing the user's role, or null if not set
     */
    suspend fun getUserRole(userId: String): Resource<UserType?>
    
    /**
     * Check if the user has already selected a role
     * @param userId The authenticated user's ID
     * @return Resource containing true if role exists, false otherwise
     */
    suspend fun hasUserSelectedRole(userId: String): Resource<Boolean>
    
    /**
     * Set the user's role (one-time operation)
     * @param userId The authenticated user's ID
     * @param role The role to assign (CLIENT or PHOTOGRAPHER)
     * @return Resource indicating success or failure with validation errors
     */
    suspend fun setUserRole(userId: String, role: UserType): Resource<UserType>
    
    /**
     * Validate that the role can be assigned
     * Checks:
     * - User doesn't already have a role
     * - Role is valid (CLIENT or PHOTOGRAPHER)
     * @return Resource containing validation result
     */
    suspend fun validateRoleAssignment(userId: String, role: UserType): Resource<Boolean>
    
    /**
     * Create user profile if it doesn't exist
     * @param userId The authenticated user's ID
     * @param email User's email
     * @param displayName User's display name
     */
    suspend fun createProfileIfNotExists(
        userId: String, 
        email: String?, 
        displayName: String?
    ): Resource<Unit>
    
    /**
     * Observe user role changes (for real-time updates)
     */
    fun observeUserRole(userId: String): Flow<Resource<UserType?>>
}

/**
 * Role validation result
 */
sealed class RoleValidationResult {
    data object Valid : RoleValidationResult()
    data class Invalid(val reason: RoleValidationError) : RoleValidationResult()
}

/**
 * Possible role validation errors
 */
enum class RoleValidationError {
    ROLE_ALREADY_EXISTS,    // User already has a role assigned
    INVALID_ROLE,           // Role is not valid (not CLIENT or PHOTOGRAPHER)
    NETWORK_ERROR,          // Network connectivity issue
    SERVER_ERROR,           // Backend server error
    USER_NOT_FOUND,         // User doesn't exist in auth
    UNKNOWN_ERROR           // Unknown error occurred
}
