package com.gndy.camman.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = PureBlack,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,

    secondary = LightGray,
    onSecondary = PureBlack,
    secondaryContainer = DarkGray,
    onSecondaryContainer = OffWhite,

    tertiary = Gold,
    onTertiary = PureBlack,
    tertiaryContainer = GoldDark,
    onTertiaryContainer = GoldLight,

    background = DarkBackground,
    onBackground = PureWhite,

    surface = DarkSurface,
    onSurface = PureWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = LightGray,

    error = Error,
    onError = PureWhite,
    errorContainer = Error.copy(alpha = 0.3f),
    onErrorContainer = Error,

    outline = MediumGray,
    outlineVariant = DarkGray
)

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = PureBlack,
    primaryContainer = GoldLight,
    onPrimaryContainer = GoldDark,

    secondary = DarkGray,
    onSecondary = PureWhite,
    secondaryContainer = LightGray,
    onSecondaryContainer = CharcoalBlack,

    tertiary = Gold,
    onTertiary = PureBlack,
    tertiaryContainer = GoldLight,
    onTertiaryContainer = GoldDark,

    background = LightBackground,
    onBackground = CharcoalBlack,

    surface = LightSurface,
    onSurface = CharcoalBlack,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = MediumGray,

    error = Error,
    onError = PureWhite,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error,

    outline = LightGray,
    outlineVariant = OffWhite
)

@Composable
fun CamManTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CamManTypography,
        content = content
    )
}
