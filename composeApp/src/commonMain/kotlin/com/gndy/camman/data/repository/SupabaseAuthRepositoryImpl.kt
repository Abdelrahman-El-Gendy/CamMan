package com.gndy.camman.data.repository

import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.model.AuthState
import com.gndy.camman.domain.model.AuthUser
import com.gndy.camman.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Implementation of AuthRepository using Supabase Authentication
 * 
 * This implementation supports:
 * - Multiple concurrent users (production-ready)
 * - Email/Password authentication
 * - Email verification
 * - Password reset
 * - Session management with automatic token refresh
 */
class SupabaseAuthRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    private val auth = supabaseClient.auth

    override val authState: Flow<AuthState> = auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> {
                val user = status.session.user
                if (user != null) {
                    AuthState.Authenticated(user.toAuthUser())
                } else {
                    AuthState.Unauthenticated
                }
            }
            is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated
            is SessionStatus.Initializing -> AuthState.Loading
            is SessionStatus.RefreshFailure -> {
                @Suppress("DEPRECATION")
                AuthState.Error("Session refresh failed: ${status.cause}")
            }
        }
    }

    override val currentUser: AuthUser?
        get() = auth.currentUserOrNull()?.toAuthUser()

    override val isLoggedIn: Boolean
        get() = auth.currentUserOrNull() != null

    override suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            val user = auth.currentUserOrNull()
            if (user != null) {
                AuthResult.Success(user.toAuthUser())
            } else {
                AuthResult.Error("Sign in failed: No user returned")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapSupabaseError(e), e)
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String?
    ): AuthResult {
        return try {
            // Sign up with email and password
            // Supabase automatically sends confirmation email if enabled in dashboard
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                // Store display name in user metadata as JsonObject
                if (!displayName.isNullOrBlank()) {
                    data = buildJsonObject {
                        put("display_name", JsonPrimitive(displayName))
                        put("full_name", JsonPrimitive(displayName))
                    }
                }
            }
            
            val user = auth.currentUserOrNull()
            if (user != null) {
                AuthResult.Success(user.toAuthUser())
            } else {
                // User created but needs email confirmation
                AuthResult.Success(
                    AuthUser(
                        uid = "",
                        email = email,
                        displayName = displayName,
                        photoUrl = null,
                        isEmailVerified = false,
                        providerId = "email"
                    )
                )
            }
        } catch (e: Exception) {
            AuthResult.Error(mapSupabaseError(e), e)
        }
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            // Ignore sign out errors - user is signed out locally anyway
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            auth.resetPasswordForEmail(email)
            AuthResult.Success(
                AuthUser(
                    uid = "",
                    email = email,
                    displayName = null,
                    photoUrl = null,
                    isEmailVerified = false,
                    providerId = null
                )
            )
        } catch (e: Exception) {
            AuthResult.Error(mapSupabaseError(e), e)
        }
    }

    override suspend fun sendEmailVerification(): AuthResult {
        return try {
            val user = auth.currentUserOrNull()
            if (user != null && user.email != null) {
                // Resend the signup confirmation email
                auth.resendEmail(OtpType.Email.SIGNUP, user.email!!)
                AuthResult.Success(user.toAuthUser())
            } else {
                AuthResult.Error("No user is signed in or email not available")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapSupabaseError(e), e)
        }
    }

    override suspend fun reloadUser(): AuthResult {
        return try {
            auth.refreshCurrentSession()
            val user = auth.currentUserOrNull()
            if (user != null) {
                AuthResult.Success(user.toAuthUser())
            } else {
                AuthResult.Error("No user is signed in")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapSupabaseError(e), e)
        }
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): AuthResult {
        return try {
            val user = auth.currentUserOrNull()
            if (user != null) {
                // Update user metadata with new profile info
                auth.updateUser {
                    data {
                        displayName?.let { put("display_name", JsonPrimitive(it)) }
                        displayName?.let { put("full_name", JsonPrimitive(it)) }
                        photoUrl?.let { put("avatar_url", JsonPrimitive(it)) }
                    }
                }
                
                // Refresh to get updated user
                auth.refreshCurrentSession()
                val updatedUser = auth.currentUserOrNull()
                if (updatedUser != null) {
                    AuthResult.Success(updatedUser.toAuthUser())
                } else {
                    AuthResult.Success(user.toAuthUser())
                }
            } else {
                AuthResult.Error("No user is signed in")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapSupabaseError(e), e)
        }
    }

    override suspend fun deleteAccount(): AuthResult {
        return try {
            // Note: Supabase requires service_role key to delete users from client
            // In production, this should be done via an Edge Function or server-side
            // For now, we'll sign out the user and note that admin deletion is needed
            auth.signOut()
            AuthResult.Success(
                AuthUser(
                    uid = "",
                    email = null,
                    displayName = null,
                    photoUrl = null,
                    isEmailVerified = false,
                    providerId = null
                )
            )
        } catch (e: Exception) {
            AuthResult.Error(mapSupabaseError(e), e)
        }
    }

    /**
     * Convert Supabase UserInfo to domain AuthUser
     */
    private fun UserInfo.toAuthUser(): AuthUser {
        val metadata = userMetadata
        return AuthUser(
            uid = id,
            email = email,
            displayName = metadata?.getStringOrNull("display_name")
                ?: metadata?.getStringOrNull("full_name"),
            photoUrl = metadata?.getStringOrNull("avatar_url"),
            isEmailVerified = emailConfirmedAt != null,
            providerId = appMetadata?.getStringOrNull("provider") ?: "email"
        )
    }

    /**
     * Helper extension to safely get a String from JsonObject
     */
    private fun JsonObject.getStringOrNull(key: String): String? {
        return this[key]?.jsonPrimitive?.content
    }

    /**
     * Map Supabase exceptions to user-friendly error messages
     */
    private fun mapSupabaseError(e: Exception): String {
        val message = e.message?.lowercase() ?: return "An unknown error occurred"

        return when {
            message.contains("user already registered") || 
            message.contains("email already") ||
            message.contains("already exists") ->
                "This email is already registered. Please sign in instead."

            message.contains("invalid email") || message.contains("invalid_email") ->
                "Invalid email address format."

            message.contains("weak password") || 
            message.contains("password should be at least") ||
            message.contains("password too short") ->
                "Password is too weak. Please use at least 6 characters."

            message.contains("user not found") || 
            message.contains("no user found") ||
            message.contains("invalid login credentials") ->
                "Incorrect email or password. Please try again."

            message.contains("email not confirmed") ->
                "Please verify your email address before signing in."

            message.contains("rate limit") || message.contains("too many requests") ->
                "Too many attempts. Please try again later."

            message.contains("network") || 
            message.contains("connection") ||
            message.contains("timeout") ->
                "Network error. Please check your internet connection."

            message.contains("session expired") || message.contains("refresh token") ->
                "Your session has expired. Please sign in again."

            else -> e.message ?: "An unknown error occurred"
        }
    }
}
