package com.cocobiz.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SalesEntryDto(
    @SerializedName("localId") val localId: Long,
    @SerializedName("dealerId") val dealerId: Long,
    @SerializedName("dealerName") val dealerName: String,
    @SerializedName("dealerPlace") val dealerPlace: String,
    @SerializedName("salesDate") val salesDate: Long,
    @SerializedName("nextSalesDate") val nextSalesDate: Long,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("rate") val rate: Double,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("coconutType") val coconutType: String,
    @SerializedName("cycleDays") val cycleDays: Int,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String = "",
    @SerializedName("createdAt") val createdAt: Long = 0L,
    @SerializedName("updatedAt") val updatedAt: Long = 0L
)
