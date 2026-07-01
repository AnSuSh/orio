package com.quickthought.orio.presentation.analytics.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quickthought.orio.ui.theme.OrioTheme

@Composable
fun IncomeExpenseBalanceBar(
    income: Double,
    expense: Double,
    modifier: Modifier = Modifier
) {
    var showPercentage by remember { mutableStateOf(false) }
    val total = income + expense
    val incomeWeight = if (total > 0) (income / total).toFloat() else 0.5f
    val expenseWeight = if (total > 0) (expense / total).toFloat() else 0.5f

    val animatedIncomeWeight by animateFloatAsState(
        targetValue = incomeWeight,
        label = "incomeWeight"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showPercentage = !showPercentage }
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Income",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = if (showPercentage) "${
                        String.format(
                            java.util.Locale.getDefault(),
                            "%.1f",
                            incomeWeight * 100
                        )
                    }%" else "₹${String.format(java.util.Locale.getDefault(), "%.0f", income)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34A853)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Expense",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = if (showPercentage) "${
                        String.format(
                            java.util.Locale.getDefault(),
                            "%.1f",
                            expenseWeight * 100
                        )
                    }%" else "₹${String.format(java.util.Locale.getDefault(), "%.0f", expense)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEA4335)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(if (animatedIncomeWeight > 0f) animatedIncomeWeight else 0.0001f)
                        .background(Color(0xFF34A853))
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(if (1f - animatedIncomeWeight > 0f) 1f - animatedIncomeWeight else 0.0001f)
                        .background(Color(0xFFEA4335))
                )
            }
        }

        Text(
            text = if (income > expense) "Positive Cashflow" else "High Spending",
            style = MaterialTheme.typography.labelSmall,
            color = if (income > expense) Color(0xFF34A853) else Color(0xFFEA4335),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IncomeExpenseBalanceBarPreview() {
    OrioTheme {
        IncomeExpenseBalanceBar(income = 5000.0, expense = 3000.0)
    }
}
