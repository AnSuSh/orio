package com.quickthought.orio.domain.repository

import com.quickthought.orio.domain.model.TransactionDomain
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {

    fun getAllTransactions(): Flow<List<TransactionDomain>>

    suspend fun insertTransaction(transaction: TransactionDomain)

    suspend fun deleteTransaction(transaction: TransactionDomain)

    suspend fun updateTransaction(updatedTransaction: TransactionDomain)
}