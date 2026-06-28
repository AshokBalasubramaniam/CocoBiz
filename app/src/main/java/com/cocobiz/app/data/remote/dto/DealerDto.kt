package com.cocobiz.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DealerDto(
    @SerializedName("localId") val localId: Long,
    @SerializedName("dealerName") val dealerName: String,
    @SerializedName("place") val place: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("alternatePhone") val alternatePhone: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("address") val address: String = "",
    @SerializedName("notes") val notes: String = "",
    @SerializedName("photoPath") val photoPath: String = "",
    @SerializedName("createdAt") val createdAt: Long = 0L,
    @SerializedName("updatedAt") val updatedAt: Long = 0L
)
