package com.quickthought.orio.domain.use_case.transactions

import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.repository.TransactionsRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(transaction: TransactionDomain) {
        repository.deleteTransaction(transaction)
    }
}
