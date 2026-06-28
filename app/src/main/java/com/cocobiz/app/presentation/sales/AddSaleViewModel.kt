package com.cocobiz.app.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.domain.model.CoconutType
import com.cocobiz.app.domain.model.Dealer
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.domain.repository.DealerRepository
import com.cocobiz.app.domain.repository.SalesRepository
import com.cocobiz.app.domain.repository.SettingsRepository
import com.cocobiz.app.util.DateUtils.calculateNextSalesDate
import com.cocobiz.app.util.DateUtils.toEpochMilli
import com.cocobiz.app.util.DateUtils.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class SaleFormState(
    val id: Long = 0L,
    val selectedDealerId: Long = 0L,
    val dealerName: String = "",
    val dealerPlace: String = "",
    val dealerPhone: String = "",
    val salesDate: LocalDate = LocalDate.now(),
    val nextSalesDate: LocalDate = LocalDate.now().plusDays(60),
    val coconutType: CoconutType = CoconutType.TONNAGE,
    val quantity: String = "",
    val rate: String = "",
    val totalAmount: Double = 0.0,
    val cycleDays: Int = 60,
    val notes: String = "",
    val dealers: List<Dealer> = emptyList(),
    val dealerNameError: String = "",
    val quantityError: String = "",
    val rateError: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val showDatePicker: Boolean = false
) {
    val isValid: Boolean
        get() = dealerName.isNotBlank() && quantity.isNotBlank() && rate.isNotBlank() &&
                quantity.toDoubleOrNull() != null && rate.toDoubleOrNull() != null &&
                (quantity.toDoubleOrNull() ?: 0.0) > 0 && (rate.toDoubleOrNull() ?: 0.0) > 0
}

@HiltViewModel
class AddSaleViewModel @Inject constructor(
    private val salesRepository: SalesRepository,
    private val dealerRepository: DealerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(SaleFormState())
    val formState: StateFlow<SaleFormState> = _formState

    init {
        loadDealersAndSettings()
    }

    private fun loadDealersAndSettings() {
        viewModelScope.launch {
            val settings = settingsRepository.getSettings().first()
            val dealers = dealerRepository.getAllDealers().first()
            _formState.value = _formState.value.copy(
                cycleDays = settings.defaultCycleDays,
                nextSalesDate = LocalDate.now().plusDays(settings.defaultCycleDays.toLong()),
                dealers = dealers
            )
        }
    }

    fun loadSaleForEdit(saleId: Long) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            val sale = salesRepository.getSaleById(saleId).first()
            sale?.let {
                _formState.value = _formState.value.copy(
                    id = it.id,
                    selectedDealerId = it.dealerId,
                    dealerName = it.dealerName,
                    dealerPlace = it.dealerPlace,
                    salesDate = it.salesDate,
                    nextSalesDate = it.nextSalesDate,
                    coconutType = it.coconutType,
                    quantity = it.quantity.toFormattedString(),
                    rate = it.rate.toString(),
                    totalAmount = it.totalAmount,
                    cycleDays = it.cycleDays,
                    notes = it.notes,
                    isEditMode = true,
                    isLoading = false
                )
            } ?: run {
                _formState.value = _formState.value.copy(isLoading = false)
            }
        }
    }

    fun selectDealer(dealer: Dealer) {
        _formState.value = _formState.value.copy(
            selectedDealerId = dealer.id,
            dealerName = dealer.dealerName,
            dealerPlace = dealer.place,
            dealerPhone = dealer.phone,
            dealerNameError = ""
        )
    }

    fun updateDealerName(name: String) {
        _formState.value = _formState.value.copy(
            dealerName = name,
            selectedDealerId = 0L,
            dealerNameError = ""
        )
    }

    fun updateDealerPlace(place: String) {
        _formState.value = _formState.value.copy(dealerPlace = place)
    }

    fun updateCoconutType(type: CoconutType) {
        _formState.value = _formState.value.copy(coconutType = type)
        recalculate()
    }

    fun updateQuantity(value: String) {
        _formState.value = _formState.value.copy(quantity = value, quantityError = "")
        recalculate()
    }

    fun updateRate(value: String) {
        _formState.value = _formState.value.copy(rate = value, rateError = "")
        recalculate()
    }

    fun updateSalesDate(date: LocalDate) {
        val nextDate = calculateNextSalesDate(date, _formState.value.cycleDays)
        _formState.value = _formState.value.copy(
            salesDate = date,
            nextSalesDate = nextDate,
            showDatePicker = false
        )
    }

    fun updateCycleDays(days: Int) {
        val nextDate = calculateNextSalesDate(_formState.value.salesDate, days)
        _formState.value = _formState.value.copy(
            cycleDays = days,
            nextSalesDate = nextDate
        )
    }

    fun updateNotes(notes: String) {
        _formState.value = _formState.value.copy(notes = notes)
    }

    fun toggleDatePicker(show: Boolean) {
        _formState.value = _formState.value.copy(showDatePicker = show)
    }

    private fun recalculate() {
        val qty = _formState.value.quantity.toDoubleOrNull() ?: 0.0
        val rate = _formState.value.rate.toDoubleOrNull() ?: 0.0
        _formState.value = _formState.value.copy(totalAmount = qty * rate)
    }

    fun saveSale() {
        val state = _formState.value
        var hasError = false

        if (state.dealerName.isBlank()) {
            _formState.value = _formState.value.copy(dealerNameError = "Dealer name is required")
            hasError = true
        }
        val qty = state.quantity.toDoubleOrNull()
        if (qty == null || qty <= 0) {
            _formState.value = _formState.value.copy(quantityError = "Valid quantity required")
            hasError = true
        }
        val rate = state.rate.toDoubleOrNull()
        if (rate == null || rate <= 0) {
            _formState.value = _formState.value.copy(rateError = "Valid rate required")
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSaving = true)
            val sale = SalesEntry(
                id = state.id,
                dealerId = state.selectedDealerId,
                dealerName = state.dealerName.trim(),
                dealerPlace = state.dealerPlace.trim(),
                salesDate = state.salesDate,
                nextSalesDate = state.nextSalesDate,
                quantity = qty!!,
                rate = rate!!,
                totalAmount = qty * rate,
                coconutType = state.coconutType,
                cycleDays = state.cycleDays,
                notes = state.notes.trim()
            )
            if (state.isEditMode) {
                salesRepository.updateSale(sale)
            } else {
                salesRepository.addSale(sale)
            }
            _formState.value = _formState.value.copy(isSaving = false, isSaved = true)
        }
    }

    private fun Double.toFormattedString(): String =
        if (this == kotlin.math.floor(this)) "%.0f".format(this) else "%.2f".format(this)
}
