package com.cocobiz.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.data.remote.api.AuthApiService
import com.cocobiz.app.data.remote.dto.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val email: String = "",
    val phone: String = "",
    val businessName: String = "",
    val ownerName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authApi: AuthApiService
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onUsernameChange(v: String) = _state.update { it.copy(username = v, error = null) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v, error = null) }
    fun onConfirmPasswordChange(v: String) = _state.update { it.copy(confirmPassword = v, error = null) }
    fun onEmailChange(v: String) = _state.update { it.copy(email = v, error = null) }
    fun onPhoneChange(v: String) = _state.update { it.copy(phone = v, error = null) }
    fun onBusinessNameChange(v: String) = _state.update { it.copy(businessName = v, error = null) }
    fun onOwnerNameChange(v: String) = _state.update { it.copy(ownerName = v, error = null) }

    fun register() {
        val s = _state.value
        when {
            s.username.isBlank() -> { _state.update { it.copy(error = "Username is required") }; return }
            s.username.length < 3 -> { _state.update { it.copy(error = "Username must be at least 3 characters") }; return }
            s.password.length < 6 -> { _state.update { it.copy(error = "Password must be at least 6 characters") }; return }
            s.password != s.confirmPassword -> { _state.update { it.copy(error = "Passwords don't match") }; return }
            s.businessName.isBlank() -> { _state.update { it.copy(error = "Business name is required") }; return }
            s.phone.isBlank() -> { _state.update { it.copy(error = "Phone number is required") }; return }
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authApi.register(
                    RegisterRequest(
                        username = s.username.trim().lowercase(),
                        password = s.password,
                        email = s.email.trim(),
                        phone = s.phone.trim(),
                        businessName = s.businessName.trim(),
                        ownerName = s.ownerName.trim()
                    )
                )
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.toUserMessage()) }
            }
        }
    }
}
