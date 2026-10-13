package com.cocobiz.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance

/**
 * Whether the app is currently rendering in dark mode - respects the user's explicit
 * Settings choice (Light/Dark/System), not just the raw OS setting, since it reads the
 * already-resolved [MaterialTheme] color scheme that [com.cocobiz.app.ui.theme.CocoBizTheme]
 * picked. Every per-screen "Figma export" color object (FigmaColors, DashboardColors,
 * DealersColors, ReportsColors, SettingsColors) uses this to pick a light/dark variant
 * for each token, so screens built against those fixed palettes still follow the app's
 * theme instead of staying permanently light.
 */
@Composable
fun isAppDarkTheme(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
