package com.quickthought.orio.presentation.transactions

import com.quickthought.orio.domain.model.TransactionDomain

data class TransactionsState(
    val transactions: List<TransactionDomain> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
