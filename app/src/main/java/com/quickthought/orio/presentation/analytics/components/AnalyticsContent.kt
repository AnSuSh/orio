package com.quickthought.orio.presentation.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.quickthought.orio.presentation.analytics.AnalyticsState
import com.quickthought.orio.ui.theme.OrioTheme

@Composable
fun AnalyticsContent(state: AnalyticsState, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        item {
            IncomeExpenseBalanceBar(
                income = state.totalIncome,
                expense = state.totalExpense,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Category Breakdown", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryPieChart(
                        data = state.categorySpending,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (state.dailySpending.isNotEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    RecentSpendingChart(
                        dailySpending = state.dailySpending,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        item {
            Text(text = "Details", style = MaterialTheme.typography.titleMedium)
        }

        items(state.categorySpending.toList()) { (category, amount) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = category)
                    Text(text = "₹ $amount", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        item {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun AnalyticsPreview() {
    OrioTheme {
        AnalyticsContent(
            state = AnalyticsState(
                categorySpending = mapOf(
                    "Food" to 100.0,
                    "Transport" to 200.0,
                    "Entertainment" to 150.0,
                    "Health" to 300.0,
                    "Other" to 50.0,
                ),
                totalIncome = 5000.0,
                totalExpense = 800.0,
                dailySpending = (1..7).map { System.currentTimeMillis() - it * 24 * 3600 * 1000L to it * 100.0 }
            )
        )
    }
}
