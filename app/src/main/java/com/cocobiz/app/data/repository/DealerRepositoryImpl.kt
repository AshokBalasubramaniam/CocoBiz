package com.cocobiz.app.data.repository

import com.cocobiz.app.data.remote.api.CocoBizApiService
import com.cocobiz.app.data.remote.dto.DealerDto
import com.cocobiz.app.domain.model.Dealer
import com.cocobiz.app.domain.repository.DealerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DealerRepositoryImpl @Inject constructor(
    private val api: CocoBizApiService
) : DealerRepository {

    private val _dealers = MutableStateFlow<List<Dealer>>(emptyList())
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch { refresh() }
    }

    private suspend fun refresh() {
        runCatching { _dealers.value = api.getAllDealers().map { it.toDomain() } }
    }

    override fun getAllDealers(): Flow<List<Dealer>> = _dealers.asStateFlow()

    override fun getDealerById(id: Long): Flow<Dealer?> =
        _dealers.map { it.firstOrNull { d -> d.id == id } }

    override fun searchDealers(query: String): Flow<List<Dealer>> =
        _dealers.map { list ->
            if (query.isBlank()) list
            else list.filter {
                it.dealerName.contains(query, ignoreCase = true) ||
                it.place.contains(query, ignoreCase = true) ||
                it.phone.contains(query, ignoreCase = true)
            }
        }

    override suspend fun addDealer(dealer: Dealer): Long {
        val id = if (dealer.id == 0L) System.currentTimeMillis() else dealer.id
        val newDealer = dealer.copy(id = id)
        runCatching { api.upsertDealer(newDealer.toDto()) }
        _dealers.value = _dealers.value + newDealer
        return id
    }

    override suspend fun updateDealer(dealer: Dealer) {
        runCatching { api.updateDealer(dealer.id, dealer.toDto()) }
        _dealers.value = _dealers.value.map { if (it.id == dealer.id) dealer else it }
    }

    override suspend fun deleteDealer(id: Long) {
        runCatching { api.deleteDealer(id) }
        _dealers.value = _dealers.value.filter { it.id != id }
    }

    override suspend fun getDealerCount(): Int = _dealers.value.size

    private fun DealerDto.toDomain(): Dealer = Dealer(
        id = localId, dealerName = dealerName, place = place,
        phone = phone, alternatePhone = alternatePhone, email = email,
        address = address, notes = notes, photoPath = photoPath,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun Dealer.toDto(): DealerDto = DealerDto(
        localId = id, dealerName = dealerName, place = place,
        phone = phone, alternatePhone = alternatePhone, email = email,
        address = address, notes = notes, photoPath = photoPath,
        createdAt = createdAt, updatedAt = System.currentTimeMillis()
    )
}
