package com.gndy.camman.presentation.screens.auth.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.AuthResult
import com.gndy.camman.domain.usecase.auth.ResetPasswordUseCase
import com.gndy.camman.presentation.screens.auth.authevents.ForgotPasswordUiEvent
import com.gndy.camman.presentation.screens.auth.authstates.ForgotPasswordUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<ForgotPasswordUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                error = null,
                isEmailSent = false
            )
        }
    }

    fun onResetPasswordClick() {
        val state = _uiState.value

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = resetPasswordUseCase(state.email)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isEmailSent = true
                        )
                    }
                    _uiEvents.emit(
                        ForgotPasswordUiEvent.ShowSuccess(
                            "Password reset email sent! Check your inbox."
                        )
                    )
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

    fun onBackClick() {
        viewModelScope.launch {
            _uiEvents.emit(ForgotPasswordUiEvent.NavigateBack)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
