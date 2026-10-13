package com.cocobiz.app.presentation.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.domain.repository.SalesRepository
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
    val totalQuantity: Double = 0.0,
    val averageOrder: Double = 0.0,
    val trendPercent: Double? = null,
    val sparklineValues: List<Double> = emptyList(),
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
        // Filtering by the sale's actual business date (salesDate), not the row's
        // createdAt timestamp - a sale entered days after it happened must still count
        // toward the day/week/month/year it was actually sold in, not the entry date.
        val now = LocalDate.now()
        val filtered = when (reportType) {
            ReportType.DAILY -> allSales.filter { it.salesDate == now }
            ReportType.WEEKLY -> {
                val weekStart = now.minusDays(7)
                allSales.filter { it.salesDate in weekStart..now }
            }
            ReportType.MONTHLY -> allSales.filter {
                it.salesDate.year == year && it.salesDate.monthValue == month
            }
            ReportType.YEARLY -> allSales.filter { it.salesDate.year == year }
        }
        // Last 8 calendar months of revenue (oldest first), for the trend sparkline
        // and the "vs last month" comparison - independent of the selected filter.
        val monthlyRevenue = (7 downTo 0).map { offset ->
            val monthDate = LocalDate.now().minusMonths(offset.toLong())
            allSales
                .filter { it.salesDate.year == monthDate.year && it.salesDate.monthValue == monthDate.monthValue }
                .sumOf { it.totalAmount }
        }
        val thisMonthRevenue = monthlyRevenue.last()
        val lastMonthRevenue = monthlyRevenue[monthlyRevenue.size - 2]
        val trend = if (lastMonthRevenue > 0) {
            ((thisMonthRevenue - lastMonthRevenue) / lastMonthRevenue) * 100
        } else null

        ReportsUiState(
            sales = filtered,
            totalRevenue = filtered.sumOf { it.totalAmount },
            totalQuantity = filtered.sumOf { it.quantity },
            averageOrder = if (filtered.isNotEmpty()) filtered.sumOf { it.totalAmount } / filtered.size else 0.0,
            trendPercent = trend,
            sparklineValues = monthlyRevenue,
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
