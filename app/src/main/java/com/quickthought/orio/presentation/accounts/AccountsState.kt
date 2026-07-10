package com.quickthought.orio.presentation.accounts

import com.quickthought.orio.domain.model.AccountDomain
import com.quickthought.orio.domain.model.DebtDomain

data class AccountsState(
    val accounts: List<AccountDomain> = emptyList(),
    val totalBalance: Double = 0.0,
    val debts: List<DebtDomain> = emptyList(),
    val totalReceivables: Double = 0.0,
    val isLoading: Boolean = false
)
