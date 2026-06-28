package com.cocobiz.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cocobiz.app.data.local.entity.DealerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DealerDao {
    @Query("SELECT * FROM dealers ORDER BY dealerName ASC")
    fun getAllDealers(): Flow<List<DealerEntity>>

    @Query("SELECT * FROM dealers WHERE id = :id")
    fun getDealerById(id: Long): Flow<DealerEntity?>

    @Query("""
        SELECT * FROM dealers
        WHERE dealerName LIKE '%' || :query || '%'
        OR place LIKE '%' || :query || '%'
        OR phone LIKE '%' || :query || '%'
        ORDER BY dealerName ASC
    """)
    fun searchDealers(query: String): Flow<List<DealerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dealer: DealerEntity): Long

    @Update
    suspend fun update(dealer: DealerEntity)

    @Delete
    suspend fun delete(dealer: DealerEntity)

    @Query("DELETE FROM dealers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM dealers")
    suspend fun getDealerCount(): Int

    @Query("SELECT * FROM dealers ORDER BY id ASC")
    suspend fun getAllSync(): List<DealerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dealers: List<DealerEntity>)

    @Query("DELETE FROM dealers")
    suspend fun deleteAll()
}
