package com.gndy.camman.presentation.screens.auth.authevents

sealed class SignInUiEvent {
    data object NavigateToClientHome : SignInUiEvent()
    data object NavigateToPhotographerHome : SignInUiEvent()
    data object NavigateToRoleSelection : SignInUiEvent()
    data object NavigateToSignUp : SignInUiEvent()
    data object NavigateToForgotPassword : SignInUiEvent()
    data class ShowError(val message: String) : SignInUiEvent()
    data class ShowSuccess(val message: String) : SignInUiEvent()
}
