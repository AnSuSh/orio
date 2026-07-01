package com.quickthought.orio.presentation.analytics.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FloatExponentialDecaySpec
import androidx.compose.animation.core.generateDecayAnimationSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.quickthought.orio.ui.theme.OrioTheme
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun CategoryPieChart(data: Map<String, Double>, modifier: Modifier = Modifier) {
    val colors = listOf(
        Color(0xFF4285F4), Color(0xFF34A853), Color(0xFFFBBC05),
        Color(0xFFEA4335), Color(0xFF46BDC6), Color(0xFFFF6D01)
    )
    val total = data.values.sum()
    val slices = data.toList()

    var selectedIndex by remember { mutableIntStateOf(-1) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val velocityTracker = remember { VelocityTracker() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize() // Use fillMaxSize inside the aspect-ratio Box
                .pointerInput(total, rotation.value, slices) {
                    detectTapGestures { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f

                        val dx = offset.x - centerX
                        val dy = offset.y - centerY
                        val distance = sqrt(dx.pow(2) + dy.pow(2))
                        val radius = minOf(size.width, size.height) / 2f

                        // We check distance first to avoid complex math for taps far outside the circle
                        // However, we must account for the 15dp "explosion" offset
                        val explosionBuffer = 15.dp.toPx()
                        if (distance > radius + explosionBuffer) {
                            selectedIndex = -1
                            return@detectTapGestures
                        }

                        // Get the angle of the touch (0 to 360)
                        var touchAngle =
                            Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                        if (touchAngle < 0) touchAngle += 360f

                        // Adjust for current chart rotation to find the "original" slice angle
                        val currentRotation = (rotation.value % 360f + 360f) % 360f
                        val adjustedAngle = (touchAngle - currentRotation + 360f) % 360f

                        var startAngle = 0f
                        var foundIndex = -1

                        for (index in slices.indices) {
                            val sweepAngle = (slices[index].second / total * 360f).toFloat()
                            val endAngle = startAngle + sweepAngle

                            if (adjustedAngle >= startAngle && adjustedAngle < endAngle) {
                                // Double check the distance for the specific slice, especially if it's exploded
                                val isPreviouslySelected = index == selectedIndex
                                val offsetDistance =
                                    if (isPreviouslySelected) explosionBuffer else 0f

                                // Calculate the mid-angle in absolute coordinate space (with rotation)
                                val midAngleAbsolute =
                                    Math.toRadians((startAngle + sweepAngle / 2f + currentRotation).toDouble())
                                val sliceCenterX =
                                    centerX + cos(midAngleAbsolute).toFloat() * offsetDistance
                                val sliceCenterY =
                                    centerY + sin(midAngleAbsolute).toFloat() * offsetDistance

                                val distToSliceCenter = sqrt(
                                    (offset.x - sliceCenterX).pow(2) + (offset.y - sliceCenterY).pow(
                                        2
                                    )
                                )

                                if (distToSliceCenter <= radius) {
                                    foundIndex = index
                                }
                                break
                            }
                            startAngle += sweepAngle
                        }
                        selectedIndex = if (selectedIndex == foundIndex) -1 else foundIndex
                    }
                }
                .pointerInput(Unit) {
                    val decay = FloatExponentialDecaySpec().generateDecayAnimationSpec<Float>()
                    detectDragGestures(
                        onDragStart = {
                            velocityTracker.resetTracking()
                        },
                        onDrag = { change, dragAmount ->
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f

                            val startPos = change.position - dragAmount
                            val endPos = change.position

                            val angleStart = Math.toDegrees(
                                atan2(
                                    (startPos.y - centerY).toDouble(),
                                    (startPos.x - centerX).toDouble()
                                )
                            ).toFloat()
                            val angleEnd = Math.toDegrees(
                                atan2(
                                    (endPos.y - centerY).toDouble(),
                                    (endPos.x - centerX).toDouble()
                                )
                            ).toFloat()

                            var dAngle = angleEnd - angleStart
                            if (dAngle > 180f) dAngle -= 360f
                            if (dAngle < -180f) dAngle += 360f

                            scope.launch {
                                rotation.snapTo(rotation.value + dAngle)
                            }
                            velocityTracker.addPosition(change.uptimeMillis, change.position)
                        },
                        onDragEnd = {
                            val velocity = velocityTracker.calculateVelocity()
                            // Map linear velocity to angular velocity roughly
                            val angularVelocity = (velocity.x + velocity.y) / 2f

                            scope.launch {
                                rotation.animateDecay(angularVelocity, decay)
                            }
                        }
                    )
                }
        ) {
            val rectSize = Size(size.minDimension, size.minDimension)
            val topLeft =
                Offset((size.width - size.minDimension) / 2, (size.height - size.minDimension) / 2)

            rotate(rotation.value) {
                var startAngle = 0f
                slices.forEachIndexed { index, pair ->
                    val sweepAngle = (pair.second / total * 360f).toFloat()
                    val isSelected = index == selectedIndex

                    // Exploding effect: offset the selected slice
                    val midAngle = startAngle + sweepAngle / 2f
                    val midAngleRad = Math.toRadians(midAngle.toDouble())
                    val offsetDistance = if (isSelected) 15.dp.toPx() else 0f
                    val offsetX = cos(midAngleRad).toFloat() * offsetDistance
                    val offsetY = sin(midAngleRad).toFloat() * offsetDistance

                    drawArc(
                        color = colors[index % colors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = rectSize,
                        topLeft = Offset(topLeft.x + offsetX, topLeft.y + offsetY),
                        alpha = if (selectedIndex == -1 || isSelected) 1f else 0.4f
                    )

                    if (isSelected) {
                        drawArc(
                            color = Color.White,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            size = rectSize,
                            topLeft = Offset(topLeft.x + offsetX, topLeft.y + offsetY),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    startAngle += sweepAngle
                }
            }
        }

        if (selectedIndex != -1) {
            val selectedSlice = slices[selectedIndex]
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = selectedSlice.first,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "₹${String.format(Locale.getDefault(), "%.2f", selectedSlice.second)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${
                        String.format(
                            Locale.getDefault(),
                            "%.1f",
                            (selectedSlice.second / total) * 100
                        )
                    }%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Spending",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "₹${String.format(Locale.getDefault(), "%.2f", total)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun CategoryPieChartPreview() {
    OrioTheme {
        CategoryPieChart(
            data = mapOf(
                "Food" to 1500.0,
                "Transport" to 800.0,
                "Entertainment" to 1200.0,
                "Shopping" to 2000.0,
                "Health" to 500.0
            ),
            modifier = Modifier
                .padding(32.dp)
        )
    }
}
