package com.cocobiz.app.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.R
import com.cocobiz.app.domain.model.DarkModeOption
import com.cocobiz.app.ui.components.ConfirmationDialog
import com.cocobiz.app.ui.theme.SettingsColors

@Composable
fun SettingsScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val reminder by viewModel.reminder.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        ConfirmationDialog(
            title = "Logout",
            message = "Are you sure you want to logout?",
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
                onLogout()
            },
            onDismiss = { showLogoutDialog = false },
            confirmText = "Logout",
            confirmButtonColor = MaterialTheme.colorScheme.error
        )
    }

    Scaffold(
        containerColor = SettingsColors.PageBackground,
        // The outer NavGraph Scaffold already reserves the status-bar/nav-bar insets.
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SettingsHeader() }

            item {
                ProfileCard(
                    name = profile?.ownerName?.ifBlank { null } ?: "Your Profile",
                    subtitle = listOfNotNull(
                        profile?.businessName?.ifBlank { null } ?: "Business Owner",
                        profile?.city?.ifBlank { null }
                    ).joinToString(" • "),
                    phone = profile?.phone.orEmpty(),
                    onClick = onNavigateToProfile,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                SettingsSectionCard(
                    title = "Account",
                    subtitle = "Manage your account",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    SettingsNavRow(
                        icon = Icons.Default.AccountCircle,
                        iconBackground = SettingsColors.Green50,
                        iconTint = SettingsColors.Green700,
                        title = "Business Profile",
                        subtitle = "View & edit your business information",
                        onClick = onNavigateToProfile
                    )
                    SettingsNavRow(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        iconBackground = SettingsColors.Red50,
                        iconTint = SettingsColors.Red500,
                        title = "Logout",
                        titleColor = SettingsColors.Red500,
                        subtitle = "Sign out of your account",
                        onClick = { showLogoutDialog = true }
                    )
                }
            }

            item {
                SettingsSectionCard(
                    title = "Notifications",
                    subtitle = "Stay updated always",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    SettingsSwitchRow(
                        icon = Icons.Default.Notifications,
                        iconBackground = SettingsColors.Green50,
                        iconTint = SettingsColors.Green700,
                        title = "Enable Notifications",
                        subtitle = "Receive sale reminders and important updates",
                        checked = settings.notificationEnabled,
                        onCheckedChange = viewModel::updateNotificationEnabled
                    )
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconTile(icon = Icons.Default.Timer, background = SettingsColors.Blue50, tint = SettingsColors.Blue500)
                            Column {
                                Text("Reminder Days Before", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SettingsColors.Gray900)
                                Text("Notify before the selected number of days", fontSize = 11.sp, color = SettingsColors.Gray400)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(3, 5, 7, 10).forEach { d ->
                                DayPill(
                                    label = "$d days",
                                    selected = settings.reminderDays == d,
                                    onClick = { viewModel.updateReminderDays(d) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                SettingsSectionCard(
                    title = "Cloud Reminders",
                    subtitle = "Choose how you get notified",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconTile(icon = Icons.Default.Cloud, background = SettingsColors.Blue50, tint = SettingsColors.Blue400)
                        Column {
                            Text("Reminder Channel", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SettingsColors.Gray900)
                            Text("How you receive harvesting reminders", fontSize = 11.sp, color = SettingsColors.Gray400)
                        }
                    }
                    ChannelRow(
                        icon = Icons.Default.Sms,
                        iconBackground = SettingsColors.Green50,
                        iconTint = SettingsColors.Green600,
                        title = "WhatsApp",
                        subtitle = "Get notified via WhatsApp",
                        selected = reminder.channel == "WHATSAPP",
                        onClick = { viewModel.selectAndSaveReminderChannel("WHATSAPP") }
                    )
                    ChannelRow(
                        icon = Icons.Default.Email,
                        iconBackground = SettingsColors.Blue50,
                        iconTint = SettingsColors.Blue500,
                        title = "Email",
                        subtitle = "Get notified via Email",
                        selected = reminder.channel == "EMAIL",
                        onClick = { viewModel.selectAndSaveReminderChannel("EMAIL") }
                    )
                    ChannelRow(
                        icon = Icons.Default.Sms,
                        iconBackground = SettingsColors.Purple50,
                        iconTint = SettingsColors.Purple500,
                        title = "SMS",
                        subtitle = "Get notified via SMS",
                        selected = reminder.channel == "SMS",
                        onClick = { viewModel.selectAndSaveReminderChannel("SMS") }
                    )
                }
            }

            item {
                SettingsSectionCard(
                    title = "App Preferences",
                    subtitle = "Customize your experience",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    SettingsSwitchRow(
                        icon = Icons.Default.DarkMode,
                        iconBackground = SettingsColors.Indigo50,
                        iconTint = SettingsColors.Indigo500,
                        title = "Dark Mode",
                        subtitle = "Switch between light and dark theme",
                        checked = settings.darkMode == DarkModeOption.DARK,
                        onCheckedChange = { viewModel.updateDarkMode(if (it) DarkModeOption.DARK else DarkModeOption.LIGHT) }
                    )
                    SettingsNavRow(
                        icon = Icons.Default.Backup,
                        iconBackground = SettingsColors.Blue50,
                        iconTint = SettingsColors.Blue500,
                        title = "Backup & Restore",
                        subtitle = "Backup data and sync to cloud",
                        onClick = onNavigateToBackup
                    )
                }
            }

            item {
                Text(
                    "CocoBiz — Version 1.0.0",
                    fontSize = 12.sp,
                    color = SettingsColors.Gray400,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SettingsHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.width(200.dp)) {
            Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = SettingsColors.Gray900)
            Text(
                "Manage your account and\napp preferences",
                fontSize = 13.sp,
                color = SettingsColors.Gray500,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Text(
            "Grow\nTogether",
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            color = SettingsColors.Green800,
            lineHeight = 15.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 4.dp, end = 36.dp)
        )
        Image(
            painter = painterResource(R.drawable.ic_dashboard_hero),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(width = 110.dp, height = 90.dp)
        )
    }
}

@Composable
private fun ProfileCard(
    name: String,
    subtitle: String,
    phone: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SettingsColors.White,
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SettingsColors.Green800),
                contentAlignment = Alignment.Center
            ) {
                Text(name.take(1).uppercase(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SettingsColors.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SettingsColors.Gray900)
                Text(subtitle, fontSize = 13.sp, color = SettingsColors.Gray500)
                if (phone.isNotBlank()) {
                    Text(phone, fontSize = 13.sp, color = SettingsColors.Gray500)
                }
            }
            Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null, tint = SettingsColors.Gray400, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SettingsColors.White,
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SettingsColors.Green800)
                Text(subtitle, fontSize = 11.sp, color = SettingsColors.Gray400)
            }
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun IconTile(icon: ImageVector, background: Color, tint: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: Color = SettingsColors.Gray900
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconTile(icon, iconBackground, iconTint)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = titleColor)
            Text(subtitle, fontSize = 11.sp, color = SettingsColors.Gray400)
        }
        Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null, tint = SettingsColors.Gray400, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconTile(icon, iconBackground, iconTint)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SettingsColors.Gray900)
            Text(subtitle, fontSize = 11.sp, color = SettingsColors.Gray400)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = SettingsColors.Green600)
        )
    }
}

@Composable
private fun ChannelRow(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconTile(icon, iconBackground, iconTint)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SettingsColors.Gray900)
            Text(subtitle, fontSize = 11.sp, color = SettingsColors.Gray400)
        }
        RadioDot(selected = selected)
    }
}

@Composable
private fun RadioDot(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(2.dp, if (selected) SettingsColors.Green700 else SettingsColors.Gray100, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(SettingsColors.Green700)
            )
        }
    }
}

@Composable
private fun DayPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) SettingsColors.Green700 else SettingsColors.White,
        border = BorderStroke(1.dp, if (selected) SettingsColors.Green700 else SettingsColors.Gray100),
        modifier = modifier.clip(RoundedCornerShape(50)).clickable(onClick = onClick)
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) SettingsColors.White else SettingsColors.Gray500,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp)
        )
    }
}
