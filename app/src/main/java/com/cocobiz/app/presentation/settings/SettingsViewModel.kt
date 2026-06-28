package com.cocobiz.app.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.domain.model.AppSettings
import com.cocobiz.app.domain.model.DarkModeOption
import com.cocobiz.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    fun updateReminderDays(days: Int) {
        viewModelScope.launch { settingsRepository.updateReminderDays(days) }
    }

    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateNotificationEnabled(enabled) }
    }

    fun updateEmailEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateEmailEnabled(enabled) }
    }

    fun updateDarkMode(mode: DarkModeOption) {
        viewModelScope.launch { settingsRepository.updateDarkMode(mode.name) }
    }

    fun saveSettings(settings: AppSettings) {
        viewModelScope.launch { settingsRepository.saveSettings(settings) }
    }
}
