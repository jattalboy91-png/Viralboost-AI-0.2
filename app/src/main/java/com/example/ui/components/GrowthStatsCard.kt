package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen
import java.text.NumberFormat
import java.util.Locale

data class GeneratedContentAnalyticsItem(
    val id: String,
    val title: String,
    val platform: String,
    val postedAgo: String,
    val baseViews: Int,
    val baseShares: Int,
    val baseGrowthPercent: Int,
    val retentionRate: String,
    val hashtagReachPercent: String,
    val trendPoints: List<Float>,
    val accentColor: Color
)

/**
 * Dashboard 'Growth Stats' Card displaying mock analytics for the user's latest generated content:
 * - Views
 * - Shares
 * - Growth Percentage
 * Along with a 7-day velocity sparkline chart, content switcher, and interactive viral boost simulation.
 */
@Composable
fun GrowthStatsCard(
    onCreateNewContent: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val contentAnalyticsList = remember {
        listOf(
            GeneratedContentAnalyticsItem(
                id = "post_1",
                title = "🔥 Haryanvi Rap & Desi Swag AI Reel #ViralBoost",
                platform = "Instagram Reels",
                postedAgo = "2h ago",
                baseViews = 48640,
                baseShares = 6420,
                baseGrowthPercent = 284,
                retentionRate = "94.2%",
                hashtagReachPercent = "88.5%",
                trendPoints = listOf(0.22f, 0.35f, 0.42f, 0.58f, 0.71f, 0.86f, 1.0f),
                accentColor = NeonCyan
            ),
            GeneratedContentAnalyticsItem(
                id = "post_2",
                title = "🚀 5 Secret AI Tools Every Creator Needs in 2026",
                platform = "YouTube Shorts",
                postedAgo = "6h ago",
                baseViews = 92400,
                baseShares = 11850,
                baseGrowthPercent = 412,
                retentionRate = "96.8%",
                hashtagReachPercent = "91.4%",
                trendPoints = listOf(0.18f, 0.30f, 0.49f, 0.65f, 0.79f, 0.92f, 1.0f),
                accentColor = GoldAccent
            ),
            GeneratedContentAnalyticsItem(
                id = "post_3",
                title = "😂 Relatable Desi Best Friend Comedy Skit",
                platform = "Facebook Reels",
                postedAgo = "1d ago",
                baseViews = 31900,
                baseShares = 4190,
                baseGrowthPercent = 195,
                retentionRate = "89.5%",
                hashtagReachPercent = "82.0%",
                trendPoints = listOf(0.25f, 0.32f, 0.45f, 0.52f, 0.68f, 0.77f, 0.90f),
                accentColor = BrandPurple
            )
        )
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    var viralBoostMultiplier by remember { mutableIntStateOf(0) }

    val activeItem = contentAnalyticsList[selectedIndex]

    val currentViews = activeItem.baseViews + (viralBoostMultiplier * 4250)
    val currentShares = activeItem.baseShares + (viralBoostMultiplier * 610)
    val currentGrowthPercent = activeItem.baseGrowthPercent + (viralBoostMultiplier * 28)

    val formattedViewsCompact = remember(currentViews) {
        if (currentViews >= 1000) {
            String.format(Locale.US, "%.1fK", currentViews / 1000f)
        } else {
            currentViews.toString()
        }
    }
    val formattedViewsExact = remember(currentViews) {
        NumberFormat.getNumberInstance(Locale.US).format(currentViews)
    }
    val formattedShares = remember(currentShares) {
        NumberFormat.getNumberInstance(Locale.US).format(currentShares)
    }

    val chartProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "growth_chart_progress"
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101528)),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(NeonCyan, BrandPurple, GoldAccent)
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("growth_stats_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Header Row: Title + Live Badge + Refresh Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(BrandPurple, NeonCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Growth Stats",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Growth Stats",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SuccessGreen.copy(alpha = 0.18f))
                                    .border(1.dp, SuccessGreen.copy(alpha = 0.7f), RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "+$currentGrowthPercent% Surge",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SuccessGreen
                                )
                            }
                        }
                        Text(
                            text = "Latest Generated Content Performance • Live Analytics",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                IconButton(
                    onClick = { viralBoostMultiplier += 1 },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .testTag("btn_refresh_growth_stats")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Growth Stats",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Latest Generated Content Selector Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                contentAnalyticsList.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) BrandPurple.copy(alpha = 0.35f)
                                else Color(0xFF1A2238)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) NeonCyan else DarkSurfaceBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedIndex = index }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("growth_stats_post_chip_$index")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) NeonCyan else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${item.platform} (${item.postedAgo})",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Generated Content Title Banner
            Surface(
                color = Color(0xFF171F36),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF2A3655)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LATEST GENERATED POST",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = activeItem.title,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("growth_stats_latest_content_title")
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = activeItem.platform,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Core 3 Analytics Metric Cards: Views, Shares, Growth Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Metric 1: Views
                GrowthMetricTile(
                    label = "Views",
                    primaryValue = formattedViewsCompact,
                    subValue = "$formattedViewsExact total",
                    badgeText = "+18% today",
                    icon = Icons.Default.Visibility,
                    accentColor = NeonCyan,
                    testTag = "growth_stats_views_metric",
                    modifier = Modifier.weight(1f)
                )

                // Metric 2: Shares
                GrowthMetricTile(
                    label = "Shares",
                    primaryValue = formattedShares,
                    subValue = "13.2% share rate",
                    badgeText = "Viral Velocity",
                    icon = Icons.Default.Share,
                    accentColor = GoldAccent,
                    testTag = "growth_stats_shares_metric",
                    modifier = Modifier.weight(1f)
                )

                // Metric 3: Growth Percentage
                GrowthMetricTile(
                    label = "Growth",
                    primaryValue = "+$currentGrowthPercent%",
                    subValue = "vs. last 7 days",
                    badgeText = "Top 1% Reach",
                    icon = Icons.Default.TrendingUp,
                    accentColor = SuccessGreen,
                    testTag = "growth_stats_growth_percentage_metric",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. 7-Day Viral Trajectory Sparkline & Retention Bar
            Surface(
                color = Color(0xFF0B0F1E),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📈 7-Day Viral Reach & Engagement Velocity",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "Retention: ${activeItem.retentionRate} • Tags: ${activeItem.hashtagReachPercent}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiamondCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sparkline + Bar Chart Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .testTag("growth_stats_sparkline_chart")
                    ) {
                        val points = activeItem.trendPoints
                        if (points.isNotEmpty()) {
                            val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                            val maxBarWidth = (size.width / (points.size * 2.4f)).coerceAtLeast(8f)

                            // Draw subtle background bars
                            points.forEachIndexed { idx, value ->
                                val animatedVal = (value * chartProgress).coerceIn(0.08f, 1f)
                                val barHeight = size.height * animatedVal
                                val xCenter = idx * stepX
                                drawRoundRect(
                                    color = activeItem.accentColor.copy(alpha = 0.18f),
                                    topLeft = Offset(
                                        x = (xCenter - maxBarWidth / 2f).coerceIn(0f, size.width - maxBarWidth),
                                        y = size.height - barHeight
                                    ),
                                    size = Size(maxBarWidth, barHeight),
                                    cornerRadius = CornerRadius(6f, 6f)
                                )
                            }

                            // Draw smooth trend line
                            val linePath = Path()
                            points.forEachIndexed { idx, value ->
                                val animatedVal = (value * chartProgress).coerceIn(0.08f, 1f)
                                val px = idx * stepX
                                val py = size.height - (size.height * animatedVal * 0.9f) - 4f
                                if (idx == 0) {
                                    linePath.moveTo(px, py)
                                } else {
                                    linePath.lineTo(px, py)
                                }
                            }

                            drawPath(
                                path = linePath,
                                brush = Brush.horizontalGradient(
                                    listOf(BrandPurple, NeonCyan, SuccessGreen)
                                ),
                                style = Stroke(width = 5f, cap = StrokeCap.Round)
                            )

                            // Draw data point nodes
                            points.forEachIndexed { idx, value ->
                                val animatedVal = (value * chartProgress).coerceIn(0.08f, 1f)
                                val px = idx * stepX
                                val py = size.height - (size.height * animatedVal * 0.9f) - 4f
                                drawCircle(
                                    color = Color(0xFF0B0F1E),
                                    radius = 7f,
                                    center = Offset(px, py)
                                )
                                drawCircle(
                                    color = if (idx == points.lastIndex) SuccessGreen else NeonCyan,
                                    radius = 4.5f,
                                    center = Offset(px, py)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Action Buttons: Simulate Viral Boost & Generate New Content
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viralBoostMultiplier += 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("btn_boost_growth_stats")
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Boost Reach 🚀",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onCreateNewContent,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.2.dp, NeonCyan),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("btn_generate_from_growth_stats")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "New AI Post ✨",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }
    }
}

@Composable
private fun GrowthMetricTile(
    label: String,
    primaryValue: String,
    subValue: String,
    badgeText: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF171F36),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        modifier = modifier.testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCBD5E1)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = primaryValue,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subValue,
                fontSize = 9.5.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    maxLines = 1
                )
            }
        }
    }
}
