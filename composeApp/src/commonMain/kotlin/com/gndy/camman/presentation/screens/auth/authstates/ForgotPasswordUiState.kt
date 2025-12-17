package com.gndy.camman.presentation.screens.auth.authstates

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val error: String? = null,
    val isEmailSent: Boolean = false
)