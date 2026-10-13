package com.cocobiz.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotUiState(
    val identifier: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotUiState())
    val state: StateFlow<ForgotUiState> = _state.asStateFlow()

    fun onIdentifierChange(v: String) = _state.update { it.copy(identifier = v, error = null) }
    fun onNewPasswordChange(v: String) = _state.update { it.copy(newPassword = v, error = null) }
    fun onConfirmPasswordChange(v: String) = _state.update { it.copy(confirmPassword = v, error = null) }

    fun resetPassword(onSuccess: () -> Unit) {
        val s = _state.value
        when {
            s.identifier.isBlank() -> { _state.update { it.copy(error = "Enter your username") }; return }
            s.newPassword.length < 6 -> { _state.update { it.copy(error = "New password must be at least 6 characters") }; return }
            s.newPassword != s.confirmPassword -> { _state.update { it.copy(error = "Passwords don't match") }; return }
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.resetPassword(s.identifier.trim().lowercase(), s.newPassword)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
                onSuccess()
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.toUserMessage()) }
            }
        }
    }
}
