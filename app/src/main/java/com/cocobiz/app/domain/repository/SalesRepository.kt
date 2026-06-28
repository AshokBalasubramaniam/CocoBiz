package com.cocobiz.app.domain.repository

import com.cocobiz.app.domain.model.SalesEntry
import kotlinx.coroutines.flow.Flow

interface SalesRepository {
    fun getAllSales(): Flow<List<SalesEntry>>
    fun getActiveSales(): Flow<List<SalesEntry>>
    fun getCompletedSales(): Flow<List<SalesEntry>>
    fun getSaleById(id: Long): Flow<SalesEntry?>
    fun getSalesByDealer(dealerId: Long): Flow<List<SalesEntry>>
    fun searchSales(query: String): Flow<List<SalesEntry>>
    fun getTotalActiveRevenue(): Flow<Double>
    fun getTotalCompletedRevenue(): Flow<Double>
    fun getActiveSalesCount(): Flow<Int>
    fun getCompletedSalesCount(): Flow<Int>
    fun getUpcomingSales(daysFromNow: Long): Flow<List<SalesEntry>>
    fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<SalesEntry>>
    fun getTotalRevenueByDealer(dealerId: Long): Flow<Double>
    suspend fun addSale(sale: SalesEntry): Long
    suspend fun updateSale(sale: SalesEntry)
    suspend fun deleteSale(id: Long)
    suspend fun markAsCompleted(id: Long)
    suspend fun markAsActive(id: Long)
    suspend fun checkAndAutoCompleteSales()
}
