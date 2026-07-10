package com.quickthought.orio.domain.use_case.accounts

data class AccountUseCases(
    val getAccounts: GetAccountsUseCase,
    val addAccount: AddAccountUseCase
)
