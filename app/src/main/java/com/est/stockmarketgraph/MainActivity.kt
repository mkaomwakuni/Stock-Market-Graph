package com.est.stockmarketgraph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StockMarketGraph(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
          }
    }
}

@Composable
fun StockMarketGraph(modifier: Modifier = Modifier) {
    //dummy data set
    val data = listOf(
        0f to 1.2f, 1f to 1.1f, 2.2f to 1.9f, 3f to 2.5f, 4.5f to 2.4f,
        5.2f to 4.2f, 5.3f to 6f, 5.5f to 5.9f,4.4f to 5f, 3.2f to 4f, 9f to 11f,
        10f to 6f, 11f to 8f, 12f to 9f, 13f to 14f, 14f to 13f,
        15f to 12.5f, 16f to 14f, 17f to 19f, 18f to 18f, 19f to 19f,
        20f to 15f, 21f to 21f
    )
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, color = Color.LightGray.copy(alpha = 0.4f)) //You may remove this on your implementation
            .padding(5.dp)
            .height(250.dp)
    ) {
        AnimatedWavyGradientLineChart(
            data = data,
            modifier = Modifier.fillMaxSize()
        )
    }
  }
}

/**
 * Displays an animated wavy gradient line chart.
 *
 * @param data The list of data points to be plotted on the chart. Each point is a pair of x and y values.
 * @param lineColor The color of the line. Default is green.
 * @param lineThickness The thickness of the line. Default is 4.dp.
 * @param animationDuration The duration of the animation in milliseconds. Default is 3000ms.
 * @param modifier Optional [Modifier] to apply custom styling to the chart.
 */

@Composable
fun AnimatedWavyGradientLineChart(
    data: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = true) {

        for (i in 0..100) {
            progress = i / 100f
            delay(30)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (data.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height

        val xScale = width / (data.size - 1)
        val yMin = data.minOf { it.second }
        val yMax = data.maxOf { it.second }
        val yScale = height / (yMax - yMin)

        val linePath = Path()
        val animatedData = data.take((data.size * progress).toInt() + 1)

        animatedData.forEachIndexed { index, (_, y) ->
            val scaledX = index * xScale
            val scaledY = height - (y - yMin) * yScale

            if (index == 0) {
                linePath.moveTo(scaledX, scaledY)
            } else {
                val prevX = (index - 1) * xScale
                val prevY = height - (animatedData[index - 1].second - yMin) * yScale
                val controlX = (prevX + scaledX) / 2f
                val controlY = prevY + (scaledY - prevY) / 2f + (abs(scaledY - prevY) * 0.1f)

                linePath.quadraticBezierTo(controlX, controlY, scaledX, scaledY)
            }
        }

        drawPath(
            path = linePath,
            color = Color(0xFF4CAF50),
            style = Stroke(width = 4.dp.toPx())
        )

        val fillPath = Path()
        fillPath.addPath(linePath)
        fillPath.lineTo(width * progress, height)
        fillPath.lineTo(0f, height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF4CAF50).copy(alpha = 0.3f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )
    }
}