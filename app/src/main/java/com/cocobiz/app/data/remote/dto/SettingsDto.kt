package com.cocobiz.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SettingsDto(
    @SerializedName("localId") val localId: Long = 1L,
    @SerializedName("reminderDays") val reminderDays: Int = 5,
    @SerializedName("notificationEnabled") val notificationEnabled: Boolean = true,
    @SerializedName("emailEnabled") val emailEnabled: Boolean = false,
    @SerializedName("senderEmail") val senderEmail: String = "",
    @SerializedName("darkMode") val darkMode: String = "SYSTEM",
    @SerializedName("backupEnabled") val backupEnabled: Boolean = false,
    @SerializedName("defaultCycleDays") val defaultCycleDays: Int = 60,
    @SerializedName("updatedAt") val updatedAt: Long = 0L
)
