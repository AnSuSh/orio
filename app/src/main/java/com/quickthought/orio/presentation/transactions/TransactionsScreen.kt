package com.quickthought.orio.presentation.transactions

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.quickthought.orio.presentation.transactions.components.EditTransactionSheet
import com.quickthought.orio.presentation.transactions.components.FilterSection
import com.quickthought.orio.presentation.transactions.components.TransactionItem
import com.quickthought.orio.presentation.util.EmptyTransactionsState
import com.quickthought.orio.ui.theme.ExpenseRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()

    var showSheet by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<TransactionDomain?>(null) }
    val editingTransaction by viewModel.editingTransaction.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Orio - Transactions",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- STICKY HEADER SECTION ---
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp // Subtle shadow when scrolling
                ) {
                    FilterSection(
                        state = filterState,
                        onFilterChange = { viewModel.updateFilters(it) }
                    )
                }
            }
            if (filteredTransactions.isEmpty()) {
                item {
                    Box(
                        Modifier.fillParentMaxHeight(0.5f),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyTransactionsState()
                    }
                }
            } else {
                items(filteredTransactions, key = { it.transactionId }) { transaction ->

                    var showOptionsDialog by remember { mutableStateOf(false) }

                    val dismissState = rememberSwipeToDismissBoxState(
                        initialValue = SwipeToDismissBoxValue.Settled,
                        positionalThreshold = { fullSize -> fullSize * 0.3f },
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                transactionToDelete = transaction
                                false
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
                        TransactionItem(
                            transaction,
                            modifier = Modifier
                                .combinedClickable(
                                    onClick = {
                                        viewModel.onEditTransactionSelected(transaction)
                                    },
                                    onLongClick = {
                                        showOptionsDialog = true
                                    })
                                .animateItem(
                                    fadeInSpec = tween(300),
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessLow, // Makes movement "flow" rather than "snap"
                                        dampingRatio = Spring.DampingRatioLowBouncy
                                    ),
                                    fadeOutSpec = tween(200)
                                )
                        )

                        if (showOptionsDialog) {
                            AlertDialog(
                                onDismissRequest = { showOptionsDialog = false },
                                title = { Text("Transaction Options") },
                                text = { Text("Choose an action for this transaction.") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        viewModel.onEditTransactionSelected(transaction)
                                        showOptionsDialog = false
                                    }) { Text("Edit") }
                                },
                                dismissButton = {
                                    TextButton(onClick = {
                                        transactionToDelete = transaction
                                        showOptionsDialog = false
                                    }) { Text("Delete", color = ExpenseRed) }
                                }
                            )
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

    // Show Edit Sheet when a transaction is selected via Long Click -> Edit
    editingTransaction?.let { transaction ->
        EditTransactionSheet(
            transaction = transaction,
            onDismiss = { viewModel.onEditTransactionSelected(null) },
            onSave = { updated ->
                viewModel.updateTransaction(updated)
            }
        )
    }
}