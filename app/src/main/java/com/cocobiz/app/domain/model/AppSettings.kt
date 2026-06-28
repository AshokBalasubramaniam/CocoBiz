package com.cocobiz.app.domain.model

data class AppSettings(
    val id: Long = 1L,
    val reminderDays: Int = 5,
    val notificationEnabled: Boolean = true,
    val emailEnabled: Boolean = false,
    val senderEmail: String = "",
    val darkMode: DarkModeOption = DarkModeOption.SYSTEM,
    val backupEnabled: Boolean = false,
    val defaultCycleDays: Int = 60
)

enum class DarkModeOption(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System Default")
}

data class DashboardStats(
    val totalActiveSales: Int = 0,
    val totalCompletedSales: Int = 0,
    val totalRevenue: Double = 0.0,
    val upcomingReminders: Int = 0
)
