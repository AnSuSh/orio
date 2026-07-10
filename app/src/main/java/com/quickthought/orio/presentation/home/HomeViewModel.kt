package com.quickthought.orio.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.data.local.PreferenceManager
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.use_case.accounts.AccountUseCases
import com.quickthought.orio.domain.use_case.category.CategoryUseCases
import com.quickthought.orio.domain.use_case.home.GetHomeOverviewUseCase
import com.quickthought.orio.domain.use_case.transactions.TransactionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getHomeOverviewUseCase: GetHomeOverviewUseCase,
    private val transactionUseCases: TransactionUseCases,
    private val accountUseCases: AccountUseCases,
    private val categoryUseCases: CategoryUseCases,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val accounts = accountUseCases.getAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories = categoryUseCases.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val state: StateFlow<HomeState> = getHomeOverviewUseCase()
        .map { overview ->
            HomeState(
                transactions = overview.transactions,
                totalIncome = overview.totalIncome,
                totalExpense = overview.totalExpense,
                monthlyBudget = overview.monthlyBudget,
                daysLeftInMonth = overview.daysLeftInMonth
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState(daysLeftInMonth = 1)
        )

    fun saveMonthlyBudget(newAmount: Double) {
        viewModelScope.launch {
            preferenceManager.saveBudget(newAmount)
        }
    }

    fun addTransaction(
        transaction: TransactionDomain,
        splitCount: Int? = null
    ) {
        viewModelScope.launch {
            transactionUseCases.addTransaction(transaction, splitCount)
        }
    }
}