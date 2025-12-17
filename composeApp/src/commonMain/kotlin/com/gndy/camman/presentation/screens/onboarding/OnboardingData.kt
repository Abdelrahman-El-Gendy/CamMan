package com.gndy.camman.presentation.screens.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.graphics.vector.ImageVector
import com.gndy.camman.resources.*
import org.jetbrains.compose.resources.StringResource

/**
 * Data class representing a single onboarding page
 */
data class OnboardingPage(
    val icon: ImageVector,
    val titleRes: StringResource,
    val descriptionRes: StringResource,
    val highlightTextRes: StringResource? = null
)

/**
 * List of onboarding pages
 */
val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.Default.CameraAlt,
        titleRes = Res.string.discover_amazing_photographers,
        descriptionRes = Res.string.discover_photographers_desc,
        highlightTextRes = Res.string.find_your_perfect_match
    ),
    OnboardingPage(
        icon = Icons.Default.Collections,
        titleRes = Res.string.explore_stunning_portfolios,
        descriptionRes = Res.string.explore_portfolios_desc,
        highlightTextRes = Res.string.quality_that_speaks
    ),
    OnboardingPage(
        icon = Icons.Default.CalendarMonth,
        titleRes = Res.string.book_with_confidence,
        descriptionRes = Res.string.book_with_confidence_desc,
        highlightTextRes = Res.string.seamless_booking
    ),
    OnboardingPage(
        icon = Icons.Default.Verified,
        titleRes = Res.string.trusted_and_professional,
        descriptionRes = Res.string.trusted_professional_desc,
        highlightTextRes = Res.string.verified_badge
    )
)
