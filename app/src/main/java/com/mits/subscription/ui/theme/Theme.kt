package com.mits.subscription.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow

/*private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)*/

private val LightColorScheme = Colors(
    primary = Purple,
    primaryVariant = PurpleDark,
    secondary = Yellow,
    secondaryVariant = YellowDark,
    surface = YellowLight,
    background = PurpleGrey,
    error = Red,
    onPrimary = White,
    onSecondary = White,
    onBackground = PurpleDark,
    onSurface = PurpleDark,
    onError = White,
    isLight = true

    /*   onPrimary = Color(0xFF60009e),
       primaryContainer = Color(0xFFE90457),
       onPrimaryContainer = Color(0xFF13E4A2),
       inversePrimary = Color(0xFFFF9100),
       secondary = Color(0xFF00897B),
       onSecondary = Color(0xFFC0710D),
       secondaryContainer = Color(0xFFDAB411),
       onSecondaryContainer = Color(0xFFC0CA33),
       tertiary = Color(0xFFFB8C00),
       onTertiary = Color(0xFF009E71),
       tertiaryContainer = Color(0xFF616063),
       onTertiaryContainer = Color(0xFF2B0842),
       background = Color(0xFFEC3B04),
       onBackground = Color(0xFF4C839E),
       surface = Color(0xFFD3911E),
       onSurface = Color(0xEEFCE273),
       surfaceVariant = Color(0xFFDCB6E6),
       onSurfaceVariant = Color(0xFFFF6F00),
       inverseSurface = Color(0xFF76FF03),
       inverseOnSurface = Color(0xFF00E5FF),
       outline = Color(0xFFE49C86),*/
)

@Composable
fun SubscriptionTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    MaterialTheme(
        colors = colorScheme,
        typography = Typography,
        content = content
    )
}