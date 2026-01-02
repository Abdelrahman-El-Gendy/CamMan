package com.gndy.camman.presentation.screens.auth.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.model.AuthState
import com.gndy.camman.domain.model.UserType
import com.gndy.camman.domain.repository.UserRoleRepository
import com.gndy.camman.domain.usecase.auth.GetAuthStateUseCase
import com.gndy.camman.domain.usecase.auth.SignUpUseCase
import com.gndy.camman.domain.util.Resource
import com.gndy.camman.presentation.screens.auth.authevents.SignUpUiEvent
import com.gndy.camman.presentation.screens.auth.authstates.SignUpUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for SignUp screen
 * 
 * Handles the complete signup flow with Supabase:
 * 1. Validate user inputs (email, password, user type)
 * 2. Create auth user in Supabase auth.users
 * 3. Create user profile in user_profiles table
 * 4. Set user role (client/photographer) in the profile
 */
class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val userRoleRepository: UserRoleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<SignUpUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            getAuthStateUseCase().collect { authState ->
                // Only auto-navigate if user is authenticated AND has completed signup flow
                // The signup flow sets the role, so we check if state is not loading
                if (authState is AuthState.Authenticated && !_uiState.value.isLoading) {
                    // Check if this is a fresh signup that we're handling
                    // Don't auto-navigate during our signup flow
                }
            }
        }
    }

    fun onDisplayNameChanged(name: String) {
        _uiState.update {
            it.copy(
                displayName = name,
                displayNameError = null,
                error = null
            )
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                error = null
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                error = null
            )
        }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                confirmPassword = password,
                confirmPasswordError = null,
                error = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onUserTypeSelected(userType: UserType) {
        _uiState.update {
            it.copy(
                selectedUserType = userType,
                userTypeError = null,
                error = null
            )
        }
    }

    fun onSignUpClick() {
        val state = _uiState.value

        // Validate inputs
        var hasError = false
        
        if (state.selectedUserType == null) {
            _uiState.update { it.copy(userTypeError = "Please select your account type") }
            hasError = true
        }
        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            hasError = true
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Password is required") }
            hasError = true
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }
        if (state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(confirmPasswordError = "Please confirm your password") }
            hasError = true
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Step 1: Create auth user in Supabase auth.users
            val authResult = signUpUseCase(
                email = state.email,
                password = state.password,
                confirmPassword = state.confirmPassword,
                displayName = state.displayName.takeIf { it.isNotBlank() }
            )

            when (authResult) {
                is AuthResult.Success -> {
                    val userId = authResult.user.uid
                    
                    // Step 2: Create user profile in user_profiles table
                    val profileResult = userRoleRepository.createProfileIfNotExists(
                        userId = userId,
                        email = state.email,
                        displayName = state.displayName.takeIf { it.isNotBlank() }
                    )
                    
                    when (profileResult) {
                        is Resource.Success -> {
                            // Step 3: Set user role (client/photographer)
                            val roleResult = userRoleRepository.setUserRole(
                                userId = userId,
                                role = state.selectedUserType!! // Already validated above
                            )
                            
                            when (roleResult) {
                                is Resource.Success -> {
                                    _uiState.update { it.copy(isLoading = false) }
                                    _uiEvents.emit(
                                        SignUpUiEvent.ShowSuccess(
                                            "Account created successfully! Welcome to CamMan."
                                        )
                                    )
                                    _uiEvents.emit(SignUpUiEvent.NavigateToHome)
                                }
                                is Resource.Error -> {
                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            error = "Account created but role setup failed: ${roleResult.message}"
                                        )
                                    }
                                    // Still navigate - user can set role later
                                    _uiEvents.emit(SignUpUiEvent.NavigateToHome)
                                }
                                is Resource.Loading -> {
                                    // Shouldn't happen
                                }
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Account created but profile setup failed: ${profileResult.message}"
                                )
                            }
                            // Still navigate - profile will be created on next sign in
                            _uiEvents.emit(SignUpUiEvent.NavigateToHome)
                        }
                        is Resource.Loading -> {
                            // Shouldn't happen
                        }
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = authResult.message
                        )
                    }
                }
            }
        }
    }

    fun onSignInClick() {
        viewModelScope.launch {
            _uiEvents.emit(SignUpUiEvent.NavigateToSignIn)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
