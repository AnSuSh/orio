package com.quickthought.orio.core.di

import com.quickthought.orio.data.local.dao.AccountsDAO
import com.quickthought.orio.data.local.dao.CategoryDAO
import com.quickthought.orio.data.local.dao.DebtsDAO
import com.quickthought.orio.data.local.dao.TransactionsDAO
import com.quickthought.orio.data.repository.AccountsRepositoryImpl
import com.quickthought.orio.data.repository.CategoryRepositoryImpl
import com.quickthought.orio.data.repository.DebtsRepositoryImpl
import com.quickthought.orio.data.repository.HomeRepositoryImpl
import com.quickthought.orio.data.repository.ProfileRepositoryImpl
import com.quickthought.orio.data.repository.TransactionsRepositoryImpl
import com.quickthought.orio.domain.repository.AccountsRepository
import com.quickthought.orio.domain.repository.CategoryRepository
import com.quickthought.orio.domain.repository.DebtsRepository
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
    fun provideAccountsRepository(dao: AccountsDAO): AccountsRepository {
        return AccountsRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideDebtsRepository(dao: DebtsDAO): DebtsRepository {
        return DebtsRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: CategoryDAO): CategoryRepository {
        return CategoryRepositoryImpl(dao)
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