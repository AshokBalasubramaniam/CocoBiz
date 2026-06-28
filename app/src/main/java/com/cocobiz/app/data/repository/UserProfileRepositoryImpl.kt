package com.cocobiz.app.data.repository

import com.cocobiz.app.data.remote.api.CocoBizApiService
import com.cocobiz.app.data.remote.dto.UserProfileDto
import com.cocobiz.app.domain.model.UserProfile
import com.cocobiz.app.domain.repository.UserProfileRepository
import com.cocobiz.app.util.NetworkConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val api: CocoBizApiService,
    private val network: NetworkConnectivityObserver
) : UserProfileRepository {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            network.isConnected.collect { connected ->
                if (connected) refresh()
            }
        }
    }

    private suspend fun refresh() {
        runCatching { _profile.value = api.getProfile().toDomain() }
    }

    override fun getProfile(): Flow<UserProfile?> = _profile.asStateFlow()

    override suspend fun saveProfile(profile: UserProfile) {
        runCatching { api.saveProfile(profile.toDto()) }
        _profile.value = profile
    }

    private fun UserProfileDto.toDomain(): UserProfile = UserProfile(
        id = localId, businessName = businessName, ownerName = ownerName,
        phone = phone, alternatePhone = alternatePhone, email = email,
        address = address, city = city, state = state, pincode = pincode,
        gstNumber = gstNumber, logoPath = logoPath,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun UserProfile.toDto(): UserProfileDto = UserProfileDto(
        localId = id, businessName = businessName, ownerName = ownerName,
        phone = phone, alternatePhone = alternatePhone, email = email,
        address = address, city = city, state = state, pincode = pincode,
        gstNumber = gstNumber, logoPath = logoPath,
        createdAt = createdAt, updatedAt = System.currentTimeMillis()
    )
}
