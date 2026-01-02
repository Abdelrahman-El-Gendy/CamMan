package com.gndy.camman.presentation.screens.auth.authstates

import com.gndy.camman.domain.model.UserType

data class SignUpUiState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedUserType: UserType? = null,
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val displayNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val userTypeError: String? = null,
    val error: String? = null
)