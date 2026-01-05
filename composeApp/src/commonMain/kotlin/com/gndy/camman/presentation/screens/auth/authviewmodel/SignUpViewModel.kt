package com.gndy.camman.presentation.screens.auth.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.model.UserType
import com.gndy.camman.domain.repository.AuthRepository
import com.gndy.camman.domain.usecase.auth.GetAuthStateUseCase
import com.gndy.camman.domain.usecase.auth.SignUpUseCase
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
 * 1. User selects their role (Client or Photographer)
 * 2. Validate user inputs (email, password, role)
 * 3. Create auth user in Supabase auth.users
 * 4. Store role in auth.users.raw_user_meta_data
 * 5. Sign out the user (requires login to access app)
 * 6. Navigate to SignIn screen so user can log in with their credentials
 */
class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<SignUpUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

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

    /**
     * Handle user type (role) selection
     */
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

        // Validate role selection first
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
                    // Step 2: Set user role in auth.users.raw_user_meta_data
                    val roleResult = authRepository.setUserRole(state.selectedUserType!!)
                    
                    when (roleResult) {
                        is AuthResult.Success -> {
                            // Step 3: Sign out the user so they need to log in
                            authRepository.signOut()
                            
                            _uiState.update { it.copy(isLoading = false) }
                            _uiEvents.emit(
                                SignUpUiEvent.ShowSuccess(
                                    "Account created successfully! Please sign in to continue."
                                )
                            )
                            
                            // Step 4: Navigate to SignIn screen
                            _uiEvents.emit(SignUpUiEvent.NavigateToSignIn)
                        }
                        is AuthResult.Error -> {
                            // Account created but role failed - still navigate to sign in
                            authRepository.signOut()
                            
                            _uiState.update { it.copy(isLoading = false) }
                            _uiEvents.emit(
                                SignUpUiEvent.ShowSuccess(
                                    "Account created! Please sign in to complete setup."
                                )
                            )
                            
                            _uiEvents.emit(SignUpUiEvent.NavigateToSignIn)
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
