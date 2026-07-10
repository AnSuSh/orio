package com.quickthought.orio.presentation.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.domain.model.AccountDomain
import com.quickthought.orio.domain.model.DebtDomain
import com.quickthought.orio.domain.repository.DebtsRepository
import com.quickthought.orio.domain.use_case.accounts.AccountUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountUseCases: AccountUseCases,
    private val debtsRepository: DebtsRepository
) : ViewModel() {

    val state = combine(
        accountUseCases.getAccounts(),
        debtsRepository.getAllDebts(),
        debtsRepository.getTotalReceivables()
    ) { accounts, debts, receivables ->
        AccountsState(
            accounts = accounts,
            totalBalance = accounts.sumOf { it.balance },
            debts = debts,
            totalReceivables = receivables,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AccountsState(isLoading = true)
    )

    fun addAccount(account: AccountDomain) {
        viewModelScope.launch {
            accountUseCases.addAccount(account)
        }
    }

    fun markDebtAsPaid(debt: DebtDomain) {
        viewModelScope.launch {
            debtsRepository.updateDebt(debt.copy(isPaid = true))
        }
    }
}
