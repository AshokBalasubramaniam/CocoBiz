package com.cocobiz.app.data.repository

import com.cocobiz.app.data.remote.api.CocoBizApiService
import com.cocobiz.app.data.remote.dto.SettingsDto
import com.cocobiz.app.domain.model.AppSettings
import com.cocobiz.app.domain.model.DarkModeOption
import com.cocobiz.app.domain.repository.SettingsRepository
import com.cocobiz.app.util.NetworkConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val api: CocoBizApiService,
    private val network: NetworkConnectivityObserver
) : SettingsRepository {

    private val _settings = MutableStateFlow(AppSettings())
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            network.onConnected.collect { refresh() }
        }
    }

    private suspend fun refresh() {
        runCatching { _settings.value = api.getSettings().toDomain() }
    }

    override fun getSettings(): Flow<AppSettings> = _settings.asStateFlow()

    override suspend fun saveSettings(settings: AppSettings) {
        runCatching { api.saveSettings(settings.toDto()) }
        _settings.value = settings
    }

    override suspend fun updateReminderDays(days: Int) {
        val updated = _settings.value.copy(reminderDays = days)
        runCatching { api.saveSettings(updated.toDto()) }
        _settings.value = updated
    }

    override suspend fun updateNotificationEnabled(enabled: Boolean) {
        val updated = _settings.value.copy(notificationEnabled = enabled)
        runCatching { api.saveSettings(updated.toDto()) }
        _settings.value = updated
    }

    override suspend fun updateEmailEnabled(enabled: Boolean) {
        val updated = _settings.value.copy(emailEnabled = enabled)
        runCatching { api.saveSettings(updated.toDto()) }
        _settings.value = updated
    }

    override suspend fun updateDarkMode(mode: String) {
        val updated = _settings.value.copy(
            darkMode = DarkModeOption.entries.firstOrNull { it.name == mode } ?: DarkModeOption.SYSTEM
        )
        runCatching { api.saveSettings(updated.toDto()) }
        _settings.value = updated
    }

    private fun SettingsDto.toDomain(): AppSettings = AppSettings(
        id = localId, reminderDays = reminderDays,
        notificationEnabled = notificationEnabled, emailEnabled = emailEnabled,
        senderEmail = senderEmail,
        darkMode = DarkModeOption.entries.firstOrNull { it.name == darkMode } ?: DarkModeOption.SYSTEM,
        backupEnabled = backupEnabled, defaultCycleDays = defaultCycleDays
    )

    private fun AppSettings.toDto(): SettingsDto = SettingsDto(
        localId = id, reminderDays = reminderDays,
        notificationEnabled = notificationEnabled, emailEnabled = emailEnabled,
        senderEmail = senderEmail, darkMode = darkMode.name,
        backupEnabled = backupEnabled, defaultCycleDays = defaultCycleDays,
        updatedAt = System.currentTimeMillis()
    )
}
