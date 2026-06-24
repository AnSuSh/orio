package com.quickthought.orio.domain.use_case.transactions

data class TransactionUseCases(
    val getTransactions: GetTransactionsUseCase,
    val addTransaction: AddTransactionUseCase,
    val deleteTransaction: DeleteTransactionUseCase,
    val updateTransaction: UpdateTransactionUseCase
)
