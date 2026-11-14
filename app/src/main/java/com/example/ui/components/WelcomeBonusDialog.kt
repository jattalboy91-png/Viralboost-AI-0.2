package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen

/**
 * BIG BEAUTIFUL POPUP DIALOG FOR WELCOME BONUS
 * Matches exact prompt specification:
 * Title: "🎁 वेलकम बोनस!"
 * Text: "बधाई हो!\n\nViralBoost AI ऐप डाउनलोड करने पर आपको ₹50 का वेलकम बोनस मिला है!"
 * Confirm Button: "Claim Bonus & Continue" (triggers AdMob Interstitial Ad)
 */
@Composable
fun WelcomeBonusPopupDialog(
    bonusAmountRupees: Double = 50.0,
    onClaimBonusAndShowAd: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("welcome_bonus_popup_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(GoldAccent, Color(0xFFFFB300)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "🎁 वेलकम बोनस!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAccent
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Exact prompt message in Hindi & English
                Text(
                    text = "बधाई हो!\n\nViralBoost AI ऐप डाउनलोड करने का वेलकम बोनस आपको ₹${bonusAmountRupees.toInt()} मिले हैं!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 24.sp
                )

                // Highlight Card for Bonus Value
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1333)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Instant Reward Credit",
                                fontSize = 11.sp,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "₹${bonusAmountRupees.toInt()} Welcome Cash Balance",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent
                            )
                            Text(
                                text = "Usable for Shopping Hub discounts & VIP upgrades",
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Text(
                    text = "👉 टैप करें और स्पॉन्सर्ड ऐड देखकर अपना ₹50 बैलेंस तुरंत वॉलेट में पाएं।",
                    fontSize = 11.5.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onClaimBonusAndShowAd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("claim_welcome_bonus_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Claim Bonus & Continue",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Text(
                    text = "Later",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }
    )
}

/**
 * AdMob Interstitial Ad Simulation Dialog
 * Matches exact prompt specification:
 * Title: "AdMob Interstitial Ad"
 * Text: "📺 [AdMob Sponsored Ad Loaded]\n\nWatching ad to credit your ₹50 bonus."
 * Confirm Button: "Close Ad & Get ₹50" (Credits bonusRupees = 50.0 to wallet)
 */
@Composable
fun WelcomeBonusAdModal(
    bonusAmountRupees: Double = 50.0,
    onCloseAdAndGetBonus: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    val mediationState by com.example.monetization.AdManager.mediationState.collectAsState()
    val lastDecision by com.example.monetization.AdManager.lastInterstitialDecision.collectAsState()
    LaunchedEffect(Unit) {
        com.example.monetization.AdManager.selectNextMediatedAd(
            format = com.example.monetization.AdFormat.INTERSTITIAL,
            admobUnitId = com.example.monetization.AdManager.activeInterstitialAdUnitId,
            unityPlacementId = com.example.monetization.AdManager.activeUnityInterstitialPlacementId
        )
    }
    val unityGameId = mediationState.unityGameId.ifBlank { com.example.monetization.AdManager.DEFAULT_UNITY_GAME_ID }
    val unityPlacementId = mediationState.unityInterstitialPlacementId.ifBlank { com.example.monetization.AdManager.DEFAULT_UNITY_INTERSTITIAL_PLACEMENT_ID }
    val isUnitySelected = lastDecision?.selectedNetwork == com.example.monetization.AdNetwork.UNITY_ADS

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF120E24),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, BrandPurple, RoundedCornerShape(18.dp))
            .testTag("admob_interstitial_bonus_modal"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AdMob Interstitial Ad",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = if (isUnitySelected) {
                            "Unity Ads [$unityGameId] • $unityPlacementId (50/50 Mediated)"
                        } else {
                            "Sponsored Interstitial Creative • Fallback: $unityPlacementId"
                        },
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0716))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📺 [AdMob Sponsored Ad Loaded]\n\nWatching ad to credit your ₹${bonusAmountRupees.toInt()} bonus.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Unity Game ID: $unityGameId ($unityPlacementId) • Ad Unit: ca-app-pub-3940256099942544/1033173712",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Instant 100% Credit Guaranteed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCloseAdAndGetBonus,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("close_ad_and_get_bonus_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPurple,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Close Ad & Get ₹${bonusAmountRupees.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    )
}
