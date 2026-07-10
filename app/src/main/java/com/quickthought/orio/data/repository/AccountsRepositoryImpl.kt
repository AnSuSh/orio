package com.quickthought.orio.data.repository

import com.quickthought.orio.data.local.dao.AccountsDAO
import com.quickthought.orio.data.mapper.toAccountDomain
import com.quickthought.orio.domain.model.AccountDomain
import com.quickthought.orio.domain.model.toAccountData
import com.quickthought.orio.domain.repository.AccountsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountsRepositoryImpl @Inject constructor(
    private val dao: AccountsDAO
) : AccountsRepository {

    override fun getAllAccounts(): Flow<List<AccountDomain>> =
        dao.getAllAccounts().map { entities ->
            entities.map { it.toAccountDomain() }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.Default)

    override suspend fun getAccountById(id: Int): AccountDomain? {
        return dao.getAccountById(id)?.toAccountDomain()
    }

    override suspend fun insertAccount(account: AccountDomain): Long {
        return dao.insertAccount(account.toAccountData())
    }

    override suspend fun updateAccount(account: AccountDomain) {
        dao.updateAccount(account.toAccountData())
    }

    override suspend fun deleteAccount(account: AccountDomain) {
        dao.deleteAccount(account.toAccountData())
    }

    override suspend fun updateBalance(accountId: Int, amount: Double) {
        dao.updateBalance(accountId, amount)
    }
}
