package com.quickthought.orio.presentation.transactions

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionFilterState
import com.quickthought.orio.presentation.transactions.components.AddTransactionSheet
import com.quickthought.orio.presentation.transactions.components.EditTransactionSheet
import com.quickthought.orio.presentation.transactions.components.FilterSection
import com.quickthought.orio.presentation.transactions.components.TransactionItem
import com.quickthought.orio.presentation.util.EmptyTransactionsState
import com.quickthought.orio.presentation.util.OrioTopAppBar
import com.quickthought.orio.ui.components.AdaptiveWrapper
import com.quickthought.orio.ui.theme.OrioExpense
import com.quickthought.orio.ui.theme.OrioTheme

@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val editingTransaction by viewModel.editingTransaction.collectAsStateWithLifecycle()

    TransactionsScreenContent(
        modifier = modifier,
        filterState = filterState,
        filteredTransactions = filteredTransactions,
        editingTransaction = editingTransaction,
        onFilterChange = viewModel::updateFilters,
        onAddTransaction = viewModel::addTransaction,
        onEditTransactionSelected = viewModel::onEditTransactionSelected,
        onUpdateTransaction = viewModel::updateTransaction,
        onDeleteTransaction = viewModel::deleteTransaction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreenContent(
    modifier: Modifier = Modifier,
    filterState: TransactionFilterState,
    filteredTransactions: List<TransactionDomain>,
    editingTransaction: TransactionDomain?,
    onFilterChange: (TransactionFilterState) -> Unit,
    onAddTransaction: (TransactionDomain) -> Unit,
    onEditTransactionSelected: (TransactionDomain?) -> Unit,
    onUpdateTransaction: (TransactionDomain) -> Unit,
    onDeleteTransaction: (TransactionDomain) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showAddSheet by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<TransactionDomain?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { OrioTopAppBar("Orio - Transactions") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        AdaptiveWrapper(modifier = Modifier.padding(paddingValues)) {
            TransactionList(
                transactions = filteredTransactions,
                filterState = filterState,
                onFilterChange = onFilterChange,
                onEdit = onEditTransactionSelected,
                onDeleteRequest = { transactionToDelete = it }
            )
        }
    }

    if (showAddSheet) {
        AddTransactionSheet(
            sheetState = sheetState,
            onDismiss = { showAddSheet = false },
            onSave = {
                onAddTransaction(it)
                showAddSheet = false
            }
        )
    }

    transactionToDelete?.let { transaction ->
        DeleteConfirmationDialog(
            onConfirm = {
                onDeleteTransaction(transaction)
                transactionToDelete = null
            },
            onDismiss = { transactionToDelete = null }
        )
    }

    editingTransaction?.let { transaction ->
        EditTransactionSheet(
            transaction = transaction,
            onDismiss = { onEditTransactionSelected(null) },
            onSave = { updated ->
                onUpdateTransaction(updated)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionList(
    transactions: List<TransactionDomain>,
    filterState: TransactionFilterState,
    onFilterChange: (TransactionFilterState) -> Unit,
    onEdit: (TransactionDomain) -> Unit,
    onDeleteRequest: (TransactionDomain) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val groupedTransactions = remember(transactions) {
        derivedStateOf { transactions.groupBy { it.dateTimeString } }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stickyHeader {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                FilterSection(
                    state = filterState,
                    onFilterChange = onFilterChange
                )
            }
        }
        if (transactions.isEmpty()) {
            item {
                Box(
                    Modifier.fillParentMaxHeight(0.5f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTransactionsState()
                }
            }
        } else {
            groupedTransactions.value.forEach { (date, dailyTransactions) ->
                stickyHeader {
                    Surface(
                        Modifier.fillMaxWidth(),
                        color = Color.Transparent
                    ) {
                        Text(
                            date,
                            Modifier.padding(8.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                items(dailyTransactions, key = { it.transactionId }) { transaction ->
                    SwipeableTransactionItem(
                        transaction = transaction,
                        onEdit = { onEdit(transaction) },
                        onDelete = { onDeleteRequest(transaction) },
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(300),
                            placementSpec = spring(
                                stiffness = Spring.StiffnessLow,
                                dampingRatio = Spring.DampingRatioLowBouncy
                            ),
                            fadeOutSpec = tween(200)
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTransactionItem(
    transaction: TransactionDomain,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOptionsDialog by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        positionalThreshold = { fullSize -> fullSize * 0.3f },
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
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
            val color = OrioExpense
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
        },
        modifier = modifier
    ) {
        TransactionItem(
            transaction,
            modifier = Modifier
                .combinedClickable(
                    onClick = onEdit,
                    onLongClick = { showOptionsDialog = true }
                )
        )

        if (showOptionsDialog) {
            TransactionOptionsDialog(
                onEdit = {
                    onEdit()
                    showOptionsDialog = false
                },
                onDelete = {
                    onDelete()
                    showOptionsDialog = false
                },
                onDismiss = { showOptionsDialog = false }
            )
        }
    }
}

@Composable
fun TransactionOptionsDialog(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transaction Options") },
        text = { Text("Choose an action for this transaction.") },
        confirmButton = {
            TextButton(onClick = onEdit) { Text("Edit") }
        },
        dismissButton = {
            TextButton(onClick = onDelete) { Text("Delete", color = OrioExpense) }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this transaction?") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Delete", color = OrioExpense) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun TransactionsScreenContentPopulatedPreview() {
    OrioTheme {
        TransactionsScreenContent(
            filterState = TransactionFilterState(),
            filteredTransactions = listOf(
                TransactionDomain(
                    transactionId = 1,
                    amount = 500.0,
                    category = "food",
                    date = System.currentTimeMillis(),
                    note = "Lunch",
                    type = com.quickthought.orio.domain.model.TransactionType.EXPENSE
                ),
                TransactionDomain(
                    transactionId = 2,
                    amount = 1200.0,
                    category = "shopping",
                    date = System.currentTimeMillis(),
                    note = "New shoes",
                    type = com.quickthought.orio.domain.model.TransactionType.EXPENSE
                )
            ),
            editingTransaction = null,
            onFilterChange = {},
            onAddTransaction = {},
            onEditTransactionSelected = {},
            onUpdateTransaction = {},
            onDeleteTransaction = {}
        )
    }
}

@Preview(name = "Empty - Large Font", fontScale = 1.5f)
@Preview(name = "Empty - Dynamic Color", showBackground = true)
@Composable
private fun TransactionsScreenContentEmptyPreview() {
    OrioTheme {
        TransactionsScreenContent(
            filterState = TransactionFilterState(),
            filteredTransactions = emptyList(),
            editingTransaction = null,
            onFilterChange = {},
            onAddTransaction = {},
            onEditTransactionSelected = {},
            onUpdateTransaction = {},
            onDeleteTransaction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwipeableTransactionItemPreview() {
    OrioTheme {
        SwipeableTransactionItem(
            transaction = TransactionDomain(
                amount = 100.0,
                type = com.quickthought.orio.domain.model.TransactionType.EXPENSE,
                category = "food",
                date = System.currentTimeMillis(),
                note = "Coffee"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}
