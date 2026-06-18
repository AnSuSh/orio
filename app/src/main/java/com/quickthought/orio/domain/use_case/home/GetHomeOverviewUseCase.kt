package com.quickthought.orio.domain.use_case.home

import android.icu.util.Calendar
import com.quickthought.orio.data.local.PreferenceManager
import com.quickthought.orio.domain.model.HomeOverview
import com.quickthought.orio.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import kotlin.math.max

class GetHomeOverviewUseCase @Inject constructor(
    private val repository: TransactionsRepository,
    private val preferenceManager: PreferenceManager
) {
    operator fun invoke(): Flow<HomeOverview> {
        return combine(
            repository.getAllTransactions(),
            preferenceManager.monthlyBudget
        ) { transactions, budget ->
            val income = transactions.filter { it.isIncome }.sumOf { it.amount }
            val expense = transactions.filter { !it.isIncome }.sumOf { it.amount }

            HomeOverview(
                transactions = transactions,
                totalIncome = income,
                totalExpense = expense,
                monthlyBudget = budget,
                daysLeftInMonth = getDaysLeftInMonth()
            )
        }
    }

    private fun getDaysLeftInMonth(): Int {
        val calendar = Calendar.getInstance()
        val totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        return max(1, ((totalDays - currentDay) + 1))
    }
}
