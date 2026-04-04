package com.quickthought.orio.presentation.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionType
import com.quickthought.orio.ui.theme.ExpenseRed
import com.quickthought.orio.ui.theme.IncomeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (TransactionDomain) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentWindowInsets = { WindowInsets.ime }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .fillMaxWidth()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "New Transaction",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Amount Field
            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
                label = { Text("Amount") },
                textStyle = MaterialTheme.typography.titleLarge,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                prefix = { Text("₹", style = MaterialTheme.typography.titleLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                shape = MaterialTheme.shapes.medium,
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
//                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
//                )
            )

            // Type Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    modifier = Modifier
                        .weight(1f),
                    selected = !isIncome,
                    onClick = { isIncome = false },
                    label = {
                        Text(
                            "Expense",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
//                    border = FilterChipDefaults.filterChipBorder(
//                    colors = FilterChipDefaults.filterChipColors(
//                        selectedContainerColor = ExpenseRed.copy(alpha = 0.1f),
//                        selectedLabelColor = MaterialTheme.colorScheme.onSurface
//                    )
                )
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = isIncome,
                    onClick = { isIncome = true },
                    label = {
                        Text(
                            "Income",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
//                    colors = FilterChipDefaults.filterChipColors(
//                        selectedContainerColor = IncomeGreen.copy(alpha = 0.1f),
//                        selectedLabelColor = MaterialTheme.colorScheme.onSurface
//                    )
                )
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note / Description") },
                placeholder = { Text("e.g. Groceries") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
//                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
//                )
            )

            Button(
                onClick = {
                    val valAmount = amount.toDoubleOrNull() ?: 0.0
                    onSave(
                        TransactionDomain(
                            type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
                            amount = valAmount,
                            note = note,
                            category = "",
                            date = System.currentTimeMillis()
                        )
                    )
                    focusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = (amount.toDoubleOrNull() != null) && (amount.toDouble() > 0),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Save Transaction", style = MaterialTheme.typography.bodyLarge)
            }
        }

        // Auto-focus amount field when sheet opens
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}