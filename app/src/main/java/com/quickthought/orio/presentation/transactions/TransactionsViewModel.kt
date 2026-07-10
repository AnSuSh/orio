package com.quickthought.orio.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionFilterState
import com.quickthought.orio.domain.use_case.accounts.AccountUseCases
import com.quickthought.orio.domain.use_case.category.CategoryUseCases
import com.quickthought.orio.domain.use_case.transactions.TransactionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val useCases: TransactionUseCases,
    private val accountUseCases: AccountUseCases,
    private val categoryUseCases: CategoryUseCases
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

    // 1. The state for filters (Search, Category, etc.)
    private val _filterState = MutableStateFlow(TransactionFilterState())
    val filterState = _filterState.asStateFlow()

    // 2. The SINGLE SOURCE OF TRUTH for the list
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val filteredTransactions = filterState
        .debounce { filters ->
            if (filters.searchQuery.isNotEmpty()) 300L else 0L
        }
        .flatMapLatest { filters ->
            useCases.getTransactions(filters)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _editingTransaction = MutableStateFlow<TransactionDomain?>(null)
    val editingTransaction = _editingTransaction.asStateFlow()

    // Update methods
    fun updateFilters(newFilterState: TransactionFilterState) {
        _filterState.update { newFilterState }
    }

    fun onEditTransactionSelected(transaction: TransactionDomain?) {
        _editingTransaction.value = transaction
    }

    fun updateTransaction(
        updatedTransaction: TransactionDomain,
        isInvestment: Boolean = false,
        isSelfTransfer: Boolean = false
    ) {
        viewModelScope.launch {
            if (isSelfTransfer) {
                useCases.deleteTransaction(updatedTransaction)
            } else {
                useCases.updateTransaction(updatedTransaction)
                if (isInvestment) {
                    accountUseCases.addAccount(
                        com.quickthought.orio.domain.model.AccountDomain(
                            name = updatedTransaction.note.ifBlank { "New Investment" },
                            type = com.quickthought.orio.domain.model.AccountType.INVESTMENT,
                            balance = updatedTransaction.amount,
                            initialBalance = updatedTransaction.amount
                        )
                    )
                }
            }
            _editingTransaction.value = null // Clear state after save
        }
    }

    fun addTransaction(
        transaction: TransactionDomain,
        splitCount: Int? = null
    ) {
        viewModelScope.launch {
            useCases.addTransaction(transaction, splitCount)
        }
    }

    fun deleteTransaction(transaction: TransactionDomain) {
        viewModelScope.launch {
            useCases.deleteTransaction(transaction)
        }
    }
}
