package com.quickthought.orio.domain.use_case.analytics

import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionType
import com.quickthought.orio.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategorySpendingUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    operator fun invoke(): Flow<Map<String, Double>> {
        return repository.getAllTransactions().map { transactions ->
            transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
        }
    }
}
