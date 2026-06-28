package com.cocobiz.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserProfileDto(
    @SerializedName("localId") val localId: Long = 1L,
    @SerializedName("businessName") val businessName: String = "",
    @SerializedName("ownerName") val ownerName: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("alternatePhone") val alternatePhone: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("address") val address: String = "",
    @SerializedName("city") val city: String = "",
    @SerializedName("state") val state: String = "",
    @SerializedName("pincode") val pincode: String = "",
    @SerializedName("gstNumber") val gstNumber: String = "",
    @SerializedName("logoPath") val logoPath: String = "",
    @SerializedName("createdAt") val createdAt: Long = 0L,
    @SerializedName("updatedAt") val updatedAt: Long = 0L
)
