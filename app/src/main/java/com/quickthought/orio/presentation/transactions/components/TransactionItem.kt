package com.quickthought.orio.presentation.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickthought.orio.domain.model.Category
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.transactionCategories
import com.quickthought.orio.ui.theme.ExpenseRed
import com.quickthought.orio.ui.theme.IncomeGreen

@Composable
fun TransactionItem(
    transaction: TransactionDomain,
    modifier: Modifier = Modifier
) {

    val category = getCategoryById(transaction.category)

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max), // Important to make fillMaxHeight work for children
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp) // Professional width
                    .fillMaxHeight() // Ensures it stretches top to bottom
                    .background(category.color)
            )

            Row(
                modifier = Modifier
                    .padding(start = 8.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon with Secondary Container background
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))


                Column(Modifier.weight(1f)) {
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = transaction.dateTimeString, // Use your date formatting logic here
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${if (transaction.isIncome) "+" else "-"} ${transaction.localizedPriceString}",
                    style = MaterialTheme.typography.titleLarge,
                    // Using your Primary for Income and Error for Expense
                    color = if (transaction.isIncome) IncomeGreen else ExpenseRed
                )
            }
        }
    }
}

@Composable
fun getCategoryById(id: String): Category {
    return transactionCategories.find { it.id == id } ?: transactionCategories.last()
}