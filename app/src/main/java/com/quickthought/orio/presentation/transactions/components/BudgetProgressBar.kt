package com.quickthought.orio.presentation.transactions.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.math.max

@Composable
fun BudgetProgressSection(
    totalExpenses: Double,
    daysLeftInMonth: Int,
    modifier: Modifier = Modifier,
    monthlyBudget: Double = 500000.0,
) {
    // Calculate Progress
    val progressRaw = (totalExpenses / monthlyBudget).toFloat().coerceIn(0f, 1f)

    // Animate Progress on load
    val animatedProgress by animateFloatAsState(
        targetValue = progressRaw,
        animationSpec = tween(durationMillis = 1000),
        label = "budgetProgress"
    )

    // Dynamic Coloring Logic
    val targetColor = when {
        progressRaw < 0.5f -> MaterialTheme.colorScheme.secondary // Green-ish (OrioSecondary)
        progressRaw < 0.8f -> MaterialTheme.colorScheme.primary   // Blue (OrioPrimary)
        else -> MaterialTheme.colorScheme.tertiary               // Red (OrioTertiary)
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 500),
        label = "progressColor"
    )

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Monthly Budget",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹${totalExpenses.toInt()} / ₹${monthlyBudget.toInt()}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = animatedColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
            )

            // 5. Nudge Text with AnimatedContent
            val remainingBudget = max(0.0, monthlyBudget - totalExpenses)
            val dailyRemaining = remainingBudget / daysLeftInMonth

            AnimatedContent(
                targetState = dailyRemaining,
                label = "nudgeTextAnimation"
            ) { amount ->
                Text(
                    text = if (amount > 0) {
                        "You can spend ₹${
                            String.format(
                                Locale.getDefault(),
                                "%.0f",
                                amount
                            )
                        } daily for the next $daysLeftInMonth days."
                    } else {
                        "Budget exceeded! Try to limit spending."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (amount > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}