package com.quickthought.orio.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionFilterState
import com.quickthought.orio.domain.model.applySort
import com.quickthought.orio.domain.model.matches
import com.quickthought.orio.domain.repository.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionsRepository,
) : ViewModel() {

    // 1. The state for filters (Search, Category, etc.)
    private val _filterState = MutableStateFlow(TransactionFilterState())
    val filterState = _filterState.asStateFlow()

    // 2. The SINGLE SOURCE OF TRUTH for the list
    // This starts automatically when the UI observes it.
    @OptIn(FlowPreview::class)
    val filteredTransactions = combine(
        repository.getAllTransactions(),
        // We debounce the filterState so typing doesn't trigger 100 re-renders
        filterState.debounce { filters ->
            // If only the search query changed, wait 300ms.
            // If category or sort changed, wait 0ms (instant).
            if (filters.searchQuery.isNotEmpty()) 300L else 0L
        }
    ) { transactions, filters ->
        withContext(Dispatchers.Default) {
            transactions
                .filter { it.matches(filters) }
                .applySort(filters.sortBy)
        }
    }.stateIn(
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

    fun updateTransaction(updatedTransaction: TransactionDomain) {
        viewModelScope.launch {
            repository.updateTransaction(updatedTransaction)
            _editingTransaction.value = null // Clear state after save
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
}