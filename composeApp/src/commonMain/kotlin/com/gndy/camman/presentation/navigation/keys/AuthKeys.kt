package com.gndy.camman.presentation.navigation.keys

import kotlinx.serialization.Serializable

/**
 * Navigation keys for the Authentication feature module
 * Following Navigation 3 modularization pattern - these represent the "api" layer
 */
@Serializable
sealed interface AuthKey {
    @Serializable
    data object SignIn : AuthKey

    @Serializable
    data object SignUp : AuthKey

    @Serializable
    data object ForgotPassword : AuthKey
}
