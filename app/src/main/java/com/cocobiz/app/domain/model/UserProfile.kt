package com.cocobiz.app.domain.model

data class UserProfile(
    val id: Long = 1L,
    val businessName: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val alternatePhone: String = "",
    val email: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val gstNumber: String = "",
    val logoPath: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isComplete: Boolean
        get() = businessName.isNotBlank() && ownerName.isNotBlank() && phone.isNotBlank()
}
