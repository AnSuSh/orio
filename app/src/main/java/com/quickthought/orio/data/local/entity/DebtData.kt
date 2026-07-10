package com.quickthought.orio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionId: Int,
    val amount: Double,
    val personName: String? = null,
    val isPaid: Boolean = false,
    val date: Long = System.currentTimeMillis()
)
