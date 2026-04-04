package com.quickthought.orio.core.di

import com.quickthought.orio.data.local.TransactionsDAO
import com.quickthought.orio.data.repository.HomeRepositoryImpl
import com.quickthought.orio.data.repository.ProfileRepositoryImpl
import com.quickthought.orio.data.repository.TransactionsRepositoryImpl
import com.quickthought.orio.domain.repository.HomeRepository
import com.quickthought.orio.domain.repository.ProfileRepository
import com.quickthought.orio.domain.repository.TransactionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTransactionsRepository(dao: TransactionsDAO): TransactionsRepository {
        return TransactionsRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(dao: TransactionsDAO): HomeRepository {
        return HomeRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(): ProfileRepository {
        return ProfileRepositoryImpl()
    }
}