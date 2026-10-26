package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimpleVitalsTrendChart(
    title: String,
    dataPoints: List<Float>,
    dates: List<String>,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    unit: String = "",
    targetMin: Float? = null,
    targetMax: Float? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                if (dataPoints.isNotEmpty()) {
                    Text(
                        text = "Latest: ${dataPoints.last().toInt()} $unit",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = lineColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (dataPoints.size < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log at least 2 entries to view trend chart",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val minVal = (dataPoints.minOrNull() ?: 0f).coerceAtLeast(0f)
                val maxVal = (dataPoints.maxOrNull() ?: 100f).coerceAtLeast(minVal + 10f)
                val range = (maxVal - minVal).coerceAtLeast(1f)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val paddingBottom = 20.dp.toPx()
                    val chartHeight = h - paddingBottom

                    // Draw baseline grid lines
                    val gridLines = 3
                    for (i in 0..gridLines) {
                        val y = chartHeight * (i.toFloat() / gridLines)
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.4f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Build path
                    val path = Path()
                    val points = mutableListOf<Offset>()
                    val stepX = w / (dataPoints.size - 1)

                    dataPoints.forEachIndexed { index, value ->
                        val x = index * stepX
                        val normalized = (value - minVal) / range
                        val y = chartHeight - (normalized * (chartHeight - 16.dp.toPx())) - 8.dp.toPx()
                        points.add(Offset(x, y))
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }

                    // Draw line
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw dots on data points
                    points.forEach { point ->
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = lineColor,
                            radius = 3.5.dp.toPx(),
                            center = point
                        )
                    }
                }

                // Date axis
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    if (dates.isNotEmpty()) {
                        Text(dates.first(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (dates.size > 2) {
                            Text(dates[dates.size / 2], fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(dates.last(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
