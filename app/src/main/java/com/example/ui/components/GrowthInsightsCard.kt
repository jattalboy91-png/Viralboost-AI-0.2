package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen
import com.example.util.SocialShareHelper
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

data class GrowthInsightPreset(
    val id: String,
    val contentTitle: String,
    val platform: String,
    val predictedViewsBase: Int,
    val predictedViewsLow: Int,
    val predictedViewsHigh: Int,
    val shareabilityScoreBase: Int,
    val hookRetentionPercent: Int,
    val dmShareVelocityPercent: Float,
    val saveToViewPercent: Float,
    val engagementTrendSeries: List<Float>,
    val baselineReachSeries: List<Float>,
    val xLabels: List<String>
)

/**
 * Dashboard 'Growth Insights' Card displaying predictive mock analytics for generated AI content:
 * - 'Predicted Views' (with confidence interval & viral multiplier)
 * - 'Shareability Score' (with Compose Canvas radial gauge & viral trigger breakdown)
 * - 'Engagement Trend' multi-series smooth cubic Bezier area/line graph with interactive data point inspection.
 */
@Composable
fun GrowthInsightsCard(
    modifier: Modifier = Modifier,
    onOptimizeWithAi: () -> Unit = {}
) {
    val context = LocalContext.current

    val presets = remember {
        listOf(
            GrowthInsightPreset(
                id = "reels_viral_hook",
                contentTitle = "✨ AI Pattern-Interrupt Reel: 'Stop Doing This in 2026'",
                platform = "Instagram Reels",
                predictedViewsBase = 184500,
                predictedViewsLow = 148000,
                predictedViewsHigh = 242000,
                shareabilityScoreBase = 96,
                hookRetentionPercent = 98,
                dmShareVelocityPercent = 21.4f,
                saveToViewPercent = 14.8f,
                engagementTrendSeries = listOf(0.24f, 0.38f, 0.46f, 0.64f, 0.78f, 0.89f, 0.97f),
                baselineReachSeries = listOf(0.15f, 0.22f, 0.29f, 0.36f, 0.44f, 0.51f, 0.58f),
                xLabels = listOf("D1", "D2", "D3", "D4", "D5", "D6", "D7")
            ),
            GrowthInsightPreset(
                id = "shorts_ai_tech",
                contentTitle = "🚀 5 Secret Gemini AI Workflows for 10x Faster Growth",
                platform = "YouTube Shorts",
                predictedViewsBase = 312800,
                predictedViewsLow = 260000,
                predictedViewsHigh = 410000,
                shareabilityScoreBase = 98,
                hookRetentionPercent = 99,
                dmShareVelocityPercent = 24.9f,
                saveToViewPercent = 18.2f,
                engagementTrendSeries = listOf(0.30f, 0.45f, 0.59f, 0.72f, 0.84f, 0.93f, 1.0f),
                baselineReachSeries = listOf(0.18f, 0.25f, 0.33f, 0.41f, 0.48f, 0.55f, 0.63f),
                xLabels = listOf("D1", "D2", "D3", "D4", "D5", "D6", "D7")
            ),
            GrowthInsightPreset(
                id = "tiktok_story_loop",
                contentTitle = "🔥 Seamless 60s Storytelling Loop + High-CTR Hashtags",
                platform = "TikTok",
                predictedViewsBase = 129400,
                predictedViewsLow = 98000,
                predictedViewsHigh = 175000,
                shareabilityScoreBase = 92,
                hookRetentionPercent = 95,
                dmShareVelocityPercent = 18.6f,
                saveToViewPercent = 12.9f,
                engagementTrendSeries = listOf(0.20f, 0.31f, 0.49f, 0.57f, 0.71f, 0.83f, 0.92f),
                baselineReachSeries = listOf(0.14f, 0.19f, 0.26f, 0.31f, 0.38f, 0.45f, 0.52f),
                xLabels = listOf("D1", "D2", "D3", "D4", "D5", "D6", "D7")
            )
        )
    }

    val timeframes = remember { listOf("24H", "7D", "30D", "90D") }
    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    var selectedTimeframeIndex by remember { mutableIntStateOf(1) } // Default "7D"
    var boostCount by remember { mutableIntStateOf(0) }
    var selectedPointIndex by remember { mutableIntStateOf(6) } // Default latest point D7

    val activePreset = presets[selectedPresetIndex]
    val timeframeMultiplier = when (selectedTimeframeIndex) {
        0 -> 0.45f
        1 -> 1.0f
        2 -> 2.8f
        else -> 6.4f
    }

    val predictedViews = ((activePreset.predictedViewsBase * timeframeMultiplier).roundToInt()) + (boostCount * 18500)
    val predictedLow = ((activePreset.predictedViewsLow * timeframeMultiplier).roundToInt()) + (boostCount * 14000)
    val predictedHigh = ((activePreset.predictedViewsHigh * timeframeMultiplier).roundToInt()) + (boostCount * 24000)
    val shareabilityScore = (activePreset.shareabilityScoreBase + boostCount).coerceAtMost(100)

    val formattedPredictedViews = remember(predictedViews) {
        formatCompactCount(predictedViews)
    }
    val formattedRange = remember(predictedLow, predictedHigh) {
        "${formatCompactCount(predictedLow)} – ${formatCompactCount(predictedHigh)}"
    }

    val animatedGaugeProgress by animateFloatAsState(
        targetValue = shareabilityScore / 100f,
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "shareability_gauge"
    )

    val animatedTrendProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "engagement_trend_anim"
    )

    val activePointValue = activePreset.engagementTrendSeries.getOrElse(selectedPointIndex) { 0.95f }
    val selectedDayViews = ((predictedViews * (0.35f + activePointValue * 0.65f)) / 7f).roundToInt()
    val selectedDayEngagementRate = String.format(
        Locale.US,
        "%.1f%%",
        (activePointValue * 18.4f) + (boostCount * 0.8f)
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1324)),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(BrandPurple, NeonCyan, SuccessGreen)
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("growth_insights_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Header Row: Growth Insights Title + Timeframe Selector Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonCyan, BrandPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Growth Insights",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Growth Insights",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = NeonCyan.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "AI Forecast",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Predicted Views • Shareability Score • Engagement Trend",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Timeframe Pills (24H, 7D, 30D, 90D)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    timeframes.forEachIndexed { idx, tf ->
                        val selected = idx == selectedTimeframeIndex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selected) BrandPurple else Color(0xFF1A2238))
                                .border(
                                    1.dp,
                                    if (selected) NeonCyan else Color(0xFF2A3655),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedTimeframeIndex = idx }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                .testTag("growth_insights_timeframe_$tf")
                        ) {
                            Text(
                                text = tf,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Generated Content Preset Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEachIndexed { idx, item ->
                    val isSelected = idx == selectedPresetIndex
                    Surface(
                        color = if (isSelected) BrandPurple.copy(alpha = 0.32f) else Color(0xFF171F36),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) NeonCyan else DarkSurfaceBorder
                        ),
                        modifier = Modifier
                            .clickable { selectedPresetIndex = idx }
                            .testTag("growth_insights_preset_$idx")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SuccessGreen else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.platform,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = activePreset.contentTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE2E8F0),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.testTag("growth_insights_content_title")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Top Split Row: 'Predicted Views' Card & 'Shareability Score' Radial Gauge Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left Card: Predicted Views
                Surface(
                    color = Color(0xFF151D34),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("growth_insights_predicted_views")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Predicted Views",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCBD5E1)
                            )
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Predicted Views",
                                tint = NeonCyan,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = formattedPredictedViews,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.testTag("growth_insights_predicted_views_value")
                        )

                        Text(
                            text = "Est. Range: $formattedRange",
                            fontSize = 10.sp,
                            color = DiamondCyan,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = SuccessGreen.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "+340% Viral Lift",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "${activePreset.hookRetentionPercent}% Hook",
                                fontSize = 9.5.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Right Card: Shareability Score with Radial Gauge
                Surface(
                    color = Color(0xFF151D34),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("growth_insights_shareability_score")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Shareability Score",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCBD5E1)
                            )
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Shareability Score",
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Radial Gauge Canvas
                            Box(
                                modifier = Modifier.size(52.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(52.dp)) {
                                    val strokeWidth = 11f
                                    val diameter = size.minDimension - strokeWidth
                                    val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                                    // Background track arc
                                    drawArc(
                                        color = Color(0xFF2A3655),
                                        startAngle = 135f,
                                        sweepAngle = 270f,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = Size(diameter, diameter),
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                    // Foreground gradient arc
                                    drawArc(
                                        brush = Brush.sweepGradient(
                                            listOf(BrandPurple, NeonCyan, SuccessGreen, GoldAccent)
                                        ),
                                        startAngle = 135f,
                                        sweepAngle = 270f * animatedGaugeProgress,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = Size(diameter, diameter),
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                                Text(
                                    text = "$shareabilityScore%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.testTag("growth_insights_shareability_value")
                                )
                            }

                            Column {
                                Text(
                                    text = if (shareabilityScore >= 95) "Ultra-Viral 🔥" else "High Velocity ⚡",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldAccent
                                )
                                Text(
                                    text = "DM Share: ${activePreset.dmShareVelocityPercent}%",
                                    fontSize = 9.5.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = "Saves: ${activePreset.saveToViewPercent}%",
                                    fontSize = 9.5.sp,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Interactive 'Engagement Trend' Graph Section (D3 / Recharts-style Multi-Series Area & Cubic Bezier Chart)
            Surface(
                color = Color(0xFF090D1A),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFF222F49)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "📊 Engagement Trend & Viral Trajectory",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Tap chart nodes to inspect daily predicted engagement",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        // Chart Legend
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(NeonCyan)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Boosted", fontSize = 9.5.sp, color = Color(0xFFCBD5E1))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BrandPurple)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Baseline", fontSize = 9.5.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Interactive Tooltip Pill for Selected Data Point
                    Surface(
                        color = Color(0xFF151F38),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("growth_insights_trend_tooltip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Point: ${activePreset.xLabels.getOrElse(selectedPointIndex) { "D7" }} (${timeframes[selectedTimeframeIndex]})",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Text(
                                text = "Est. Views: ${formatCompactCount(selectedDayViews)}",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = "Eng. Rate: $selectedDayEngagementRate",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SuccessGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Multi-Series Smooth Cubic Bezier Area & Line Chart Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .pointerInput(activePreset) {
                                detectTapGestures { tapOffset ->
                                    val count = activePreset.engagementTrendSeries.size
                                    if (count > 1 && size.width > 0) {
                                        val stepX = size.width.toFloat() / (count - 1)
                                        val tappedIdx = ((tapOffset.x / stepX).roundToInt()).coerceIn(0, count - 1)
                                        selectedPointIndex = tappedIdx
                                    }
                                }
                            }
                            .testTag("growth_insights_engagement_trend_chart")
                    ) {
                        val primaryPoints = activePreset.engagementTrendSeries
                        val baselinePoints = activePreset.baselineReachSeries
                        val count = primaryPoints.size
                        if (count > 1) {
                            val horizontalPadding = 10f
                            val usableWidth = (size.width - horizontalPadding * 2).coerceAtLeast(1f)
                            val usableHeight = (size.height - 16f).coerceAtLeast(1f)
                            val stepX = usableWidth / (count - 1)

                            // 1. Horizontal Dashed Reference Grid Lines (25%, 50%, 75%, 100%)
                            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            listOf(0.2f, 0.5f, 0.8f).forEach { fraction ->
                                val y = size.height - (usableHeight * fraction)
                                drawLine(
                                    color = Color(0xFF1E293B),
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 1.5f,
                                    pathEffect = dashEffect
                                )
                            }

                            // Helper to map (index, normalizedValue) -> Canvas Offset
                            fun pointOffset(index: Int, value: Float): Offset {
                                val boostedVal = (value * animatedTrendProgress + (boostCount * 0.02f)).coerceIn(0.08f, 1f)
                                val x = horizontalPadding + index * stepX
                                val y = size.height - 6f - (usableHeight * boostedVal * 0.9f)
                                return Offset(x, y)
                            }

                            // 2. Draw Baseline Series (BrandPurple smooth curve)
                            val baselinePath = Path()
                            baselinePoints.forEachIndexed { idx, v ->
                                val curr = pointOffset(idx, v)
                                if (idx == 0) {
                                    baselinePath.moveTo(curr.x, curr.y)
                                } else {
                                    val prev = pointOffset(idx - 1, baselinePoints[idx - 1])
                                    val ctrlX = (prev.x + curr.x) / 2f
                                    baselinePath.cubicTo(ctrlX, prev.y, ctrlX, curr.y, curr.x, curr.y)
                                }
                            }
                            drawPath(
                                path = baselinePath,
                                color = BrandPurple.copy(alpha = 0.65f),
                                style = Stroke(
                                    width = 3.5f,
                                    cap = StrokeCap.Round,
                                    pathEffect = dashEffect
                                )
                            )

                            // 3. Draw Primary Engagement Trend Series (Cubic Bezier + Gradient Area Fill)
                            val linePath = Path()
                            val areaPath = Path()
                            primaryPoints.forEachIndexed { idx, v ->
                                val curr = pointOffset(idx, v)
                                if (idx == 0) {
                                    linePath.moveTo(curr.x, curr.y)
                                    areaPath.moveTo(curr.x, size.height)
                                    areaPath.lineTo(curr.x, curr.y)
                                } else {
                                    val prev = pointOffset(idx - 1, primaryPoints[idx - 1])
                                    val ctrlX = (prev.x + curr.x) / 2f
                                    linePath.cubicTo(ctrlX, prev.y, ctrlX, curr.y, curr.x, curr.y)
                                    areaPath.cubicTo(ctrlX, prev.y, ctrlX, curr.y, curr.x, curr.y)
                                }
                                if (idx == primaryPoints.lastIndex) {
                                    areaPath.lineTo(curr.x, size.height)
                                    areaPath.close()
                                }
                            }

                            drawPath(
                                path = areaPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        NeonCyan.copy(alpha = 0.36f),
                                        BrandPurple.copy(alpha = 0.14f),
                                        Color.Transparent
                                    )
                                )
                            )

                            drawPath(
                                path = linePath,
                                brush = Brush.horizontalGradient(
                                    listOf(BrandPurple, NeonCyan, SuccessGreen)
                                ),
                                style = Stroke(
                                    width = 6f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )

                            // 4. Vertical Crosshair & Data Nodes
                            primaryPoints.forEachIndexed { idx, v ->
                                val pt = pointOffset(idx, v)
                                val isSelectedNode = idx == selectedPointIndex
                                if (isSelectedNode) {
                                    drawLine(
                                        color = NeonCyan.copy(alpha = 0.45f),
                                        start = Offset(pt.x, 4f),
                                        end = Offset(pt.x, size.height),
                                        strokeWidth = 2f,
                                        pathEffect = dashEffect
                                    )
                                    drawCircle(
                                        color = NeonCyan.copy(alpha = 0.28f),
                                        radius = 13f,
                                        center = pt
                                    )
                                }
                                drawCircle(
                                    color = Color(0xFF090D1A),
                                    radius = if (isSelectedNode) 8f else 6f,
                                    center = pt
                                )
                                drawCircle(
                                    color = if (isSelectedNode) GoldAccent else if (idx == primaryPoints.lastIndex) SuccessGreen else NeonCyan,
                                    radius = if (isSelectedNode) 5.5f else 4f,
                                    center = pt
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // X-Axis Day Labels Row (Tappable)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        activePreset.xLabels.forEachIndexed { idx, label ->
                            val isSelected = idx == selectedPointIndex
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) GoldAccent else Color(0xFF94A3B8),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { selectedPointIndex = idx }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Bottom Actions: Simulate AI Boost + Share Insights + Optimize Content
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { boostCount += 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("btn_boost_growth_insights")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Simulate Boost",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = {
                        val shareSummary = buildString {
                            appendLine("📊 ViralBoost AI • Growth Insights Forecast")
                            appendLine("📌 Content: ${activePreset.contentTitle}")
                            appendLine("📱 Platform: ${activePreset.platform} (${timeframes[selectedTimeframeIndex]})")
                            appendLine("👁️ Predicted Views: $formattedPredictedViews (Range: $formattedRange)")
                            appendLine("🔥 Shareability Score: $shareabilityScore/100 (Hook Retention: ${activePreset.hookRetentionPercent}%)")
                            appendLine("📈 Peak Engagement Rate: $selectedDayEngagementRate")
                        }
                        SocialShareHelper.shareTextViaAndroidIntent(
                            context = context,
                            subject = "ViralBoost AI Growth Insights (${activePreset.platform})",
                            shareText = shareSummary,
                            chooserTitle = "Share Growth Insights via"
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SuccessGreen),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("btn_share_growth_insights")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Growth Insights",
                        tint = SuccessGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }

                OutlinedButton(
                    onClick = onOptimizeWithAi,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, NeonCyan),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("btn_optimize_from_growth_insights")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI Studio",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }
    }
}

private fun formatCompactCount(value: Int): String {
    return when {
        value >= 1_000_000 -> String.format(Locale.US, "%.2fM", value / 1_000_000f)
        value >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1_000f)
        else -> NumberFormat.getNumberInstance(Locale.US).format(value)
    }
}
