package com.cocobiz.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.data.remote.api.AuthApiService
import com.cocobiz.app.data.remote.dto.SendOtpRequest
import com.cocobiz.app.data.remote.dto.VerifyOtpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ForgotStep { SEND_OTP, VERIFY_OTP, SUCCESS }

data class ForgotUiState(
    val identifier: String = "",
    val otp: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val step: ForgotStep = ForgotStep.SEND_OTP,
    val maskedContact: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authApi: AuthApiService
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotUiState())
    val state: StateFlow<ForgotUiState> = _state.asStateFlow()

    fun onIdentifierChange(v: String) = _state.update { it.copy(identifier = v, error = null) }
    fun onOtpChange(v: String) = _state.update { it.copy(otp = v, error = null) }
    fun onNewPasswordChange(v: String) = _state.update { it.copy(newPassword = v, error = null) }
    fun onConfirmPasswordChange(v: String) = _state.update { it.copy(confirmPassword = v, error = null) }

    fun sendOtp() {
        val s = _state.value
        if (s.identifier.isBlank()) {
            _state.update { it.copy(error = "Enter your username, email or phone number") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val resp = authApi.sendOtp(SendOtpRequest(s.identifier.trim()))
                _state.update { it.copy(isLoading = false, step = ForgotStep.VERIFY_OTP, maskedContact = resp.masked) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.toUserMessage()) }
            }
        }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        val s = _state.value
        when {
            s.otp.isBlank() -> { _state.update { it.copy(error = "Enter the OTP sent to your contact") }; return }
            s.newPassword.length < 6 -> { _state.update { it.copy(error = "New password must be at least 6 characters") }; return }
            s.newPassword != s.confirmPassword -> { _state.update { it.copy(error = "Passwords don't match") }; return }
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authApi.verifyOtp(VerifyOtpRequest(s.identifier.trim(), s.otp.trim(), s.newPassword))
                _state.update { it.copy(isLoading = false, step = ForgotStep.SUCCESS) }
                onSuccess()
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.toUserMessage()) }
            }
        }
    }
}
