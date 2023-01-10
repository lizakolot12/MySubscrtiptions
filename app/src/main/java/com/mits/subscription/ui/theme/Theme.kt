package com.mits.subscription.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Black
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
    onBackground = Black,
    onSurface = PurpleDark,
    onError = White,
    isLight = true
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