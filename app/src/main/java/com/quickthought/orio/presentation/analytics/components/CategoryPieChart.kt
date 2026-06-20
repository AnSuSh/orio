package com.quickthought.orio.presentation.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.quickthought.orio.ui.theme.OrioTheme

@Composable
fun CategoryPieChart(data: Map<String, Double>, modifier: Modifier = Modifier) {
    val colors = listOf(Color.Blue, Color.Green, Color.Red, Color.Yellow, Color.Cyan, Color.Magenta)
    val total = data.values.sum()

    Canvas(modifier = modifier) {
        var startAngle = 0f
        data.values.forEachIndexed { index, value ->
            val sweepAngle = (value / total * 360f).toFloat()
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.minDimension, size.minDimension),
                topLeft = Offset((size.width - size.minDimension) / 2, 0f)
            )
            startAngle += sweepAngle
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryPieChartPreview() {
    OrioTheme {
        CategoryPieChart(
            data = mapOf(
                "Food" to 100.0,
                "Transport" to 200.0,
                "Entertainment" to 150.0
            )
        )
    }
}