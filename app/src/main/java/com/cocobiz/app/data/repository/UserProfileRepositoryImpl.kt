package com.cocobiz.app.data.repository

import com.cocobiz.app.data.local.dao.UserProfileDao
import com.cocobiz.app.data.local.entity.UserProfileEntity
import com.cocobiz.app.data.remote.api.CocoBizApiService
import com.cocobiz.app.data.remote.dto.UserProfileDto
import com.cocobiz.app.domain.model.UserProfile
import com.cocobiz.app.domain.repository.UserProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao,
    private val api: CocoBizApiService
) : UserProfileRepository {

    private val syncScope = CoroutineScope(Dispatchers.IO)

    override fun getProfile(): Flow<UserProfile?> =
        dao.getProfile().map { it?.toDomain() }

    override suspend fun saveProfile(profile: UserProfile) {
        dao.insertOrUpdate(profile.toEntity())
        syncScope.launch { runCatching { api.saveProfile(profile.toDto()) } }
    }

    private fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
        id = id,
        businessName = businessName,
        ownerName = ownerName,
        phone = phone,
        alternatePhone = alternatePhone,
        email = email,
        address = address,
        city = city,
        state = state,
        pincode = pincode,
        gstNumber = gstNumber,
        logoPath = logoPath,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
        id = id,
        businessName = businessName,
        ownerName = ownerName,
        phone = phone,
        alternatePhone = alternatePhone,
        email = email,
        address = address,
        city = city,
        state = state,
        pincode = pincode,
        gstNumber = gstNumber,
        logoPath = logoPath,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )

    private fun UserProfile.toDto(): UserProfileDto = UserProfileDto(
        localId = id,
        businessName = businessName,
        ownerName = ownerName,
        phone = phone,
        alternatePhone = alternatePhone,
        email = email,
        address = address,
        city = city,
        state = state,
        pincode = pincode,
        gstNumber = gstNumber,
        logoPath = logoPath,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )
}
