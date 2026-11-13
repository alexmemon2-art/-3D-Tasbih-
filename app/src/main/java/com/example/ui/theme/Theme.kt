package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TasbihDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = EmeraldDeepDark,
    primaryContainer = EmeraldMedium,
    onPrimaryContainer = GoldBright,
    secondary = GoldBright,
    onSecondary = EmeraldDeepDark,
    secondaryContainer = EmeraldDark,
    onSecondaryContainer = IvoryText,
    tertiary = JadeGreen,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = IvoryText,
    surface = CardBackground,
    onSurface = IvoryText,
    surfaceVariant = EmeraldDark,
    onSurfaceVariant = IvorySubtle,
    outline = GoldMuted,
    outlineVariant = GlassBorder
)

private val TasbihLightColorScheme = lightColorScheme(
    primary = GoldDeep,
    onPrimary = Color.White,
    primaryContainer = GoldLight,
    onPrimaryContainer = EmeraldDeepDark,
    secondary = EmeraldDark,
    onSecondary = Color.White,
    secondaryContainer = EmeraldMedium,
    onSecondaryContainer = IvoryText,
    tertiary = JadeGreen,
    onTertiary = Color.White,
    background = EmeraldDark,
    onBackground = IvoryText,
    surface = EmeraldMedium,
    onSurface = IvoryText,
    surfaceVariant = EmeraldDeepDark,
    onSurfaceVariant = IvorySubtle,
    outline = GoldPrimary,
    outlineVariant = GlassBorder
)

@Composable
fun TasbihTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) TasbihDarkColorScheme else TasbihDarkColorScheme // The Islamic emerald & gold palette shines best in dark/rich mood

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
