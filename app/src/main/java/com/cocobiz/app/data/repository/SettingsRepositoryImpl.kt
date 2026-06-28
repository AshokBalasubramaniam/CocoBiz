package com.cocobiz.app.data.repository

import com.cocobiz.app.data.local.dao.SettingsDao
import com.cocobiz.app.data.local.entity.SettingsEntity
import com.cocobiz.app.data.remote.api.CocoBizApiService
import com.cocobiz.app.data.remote.dto.SettingsDto
import com.cocobiz.app.domain.model.AppSettings
import com.cocobiz.app.domain.model.DarkModeOption
import com.cocobiz.app.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dao: SettingsDao,
    private val api: CocoBizApiService
) : SettingsRepository {

    private val syncScope = CoroutineScope(Dispatchers.IO)

    override fun getSettings(): Flow<AppSettings> =
        dao.getSettings().map { it?.toDomain() ?: AppSettings() }

    override suspend fun saveSettings(settings: AppSettings) {
        dao.insertOrUpdate(settings.toEntity())
        syncScope.launch { runCatching { api.saveSettings(settings.toDto()) } }
    }

    override suspend fun updateReminderDays(days: Int) {
        val entity = getOrCreate().copy(reminderDays = days)
        dao.insertOrUpdate(entity)
        syncScope.launch { runCatching { api.saveSettings(entity.toDomain().toDto()) } }
    }

    override suspend fun updateNotificationEnabled(enabled: Boolean) {
        val entity = getOrCreate().copy(notificationEnabled = enabled)
        dao.insertOrUpdate(entity)
        syncScope.launch { runCatching { api.saveSettings(entity.toDomain().toDto()) } }
    }

    override suspend fun updateEmailEnabled(enabled: Boolean) {
        val entity = getOrCreate().copy(emailEnabled = enabled)
        dao.insertOrUpdate(entity)
        syncScope.launch { runCatching { api.saveSettings(entity.toDomain().toDto()) } }
    }

    override suspend fun updateDarkMode(mode: String) {
        val entity = getOrCreate().copy(darkMode = mode)
        dao.insertOrUpdate(entity)
        syncScope.launch { runCatching { api.saveSettings(entity.toDomain().toDto()) } }
    }

    private suspend fun getOrCreate(): SettingsEntity =
        dao.getSettingsSync() ?: SettingsEntity().also { dao.insertOrUpdate(it) }

    private fun SettingsEntity.toDomain(): AppSettings = AppSettings(
        id = id,
        reminderDays = reminderDays,
        notificationEnabled = notificationEnabled,
        emailEnabled = emailEnabled,
        senderEmail = senderEmail,
        darkMode = DarkModeOption.entries.firstOrNull { it.name == darkMode } ?: DarkModeOption.SYSTEM,
        backupEnabled = backupEnabled,
        defaultCycleDays = defaultCycleDays
    )

    private fun AppSettings.toEntity(): SettingsEntity = SettingsEntity(
        id = id,
        reminderDays = reminderDays,
        notificationEnabled = notificationEnabled,
        emailEnabled = emailEnabled,
        senderEmail = senderEmail,
        darkMode = darkMode.name,
        backupEnabled = backupEnabled,
        defaultCycleDays = defaultCycleDays
    )

    private fun AppSettings.toDto(): SettingsDto = SettingsDto(
        localId = id,
        reminderDays = reminderDays,
        notificationEnabled = notificationEnabled,
        emailEnabled = emailEnabled,
        senderEmail = senderEmail,
        darkMode = darkMode.name,
        backupEnabled = backupEnabled,
        defaultCycleDays = defaultCycleDays,
        updatedAt = System.currentTimeMillis()
    )
}
