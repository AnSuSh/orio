package com.quickthought.orio.presentation.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import com.quickthought.orio.ui.theme.OrioTheme

@Composable
fun AnalyticsContent(categorySpending: Map<String, Double>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Category Breakdown", style = MaterialTheme.typography.titleLarge)
        }
        item {
            CategoryPieChart(
                data = categorySpending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }
        items(categorySpending.toList()) { (category, amount) ->
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
    }
}

@PreviewLightDark
@Composable
private fun AnalyticsPreview() {
    OrioTheme {
        AnalyticsContent(
            categorySpending = mapOf(
                "Food" to 100.0,
                "Transport" to 200.0,
                "Entertainment" to 150.0,
                "Health" to 300.0,
                "Other" to 50.0,
            )
        )
    }
}