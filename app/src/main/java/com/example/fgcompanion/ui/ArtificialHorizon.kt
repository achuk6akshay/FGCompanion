package com.example.fgcompanion.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
// import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
// import kotlin.math.abs

@Composable
fun ArtificialHorizon(
    pitch: Double,
    roll: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color.Black)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val width = size.width
            val height = size.height

            val centerX = width / 2f
            val centerY = height / 2f

            /*
             * Number of pixels the horizon moves for
             * each degree of aircraft pitch.
             */
            val pixelsPerDegree = height / 45f

            val pitchOffset =
                pitch.toFloat() * pixelsPerDegree

            /*
             * Everything inside this transformation is
             * aircraft attitude dependent.
             *
             * Positive aircraft roll means the horizon
             * rotates in the opposite direction.
             */
            withTransform({
                rotate(
                    degrees = -roll.toFloat(),
                    pivot = Offset(centerX, centerY)
                )

                translate(
                    left = 0f,
                    top = pitchOffset
                )
            }) {

                // SKY
                drawRect(
                    color = Color(0xFF2874A6),
                    topLeft = Offset(
                        -width,
                        -height * 3f
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width * 3f,
                        height * 3f + centerY
                    )
                )

                // GROUND
                drawRect(
                    color = Color(0xFF8B5A2B),
                    topLeft = Offset(
                        -width,
                        centerY
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width * 3f,
                        height * 4f
                    )
                )

                // Horizon line
                drawLine(
                    color = Color.White,
                    start = Offset(-width, centerY),
                    end = Offset(width * 2f, centerY),
                    strokeWidth = 4f
                )

                /*
                 * Pitch ladder
                 *
                 * Marks every 5 degrees.
                 */
                for (degree in -30..30 step 5) {

                    if (degree == 0) continue

                    val y =
                        centerY -
                                degree * pixelsPerDegree

                    val major =
                        degree % 10 == 0

                    val lineWidth =
                        if (major) width * 0.16f
                        else width * 0.09f

                    drawLine(
                        color = Color.White,
                        start = Offset(
                            centerX - lineWidth,
                            y
                        ),
                        end = Offset(
                            centerX + lineWidth,
                            y
                        ),
                        strokeWidth =
                            if (major) 3f else 2f
                    )
                }
            }

            /*
             * Aircraft reference symbol
             *
             * This does NOT rotate with the horizon.
             */
            val wingWidth = width * 0.15f
            val wingGap = width * 0.035f

            drawLine(
                color = Color(0xFFFFFF00),
                start = Offset(
                    centerX - wingGap - wingWidth,
                    centerY
                ),
                end = Offset(
                    centerX - wingGap,
                    centerY
                ),
                strokeWidth = 7f
            )

            drawLine(
                color = Color(0xFFFFFF00),
                start = Offset(
                    centerX + wingGap,
                    centerY
                ),
                end = Offset(
                    centerX + wingGap + wingWidth,
                    centerY
                ),
                strokeWidth = 7f
            )

            /*
             * Small aircraft center marker
             */
            val marker = Path().apply {

                moveTo(
                    centerX - 10f,
                    centerY
                )

                lineTo(
                    centerX,
                    centerY + 8f
                )

                lineTo(
                    centerX + 10f,
                    centerY
                )
            }

            drawPath(
                path = marker,
                color = Color(0xFFFFFF00)
            )
        }
    }
}

