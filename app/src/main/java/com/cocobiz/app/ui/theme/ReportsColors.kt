package com.cocobiz.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Exact colors from the Reports screen Figma export. Every token picks a light/dark
 * variant via [isAppDarkTheme] so the screen follows the app's Light/Dark/System setting.
 */
object ReportsColors {
    val White: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E293B) else Color(0xFFFFFFFF)

    val PageGradientTop: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0F1A12) else Color(0xFFE8F5E9)
    val PageGradientMid: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0F1512) else Color(0xFFF0FDF4)
    val PageGradientEnd: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0F172A) else Color(0xFFFFFFFF)

    val Slate900: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val Slate500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF94A3B8) else Color(0xFF64748B)
    val Slate400: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF94A3B8) else Color(0xFF94A3B8)
    val Slate300: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF475569) else Color(0xFFD1D5DB)
    val Gray700: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFE2E8F0) else Color(0xFF374151)
    val Gray500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val Gray200: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF374151) else Color(0xFFE5E7EB)

    val Green600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4ADE80) else Color(0xFF16A34A)
    val GreenCardStart: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF16281C) else Color(0xFFF0FDF4)
    val GreenCardEnd: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E3A2E) else Color(0xFFDCFCE7)
    val GreenBorder: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF24503A) else Color(0xFFBBF7D0)

    val StatusActiveBg: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E3A2E) else Color(0xFFDCFCE7)
    val StatusActiveText: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4ADE80) else Color(0xFF16A34A)
    val StatusDoneBg: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E3A5F) else Color(0xFFEFF6FF)
    val StatusDoneText: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF60A5FA) else Color(0xFF2563EB)

    // Avatar background palette (letter itself stays Gray700, which inverts with theme too)
    val AvatarPurple: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF322A4A) else Color(0xFFEDE9FE)
    val AvatarGreen: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1C4033) else Color(0xFFD1FAE5)
    val AvatarOrange: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4A331E) else Color(0xFFFED7AA)
    val AvatarBlue: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF24466E) else Color(0xFFDBEAFE)
    val AvatarRed: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4A1E1E) else Color(0xFFFEE2E2)
}
