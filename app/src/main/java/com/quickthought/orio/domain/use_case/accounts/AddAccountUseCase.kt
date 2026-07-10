package com.quickthought.orio.domain.use_case.accounts

import com.quickthought.orio.domain.model.AccountDomain
import com.quickthought.orio.domain.repository.AccountsRepository
import javax.inject.Inject

class AddAccountUseCase @Inject constructor(
    private val repository: AccountsRepository
) {
    suspend operator fun invoke(account: AccountDomain): Long {
        return repository.insertAccount(account)
    }
}
