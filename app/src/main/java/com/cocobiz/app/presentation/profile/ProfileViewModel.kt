package com.cocobiz.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.domain.model.UserProfile
import com.cocobiz.app.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val businessName: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val alternatePhone: String = "",
    val email: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val gstNumber: String = "",
    val logoPath: String = "",
    val businessNameError: String = "",
    val ownerNameError: String = "",
    val phoneError: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isLoaded: Boolean = false,
    // Incremented on each successful save; never reset by loadProfile().
    // LaunchedEffect(savedEventCount) in the UI fires reliably even when the
    // repository Flow re-emits and resets isSaved within the same Compose frame.
    val savedEventCount: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            repository.getProfile().collect { profile ->
                profile?.let {
                    _uiState.value = ProfileUiState(
                        businessName = it.businessName,
                        ownerName = it.ownerName,
                        phone = it.phone,
                        alternatePhone = it.alternatePhone,
                        email = it.email,
                        address = it.address,
                        city = it.city,
                        state = it.state,
                        pincode = it.pincode,
                        gstNumber = it.gstNumber,
                        logoPath = it.logoPath,
                        isLoaded = true,
                        savedEventCount = _uiState.value.savedEventCount
                    )
                } ?: run {
                    _uiState.value = _uiState.value.copy(isLoaded = true)
                }
            }
        }
    }

    fun updateField(field: String, value: String) {
        _uiState.value = when (field) {
            "businessName" -> _uiState.value.copy(businessName = value, businessNameError = "")
            "ownerName" -> _uiState.value.copy(ownerName = value, ownerNameError = "")
            "phone" -> _uiState.value.copy(phone = value, phoneError = "")
            "alternatePhone" -> _uiState.value.copy(alternatePhone = value)
            "email" -> _uiState.value.copy(email = value)
            "address" -> _uiState.value.copy(address = value)
            "city" -> _uiState.value.copy(city = value)
            "state" -> _uiState.value.copy(state = value)
            "pincode" -> _uiState.value.copy(pincode = value)
            "gstNumber" -> _uiState.value.copy(gstNumber = value)
            else -> _uiState.value
        }
    }

    fun saveProfile() {
        val state = _uiState.value
        var hasError = false

        if (state.businessName.isBlank()) {
            _uiState.value = _uiState.value.copy(businessNameError = "Business name is required")
            hasError = true
        }
        if (state.ownerName.isBlank()) {
            _uiState.value = _uiState.value.copy(ownerNameError = "Owner name is required")
            hasError = true
        }
        if (state.phone.isBlank()) {
            _uiState.value = _uiState.value.copy(phoneError = "Phone number is required")
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            repository.saveProfile(
                UserProfile(
                    businessName = state.businessName.trim(),
                    ownerName = state.ownerName.trim(),
                    phone = state.phone.trim(),
                    alternatePhone = state.alternatePhone.trim(),
                    email = state.email.trim(),
                    address = state.address.trim(),
                    city = state.city.trim(),
                    state = state.state.trim(),
                    pincode = state.pincode.trim(),
                    gstNumber = state.gstNumber.trim(),
                    logoPath = state.logoPath
                )
            )
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                isSaved = true,
                savedEventCount = _uiState.value.savedEventCount + 1
            )
        }
    }
}
