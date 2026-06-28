package com.cocobiz.app.data.repository

import com.cocobiz.app.data.local.dao.DealerDao
import com.cocobiz.app.data.local.entity.DealerEntity
import com.cocobiz.app.data.remote.api.CocoBizApiService
import com.cocobiz.app.data.remote.dto.DealerDto
import com.cocobiz.app.domain.model.Dealer
import com.cocobiz.app.domain.repository.DealerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DealerRepositoryImpl @Inject constructor(
    private val dao: DealerDao,
    private val api: CocoBizApiService
) : DealerRepository {

    private val syncScope = CoroutineScope(Dispatchers.IO)

    override fun getAllDealers(): Flow<List<Dealer>> =
        dao.getAllDealers().map { list -> list.map { it.toDomain() } }

    override fun getDealerById(id: Long): Flow<Dealer?> =
        dao.getDealerById(id).map { it?.toDomain() }

    override fun searchDealers(query: String): Flow<List<Dealer>> =
        dao.searchDealers(query).map { list -> list.map { it.toDomain() } }

    override suspend fun addDealer(dealer: Dealer): Long {
        val id = dao.insert(dealer.toEntity())
        syncScope.launch { runCatching { api.upsertDealer(dealer.copy(id = id).toDto()) } }
        return id
    }

    override suspend fun updateDealer(dealer: Dealer) {
        dao.update(dealer.toEntity())
        syncScope.launch { runCatching { api.updateDealer(dealer.id, dealer.toDto()) } }
    }

    override suspend fun deleteDealer(id: Long) {
        dao.deleteById(id)
        syncScope.launch { runCatching { api.deleteDealer(id) } }
    }

    override suspend fun getDealerCount(): Int = dao.getDealerCount()

    private fun DealerEntity.toDomain(): Dealer = Dealer(
        id = id,
        dealerName = dealerName,
        place = place,
        phone = phone,
        alternatePhone = alternatePhone,
        email = email,
        address = address,
        notes = notes,
        photoPath = photoPath,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun Dealer.toEntity(): DealerEntity = DealerEntity(
        id = id,
        dealerName = dealerName,
        place = place,
        phone = phone,
        alternatePhone = alternatePhone,
        email = email,
        address = address,
        notes = notes,
        photoPath = photoPath,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )

    private fun Dealer.toDto(): DealerDto = DealerDto(
        localId = id,
        dealerName = dealerName,
        place = place,
        phone = phone,
        alternatePhone = alternatePhone,
        email = email,
        address = address,
        notes = notes,
        photoPath = photoPath,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )
}
