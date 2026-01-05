package com.gndy.camman.presentation.screens.auth.authevents

sealed class SignUpUiEvent {
    /** Navigate to sign in screen after successful registration */
    data object NavigateToSignIn : SignUpUiEvent()
    
    /** Show error message */
    data class ShowError(val message: String) : SignUpUiEvent()
    
    /** Show success message */
    data class ShowSuccess(val message: String) : SignUpUiEvent()
}
