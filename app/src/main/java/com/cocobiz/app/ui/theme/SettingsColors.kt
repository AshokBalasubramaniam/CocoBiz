package com.cocobiz.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Exact colors from the Settings screen Figma export. Every token picks a light/dark
 * variant via [isAppDarkTheme] so the screen follows the app's Light/Dark/System setting.
 */
object SettingsColors {
    val White: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    val PageBackground: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0F1A12) else Color(0xFFE8F5E9)

    val Gray900: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFF8FAFC) else Color(0xFF111827)
    val Gray500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val Gray400: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF9CA3AF) else Color(0xFF9CA3AF)
    val Gray100: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF334155) else Color(0xFFF3F4F6)

    val Green800: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFBBF7D0) else Color(0xFF166534)
    val Green700: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF86EFAC) else Color(0xFF15803D)
    val Green600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4ADE80) else Color(0xFF16A34A)
    val Green50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF16281C) else Color(0xFFF0FDF4)

    val Blue500: Color @Composable get() = Color(0xFF3B82F6)
    val Blue400: Color @Composable get() = Color(0xFF60A5FA)
    val Blue50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E3A5F) else Color(0xFFEFF6FF)

    val Purple500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFC084FC) else Color(0xFFA855F7)
    val Purple50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF322A4A) else Color(0xFFFAF5FF)

    val Indigo500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF818CF8) else Color(0xFF6366F1)
    val Indigo50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF272C4A) else Color(0xFFEEF2FF)

    val Red500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFF87171) else Color(0xFFEF4444)
    val Red50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF3F1E1E) else Color(0xFFFEF2F2)
}
