package com.quickthought.orio.data.repository

import com.quickthought.orio.data.local.TransactionsDAO
import com.quickthought.orio.data.mapper.toTransactionDomain
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.toTransactionData
import com.quickthought.orio.domain.repository.TransactionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(private val dao: TransactionsDAO) : TransactionsRepository {
    override fun getAllTransactions(): Flow<List<TransactionDomain>> =
        dao.getAllTransactions().map { entities ->
            entities.map { it.toTransactionDomain() }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.Default)

    override suspend fun insertTransaction(transaction: TransactionDomain) {
        dao.insertTransaction(transaction.toTransactionData())
    }

    override suspend fun deleteTransaction(transaction: TransactionDomain) {
        dao.deleteTransaction(transaction.toTransactionData())
    }

    override suspend fun updateTransaction(updatedTransaction: TransactionDomain) {
        dao.updateTransaction(updatedTransaction.toTransactionData())
    }
}