package com.quickthought.orio.domain.model

data class HomeOverview(
    val transactions: List<TransactionDomain>,
    val totalIncome: Double,
    val totalExpense: Double,
    val monthlyBudget: Double,
    val daysLeftInMonth: Int
) {
    val totalBalance: Double = totalIncome - totalExpense
    val remainingBudget: Double = (monthlyBudget - totalExpense).coerceAtLeast(0.0)
    val dailyRemaining: Double = if (daysLeftInMonth > 0) remainingBudget / daysLeftInMonth else 0.0
}
