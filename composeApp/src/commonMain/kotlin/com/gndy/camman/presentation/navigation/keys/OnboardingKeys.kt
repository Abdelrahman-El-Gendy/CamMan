package com.gndy.camman.presentation.navigation.keys

import kotlinx.serialization.Serializable

/**
 * Navigation keys for the Onboarding feature module
 * Following Navigation 3 modularization pattern - these represent the "api" layer
 */
@Serializable
sealed interface OnboardingKey {
    @Serializable
    data object Splash : OnboardingKey

    @Serializable
    data object Onboarding : OnboardingKey

    @Serializable
    data object UserTypeSelection : OnboardingKey
}
