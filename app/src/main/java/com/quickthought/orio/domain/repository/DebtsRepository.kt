package com.quickthought.orio.domain.repository

import com.quickthought.orio.domain.model.DebtDomain
import kotlinx.coroutines.flow.Flow

interface DebtsRepository {
    fun getAllDebts(): Flow<List<DebtDomain>>
    suspend fun insertDebt(debt: DebtDomain): Long
    suspend fun updateDebt(debt: DebtDomain)
    suspend fun deleteDebt(debt: DebtDomain)
    fun getTotalReceivables(): Flow<Double>
}
