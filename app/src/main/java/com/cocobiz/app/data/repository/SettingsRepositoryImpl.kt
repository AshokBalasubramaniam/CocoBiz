package com.cocobiz.app.data.repository

import com.cocobiz.app.data.local.dao.SettingsDao
import com.cocobiz.app.data.local.entity.SettingsEntity
import com.cocobiz.app.domain.model.AppSettings
import com.cocobiz.app.domain.model.DarkModeOption
import com.cocobiz.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dao: SettingsDao
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> =
        dao.getSettings().map { it?.toDomain() ?: AppSettings() }

    override suspend fun saveSettings(settings: AppSettings) {
        dao.insertOrUpdate(settings.toEntity())
    }

    override suspend fun updateReminderDays(days: Int) {
        dao.insertOrUpdate(currentOrDefault().copy(reminderDays = days).toEntity())
    }

    override suspend fun updateNotificationEnabled(enabled: Boolean) {
        dao.insertOrUpdate(currentOrDefault().copy(notificationEnabled = enabled).toEntity())
    }

    override suspend fun updateEmailEnabled(enabled: Boolean) {
        dao.insertOrUpdate(currentOrDefault().copy(emailEnabled = enabled).toEntity())
    }

    override suspend fun updateDarkMode(mode: String) {
        dao.insertOrUpdate(
            currentOrDefault().copy(
                darkMode = DarkModeOption.entries.firstOrNull { it.name == mode } ?: DarkModeOption.SYSTEM
            ).toEntity()
        )
    }

    private suspend fun currentOrDefault(): AppSettings =
        dao.getSettingsSync()?.toDomain() ?: AppSettings()

    private fun SettingsEntity.toDomain(): AppSettings = AppSettings(
        id = id, reminderDays = reminderDays,
        notificationEnabled = notificationEnabled, emailEnabled = emailEnabled,
        senderEmail = senderEmail,
        darkMode = DarkModeOption.entries.firstOrNull { it.name == darkMode } ?: DarkModeOption.SYSTEM,
        backupEnabled = backupEnabled, defaultCycleDays = defaultCycleDays
    )

    private fun AppSettings.toEntity(): SettingsEntity = SettingsEntity(
        id = id, reminderDays = reminderDays,
        notificationEnabled = notificationEnabled, emailEnabled = emailEnabled,
        senderEmail = senderEmail, darkMode = darkMode.name,
        backupEnabled = backupEnabled, defaultCycleDays = defaultCycleDays
    )
}
