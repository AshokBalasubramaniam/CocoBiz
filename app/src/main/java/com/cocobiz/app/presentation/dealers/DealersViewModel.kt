package com.cocobiz.app.presentation.dealers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.domain.model.Dealer
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.domain.repository.DealerRepository
import com.cocobiz.app.domain.repository.SalesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DealerSortType(val label: String) {
    NAME_ASC("Name A-Z"), NAME_DESC("Name Z-A")
}

enum class DealerStatusFilter(val label: String) {
    ALL("All Dealers"), ACTIVE("Active Only"), INACTIVE("Inactive Only")
}

data class DealersUiState(
    val dealers: List<Dealer> = emptyList(),
    val totalCount: Int = 0,
    val activeCount: Int = 0,
    val inactiveCount: Int = 0,
    val searchQuery: String = "",
    val sortType: DealerSortType = DealerSortType.NAME_ASC,
    val statusFilter: DealerStatusFilter = DealerStatusFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DealerFormState(
    val id: Long = 0L,
    val dealerName: String = "",
    val place: String = "",
    val phone: String = "",
    val alternatePhone: String = "",
    val email: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val notes: String = "",
    val photoPath: String = "",
    val isActive: Boolean = true,
    val nameError: String = "",
    val placeError: String = "",
    val phoneError: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val savedEventCount: Int = 0
) {
    val isValid: Boolean
        get() = dealerName.isNotBlank() && place.isNotBlank() && phone.isNotBlank()
}

data class DealerProfileUiState(
    val dealer: Dealer? = null,
    val dealerSales: List<SalesEntry> = emptyList(),
    val totalRevenue: Double = 0.0,
    val activeSalesCount: Int = 0,
    val completedSalesCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DealersViewModel @Inject constructor(
    private val dealerRepository: DealerRepository,
    private val salesRepository: SalesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _sortType = MutableStateFlow(DealerSortType.NAME_ASC)
    private val _statusFilter = MutableStateFlow(DealerStatusFilter.ALL)

    val uiState: StateFlow<DealersUiState> = combine(
        dealerRepository.getAllDealers(),
        _searchQuery,
        _sortType,
        _statusFilter
    ) { dealers, query, sort, statusFilter ->
        val searched = if (query.isBlank()) dealers
        else dealers.filter { d ->
            d.dealerName.contains(query, ignoreCase = true) ||
                    d.place.contains(query, ignoreCase = true) ||
                    d.phone.contains(query)
        }
        val filtered = when (statusFilter) {
            DealerStatusFilter.ALL -> searched
            DealerStatusFilter.ACTIVE -> searched.filter { it.isActive }
            DealerStatusFilter.INACTIVE -> searched.filter { !it.isActive }
        }
        val sorted = when (sort) {
            DealerSortType.NAME_ASC -> filtered.sortedBy { it.dealerName.lowercase() }
            DealerSortType.NAME_DESC -> filtered.sortedByDescending { it.dealerName.lowercase() }
        }
        DealersUiState(
            dealers = sorted,
            totalCount = dealers.size,
            activeCount = dealers.count { it.isActive },
            inactiveCount = dealers.count { !it.isActive },
            searchQuery = query,
            sortType = sort,
            statusFilter = statusFilter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DealersUiState(isLoading = true)
    )

    private val _formState = MutableStateFlow(DealerFormState())
    val formState: StateFlow<DealerFormState> = _formState

    private val _selectedDealerId = MutableStateFlow<Long?>(null)

    val dealerProfileState: StateFlow<DealerProfileUiState> = combine(
        _selectedDealerId,
        dealerRepository.getAllDealers(),
        salesRepository.getAllSales()
    ) { dealerId, dealers, allSales ->
        if (dealerId == null) return@combine DealerProfileUiState()
        val dealer = dealers.firstOrNull { it.id == dealerId }
        val dealerSales = allSales.filter { it.dealerId == dealerId }
        val revenue = dealerSales.sumOf { it.totalAmount }
        DealerProfileUiState(
            dealer = dealer,
            dealerSales = dealerSales,
            totalRevenue = revenue,
            activeSalesCount = dealerSales.count { it.status.name == "ACTIVE" },
            completedSalesCount = dealerSales.count { it.status.name == "COMPLETED" },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DealerProfileUiState()
    )

    fun loadDealerProfile(dealerId: Long) {
        _selectedDealerId.value = dealerId
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSort(sort: DealerSortType) {
        _sortType.value = sort
    }

    fun updateStatusFilter(filter: DealerStatusFilter) {
        _statusFilter.value = filter
    }

    fun updateFormField(field: String, value: String) {
        _formState.value = when (field) {
            "name" -> _formState.value.copy(dealerName = value, nameError = "")
            "place" -> _formState.value.copy(place = value, placeError = "")
            "phone" -> _formState.value.copy(phone = value, phoneError = "")
            "altPhone" -> _formState.value.copy(alternatePhone = value)
            "email" -> _formState.value.copy(email = value)
            "address" -> _formState.value.copy(address = value)
            "city" -> _formState.value.copy(city = value)
            "state" -> _formState.value.copy(state = value)
            "pincode" -> _formState.value.copy(pincode = value)
            "notes" -> _formState.value.copy(notes = value)
            else -> _formState.value
        }
    }

    fun updateIsActive(isActive: Boolean) {
        _formState.value = _formState.value.copy(isActive = isActive)
    }

    fun loadDealerForEdit(dealerId: Long) {
        viewModelScope.launch {
            dealerRepository.getDealerById(dealerId).collect { dealer ->
                dealer?.let {
                    _formState.value = DealerFormState(
                        id = it.id,
                        dealerName = it.dealerName,
                        place = it.place,
                        phone = it.phone,
                        alternatePhone = it.alternatePhone,
                        email = it.email,
                        address = it.address,
                        city = it.city,
                        state = it.state,
                        pincode = it.pincode,
                        notes = it.notes,
                        photoPath = it.photoPath,
                        isActive = it.isActive,
                        savedEventCount = _formState.value.savedEventCount
                    )
                }
            }
        }
    }

    fun saveDealer() {
        val state = _formState.value
        var hasError = false

        if (state.dealerName.isBlank()) {
            _formState.value = _formState.value.copy(nameError = "Dealer name is required")
            hasError = true
        }
        if (state.place.isBlank()) {
            _formState.value = _formState.value.copy(placeError = "Place is required")
            hasError = true
        }
        if (state.phone.isBlank()) {
            _formState.value = _formState.value.copy(phoneError = "Phone number is required")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSaving = true)
            val dealer = Dealer(
                id = state.id,
                dealerName = state.dealerName.trim(),
                place = state.place.trim(),
                phone = state.phone.trim(),
                alternatePhone = state.alternatePhone.trim(),
                email = state.email.trim(),
                address = state.address.trim(),
                city = state.city.trim(),
                state = state.state.trim(),
                pincode = state.pincode.trim(),
                notes = state.notes.trim(),
                photoPath = state.photoPath,
                isActive = state.isActive
            )
            if (state.id == 0L) {
                dealerRepository.addDealer(dealer)
            } else {
                dealerRepository.updateDealer(dealer)
            }
            _formState.value = _formState.value.copy(
                isSaving = false,
                isSaved = true,
                savedEventCount = _formState.value.savedEventCount + 1
            )
        }
    }

    fun deleteDealer(id: Long) {
        viewModelScope.launch {
            dealerRepository.deleteDealer(id)
        }
    }

    fun resetForm() {
        _formState.value = DealerFormState()
    }
}
