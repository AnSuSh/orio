package com.quickthought.orio.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.repository.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionsRepository,
) : ViewModel() {

    // Using StateFlow for reactive UI updates
    private val _state = MutableStateFlow(TransactionsState())
    val state: StateFlow<TransactionsState> = _state.asStateFlow()

    private val _editingTransaction = MutableStateFlow<TransactionDomain?>(null)
    val editingTransaction = _editingTransaction.asStateFlow()

    fun onEditTransactionSelected(transaction: TransactionDomain?) {
        _editingTransaction.value = transaction
    }

    init {
        loadTransactionHistory()
    }

    private fun loadTransactionHistory() {
        viewModelScope.launch {
            repository.getAllTransactions().collect { transactions ->

                _state.update { it.copy(transactions = transactions) }
            }
        }
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