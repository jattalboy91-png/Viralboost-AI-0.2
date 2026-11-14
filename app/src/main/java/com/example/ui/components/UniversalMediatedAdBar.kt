package com.example.ui.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monetization.AdFormat
import com.example.monetization.AdManager
import com.example.monetization.AdNetwork
import kotlinx.coroutines.delay

/**
 * Universal Mediated Ad Dock that renders BOTH:
 * 1) A Mediated Rewarded Ad Trigger Strip (Unity Ads `BP_Rewarded_Android` [Game ID 800427608] <-> Google AdMob 50/50 rotation & automatic fallback)
 * 2) A Permanent Mediated Banner Ad (`BP_Banner_Android` [Game ID 800427608] <-> Google AdMob Banner rotation & fallback)
 *
 * Designed to be embedded in EVERY screen, sub-page, and feature dialog across the app.
 */
@Composable
fun UniversalMediatedAdDock(
    isVip: Boolean,
    admobBannerUnitId: String = AdManager.activeBannerAdUnitId,
    unityBannerPlacementId: String = AdManager.activeUnityBannerPlacementId.ifBlank { AdManager.DEFAULT_UNITY_BANNER_PLACEMENT_ID },
    admobRewardedUnitId: String = AdManager.activeRewardedAdUnitId,
    unityRewardedPlacementId: String = AdManager.activeUnityRewardedPlacementId.ifBlank { AdManager.DEFAULT_UNITY_REWARDED_PLACEMENT_ID },
    featureLabel: String = "ViralBoost AI",
    onRewardEarned: (diamonds: Int, coins: Int) -> Unit = { _, _ -> },
    onUpgradeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (isVip) return

    val context = LocalContext.current
    val mediationState by AdManager.mediationState.collectAsState()
    val lastRewardedDecision by AdManager.lastRewardedDecision.collectAsState()

    var showFallbackRewardedModal by remember { mutableStateOf(false) }

    val resolvedUnityGameId = mediationState.unityGameId.ifBlank { AdManager.DEFAULT_UNITY_GAME_ID }
    val resolvedUnityRewardedId = unityRewardedPlacementId.ifBlank { AdManager.DEFAULT_UNITY_REWARDED_PLACEMENT_ID }
    val resolvedAdmobRewardedId = admobRewardedUnitId.ifBlank { AdManager.TEST_REWARDED_ID }

    val bothActive = mediationState.admobActive && mediationState.unityActive
    val nextNetworkLabel = when {
        bothActive -> "Unity ($resolvedUnityRewardedId) ⇄ AdMob"
        mediationState.unityActive -> "Unity Ads ($resolvedUnityRewardedId)"
        else -> "Google AdMob Rewarded"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF080C18))
            .testTag("universal_mediated_ad_dock")
    ) {
        // 1. Universal Rewarded Ad Trigger Strip (BP_Rewarded_Android / AdMob)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF141E38), Color(0xFF1E1438))
                    )
                )
                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                .clickable {
                    val activity = context as? Activity
                    if (activity != null) {
                        AdManager.showRewardedAd(
                            activity = activity,
                            rewardedUnitId = resolvedAdmobRewardedId,
                            unityPlacementId = resolvedUnityRewardedId,
                            onUserEarnedReward = {
                                DiamondRewardParticleController.triggerDiamondRewardBurst(
                                    diamondsEarned = 5,
                                    coinsEarned = 15,
                                    taskLabel = "$featureLabel Rewarded Ad"
                                )
                                onRewardEarned(5, 15)
                                Toast.makeText(
                                    context,
                                    "🎉 +5 💎 Diamonds & +15 🪙 Coins Credited! ($featureLabel)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFallbackSimulation = {
                                showFallbackRewardedModal = true
                            }
                        )
                    } else {
                        AdManager.selectNextMediatedAd(
                            format = AdFormat.REWARDED,
                            admobUnitId = resolvedAdmobRewardedId,
                            unityPlacementId = resolvedUnityRewardedId
                        )
                        showFallbackRewardedModal = true
                    }
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("universal_rewarded_ad_trigger_bar"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Rewarded Ad Bonus",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "🎁 Watch Rewarded Ad • Earn +5 💎 & +15 🪙",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$nextNetworkLabel • Game ID: $resolvedUnityGameId",
                        color = Color(0xFF94A3B8),
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = Color(0xFF10B981),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("universal_rewarded_ad_trigger_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = Color(0xFF052E16),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WATCH AD",
                        color = Color(0xFF052E16),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // 2. Permanent Mediated Banner Ad (BP_Banner_Android / AdMob)
        BannerAdView(
            isVip = isVip,
            adUnitId = admobBannerUnitId,
            unityBannerPlacementId = unityBannerPlacementId,
            unityGameId = resolvedUnityGameId
        )
    }

    if (showFallbackRewardedModal) {
        UniversalMediatedRewardedDialog(
            featureLabel = featureLabel,
            unityGameId = resolvedUnityGameId,
            unityPlacementId = resolvedUnityRewardedId,
            admobUnitId = resolvedAdmobRewardedId,
            isUnitySelected = lastRewardedDecision?.selectedNetwork == AdNetwork.UNITY_ADS,
            rotationReason = lastRewardedDecision?.reason ?: "Smart 50/50 Mediation Active",
            onDismiss = { showFallbackRewardedModal = false },
            onRewardCompleted = {
                showFallbackRewardedModal = false
                DiamondRewardParticleController.triggerDiamondRewardBurst(
                    diamondsEarned = 5,
                    coinsEarned = 15,
                    taskLabel = "$featureLabel Rewarded Ad"
                )
                onRewardEarned(5, 15)
                Toast.makeText(
                    context,
                    "🎉 +5 💎 Diamonds & +15 🪙 Coins Credited! ($featureLabel)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}

@Composable
private fun UniversalMediatedRewardedDialog(
    featureLabel: String,
    unityGameId: String,
    unityPlacementId: String,
    admobUnitId: String,
    isUnitySelected: Boolean,
    rotationReason: String,
    onDismiss: () -> Unit,
    onRewardCompleted: () -> Unit
) {
    var timeLeft by remember { mutableIntStateOf(4) }
    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        isCompleted = true
    }

    val badgeColor = if (isUnitySelected) Color(0xFF38BDF8) else Color(0xFF10B981)
    val networkTitle = if (isUnitySelected) {
        "Unity Rewarded Ad ($unityPlacementId)"
    } else {
        "Google AdMob Rewarded Video Ad"
    }

    AlertDialog(
        onDismissRequest = { if (isCompleted) onDismiss() },
        containerColor = Color(0xFF0F172A),
        titleContentColor = Color.White,
        textContentColor = Color(0xFFCBD5E1),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isUnitySelected) "UNITY ADS • $unityGameId" else "ADMOB REWARDED",
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = if (isCompleted) "Reward Unlocked ✅" else "${timeLeft}s remaining",
                    color = if (isCompleted) Color(0xFF10B981) else Color(0xFFFBBF24),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = networkTitle,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isUnitySelected) {
                        "Game ID: $unityGameId • Placement: $unityPlacementId"
                    } else {
                        "AdMob Unit: $admobUnitId • Fallback: $unityPlacementId"
                    },
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Feature: $featureLabel • $rotationReason",
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { (4 - timeLeft) / 4f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = badgeColor,
                    trackColor = Color.White.copy(alpha = 0.12f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onRewardCompleted,
                enabled = isCompleted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    disabledContainerColor = Color(0xFF1E293B)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("claim_universal_rewarded_ad_button")
            ) {
                Text(
                    text = if (isCompleted) "🎁 Claim +5 💎 Diamonds & +15 🪙 Coins" else "Watching Ad (${timeLeft}s)...",
                    color = if (isCompleted) Color(0xFF052E16) else Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
        },
        dismissButton = {
            if (!isCompleted) {
                TextButton(onClick = onDismiss) {
                    Text("Skip (No Reward)", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }
        }
    )
}

/**
 * Reusable Mediated Interstitial Ad Trigger Hook (`BP_Interstitial_Android` [Game ID 800427608] ⇄ Google AdMob 50/50 rotation & fallback).
 * Automatically manages fallback `InterstitialAdDialog` state so any screen, tool tab, or action button can trigger
 * a Mediated Interstitial Ad seamlessly without boilerplate or crashes.
 */
@Composable
fun rememberMediatedInterstitialTrigger(
    isVip: Boolean,
    admobInterstitialUnitId: String = AdManager.activeInterstitialAdUnitId,
    unityInterstitialPlacementId: String = AdManager.activeUnityInterstitialPlacementId.ifBlank { AdManager.DEFAULT_UNITY_INTERSTITIAL_PLACEMENT_ID }
): (actionName: String, onComplete: () -> Unit) -> Unit {
    val context = LocalContext.current
    var showFallbackInterstitial by remember { mutableStateOf(false) }
    var pendingOnComplete by remember { mutableStateOf<(() -> Unit)?>(null) }

    if (showFallbackInterstitial) {
        InterstitialAdDialog(
            interstitialUnitId = admobInterstitialUnitId,
            unityPlacementId = unityInterstitialPlacementId,
            onDismiss = {
                showFallbackInterstitial = false
                val action = pendingOnComplete
                pendingOnComplete = null
                try {
                    action?.invoke()
                } catch (_: Throwable) {
                }
            }
        )
    }

    return remember(context, isVip, admobInterstitialUnitId, unityInterstitialPlacementId) {
        { actionName: String, onComplete: () -> Unit ->
            if (isVip) {
                onComplete()
            } else {
                AdManager.triggerMilestoneInterstitial(
                    context = context,
                    isVip = false,
                    milestoneName = actionName,
                    interstitialUnitId = admobInterstitialUnitId,
                    unityPlacementId = unityInterstitialPlacementId,
                    onShowInterstitialFallback = { onDismiss ->
                        pendingOnComplete = {
                            onDismiss()
                            onComplete()
                        }
                        showFallbackInterstitial = true
                    },
                    onComplete = {
                        onComplete()
                    }
                )
            }
        }
    }
}

