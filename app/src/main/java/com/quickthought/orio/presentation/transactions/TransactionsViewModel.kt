package com.quickthought.orio.presentation.transactions

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.data.local.PreferenceManager
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.repository.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionsRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    // Using StateFlow for reactive UI updates
    private val _state = MutableStateFlow(
        TransactionsState(
            daysLeftInMonth = getDaysLeftInMonth()
        )
    )
    val state: StateFlow<TransactionsState> = _state.asStateFlow()

    init {
        loadTransactionHistory()
        observeBudget()
    }

    private fun loadTransactionHistory() {
        viewModelScope.launch {
            repository.getAllTransactions().collect { transactions ->

                val income = transactions.filter { it.isIncome }.sumOf { it.amount }
                val expense = transactions.filter { !it.isIncome }.sumOf { it.amount }

                _state.update {
                    it.copy(
                        transactions = transactions,
                        totalIncome = income,
                        totalExpense = expense
                    )
                }
            }
        }
    }

    private fun observeBudget() {
        viewModelScope.launch {
            preferenceManager.monthlyBudget.collect { savedBudget ->
                _state.update { it.copy(monthlyBudget = savedBudget) }
            }
        }
    }

    fun saveMonthlyBudget(newAmount: Double) {
        viewModelScope.launch {
            // This saves it to local storage permanently
            preferenceManager.saveBudget(newAmount)
        }
    }

    fun addTransaction(transaction: TransactionDomain) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionDomain) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    /**
     * Helper to calculate remaining days in the current month
     */
    private fun getDaysLeftInMonth(): Int {
        val calendar = Calendar.getInstance()
        val totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        return max(1, ((totalDays - currentDay) + 1))
    }
}