package com.quickthought.orio.domain.use_case.accounts

import com.quickthought.orio.domain.model.AccountDomain
import com.quickthought.orio.domain.repository.AccountsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val repository: AccountsRepository
) {
    operator fun invoke(): Flow<List<AccountDomain>> {
        return repository.getAllAccounts()
    }
}
