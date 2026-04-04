package com.quickthought.orio.presentation.home

import com.quickthought.orio.domain.model.TransactionDomain

data class HomeState(
    val transactions: List<TransactionDomain> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val daysLeftInMonth: Int,
    val monthlyBudget: Double = 5000.0,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val totalBalance: Double = totalIncome - totalExpense
}
