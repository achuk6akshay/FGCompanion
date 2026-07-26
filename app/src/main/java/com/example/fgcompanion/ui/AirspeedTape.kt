package com.example.fgcompanion.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint

@Composable
fun AirspeedTape(
    airspeed: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color(0xDD101010))
    ) {
        Canvas(Modifier.fillMaxSize()) {

            val width = size.width
            val height = size.height
            val centerY = height / 2f

            // Distance between each knot on the tape
            val pixelsPerKnot = 4f

            val textPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 30f
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }

            // Draw values around current IAS
            val minimum = (airspeed.toInt() - 60).coerceAtLeast(0)
            val maximum = airspeed.toInt() + 60

            for (speed in minimum..maximum) {

                if (speed % 10 != 0) continue

                val difference = speed - airspeed

                val y = centerY -
                        difference.toFloat() * pixelsPerKnot

                if (y !in 0f..height) continue

                // Major tick
                drawLine(
                    color = Color.White,
                    start = Offset(width - 20f, y),
                    end = Offset(width, y),
                    strokeWidth = 3f
                )

                drawContext.canvas.nativeCanvas.drawText(
                    speed.toString(),
                    width - 28f,
                    y + 10f,
                    textPaint
                )
            }

            // Current IAS box
            val boxHeight = 58f

            drawRect(
                color = Color.Black,
                topLeft = Offset(0f, centerY - boxHeight / 2),
                size = androidx.compose.ui.geometry.Size(
                    width,
                    boxHeight
                )
            )

            drawRect(
                color = Color.White,
                topLeft = Offset(0f, centerY - boxHeight / 2),
                size = androidx.compose.ui.geometry.Size(
                    width,
                    boxHeight
                ),
                style = Stroke(width = 3f)
            )

            val currentPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 38f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText(
                airspeed.toInt().toString(),
                width / 2,
                centerY + 13f,
                currentPaint
            )
        }
    }
}