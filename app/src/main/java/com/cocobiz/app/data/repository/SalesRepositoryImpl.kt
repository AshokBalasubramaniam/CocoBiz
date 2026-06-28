package com.cocobiz.app.data.repository

import com.cocobiz.app.data.remote.api.CocoBizApiService
import com.cocobiz.app.data.remote.dto.SalesEntryDto
import com.cocobiz.app.domain.model.CoconutType
import com.cocobiz.app.domain.model.SaleStatus
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.domain.repository.SalesRepository
import com.cocobiz.app.util.DateUtils.toEpochMilli
import com.cocobiz.app.util.DateUtils.toLocalDate
import com.cocobiz.app.util.NetworkConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesRepositoryImpl @Inject constructor(
    private val api: CocoBizApiService,
    private val network: NetworkConnectivityObserver
) : SalesRepository {

    private val _allSales = MutableStateFlow<List<SalesEntry>>(emptyList())
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            network.onConnected.collect { refresh() }
        }
    }

    private suspend fun refresh() {
        runCatching { _allSales.value = api.getAllSales().map { it.toDomain() } }
    }

    override fun getAllSales(): Flow<List<SalesEntry>> = _allSales.asStateFlow()

    override fun getActiveSales(): Flow<List<SalesEntry>> =
        _allSales.map { it.filter { s -> s.status == SaleStatus.ACTIVE } }

    override fun getCompletedSales(): Flow<List<SalesEntry>> =
        _allSales.map { it.filter { s -> s.status == SaleStatus.COMPLETED } }

    override fun getSaleById(id: Long): Flow<SalesEntry?> =
        _allSales.map { it.firstOrNull { s -> s.id == id } }

    override fun getSalesByDealer(dealerId: Long): Flow<List<SalesEntry>> =
        _allSales.map { it.filter { s -> s.dealerId == dealerId } }

    override fun searchSales(query: String): Flow<List<SalesEntry>> =
        _allSales.map { list ->
            if (query.isBlank()) list
            else list.filter {
                it.dealerName.contains(query, ignoreCase = true) ||
                it.dealerPlace.contains(query, ignoreCase = true)
            }
        }

    override fun getTotalActiveRevenue(): Flow<Double> =
        _allSales.map { it.filter { s -> s.status == SaleStatus.ACTIVE }.sumOf { s -> s.totalAmount } }

    override fun getTotalCompletedRevenue(): Flow<Double> =
        _allSales.map { it.filter { s -> s.status == SaleStatus.COMPLETED }.sumOf { s -> s.totalAmount } }

    override fun getActiveSalesCount(): Flow<Int> =
        _allSales.map { it.count { s -> s.status == SaleStatus.ACTIVE } }

    override fun getCompletedSalesCount(): Flow<Int> =
        _allSales.map { it.count { s -> s.status == SaleStatus.COMPLETED } }

    override fun getUpcomingSales(daysFromNow: Long): Flow<List<SalesEntry>> {
        val threshold = LocalDate.now().plusDays(daysFromNow)
        return _allSales.map { list ->
            list.filter { it.status == SaleStatus.ACTIVE && !it.nextSalesDate.isAfter(threshold) }
        }
    }

    override fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<SalesEntry>> =
        _allSales.map { list ->
            list.filter { sale -> sale.salesDate.toEpochMilli() in startDate..endDate }
        }

    override fun getTotalRevenueByDealer(dealerId: Long): Flow<Double> =
        _allSales.map { it.filter { s -> s.dealerId == dealerId }.sumOf { s -> s.totalAmount } }

    override suspend fun addSale(sale: SalesEntry): Long {
        val id = if (sale.id == 0L) System.currentTimeMillis() else sale.id
        val newSale = sale.copy(id = id)
        runCatching { api.upsertSale(newSale.toDto()) }
        _allSales.value = _allSales.value + newSale
        return id
    }

    override suspend fun updateSale(sale: SalesEntry) {
        runCatching { api.updateSale(sale.id, sale.toDto()) }
        _allSales.value = _allSales.value.map { if (it.id == sale.id) sale else it }
    }

    override suspend fun deleteSale(id: Long) {
        runCatching { api.deleteSale(id) }
        _allSales.value = _allSales.value.filter { it.id != id }
    }

    override suspend fun markAsCompleted(id: Long) {
        runCatching { api.markSaleCompleted(id) }
        _allSales.value = _allSales.value.map {
            if (it.id == id) it.copy(status = SaleStatus.COMPLETED) else it
        }
    }

    override suspend fun markAsActive(id: Long) {
        runCatching { api.markSaleActive(id) }
        _allSales.value = _allSales.value.map {
            if (it.id == id) it.copy(status = SaleStatus.ACTIVE) else it
        }
    }

    override suspend fun checkAndAutoCompleteSales() {
        val today = LocalDate.now()
        val overdue = _allSales.value.filter {
            it.status == SaleStatus.ACTIVE && !it.nextSalesDate.isAfter(today)
        }
        overdue.forEach { runCatching { api.markSaleCompleted(it.id) } }
        if (overdue.isNotEmpty()) {
            _allSales.value = _allSales.value.map { sale ->
                if (overdue.any { it.id == sale.id }) sale.copy(status = SaleStatus.COMPLETED) else sale
            }
        }
    }

    private fun SalesEntryDto.toDomain(): SalesEntry = SalesEntry(
        id = localId, dealerId = dealerId, dealerName = dealerName,
        dealerPlace = dealerPlace, salesDate = salesDate.toLocalDate(),
        nextSalesDate = nextSalesDate.toLocalDate(), quantity = quantity,
        rate = rate, totalAmount = totalAmount,
        coconutType = CoconutType.entries.firstOrNull { it.name == coconutType } ?: CoconutType.TONNAGE,
        cycleDays = cycleDays,
        status = SaleStatus.entries.firstOrNull { it.name == status } ?: SaleStatus.ACTIVE,
        notes = notes, createdAt = createdAt, updatedAt = updatedAt
    )

    private fun SalesEntry.toDto(): SalesEntryDto = SalesEntryDto(
        localId = id, dealerId = dealerId, dealerName = dealerName,
        dealerPlace = dealerPlace, salesDate = salesDate.toEpochMilli(),
        nextSalesDate = nextSalesDate.toEpochMilli(), quantity = quantity,
        rate = rate, totalAmount = totalAmount, coconutType = coconutType.name,
        cycleDays = cycleDays, status = status.name, notes = notes,
        createdAt = createdAt, updatedAt = System.currentTimeMillis()
    )
}
