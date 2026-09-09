package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SpeedCyan
import com.example.ui.theme.SpeedDarkCard
import com.example.ui.theme.SpeedMagenta
import com.example.ui.theme.SpeedNeonBlue
import com.example.ui.theme.SpeedRose
import com.example.ui.theme.SpeedTextMuted
import com.example.ui.theme.SpeedTextPrimary
import com.example.ui.theme.SpeedTextSecondary
import com.example.ui.theme.SpeedViolet
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

// Piecewise mapping for realistic speedometer dial feel
private val speedThresholds = listOf(
    0.0 to 0.00f,
    1.0 to 0.08f,
    5.0 to 0.18f,
    10.0 to 0.28f,
    25.0 to 0.40f,
    50.0 to 0.54f,
    100.0 to 0.68f,
    250.0 to 0.80f,
    500.0 to 0.90f,
    1000.0 to 1.00f
)

fun speedToDialFraction(speedMbps: Double): Float {
    if (speedMbps <= 0.0) return 0f
    if (speedMbps >= 1000.0) return 1f

    for (i in 0 until speedThresholds.size - 1) {
        val (s1, f1) = speedThresholds[i]
        val (s2, f2) = speedThresholds[i + 1]
        if (speedMbps in s1..s2) {
            val ratio = (speedMbps - s1) / (s2 - s1)
            return (f1 + ratio * (f2 - f1)).toFloat().coerceIn(0f, 1f)
        }
    }
    return 1f
}

@Composable
fun SpeedometerGauge(
    currentSpeedMbps: Double,
    label: String,
    unit: String = "Mbps",
    modifier: Modifier = Modifier,
    accentColor: Color = SpeedCyan
) {
    val targetFraction = speedToDialFraction(currentSpeedMbps)
    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "NeedleAngle"
    )

    val startAngle = 140f
    val sweepAngle = 260f
    val currentSweep = sweepAngle * animatedFraction

    Box(
        modifier = modifier
            .size(280.dp)
            .testTag("speedometer_gauge"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)
            val radius = (canvasWidth / 2f) - 24.dp.toPx()

            val strokeWidth = 14.dp.toPx()

            // 1. Background Track Arc
            drawArc(
                color = SpeedDarkCard.copy(alpha = 0.8f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Outer glowing accent track
            drawArc(
                color = Color.White.copy(alpha = 0.04f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius - 10.dp.toPx(), center.y - radius - 10.dp.toPx()),
                size = Size((radius + 10.dp.toPx()) * 2, (radius + 10.dp.toPx()) * 2),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // 2. Active Speed Arc with vibrant gradient
            if (currentSweep > 1f) {
                val gradientBrush = Brush.sweepGradient(
                    0.35f to SpeedCyan,
                    0.65f to SpeedViolet,
                    0.85f to SpeedMagenta,
                    1.0f to SpeedRose,
                    center = center
                )

                drawArc(
                    brush = gradientBrush,
                    startAngle = startAngle,
                    sweepAngle = currentSweep,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 3. Dial Tick Marks and Labels
            val tickRadius = radius - strokeWidth / 2f - 10.dp.toPx()
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(160, 148, 163, 184)
                textSize = 9.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }

            for ((speedValue, fraction) in speedThresholds) {
                val angleDeg = startAngle + sweepAngle * fraction
                val angleRad = Math.toRadians(angleDeg.toDouble())

                val tickOuter = Offset(
                    (center.x + tickRadius * cos(angleRad)).toFloat(),
                    (center.y + tickRadius * sin(angleRad)).toFloat()
                )
                val tickInner = Offset(
                    (center.x + (tickRadius - 6.dp.toPx()) * cos(angleRad)).toFloat(),
                    (center.y + (tickRadius - 6.dp.toPx()) * sin(angleRad)).toFloat()
                )

                val isPassed = fraction <= animatedFraction
                val tickColor = if (isPassed) accentColor else SpeedTextMuted.copy(alpha = 0.4f)

                drawLine(
                    color = tickColor,
                    start = tickInner,
                    end = tickOuter,
                    strokeWidth = if (speedValue in listOf(0.0, 10.0, 100.0, 1000.0)) 2.5f.dp.toPx() else 1.5f.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Draw label for key intervals
                if (speedValue in listOf(0.0, 10.0, 50.0, 100.0, 500.0, 1000.0)) {
                    val labelRadius = tickRadius - 16.dp.toPx()
                    val labelX = (center.x + labelRadius * cos(angleRad)).toFloat()
                    val labelY = (center.y + labelRadius * sin(angleRad) + 3.dp.toPx()).toFloat()
                    val speedText = if (speedValue >= 1000) "1G" else speedValue.toInt().toString()
                    drawContext.canvas.nativeCanvas.drawText(speedText, labelX, labelY, textPaint)
                }
            }

            // 4. Tachometer Needle
            val needleAngleDeg = startAngle + currentSweep
            val needleAngleRad = Math.toRadians(needleAngleDeg.toDouble())
            val needleLength = radius - 14.dp.toPx()

            val tip = Offset(
                (center.x + needleLength * cos(needleAngleRad)).toFloat(),
                (center.y + needleLength * sin(needleAngleRad)).toFloat()
            )

            // Subtle needle glow
            drawCircle(
                color = accentColor.copy(alpha = 0.4f),
                radius = 7.dp.toPx(),
                center = tip
            )

            val perpAngle1 = needleAngleRad + Math.PI / 2.0
            val perpAngle2 = needleAngleRad - Math.PI / 2.0
            val baseWidth = 4.5.dp.toPx()

            val p1 = Offset(
                (center.x + baseWidth * cos(perpAngle1)).toFloat(),
                (center.y + baseWidth * sin(perpAngle1)).toFloat()
            )
            val p2 = Offset(
                (center.x + baseWidth * cos(perpAngle2)).toFloat(),
                (center.y + baseWidth * sin(perpAngle2)).toFloat()
            )

            val needlePath = Path().apply {
                moveTo(p1.x, p1.y)
                lineTo(tip.x, tip.y)
                lineTo(p2.x, p2.y)
                close()
            }

            drawPath(
                path = needlePath,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White, accentColor),
                    start = center,
                    end = tip
                )
            )

            // Center Hub
            drawCircle(
                color = SpeedDarkCard,
                radius = 16.dp.toPx(),
                center = center
            )
            drawCircle(
                color = accentColor,
                radius = 7.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = center
            )
        }

        // Center Digital Display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            val formattedSpeed = when {
                currentSpeedMbps >= 100 -> String.format(Locale.US, "%.0f", currentSpeedMbps)
                currentSpeedMbps >= 10 -> String.format(Locale.US, "%.1f", currentSpeedMbps)
                currentSpeedMbps > 0 -> String.format(Locale.US, "%.2f", currentSpeedMbps)
                else -> "0.0"
            }

            Text(
                text = formattedSpeed,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = SpeedTextPrimary,
                letterSpacing = (-1).sp,
                modifier = Modifier.testTag("current_speed_readout")
            )

            Text(
                text = unit,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = SpeedCyan,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SpeedTextSecondary,
                letterSpacing = 1.5.sp
            )
        }
    }
}
