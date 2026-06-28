package com.cocobiz.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cocobiz.app.data.local.entity.SalesEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesEntryDao {
    @Query("SELECT * FROM sales_entries ORDER BY createdAt DESC")
    fun getAllSales(): Flow<List<SalesEntryEntity>>

    @Query("SELECT * FROM sales_entries WHERE status = 'ACTIVE' ORDER BY nextSalesDate ASC")
    fun getActiveSales(): Flow<List<SalesEntryEntity>>

    @Query("SELECT * FROM sales_entries WHERE status = 'COMPLETED' ORDER BY updatedAt DESC")
    fun getCompletedSales(): Flow<List<SalesEntryEntity>>

    @Query("SELECT * FROM sales_entries WHERE id = :id")
    fun getSaleById(id: Long): Flow<SalesEntryEntity?>

    @Query("SELECT * FROM sales_entries WHERE dealerId = :dealerId ORDER BY createdAt DESC")
    fun getSalesByDealer(dealerId: Long): Flow<List<SalesEntryEntity>>

    @Query("""
        SELECT * FROM sales_entries
        WHERE (dealerName LIKE '%' || :query || '%' OR dealerPlace LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun searchSales(query: String): Flow<List<SalesEntryEntity>>

    @Query("SELECT SUM(totalAmount) FROM sales_entries WHERE status = 'ACTIVE'")
    fun getTotalActiveRevenue(): Flow<Double?>

    @Query("SELECT SUM(totalAmount) FROM sales_entries WHERE status = 'COMPLETED'")
    fun getTotalCompletedRevenue(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM sales_entries WHERE status = 'ACTIVE'")
    fun getActiveSalesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM sales_entries WHERE status = 'COMPLETED'")
    fun getCompletedSalesCount(): Flow<Int>

    @Query("""
        SELECT * FROM sales_entries
        WHERE status = 'ACTIVE'
        AND nextSalesDate <= :daysFromNow
        ORDER BY nextSalesDate ASC
    """)
    fun getUpcomingSales(daysFromNow: Long): Flow<List<SalesEntryEntity>>

    @Query("""
        SELECT * FROM sales_entries
        WHERE status = 'ACTIVE'
        AND nextSalesDate <= :today
    """)
    suspend fun getOverdueSales(today: Long): List<SalesEntryEntity>

    @Query("""
        SELECT * FROM sales_entries
        WHERE salesDate >= :startDate AND salesDate <= :endDate
        ORDER BY salesDate DESC
    """)
    fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<SalesEntryEntity>>

    @Query("SELECT SUM(totalAmount) FROM sales_entries WHERE dealerId = :dealerId")
    fun getTotalRevenueByDealer(dealerId: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sale: SalesEntryEntity): Long

    @Update
    suspend fun update(sale: SalesEntryEntity)

    @Delete
    suspend fun delete(sale: SalesEntryEntity)

    @Query("DELETE FROM sales_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE sales_entries SET status = 'COMPLETED', updatedAt = :timestamp WHERE id = :id")
    suspend fun markAsCompleted(id: Long, timestamp: Long)

    @Query("UPDATE sales_entries SET status = 'ACTIVE', updatedAt = :timestamp WHERE id = :id")
    suspend fun markAsActive(id: Long, timestamp: Long)

    @Query("SELECT * FROM sales_entries WHERE status = 'ACTIVE' AND nextSalesDate <= :threshold")
    suspend fun getSalesDueByThreshold(threshold: Long): List<SalesEntryEntity>

    @Query("SELECT * FROM sales_entries ORDER BY id ASC")
    suspend fun getAllSync(): List<SalesEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sales: List<SalesEntryEntity>)

    @Query("DELETE FROM sales_entries")
    suspend fun deleteAll()
}
