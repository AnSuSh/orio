package com.quickthought.orio.presentation.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickthought.orio.domain.model.AccountDomain
import com.quickthought.orio.domain.model.DebtDomain
import com.quickthought.orio.presentation.accounts.components.AddAccountSheet
import com.quickthought.orio.presentation.util.OrioTopAppBar
import com.quickthought.orio.ui.components.AdaptiveWrapper
import com.quickthought.orio.ui.theme.OrioIncome
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier,
        topBar = {
            OrioTopAppBar("Accounts & Splits")
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Account")
            }
        }
    ) { innerPadding ->
        AdaptiveWrapper(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TotalBalanceCard(
                        balance = state.totalBalance,
                        receivables = state.totalReceivables
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "My Accounts",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(state.accounts, key = { "acc_${it.id}" }) { account ->
                    AccountItem(account = account)
                }

                if (state.debts.any { !it.isPaid }) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pending Receivables",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(state.debts.filter { !it.isPaid }, key = { "debt_${it.id}" }) { debt ->
                        DebtItem(
                            debt = debt,
                            onMarkAsPaid = { viewModel.markDebtAsPaid(debt) }
                        )
                    }
                }

                if (state.debts.any { it.isPaid }) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Past Payments",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(state.debts.filter { it.isPaid }, key = { "paid_debt_${it.id}" }) { debt ->
                        DebtItem(
                            debt = debt,
                            onMarkAsPaid = {}
                        )
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddAccountSheet(
            sheetState = sheetState,
            onDismiss = { showAddSheet = false },
            onSave = {
                viewModel.addAccount(it)
                showAddSheet = false
            }
        )
    }
}

@Composable
fun TotalBalanceCard(balance: Double, receivables: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Assets",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = String.format(Locale.getDefault(), "₹ %.2f", balance + receivables),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (receivables > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Cash: ₹ %.2f".format(balance),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Receivables: ₹ %.2f".format(receivables),
                        style = MaterialTheme.typography.bodySmall,
                        color = OrioIncome
                    )
                }
            }
        }
    }
}

@Composable
fun AccountItem(account: AccountDomain) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = account.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = String.format(Locale.getDefault(), "₹ %.2f", account.balance),
                style = MaterialTheme.typography.titleLarge,
                color = if (account.balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun DebtItem(debt: DebtDomain, onMarkAsPaid: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = debt.personName ?: "Split Bill",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (debt.isPaid) "Resolved" else "Pending",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (debt.isPaid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                )
            }
            if (debt.isPaid){
                Text(
                    text = String.format(Locale.getDefault(), "₹ %.2f", debt.amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = OrioIncome
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else{
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format(Locale.getDefault(), "₹ %.2f", debt.amount),
                        style = MaterialTheme.typography.titleMedium,
                        color = OrioIncome
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onMarkAsPaid,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Resolve", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
