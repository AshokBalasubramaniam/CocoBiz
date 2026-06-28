package com.cocobiz.app.domain.repository

import com.cocobiz.app.domain.model.Dealer
import kotlinx.coroutines.flow.Flow

interface DealerRepository {
    fun getAllDealers(): Flow<List<Dealer>>
    fun getDealerById(id: Long): Flow<Dealer?>
    fun searchDealers(query: String): Flow<List<Dealer>>
    suspend fun addDealer(dealer: Dealer): Long
    suspend fun updateDealer(dealer: Dealer)
    suspend fun deleteDealer(id: Long)
    suspend fun getDealerCount(): Int
}
