package com.cocobiz.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocobiz.app.domain.model.AppSettings
import com.cocobiz.app.domain.model.DashboardStats
import com.cocobiz.app.domain.model.SaleStatus
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.domain.repository.SalesRepository
import com.cocobiz.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

enum class ViewMode { NORMAL, NEXT_HARVESTING, YEAR_REPORT }

data class DashboardUiState(
    val activeSales: List<SalesEntry> = emptyList(),
    val completedSales: List<SalesEntry> = emptyList(),
    val yearSales: List<SalesEntry> = emptyList(),
    val stats: DashboardStats = DashboardStats(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val filterType: FilterType = FilterType.ALL,
    val sortType: SortType = SortType.NEWEST,
    val viewMode: ViewMode = ViewMode.NORMAL,
    val settings: AppSettings = AppSettings()
)

enum class FilterType(val label: String) {
    ALL("All"), ACTIVE("Active"), COMPLETED("Completed")
}

enum class SortType(val label: String) {
    NEWEST("Newest"), OLDEST("Oldest"),
    HIGHEST_AMOUNT("Highest Amount"), LOWEST_AMOUNT("Lowest Amount"),
    NEAREST_DUE("Nearest Due")
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val salesRepository: SalesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterType = MutableStateFlow(FilterType.ALL)
    private val _sortType = MutableStateFlow(SortType.NEWEST)
    private val _viewMode = MutableStateFlow(ViewMode.NORMAL)

    val uiState: StateFlow<DashboardUiState> = combine(
        salesRepository.getAllSales(),
        salesRepository.getActiveSalesCount(),
        salesRepository.getCompletedSalesCount(),
        salesRepository.getTotalActiveRevenue(),
        salesRepository.getUpcomingSales(7L),
        _searchQuery,
        _filterType,
        _sortType,
        _viewMode,
        settingsRepository.getSettings()
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        val allSales = args[0] as List<SalesEntry>
        val activeCount = args[1] as Int
        val completedCount = args[2] as Int
        val totalRevenue = args[3] as Double
        val upcoming = args[4] as List<SalesEntry>
        val query = args[5] as String
        val filter = args[6] as FilterType
        val sort = args[7] as SortType
        val mode = args[8] as ViewMode
        val settings = args[9] as AppSettings

        val oneYearAgo = LocalDate.now().minusYears(1)
        val yearSales = allSales
            .filter { it.salesDate >= oneYearAgo }
            .sortedByDescending { it.salesDate }

        val filtered = allSales
            .filter { sale ->
                if (query.isBlank()) true
                else sale.dealerName.contains(query, ignoreCase = true) ||
                        sale.dealerPlace.contains(query, ignoreCase = true)
            }
            .filter { sale ->
                when (filter) {
                    FilterType.ALL -> true
                    FilterType.ACTIVE -> sale.status == SaleStatus.ACTIVE
                    FilterType.COMPLETED -> sale.status == SaleStatus.COMPLETED
                }
            }
            .let { list ->
                when (sort) {
                    SortType.NEWEST -> list.sortedByDescending { it.createdAt }
                    SortType.OLDEST -> list.sortedBy { it.createdAt }
                    SortType.HIGHEST_AMOUNT -> list.sortedByDescending { it.totalAmount }
                    SortType.LOWEST_AMOUNT -> list.sortedBy { it.totalAmount }
                    SortType.NEAREST_DUE -> list.sortedBy { it.nextSalesDate }
                }
            }

        val activeSales = when (mode) {
            ViewMode.NEXT_HARVESTING ->
                allSales.filter { it.status == SaleStatus.ACTIVE }
                    .sortedBy { it.nextSalesDate }
            else -> filtered.filter { it.status == SaleStatus.ACTIVE }
        }

        val completedSales = when (mode) {
            ViewMode.NEXT_HARVESTING -> emptyList()
            else -> filtered.filter { it.status == SaleStatus.COMPLETED }
        }

        DashboardUiState(
            activeSales = activeSales,
            completedSales = completedSales,
            yearSales = yearSales,
            stats = DashboardStats(
                totalActiveSales = activeCount,
                totalCompletedSales = completedCount,
                totalRevenue = totalRevenue,
                upcomingReminders = upcoming.size
            ),
            searchQuery = query,
            filterType = filter,
            sortType = sort,
            viewMode = mode,
            settings = settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateFilter(filter: FilterType) { _filterType.value = filter }
    fun updateSort(sort: SortType) { _sortType.value = sort }
    fun setViewMode(mode: ViewMode) { _viewMode.value = mode }

    fun deleteSale(id: Long) {
        viewModelScope.launch { salesRepository.deleteSale(id) }
    }

    fun completeSale(id: Long) {
        viewModelScope.launch { salesRepository.markAsCompleted(id) }
    }
}
