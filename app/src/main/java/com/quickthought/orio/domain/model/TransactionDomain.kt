package com.quickthought.orio.domain.model

import com.quickthought.orio.data.local.entity.TransactionData
import com.quickthought.orio.domain.util.toDateString
import java.util.Locale

/**
 * Represent a financial transaction in the domain layer.
 * 
 * @property transactionId Local unique identifier.
 * @property amount Value of the transaction.
 * @property type Whether it's [TransactionType.INCOME] or [TransactionType.EXPENSE].
 * @property category The category of spending/earning (e.g., 'food', 'salary').
 * @property date Epoch timestamp of the transaction.
 * @property note User-provided or auto-extracted description.
 */
data class TransactionDomain(
    val transactionId: Int = 0,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long,
    val note: String = "",
    val trackingMethod: TrackingMethod = TrackingMethod.MANUAL,
    val rawMessage: String? = null,
    val isSynced: Boolean = false,
    val remoteId: String? = null,
    val lastModified: Long = System.currentTimeMillis()
) {
    /** Returns true if the transaction is an income. */
    val isIncome: Boolean = type == TransactionType.INCOME

    /** Returns a human-readable date string. */
    val dateTimeString = date.toDateString()

    /** Returns the amount formatted with the Rupee symbol. */
    val localizedPriceString = String.format(Locale.getDefault(), "₹ %.2f", amount)
}

/**
 * Categorization of financial movement.
 */
enum class TransactionType {
    /** Money coming in. */
    INCOME,

    /** Money going out. */
    EXPENSE
}

fun TransactionDomain.toTransactionData(): TransactionData {
    return TransactionData(
        transactionId,
        amount,
        type.name,
        category,
        date,
        note,
        trackingMethod.name,
        rawMessage,
        isSynced,
        remoteId,
        lastModified
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

