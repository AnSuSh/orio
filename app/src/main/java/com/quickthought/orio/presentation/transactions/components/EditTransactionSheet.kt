package com.quickthought.orio.presentation.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.quickthought.orio.domain.model.TrackingMethod
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionType
import com.quickthought.orio.domain.model.transactionCategories
import com.quickthought.orio.ui.theme.OrioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionSheet(
    transaction: TransactionDomain,
    categories: List<com.quickthought.orio.domain.model.Category>,
    onDismiss: () -> Unit,
    onSave: (TransactionDomain, Boolean, Boolean) -> Unit
) {
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var note by remember { mutableStateOf(transaction.note) }
    var isIncome by remember { mutableStateOf(transaction.isIncome) }
    var selectedCategory by remember {
        mutableStateOf(
            categories.find { it.id == transaction.category }
                ?: transactionCategories.find { it.id == transaction.category }
                ?: categories.firstOrNull()
                ?: transactionCategories.last()
        )
    }
    var selectedDate by remember { mutableLongStateOf(transaction.date) }
    var isInvestment by remember { mutableStateOf(false) }
    var isSelfTransfer by remember { mutableStateOf(false) }
    var showSelfTransferWarning by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    ModalBottomSheet(onDismissRequest = onDismiss) {
        EditTransactionContent(
            transaction = transaction,
            amount = amount,
            onAmountChange = { amount = it },
            note = note,
            onNoteChange = { note = it },
            isIncome = isIncome,
            onTypeChange = { isIncome = it },
            selectedCategory = selectedCategory,
            categories = categories,
            onCategoryChange = { selectedCategory = it },
            selectedDate = selectedDate,
            onDateClick = { showDatePicker = true },
            isInvestment = isInvestment,
            onInvestmentToggle = { isInvestment = it },
            isSelfTransfer = isSelfTransfer,
            onSelfTransferToggle = {
                if (it) {
                    showSelfTransferWarning = true
                } else {
                    isSelfTransfer = false
                }
            },
            onSave = {
                val updated = transaction.copy(
                    amount = amount.toDoubleOrNull() ?: transaction.amount,
                    note = note,
                    type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
                    category = selectedCategory.id,
                    date = selectedDate
                )
                onSave(updated, isInvestment, isSelfTransfer)
            }
        )

        if (showSelfTransferWarning) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showSelfTransferWarning = false },
                title = { Text("Self Transfer") },
                text = { Text("Self transfers will not be recorded as income or expense. This action will delete this transaction.") },
                confirmButton = {
                    TextButton(onClick = {
                        isSelfTransfer = true
                        showSelfTransferWarning = false
                    }) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isSelfTransfer = false
                        showSelfTransferWarning = false
                    }) { Text("Cancel") }
                }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedDate = datePickerState.selectedDateMillis ?: selectedDate
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun EditTransactionContent(
    transaction: TransactionDomain,
    amount: String,
    onAmountChange: (String) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    isIncome: Boolean,
    onTypeChange: (Boolean) -> Unit,
    selectedCategory: com.quickthought.orio.domain.model.Category,
    categories: List<com.quickthought.orio.domain.model.Category>,
    onCategoryChange: (com.quickthought.orio.domain.model.Category) -> Unit,
    selectedDate: Long,
    onDateClick: () -> Unit,
    isInvestment: Boolean,
    onInvestmentToggle: (Boolean) -> Unit,
    isSelfTransfer: Boolean,
    onSelfTransferToggle: (Boolean) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        Text(
            "Edit Transaction", style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (transaction.trackingMethod == TrackingMethod.AUTO_SMS) {
            Spacer(modifier = Modifier.height(12.dp))
            SmsSourceInfoCard(rawMessage = transaction.rawMessage)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TransactionAmountField(
            amount = amount,
            onAmountChange = onAmountChange,
            focusRequester = remember { FocusRequester() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransactionTypeSelector(
            isIncome = isIncome,
            onTypeChange = onTypeChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransactionCategorySelector(
            selectedCategory = selectedCategory,
            categories = categories,
            onCategoryChange = onCategoryChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransactionDatePickerCard(
            selectedDate = selectedDate,
            onClick = onDateClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransactionFlagsSection(
            isInvestment = isInvestment,
            onInvestmentToggle = onInvestmentToggle,
            isSelfTransfer = isSelfTransfer,
            onSelfTransferToggle = onSelfTransferToggle
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Note / Description") },
            placeholder = { Text("e.g. Groceries") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Update Transaction")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsSourceInfoCard(rawMessage: String?) {
    var showRawMessage by remember { mutableStateOf(false) }
    Card(
        onClick = { showRawMessage = !showRawMessage },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (showRawMessage) "Scanned Message" else "Auto-tracked from SMS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (showRawMessage) "Hide" else "View Message",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }

            if (showRawMessage) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rawMessage ?: "Original message not available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Note: SMS structures vary by bank. Please verify these details to ensure accuracy.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TransactionFlagsSection(
    isInvestment: Boolean,
    onInvestmentToggle: (Boolean) -> Unit,
    isSelfTransfer: Boolean,
    onSelfTransferToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.material3.FilterChip(
            modifier = Modifier.weight(1f),
            selected = isInvestment,
            onClick = { onInvestmentToggle(!isInvestment) },
            label = {
                Text(
                    "Investment",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
        )
        androidx.compose.material3.FilterChip(
            modifier = Modifier.weight(1f),
            selected = isSelfTransfer,
            onClick = { onSelfTransferToggle(!isSelfTransfer) },
            label = {
                Text(
                    "Self Transfer",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun EditTransactionContentPreview() {
    OrioTheme {
        EditTransactionContent(
            transaction = TransactionDomain(
                amount = 1200.0,
                type = TransactionType.EXPENSE,
                note = "New shoes",
                category = "shopping",
                date = System.currentTimeMillis(),
                trackingMethod = TrackingMethod.AUTO_SMS,
                rawMessage = "Debit of 1200 INR at ZARA"
            ),
            amount = "1200",
            onAmountChange = {},
            note = "New shoes",
            onNoteChange = {},
            isIncome = false,
            onTypeChange = {},
            selectedCategory = transactionCategories.first { it.id == "shopping" },
            categories = emptyList(),
            onCategoryChange = {},
            selectedDate = System.currentTimeMillis(),
            onDateClick = {},
            isInvestment = false,
            onInvestmentToggle = {},
            isSelfTransfer = false,
            onSelfTransferToggle = {},
            onSave = {}
        )
    }
}

@Preview(name = "SMS Info Card - Expanded", showBackground = true)
@Composable
private fun SmsSourceInfoCardPreview() {
    OrioTheme {
        SmsSourceInfoCard(rawMessage = "Debit of 1200 INR at ZARA")
    }
}
