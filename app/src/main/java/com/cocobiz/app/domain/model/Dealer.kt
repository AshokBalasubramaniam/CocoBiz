package com.cocobiz.app.domain.model

data class Dealer(
    val id: Long = 0L,
    val dealerName: String,
    val place: String,
    val phone: String,
    val alternatePhone: String = "",
    val email: String = "",
    val address: String = "",
    val notes: String = "",
    val photoPath: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class DealerWithStats(
    val dealer: Dealer,
    val totalRevenue: Double = 0.0,
    val activeSalesCount: Int = 0,
    val completedSalesCount: Int = 0,
    val lastSaleDate: Long? = null,
    val upcomingSaleDate: Long? = null
)
