package com.quickthought.orio.presentation.transactions

import app.cash.turbine.test
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionType
import com.quickthought.orio.domain.use_case.transactions.AddTransactionUseCase
import com.quickthought.orio.domain.use_case.transactions.DeleteTransactionUseCase
import com.quickthought.orio.domain.use_case.transactions.GetTransactionsUseCase
import com.quickthought.orio.domain.use_case.transactions.TransactionUseCases
import com.quickthought.orio.domain.use_case.transactions.UpdateTransactionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: TransactionsViewModel
    private lateinit var useCases: TransactionUseCases
    
    private val getTransactions = mockk<GetTransactionsUseCase>()
    private val addTransaction = mockk<AddTransactionUseCase>()
    private val deleteTransaction = mockk<DeleteTransactionUseCase>()
    private val updateTransaction = mockk<UpdateTransactionUseCase>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCases = TransactionUseCases(
            getTransactions = getTransactions,
            addTransaction = addTransaction,
            deleteTransaction = deleteTransaction,
            updateTransaction = updateTransaction
        )
        
        // Default behavior for getTransactions
        every { getTransactions(any()) } returns flowOf(emptyList())
        
        viewModel = TransactionsViewModel(useCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when deleting transaction, use case is called`() = runTest {
        val transaction = TransactionDomain(
            amount = 100.0,
            type = TransactionType.EXPENSE,
            category = "food",
            date = System.currentTimeMillis()
        )
        coEvery { deleteTransaction(transaction) } returns Unit
        
        viewModel.deleteTransaction(transaction)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { deleteTransaction(transaction) }
    }

    @Test
    fun `filteredTransactions reflects use case output`() = runTest {
        val transactions = listOf(
            TransactionDomain(
                amount = 50.0,
                type = TransactionType.EXPENSE,
                category = "food",
                date = System.currentTimeMillis()
            )
        )
        every { getTransactions(any()) } returns flowOf(transactions)
        
        // Trigger a change to refresh the flow
        viewModel.updateFilters(viewModel.filterState.value)
        
        viewModel.filteredTransactions.test {
            val result = awaitItem()
            assertEquals(transactions, result)
        }
    }
}
