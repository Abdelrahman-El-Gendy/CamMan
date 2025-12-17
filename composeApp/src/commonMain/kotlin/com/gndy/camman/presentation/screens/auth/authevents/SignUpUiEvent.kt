package com.gndy.camman.presentation.screens.auth.authevents

sealed class SignUpUiEvent {
    data object NavigateToHome : SignUpUiEvent()
    data object NavigateToSignIn : SignUpUiEvent()
    data class ShowError(val message: String) : SignUpUiEvent()
    data class ShowSuccess(val message: String) : SignUpUiEvent()
}