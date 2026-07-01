package com.quickthought.orio.presentation.analytics.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quickthought.orio.ui.theme.OrioTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecentSpendingChart(
    dailySpending: List<Pair<Long, Double>>,
    modifier: Modifier = Modifier
) {
    var selectedDayIndex by remember { mutableIntStateOf(-1) }

    // Remember the max value to avoid recalculation on every recomposition
    val maxSpending = remember(dailySpending) {
        (dailySpending.maxOfOrNull { it.second } ?: 0.0).coerceAtLeast(1.0)
    }

    // Use NumberFormat for localized currency and remember the formatter
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).apply {
            maximumFractionDigits = 0
        }
    }
    val dateFormatter = remember { SimpleDateFormat("EEE", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Last 7 Days Spending",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            dailySpending.forEachIndexed { index, pair ->
                // Calculate percentage, but ensure a visible minimum for zero values
                val heightPercent = (pair.second / maxSpending).toFloat()

                // Use a spring animation for a more natural feel
                val animatedHeight by animateFloatAsState(
                    targetValue = heightPercent,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "barHeight"
                )

                val isSelected = selectedDayIndex == index

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            selectedDayIndex = if (isSelected) -1 else index
                        }
                        .semantics {
                            contentDescription =
                                "Spending on ${dateFormatter.format(Date(pair.first))}: ${pair.second}"
                        }
                ) {
                    // Selection Label Space
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (isSelected) {
                            Text(
                                text = currencyFormatter.format(pair.second),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    // Bar Space
                    Box(
                        modifier = Modifier
                            .height(140.dp) // Fixed height for the bar area
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Background track for the bar to give context for zero-spending days
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(4.dp) // Small indicator for zero values
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        )

                        // The actual spending bar
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .fillMaxHeight(animatedHeight.coerceAtLeast(0.0f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primaryContainer
                                )
                        )
                    }

                    // X-Axis Label
                    Text(
                        text = dateFormatter.format(Date(pair.first)),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentSpendingChartPreview() {
    val dummyData = listOf(
        System.currentTimeMillis() - 6 * 24 * 3600 * 1000L to 1200.0,
        System.currentTimeMillis() - 5 * 24 * 3600 * 1000L to 00.0,
        System.currentTimeMillis() - 4 * 24 * 3600 * 1000L to 2500.0,
        System.currentTimeMillis() - 3 * 24 * 3600 * 1000L to 1500.0,
        System.currentTimeMillis() - 2 * 24 * 3600 * 1000L to 3000.0,
        System.currentTimeMillis() - 1 * 24 * 3600 * 1000L to 400.0,
        System.currentTimeMillis() to 900.0,
    )
    OrioTheme {
        RecentSpendingChart(dailySpending = dummyData)
    }
}
