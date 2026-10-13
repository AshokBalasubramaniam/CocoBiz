package com.cocobiz.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Exact colors from the "Analyze and Develop UI" Figma Make export used for the
 * redesigned Dashboard screen. Kept separate from the app's Material theme so this
 * screen can match the Figma reference pixel-for-pixel. Every token picks a light/dark
 * variant via [isAppDarkTheme] so the screen follows the app's Light/Dark/System setting.
 */
object DashboardColors {
    val ShellTop: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0D1F14) else Color(0xFFD4EDDA)
    val ShellMid: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0F1A12) else Color(0xFFE8F5E9)
    val ShellBottom: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0F1512) else Color(0xFFF0FAF0)

    val BrandGreen: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4ADE80) else Color(0xFF1A6B3A)
    val BrandGreenTitle: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF86EFAC) else Color(0xFF0F4C25)
    val BannerGradientStart: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF14532D) else Color(0xFF1A6B3A)
    val BannerGradientEnd: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E7A45) else Color(0xFF2D9B5A)

    val GreenLight: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E3A2E) else Color(0xFFE8F5E9)
    val OrangeLight: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4A2E1E) else Color(0xFFFFF3E0)
    val BlueLight: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E3A5F) else Color(0xFFDBEAFE)
    val PurpleLight: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF322A4A) else Color(0xFFEDE9FE)

    val Orange: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFFB923C) else Color(0xFFF5760A)
    val Blue: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF60A5FA) else Color(0xFF3B82F6)
    val Purple: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFA78BFA) else Color(0xFF8B5CF6)

    val Gray900: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFF8FAFC) else Color(0xFF111827)
    val Gray700: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFE2E8F0) else Color(0xFF374151)
    val Gray600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFCBD5E1) else Color(0xFF4B5563)
    val Gray500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val Gray400: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF9CA3AF) else Color(0xFF9CA3AF)
    val Gray200: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF374151) else Color(0xFFE5E7EB)
    val White: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    val Red500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFF87171) else Color(0xFFEF4444)
    val RedLight: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4A1E1E) else Color(0xFFFEE2E2)
}
