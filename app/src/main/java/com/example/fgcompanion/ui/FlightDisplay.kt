package com.example.fgcompanion.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import com.example.fgcompanion.data.FlightData
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.clipRect


private val SkyBlue = Color(0xFF238AE5)
private val GroundBrown = Color(0xFD852A12)
private val PfdWhite = Color.White
private val PfdYellow = Color(0xFFFFFF00)
private val PfdMagenta = Color(0xFFFF00FF)

@Composable
fun FlightDisplay(
    data: FlightData,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        val w = size.width
        val h = size.height

        val centerX = w / 2f
        val centerY = h * 0.46f

        drawAttitude(
            pitch = data.pitch,
            roll = data.roll,
            centerX = centerX,
            centerY = centerY,
            screenWidth = w,
            screenHeight = h
        )

        drawBankScale(
            roll = data.roll,
            centerX = centerX,
            screenHeight = h
        )

        drawAircraftSymbol(
            centerX = centerX,
            centerY = centerY,
            screenWidth = w
        )

        drawAirspeedTape(
            airspeed = data.airspeed,
            screenWidth = w,
            screenHeight = h
        )

        drawAltitudeTape(
            altitude = data.altitude,
            screenWidth = w,
            screenHeight = h
        )

        drawHeadingArc(
            heading = data.heading,
            track = data.magneticTrack,
            screenWidth = w,
            screenHeight = h
        )

        drawSpeedInfo(
            trueAirspeed = data.trueAirspeed,
            groundSpeed = data.groundSpeed,
            screenWidth = w,
            screenHeight = h
        )
        drawAutopilotIndicator(
            autopilotOn = data.autopilotOn,
            centerX = centerX,
            screenWidth = w,
            screenHeight = h
        )
        drawGearIndicator(
            gearDown = data.gearDown,
            screenWidth = w,
            screenHeight = h
        )
        drawFlapIndicator(
            flapPosition = data.flapPosition,
            screenWidth = w,
            screenHeight = h
        )
        drawVerticalSpeedValue(
            verticalSpeedFps = data.verticalSpeed,
            screenWidth = w,
            screenHeight = h
        )
        drawThrottleIndicator(
            throttle = data.throttle,
            screenWidth = w,
            screenHeight = h
        )
    }
}

private fun DrawScope.drawAttitude(
    pitch: Double,
    roll: Double,
    centerX: Float,
    centerY: Float,
    screenWidth: Float,
    screenHeight: Float
) {

    /*
     * Roughly 8 pixels per degree on a 1080 px-high screen.
     * Scaling it relative to screen height keeps it usable
     * across phones and tablets.
     */
    val pixelsPerDegree = screenHeight / 90f

    val pitchOffset =
        pitch.toFloat() * pixelsPerDegree

    /*
     * The attitude background is intentionally much larger
     * than the visible screen because it rotates.
     */
    val hugeWidth = screenWidth * 3f
    val hugeHeight = screenHeight * 4f

    val attitudeLeft = screenWidth * 0.22f
    val attitudeRight = screenWidth * 0.76f
    val attitudeTop = screenHeight * 0.12f
    val attitudeBottom = screenHeight * 0.80f



        withTransform({

            /*
             * Aircraft rolls right -> horizon rotates left.
             */
            rotate(
                degrees = -roll.toFloat(),
                pivot = Offset(centerX, centerY)
            )

            /*
             * Nose up -> horizon moves down.
             */
            translate(
                left = 0f,
                top = pitchOffset
            )

        }) {

            // SKY

            drawRect(
                color = SkyBlue,
                topLeft = Offset(
                    centerX - hugeWidth / 2f,
                    centerY - hugeHeight
                ),
                size = Size(
                    hugeWidth,
                    hugeHeight
                )
            )

            // GROUND

            drawRect(
                color = GroundBrown,
                topLeft = Offset(
                    centerX - hugeWidth / 2f,
                    centerY
                ),
                size = Size(
                    hugeWidth,
                    hugeHeight
                )
            )

            // Main horizon line

            drawLine(
                color = PfdWhite,
                start = Offset(
                    centerX - screenWidth,
                    centerY
                ),
                end = Offset(
                    centerX + screenWidth,
                    centerY
                ),
                strokeWidth = 3f
            )

            drawPitchLadder(
                centerX = centerX,
                centerY = centerY,
                screenWidth = screenWidth,
                pixelsPerDegree = pixelsPerDegree
            )
        }
}

private fun DrawScope.drawPitchLadder(
    centerX: Float,
    centerY: Float,
    screenWidth: Float,
    pixelsPerDegree: Float
) {

    for (degree in -30..30 step 5) {

        if (degree == 0) {
            continue
        }

        val y =
            centerY -
                    (degree * pixelsPerDegree)

        val major =
            degree % 10 == 0

        val lineHalfWidth =
            if (major) {
                screenWidth * 0.075f
            } else {
                screenWidth * 0.035f
            }

        drawLine(
            color = PfdWhite,
            start = Offset(
                centerX - lineHalfWidth,
                y
            ),
            end = Offset(
                centerX + lineHalfWidth,
                y
            ),
            strokeWidth =
                if (major) 3f else 2f
        )

        /*
         * Only label the 10-degree marks.
         */
        if (major) {

            val label =
                kotlin.math.abs(degree).toString()

            val paint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = screenWidth * 0.025f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText(
                label,
                centerX - lineHalfWidth - screenWidth * 0.035f,
                y + paint.textSize * 0.35f,
                paint
            )

            drawContext.canvas.nativeCanvas.drawText(
                label,
                centerX + lineHalfWidth + screenWidth * 0.035f,
                y + paint.textSize * 0.35f,
                paint
            )
        }
    }
}

private fun DrawScope.drawAircraftSymbol(
    centerX: Float,
    centerY: Float,
    screenWidth: Float
) {

    val innerGap =
        screenWidth * 0.025f

    val outerWidth =
        screenWidth * 0.10f

    val stroke =
        screenWidth * 0.006f

    /*
     * Left wing
     */
    drawLine(
        color = PfdYellow,
        start = Offset(
            centerX - outerWidth,
            centerY
        ),
        end = Offset(
            centerX - innerGap,
            centerY
        ),
        strokeWidth = stroke
    )

    /*
     * Right wing
     */
    drawLine(
        color = PfdYellow,
        start = Offset(
            centerX + innerGap,
            centerY
        ),
        end = Offset(
            centerX + outerWidth,
            centerY
        ),
        strokeWidth = stroke
    )

    /*
     * Center aircraft nose/reference.
     */
    val aircraft = Path().apply {

        moveTo(
            centerX - innerGap,
            centerY
        )

        lineTo(
            centerX,
            centerY + screenWidth * 0.012f
        )

        lineTo(
            centerX + innerGap,
            centerY
        )
    }

    drawPath(
        path = aircraft,
        color = PfdYellow
    )
}

private fun DrawScope.drawBankScale(
    roll: Double,
    centerX: Float,
    screenHeight: Float
) {

    val radius =
        screenHeight * 0.38f

    val center = Offset(
        centerX,
        screenHeight * 0.46f
    )

    /*
     * Common bank-angle marks.
     */
    val marks = listOf(
        -60,
        -45,
        -30,
        -20,
        -10,
        0,
        10,
        20,
        30,
        45,
        60
    )

    for (angle in marks) {

        /*
         * -90 converts our aviation bank-angle convention
         * into Canvas coordinates where straight up is
         * -90 degrees.
         */
        val radians =
            Math.toRadians(
                (angle - 90).toDouble()
            )

        val outerX =
            center.x +
                    cos(radians).toFloat() * radius

        val outerY =
            center.y +
                    sin(radians).toFloat() * radius

        val tickLength =
            when (kotlin.math.abs(angle)) {
                0 -> screenHeight * 0.035f
                30, 60 -> screenHeight * 0.030f
                else -> screenHeight * 0.020f
            }

        val innerRadius =
            radius - tickLength

        val innerX =
            center.x +
                    cos(radians).toFloat() * innerRadius

        val innerY =
            center.y +
                    sin(radians).toFloat() * innerRadius

        drawLine(
            color = PfdWhite,
            start = Offset(
                innerX,
                innerY
            ),
            end = Offset(
                outerX,
                outerY
            ),
            strokeWidth = 3f
        )
    }

    /*
     * Current-bank pointer.
     *
     * Limit display to +/- 60 degrees so it doesn't run
     * outside the scale.
     */
    val displayedRoll =
        roll.coerceIn(-60.0, 60.0)

    val pointerRadians =
        Math.toRadians(
            displayedRoll - 90.0
        )

    val pointerRadius =
        radius - screenHeight * 0.055f

    val pointerX =
        center.x +
                cos(pointerRadians).toFloat() *
                pointerRadius

    val pointerY =
        center.y +
                sin(pointerRadians).toFloat() *
                pointerRadius

    val triangleSize =
        screenHeight * 0.018f

    val pointer = Path().apply {

        moveTo(
            pointerX,
            pointerY
        )

        lineTo(
            pointerX - triangleSize,
            pointerY - triangleSize * 1.5f
        )

        lineTo(
            pointerX + triangleSize,
            pointerY - triangleSize * 1.5f
        )

        close()
    }

    drawPath(
        path = pointer,
        color = PfdWhite
    )
}

private fun DrawScope.drawAirspeedTape(
    airspeed: Double,
    screenWidth: Float,
    screenHeight: Float
) {

    val centerY = screenHeight * 0.46f

    val left = screenWidth * 0.25f
    val right = screenWidth * 0.35f

    val top = screenHeight * 0.10f
    val bottom = screenHeight * 0.82f

    /*
     * How much vertical screen space represents 1 knot.
     */
    val pixelsPerKnot = screenHeight * 0.010f

    // Dark translucent tape background

    drawRect(
        color = Color(0xCC06101E),
        topLeft = Offset(left, top),
        size = Size(
            right - left,
            bottom - top
        )
    )

    // White border

    drawRect(
        color = PfdWhite,
        topLeft = Offset(left, top),
        size = Size(
            right - left,
            bottom - top
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = screenWidth * 0.0015f
        )
    )

    /*
     * Draw speed ticks around the current IAS.
     */
    val lowerSpeed =
        (airspeed.toInt() - 70).coerceAtLeast(0)

    val upperSpeed =
        airspeed.toInt() + 70

    for (speed in lowerSpeed..upperSpeed) {

        if (speed % 5 != 0) {
            continue
        }

        val y =
            centerY -
                    ((speed - airspeed) * pixelsPerKnot).toFloat()

        if (y < top || y > bottom) {
            continue
        }

        val major = speed % 10 == 0

        val tickLength =
            if (major) {
                screenWidth * 0.018f
            } else {
                screenWidth * 0.010f
            }

        drawLine(
            color = PfdWhite,
            start = Offset(
                right - tickLength,
                y
            ),
            end = Offset(
                right,
                y
            ),
            strokeWidth = if (major) 3f else 2f
        )

        if (major) {

            val paint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = screenHeight * 0.042f
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText(
                speed.toString(),
                right - tickLength - screenWidth * 0.008f,
                y + paint.textSize * 0.35f,
                paint
            )
        }
    }

    /*
     * Current IAS pointer/box.
     */

    val boxLeft = screenWidth * 0.20f
    val boxRight = right + screenWidth * 0.012f

    val boxHalfHeight =
        screenHeight * 0.048f

    val pointerWidth =
        screenWidth * 0.018f

    val speedBox = Path().apply {

        moveTo(
            boxLeft,
            centerY - boxHalfHeight
        )

        lineTo(
            boxRight - pointerWidth,
            centerY - boxHalfHeight
        )

        lineTo(
            boxRight,
            centerY
        )

        lineTo(
            boxRight - pointerWidth,
            centerY + boxHalfHeight
        )

        lineTo(
            boxLeft,
            centerY + boxHalfHeight
        )

        close()
    }

    drawPath(
        path = speedBox,
        color = Color.Black
    )

    drawPath(
        path = speedBox,
        color = PfdWhite,
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = 3f
        )
    )

    val currentPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = screenHeight * 0.060f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        airspeed.toInt().toString(),
        (boxLeft + boxRight - pointerWidth) / 2f,
        centerY + currentPaint.textSize * 0.35f,
        currentPaint
    )
}

private fun DrawScope.drawAltitudeTape(
    altitude: Double,
    screenWidth: Float,
    screenHeight: Float
) {

    val centerY = screenHeight * 0.46f

    val left = screenWidth * 0.65f
    val right = screenWidth * 0.75f

    val top = screenHeight * 0.10f
    val bottom = screenHeight * 0.82f

    /*
     * Vertical scaling:
     * approximately 100 ft every 8% of screen height.
     */
    val pixelsPerFoot =
        screenHeight * 0.0008f

    drawRect(
        color = Color(0xCC06101E),
        topLeft = Offset(left, top),
        size = Size(
            right - left,
            bottom - top
        )
    )

    drawRect(
        color = PfdWhite,
        topLeft = Offset(left, top),
        size = Size(
            right - left,
            bottom - top
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = screenWidth * 0.0015f
        )
    )

    /*
     * Find the nearest lower 100-foot value.
     */
    val baseAltitude =
        (altitude.toInt() / 100) * 100

    for (offset in -1500..1500 step 20) {

        val value =
            baseAltitude + offset

        if (value < 0) {
            continue
        }

        val y =
            centerY -
                    ((value - altitude) * pixelsPerFoot).toFloat()

        if (y < top || y > bottom) {
            continue
        }

        val major =
            value % 100 == 0

        val tickLength =
            if (major) {
                screenWidth * 0.018f
            } else {
                screenWidth * 0.009f
            }

        drawLine(
            color = PfdWhite,
            start = Offset(
                left,
                y
            ),
            end = Offset(
                left + tickLength,
                y
            ),
            strokeWidth =
                if (major) 3f else 2f
        )

        if (major) {

            val paint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = screenHeight * 0.038f
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText(
                value.toString(),
                left + tickLength + screenWidth * 0.007f,
                y + paint.textSize * 0.35f,
                paint
            )
        }
    }

    /*
     * Current altitude box.
     */

    val pointerLeft =
        left - screenWidth * 0.012f

    val boxRight =
        screenWidth * 0.80f

    val pointerWidth =
        screenWidth * 0.018f

    val boxHalfHeight =
        screenHeight * 0.048f

    val altitudeBox = Path().apply {

        moveTo(
            pointerLeft,
            centerY
        )

        lineTo(
            pointerLeft + pointerWidth,
            centerY - boxHalfHeight
        )

        lineTo(
            boxRight,
            centerY - boxHalfHeight
        )

        lineTo(
            boxRight,
            centerY + boxHalfHeight
        )

        lineTo(
            pointerLeft + pointerWidth,
            centerY + boxHalfHeight
        )

        close()
    }

    drawPath(
        path = altitudeBox,
        color = Color.Black
    )

    drawPath(
        path = altitudeBox,
        color = PfdWhite,
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = 3f
        )
    )

    val currentPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = screenHeight * 0.054f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        altitude.toInt().toString(),
        (pointerLeft + pointerWidth + boxRight) / 2f,
        centerY + currentPaint.textSize * 0.35f,
        currentPaint
    )
}

private fun DrawScope.drawHeadingArc(
    heading: Double,
    track: Double,
    screenWidth: Float,
    screenHeight: Float
) {
    val centerX = screenWidth / 2f

    /*
     * Arc centre is below the visible display.
     */
    val arcCenterY =
        screenHeight * 1.62f

    val radius =
        screenHeight * 0.8f

    /*
     * Approximately +/- 50 degrees visible.
     */
    val degreesVisible = 50

    val headingPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = screenHeight * 0.038f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    /*
     * Draw ticks relative to current heading.
     */
    for (relativeDegree in -degreesVisible..degreesVisible step 5) {

        val displayedHeading =
            normalizeHeading(
                heading + relativeDegree
            )

        val radians =
            Math.toRadians(
                relativeDegree.toDouble() - 90.0
            )

        val outerX =
            centerX +
                    kotlin.math.cos(radians).toFloat() *
                    radius

        val outerY =
            arcCenterY +
                    kotlin.math.sin(radians).toFloat() *
                    radius

        val major =
            relativeDegree % 10 == 0

        val tickLength =
            if (major) {
                screenHeight * 0.028f
            } else {
                screenHeight * 0.016f
            }

        val innerRadius =
            radius - tickLength

        val innerX =
            centerX +
                    kotlin.math.cos(radians).toFloat() *
                    innerRadius

        val innerY =
            arcCenterY +
                    kotlin.math.sin(radians).toFloat() *
                    innerRadius

        drawLine(
            color = PfdWhite,
            start = Offset(
                innerX,
                innerY
            ),
            end = Offset(
                outerX,
                outerY
            ),
            strokeWidth =
                if (major) 3f else 2f
        )

        /*
         * Label every 10 degrees.
         *
         * 180 degrees becomes "18"
         * 270 becomes "27", etc.
         */
        if (major) {

            val labelRadius =
                radius - screenHeight * 0.065f

            val labelX =
                centerX +
                        kotlin.math.cos(radians).toFloat() *
                        labelRadius

            val labelY =
                arcCenterY +
                        kotlin.math.sin(radians).toFloat() *
                        labelRadius

            val label =
                (displayedHeading.toInt() / 10)
                    .toString()
                    .padStart(2, '0')

            drawContext.canvas.nativeCanvas.drawText(
                label,
                labelX,
                labelY +
                        headingPaint.textSize * 0.35f,
                headingPaint
            )
        }
    }

    /*
     * Fixed heading reference triangle.
     */

    val referenceY =
        arcCenterY - radius

    val triangleSize =
        screenHeight * 0.018f

    val referenceTriangle = Path().apply {

        moveTo(
            centerX,
            referenceY
        )

        lineTo(
            centerX - triangleSize,
            referenceY - triangleSize
        )

        lineTo(
            centerX + triangleSize,
            referenceY - triangleSize
        )

        close()
    }

    drawPath(
        path = referenceTriangle,
        color = PfdWhite
    )

    /*
     * Current heading text.
     */

    val currentHeadingPaint = Paint().apply {
        color = android.graphics.Color.GREEN
        textSize = screenHeight * 0.045f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    val headingString =
        normalizeHeading(heading)
            .toInt()
            .toString()
            .padStart(3, '0')

    drawContext.canvas.nativeCanvas.drawText(
        "HDG $headingString°",
        centerX,
        screenHeight * 0.97f,
        currentHeadingPaint
    )

    /*
     * Track marker.
     *
     * Shows where the aircraft is actually travelling
     * relative to its heading.
     */

    val trackDifference =
        shortestAngleDifference(
            heading,
            track
        ).coerceIn(
            -degreesVisible.toDouble(),
            degreesVisible.toDouble()
        )

    val trackRadians =
        Math.toRadians(
            trackDifference - 90.0
        )

    val trackRadius =
        radius - screenHeight * 0.008f

    val trackX =
        centerX +
                kotlin.math.cos(trackRadians).toFloat() *
                trackRadius

    val trackY =
        arcCenterY +
                kotlin.math.sin(trackRadians).toFloat() *
                trackRadius

    val trackMarker = Path().apply {

        moveTo(
            trackX,
            trackY
        )

        lineTo(
            trackX - triangleSize * 0.7f,
            trackY + triangleSize
        )

        lineTo(
            trackX + triangleSize * 0.7f,
            trackY + triangleSize
        )

        close()
    }

    drawPath(
        path = trackMarker,
        color = PfdMagenta
    )
}

private fun normalizeHeading(
    heading: Double
): Double {

    var result = heading % 360.0

    if (result < 0) {
        result += 360.0
    }

    return result
}

private fun shortestAngleDifference(
    heading: Double,
    target: Double
): Double {

    var difference =
        (target - heading + 540.0) %
                360.0 - 180.0

    if (difference < -180.0) {
        difference += 360.0
    }

    return difference
}

private fun DrawScope.drawSpeedInfo(
    trueAirspeed: Double,
    groundSpeed: Double,
    screenWidth: Float,
    screenHeight: Float
) {

    val labelPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = screenHeight * 0.06f
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }

    val valuePaint = Paint().apply {
        color = android.graphics.Color.GREEN
        textSize = screenHeight * 0.06f
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
    }

    val x =
        screenWidth * 0.15f

    val y =
        screenHeight * 0.7f

    drawContext.canvas.nativeCanvas.drawText(
        "TAS",
        x,
        y,
        labelPaint
    )

    drawContext.canvas.nativeCanvas.drawText(
        trueAirspeed.toInt().toString(),
        x + screenWidth * 0.055f,
        y,
        valuePaint
    )

    drawContext.canvas.nativeCanvas.drawText(
        "GS",
        x,
        y + screenHeight * 0.052f,
        labelPaint
    )

    drawContext.canvas.nativeCanvas.drawText(
        groundSpeed.toInt().toString(),
        x + screenWidth * 0.055f,
        y + screenHeight * 0.052f,
        valuePaint
    )
}

private fun DrawScope.drawAutopilotIndicator(
    autopilotOn: Boolean,
    screenWidth: Float,
    screenHeight: Float,
    centerX: Float
) {
    val paint = Paint().apply {
        color = if (autopilotOn) {
            android.graphics.Color.GREEN
        } else {
            android.graphics.Color.GREEN
        }

        textSize = screenHeight * 0.06f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    val text = if (autopilotOn) {
        "AP ON"
    } else {
        ""
    }

    drawContext.canvas.nativeCanvas.drawText(
        text,
        centerX - screenWidth * 0.3f  ,
        screenHeight * 0.2f,
        paint
    )
}

private fun DrawScope.drawGearIndicator(
    gearDown: Boolean,
    screenWidth: Float,
    screenHeight: Float
) {
    val green = Color(0xFF00FF66)
    val red = Color(0xFFEA043B)
    var color = green
    // Gear up/not down -> change colour
    if (!gearDown) color = red


    val cx = screenWidth * 0.1f
    val cy = screenHeight * 0.2f

    val scale = screenHeight * 0.045f
    val stroke = screenHeight * 0.005f

    // -------------------------
    // Nose gear
    // -------------------------

    // Nose strut
    drawLine(
        color = color,
        start = Offset(cx, cy - scale * 1.4f),
        end = Offset(cx, cy - scale * 0.45f),
        strokeWidth = stroke
    )

    // Nose wheel
    drawCircle(
        color = color,
        radius = scale * 0.22f,
        center = Offset(
            cx,
            cy - scale * 1.65f
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = stroke
        )
    )

    // -------------------------
    // Main gear structure
    // -------------------------

    // Horizontal aircraft/axle representation
    drawLine(
        color = color,
        start = Offset(
            cx - scale,
            cy
        ),
        end = Offset(
            cx + scale,
            cy
        ),
        strokeWidth = stroke
    )

    // Center connection
    drawLine(
        color = color,
        start = Offset(
            cx,
            cy - scale * 0.45f
        ),
        end = Offset(
            cx,
            cy
        ),
        strokeWidth = stroke
    )

    // Left main strut
    drawLine(
        color = color,
        start = Offset(
            cx - scale * 0.75f,
            cy
        ),
        end = Offset(
            cx - scale * 0.75f,
            cy + scale * 0.75f
        ),
        strokeWidth = stroke
    )

    // Right main strut
    drawLine(
        color = color,
        start = Offset(
            cx + scale * 0.75f,
            cy
        ),
        end = Offset(
            cx + scale * 0.75f,
            cy + scale * 0.75f
        ),
        strokeWidth = stroke
    )

    // Left main wheel
    drawCircle(
        color = color,
        radius = scale * 0.25f,
        center = Offset(
            cx - scale * 0.75f,
            cy + scale
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = stroke
        )
    )

    // Right main wheel
    drawCircle(
        color = color,
        radius = scale * 0.25f,
        center = Offset(
            cx + scale * 0.75f,
            cy + scale
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = stroke
        )
    )
}

private fun DrawScope.drawFlapIndicator(
    flapPosition: Double,
    screenWidth: Float,
    screenHeight: Float
) {
    val position = flapPosition
        .coerceIn(0.0, 1.0)
        .toFloat()

    // Extreme-right position
    val x = screenWidth * 0.93f

    val top = screenHeight * 0.34f
    val bottom = screenHeight * 0.66f

    val trackWidth = screenWidth * 0.012f
    val stroke = screenHeight * 0.003f

    val green = Color(0xFF00FF66)
    val white = Color.White

    // Vertical track
    drawRect(
        color = white,
        topLeft = Offset(
            x - trackWidth / 2f,
            top
        ),
        size = Size(
            trackWidth,
            bottom - top
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = stroke
        )
    )

    /*
     * 0.0 -> top
     * 1.0 -> bottom
     */
    val leverY =
        top + (bottom - top) * position

    // Lever/pointer
    drawLine(
        color = green,
        start = Offset(
            x - screenWidth * 0.022f,
            leverY
        ),
        end = Offset(
            x + screenWidth * 0.012f,
            leverY
        ),
        strokeWidth = screenHeight * 0.007f
    )

    // Small triangular pointer
    val triangleSize = screenHeight * 0.012f

    val pointer = Path().apply {
        moveTo(
            x - trackWidth / 2f,
            leverY
        )

        lineTo(
            x - trackWidth / 2f - triangleSize,
            leverY - triangleSize
        )

        lineTo(
            x - trackWidth / 2f - triangleSize,
            leverY + triangleSize
        )

        close()
    }

    drawPath(
        path = pointer,
        color = green
    )

    // FLAP label
    val labelPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = screenHeight * 0.025f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "FLAP",
        x,
        top - screenHeight * 0.025f,
        labelPaint
    )
}

private fun DrawScope.drawVerticalSpeedValue(
    verticalSpeedFps: Double,
    screenWidth: Float,
    screenHeight: Float
) {
    // FlightGear gives us feet/second.
    // Convert to feet/minute.
    val verticalSpeedFpm = verticalSpeedFps * 60.0

    // Avoid constantly showing tiny fluctuations while level.
    val displayedVs =
        if (kotlin.math.abs(verticalSpeedFpm) < 50.0) {
            0
        } else {
            verticalSpeedFpm.toInt()
        }

    val color = when {
        displayedVs > 0 ->
            android.graphics.Color.GREEN

        displayedVs < 0 ->
            android.graphics.Color.RED

        else ->
            android.graphics.Color.WHITE
    }

    val text = when {
        displayedVs > 0 -> "+$displayedVs"
        else -> displayedVs.toString()
    }

    val paint = Paint().apply {
        this.color = color
        textSize = screenHeight * 0.054f
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    /*
     * Altitude tape ends around 88% of screen width.
     * Put VS immediately beside the current altitude box.
     */
    val x = screenWidth * 0.81f
    val y = screenHeight * 0.46f +
            paint.textSize * 0.35f

    drawContext.canvas.nativeCanvas.drawText(
        text,
        x,
        y,
        paint
    )
}

private fun DrawScope.drawThrottleIndicator(
    throttle: Double,
    screenWidth: Float,
    screenHeight: Float
) {
    val position = throttle
        .coerceIn(0.0, 1.0)
        .toFloat()

    // Extreme left
    val x = screenWidth * 0.1f

    val top = screenHeight * 0.36f
    val bottom = screenHeight * 0.64f

    val trackWidth = screenWidth * 0.012f
    val stroke = screenHeight * 0.003f

    val green = Color(0xFF00FF66)
    val white = Color.White

    // Outer throttle track
    drawRect(
        color = white,
        topLeft = Offset(
            x - trackWidth / 2f,
            top
        ),
        size = Size(
            trackWidth,
            bottom - top
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = stroke
        )
    )

    /*
     * Throttle direction:
     *
     * 1.0 = TOP
     * 0.0 = BOTTOM
     */
    val leverY =
        bottom - (bottom - top) * position

    // Horizontal throttle lever
    drawLine(
        color = green,
        start = Offset(
            x - screenWidth * 0.012f,
            leverY
        ),
        end = Offset(
            x + screenWidth * 0.022f,
            leverY
        ),
        strokeWidth = screenHeight * 0.007f
    )

    // Triangle pointing into the scale
    val triangleSize = screenHeight * 0.012f

    val pointer = Path().apply {

        moveTo(
            x + trackWidth / 2f,
            leverY
        )

        lineTo(
            x + trackWidth / 2f + triangleSize,
            leverY - triangleSize
        )

        lineTo(
            x + trackWidth / 2f + triangleSize,
            leverY + triangleSize
        )

        close()
    }

    drawPath(
        path = pointer,
        color = green
    )

    // THR label
    val labelPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = screenHeight * 0.025f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "THR",
        x,
        top - screenHeight * 0.025f,
        labelPaint
    )
}