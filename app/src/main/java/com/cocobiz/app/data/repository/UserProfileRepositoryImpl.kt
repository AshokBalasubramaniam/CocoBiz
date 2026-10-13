package com.cocobiz.app.data.repository

import com.cocobiz.app.data.local.dao.UserProfileDao
import com.cocobiz.app.data.local.entity.UserProfileEntity
import com.cocobiz.app.domain.model.UserProfile
import com.cocobiz.app.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao
) : UserProfileRepository {

    override fun getProfile(): Flow<UserProfile?> = dao.getProfile().map { it?.toDomain() }

    override suspend fun saveProfile(profile: UserProfile) {
        dao.insertOrUpdate(profile.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    private fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
        id = id, businessName = businessName, ownerName = ownerName,
        phone = phone, alternatePhone = alternatePhone, email = email,
        address = address, city = city, state = state, pincode = pincode,
        gstNumber = gstNumber, logoPath = logoPath,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
        id = id, businessName = businessName, ownerName = ownerName,
        phone = phone, alternatePhone = alternatePhone, email = email,
        address = address, city = city, state = state, pincode = pincode,
        gstNumber = gstNumber, logoPath = logoPath,
        createdAt = createdAt, updatedAt = updatedAt
    )
}
