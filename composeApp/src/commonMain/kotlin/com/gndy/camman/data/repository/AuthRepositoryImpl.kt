package com.gndy.camman.data.repository

import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.model.AuthState
import com.gndy.camman.domain.model.AuthUser
import com.gndy.camman.domain.model.UserType
import com.gndy.camman.domain.repository.AuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of AuthRepository using Firebase Authentication
 * 
 * NOTE: This is a legacy implementation. The app now uses SupabaseAuthRepositoryImpl.
 * Role management is not fully supported with Firebase - use Supabase instead.
 */
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val authState: Flow<AuthState> = firebaseAuth.authStateChanged.map { firebaseUser ->
        if (firebaseUser != null) {
            AuthState.Authenticated(firebaseUser.toAuthUser())
        } else {
            AuthState.Unauthenticated
        }
    }

    override val currentUser: AuthUser?
        get() = firebaseAuth.currentUser?.toAuthUser()

    override val isLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null
    
    override val hasRole: Boolean
        get() = false // Firebase doesn't support role management in metadata

    override suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password)
            val user = result.user
            if (user != null) {
                AuthResult.Success(user.toAuthUser())
            } else {
                AuthResult.Error("Sign in failed: No user returned")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e), e)
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String?
    ): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val user = result.user

            if (user != null) {
                // Update display name if provided
                if (!displayName.isNullOrBlank()) {
                    user.updateProfile(displayName = displayName)
                }

                // Send email verification
                try {
                    user.sendEmailVerification()
                } catch (e: Exception) {
                    // Don't fail the sign up if verification email fails
                }

                AuthResult.Success(user.toAuthUser())
            } else {
                AuthResult.Error("Sign up failed: No user returned")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e), e)
        }
    }

    override suspend fun signOut() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            // Ignore sign out errors
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            firebaseAuth.sendPasswordResetEmail(email)
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
            AuthResult.Error(mapFirebaseError(e), e)
        }
    }

    override suspend fun sendEmailVerification(): AuthResult {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.sendEmailVerification()
                AuthResult.Success(user.toAuthUser())
            } else {
                AuthResult.Error("No user is signed in")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e), e)
        }
    }

    override suspend fun reloadUser(): AuthResult {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.reload()
                AuthResult.Success(user.toAuthUser())
            } else {
                AuthResult.Error("No user is signed in")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e), e)
        }
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): AuthResult {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.updateProfile(displayName = displayName, photoUrl = photoUrl)
                AuthResult.Success(user.toAuthUser())
            } else {
                AuthResult.Error("No user is signed in")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e), e)
        }
    }

    override suspend fun deleteAccount(): AuthResult {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.delete()
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
            } else {
                AuthResult.Error("No user is signed in")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e), e)
        }
    }
    
    // ============== Role Management (Not fully supported with Firebase) ==============
    
    override fun getUserRole(): UserType? {
        // Firebase doesn't natively support role in user metadata like Supabase
        // You would need to use Firestore/Realtime Database for this
        return null
    }
    
    override suspend fun setUserRole(role: UserType): AuthResult {
        // Firebase doesn't support storing arbitrary metadata in auth user
        // You would need to use Firestore/Realtime Database
        return AuthResult.Error("Role management requires Supabase. Please use SupabaseAuthRepositoryImpl.")
    }
    
    override fun canSetRole(): Boolean {
        return false
    }

    /**
     * Convert FirebaseUser to domain AuthUser
     */
    private fun FirebaseUser.toAuthUser(): AuthUser {
        return AuthUser(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoURL,
            isEmailVerified = isEmailVerified,
            providerId = providerId,
            role = null // Firebase doesn't support role in metadata
        )
    }

    /**
     * Map Firebase exceptions to user-friendly error messages
     */
    private fun mapFirebaseError(e: Exception): String {
        val message = e.message?.lowercase() ?: return "An unknown error occurred"

        return when {
            message.contains("email-already-in-use") || message.contains("email already in use") ->
                "This email is already registered. Please sign in instead."

            message.contains("invalid-email") || message.contains("invalid email") ->
                "Invalid email address format."

            message.contains("weak-password") || message.contains("weak password") ->
                "Password is too weak. Please use at least 6 characters."

            message.contains("user-not-found") || message.contains("user not found") ->
                "No account found with this email. Please sign up."

            message.contains("wrong-password") || message.contains("wrong password") ||
                    message.contains("invalid-credential") || message.contains("invalid credential") ->
                "Incorrect email or password. Please try again."

            message.contains("user-disabled") || message.contains("user disabled") ->
                "This account has been disabled. Please contact support."

            message.contains("too-many-requests") || message.contains("too many requests") ->
                "Too many failed attempts. Please try again later."

            message.contains("network") || message.contains("internet") ->
                "Network error. Please check your internet connection."

            message.contains("requires-recent-login") || message.contains("recent login") ->
                "Please sign in again to complete this action."

            else -> e.message ?: "An unknown error occurred"
        }
    }
}
