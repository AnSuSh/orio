package com.quickthought.orio.data.repository

import com.quickthought.orio.data.local.dao.DebtsDAO
import com.quickthought.orio.domain.model.DebtDomain
import com.quickthought.orio.domain.model.toDebtData
import com.quickthought.orio.domain.model.toDebtDomain
import com.quickthought.orio.domain.repository.DebtsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DebtsRepositoryImpl @Inject constructor(
    private val dao: DebtsDAO
) : DebtsRepository {
    override fun getAllDebts(): Flow<List<DebtDomain>> =
        dao.getAllDebts().map { entities ->
            entities.map { it.toDebtDomain() }
        }

    override suspend fun insertDebt(debt: DebtDomain): Long {
        return dao.insertDebt(debt.toDebtData())
    }

    override suspend fun updateDebt(debt: DebtDomain) {
        dao.updateDebt(debt.toDebtData())
    }

    override suspend fun deleteDebt(debt: DebtDomain) {
        dao.deleteDebt(debt.toDebtData())
    }

    override fun getTotalReceivables(): Flow<Double> =
        dao.getTotalReceivables().map { it ?: 0.0 }
}
