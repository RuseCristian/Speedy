package com.example.speedy.ui.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.rotate
import kotlin.math.roundToInt
import kotlin.math.sin

class ChartLine(
    val xValues: List<Float>,
    val yValues: List<Float>,
    val color: Color
)

fun Path.catmullRomTo(points: List<Offset>, tension: Float = 0.3f) {
    if (points.size < 2) return

    for (i in 1 until points.size - 1) {
        val p0 = points[i - 1]
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = if (i + 2 < points.size) points[i + 2] else p2

        val t1x = (p2.x - p0.x) * tension
        val t1y = (p2.y - p0.y) * tension
        val t2x = (p3.x - p1.x) * tension
        val t2y = (p3.y - p1.y) * tension

        cubicTo(
            p1.x + t1x / 3f, p1.y + t1y / 3f,
            p2.x - t2x / 3f, p2.y - t2y / 3f,
            p2.x, p2.y
        )
    }
}

@Composable
fun LineGraph(
    chartLines: List<ChartLine>,
    title: String = "Plot Title",
    xAxisLabel: String = "X-Axis",
    yAxisLabel: String = "Y-Axis",
    showTitle: Boolean = true,
    showXAxisLabel: Boolean = true,
    showYAxisLabel: Boolean = true,
    showGrid: Boolean = true,
    smoothLines: Boolean = false,
    modifier: Modifier = Modifier,
    maxPoints: Int = 500,
    legendLabels: List<String> = emptyList() // Add legend labels corresponding to chartLines
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Title (Centered relative to the graph area)
        if (showTitle) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp), // Add spacing between title and graph
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f)
        ) {
            // Y-Axis Label (Optional and closer to the graph)
            if (showYAxisLabel) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = yAxisLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.rotate(-90f)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Graph Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        val graphPadding = 40f // Increased padding for better visibility
                        val graphWidth = width - 2 * graphPadding
                        val graphHeight = height - 2 * graphPadding

                        // Calculate axis ranges
                        val xMin = chartLines.minOfOrNull { it.xValues.minOrNull() ?: 0f } ?: 0f
                        val xMax = chartLines.maxOfOrNull { it.xValues.maxOrNull() ?: 1f } ?: 1f
                        val yMin = chartLines.minOfOrNull { it.yValues.minOrNull() ?: 0f } ?: 0f
                        val yMax = chartLines.maxOfOrNull { it.yValues.maxOrNull() ?: 1f } ?: 1f

                        val xRange = xMax - xMin
                        val yRange = yMax - yMin

                        // Draw gridlines and ticks
                        val gridSteps = 5
                        val xStep = xRange / gridSteps
                        val yStep = yRange / gridSteps

                        for (i in 0..gridSteps) {
                            // Horizontal gridlines and ticks
                            val y = graphPadding + graphHeight - (graphHeight / gridSteps) * i
                            if (showGrid) {
                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    start = Offset(graphPadding, y),
                                    end = Offset(width - graphPadding, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            drawContext.canvas.nativeCanvas.drawText(
                                "${(yMin + yStep * i).roundToInt()}",
                                graphPadding - 12f,
                                y,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    textSize = 28f // Visible font size
                                    textAlign = android.graphics.Paint.Align.RIGHT
                                }
                            )

                            // Vertical gridlines and ticks
                            val x = graphPadding + (graphWidth / gridSteps) * i
                            if (showGrid) {
                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    start = Offset(x, graphPadding),
                                    end = Offset(x, height - graphPadding),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            drawContext.canvas.nativeCanvas.drawText(
                                "${(xMin + xStep * i).roundToInt()}",
                                x,
                                height - graphPadding + 24f,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    textSize = 28f // Visible font size
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                        }

                        // Draw axes
                        drawLine(
                            color = Color.Black,
                            start = Offset(graphPadding, graphPadding),
                            end = Offset(graphPadding, height - graphPadding),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            color = Color.Black,
                            start = Offset(graphPadding, height - graphPadding),
                            end = Offset(width - graphPadding, height - graphPadding),
                            strokeWidth = 2.dp.toPx()
                        )

                        // Draw chart lines
                        chartLines.forEach { chartLine ->
                            val path = Path()
                            val totalPoints = chartLine.xValues.size
                            val step = if (totalPoints > maxPoints) totalPoints / maxPoints else 1
                            val sampledPoints = chartLine.xValues.filterIndexed { i, _ -> i % step == 0 }
                                .zip(chartLine.yValues.filterIndexed { i, _ -> i % step == 0 }) { x, y ->
                                    Offset(
                                        graphPadding + ((x - xMin) / xRange) * graphWidth,
                                        graphPadding + graphHeight - ((y - yMin) / yRange) * graphHeight
                                    )
                                }

                            if (sampledPoints.isNotEmpty()) {
                                path.moveTo(sampledPoints[0].x, sampledPoints[0].y)
                                if (smoothLines) {
                                    path.catmullRomTo(sampledPoints) // Call the smoothing function
                                } else {
                                    for (i in 1 until sampledPoints.size) {
                                        path.lineTo(sampledPoints[i].x, sampledPoints[i].y)
                                    }
                                }
                            }

                            drawPath(path, color = chartLine.color, style = Stroke(2.dp.toPx()))
                        }
                    }
                }

                // X-Axis Label (Optional and closer to the graph)
                if (showXAxisLabel) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp), // Space between graph and label
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = xAxisLabel,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Legend (Display below the graph)
        if (legendLabels.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                legendLabels.forEachIndexed { index, label ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp) // Reduced padding between legend items
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp) // Smaller size for legend icon
                                .background(chartLines[index].color)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            fontSize = 10.sp, // Smaller font size for legend labels
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExampleGraph() {
    val simpleLine = ChartLine(
        xValues = listOf(0f, 1f, 2f, 3f, 4f),
        yValues = listOf(0f, 0f, 0f, 9f, 0f),
        color = Color.Blue
    )

    val anotherLine = ChartLine(
        xValues = listOf(0f, 1f, 2f, 3f, 4f),
        yValues = listOf(0f, 2f, 4f, 6f, 8f),
        color = Color.Red
    )

    val anotherLineF2 = ChartLine(
        xValues = listOf(0f, 1f, 2f, 3f, 4f),
        yValues = listOf(0f, 1f, -1f, 1f, -1f),
        color = Color.Green
    )

    // Define legend labels corresponding to the chart lines
    val legendLabels = listOf("Simple Line", "Another Line", "Fluctuating Line")

    Box(modifier = Modifier.height(300.dp).padding(16.dp)) {
        LineGraph(
            chartLines = listOf(simpleLine, anotherLine, anotherLineF2),
            title = "Example Graph",
            xAxisLabel = "X-Axis",
            yAxisLabel = "Y-Axis",
            showGrid = true,
            smoothLines = true,
            maxPoints = 500,
            legendLabels = legendLabels, // Pass legend labels here
            modifier = Modifier.fillMaxSize()
        )
    }
}





