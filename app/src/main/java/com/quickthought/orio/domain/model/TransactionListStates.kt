package com.quickthought.orio.domain.model

enum class TransactionSort { DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC }
enum class TransactionTypeFilter { ALL, INCOME, EXPENSE }

data class TransactionFilterState(
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val typeFilter: TransactionTypeFilter = TransactionTypeFilter.ALL,
    val sortBy: TransactionSort = TransactionSort.DATE_DESC
)