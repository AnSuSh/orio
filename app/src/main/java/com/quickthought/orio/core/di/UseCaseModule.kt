package com.quickthought.orio.core.di

import com.quickthought.orio.domain.repository.TransactionsRepository
import com.quickthought.orio.domain.use_case.transactions.AddTransactionUseCase
import com.quickthought.orio.domain.use_case.transactions.DeleteTransactionUseCase
import com.quickthought.orio.domain.use_case.transactions.GetTransactionsUseCase
import com.quickthought.orio.domain.use_case.transactions.TransactionUseCases
import com.quickthought.orio.domain.use_case.transactions.UpdateTransactionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideTransactionUseCases(
        repository: TransactionsRepository
    ): TransactionUseCases {
        return TransactionUseCases(
            getTransactions = GetTransactionsUseCase(repository),
            addTransaction = AddTransactionUseCase(repository),
            deleteTransaction = DeleteTransactionUseCase(repository),
            updateTransaction = UpdateTransactionUseCase(repository)
        )
    }
}
