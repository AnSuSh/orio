package com.quickthought.orio.data.repository

import com.quickthought.orio.data.local.TransactionsDAO
import com.quickthought.orio.data.mapper.toTransactionDomain
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(private val dao: TransactionsDAO) : HomeRepository {
    override fun getAllTransactions(): Flow<List<TransactionDomain>> =
        dao.getAllTransactions().map { entities ->
            entities.map { it.toTransactionDomain() }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.Default)
}