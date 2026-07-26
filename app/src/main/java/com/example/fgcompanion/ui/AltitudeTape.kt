package com.example.fgcompanion.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas

@Composable
fun AltitudeTape(
    altitude: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color(0xDD101010))
    ) {
        Canvas(Modifier.fillMaxSize()) {

            val width = size.width
            val height = size.height
            val centerY = height / 2f

            /*
             * 100 ft occupies approximately 40 px.
             */
            val pixelsPerFoot = 0.4f

            val textPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 28f
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
            }

            val minimum =
                ((altitude.toInt() - 1000) / 100) * 100

            val maximum =
                ((altitude.toInt() + 1000) / 100) * 100

            for (alt in minimum..maximum step 100) {

                if (alt < 0) continue

                val difference = alt - altitude

                val y = centerY -
                        difference.toFloat() * pixelsPerFoot

                if (y < 0f || y > height) continue

                drawLine(
                    color = Color.White,
                    start = Offset(0f, y),
                    end = Offset(20f, y),
                    strokeWidth = 3f
                )

                drawContext.canvas.nativeCanvas.drawText(
                    alt.toString(),
                    28f,
                    y + 10f,
                    textPaint
                )
            }

            // Current altitude box

            val boxHeight = 58f

            drawRect(
                color = Color.Black,
                topLeft = Offset(
                    0f,
                    centerY - boxHeight / 2
                ),
                size = Size(
                    width,
                    boxHeight
                )
            )

            drawRect(
                color = Color.White,
                topLeft = Offset(
                    0f,
                    centerY - boxHeight / 2
                ),
                size = Size(
                    width,
                    boxHeight
                ),
                style = Stroke(width = 3f)
            )

            val currentPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 34f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText(
                altitude.toInt().toString(),
                width / 2,
                centerY + 12f,
                currentPaint
            )
        }
    }
}