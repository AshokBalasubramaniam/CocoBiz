package com.cocobiz.app.presentation.settings

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.domain.model.DarkModeOption
import com.cocobiz.app.ui.components.CocoBizTopBar
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToBackup: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { CocoBizTopBar(title = "Settings") }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Account (section 0, 60ms) ──────────────────────────────
            item {
                AnimatedSettingsSection(animDelay = 60) {
                    SettingsSection(title = "Account") {
                        SettingsNavigationItem(
                            icon = Icons.Default.AccountCircle,
                            title = "Business Profile",
                            subtitle = "Manage your business information",
                            onClick = onNavigateToProfile
                        )
                    }
                }
            }

            // ── Notifications (section 1, 140ms) ─────────────────────
            item {
                AnimatedSettingsSection(animDelay = 140) {
                    SettingsSection(title = "Notifications") {
                        SettingsSwitchItem(
                            icon = Icons.Default.Notifications,
                            title = "Enable Notifications",
                            subtitle = "Receive sale reminders",
                            checked = settings.notificationEnabled,
                            onCheckedChange = viewModel::updateNotificationEnabled
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.padding(horizontal = 8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Reminder Days Before",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "Notify ${settings.reminderDays} days before due",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listOf(3, 5, 7, 10, 14)) { days ->
                                    FilterChip(
                                        selected = settings.reminderDays == days,
                                        onClick = { viewModel.updateReminderDays(days) },
                                        label = { Text("$days days") }
                                    )
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        SettingsSwitchItem(
                            icon = Icons.Default.Email,
                            title = "Email Notifications",
                            subtitle = "Receive completion emails",
                            checked = settings.emailEnabled,
                            onCheckedChange = viewModel::updateEmailEnabled
                        )
                    }
                }
            }

            // ── Appearance (section 2, 220ms) ─────────────────────────
            item {
                AnimatedSettingsSection(animDelay = 220) {
                    SettingsSection(title = "Appearance") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.padding(horizontal = 8.dp))
                            Text(
                                "Theme",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(DarkModeOption.entries) { mode ->
                                FilterChip(
                                    selected = settings.darkMode == mode,
                                    onClick = { viewModel.updateDarkMode(mode) },
                                    label = { Text(mode.displayName) }
                                )
                            }
                        }
                    }
                }
            }

            // ── Data (section 3, 300ms) ───────────────────────────────
            item {
                AnimatedSettingsSection(animDelay = 300) {
                    SettingsSection(title = "Data") {
                        SettingsNavigationItem(
                            icon = Icons.Default.Backup,
                            title = "Backup & Restore",
                            subtitle = "Backup your data locally",
                            onClick = onNavigateToBackup
                        )
                    }
                }
            }

            // ── About (section 4, 380ms) ──────────────────────────────
            item {
                AnimatedSettingsSection(animDelay = 380) {
                    SettingsSection(title = "About") {
                        SettingsInfoItem(
                            icon = Icons.Default.Info,
                            title = "CocoBiz",
                            subtitle = "Version 1.0.0 — Coconut Sales Manager"
                        )
                    }
                }
            }
        }
    }
}

// ── Wrapper that gives each section card a slide-up + fade entrance ───────────

@Composable
private fun AnimatedSettingsSection(animDelay: Int, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(animDelay.toLong()); visible = true }
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "settingsSection"
    )
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.graphicsLayer {
            alpha = progress
            translationY = 20.dp.toPx() * (1f - progress)
        }
    ) {
        content()
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.padding(horizontal = 8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.NavigateNext, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.padding(horizontal = 8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.padding(horizontal = 8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
