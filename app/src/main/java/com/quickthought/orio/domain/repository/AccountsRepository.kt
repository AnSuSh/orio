package com.quickthought.orio.domain.repository

import com.quickthought.orio.domain.model.AccountDomain
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    fun getAllAccounts(): Flow<List<AccountDomain>>
    suspend fun getAccountById(id: Int): AccountDomain?
    suspend fun insertAccount(account: AccountDomain): Long
    suspend fun updateAccount(account: AccountDomain)
    suspend fun deleteAccount(account: AccountDomain)
    suspend fun updateBalance(accountId: Int, amount: Double)
}
