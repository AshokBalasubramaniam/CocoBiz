package com.cocobiz.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dealers")
data class DealerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
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
