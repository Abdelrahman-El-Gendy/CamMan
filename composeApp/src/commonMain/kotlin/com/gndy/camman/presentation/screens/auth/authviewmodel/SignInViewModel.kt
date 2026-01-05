package com.gndy.camman.presentation.screens.auth.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.model.AuthState
import com.gndy.camman.domain.model.UserType
import com.gndy.camman.domain.repository.AuthRepository
import com.gndy.camman.domain.usecase.auth.GetAuthStateUseCase
import com.gndy.camman.domain.usecase.auth.SignInUseCase
import com.gndy.camman.presentation.screens.auth.authevents.SignInUiEvent
import com.gndy.camman.presentation.screens.auth.authstates.SignInUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<SignInUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            getAuthStateUseCase().collect { authState ->
                if (authState is AuthState.Authenticated) {
                    // Check user's role and navigate accordingly
                    navigateBasedOnRole()
                }
            }
        }
    }
    
    /**
     * Navigates to the appropriate home based on user's role
     */
    private suspend fun navigateBasedOnRole() {
        val role = authRepository.getUserRole()
        when (role) {
            UserType.USER -> _uiEvents.emit(SignInUiEvent.NavigateToClientHome)
            UserType.PHOTOGRAPHER -> _uiEvents.emit(SignInUiEvent.NavigateToPhotographerHome)
            null -> _uiEvents.emit(SignInUiEvent.NavigateToRoleSelection)
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

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSignInClick() {
        val state = _uiState.value

        // Validate inputs
        var hasError = false
        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            hasError = true
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Password is required") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = signInUseCase(state.email, state.password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    // Navigate based on user's role from auth metadata
                    navigateBasedOnRole()
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _uiEvents.emit(SignInUiEvent.NavigateToSignUp)
        }
    }

    fun onForgotPasswordClick() {
        viewModelScope.launch {
            _uiEvents.emit(SignInUiEvent.NavigateToForgotPassword)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
