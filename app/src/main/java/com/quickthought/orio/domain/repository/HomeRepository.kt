package com.quickthought.orio.domain.repository

import com.quickthought.orio.domain.model.TransactionDomain
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getAllTransactions(): Flow<List<TransactionDomain>>
}