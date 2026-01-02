package com.gndy.camman.presentation.screens.onboarding

import com.gndy.camman.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Data class representing a single onboarding page
 */
data class OnboardingPage(
    val image: DrawableResource,
    val titleRes: StringResource,
    val descriptionRes: StringResource
)

/**
 * List of onboarding pages with unique images
 */
val onboardingPages = listOf(
    // Page 1: Find Your Perfect Shot
    OnboardingPage(
        image = Res.drawable.onboard,
        titleRes = Res.string.onboarding_title_1,
        descriptionRes = Res.string.onboarding_desc_1
    ),
    // Page 2: Connect & Coordinate
    OnboardingPage(
        image = Res.drawable.onboard_chat,
        titleRes = Res.string.onboarding_title_2,
        descriptionRes = Res.string.onboarding_desc_2
    ),
    // Page 3: Secure Payments
    OnboardingPage(
        image = Res.drawable.ponboard,
        titleRes = Res.string.onboarding_title_3,
        descriptionRes = Res.string.onboarding_desc_3
    )
)
