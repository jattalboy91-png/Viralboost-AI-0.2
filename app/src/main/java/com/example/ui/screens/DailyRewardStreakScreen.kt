package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppConfig
import com.example.data.local.UserProfile
import com.example.ui.components.UniversalMediatedAdDock
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.util.SocialShareHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyRewardStreakScreen(
    userProfile: UserProfile?,
    appConfig: AppConfig = AppConfig(),
    onClaimReward: () -> Unit,
    onContinueToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    }

    val profile = userProfile ?: UserProfile()
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val isClaimedToday = profile.lastStreakClaimDate == todayStr

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Header with Skip to Home option
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(BrandPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ViralBoost AI",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            OutlinedButton(
                onClick = onContinueToHome,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.testTag("skip_to_home_button")
            ) {
                Text("Go to Home", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onBackground)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Celebratory Hero Gift & Streak Header
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFF9900), Color(0xFFEA580C), BrandPurple)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isClaimedToday) Icons.Default.LocalFireDepartment else Icons.Default.CardGiftcard,
                contentDescription = "Daily Reward",
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isClaimedToday) "🔥 Daily Streak Active!" else "🎁 Daily Reward Ready!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (isClaimedToday) {
                "You've claimed today's login bonus! Your streak is on fire."
            } else {
                "Welcome to ViralBoost AI! Claim your post-signup daily streak bonus to supercharge your wallet & start going viral."
            },
            fontSize = 12.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Unique Creator User ID Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("daily_reward_user_id_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1B4B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = profile.username,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "User ID: #${profile.displayUserId}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${profile.niche}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        val clip = ClipData.newPlainText("ViralBoost User ID", profile.displayUserId)
                        clipboardManager?.setPrimaryClip(clip)
                        Toast.makeText(context, "User ID #${profile.displayUserId} copied! 📋", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_user_id_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy User ID",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Big Spotlight Reward Box (Coins + Diamonds Designated Claim)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("daily_reward_spotlight_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(Color(0xFFFF9900), GoldAccent, NeonCyan))
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Designated Reward Package",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF065F46))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Day ${profile.streakDays} Bonus", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6EE7B7))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dual Reward Badges (Coins & Diamonds)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Coins Box (+100 Coins)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1702))
                            .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("+100 Coins", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = GoldAccent)
                            Text("Auto-Hooks & Video Posts", fontSize = 9.5.sp, color = Color.LightGray, textAlign = TextAlign.Center)
                        }
                    }

                    // Diamonds Box (+2 Diamonds)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF031E2A))
                            .border(1.dp, DiamondCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("+2 Diamonds", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = DiamondCyan)
                            Text("VIP Tools & Pro Render", fontSize = 9.5.sp, color = Color.LightGray, textAlign = TextAlign.Center)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Free Trial Perk Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Instant 3-Day Free Trial Active: Full AI Suite Unlocked (3 Videos, 5 Images, 5 Scripts)",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Streak Roadmap Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Weekly & Monthly Streak Roadmap",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(
                        Triple("Day 1", "+100🪙 +2💎", true),
                        Triple("Day 3", "+3💎 Bonus", false),
                        Triple("Day 7", "+10💎 Bonus", false),
                        Triple("Day 30", "+50💎 VIP", false)
                    ).forEach { (day, reward, isCurrent) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isCurrent) Color(0xFFEA580C) else DarkSurface)
                                    .border(1.dp, if (isCurrent) Color(0xFFFF9900) else DarkSurfaceBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.removePrefix("Day "),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.White else Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(day, fontSize = 10.sp, color = if (isCurrent) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
                            Text(reward, fontSize = 8.5.sp, color = if (isCurrent) GoldAccent else Color.LightGray)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Primary Claim Reward Button
        if (!isClaimedToday) {
            Button(
                onClick = onClaimReward,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("claim_daily_reward_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C))
            ) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Claim Daily Reward (+100 🪙 & +2 💎) 🎁",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        } else {
            Button(
                onClick = onContinueToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("continue_to_home_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
            ) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Claimed for Today! Continue to Home ➔",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Share Streak Milestone via Android Share Intent
        OutlinedButton(
            onClick = {
                val (nextMilestone, milestoneBonus) = when {
                    profile.streakDays < 3 -> 3 to 3
                    profile.streakDays < 7 -> 7 to 10
                    profile.streakDays < 14 -> 14 to 20
                    profile.streakDays < 30 -> 30 to 50
                    else -> (((profile.streakDays / 30) + 1) * 30) to 100
                }
                SocialShareHelper.shareStreakMilestone(
                    context = context,
                    streakDays = profile.streakDays,
                    nextMilestone = nextMilestone,
                    milestoneBonus = milestoneBonus,
                    dailyReward = appConfig.dailyStreakReward,
                    username = profile.username
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("daily_streak_share_milestone_button"),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share Streak Milestone",
                tint = NeonCyan,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Share My ${profile.streakDays}-Day Streak Milestone",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "ℹ️ Tapping claim triggers a mediated Unity Ads (${appConfig.unityInterstitialPlacementId}) / Google AdMob ad. Designated coins & diamonds will be instantly credited to your creator balance upon completion.",
            fontSize = 10.5.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Universal Mediated Rewarded Ad + Permanent Banner Ad (BP_Banner_Android / BP_Rewarded_Android / AdMob)
        UniversalMediatedAdDock(
            isVip = profile.isVip,
            admobBannerUnitId = appConfig.admobBannerUnitId,
            unityBannerPlacementId = appConfig.unityBannerPlacementId,
            admobRewardedUnitId = appConfig.admobRewardedUnitId,
            unityRewardedPlacementId = appConfig.unityRewardedPlacementId,
            featureLabel = "Daily Streak Reward"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
