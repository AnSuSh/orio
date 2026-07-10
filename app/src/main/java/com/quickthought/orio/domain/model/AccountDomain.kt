package com.quickthought.orio.domain.model

import com.quickthought.orio.data.local.entity.AccountData

data class AccountDomain(
    val id: Int = 0,
    val name: String,
    val type: AccountType,
    val balance: Double = 0.0,
    val initialBalance: Double = 0.0,
    val iconRes: String? = null,
    val colorHex: String? = null,
    val lastModified: Long = System.currentTimeMillis()
)

fun AccountDomain.toAccountData(): AccountData {
    return AccountData(
        id = id,
        name = name,
        type = type,
        balance = balance,
        initialBalance = initialBalance,
        iconRes = iconRes,
        colorHex = colorHex,
        lastModified = lastModified
    )
}
