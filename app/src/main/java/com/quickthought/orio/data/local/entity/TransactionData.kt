package com.quickthought.orio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val categoryId: String = "other",
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
)
