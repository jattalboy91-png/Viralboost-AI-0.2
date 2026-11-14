package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.collectAsState
import com.example.monetization.AdManager
import com.example.monetization.AdNetwork
import com.example.monetization.MonetizationConstants
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

@Composable
fun RewardedAdDialog(
    rewardDiamonds: Int = MonetizationConstants.REWARDED_AD_DIAMOND_REWARD,
    rewardedUnitId: String = AdManager.activeAdMobRewardedUnitId,
    unityPlacementId: String = AdManager.activeUnityRewardedPlacementId,
    onDismiss: () -> Unit,
    onRewardEarned: () -> Unit
) {
    val decision by AdManager.lastRewardedDecision.collectAsState()
    val mediationState by AdManager.mediationState.collectAsState()
    val isUnity = decision.network == AdNetwork.UNITY_ADS
    val activeUnityGameId = mediationState.unityGameId.ifBlank { AdManager.activeUnityGameId }
    val activeUnitOrPlacement = if (isUnity) {
        unityPlacementId.ifBlank { mediationState.unityRewardedPlacementId.ifBlank { MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID } }
    } else {
        rewardedUnitId.ifBlank { AdManager.activeAdMobRewardedUnitId.ifBlank { MonetizationConstants.ADMOB_REWARDED_UNIT_ID } }
    }

    var secondsLeft by remember { mutableIntStateOf(5) }
    var isFinished by remember { mutableIntStateOf(0) }.let { remember { mutableStateOf(false) } }

    val totalDurationSeconds = 5
    val progress by animateFloatAsState(
        targetValue = (totalDurationSeconds - secondsLeft).toFloat() / totalDurationSeconds,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "AdProgress"
    )

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        isFinished = true
    }

    Dialog(
        onDismissRequest = {
            if (isFinished) onDismiss()
        },
        properties = DialogProperties(dismissOnBackPress = isFinished, dismissOnClickOutside = isFinished)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("rewarded_ad_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandPurple)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with Timer / Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEAB308).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isUnity) {
                                "Unity Ads [$activeUnityGameId] • $activeUnitOrPlacement"
                            } else {
                                "AdMob Rewarded Video • Turn #${decision.requestNumber}"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }

                    if (isFinished) {
                        IconButton(
                            onClick = {
                                onRewardEarned()
                                onDismiss()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Ad",
                                tint = Color.White
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${secondsLeft}s",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ad Video Simulation Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                            )
                        )
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (isFinished) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Reward Earned",
                                tint = SuccessGreen,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Reward Unlocked!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "+$rewardDiamonds Diamonds Credited",
                                fontSize = 13.sp,
                                color = DiamondCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(BrandPurple.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Playing Ad",
                                    tint = BrandPurple,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "ViralBoost AI Creator Engine",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isUnity) {
                                    "Unity Game ID: $activeUnityGameId • Placement: $activeUnitOrPlacement"
                                } else {
                                    "AdMob Unit: ${activeUnitOrPlacement.take(24)}..."
                                },
                                fontSize = 10.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = DiamondCyan,
                    trackColor = DarkSurfaceBorder
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Reward Claim Action
                if (isFinished) {
                    Button(
                        onClick = {
                            onRewardEarned()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("claim_ad_reward_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Claim $rewardDiamonds Diamonds Now",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                } else {
                    Text(
                        text = "Watch till the end to receive exactly $rewardDiamonds Diamonds",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
