package com.quickthought.orio.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.presentation.transactions.components.AddTransactionSheet
import com.quickthought.orio.presentation.transactions.components.BalanceOverview
import com.quickthought.orio.presentation.transactions.components.BudgetEditDialog
import com.quickthought.orio.presentation.transactions.components.BudgetProgressSection
import com.quickthought.orio.presentation.transactions.components.TransactionItem
import com.quickthought.orio.ui.theme.ExpenseRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()

    var showSheet by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<TransactionDomain?>(null) }

    var showBudgetDialog by remember { mutableStateOf(false) }
    // A key to force animation replay
    var budgetAnimKey by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orio", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
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
                            EmptyState()
                        }
                    }
                } else {
                    items(state.transactions, key = { it.transactionId }) { transaction ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    // Trigger your delete confirmation dialog or direct delete
                                    transactionToDelete = transaction
                                    false // Return false so the item doesn't disappear until confirmed
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromEndToStart = true,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color = ExpenseRed
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color, MaterialTheme.shapes.medium)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.White
                                    )
                                }
                            }
                        ) {
                            TransactionItem(transaction) { transactionToDelete = it }
                        }
                    }
                }
            }
        }
    }

    // Add Transaction Bottom Sheet
    if (showSheet) {
        AddTransactionSheet(
            sheetState = sheetState,
            onDismiss = { showSheet = false },
            onSave = {
                viewModel.addTransaction(it)
                showSheet = false
            }
        )
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

    // Delete Confirmation Dialog
    transactionToDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTransaction(transaction)
                    transactionToDelete = null
                }) { Text("Delete", color = ExpenseRed) }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun EmptyState() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Text("No transactions yet", color = Color.Gray)
    }
}