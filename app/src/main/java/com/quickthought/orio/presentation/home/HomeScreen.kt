package com.quickthought.orio.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickthought.orio.presentation.transactions.components.BalanceOverview
import com.quickthought.orio.presentation.transactions.components.BudgetEditDialog
import com.quickthought.orio.presentation.transactions.components.BudgetProgressSection
import com.quickthought.orio.presentation.transactions.components.TransactionItem
import com.quickthought.orio.presentation.util.EmptyTransactionsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    var showBudgetDialog by remember { mutableStateOf(false) }
    // A key to force animation replay
    var budgetAnimKey by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Orio - Dashboard",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 96.dp,
                    top = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Professional Overview Section
                item {
                    BalanceOverview(
                        balance = state.totalBalance,
                        income = state.totalIncome,
                        expense = state.totalExpense
                    )
                }

                // Budget Progress Section
                item {
                    // We wrap this in a key. When budgetAnimKey changes, the animation restarts.
                    key(budgetAnimKey) {
                        BudgetProgressSection(
                            totalExpenses = state.totalExpense,
                            daysLeftInMonth = state.daysLeftInMonth,
                            monthlyBudget = state.monthlyBudget,
                            modifier = Modifier.clickable {
                                showBudgetDialog = true
                            }
                        )
                    }
                }

                // Section Header
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (state.transactions.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillParentMaxHeight(0.5f),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyTransactionsState()
                        }
                    }
                } else {
                    items(state.transactions.take(5), key = { it.transactionId }) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }
        }
    }

    if (showBudgetDialog) {
        BudgetEditDialog(
            currentBudget = state.monthlyBudget,
            onDismiss = {
                showBudgetDialog = false
                budgetAnimKey++ // Trigger replay even if value didn't change
            },
            onSave = { newBudget ->
                viewModel.saveMonthlyBudget(newBudget)
                showBudgetDialog = false
                budgetAnimKey++
            }
        )
    }
}