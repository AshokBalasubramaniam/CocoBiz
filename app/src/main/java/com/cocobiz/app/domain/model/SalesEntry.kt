package com.cocobiz.app.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class SalesEntry(
    val id: Long = 0L,
    val dealerId: Long,
    val dealerName: String,
    val dealerPlace: String,
    val salesDate: LocalDate,
    val nextSalesDate: LocalDate,
    val quantity: Double,
    val rate: Double,
    val totalAmount: Double,
    val coconutType: CoconutType,
    val cycleDays: Int = 60,
    val status: SaleStatus = SaleStatus.ACTIVE,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val remainingDays: Long
        get() = ChronoUnit.DAYS.between(LocalDate.now(), nextSalesDate).coerceAtLeast(0)

    val statusColor: StatusColor
        get() = when {
            status == SaleStatus.COMPLETED -> StatusColor.GRAY
            remainingDays <= 6 -> StatusColor.RED
            remainingDays <= 14 -> StatusColor.YELLOW
            else -> StatusColor.GREEN
        }

    val isOverdue: Boolean
        get() = status == SaleStatus.ACTIVE && LocalDate.now().isAfter(nextSalesDate)
}

enum class CoconutType(val displayName: String) {
    TONNAGE("Tonnage"),
    SINGLE_PIECE("Single Piece")
}

enum class SaleStatus(val displayName: String) {
    ACTIVE("Active"),
    COMPLETED("Completed")
}

enum class StatusColor {
    GREEN, YELLOW, RED, GRAY
}
