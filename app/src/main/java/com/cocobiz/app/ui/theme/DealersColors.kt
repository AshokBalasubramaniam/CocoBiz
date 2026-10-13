package com.cocobiz.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Exact colors from the "Manage Your Dealers" Figma Make export, kept separate
 * from the app's Material theme so this screen can match the reference exactly.
 * Every token picks a light/dark variant via [isAppDarkTheme] so the screen follows
 * the app's Light/Dark/System setting.
 */
object DealersColors {
    val White: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E293B) else Color(0xFFFFFFFF)

    val Gray400: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF9CA3AF) else Color(0xFF9CA3AF)
    val Gray500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val Gray600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFCBD5E1) else Color(0xFF4B5563)
    val Gray700: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFE2E8F0) else Color(0xFF374151)
    val Gray900: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFF8FAFC) else Color(0xFF111827)

    val Emerald50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF16332A) else Color(0xFFECFDF5)
    val Emerald100: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1C4033) else Color(0xFFD1FAE5)
    val Emerald400: Color @Composable get() = Color(0xFF34D399)
    val Emerald500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF34D399) else Color(0xFF10B981)
    val Emerald600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF10B981) else Color(0xFF059669)
    val Emerald700: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF6EE7B7) else Color(0xFF047857)

    val Green50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF16281C) else Color(0xFFF0FDF4)
    val Green100: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E3A2E) else Color(0xFFDCFCE7)

    val Blue50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E3A5F) else Color(0xFFEFF6FF)
    val Blue100: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF24466E) else Color(0xFFDBEAFE)
    val Blue400: Color @Composable get() = Color(0xFF60A5FA)
    val Blue500: Color @Composable get() = Color(0xFF3B82F6)
    val Blue600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF60A5FA) else Color(0xFF2563EB)

    val Orange50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4A2E1E) else Color(0xFFFFF7ED)
    val Orange100: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF5C3A24) else Color(0xFFFFEDD5)
    val Orange400: Color @Composable get() = Color(0xFFFB923C)
    val Orange500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFFB923C) else Color(0xFFF97316)

    val Purple400: Color @Composable get() = Color(0xFFC084FC)
    val Purple600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFC084FC) else Color(0xFF9333EA)

    val Red50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF3F1E1E) else Color(0xFFFEF2F2)
    val Red400: Color @Composable get() = Color(0xFFF87171)

    val HeaderGradientTop: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0D1F14) else Color(0xFFD4EDDA)
    val HeaderGradientMid: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0F1A12) else Color(0xFFE8F5E9)
    val HeaderGradientEnd: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0C1810) else Color(0xFFC8E6C9)

    val FabGradientStart: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF2D9B6F) else Color(0xFF2D9B6F)
    val FabGradientEnd: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF14532D) else Color(0xFF1A7A52)
}
