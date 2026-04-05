package com.quickthought.orio.domain.model

import com.quickthought.orio.data.local.entity.TransactionData
import com.quickthought.orio.domain.util.toDateString
import java.util.Locale
import kotlin.text.contains

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

// Helper extension for readability
fun TransactionDomain.matches(filters: TransactionFilterState): Boolean {
    val matchesSearch = note.contains(filters.searchQuery, ignoreCase = true)
    val matchesCategory =
        filters.selectedCategory == null || category == filters.selectedCategory
    val matchesType = when (filters.typeFilter) {
        TransactionTypeFilter.ALL -> true
        TransactionTypeFilter.INCOME -> isIncome
        TransactionTypeFilter.EXPENSE -> !isIncome
    }
    return amount > 0 && matchesSearch && matchesCategory && matchesType
}

// Helper extension for correct sorting
fun List<TransactionDomain>.applySort(sortBy: TransactionSort): List<TransactionDomain> {
    return when (sortBy) {
        TransactionSort.DATE_ASC -> sortedBy { it.date }
        TransactionSort.DATE_DESC -> sortedByDescending { it.date }
        TransactionSort.AMOUNT_ASC -> sortedBy { it.amount }
        TransactionSort.AMOUNT_DESC -> sortedByDescending { it.amount }
    }
}

