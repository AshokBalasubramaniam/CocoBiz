package com.cocobiz.app.presentation.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.domain.repository.SalesRepository
import com.cocobiz.app.util.DateUtils.getEndOfMonth
import com.cocobiz.app.util.DateUtils.getEndOfYear
import com.cocobiz.app.util.DateUtils.getStartOfMonth
import com.cocobiz.app.util.DateUtils.getStartOfYear
import com.cocobiz.app.util.PdfExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

enum class ReportType(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}

data class ReportsUiState(
    val sales: List<SalesEntry> = emptyList(),
    val totalRevenue: Double = 0.0,
    val reportType: ReportType = ReportType.MONTHLY,
    val selectedMonth: Int = LocalDate.now().monthValue,
    val selectedYear: Int = LocalDate.now().year,
    val isLoading: Boolean = false,
    val exportState: ExportState = ExportState.IDLE
)

enum class ExportState { IDLE, EXPORTING, SUCCESS, FAILED }

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val salesRepository: SalesRepository
) : ViewModel() {

    private val _reportType = MutableStateFlow(ReportType.MONTHLY)
    private val _selectedMonth = MutableStateFlow(LocalDate.now().monthValue)
    private val _selectedYear = MutableStateFlow(LocalDate.now().year)
    private val _exportState = MutableStateFlow(ExportState.IDLE)

    val uiState: StateFlow<ReportsUiState> = combine(
        _reportType,
        _selectedMonth,
        _selectedYear,
        salesRepository.getAllSales(),
        _exportState
    ) { reportType, month, year, allSales, exportState ->
        val now = LocalDate.now()
        val filtered = when (reportType) {
            ReportType.DAILY -> {
                val dayStart = com.cocobiz.app.util.DateUtils.getStartOfDay(now)
                val dayEnd = com.cocobiz.app.util.DateUtils.getEndOfDay(now)
                allSales.filter { it.createdAt in dayStart..dayEnd }
            }
            ReportType.WEEKLY -> {
                val weekStart = now.minusDays(7)
                val start = com.cocobiz.app.util.DateUtils.getStartOfDay(weekStart)
                val end = com.cocobiz.app.util.DateUtils.getEndOfDay(now)
                allSales.filter { it.createdAt in start..end }
            }
            ReportType.MONTHLY -> {
                val monthDate = LocalDate.of(year, month, 1)
                allSales.filter {
                    it.createdAt in getStartOfMonth(monthDate)..getEndOfMonth(monthDate)
                }
            }
            ReportType.YEARLY -> {
                allSales.filter {
                    it.createdAt in getStartOfYear(year)..getEndOfYear(year)
                }
            }
        }
        ReportsUiState(
            sales = filtered,
            totalRevenue = filtered.sumOf { it.totalAmount },
            reportType = reportType,
            selectedMonth = month,
            selectedYear = year,
            exportState = exportState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReportsUiState(isLoading = true)
    )

    fun updateReportType(type: ReportType) { _reportType.value = type }
    fun updateMonth(month: Int) { _selectedMonth.value = month }
    fun updateYear(year: Int) { _selectedYear.value = year }

    fun exportPdf(context: Context) {
        val currentState = uiState.value
        if (currentState.exportState == ExportState.EXPORTING) return
        viewModelScope.launch(Dispatchers.IO) {
            _exportState.value = ExportState.EXPORTING
            val success = PdfExporter.exportReportToPdf(
                context = context,
                sales = currentState.sales,
                totalRevenue = currentState.totalRevenue,
                reportTitle = currentState.reportType.displayName
            )
            _exportState.value = if (success) ExportState.SUCCESS else ExportState.FAILED
        }
    }

    fun resetExportState() {
        _exportState.value = ExportState.IDLE
    }
}
