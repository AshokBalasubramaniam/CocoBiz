package com.cocobiz.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_user")
data class UserEntity(
    @PrimaryKey val id: Long = 1L,
    val username: String,
    val passwordHash: String,
    val passwordSalt: String,
    val email: String = "",
    val phone: String = "",
    val businessName: String = "",
    val ownerName: String = "",
    val reminderChannel: String = "EMAIL",
    val reminderFrequency: String = "DAILY",
    val createdAt: Long = System.currentTimeMillis()
)
