package com.quickthought.orio.domain.model

import com.quickthought.orio.data.local.entity.TransactionData
import com.quickthought.orio.domain.util.toDateString
import java.util.Locale

/**
 * Model to be consumed in UI and Domain layer.
 * */
data class TransactionDomain(
    val transactionId: Int = 0, // Needed to del and update later
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long,
    val note: String = ""
) {
    val isIncome: Boolean = type == TransactionType.INCOME

    val dateTimeString = date.toDateString()

    val localizedPriceString = String.format(Locale.getDefault(), "₹ %.2f", amount)
}

enum class TransactionType {
    INCOME, EXPENSE
}

fun TransactionDomain.toTransactionData(): TransactionData {
    return TransactionData(
        transactionId,
        amount,
        type.name,
        category,
        date,
        note
    )
}

