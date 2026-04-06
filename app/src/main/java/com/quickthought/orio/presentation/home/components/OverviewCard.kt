package com.quickthought.orio.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quickthought.orio.ui.theme.ExpenseRed
import com.quickthought.orio.ui.theme.IncomeGreen
import java.util.Locale

@Composable
fun BalanceOverview(
    balance: Double,
    income: Double,
    expense: Double
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total Balance", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "₹${String.format(Locale.getDefault(), "%.2f", balance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                SummaryItem(
                    label = "Income",
                    amount = income,
                    color = IncomeGreen, // Your custom color
                    modifier = Modifier.weight(1f)
                )
                SummaryItem(
                    label = "Expense",
                    amount = expense,
                    color = ExpenseRed, // Your custom color
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, amount: Double, color: Color, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(
            "₹${String.format(Locale.getDefault(), "%.2f", amount)}",
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}

@Preview
@Composable
private fun BalanceOverviewPrev() {
    BalanceOverview(
        balance = 1250.0,
        income = 5000.0,
        expense = 3750.0
    )
}

@Preview
@Composable
private fun SummaryPreview() {
    SummaryItem(
        label = "Income",
        amount = 5000.0,
        color = IncomeGreen,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun BalancePreDark() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        BalanceOverview(
            balance = 1250.0,
            income = 5000.0,
            expense = 3750.0
        )
    }
}