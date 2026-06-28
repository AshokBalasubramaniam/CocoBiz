package com.cocobiz.app.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.util.BackupManager
import com.cocobiz.app.util.BackupResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BackupUiState(
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false,
    val isResetting: Boolean = false,
    val resetComplete: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)

@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState

    fun backup() {
        if (_uiState.value.isBackingUp) return
        viewModelScope.launch {
            _uiState.value = BackupUiState(isBackingUp = true)
            when (val result = backupManager.backup()) {
                is BackupResult.Success -> _uiState.value = BackupUiState(
                    message = result.details,
                    isError = false
                )
                is BackupResult.Error -> _uiState.value = BackupUiState(
                    message = result.message,
                    isError = true
                )
            }
        }
    }

    fun restore(uri: Uri) {
        if (_uiState.value.isRestoring) return
        viewModelScope.launch {
            _uiState.value = BackupUiState(isRestoring = true)
            when (val result = backupManager.restore(uri)) {
                is BackupResult.Success -> _uiState.value = BackupUiState(
                    message = result.details,
                    isError = false
                )
                is BackupResult.Error -> _uiState.value = BackupUiState(
                    message = result.message,
                    isError = true
                )
            }
        }
    }

    fun resetAllData() {
        if (_uiState.value.isResetting) return
        viewModelScope.launch {
            _uiState.value = BackupUiState(isResetting = true)
            when (val result = backupManager.resetAllData()) {
                is BackupResult.Success -> _uiState.value = BackupUiState(
                    resetComplete = true,
                    message = result.details,
                    isError = false
                )
                is BackupResult.Error -> _uiState.value = BackupUiState(
                    message = result.message,
                    isError = true
                )
            }
        }
    }

    fun showAuthError(message: String) {
        _uiState.value = BackupUiState(message = message, isError = true)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
