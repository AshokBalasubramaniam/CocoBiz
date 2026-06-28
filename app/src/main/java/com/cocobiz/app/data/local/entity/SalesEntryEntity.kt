package com.cocobiz.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sales_entries",
    foreignKeys = [
        ForeignKey(
            entity = DealerEntity::class,
            parentColumns = ["id"],
            childColumns = ["dealerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["dealerId"])]
)
data class SalesEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val dealerId: Long,
    val dealerName: String,
    val dealerPlace: String,
    val salesDate: Long,
    val nextSalesDate: Long,
    val quantity: Double,
    val rate: Double,
    val totalAmount: Double,
    val coconutType: String,
    val cycleDays: Int = 60,
    val status: String = "ACTIVE",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
