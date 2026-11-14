package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class ParticleShapeType {
    DIAMOND_GEM,
    STAR_SPARKLE,
    GOLD_COIN,
    CONFETTI_SHARD
}

data class DiamondBurstEvent(
    val id: Long = System.currentTimeMillis(),
    val diamondsEarned: Int,
    val coinsEarned: Int = 0,
    val taskLabel: String = "Task Completed!"
)

private data class CanvasParticle(
    val angleRad: Float,
    val speedFactor: Float,
    val upwardBias: Float,
    val gravityFactor: Float,
    val baseRadiusDp: Float,
    val rotationSpeedDeg: Float,
    val initialRotationDeg: Float,
    val color: Color,
    val secondaryColor: Color,
    val shapeType: ParticleShapeType,
    val delayFraction: Float
)

/**
 * Global controller that triggers the Compose Canvas Diamond Particle Explosion
 * whenever a user completes a task and earns diamond rewards anywhere in the app.
 */
object DiamondRewardParticleController {
    private val _activeBurst = MutableStateFlow<DiamondBurstEvent?>(null)
    val activeBurst: StateFlow<DiamondBurstEvent?> = _activeBurst.asStateFlow()

    fun triggerDiamondRewardBurst(
        diamondsEarned: Int,
        coinsEarned: Int = 0,
        taskLabel: String = "Task Completed!"
    ) {
        if (diamondsEarned <= 0 && coinsEarned <= 0) return
        _activeBurst.value = DiamondBurstEvent(
            id = System.nanoTime(),
            diamondsEarned = diamondsEarned,
            coinsEarned = coinsEarned,
            taskLabel = taskLabel
        )
    }

    fun clearBurst(eventId: Long) {
        if (_activeBurst.value?.id == eventId) {
            _activeBurst.value = null
        }
    }
}

/**
 * Full-screen non-blocking Compose Canvas particle explosion host.
 * Renders faceted diamonds, golden coins, 4-point starbursts, and shockwave rings
 * whenever [DiamondRewardParticleController.activeBurst] emits a new reward event.
 */
@Composable
fun DiamondParticleExplosionHost(
    modifier: Modifier = Modifier
) {
    val activeBurst by DiamondRewardParticleController.activeBurst.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        activeBurst?.let { burst ->
            DiamondParticleCanvasEffect(
                burstEvent = burst,
                onAnimationFinished = {
                    DiamondRewardParticleController.clearBurst(burst.id)
                }
            )
        }
    }
}

@Composable
fun DiamondParticleCanvasEffect(
    burstEvent: DiamondBurstEvent,
    modifier: Modifier = Modifier,
    particleCount: Int = 68,
    durationMillis: Int = 2200,
    onAnimationFinished: () -> Unit = {}
) {
    val progress = remember(burstEvent.id) { Animatable(0f) }

    LaunchedEffect(burstEvent.id) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
        )
        onAnimationFinished()
    }

    val palette = remember {
        listOf(
            Color(0xFF00E5FF), // Diamond Cyan
            Color(0xFF38BDF8), // Ice Blue
            Color(0xFFFFD700), // Gold Accent
            Color(0xFFA855F7), // Neon Purple
            Color(0xFF4ADE80), // Emerald Spark
            Color(0xFFF472B6), // Pink Aura
            Color(0xFFFFFFFF)  // Pure White Glint
        )
    }

    // Generate deterministic particle physics specs per burst ID
    val particles = remember(burstEvent.id, particleCount) {
        val rng = Random(burstEvent.id)
        List(particleCount) { index ->
            val shape = when {
                index % 3 == 0 -> ParticleShapeType.DIAMOND_GEM
                index % 4 == 1 -> ParticleShapeType.STAR_SPARKLE
                index % 5 == 2 -> ParticleShapeType.GOLD_COIN
                else -> ParticleShapeType.CONFETTI_SHARD
            }
            val primaryColor = when (shape) {
                ParticleShapeType.DIAMOND_GEM -> if (index % 2 == 0) Color(0xFF00E5FF) else Color(0xFF7DD3FC)
                ParticleShapeType.GOLD_COIN -> Color(0xFFFFD700)
                else -> palette[rng.nextInt(palette.size)]
            }
            val secondaryColor = when (shape) {
                ParticleShapeType.DIAMOND_GEM -> Color.White
                ParticleShapeType.GOLD_COIN -> Color(0xFFFFA000)
                else -> Color.White.copy(alpha = 0.85f)
            }
            CanvasParticle(
                angleRad = (rng.nextFloat() * 2f * PI.toFloat()),
                speedFactor = 0.22f + rng.nextFloat() * 0.72f,
                upwardBias = 0.18f + rng.nextFloat() * 0.35f,
                gravityFactor = 0.38f + rng.nextFloat() * 0.45f,
                baseRadiusDp = when (shape) {
                    ParticleShapeType.DIAMOND_GEM -> 9f + rng.nextFloat() * 8f
                    ParticleShapeType.STAR_SPARKLE -> 7f + rng.nextFloat() * 7f
                    ParticleShapeType.GOLD_COIN -> 7f + rng.nextFloat() * 5f
                    ParticleShapeType.CONFETTI_SHARD -> 5f + rng.nextFloat() * 6f
                },
                rotationSpeedDeg = (rng.nextFloat() - 0.5f) * 720f,
                initialRotationDeg = rng.nextFloat() * 360f,
                color = primaryColor,
                secondaryColor = secondaryColor,
                shapeType = shape,
                delayFraction = rng.nextFloat() * 0.14f
            )
        }
    }

    val currentProgress = progress.value
    val showBanner = currentProgress in 0.02f..0.88f
    val bannerScale by animateFloatAsState(
        targetValue = if (showBanner) 1f else 0.75f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "reward_banner_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("diamond_particle_canvas_overlay"),
        contentAlignment = Alignment.TopCenter
    ) {
        // 1. High-frame-rate Compose Canvas drawing shockwaves, diamond gems, sparkles, and coins
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("diamond_reward_particle_canvas")
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            if (canvasWidth <= 0f || canvasHeight <= 0f) return@Canvas

            val originX = canvasWidth * 0.5f
            val originY = canvasHeight * 0.34f
            val maxReach = minOf(canvasWidth, canvasHeight) * 0.68f

            // Expanding dual shockwave rings at the start of the explosion
            if (currentProgress < 0.65f) {
                val ringProgress = (currentProgress / 0.65f).coerceIn(0f, 1f)
                val ringAlpha = (1f - ringProgress).coerceIn(0f, 1f) * 0.55f
                val outerRadius = maxReach * 0.85f * ringProgress

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00E5FF).copy(alpha = ringAlpha * 0.45f),
                            Color(0xFFFFD700).copy(alpha = ringAlpha * 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(originX, originY),
                        radius = outerRadius.coerceAtLeast(1f)
                    ),
                    radius = outerRadius,
                    center = Offset(originX, originY)
                )

                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = ringAlpha),
                    radius = outerRadius,
                    center = Offset(originX, originY),
                    style = Stroke(width = 3.dp.toPx() * (1f - ringProgress * 0.6f))
                )
            }

            // Render each particle
            particles.forEach { particle ->
                val localT = ((currentProgress - particle.delayFraction) / (1f - particle.delayFraction))
                    .coerceIn(0f, 1f)
                if (localT <= 0f || localT >= 1f) return@forEach

                // Smooth ease-out radial expansion + downward gravity arc
                val decelT = 1f - (1f - localT) * (1f - localT)
                val radialDist = maxReach * particle.speedFactor * decelT

                val dx = cos(particle.angleRad) * radialDist
                val dyBurst = sin(particle.angleRad) * radialDist - (maxReach * particle.upwardBias * decelT)
                val dyGravity = maxReach * particle.gravityFactor * (localT * localT)

                val px = originX + dx
                val py = originY + dyBurst + dyGravity

                // Fade in quickly, stay bright, fade out smoothly in the final 30%
                val alpha = when {
                    localT < 0.1f -> localT / 0.1f
                    localT > 0.7f -> ((1f - localT) / 0.3f).coerceIn(0f, 1f)
                    else -> 1f
                }

                val scaleFactor = when {
                    localT < 0.15f -> 0.5f + (localT / 0.15f) * 0.7f
                    else -> 1.2f - 0.5f * localT
                }

                val radiusPx = particle.baseRadiusDp.dp.toPx() * scaleFactor
                val currentRotation = particle.initialRotationDeg + particle.rotationSpeedDeg * localT

                withTransform({
                    translate(left = px, top = py)
                    rotate(degrees = currentRotation, pivot = Offset.Zero)
                }) {
                    when (particle.shapeType) {
                        ParticleShapeType.DIAMOND_GEM -> {
                            drawFacetedDiamondGem(
                                radius = radiusPx,
                                primaryColor = particle.color.copy(alpha = alpha),
                                highlightColor = particle.secondaryColor.copy(alpha = alpha * 0.9f)
                            )
                        }
                        ParticleShapeType.STAR_SPARKLE -> {
                            drawFourPointStarSparkle(
                                radius = radiusPx,
                                color = particle.color.copy(alpha = alpha)
                            )
                        }
                        ParticleShapeType.GOLD_COIN -> {
                            drawGoldenCoinParticle(
                                radius = radiusPx,
                                outerColor = particle.color.copy(alpha = alpha),
                                innerColor = particle.secondaryColor.copy(alpha = alpha)
                            )
                        }
                        ParticleShapeType.CONFETTI_SHARD -> {
                            drawRect(
                                color = particle.color.copy(alpha = alpha),
                                topLeft = Offset(-radiusPx * 0.6f, -radiusPx * 0.3f),
                                size = Size(radiusPx * 1.2f, radiusPx * 0.6f)
                            )
                        }
                    }
                }
            }
        }

        // 2. Floating Celebration Badge Pill at Top-Center
        AnimatedVisibility(
            visible = showBanner,
            enter = fadeIn(tween(180)) + scaleIn(tween(220, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(250)) + scaleOut(tween(250)),
            modifier = Modifier.padding(top = 78.dp)
        ) {
            Surface(
                modifier = Modifier
                    .scale(bannerScale)
                    .testTag("diamond_particle_reward_pill"),
                shape = RoundedCornerShape(50),
                color = Color(0xEE0F172A),
                border = BorderStroke(
                    2.dp,
                    Brush.horizontalGradient(
                        listOf(Color(0xFF00E5FF), Color(0xFFFFD700), Color(0xFFA855F7))
                    )
                ),
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF00E5FF), Color(0xFF6366F1))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Diamonds Earned",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = buildString {
                                    if (burstEvent.diamondsEarned > 0) {
                                        append("+${burstEvent.diamondsEarned} 💎 Diamonds")
                                    }
                                    if (burstEvent.coinsEarned > 0) {
                                        if (isNotEmpty()) append(" & ")
                                        append("+${burstEvent.coinsEarned} 🪙 Coins")
                                    }
                                },
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = burstEvent.taskLabel,
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Draws a multi-faceted cut diamond gem centered at (0, 0) on the Compose Canvas.
 */
private fun DrawScope.drawFacetedDiamondGem(
    radius: Float,
    primaryColor: Color,
    highlightColor: Color
) {
    val crownTopY = -radius * 0.78f
    val girdleY = -radius * 0.22f
    val culetBottomY = radius * 1.05f
    val halfWidth = radius * 0.92f
    val crownHalfWidth = radius * 0.56f

    // Outer diamond silhouette path
    val outerPath = Path().apply {
        moveTo(-crownHalfWidth, crownTopY)
        lineTo(crownHalfWidth, crownTopY)
        lineTo(halfWidth, girdleY)
        lineTo(0f, culetBottomY)
        lineTo(-halfWidth, girdleY)
        close()
    }
    drawPath(path = outerPath, color = primaryColor)

    // Upper table & crown highlight facet
    val crownPath = Path().apply {
        moveTo(-crownHalfWidth * 0.85f, crownTopY)
        lineTo(crownHalfWidth * 0.85f, crownTopY)
        lineTo(0f, girdleY * 0.8f)
        close()
    }
    drawPath(path = crownPath, color = highlightColor)

    // Crisp facet lines
    drawLine(
        color = highlightColor,
        start = Offset(-halfWidth, girdleY),
        end = Offset(halfWidth, girdleY),
        strokeWidth = (radius * 0.11f).coerceAtLeast(1f)
    )
    drawLine(
        color = highlightColor,
        start = Offset(0f, girdleY),
        end = Offset(0f, culetBottomY),
        strokeWidth = (radius * 0.09f).coerceAtLeast(1f)
    )
}

/**
 * Draws a 4-point glowing star sparkle centered at (0, 0) on the Compose Canvas.
 */
private fun DrawScope.drawFourPointStarSparkle(
    radius: Float,
    color: Color
) {
    val inner = radius * 0.26f
    val starPath = Path().apply {
        moveTo(0f, -radius)
        lineTo(inner, -inner)
        lineTo(radius, 0f)
        lineTo(inner, inner)
        lineTo(0f, radius)
        lineTo(-inner, inner)
        lineTo(-radius, 0f)
        lineTo(-inner, -inner)
        close()
    }
    drawPath(path = starPath, color = color)
}

/**
 * Draws a golden coin particle centered at (0, 0) on the Compose Canvas.
 */
private fun DrawScope.drawGoldenCoinParticle(
    radius: Float,
    outerColor: Color,
    innerColor: Color
) {
    drawCircle(color = outerColor, radius = radius, center = Offset.Zero)
    drawCircle(color = innerColor, radius = radius * 0.68f, center = Offset.Zero)
    drawCircle(
        color = outerColor,
        radius = radius * 0.42f,
        center = Offset.Zero,
        style = Stroke(width = (radius * 0.16f).coerceAtLeast(1f))
    )
}
