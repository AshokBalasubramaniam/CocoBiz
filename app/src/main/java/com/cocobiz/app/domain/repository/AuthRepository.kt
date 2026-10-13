package com.cocobiz.app.domain.repository

data class LocalUser(
    val username: String,
    val email: String = "",
    val phone: String = "",
    val businessName: String = "",
    val ownerName: String = "",
    val reminderChannel: String = "EMAIL",
    val reminderFrequency: String = "DAILY"
)

interface AuthRepository {
    suspend fun register(
        username: String,
        password: String,
        email: String,
        phone: String,
        businessName: String,
        ownerName: String
    ): LocalUser

    suspend fun login(username: String, password: String): LocalUser

    suspend fun resetPassword(username: String, newPassword: String)

    suspend fun updateReminderPrefs(channel: String, frequency: String)
}
