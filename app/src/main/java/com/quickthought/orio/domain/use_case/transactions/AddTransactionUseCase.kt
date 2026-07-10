package com.quickthought.orio.domain.use_case.transactions

import com.quickthought.orio.domain.model.AccountDomain
import com.quickthought.orio.domain.model.AccountType
import com.quickthought.orio.domain.model.DebtDomain
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.repository.AccountsRepository
import com.quickthought.orio.domain.repository.DebtsRepository
import com.quickthought.orio.domain.repository.TransactionsRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionsRepository,
    private val accountsRepository: AccountsRepository,
    private val debtsRepository: DebtsRepository
) {
    suspend operator fun invoke(
        transaction: TransactionDomain,
        splitCount: Int? = null
    ) {
        val actualSplitCount = splitCount ?: 1
        val finalTransaction = if (actualSplitCount > 1) {
            transaction.copy(amount = transaction.amount / actualSplitCount)
        } else {
            transaction
        }

        val transactionId = repository.insertTransaction(finalTransaction)

        transaction.accountId?.let { accountId ->
            val balanceImpact = if (transaction.isIncome) transaction.amount else -transaction.amount
            accountsRepository.updateBalance(accountId, balanceImpact)
        }

        if (actualSplitCount > 1 && !transaction.isIncome) {
            val receivableAmount = transaction.amount - finalTransaction.amount
            debtsRepository.insertDebt(
                DebtDomain(
                    transactionId = transactionId.toInt(),
                    amount = receivableAmount,
                    personName = "Split with $actualSplitCount people",
                    date = transaction.date
                )
            )
        }
    }
}
