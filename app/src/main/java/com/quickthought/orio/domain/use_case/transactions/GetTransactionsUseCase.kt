package com.quickthought.orio.domain.use_case.transactions

import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionFilterState
import com.quickthought.orio.domain.model.applySort
import com.quickthought.orio.domain.model.matches
import com.quickthought.orio.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to retrieve transactions filtered and sorted according to the provided state.
 */
class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    operator fun invoke(filters: TransactionFilterState): Flow<List<TransactionDomain>> {
        return repository.getAllTransactions().map { transactions ->
            transactions
                .filter { it.matches(filters) }
                .applySort(filters.sortBy)
        }
    }
}
