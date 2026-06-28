package com.cocobiz.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.data.local.AuthPreferences
import com.cocobiz.app.data.remote.api.AuthApiService
import com.cocobiz.app.data.remote.dto.UpdateReminderRequest
import com.cocobiz.app.domain.model.AppSettings
import com.cocobiz.app.domain.model.DarkModeOption
import com.cocobiz.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReminderState(
    val channel: String = "EMAIL",
    val frequency: String = "DAILY",
    val isSaving: Boolean = false,
    val savedOk: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authApi: AuthApiService,
    private val authPrefs: AuthPreferences
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    private val _reminder = MutableStateFlow(
        ReminderState(
            channel = authPrefs.reminderChannel,
            frequency = authPrefs.reminderFrequency
        )
    )
    val reminder: StateFlow<ReminderState> = _reminder.asStateFlow()

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

    fun setReminderChannel(channel: String) {
        _reminder.value = _reminder.value.copy(channel = channel, savedOk = false)
    }

    fun setReminderFrequency(frequency: String) {
        _reminder.value = _reminder.value.copy(frequency = frequency, savedOk = false)
    }

    fun saveReminder() {
        val r = _reminder.value
        viewModelScope.launch {
            _reminder.value = r.copy(isSaving = true, savedOk = false)
            runCatching {
                authApi.updateReminder(UpdateReminderRequest(r.channel, r.frequency))
                authPrefs.reminderChannel = r.channel
                authPrefs.reminderFrequency = r.frequency
            }
            _reminder.value = _reminder.value.copy(isSaving = false, savedOk = true)
        }
    }

    fun logout() {
        authPrefs.clear()
    }
}
