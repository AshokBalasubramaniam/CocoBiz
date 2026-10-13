package com.cocobiz.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Exact Tailwind CSS palette values used by the "Duplicate Venum UI Design" Figma Make
 * export for the Add New Sale screen. Kept separate from the app's Material theme colors
 * so this screen can match the Figma reference pixel-for-pixel without perturbing the
 * rest of the app's theming. Every token picks a light/dark variant via [isAppDarkTheme]
 * so screens built against this palette still follow the app's Light/Dark/System setting.
 */
object FigmaColors {
    val White: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E293B) else Color(0xFFFFFFFF)

    val Gray50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF0F172A) else Color(0xFFF9FAFB)
    val Gray100: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1F2937) else Color(0xFFF3F4F6)
    val Gray200: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF374151) else Color(0xFFE5E7EB)
    val Gray300: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4B5563) else Color(0xFFD1D5DB)
    val Gray400: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF9CA3AF) else Color(0xFF9CA3AF)
    val Gray500: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF94A3B8) else Color(0xFF6B7280)
    val Gray600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFCBD5E1) else Color(0xFF4B5563)
    val Gray700: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFE2E8F0) else Color(0xFF374151)
    val Gray900: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFF8FAFC) else Color(0xFF111827)

    val Blue50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E3A5F) else Color(0xFFEFF6FF)
    val Blue100: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF24466E) else Color(0xFFDBEAFE)
    val Blue200: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF2C527E) else Color(0xFFBFDBFE)
    val Blue400: Color @Composable get() = Color(0xFF60A5FA)
    val Blue500: Color @Composable get() = Color(0xFF3B82F6)
    val Blue600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF60A5FA) else Color(0xFF2563EB)
    val Blue700: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF93C5FD) else Color(0xFF1D4ED8)
    val Blue800: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFBFDBFE) else Color(0xFF1E40AF)

    val Green100: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF1E3A2E) else Color(0xFFDCFCE7)
    val Green200: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF24503A) else Color(0xFFBBF7D0)
    val Green600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4ADE80) else Color(0xFF16A34A)
    val Green700: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF86EFAC) else Color(0xFF15803D)
    val Green800: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFBBF7D0) else Color(0xFF166534)

    val Orange50: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4A2E1E) else Color(0xFFFFF7ED)
    val Orange100: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF5C3A24) else Color(0xFFFFEDD5)
    val Orange400: Color @Composable get() = Color(0xFFFB923C)
    val Orange600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFFB923C) else Color(0xFFEA580C)
    val Orange700: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFFDBA74) else Color(0xFFC2410C)

    val Purple100: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF3B2A4A) else Color(0xFFF3E8FF)
    val Purple200: Color @Composable get() = if (isAppDarkTheme()) Color(0xFF4A3560) else Color(0xFFE9D5FF)
    val Purple600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFC084FC) else Color(0xFF9333EA)
    val Purple700: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFD8B4FE) else Color(0xFF7E22CE)
    val Purple900: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFE9D5FF) else Color(0xFF581C87)

    val Red600: Color @Composable get() = if (isAppDarkTheme()) Color(0xFFF87171) else Color(0xFFDC2626)
}
