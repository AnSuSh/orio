package com.quickthought.orio.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.data.local.PreferenceManager
import com.quickthought.orio.domain.model.TransactionType
import com.quickthought.orio.domain.repository.CategoryRepository
import com.quickthought.orio.domain.repository.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: TransactionsRepository,
    private val categoryRepository: CategoryRepository,
    preferenceManager: PreferenceManager
) : ViewModel() {

    val state: StateFlow<AnalyticsState> = combine(
        repository.getAllTransactions(),
        categoryRepository.getAllCategories(),
        preferenceManager.isPremium
    ) { transactions, categories, isPremium ->
        val categorySpending = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        // Last 7 days spending including days with 0 spend
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)

        val todayStart = cal.timeInMillis
        val lastSevenDays = (0..6).map { day ->
            val c = java.util.Calendar.getInstance()
            c.timeInMillis = todayStart
            c.add(java.util.Calendar.DAY_OF_YEAR, -day)
            c.timeInMillis
        }.reversed()

        val spendingMap = transactions
            .filter { it.type == TransactionType.EXPENSE && it.date >= lastSevenDays.first() }
            .groupBy {
                val c = java.util.Calendar.getInstance()
                c.timeInMillis = it.date
                c.set(java.util.Calendar.HOUR_OF_DAY, 0)
                c.set(java.util.Calendar.MINUTE, 0)
                c.set(java.util.Calendar.SECOND, 0)
                c.set(java.util.Calendar.MILLISECOND, 0)
                c.timeInMillis
            }
            .mapValues { it.value.sumOf { t -> t.amount } }

        val dailySpending = lastSevenDays.map { date ->
            date to (spendingMap[date] ?: 0.0)
        }

        AnalyticsState(
            categorySpending = categorySpending,
            categories = categories,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            dailySpending = dailySpending,
            isPremium = isPremium
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsState()
    )
}

data class AnalyticsState(
    val categorySpending: Map<String, Double> = emptyMap(),
    val categories: List<com.quickthought.orio.domain.model.Category> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val dailySpending: List<Pair<Long, Double>> = emptyList(),
    val isPremium: Boolean = false
)
