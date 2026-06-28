package com.cocobiz.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class SettingsEntity(
    @PrimaryKey val id: Long = 1L,
    val reminderDays: Int = 5,
    val notificationEnabled: Boolean = true,
    val emailEnabled: Boolean = false,
    val senderEmail: String = "",
    val darkMode: String = "SYSTEM",
    val backupEnabled: Boolean = false,
    val defaultCycleDays: Int = 60
)
