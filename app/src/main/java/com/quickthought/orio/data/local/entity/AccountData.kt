package com.quickthought.orio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quickthought.orio.domain.model.AccountType

@Entity(tableName = "accounts")
data class AccountData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: AccountType,
    val balance: Double = 0.0,
    val initialBalance: Double = 0.0,
    val iconRes: String? = null, // Store icon name or resource string
    val colorHex: String? = null,
    val lastModified: Long = System.currentTimeMillis()
)
