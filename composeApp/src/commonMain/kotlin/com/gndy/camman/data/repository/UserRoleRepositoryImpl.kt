package com.gndy.camman.data.repository

import com.gndy.camman.domain.model.UserType
import com.gndy.camman.domain.repository.RoleValidationError
import com.gndy.camman.domain.repository.UserRoleRepository
import com.gndy.camman.domain.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Implementation of UserRoleRepository using Supabase
 */
class UserRoleRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : UserRoleRepository {

    private val postgrest = supabaseClient.postgrest

    override suspend fun getUserRole(userId: String): Resource<UserType?> {
        return try {
            val result = postgrest.from("user_profiles")
                .select(columns = Columns.list("user_type")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<UserProfileRole>()
            
            val userType = result?.userType?.toUserType()
            Resource.Success(userType)
        } catch (e: Exception) {
            Resource.Error(mapSupabaseError(e))
        }
    }

    override suspend fun hasUserSelectedRole(userId: String): Resource<Boolean> {
        return try {
            val result = postgrest.from("user_profiles")
                .select(columns = Columns.list("user_type")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<UserProfileRole>()
            
            // Check if user_type is set and not null/empty
            val hasRole = result?.userType?.isNotBlank() == true
            Resource.Success(hasRole)
        } catch (e: Exception) {
            // If profile doesn't exist, user hasn't selected role
            if (e.message?.contains("no rows") == true || 
                e.message?.contains("PGRST116") == true) {
                Resource.Success(false)
            } else {
                Resource.Error(mapSupabaseError(e))
            }
        }
    }

    override suspend fun setUserRole(userId: String, role: UserType): Resource<UserType> {
        return try {
            // First validate the role can be assigned
            val validation = validateRoleAssignment(userId, role)
            if (validation is Resource.Error) {
                return Resource.Error(validation.message ?: "Validation failed")
            }
            
            if (validation is Resource.Success && validation.data == false) {
                return Resource.Error("Role assignment validation failed")
            }

            val roleString = role.toDbString()
            
            // Update the user_type in user_profiles
            postgrest.from("user_profiles")
                .update({
                    set("user_type", roleString)
                    set("updated_at", "now()")
                }) {
                    filter {
                        eq("id", userId)
                    }
                }
            
            Resource.Success(role)
        } catch (e: Exception) {
            Resource.Error(mapSupabaseError(e))
        }
    }

    override suspend fun validateRoleAssignment(userId: String, role: UserType): Resource<Boolean> {
        return try {
            // Check if user already has a role
            val existingRole = getUserRole(userId)
            
            if (existingRole is Resource.Error) {
                return Resource.Error(existingRole.message ?: "Failed to check existing role")
            }
            
            if (existingRole is Resource.Success && existingRole.data != null) {
                // User already has a role - cannot change it
                return Resource.Error(
                    "${RoleValidationError.ROLE_ALREADY_EXISTS.name}: Role already assigned. You cannot change your role once selected."
                )
            }
            
            // Validate role is valid (CLIENT or PHOTOGRAPHER only for new users)
            if (role != UserType.USER && role != UserType.PHOTOGRAPHER) {
                return Resource.Error(
                    "${RoleValidationError.INVALID_ROLE.name}: Invalid role selection"
                )
            }
            
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(mapSupabaseError(e))
        }
    }

    override suspend fun createProfileIfNotExists(
        userId: String,
        email: String?,
        displayName: String?
    ): Resource<Unit> {
        return try {
            // Check if profile already exists
            val existingProfile = postgrest.from("user_profiles")
                .select(columns = Columns.list("id")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<UserProfileId>()
            
            if (existingProfile != null) {
                // Profile already exists
                return Resource.Success(Unit)
            }
            
            // Create new profile without role (role will be set in separate step)
            postgrest.from("user_profiles")
                .insert(
                    UserProfileInsert(
                        id = userId,
                        email = email,
                        displayName = displayName,
                        userType = null // Role not set yet
                    )
                )
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            // If it's a duplicate key error, profile already exists
            if (e.message?.contains("duplicate key") == true ||
                e.message?.contains("23505") == true) {
                Resource.Success(Unit)
            } else {
                Resource.Error(mapSupabaseError(e))
            }
        }
    }

    override fun observeUserRole(userId: String): Flow<Resource<UserType?>> = flow {
        emit(Resource.Loading())
        
        // First emit current value
        val current = getUserRole(userId)
        emit(current)
        
        // For real-time updates, we would use Supabase Realtime
        // This is a simplified version that just returns the current value
        // In production, you'd want to use postgresChangeFlow
    }

    /**
     * Convert UserType enum to database string
     */
    private fun UserType.toDbString(): String = when (this) {
        UserType.USER -> "client"
        UserType.PHOTOGRAPHER -> "photographer"
    }

    /**
     * Convert database string to UserType enum
     */
    private fun String.toUserType(): UserType? = when (this.lowercase()) {
        "client" -> UserType.USER
        "photographer" -> UserType.PHOTOGRAPHER
        else -> null
    }

    /**
     * Map Supabase exceptions to user-friendly error messages
     */
    private fun mapSupabaseError(e: Exception): String {
        val message = e.message?.lowercase() ?: return "An unknown error occurred"

        return when {
            message.contains("network") || 
            message.contains("connection") ||
            message.contains("timeout") ->
                "Network error. Please check your internet connection."

            message.contains("permission") || 
            message.contains("rls") ||
            message.contains("policy") ->
                "Permission denied. Please try signing in again."

            message.contains("not found") || 
            message.contains("no rows") ||
            message.contains("pgrst116") ->
                "User profile not found."

            message.contains("duplicate") || 
            message.contains("23505") ->
                "Profile already exists."

            message.contains("check constraint") ||
            message.contains("23514") ->
                "Invalid role value."

            else -> e.message ?: "An unknown error occurred"
        }
    }
}

/**
 * DTO for reading user role from database
 */
@Serializable
private data class UserProfileRole(
    @SerialName("user_type")
    val userType: String?
)

/**
 * DTO for checking profile existence
 */
@Serializable
private data class UserProfileId(
    val id: String
)

/**
 * DTO for inserting new user profile
 */
@Serializable
private data class UserProfileInsert(
    val id: String,
    val email: String?,
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("user_type")
    val userType: String?
)
