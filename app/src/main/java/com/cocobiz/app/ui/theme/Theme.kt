package com.cocobiz.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = CocoBlue40,
    onPrimary = CocoBlue99,
    primaryContainer = CocoBlue90,
    onPrimaryContainer = CocoBlue10,
    secondary = CocoBrown40,
    onSecondary = CocoBrown99,
    secondaryContainer = CocoBrown90,
    onSecondaryContainer = CocoBrown10,
    tertiary = CocoGold40,
    onTertiary = CocoGold99,
    tertiaryContainer = CocoGold90,
    onTertiaryContainer = CocoGold10,
    error = CocoError40,
    onError = CocoError90,
    errorContainer = CocoError90,
    onErrorContainer = CocoError10,
    background = Color(0xFFFFFFFF),
    onBackground = CocoNeutral10,
    surface = Color(0xFFFFFFFF),
    onSurface = CocoNeutral10,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = CocoNeutral30,
    outline = CocoNeutral50,
    outlineVariant = CocoNeutral80
)

private val DarkColorScheme = darkColorScheme(
    primary = CocoBlue80,
    onPrimary = CocoBlue20,
    primaryContainer = CocoBlue30,
    onPrimaryContainer = CocoBlue90,
    secondary = CocoBrown80,
    onSecondary = CocoBrown20,
    secondaryContainer = CocoBrown30,
    onSecondaryContainer = CocoBrown90,
    tertiary = CocoGold80,
    onTertiary = CocoGold20,
    tertiaryContainer = CocoGold30,
    onTertiaryContainer = CocoGold90,
    error = CocoError80,
    onError = CocoError20,
    errorContainer = CocoError30,
    onErrorContainer = CocoError90,
    background = CocoNeutral10,
    onBackground = CocoNeutral90,
    surface = CocoNeutral10,
    onSurface = CocoNeutral90,
    surfaceVariant = CocoNeutral30,
    onSurfaceVariant = CocoNeutral80,
    outline = CocoNeutral60,
    outlineVariant = CocoNeutral30
)

@Composable
fun CocoBizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CocoBizTypography,
        content = content
    )
}
