package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.AppConfig
import com.example.data.local.ProductOrderRecord
import com.example.data.local.ReferralRecord
import com.example.data.local.UserProfile
import com.example.monetization.AdManager
import com.example.ui.components.BannerAdView
import com.example.ui.components.DiamondParticleExplosionHost
import com.example.ui.components.DiamondRewardParticleController
import com.example.ui.components.NativeAdCard
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.VipGold
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TransactionFilter {
    ALL,
    EARNED,
    REDEEMED
}

data class DiamondTransactionEntry(
    val id: String,
    val title: String,
    val subtitle: String,
    val amountDiamonds: Int,
    val bonusCoins: Int = 0,
    val isEarned: Boolean = true,
    val timestampMillis: Long = System.currentTimeMillis(),
    val categoryTag: String = "ACTIVITY"
)

data class DiamondEarningActivityItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val rewardText: String,
    val actionLabel: String,
    val icon: ImageVector,
    val accentColor: Color,
    val isCompletedToday: Boolean = false,
    val onTrigger: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiamondDashboardScreen(
    userProfile: UserProfile?,
    appConfig: AppConfig = AppConfig(),
    referrals: List<ReferralRecord> = emptyList(),
    productOrders: List<ProductOrderRecord> = emptyList(),
    onNavigateBack: () -> Unit,
    onClaimStreak: () -> Unit,
    onWatchRewardedAd: () -> Unit,
    onShareReferral: (Context, String) -> Unit = { _, _ -> },
    onSimulateFriendDownload: (Context, String, String) -> Unit = { _, _, _ -> },
    onOpenShopsyMall: () -> Unit = {},
    onNavigateToAffiliate: () -> Unit = {},
    onOpenReelsHub: () -> Unit = {},
    onRedeem150ForVip: () -> Unit = {},
    onOpenRedeemStore: () -> Unit = {},
    onUpgradeToVip: () -> Unit = {},
    onRewardEarned: (diamonds: Int, coins: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activityContext = context as? Activity
    val profile = userProfile ?: UserProfile()
    val isVip = profile.isVip
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val isStreakClaimedToday = profile.lastStreakClaimDate == todayStr

    var selectedFilter by remember { mutableStateOf(TransactionFilter.ALL) }
    var lastActionFeedback by remember { mutableStateOf<String?>(null) }

    // =========================================================================
    // 📺 ADMOB INTERSTITIAL & REWARDED AD STATE FOR EVERY MOVEMENT / ACTION
    // =========================================================================
    var showMovementInterstitialAd by remember { mutableStateOf(false) }
    var activeAdTriggerReason by remember { mutableStateOf("Diamond Wallet Action") }
    var pendingMovementAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    var showWalletRewardedAd by remember { mutableStateOf(false) }
    var rewardedAdBonusCount by remember { mutableIntStateOf(0) }

    val lazyListState = rememberLazyListState()
    var hasTriggeredScrollAd by remember { mutableStateOf(false) }

    BackHandler {
        if (showMovementInterstitialAd) {
            showMovementInterstitialAd = false
            val action = pendingMovementAction
            pendingMovementAction = null
            action?.invoke()
        } else if (showWalletRewardedAd) {
            showWalletRewardedAd = false
        } else {
            onNavigateBack()
        }
    }

    /**
     * Triggers an AdMob Interstitial Ad whenever the user performs ANY movement, click, filter change,
     * or activity inside the Diamond & Wallet Dashboard (unless the user is VIP).
     */
    val triggerMovementWithInterstitialAd: (reason: String, action: () -> Unit) -> Unit = { reason, action ->
        if (isVip) {
            action()
        } else {
            activeAdTriggerReason = reason
            AdManager.triggerMilestoneInterstitial(
                context = context,
                isVip = false,
                milestoneName = "Diamond Wallet: $reason",
                interstitialUnitId = appConfig.admobInterstitialUnitId,
                onShowInterstitialFallback = {
                    pendingMovementAction = action
                    showMovementInterstitialAd = true
                },
                onComplete = {
                    action()
                }
            )
        }
    }

    // Trigger a Mediated Interstitial Ad (AdMob / Unity Ads BP_Interstitial_Android) when scrolling
    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        if (!isVip && !hasTriggeredScrollAd && lazyListState.firstVisibleItemIndex >= 3) {
            hasTriggeredScrollAd = true
            triggerMovementWithInterstitialAd("Dashboard Scroll & Wallet Movement") {}
        }
    }

    // Live session transactions recorded as the user completes activities on this screen
    val sessionTransactions = remember { mutableStateListOf<DiamondTransactionEntry>() }

    // Build combined transaction history from real Room records + session actions
    val allTransactions = remember(
        profile.diamonds,
        profile.streakDays,
        profile.lastStreakClaimDate,
        profile.isVip,
        referrals,
        productOrders,
        sessionTransactions.size
    ) {
        val list = mutableListOf<DiamondTransactionEntry>()

        // 1. Live session actions first (newest)
        list.addAll(sessionTransactions)

        // 2. Product Orders (+3 Diamonds each)
        productOrders.forEach { order ->
            list.add(
                DiamondTransactionEntry(
                    id = "order_${order.id}",
                    title = "Affiliate Order: ${order.productTitle}",
                    subtitle = "${order.platform} Verified Purchase • Order #${order.id}",
                    amountDiamonds = order.diamondsRewarded,
                    isEarned = true,
                    timestampMillis = order.timestamp,
                    categoryTag = "SHOPPING"
                )
            )
        }

        // 3. Friend Referrals (+10 Diamonds each)
        referrals.forEach { ref ->
            list.add(
                DiamondTransactionEntry(
                    id = "ref_${ref.id}",
                    title = "Friend Referral Bonus: ${ref.friendName}",
                    subtitle = "Referral Code ${ref.referralCode} • ${ref.status}",
                    amountDiamonds = 10,
                    isEarned = true,
                    timestampMillis = ref.joinedTimestamp,
                    categoryTag = "REFERRAL"
                )
            )
        }

        // 4. Daily Login Streak Rewards
        if (profile.streakDays > 0) {
            list.add(
                DiamondTransactionEntry(
                    id = "streak_current",
                    title = "Daily Login Streak (Day ${profile.streakDays})",
                    subtitle = if (isStreakClaimedToday) "Claimed today • Keep your streak alive!" else "Active Creator Streak Bonus",
                    amountDiamonds = appConfig.dailyStreakReward,
                    isEarned = true,
                    timestampMillis = profile.lastLoginDateMillis,
                    categoryTag = "STREAK"
                )
            )
        }

        // 5. VIP Redemption entry if user redeemed VIP via Diamonds
        if (profile.currentPlanTier.contains("DIAMOND", ignoreCase = true)) {
            list.add(
                DiamondTransactionEntry(
                    id = "vip_redeem",
                    title = "10-Day VIP Pro Subscription Redeemed",
                    subtitle = "Unlocked Ad-Free Studio & AI Tools via Diamonds",
                    amountDiamonds = 150,
                    isEarned = false,
                    timestampMillis = System.currentTimeMillis() - 3600_000L,
                    categoryTag = "REDEEM"
                )
            )
        }

        // 6. Initial Welcome Bonus (+10 Diamonds & +250 Coins)
        list.add(
            DiamondTransactionEntry(
                id = "welcome_bonus",
                title = "Welcome Creator Bonus",
                subtitle = "Account Onboarding & Wallet Activation",
                amountDiamonds = 10,
                bonusCoins = 250,
                isEarned = true,
                timestampMillis = profile.trialStartTimestamp,
                categoryTag = "WELCOME"
            )
        )

        list.sortedByDescending { it.timestampMillis }
    }

    val filteredTransactions = remember(allTransactions, selectedFilter) {
        when (selectedFilter) {
            TransactionFilter.ALL -> allTransactions
            TransactionFilter.EARNED -> allTransactions.filter { it.isEarned }
            TransactionFilter.REDEEMED -> allTransactions.filter { !it.isEarned }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "diamond_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val launchRewardedAdFlow: () -> Unit = {
        if (activityContext != null) {
            AdManager.showRewardedAd(
                activity = activityContext,
                rewardedUnitId = appConfig.admobRewardedUnitId,
                onUserEarnedReward = {
                    onWatchRewardedAd()
                    onRewardEarned(appConfig.rewardedAdPayout, 15)
                    DiamondRewardParticleController.triggerDiamondRewardBurst(
                        diamondsEarned = appConfig.rewardedAdPayout,
                        coinsEarned = 15,
                        taskLabel = "Rewarded Video Ad Completed!"
                    )
                    rewardedAdBonusCount++
                    sessionTransactions.add(
                        0,
                        DiamondTransactionEntry(
                            id = "session_ad_${System.currentTimeMillis()}",
                            title = "AdMob Rewarded Video Ad Payout",
                            subtitle = "Watched sponsored video ad (+15 Coins bonus)",
                            amountDiamonds = appConfig.rewardedAdPayout,
                            bonusCoins = 15,
                            isEarned = true,
                            categoryTag = "REWARDED AD"
                        )
                    )
                    lastActionFeedback = "🎉 +${appConfig.rewardedAdPayout} 💎 Diamonds & +15 🪙 Coins credited from Rewarded Ad!"
                },
                onFallbackSimulation = {
                    showWalletRewardedAd = true
                }
            )
        } else {
            showWalletRewardedAd = true
        }
    }

    val earningActivities = remember(isStreakClaimedToday, appConfig, profile.displayUserId) {
        listOf(
            DiamondEarningActivityItem(
                id = "daily_streak",
                title = "Daily Login Streak",
                subtitle = if (isStreakClaimedToday) "You claimed today's streak! Come back tomorrow for more." else "Check in daily to earn +${appConfig.dailyStreakReward} Diamonds & 7/30-day milestone bonuses.",
                rewardText = "+${appConfig.dailyStreakReward} 💎",
                actionLabel = if (isStreakClaimedToday) "Claimed ✓" else "Claim Now",
                icon = Icons.Default.LocalFireDepartment,
                accentColor = Color(0xFFFF6D00),
                isCompletedToday = isStreakClaimedToday,
                onTrigger = {
                    if (!isStreakClaimedToday) {
                        onClaimStreak()
                        DiamondRewardParticleController.triggerDiamondRewardBurst(
                            diamondsEarned = appConfig.dailyStreakReward,
                            coinsEarned = 0,
                            taskLabel = "Daily Login Streak Claimed!"
                        )
                        sessionTransactions.add(
                            0,
                            DiamondTransactionEntry(
                                id = "session_streak_${System.currentTimeMillis()}",
                                title = "Daily Streak Claimed",
                                subtitle = "Daily check-in reward credited to wallet",
                                amountDiamonds = appConfig.dailyStreakReward,
                                isEarned = true,
                                categoryTag = "STREAK"
                            )
                        )
                        lastActionFeedback = "🔥 +${appConfig.dailyStreakReward} Diamonds credited from Daily Login Streak!"
                    } else {
                        onClaimStreak()
                        DiamondRewardParticleController.triggerDiamondRewardBurst(
                            diamondsEarned = appConfig.dailyStreakReward,
                            coinsEarned = 0,
                            taskLabel = "Daily Login Streak Active!"
                        )
                        lastActionFeedback = "✅ Today's Daily Streak is already claimed!"
                    }
                    triggerMovementWithInterstitialAd("Claim Daily Login Streak") {}
                }
            ),
            DiamondEarningActivityItem(
                id = "watch_rewarded_ad",
                title = "Watch Rewarded Video Ad",
                subtitle = "Watch a short sponsored video clip to instantly earn ${appConfig.rewardedAdPayout} Diamonds & 15 Coins.",
                rewardText = "+${appConfig.rewardedAdPayout} 💎",
                actionLabel = "Watch Ad",
                icon = Icons.Default.PlayCircleFilled,
                accentColor = DiamondCyan,
                onTrigger = {
                    launchRewardedAdFlow()
                }
            ),
            DiamondEarningActivityItem(
                id = "share_referral",
                title = "Share Referral Link",
                subtitle = "Share your invite code (#${profile.displayUserId}) on WhatsApp or Instagram for instant Diamonds & Coins.",
                rewardText = "+4 💎 +10 🪙",
                actionLabel = "Share & Earn",
                icon = Icons.Default.Share,
                accentColor = Color(0xFF00E676),
                onTrigger = {
                    onShareReferral(context, "VB${profile.displayUserId.takeLast(4)}")
                    DiamondRewardParticleController.triggerDiamondRewardBurst(
                        diamondsEarned = 4,
                        coinsEarned = 10,
                        taskLabel = "Referral Link Shared!"
                    )
                    sessionTransactions.add(
                        0,
                        DiamondTransactionEntry(
                            id = "session_share_${System.currentTimeMillis()}",
                            title = "Referral Link Shared",
                            subtitle = "Instant social share reward (+10 Coins included)",
                            amountDiamonds = 4,
                            bonusCoins = 10,
                            isEarned = true,
                            categoryTag = "SHARE"
                        )
                    )
                    lastActionFeedback = "🚀 +4 Diamonds & +10 Coins credited for sharing your referral link!"
                    triggerMovementWithInterstitialAd("Share Referral Link") {}
                }
            ),
            DiamondEarningActivityItem(
                id = "friend_download",
                title = "Friend App Download Bonus",
                subtitle = "Earn +10 Diamonds every time an invited friend downloads & opens the app.",
                rewardText = "+10 💎",
                actionLabel = "Verify Invite",
                icon = Icons.Default.PersonAdd,
                accentColor = Color(0xFFD500F9),
                onTrigger = {
                    onSimulateFriendDownload(context, "Amit Sharma", "VB${profile.displayUserId.takeLast(4)}")
                    DiamondRewardParticleController.triggerDiamondRewardBurst(
                        diamondsEarned = 10,
                        coinsEarned = 0,
                        taskLabel = "Friend App Download Verified!"
                    )
                    sessionTransactions.add(
                        0,
                        DiamondTransactionEntry(
                            id = "session_friend_${System.currentTimeMillis()}",
                            title = "Friend Download Verified (Amit Sharma)",
                            subtitle = "Referral install & first open milestone",
                            amountDiamonds = 10,
                            isEarned = true,
                            categoryTag = "REFERRAL"
                        )
                    )
                    lastActionFeedback = "🎉 +10 Diamonds credited from Friend App Download!"
                    triggerMovementWithInterstitialAd("Verify Friend Download") {}
                }
            ),
            DiamondEarningActivityItem(
                id = "shopsy_shopping",
                title = "Shopsy & Affiliate Mall Deals",
                subtitle = "Shop trending budget deals (75% OFF) or place affiliate orders to earn bonus Diamonds.",
                rewardText = "+4 to +10 💎",
                actionLabel = "Open Mall",
                icon = Icons.Default.ShoppingBag,
                accentColor = VipGold,
                onTrigger = {
                    triggerMovementWithInterstitialAd("Open Shopsy & Affiliate Mall") {
                        onOpenShopsyMall()
                    }
                }
            ),
            DiamondEarningActivityItem(
                id = "watch_reels_hub",
                title = "Watch Creator Reels Hub",
                subtitle = "Watch community shorts & masterclasses in the Reels Hub to earn +3 Diamonds & +15 Coins per video.",
                rewardText = "+3 💎 +15 🪙",
                actionLabel = "Watch Reels",
                icon = Icons.Default.OndemandVideo,
                accentColor = Color(0xFFFF4081),
                onTrigger = {
                    triggerMovementWithInterstitialAd("Open Creator Reels Hub") {
                        onOpenReelsHub()
                    }
                }
            )
        )
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.US) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("diamond_dashboard_screen"),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D1326),
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("diamond_dashboard_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(DiamondCyan, BrandPurple)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Diamond Dashboard",
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Balance • History • AdMob Rewards",
                                fontSize = 10.5.sp,
                                color = DiamondCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    // Quick GO AD-FREE VIP Badge
                    if (!isVip) {
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                                    )
                                )
                                .clickable { onUpgradeToVip() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("diamond_top_ad_free_vip_badge")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "AD-FREE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            triggerMovementWithInterstitialAd("Open Diamond Redeem Store") {
                                onOpenRedeemStore()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VipGold),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .height(34.dp)
                            .testTag("diamond_top_redeem_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redeem,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Redeem",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }
            )
        },
        bottomBar = {
            // =========================================================================
            // 📌 PERSISTENT BOTTOM ADMOB BANNER AD + AD-FREE VIP UPGRADE STRIP
            // =========================================================================
            if (!isVip) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B091B))
                        .testTag("diamond_dashboard_bottom_banner_ad_container")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1F1235))
                            .clickable { onUpgradeToVip() }
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🚫 विज्ञापनों (Ads) से छुटकारा पाएं — 100% Ad-Free VIP लें!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = "अभी हटाएं →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00E5FF)
                        )
                    }
                    com.example.ui.components.UniversalMediatedAdDock(
                        isVip = false,
                        admobBannerUnitId = appConfig.admobBannerUnitId,
                        unityBannerPlacementId = appConfig.unityBannerPlacementId,
                        admobRewardedUnitId = appConfig.admobRewardedUnitId,
                        unityRewardedPlacementId = appConfig.unityRewardedPlacementId,
                        featureLabel = "Diamond Wallet Dashboard",
                        onRewardEarned = { diamonds, coins ->
                            onRewardEarned(diamonds, coins)
                        },
                        onUpgradeClick = onUpgradeToVip
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // =========================================================================
                // 0. PROMINENT AD-FREE SUBSCRIPTION DESCRIPTION CARD
                // =========================================================================
                if (!isVip) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF230F2E)),
                            border = BorderStroke(
                                2.dp,
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFFF4081), Color(0xFF00E5FF))
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("diamond_wallet_ad_free_card")
                        ) {
                            Column(modifier = Modifier.padding(15.dp)) {
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
                                                .background(Color(0xFFFFD700)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Block,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "🚫 Diamond Wallet Ads से छुटकारा पाएं!",
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFFFD700)
                                            )
                                            Text(
                                                text = "Interstitial, Rewarded & Banner Ads Active",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF00E5FF)
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFD50000))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "NO ADS",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "📌 साफ-साफ जानकारी: फ्री प्लान में Diamond & Wallet Dashboard के अंदर कोई भी मूवमेंट, स्क्रॉल या किसी भी ऑप्शन पर क्लिक करने पर AdMob Interstitial Ad, नीचे Banner Ad और Rewarded Video Ad आते हैं। सभी विज्ञापनों को 100% बंद करने के लिए अभी VIP सब्सक्रिप्शन लें या 150 💎 डायमंड्स रिडीम करें!",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = onUpgradeToVip,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1.2f)
                                            .height(44.dp)
                                            .testTag("btn_diamond_wallet_upgrade_vip")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "👑 Ad-Free VIP (₹49/₹79)",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = onRedeem150ForVip,
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.2.dp, Color(0xFF00E5FF)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .testTag("btn_diamond_wallet_quick_redeem_vip")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Diamond,
                                            contentDescription = null,
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "150 💎 से फ्री VIP",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00E5FF)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 1. HERO DIAMOND BALANCE & VALUATION CARD (Clicking triggers AdMob Interstitial Ad)
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        border = BorderStroke(
                            1.8.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    DiamondCyan.copy(alpha = glowAlpha),
                                    BrandPurple,
                                    VipGold.copy(alpha = glowAlpha)
                                )
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                triggerMovementWithInterstitialAd("Diamond Balance Card Inspected") {}
                            }
                            .testTag("diamond_balance_hero_card")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF0E1E38),
                                            Color(0xFF1A103C),
                                            Color(0xFF0B1528)
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "CURRENT DIAMOND BALANCE",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DiamondCyan,
                                            letterSpacing = 0.8.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Diamond,
                                                contentDescription = null,
                                                tint = DiamondCyan,
                                                modifier = Modifier.size(34.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "${profile.diamonds}",
                                                fontSize = 36.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                modifier = Modifier.testTag("diamond_balance_value_text")
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            val rupeeValue = String.format(Locale.US, "%.1f", profile.diamonds * 0.2)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(SuccessGreen.copy(alpha = 0.18f))
                                                    .border(1.dp, SuccessGreen.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "≈ ₹$rupeeValue Value",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SuccessGreen
                                                )
                                            }
                                        }
                                    }

                                    // Secondary Coins & Streak Pill
                                    Column(horizontalAlignment = Alignment.End) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF0F172A))
                                                .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.MonetizationOn,
                                                    contentDescription = null,
                                                    tint = GoldAccent,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${profile.coins} Coins",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldAccent
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Lifetime: ${maxOf(profile.lifetimeDiamonds, profile.diamonds)} 💎",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Progress toward 150 Diamonds (10-Day VIP Pass)
                                val targetDiamonds = 150
                                val progressFraction = (profile.diamonds.toFloat() / targetDiamonds.toFloat()).coerceIn(0f, 1f)
                                val remainingForVip = (targetDiamonds - profile.diamonds).coerceAtLeast(0)

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.Black.copy(alpha = 0.35f))
                                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.WorkspacePremium,
                                                contentDescription = null,
                                                tint = VipGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "10-Day VIP Pass Goal (150 💎 = ₹30 Value)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Text(
                                            text = "${profile.diamonds}/$targetDiamonds 💎",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = VipGold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    LinearProgressIndicator(
                                        progress = { progressFraction },
                                        color = DiamondCyan,
                                        trackColor = Color(0xFF1E293B),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(50))
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = if (remainingForVip == 0) {
                                            "🎉 Goal Reached! You can redeem 150 Diamonds for a 10-Day Free VIP Pass right now!"
                                        } else {
                                            "Earn $remainingForVip more Diamonds from the activities below to unlock 10 Days of Ad-Free VIP Pro!"
                                        },
                                        fontSize = 11.sp,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onRedeem150ForVip()
                                            if (profile.diamonds >= 150) {
                                                sessionTransactions.add(
                                                    0,
                                                    DiamondTransactionEntry(
                                                        id = "session_redeem_${System.currentTimeMillis()}",
                                                        title = "Redeemed 10-Day VIP Pass",
                                                        subtitle = "150 Diamonds exchanged for ₹30 VIP Pass",
                                                        amountDiamonds = 150,
                                                        isEarned = false,
                                                        categoryTag = "REDEEM"
                                                    )
                                                )
                                            }
                                            triggerMovementWithInterstitialAd("Redeem 150 Diamonds VIP") {}
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = VipGold),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .testTag("btn_redeem_150_diamonds_vip")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Redeem 150 💎 VIP",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            triggerMovementWithInterstitialAd("30-Day VIP (500 Diamonds)") {
                                                onOpenRedeemStore()
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.2.dp, DiamondCyan),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .testTag("btn_open_diamond_redeem_modal")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CardGiftcard,
                                            contentDescription = null,
                                            tint = DiamondCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "30-Day VIP (500 💎)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DiamondCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 1B. DEDICATED ADMOB REWARDED VIDEO AD CARD INSIDE DIAMOND WALLET
                // =========================================================================
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF102027)),
                        border = BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFFFD700)))
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("diamond_dashboard_rewarded_ad_card")
                    ) {
                        Column(modifier = Modifier.padding(15.dp)) {
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
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00E5FF).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircleFilled,
                                            contentDescription = null,
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "🎁 AdMob Rewarded Video Ad (रिवॉर्ड एड देखें)",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "वीडियो विज्ञापन देखें और तुरंत अपने वॉलेट में पाएं +${appConfig.rewardedAdPayout} 💎 डायमंड्स + 15 🪙 कॉइन्स!",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFFB2EBF2),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { launchRewardedAdFlow() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B8D4)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("btn_watch_diamond_wallet_rewarded_ad")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OndemandVideo,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (rewardedAdBonusCount > 0) "Watch Rewarded Ad Again (+${appConfig.rewardedAdPayout} 💎)" else "Watch Rewarded Ad (+${appConfig.rewardedAdPayout} 💎)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                }

                                if (!isVip) {
                                    OutlinedButton(
                                        onClick = onUpgradeToVip,
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFFFD700)),
                                        modifier = Modifier.height(44.dp)
                                    ) {
                                        Text(
                                            text = "Skip Ads (VIP)",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD700)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Live Feedback Banner when an activity is completed
                lastActionFeedback?.let { feedback ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                            border = BorderStroke(1.dp, SuccessGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("diamond_action_feedback_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = feedback,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // 2. EARN MORE DIAMONDS VIA APP ACTIVITIES SECTION
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = null,
                                tint = DiamondCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Earn More Diamonds",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "${earningActivities.size} Active Ways",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiamondCyan
                        )
                    }
                }

                items(earningActivities, key = { it.id }) { activity ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(
                            1.dp,
                            if (activity.isCompletedToday) SuccessGreen.copy(alpha = 0.5f)
                            else activity.accentColor.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activity.onTrigger() }
                            .testTag("earn_activity_card_${activity.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(activity.accentColor.copy(alpha = 0.16f))
                                        .border(
                                            1.dp,
                                            activity.accentColor.copy(alpha = 0.5f),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = activity.icon,
                                        contentDescription = null,
                                        tint = activity.accentColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = activity.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(activity.accentColor.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = activity.rewardText,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = activity.accentColor
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = activity.subtitle,
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF94A3B8),
                                        lineHeight = 15.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(
                                onClick = activity.onTrigger,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activity.isCompletedToday) Color(0xFF1E293B) else BrandPurple
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("btn_earn_activity_${activity.id}")
                            ) {
                                Text(
                                    text = activity.actionLabel,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activity.isCompletedToday) SuccessGreen else Color.White
                                )
                            }
                        }
                    }
                }

                // =========================================================================
                // 2B. IN-FEED ADMOB NATIVE & BANNER AD SHOWCASE INSIDE WALLET DASHBOARD
                // =========================================================================
                if (!isVip) {
                    item {
                        NativeAdCard(
                            isVip = false,
                            onAdClicked = onUpgradeToVip
                        )
                    }
                }

                // 3. TRANSACTION HISTORY SECTION WITH FILTER CHIPS (Any filter click triggers AdMob Interstitial Ad)
                item {
                    Column {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Transaction History",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "${filteredTransactions.size} Records",
                                fontSize = 11.5.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val filters = listOf(
                                TransactionFilter.ALL to "All Activity",
                                TransactionFilter.EARNED to "Earned (+)",
                                TransactionFilter.REDEEMED to "Redeemed (-)"
                            )
                            filters.forEach { (filter, label) ->
                                val selected = selectedFilter == filter
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        selectedFilter = filter
                                        triggerMovementWithInterstitialAd("Filter Transactions: $label") {
                                            selectedFilter = filter
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandPurple,
                                        selectedLabelColor = Color.White,
                                        containerColor = DarkSurface,
                                        labelColor = Color.LightGray
                                    ),
                                    modifier = Modifier.testTag("filter_chip_${filter.name.lowercase()}")
                                )
                            }
                        }
                    }
                }

                if (filteredTransactions.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, DarkSurfaceBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("empty_transactions_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No transactions in this filter yet",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Complete earning activities above or redeem Diamonds for VIP rewards to see entries here.",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    items(filteredTransactions, key = { it.id }) { tx ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                            border = BorderStroke(1.dp, Color(0xFF1F2937)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    triggerMovementWithInterstitialAd("Transaction Details: ${tx.title}") {}
                                }
                                .testTag("transaction_item_${tx.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val badgeColor = if (tx.isEarned) SuccessGreen else GoldAccent
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(badgeColor.copy(alpha = 0.15f))
                                            .border(1.dp, badgeColor.copy(alpha = 0.5f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (tx.isEarned) Icons.Default.Diamond else Icons.Default.Redeem,
                                            contentDescription = null,
                                            tint = badgeColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = tx.title,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = tx.subtitle,
                                            fontSize = 11.sp,
                                            color = Color(0xFF9CA3AF),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF1E293B))
                                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = tx.categoryTag,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DiamondCyan
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = dateFormatter.format(Date(tx.timestampMillis)),
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(horizontalAlignment = Alignment.End) {
                                    val sign = if (tx.isEarned) "+" else "-"
                                    val amountColor = if (tx.isEarned) SuccessGreen else GoldAccent
                                    Text(
                                        text = "$sign${tx.amountDiamonds} 💎",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = amountColor
                                    )
                                    if (tx.bonusCoins > 0) {
                                        Text(
                                            text = "+${tx.bonusCoins} 🪙",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // 📺 FULL-SCREEN ADMOB INTERSTITIAL AD MODAL ON ANY MOVEMENT / CLICK
    // =========================================================================
    if (showMovementInterstitialAd) {
        DiamondWalletInterstitialAdModal(
            triggerReason = activeAdTriggerReason,
            onDismissAndContinue = {
                showMovementInterstitialAd = false
                val action = pendingMovementAction
                pendingMovementAction = null
                action?.invoke()
            },
            onUpgradeToVip = {
                showMovementInterstitialAd = false
                pendingMovementAction = null
                onUpgradeToVip()
            }
        )
    }

    // =========================================================================
    // 🎁 FULL-SCREEN ADMOB REWARDED VIDEO AD MODAL (+5 DIAMONDS & +15 COINS)
    // =========================================================================
    if (showWalletRewardedAd) {
        DiamondWalletRewardedAdModal(
            rewardDiamonds = appConfig.rewardedAdPayout,
            onDismiss = { showWalletRewardedAd = false },
            onRewardCompleted = {
                showWalletRewardedAd = false
                rewardedAdBonusCount++
                onWatchRewardedAd()
                onRewardEarned(appConfig.rewardedAdPayout, 15)
                DiamondRewardParticleController.triggerDiamondRewardBurst(
                    diamondsEarned = appConfig.rewardedAdPayout,
                    coinsEarned = 15,
                    taskLabel = "Rewarded Video Ad Completed!"
                )
                sessionTransactions.add(
                    0,
                    DiamondTransactionEntry(
                        id = "session_ad_${System.currentTimeMillis()}",
                        title = "AdMob Rewarded Video Ad Payout",
                        subtitle = "Watched sponsored video ad (+15 Coins bonus)",
                        amountDiamonds = appConfig.rewardedAdPayout,
                        bonusCoins = 15,
                        isEarned = true,
                        categoryTag = "REWARDED AD"
                    )
                )
                lastActionFeedback = "🎉 बधाई हो! Rewarded Ad देखने पर आपको +${appConfig.rewardedAdPayout} 💎 डायमंड्स और +15 🪙 कॉइन्स मिले!"
                Toast.makeText(
                    context,
                    "💎 +${appConfig.rewardedAdPayout} Diamonds & +15 Coins credited!",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onUpgradeToVip = {
                showWalletRewardedAd = false
                onUpgradeToVip()
            }
        )
    }

    // Compose Canvas Diamond Reward Particle Burst Overlay
    DiamondParticleExplosionHost()
}

@Composable
private fun DiamondWalletInterstitialAdModal(
    triggerReason: String,
    onDismissAndContinue: () -> Unit,
    onUpgradeToVip: () -> Unit
) {
    var secondsLeft by remember { mutableIntStateOf(3) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
    }
    val progress by animateFloatAsState(
        targetValue = (3 - secondsLeft) / 3f,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "diamond_interstitial_progress"
    )

    Dialog(
        onDismissRequest = onDismissAndContinue,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF5070612))
                .padding(16.dp)
                .testTag("diamond_wallet_interstitial_ad_modal"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14112E)),
                border = BorderStroke(
                    2.dp,
                    Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFF00E5FF)))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val decision by AdManager.lastInterstitialDecision.collectAsState()
                        val isUnity = decision.network == com.example.monetization.AdNetwork.UNITY_ADS
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFD700))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isUnity) {
                                        "Unity Ads [800427608] • BP_Interstitial_Android"
                                    } else {
                                        "AdMob Interstitial Ad"
                                    },
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (secondsLeft > 0) "${secondsLeft}s..." else "Ready ✓",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF)
                            )
                        }

                        IconButton(
                            onClick = onDismissAndContinue,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .testTag("btn_close_diamond_interstitial_ad")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Ad",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        color = Color(0xFFFFD700),
                        trackColor = Color(0xFF1F2937),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(DiamondCyan, BrandPurple))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "📺 Google AdMob Interstitial Ad",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Triggered by: $triggerReason",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00E5FF),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF240F30)),
                        border = BorderStroke(1.2.dp, Color(0xFFFFD700)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🚫 क्या आप बार-बार आने वाले Ads से छुटकारा पाना चाहते हैं?",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD700),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "फ्री यूजर के लिए Diamond Wallet Dashboard में किसी भी मूवमेंट या ऑप्शन पर क्लिक करने पर विज्ञापन (Ad) आता है। सभी ऐड्स को 100% बंद करने के लिए अभी VIP सब्सक्रिप्शन लें!",
                                fontSize = 11.5.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onUpgradeToVip,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("btn_diamond_interstitial_upgrade_vip")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "👑 अभी Ad-Free VIP लें (₹49 / ₹79)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.5.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onDismissAndContinue,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_skip_diamond_interstitial_ad")
                    ) {
                        Text(
                            text = if (secondsLeft > 0) "Skip Ad & Continue (${secondsLeft}s) →" else "Continue to Diamond Wallet →",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiamondWalletRewardedAdModal(
    rewardDiamonds: Int,
    onDismiss: () -> Unit,
    onRewardCompleted: () -> Unit,
    onUpgradeToVip: () -> Unit
) {
    var secondsLeft by remember { mutableIntStateOf(5) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
    }
    val progress by animateFloatAsState(
        targetValue = (5 - secondsLeft) / 5f,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "diamond_rewarded_progress"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF5070612))
                .padding(16.dp)
                .testTag("diamond_wallet_rewarded_ad_modal"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101E2B)),
                border = BorderStroke(
                    2.dp,
                    Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFFFD700)))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val decision by AdManager.lastRewardedDecision.collectAsState()
                        val isUnity = decision.network == com.example.monetization.AdNetwork.UNITY_ADS
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF00E5FF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isUnity) {
                                    "Unity Ads [800427608] • BP_Rewarded_Android"
                                } else {
                                    "AdMob Rewarded Video Ad"
                                },
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = if (secondsLeft > 0) "Reward in ${secondsLeft}s" else "Reward Unlocked! 🎉",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        color = Color(0xFF00E5FF),
                        trackColor = Color(0xFF1F2937),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(56.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "🎁 Watch & Earn +$rewardDiamonds 💎 Diamonds + 15 🪙 Coins",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "विज्ञापन से हमेशा के लिए छुटकारा पाने के लिए आप कभी भी Ad-Free VIP सब्सक्रिप्शन ले सकते हैं!",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onRewardCompleted,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_claim_diamond_wallet_rewarded_ad")
                    ) {
                        Text(
                            text = if (secondsLeft > 0) "Claim +$rewardDiamonds 💎 Reward (${secondsLeft}s)" else "🎉 Claim +$rewardDiamonds 💎 Diamonds & Continue",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onUpgradeToVip,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFD700)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Text(
                            text = "👑 Ads हटाएँ — अभी VIP सब्सक्रिप्शन लें",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }
        }
    }
}
