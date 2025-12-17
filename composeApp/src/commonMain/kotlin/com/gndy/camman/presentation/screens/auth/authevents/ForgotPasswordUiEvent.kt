package com.gndy.camman.presentation.screens.auth.authevents

sealed class ForgotPasswordUiEvent {
    data object NavigateBack : ForgotPasswordUiEvent()
    data class ShowError(val message: String) : ForgotPasswordUiEvent()
    data class ShowSuccess(val message: String) : ForgotPasswordUiEvent()
}