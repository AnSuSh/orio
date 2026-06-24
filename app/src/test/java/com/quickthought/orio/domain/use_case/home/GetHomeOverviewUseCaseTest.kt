package com.quickthought.orio.domain.use_case.home

import app.cash.turbine.test
import com.quickthought.orio.data.local.PreferenceManager
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionType
import com.quickthought.orio.domain.repository.TransactionsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetHomeOverviewUseCaseTest {

    private lateinit var useCase: GetHomeOverviewUseCase
    private val repository = mockk<TransactionsRepository>()
    private val preferenceManager = mockk<PreferenceManager>()

    @Before
    fun setup() {
        useCase = GetHomeOverviewUseCase(repository, preferenceManager)
    }

    @Test
    fun `should combine transactions and budget into overview`() = runTest {
        val transactions = listOf(
            TransactionDomain(amount = 1000.0, type = TransactionType.INCOME, category = "salary", date = 0),
            TransactionDomain(amount = 200.0, type = TransactionType.EXPENSE, category = "food", date = 0)
        )
        every { repository.getAllTransactions() } returns flowOf(transactions)
        every { preferenceManager.monthlyBudget } returns flowOf(500.0)

        useCase().test {
            val overview = awaitItem()
            assertEquals(1000.0, overview.totalIncome, 0.1)
            assertEquals(200.0, overview.totalExpense, 0.1)
            assertEquals(500.0, overview.monthlyBudget, 0.1)
            assertEquals(300.0, overview.remainingBudget, 0.1)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
