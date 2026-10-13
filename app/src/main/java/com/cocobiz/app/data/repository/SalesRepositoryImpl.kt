package com.cocobiz.app.data.repository

import com.cocobiz.app.data.local.dao.SalesEntryDao
import com.cocobiz.app.data.local.entity.SalesEntryEntity
import com.cocobiz.app.domain.model.CoconutType
import com.cocobiz.app.domain.model.SaleStatus
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.domain.repository.SalesRepository
import com.cocobiz.app.util.DateUtils.toEpochMilli
import com.cocobiz.app.util.DateUtils.toLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesRepositoryImpl @Inject constructor(
    private val dao: SalesEntryDao
) : SalesRepository {

    override fun getAllSales(): Flow<List<SalesEntry>> =
        dao.getAllSales().map { list -> list.map { it.toDomain() } }

    override fun getActiveSales(): Flow<List<SalesEntry>> =
        dao.getActiveSales().map { list -> list.map { it.toDomain() } }

    override fun getCompletedSales(): Flow<List<SalesEntry>> =
        dao.getCompletedSales().map { list -> list.map { it.toDomain() } }

    override fun getSaleById(id: Long): Flow<SalesEntry?> =
        dao.getSaleById(id).map { it?.toDomain() }

    override fun getSalesByDealer(dealerId: Long): Flow<List<SalesEntry>> =
        dao.getSalesByDealer(dealerId).map { list -> list.map { it.toDomain() } }

    override fun searchSales(query: String): Flow<List<SalesEntry>> =
        dao.searchSales(query).map { list -> list.map { it.toDomain() } }

    override fun getTotalActiveRevenue(): Flow<Double> =
        dao.getTotalActiveRevenue().map { it ?: 0.0 }

    override fun getTotalCompletedRevenue(): Flow<Double> =
        dao.getTotalCompletedRevenue().map { it ?: 0.0 }

    override fun getActiveSalesCount(): Flow<Int> = dao.getActiveSalesCount()

    override fun getCompletedSalesCount(): Flow<Int> = dao.getCompletedSalesCount()

    override fun getUpcomingSales(daysFromNow: Long): Flow<List<SalesEntry>> {
        val threshold = LocalDate.now().plusDays(daysFromNow).toEpochMilli()
        return dao.getUpcomingSales(threshold).map { list -> list.map { it.toDomain() } }
    }

    override fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<SalesEntry>> =
        dao.getSalesByDateRange(startDate, endDate).map { list -> list.map { it.toDomain() } }

    override fun getTotalRevenueByDealer(dealerId: Long): Flow<Double> =
        dao.getTotalRevenueByDealer(dealerId).map { it ?: 0.0 }

    override suspend fun addSale(sale: SalesEntry): Long = dao.insert(sale.toEntity())

    override suspend fun updateSale(sale: SalesEntry) {
        dao.update(sale.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteSale(id: Long) = dao.deleteById(id)

    override suspend fun markAsCompleted(id: Long) =
        dao.markAsCompleted(id, System.currentTimeMillis())

    override suspend fun markAsActive(id: Long) =
        dao.markAsActive(id, System.currentTimeMillis())

    override suspend fun checkAndAutoCompleteSales() {
        val today = LocalDate.now().toEpochMilli()
        val overdue = dao.getOverdueSales(today)
        val timestamp = System.currentTimeMillis()
        overdue.forEach { dao.markAsCompleted(it.id, timestamp) }
    }

    private fun SalesEntryEntity.toDomain(): SalesEntry = SalesEntry(
        id = id, dealerId = dealerId, dealerName = dealerName,
        dealerPlace = dealerPlace, salesDate = salesDate.toLocalDate(),
        nextSalesDate = nextSalesDate.toLocalDate(), quantity = quantity,
        rate = rate, totalAmount = totalAmount,
        coconutType = CoconutType.entries.firstOrNull { it.name == coconutType } ?: CoconutType.TONNAGE,
        cycleDays = cycleDays,
        status = SaleStatus.entries.firstOrNull { it.name == status } ?: SaleStatus.ACTIVE,
        notes = notes, createdAt = createdAt, updatedAt = updatedAt
    )

    private fun SalesEntry.toEntity(): SalesEntryEntity = SalesEntryEntity(
        id = id, dealerId = dealerId, dealerName = dealerName,
        dealerPlace = dealerPlace, salesDate = salesDate.toEpochMilli(),
        nextSalesDate = nextSalesDate.toEpochMilli(), quantity = quantity,
        rate = rate, totalAmount = totalAmount, coconutType = coconutType.name,
        cycleDays = cycleDays, status = status.name, notes = notes,
        createdAt = createdAt, updatedAt = updatedAt
    )
}
