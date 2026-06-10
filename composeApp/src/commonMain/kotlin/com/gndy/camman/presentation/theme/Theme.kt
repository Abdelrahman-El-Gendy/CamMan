package com.gndy.camman.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.gndy.camman.data.local.preferences.ThemeMode

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

/**
 * Extended colors for the app that aren't part of Material3 ColorScheme
 */
@Immutable
data class ExtendedColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val surfaceElevated: Color,
    val success: Color,
    val successLight: Color,
    val warning: Color,
    val info: Color,
    val primaryAccent: Color  // Blue accent for buttons
)

private val DarkExtendedColors = ExtendedColors(
    textPrimary = Color(0xFFE6EDF3),
    textSecondary = Color(0xFF848D97),
    border = Color(0xFF30363D),
    surfaceElevated = Color(0xFF161B22),
    success = Color(0xFF22C55E),
    successLight = Color(0xFF4ADE80),
    warning = Color(0xFFFFA726),
    info = Color(0xFF42A5F5),
    primaryAccent = Color(0xFF4D76FD)
)

private val LightExtendedColors = ExtendedColors(
    textPrimary = Color(0xFF1A1A1A),
    textSecondary = Color(0xFF666666),
    border = Color(0xFFE0E0E0),
    surfaceElevated = Color(0xFFF5F5F5),
    success = Color(0xFF4CAF50),
    successLight = Color(0xFF81C784),
    warning = Color(0xFFFF9800),
    info = Color(0xFF2196F3),
    primaryAccent = Color(0xFF2563EB)
)

val LocalExtendedColors = staticCompositionLocalOf { DarkExtendedColors }

/**
 * Provides access to extended colors that aren't part of MaterialTheme
 */
object CamManColors {
    val extended: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}

@Composable
fun CamManTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CamManTypography,
            content = content
        )
    }
}
