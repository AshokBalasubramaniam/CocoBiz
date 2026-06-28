package com.cocobiz.app.domain.repository

import com.cocobiz.app.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun saveSettings(settings: AppSettings)
    suspend fun updateReminderDays(days: Int)
    suspend fun updateNotificationEnabled(enabled: Boolean)
    suspend fun updateEmailEnabled(enabled: Boolean)
    suspend fun updateDarkMode(mode: String)
}
