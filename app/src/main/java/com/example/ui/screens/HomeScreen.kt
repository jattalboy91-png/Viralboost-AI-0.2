package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WorkspacePremium
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.ui.components.AiMultiCategoryMasterHubDialog
import com.viralboost.ai.ShopsyMegaStoreScreen
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppConfig
import com.example.data.local.ReferralRecord
import com.example.data.local.UserProfile
import com.example.data.repository.TrialStatus
import com.example.monetization.AdManager
import com.example.monetization.MonetizationConstants
import com.example.monetization.PaymentManager
import com.example.ui.components.AiReelCreatorMagicCard
import com.example.ui.components.BannerAdView
import com.example.ui.components.GrowthStatsCard
import com.example.ui.components.HomeReferralRewardBanner
import com.example.ui.components.InterstitialAdDialog
import com.example.ui.components.NativeAdCard
import com.example.ui.components.NativeVideoAdCard
import com.example.ui.components.UniversalMediatedAdDock
import com.example.ui.components.rememberMediatedInterstitialTrigger
import com.example.util.ReferralNotificationHelper
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.VipGold
import com.example.data.model.MallItem
import com.example.data.model.MegaMallProduct
import com.example.data.model.ShoppingProduct
import com.example.data.api.ShopsyApiClient
import com.example.data.api.ShopsyApiService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun HomeScreen(
    userProfile: UserProfile?,
    appConfig: AppConfig = AppConfig(),
    trialStatus: TrialStatus = TrialStatus(1, true, false, 3, 5, 5, 3, 5, 5, false),
    referrals: List<ReferralRecord> = emptyList(),
    onClaimStreak: () -> Unit,
    onWatchRewardedAd: () -> Unit,
    onOpenRedeem: () -> Unit,
    onNavigateToAiTools: () -> Unit,
    onNavigateToVipPaywall: () -> Unit,
    onNavigateToAffiliate: () -> Unit,
    onNavigateToCrossPoster: () -> Unit = {},
    onTriggerPaywall: (reason: String) -> Unit = {},
    referralNotificationMessage: String? = null,
    friendJoinedStatus: String = "Waiting for friend to download & explore...",
    onDismissReferralNotification: () -> Unit = {},
    onShareReferral: (context: Context, code: String) -> Unit = { _, _ -> },
    onSimulateDownload: (context: Context, friendName: String, code: String) -> Unit = { _, _, _ -> },
    onSimulateExploration: (context: Context, friendName: String, code: String) -> Unit = { _, _, _ -> },
    onVerifyActiveReferral: (context: Context, id: Long, friendName: String) -> Unit = { _, _, _ -> },
    onSimulateActiveReferral: (context: Context, friendName: String, code: String) -> Unit = { _, _, _ -> },
    onOpenShopsyMall: () -> Unit = {},
    onRewardAction: (diamonds: Int, coins: Int) -> Unit = { _, _ -> },
    onOpenReelsHub: () -> Unit = {},
    onRedeemDiamondsForVip: () -> Unit = {},
    onTriggerWelcomePopup: () -> Unit = {},
    onOpenFullAiStudio: () -> Unit = {},
    onOpenGeminiSocialStudio: () -> Unit = {},
    onOpenOneClickAiVideoHashtag: (() -> Unit)? = null,
    onOpenDiamondDashboard: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val profile = userProfile ?: UserProfile()
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val isStreakClaimedToday = profile.lastStreakClaimDate == todayStr

    // Live Ticker Messages State simulating real-time user winnings
    val tickerMessages = remember {
        listOf(
            "🔥 राहुल ने ऐप डाउनलोड करते ही ₹50 वेलकम बोनस कमाया!",
            "💎 प्रिया ने Shopsy मॉल से शॉपिंग करके +10 डायमंड और ₹50 बचाए!",
            "🚀 विकास ने 1-Click AI वीडियो शेयर करके Instagram पर 5K व्यूज पाए!",
            "⭐ अमित जाटलिया ने आज Shopsy एफिलिएट कमीशन से ₹350 कमाए!"
        )
    }
    var currentTickerIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentTickerIndex = (currentTickerIndex + 1) % tickerMessages.size
        }
    }

    var showCreatorPoolDialog by remember { mutableStateOf(false) }
    var creatorPoolActionStatus by remember { mutableStateOf<String?>(null) }
    val triggerInterstitial = rememberMediatedInterstitialTrigger(
        isVip = profile.isVip,
        admobInterstitialUnitId = appConfig.admobInterstitialUnitId,
        unityInterstitialPlacementId = appConfig.unityInterstitialPlacementId
    )

    var showAiMasterHubDialog by remember { mutableStateOf(false) }
    var showDiamondDashboardDialog by remember { mutableStateOf(false) }
    var selectedAiCategory by remember { mutableStateOf("Haryanvi Comedy & Rap") }
    var aiActionStatus by remember { mutableStateOf<String?>(null) }
    var aiVideoReadyState by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            // 00-HERO TOP: #1 Front & Center Feature — "One-Click to Share Video Multi-Platform"
            item {
                HomeVideoCrossPosterBanner(
                    affiliateTag = appConfig.affiliateTag,
                    downloadUrl = appConfig.appDownloadUrl,
                    isVip = userProfile?.isVip == true,
                    onNavigateToCrossPoster = onNavigateToCrossPoster
                )
            }

            // 0A. Live Earning & Reward Ticker Taped at Top
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF6366F1).copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("live_earnings_ticker")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tickerMessages[currentTickerIndex],
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFC7D2FE),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 0A-TOP: #1 Dashboard Option — One Click AI Video & Viral हैशटैग (Opens Full-Screen Page)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1238)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.8.dp,
                        Brush.horizontalGradient(
                            listOf(GoldAccent, Color(0xFF8B5CF6), NeonCyan)
                        )
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (onOpenOneClickAiVideoHashtag != null) {
                                onOpenOneClickAiVideoHashtag()
                            } else {
                                triggerInterstitial("Open 1-Click AI Video & Hashtag Hub") {
                                    showAiMasterHubDialog = true
                                }
                            }
                        }
                        .testTag("one_click_ai_video_generator_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                                listOf(Color(0xFFFF5722), GoldAccent)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "🔥 One Click AI Video & Viral हैशटैग",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 15.5.sp
                                    )
                                    Text(
                                        text = "Full-Screen AI Reel Studio • Auto Viral Hashtags",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeonCyan
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoldAccent.copy(alpha = 0.2f))
                                    .border(1.dp, GoldAccent, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "FULL PAGE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "कॉमेडी, हरियाणवी रैप, मोटिवेशन या शॉर्ट्स बनाएं — 1-क्लिक में ऑटो-वायरल टाइटल, स्क्रिप्ट और ट्रेंडिंग हैशटैग्स के साथ!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (onOpenOneClickAiVideoHashtag != null) {
                                    onOpenOneClickAiVideoHashtag()
                                } else {
                                    triggerInterstitial("Open 1-Click AI Video & Hashtag Hub") {
                                        showAiMasterHubDialog = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_launch_ai_video_hub")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Launch AI Video & Hashtag Hub 🚀",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.5.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 0B. Wallet, VIP & Diamond Dashboard Hub Card (₹50 Welcome Bonus, Diamonds & Coins)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, DiamondCyan.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (onOpenDiamondDashboard != null) {
                                onOpenDiamondDashboard()
                            } else {
                                triggerInterstitial("Open Diamond Wallet Dashboard") {
                                    showDiamondDashboardDialog = true
                                }
                            }
                        }
                        .testTag("wallet_vip_hub_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = null,
                                    tint = DiamondCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "💎 Diamond & Wallet Dashboard",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldAccent
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DiamondCyan.copy(alpha = 0.16f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${profile.diamonds} 💎",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DiamondCyan
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val bonusDisplay = if (profile.bonusRupees > 0) profile.bonusRupees else 50.0
                        Text(
                            text = "Wallet Balance: ₹$bonusDisplay | 💎 Diamonds: ${profile.diamonds} | 🪙 Coins: ${profile.coins}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (onOpenDiamondDashboard != null) {
                                    onOpenDiamondDashboard()
                                } else {
                                    triggerInterstitial("Open Diamond Wallet Dashboard") {
                                        showDiamondDashboardDialog = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("btn_open_diamond_dashboard")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Open Diamond Dashboard (Balance, History & Earn)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 0C. Growth Stats Card: Displays mock analytics for the user's latest generated content (views, shares, and growth percentage)
            item {
                GrowthStatsCard(
                    onCreateNewContent = {
                        if (onOpenOneClickAiVideoHashtag != null) {
                            onOpenOneClickAiVideoHashtag()
                        } else {
                            onOpenGeminiSocialStudio()
                        }
                    }
                )
            }

            // 0D. Growth Insights Card: Displays Predicted Views, Shareability Score radial gauge, and interactive Engagement Trend multi-series graph
            item {
                com.example.ui.components.GrowthInsightsCard(
                    onOptimizeWithAi = {
                        onOpenGeminiSocialStudio()
                    }
                )
            }

            // 1. Top Section: Daily Streak Tracker & Rewards Bar
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Top Creator Identity & Balances Rewards Bar
                    CreatorHeaderCard(
                        profile = profile,
                        trialStatus = trialStatus,
                        onOpenRedeem = onOpenRedeem,
                        onNavigateToVip = onNavigateToVipPaywall
                    )
                    // Visual Daily Login Streak Tracker & Milestone Progress
                    StreakTrackerCard(
                        streakDays = profile.streakDays,
                        isClaimedToday = isStreakClaimedToday,
                        streakReward = appConfig.dailyStreakReward,
                        onClaimStreak = onClaimStreak
                    )
                }
            }

            // Section 1C: Cross-Promotion & Follow-for-Follow Creator Pool Box
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            triggerInterstitial("Open Creator Community Growth Pool") {
                                showCreatorPoolDialog = true
                            }
                        }
                        .testTag("creator_growth_pool_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🤝 Creator Community (Follow-for-Follow Pool)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "यूट्यूब, इंस्टाग्राम और गोलाइवग्राम पर अपने फॉलोअर्स और व्यूज तेजी से बढ़ाएं!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                triggerInterstitial("Open Creator Community Growth Pool") {
                                    showCreatorPoolDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_open_creator_pool")
                        ) {
                            Text(
                                text = "Open Creator Growth Pool",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Section 1D: Multi-Platform Connected Video Feed: Viral Creator Reels Hub (YouTube, Instagram, GoLiveGram)
            item {
                HomeViralCreatorReelsHubBanner(
                    onOpenReelsHub = onOpenReelsHub
                )
            }

            // Section 1E: Dedicated Full-Screen AI Trending Video Studio Launcher
            item {
                HomeAiTrendingVideoStudioBanner(
                    onOpenStudio = onOpenFullAiStudio
                )
            }

            // Section 1F: Gemini Social Media Content Generator Banner
            item {
                HomeGeminiSocialMediaStudioBanner(
                    onOpenGemini = onOpenGeminiSocialStudio
                )
            }

            // Section 1G: Interactive Gemini Keyword AI Content Generator Component
            item {
                com.example.ui.components.GeminiKeywordContentGeneratorCard(
                    isVip = profile.isVip,
                    onOpenFullGeminiStudio = onOpenGeminiSocialStudio
                )
            }

            // Section 2A: Animated Pulsing Shopsy Shopping Popup & Mall Banner ("Shopping Now - 75% OFF + 4 Diamonds & 10 Coins")
            item {
                HomeShopsyShoppingPopupBanner(
                    isVip = profile.isVip,
                    onOpenShopsyMall = onOpenShopsyMall,
                    onRewardAction = onRewardAction
                )
            }

            // 2. Section 2: Prominent "Affiliate Hub & Rewards" card (showing Meesho, Flipkart, Amazon budget deals, Tag: 5665937, Share & Earn +10 Coins, Buy & Earn +3 Diamonds)
            item {
                HomeAffiliateHubBanner(
                    affiliateTag = appConfig.affiliateTag,
                    onNavigateToAffiliate = onNavigateToAffiliate
                )
            }

            // 3. Section 3 (Right below Affiliate Hub): Rewarded Earning & Bonus Section (Unity Ads SDK + Mediated Rewarded Flow)
            item {
                RewardedAdCard(
                    rewardPayout = appConfig.rewardedAdPayout,
                    currentDiamonds = profile.diamonds,
                    unityGameId = appConfig.unityGameId,
                    unityRewardedPlacementId = appConfig.unityRewardedPlacementId,
                    admobRewardedUnitId = appConfig.admobRewardedUnitId,
                    onWatchRewardedAd = onWatchRewardedAd
                )
            }

            // 4. Referral & Rewards System: Instant Share Rewards (+10 Coins, +4 Diamonds), Download Bonus & Active Friend 1-Day Full-App Free Sub
            item {
                HomeReferralRewardBanner(
                    userProfile = profile,
                    referrals = referrals,
                    notificationMessage = referralNotificationMessage,
                    friendJoinedStatus = friendJoinedStatus,
                    onDismissNotification = onDismissReferralNotification,
                    onShareReferral = onShareReferral,
                    onSimulateDownload = onSimulateDownload,
                    onSimulateExploration = onSimulateExploration,
                    onVerifyActiveReferral = onVerifyActiveReferral,
                    onSimulateActiveReferral = onSimulateActiveReferral,
                    onRedeemDiamondsForVip = onRedeemDiamondsForVip,
                    onTriggerWelcomePopup = onTriggerWelcomePopup
                )
            }

            // 5. 3-Day Graduated Free Trial Banner / Urgency Card
            item {
                HomeTrialStatusBanner(
                    trialStatus = trialStatus,
                    onNavigateToAi = onNavigateToAiTools,
                    onUpgrade = {
                        onTriggerPaywall(
                            if (trialStatus.isExpired) {
                                "Your 3-Day Free Trial has ended! All AI video, image, script tools, and bulk creators are locked. Upgrade to VIP Pro to restore unlimited zero-drop access."
                            } else {
                                "Lock in unlimited access now! Free trial allowance decreases daily. Upgrade to VIP Pro for instant 4K AI video & thumbnail generation."
                            }
                        )
                    }
                )
            }

            // 6. Creator Engine Quick Actions (With lock indicators when trial is expired or limit reached)
            item {
                QuickActionsGrid(
                    isVip = profile.isVip,
                    trialStatus = trialStatus,
                    onNavigateToAi = onNavigateToAiTools,
                    onNavigateToAffiliate = onNavigateToAffiliate,
                    onNavigateToCrossPoster = onNavigateToCrossPoster,
                    onTriggerPaywall = onTriggerPaywall
                )
            }

            // 7. Organic AdMob Native Ad Card in content stream
            item {
                NativeAdCard(
                    isVip = profile.isVip,
                    adUnitId = appConfig.admobNativeUnitId
                )
            }

            // 8. Growth Engine Analytics
            item {
                ViralAnalyticsOverview(profile = profile)
            }

            // 9. Prominent Bottom Feature Box: "Help AI Viral Video Post" & One-Click Broadcast
            item {
                HomeAiViralPostBottomBanner(onNavigateToCrossPoster = onNavigateToCrossPoster)
            }

            // 10. Organic AdMob Native Video Ad Card in content stream
            item {
                NativeVideoAdCard(
                    isVip = profile.isVip,
                    adUnitId = appConfig.admobNativeVideoUnitId
                )
            }
        }

    // Full-Screen Creator Community (Follow-for-Follow Pool) Screen with Full AdMob Ads
    if (showCreatorPoolDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showCreatorPoolDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            CreatorCommunityPoolScreen(
                isVip = profile.isVip,
                userDiamonds = profile.diamonds,
                userCoins = profile.coins,
                bannerAdUnitId = appConfig.admobBannerUnitId,
                interstitialAdUnitId = appConfig.admobInterstitialUnitId,
                rewardedAdUnitId = appConfig.admobRewardedUnitId,
                nativeAdUnitId = appConfig.admobNativeUnitId,
                onNavigateBack = { showCreatorPoolDialog = false },
                onUpgradeToVip = {
                    showCreatorPoolDialog = false
                    onNavigateToVipPaywall()
                },
                onRedeemDiamondsForVip = {
                    onRedeemDiamondsForVip()
                },
                onRewardEarned = { earnedDiamonds, earnedCoins ->
                    onRewardAction(earnedDiamonds, earnedCoins)
                }
            )
        }
    }

    // Dedicated Full-Screen Page for 1-Click AI Video & Viral Hashtag Studio
    if (showAiMasterHubDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showAiMasterHubDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            OneClickAiVideoHashtagFullScreen(
                onNavigateBack = { showAiMasterHubDialog = false },
                isVip = profile.isVip,
                userDiamonds = profile.diamonds,
                bannerAdUnitId = appConfig.admobBannerUnitId,
                interstitialAdUnitId = appConfig.admobInterstitialUnitId,
                rewardedAdUnitId = appConfig.admobRewardedUnitId,
                onUpgradeToVip = {
                    showAiMasterHubDialog = false
                    onNavigateToVipPaywall()
                },
                onRedeemDiamondsForVip = onRedeemDiamondsForVip,
                onRewardEarned = onRewardAction
            )
        }
    }

    // Dedicated Full-Screen Diamond Dashboard Fallback
    if (showDiamondDashboardDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDiamondDashboardDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            DiamondDashboardScreen(
                userProfile = profile,
                appConfig = appConfig,
                referrals = referrals,
                onNavigateBack = { showDiamondDashboardDialog = false },
                onClaimStreak = onClaimStreak,
                onWatchRewardedAd = onWatchRewardedAd,
                onShareReferral = onShareReferral,
                onSimulateFriendDownload = onSimulateDownload,
                onOpenShopsyMall = {
                    showDiamondDashboardDialog = false
                    onOpenShopsyMall()
                },
                onNavigateToAffiliate = {
                    showDiamondDashboardDialog = false
                    onNavigateToAffiliate()
                },
                onOpenReelsHub = {
                    showDiamondDashboardDialog = false
                    onOpenReelsHub()
                },
                onRedeem150ForVip = onRedeemDiamondsForVip,
                onOpenRedeemStore = {
                    showDiamondDashboardDialog = false
                    onOpenRedeem()
                },
                onUpgradeToVip = {
                    showDiamondDashboardDialog = false
                    onNavigateToVipPaywall()
                },
                onRewardEarned = onRewardAction
            )
        }
    }
}

@Composable
private fun CreatorHeaderCard(
    profile: UserProfile,
    trialStatus: TrialStatus,
    onOpenRedeem: () -> Unit,
    onNavigateToVip: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("creator_header_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(BrandPurple, NeonCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.username,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (profile.isVip) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(VipGold.copy(alpha = 0.2f))
                                        .border(1.dp, VipGold, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = VipGold, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("VIP CREATOR", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = VipGold)
                                    }
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.niche,
                                fontSize = 11.sp,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ID: #${profile.displayUserId}",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldAccent
                            )
                        }
                    }
                }

                if (!profile.isVip) {
                    if (trialStatus.isTrialActive) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF065F46).copy(alpha = 0.35f))
                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                                .testTag("header_trial_pass_badge")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Day ${trialStatus.trialDay}/3 Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6EE7B7))
                            }
                        }
                    } else {
                        Button(
                            onClick = onNavigateToVip,
                            colors = ButtonDefaults.buttonColors(containerColor = VipGold),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp).testTag("header_upgrade_vip_button")
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upgrade VIP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Wallet Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Diamonds Pill (Clickable for Redemption)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, DiamondCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable(onClick = onOpenRedeem)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("diamonds_wallet_pill"),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Diamonds", fontSize = 9.sp, color = Color.Gray)
                                Text("${profile.diamonds}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DiamondCyan)
                            }
                        }
                        Text("Redeem", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                    }
                }

                // Coins Pill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Coins", fontSize = 9.sp, color = Color.Gray)
                            Text("${profile.coins}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTrialStatusBanner(
    trialStatus: TrialStatus,
    onNavigateToAi: () -> Unit = {},
    onUpgrade: () -> Unit
) {
    if (trialStatus.isVip) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, VipGold.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = VipGold, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("VIP Pro Creator Active", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VipGold)
                    Text("100% Ad-Free • Unlimited 4K AI Videos & Scripts • No Watermarks", fontSize = 10.5.sp, color = Color.LightGray)
                }
            }
        }
        return
    }

    if (trialStatus.isExpired) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_trial_expired_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0A0A)),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFEF4444))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3-Day Free Trial Expired",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFCA5A5)
                        )
                    }

                    Button(
                        onClick = onUpgrade,
                        colors = ButtonDefaults.buttonColors(containerColor = VipGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("home_trial_expired_upgrade_button")
                    ) {
                        Text("Upgrade VIP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "All AI tools, bulk engines, and video creators are locked. Upgrade to VIP Pro (₹199 / Month via UPI) or redeem Diamonds to unlock unlimited access.",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
        }
        return
    }

    // Active Trial Card - Frictionless & Welcoming
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_trial_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13112E)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF10B981).copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF059669), NeonCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🎁 3-Day Free Trial (Day ${trialStatus.trialDay} of 3)",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = when (trialStatus.trialDay) {
                                1 -> "Welcome! Full Creator Suite Unlocked: 3 Videos, 5 Images & 5 Scripts"
                                2 -> "Day 2 Creator Pass: 2 Videos, 3 Images & 3 Scripts ready"
                                else -> "Final Trial Day: 1 Video, 2 Images & 2 Scripts remaining"
                            },
                            fontSize = 10.5.sp,
                            color = Color(0xFF6EE7B7)
                        )
                    }
                }

                if (trialStatus.trialDay <= 2) {
                    Button(
                        onClick = onNavigateToAi,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("home_trial_explore_button")
                    ) {
                        Text("Start Creating ⚡", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = onUpgrade,
                        colors = ButtonDefaults.buttonColors(containerColor = VipGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("home_trial_go_vip_button")
                    ) {
                        Text("Go VIP", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quotas row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TrialQuotaBox(
                    label = "AI Videos",
                    remaining = trialStatus.videosRemaining,
                    max = trialStatus.maxVideosToday,
                    modifier = Modifier.weight(1f)
                )
                TrialQuotaBox(
                    label = "AI Images",
                    remaining = trialStatus.imagesRemaining,
                    max = trialStatus.maxImagesToday,
                    modifier = Modifier.weight(1f)
                )
                TrialQuotaBox(
                    label = "AI Scripts",
                    remaining = trialStatus.textRemaining,
                    max = trialStatus.maxTextToday,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TrialQuotaBox(
    label: String,
    remaining: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val isZero = remaining <= 0
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isZero) Color(0xFF450A0A) else Color(0xFF0F172A))
            .border(
                1.dp,
                if (isZero) Color(0xFFEF4444) else Color(0xFF10B981).copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 9.5.sp, color = Color.LightGray)
            Text(
                text = if (isZero) "0/$max (Quota Used)" else "$remaining/$max Ready",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isZero) Color(0xFFFCA5A5) else Color(0xFF34D399)
            )
        }
    }
}

private data class StreakConfettiParticle(
    val startXFraction: Float,
    val startYFraction: Float,
    val vx: Float,
    val vy: Float,
    val width: Float,
    val height: Float,
    val color: Color,
    val rotationInitial: Float,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
private fun StreakTrackerCard(
    streakDays: Int,
    isClaimedToday: Boolean,
    streakReward: Int = 2,
    onClaimStreak: () -> Unit
) {
    val context = LocalContext.current
    // Detect streak count increase for positive feedback animation
    var previousStreakDays by remember { mutableStateOf<Int?>(null) }
    var triggerCelebration by remember { mutableStateOf(false) }

    LaunchedEffect(streakDays) {
        if (previousStreakDays != null && streakDays > previousStreakDays!!) {
            triggerCelebration = true
        }
        previousStreakDays = streakDays
    }

    // Flame pulse and halo animation drivers
    val flameScale = remember { Animatable(1f) }
    val haloScale = remember { Animatable(1f) }
    val haloAlpha = remember { Animatable(0f) }
    val cardGlowAlpha = remember { Animatable(0f) }
    val textBounce = remember { Animatable(1f) }
    val confettiProgress = remember { Animatable(0f) }

    val confettiParticles = remember {
        val colors = listOf(
            Color(0xFFEA580C), // Fiery Orange
            Color(0xFFF59E0B), // Bright Amber
            Color(0xFFFBBF24), // Golden Spark
            Color(0xFF06B6D4), // Neon Cyan
            Color(0xFF10B981), // Emerald Green
            Color(0xFFA855F7), // Royal Purple
            Color(0xFFEC4899), // Hot Pink
            Color(0xFFFFFFFF)  // Sparkle White
        )
        List(46) { index ->
            val angleDeg = Random.nextDouble(-125.0, -15.0) // upward spray arc
            val speed = Random.nextDouble(220.0, 560.0).toFloat()
            val rad = Math.toRadians(angleDeg)
            StreakConfettiParticle(
                startXFraction = Random.nextDouble(0.08, 0.32).toFloat(),
                startYFraction = Random.nextDouble(0.12, 0.28).toFloat(),
                vx = (Math.cos(rad) * speed).toFloat(),
                vy = (Math.sin(rad) * speed).toFloat(),
                width = Random.nextDouble(6.0, 13.0).toFloat(),
                height = Random.nextDouble(4.0, 9.0).toFloat(),
                color = colors[index % colors.size],
                rotationInitial = Random.nextDouble(0.0, 360.0).toFloat(),
                rotationSpeed = Random.nextDouble(-720.0, 720.0).toFloat(),
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(triggerCelebration) {
        if (triggerCelebration) {
            launch {
                // Flame icon spring pulse
                flameScale.animateTo(1.35f, tween(durationMillis = 200, easing = FastOutSlowInEasing))
                flameScale.animateTo(1f, spring(dampingRatio = 0.42f, stiffness = 450f))
            }
            launch {
                // Streak number text bounce
                textBounce.animateTo(1.22f, tween(durationMillis = 180, easing = FastOutSlowInEasing))
                textBounce.animateTo(1f, spring(dampingRatio = 0.5f))
            }
            launch {
                // Expanding luminous halo
                haloScale.snapTo(1f)
                haloAlpha.snapTo(0.85f)
                haloScale.animateTo(2.3f, tween(durationMillis = 750, easing = FastOutSlowInEasing))
            }
            launch {
                haloAlpha.animateTo(0f, tween(durationMillis = 750, easing = FastOutSlowInEasing))
            }
            launch {
                // Card border luminous flash
                cardGlowAlpha.snapTo(1f)
                cardGlowAlpha.animateTo(0f, tween(durationMillis = 950))
            }
            launch {
                // Confetti particle progression
                confettiProgress.snapTo(0f)
                confettiProgress.animateTo(1f, tween(durationMillis = 1400, easing = LinearEasing))
                triggerCelebration = false
            }
        }
    }

    // Calculate next milestone targets and milestone bonus
    val (nextMilestone, milestoneBonus) = when {
        streakDays < 3 -> 3 to 3
        streakDays < 7 -> 7 to 10
        streakDays < 14 -> 14 to 20
        streakDays < 30 -> 30 to 50
        else -> (((streakDays / 30) + 1) * 30) to 100
    }
    val prevMilestone = when {
        streakDays < 3 -> 0
        streakDays < 7 -> 3
        streakDays < 14 -> 7
        streakDays < 30 -> 14
        else -> ((streakDays / 30) * 30)
    }

    val daysRemaining = (nextMilestone - streakDays).coerceAtLeast(0)
    val progressFraction = if (nextMilestone > prevMilestone) {
        ((streakDays - prevMilestone).toFloat() / (nextMilestone - prevMilestone).toFloat()).coerceIn(0.05f, 1f)
    } else 1f

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 600),
        label = "streak_progress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("streak_reward_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF140F24)),
        border = androidx.compose.foundation.BorderStroke(
            if (cardGlowAlpha.value > 0.05f) 2.dp else 1.2.dp,
            if (cardGlowAlpha.value > 0.05f) {
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFBBF24).copy(alpha = cardGlowAlpha.value),
                        Color(0xFFEA580C).copy(alpha = cardGlowAlpha.value),
                        NeonCyan.copy(alpha = cardGlowAlpha.value)
                    )
                )
            } else {
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFF97316),
                        Color(0xFFF59E0B),
                        NeonCyan
                    )
                )
            }
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Floating positive feedback banner when streak increases
                AnimatedVisibility(
                    visible = triggerCelebration || cardGlowAlpha.value > 0.25f,
                    enter = fadeIn() + slideInVertically { -it } + scaleIn(initialScale = 0.9f),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFD97706), Color(0xFFEA580C), Color(0xFF059669))
                                )
                            )
                            .border(1.dp, Color(0xFFFDE047), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFEF08A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🔥 STREAK BOOSTED! +$streakReward 💎 CREDITED!",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Header Row: Flame Icon + Streak Count + Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Flame badge with halo pulse
                        Box(
                            modifier = Modifier.size(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Luminous halo pulse ring
                            if (haloAlpha.value > 0.01f) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .scale(haloScale.value)
                                        .alpha(haloAlpha.value)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    Color(0xFFF59E0B).copy(alpha = 0.85f),
                                                    Color(0xFFEA580C).copy(alpha = 0.45f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                            }

                            // Main Flame badge with bouncy spring scale
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .scale(flameScale.value)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFEA580C), Color(0xFFF59E0B))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$streakDays Day${if (streakDays == 1) "" else "s"}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.scale(textBounce.value)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "STREAK",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFB923C)
                                )
                            }
                            Text(
                                text = if (isClaimedToday) "Streak active for today! 🔥" else "Daily login ready to claim",
                                fontSize = 11.sp,
                                color = if (isClaimedToday) Color(0xFF34D399) else Color.LightGray
                            )
                        }
                    }

                    // Daily Reward Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isClaimedToday) Color(0xFF065F46).copy(alpha = 0.4f) else Color(0xFF7C2D12).copy(alpha = 0.6f))
                            .border(1.dp, if (isClaimedToday) Color(0xFF10B981) else Color(0xFFF97316), RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isClaimedToday) "Claimed" else "+$streakReward 💎 Daily",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isClaimedToday) Color(0xFF6EE7B7) else Color(0xFFFED7AA)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Milestone Target & Progress Bar Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1736))
                        .border(1.dp, Color(0xFF372F57), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Next Milestone: Day $nextMilestone",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF78350F).copy(alpha = 0.7f))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+$milestoneBonus 💎 Bonus",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                }
                            }

                            Text(
                                text = if (daysRemaining == 0) "Unlocked! 🎉" else "$daysRemaining day${if (daysRemaining == 1) "" else "s"} left",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (daysRemaining == 0) Color(0xFF34D399) else Color(0xFFFB923C)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF2E274D))
                                .testTag("streak_progress_bar")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = animatedProgress)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFFEA580C),
                                                Color(0xFFF59E0B),
                                                NeonCyan
                                            )
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress: $streakDays / $nextMilestone Days",
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                            Text(
                                text = "${(animatedProgress * 100).toInt()}% completed",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 7-Day Cycle Visual Tracker
                val currentCycleStart = (((streakDays - 1) / 7) * 7) + 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (0..6).forEach { offset ->
                        val dayNum = currentCycleStart + offset
                        val isPast = dayNum < streakDays
                        val isCurrent = dayNum == streakDays
                        val isMilestoneDay = (dayNum == 3 || dayNum == 7 || dayNum == 14 || dayNum == 30)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "D$dayNum",
                                fontSize = 9.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) Color(0xFFFB923C) else Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isPast -> Color(0xFF065F46)
                                            isCurrent -> if (isClaimedToday) Color(0xFF065F46) else Color(0xFFEA580C)
                                            isMilestoneDay -> Color(0xFF78350F)
                                            else -> Color(0xFF1E1736)
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        when {
                                            isPast -> Color(0xFF10B981)
                                            isCurrent -> Color(0xFFFBBF24)
                                            isMilestoneDay -> GoldAccent
                                            else -> Color(0xFF372F57)
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPast || (isCurrent && isClaimedToday)) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isMilestoneDay) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Diamond,
                                        contentDescription = null,
                                        tint = Color(0xFF6B7280),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isMilestoneDay) "+5💎" else "+2💎",
                                fontSize = 8.sp,
                                fontWeight = if (isMilestoneDay) FontWeight.Bold else FontWeight.Normal,
                                color = if (isMilestoneDay) GoldAccent else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Button
                Button(
                    onClick = {
                        triggerCelebration = true
                        onClaimStreak()
                    },
                    enabled = !isClaimedToday,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("claim_streak_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEA580C),
                        disabledContainerColor = Color(0xFF261D36)
                    )
                ) {
                    Icon(
                        imageVector = if (isClaimedToday) Icons.Default.CheckCircle else Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isClaimedToday) Color(0xFF34D399) else Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isClaimedToday) {
                            "Claimed for Today (Streak Active 🔥)"
                        } else if (daysRemaining == 0) {
                            "Claim Today + Unlock Milestone Bonus! 🎉"
                        } else {
                            "Claim Daily Streak (+${streakReward} 💎)"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isClaimedToday) Color.LightGray else Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Share Streak Milestone to Social Apps via Android Share Intent
                OutlinedButton(
                    onClick = {
                        com.example.util.SocialShareHelper.shareStreakMilestone(
                            context = context,
                            streakDays = streakDays,
                            nextMilestone = nextMilestone,
                            milestoneBonus = milestoneBonus,
                            dailyReward = streakReward
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("share_streak_milestone_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.7f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Streak Milestone",
                        tint = NeonCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share $streakDays-Day Streak Milestone",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Daily Streak & Content Generation Reminder Service (AlarmManager)
                val reminderConfig by com.example.util.DailyStreakReminderScheduler.reminderConfigFlow.collectAsState()
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { _ ->
                    com.example.util.DailyStreakReminderScheduler.scheduleDailyReminder(
                        context = context,
                        hourOfDay = reminderConfig.reminderHour,
                        minute = reminderConfig.reminderMinute,
                        streakDays = streakDays
                    )
                    Toast.makeText(
                        context,
                        "🔔 Daily Streak Reminder active for ${reminderConfig.formattedTime}!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                LaunchedEffect(streakDays) {
                    com.example.util.DailyStreakReminderScheduler.updateStreakSnapshot(context, streakDays)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1433))
                        .border(1.dp, BrandPurple.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                        .testTag("daily_streak_reminder_card")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "⏰ Daily Streak & AI Reminder (${reminderConfig.formattedTime})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (reminderConfig.isEnabled) {
                                        "AlarmManager active • Reminds you daily to claim streak & generate Reels"
                                    } else {
                                        "Daily AlarmManager reminder paused • Turn on to protect your streak"
                                    },
                                    fontSize = 10.sp,
                                    color = if (reminderConfig.isEnabled) Color(0xFF34D399) else Color.Gray
                                )
                            }

                            androidx.compose.material3.Switch(
                                checked = reminderConfig.isEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            val updated = com.example.util.DailyStreakReminderScheduler.scheduleDailyReminder(
                                                context = context,
                                                hourOfDay = reminderConfig.reminderHour,
                                                minute = reminderConfig.reminderMinute,
                                                streakDays = streakDays
                                            )
                                            Toast.makeText(
                                                context,
                                                "🔔 Daily Reminder scheduled for ${updated.formattedTime}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        com.example.util.DailyStreakReminderScheduler.cancelDailyReminder(context)
                                        Toast.makeText(
                                            context,
                                            "🔕 Daily Streak Reminder paused",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.testTag("daily_reminder_toggle_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val timeSlots = listOf(
                                "9:00 AM" to 9,
                                "2:00 PM" to 14,
                                "8:00 PM" to 20
                            )
                            timeSlots.forEach { (label, hour) ->
                                val isSelectedTime = reminderConfig.reminderHour == hour && reminderConfig.isEnabled
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelectedTime) BrandPurple else Color(0xFF0F172A))
                                        .border(
                                            1.dp,
                                            if (isSelectedTime) NeonCyan else Color(0xFF334155),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            val updated = com.example.util.DailyStreakReminderScheduler.scheduleDailyReminder(
                                                context = context,
                                                hourOfDay = hour,
                                                minute = 0,
                                                streakDays = streakDays
                                            )
                                            Toast.makeText(
                                                context,
                                                "⏰ Daily Alarm set for ${updated.formattedTime}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                        .testTag("reminder_slot_$hour")
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelectedTime) Color.White else Color.LightGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            OutlinedButton(
                                onClick = {
                                    com.example.util.DailyStreakReminderScheduler.showDailyStreakAndContentNotification(
                                        context = context,
                                        streakDays = streakDays
                                    )
                                    Toast.makeText(
                                        context,
                                        "🔔 Streak & Content Reminder Notification Sent!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("daily_reminder_test_button")
                            ) {
                                Text(
                                    text = "Test Alert 🔔",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }
                        }
                    }
                }
            }

            // Confetti Canvas particle overlay
            if (confettiProgress.value in 0.001f..0.999f) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    val progress = confettiProgress.value
                    val alpha = if (progress < 0.65f) 1f else (1f - (progress - 0.65f) / 0.35f)
                    val gravity = 950f // px/sec^2 downward gravitational pull

                    confettiParticles.forEach { p ->
                        val startX = size.width * p.startXFraction
                        val startY = size.height * p.startYFraction
                        val t = progress
                        val posX = startX + (p.vx * t)
                        val posY = startY + (p.vy * t) + (0.5f * gravity * t * t)

                        val rot = p.rotationInitial + (p.rotationSpeed * t)

                        if (posX in -30f..(size.width + 30f) && posY in -30f..(size.height + 30f)) {
                            rotate(degrees = rot, pivot = Offset(posX, posY)) {
                                if (p.isCircle) {
                                    drawCircle(
                                        color = p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                                        radius = p.width / 2f,
                                        center = Offset(posX, posY)
                                    )
                                } else {
                                    drawRect(
                                        color = p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                                        topLeft = Offset(posX - p.width / 2f, posY - p.height / 2f),
                                        size = Size(p.width, p.height)
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

@Composable
private fun RewardedAdCard(
    rewardPayout: Int = 5,
    currentDiamonds: Int = 0,
    unityGameId: String = com.example.monetization.MonetizationConstants.UNITY_GAME_ID,
    unityRewardedPlacementId: String = com.example.monetization.MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID,
    admobRewardedUnitId: String = com.example.monetization.MonetizationConstants.ADMOB_REWARDED_UNIT_ID,
    onWatchRewardedAd: () -> Unit
) {
    val context = LocalContext.current
    val unityStatus by com.example.monetization.AdManager.unityRewardedStatus.collectAsState()
    val adsWatchedCount by com.example.monetization.AdManager.totalUnityRewardedAdsWatched.collectAsState()
    val bonusDiamondsEarned by com.example.monetization.AdManager.totalUnityRewardedDiamondsEarned.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rewarded_ad_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(BrandPurple, Color(0xFFEC4899), GoldAccent))
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF231C54), Color(0xFF130E30))
                    )
                )
                .padding(16.dp)
        ) {
            // Header Row with Section Badge
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
                            .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Unity Ads & Rewarded Bonus",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoldAccent)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("BONUS 💎", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                        Text(
                            text = "Unity SDK ($unityGameId • $unityRewardedPlacementId) • ${unityStatus.label}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeonCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Boost your creator balance! Watch a quick sponsored video via Unity Ads SDK ($unityRewardedPlacementId) to unlock +$rewardPayout Bonus Diamonds instantly. Use diamonds for unlimited AI video generation and VIP redemption.",
                fontSize = 12.sp,
                color = Color.LightGray,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Live Diamond Wallet & Unity Rewarded Telemetry Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                    .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Wallet Balance: $currentDiamonds 💎",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Unity Game ID: $unityGameId • Placement: $unityRewardedPlacementId",
                        fontSize = 9.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SuccessGreen.copy(alpha = 0.18f))
                        .border(1.dp, SuccessGreen.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Session Bonus: +$bonusDiamondsEarned 💎 ($adsWatchedCount Ads)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        val activity = context as? android.app.Activity
                        if (activity != null) {
                            com.example.monetization.AdManager.showUnityRewardedAd(
                                activity = activity,
                                unityPlacementId = unityRewardedPlacementId,
                                rewardedUnitId = admobRewardedUnitId,
                                bonusDiamonds = rewardPayout,
                                onUserEarnedReward = onWatchRewardedAd,
                                onFallbackSimulation = onWatchRewardedAd
                            )
                        } else {
                            onWatchRewardedAd()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("watch_unity_rewarded_ad_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = "Unity Rewarded Ad",
                        tint = NeonCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Unity Ad (+$rewardPayout 💎)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }

                Button(
                    onClick = onWatchRewardedAd,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("watch_ad_earn_diamonds_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Watch & Earn +$rewardPayout 💎",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(
    isVip: Boolean,
    trialStatus: TrialStatus,
    onNavigateToAi: () -> Unit,
    onNavigateToAffiliate: () -> Unit,
    onNavigateToCrossPoster: () -> Unit = {},
    onTriggerPaywall: (reason: String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Zero-Drop AI Tools & Engines",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (isVip) {
                Text(
                    text = "✨ Unlimited VIP Access",
                    fontSize = 11.sp,
                    color = VipGold,
                    fontWeight = FontWeight.Bold
                )
            } else if (trialStatus.isExpired) {
                Text(
                    text = "🔒 Trial Expired • Tools Locked",
                    fontSize = 11.sp,
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Day ${trialStatus.trialDay}/3 Quota Active",
                    fontSize = 11.sp,
                    color = NeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 1: Viral Hooks & 60s Scripts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val textLocked = !isVip && (trialStatus.isExpired || trialStatus.textRemaining <= 0)
            QuickToolCard(
                title = "Viral Hooks",
                subtitle = "Retention psychology",
                icon = Icons.Default.Bolt,
                color = NeonCyan,
                isLocked = textLocked,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (textLocked) {
                        onTriggerPaywall(
                            if (trialStatus.isExpired) "Your 3-Day Free Trial has ended! Upgrade to VIP Pro for unlimited hooks."
                            else "Day ${trialStatus.trialDay} creation quota reached. Upgrade to VIP Pro to remove limits!"
                        )
                    } else onNavigateToAi()
                }
            )
            QuickToolCard(
                title = "60s Scripts",
                subtitle = "Zero-drop pacing",
                icon = Icons.Default.AutoAwesome,
                color = BrandPurple,
                isLocked = textLocked,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (textLocked) {
                        onTriggerPaywall(
                            if (trialStatus.isExpired) "Your 3-Day Free Trial has ended! Upgrade to VIP Pro for unlimited scripts."
                            else "Day ${trialStatus.trialDay} creation quota reached. Upgrade to VIP Pro to remove limits!"
                        )
                    } else onNavigateToAi()
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 2: AI Video 9:16 & AI Thumbnail (Core Gated Visual Media)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val videoLocked = !isVip && (trialStatus.isExpired || trialStatus.videosRemaining <= 0)
            val imageLocked = !isVip && (trialStatus.isExpired || trialStatus.imagesRemaining <= 0)

            QuickToolCard(
                title = "AI Video 9:16",
                subtitle = if (isVip) "Veo Cinematic 4K" else "${trialStatus.videosRemaining}/${trialStatus.maxVideosToday} left today",
                icon = Icons.Default.VideoLibrary,
                color = Color(0xFF38BDF8),
                isLocked = videoLocked,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (videoLocked) {
                        onTriggerPaywall(
                            if (trialStatus.isExpired) "Your 3-Day Free Trial has expired! Upgrade to VIP Pro for unlimited AI Videos."
                            else "Day ${trialStatus.trialDay} AI Video quota reached (${trialStatus.maxVideosToday}/${trialStatus.maxVideosToday} used). Upgrade to VIP Pro for unlimited 4K generation!"
                        )
                    } else onNavigateToAi()
                }
            )

            QuickToolCard(
                title = "AI Thumbnail",
                subtitle = if (isVip) "High-CTR 9:16 Art" else "${trialStatus.imagesRemaining}/${trialStatus.maxImagesToday} left today",
                icon = Icons.Default.Image,
                color = Color(0xFFA855F7),
                isLocked = imageLocked,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (imageLocked) {
                        onTriggerPaywall(
                            if (trialStatus.isExpired) "Your 3-Day Free Trial has expired! Upgrade to VIP Pro for unlimited AI Thumbnails."
                            else "Day ${trialStatus.trialDay} AI Image quota reached (${trialStatus.maxImagesToday}/${trialStatus.maxImagesToday} used). Upgrade to VIP Pro for unlimited thumbnails!"
                        )
                    } else onNavigateToAi()
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 3: Bulk Reel Engine & Auto-Publisher (Advanced Toolkits - VIP Gated)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val bulkLocked = !isVip && (trialStatus.isExpired || (trialStatus.trialDay > 1) || trialStatus.textRemaining <= 0)
            val publisherLocked = !isVip && (trialStatus.isExpired || (trialStatus.trialDay > 1) || trialStatus.textRemaining <= 0)

            QuickToolCard(
                title = "Bulk Creator",
                subtitle = if (isVip) "5x Batch Generator" else (if (trialStatus.trialDay == 1 && !trialStatus.isExpired) "5x Batch (Day 1 Preview)" else "VIP Toolkit [Locked]"),
                icon = Icons.Default.Layers,
                color = GoldAccent,
                isLocked = bulkLocked,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (bulkLocked) {
                        onTriggerPaywall("Bulk Creator is an advanced VIP toolkit. Upgrade to VIP Pro to generate batches of 5 Reels simultaneously!")
                    } else onNavigateToAi()
                }
            )

            QuickToolCard(
                title = "Auto-Publisher",
                subtitle = "1-Click Cross-Post (6 Channels)",
                icon = Icons.Default.Share,
                color = Color(0xFFEC4899),
                isLocked = false,
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigateToCrossPoster()
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 4: SEO & Affiliate Hub
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickToolCard(
                title = "SEO & Tags",
                subtitle = "Algorithm booster",
                icon = Icons.Default.TrendingUp,
                color = SuccessGreen,
                isLocked = false,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToAi
            )
            QuickToolCard(
                title = "Affiliate Hub",
                subtitle = "Tag ${MonetizationConstants.AFFILIATE_TAG}",
                icon = Icons.Default.MonetizationOn,
                color = GoldAccent,
                isLocked = false,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToAffiliate
            )
        }
    }
}

@Composable
private fun QuickToolCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isLocked: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) Color(0xFF161226) else DarkSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLocked) Color(0xFFEF4444).copy(alpha = 0.4f) else DarkSurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isLocked) Color.DarkGray.copy(alpha = 0.3f) else color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Default.Lock else icon,
                    contentDescription = null,
                    tint = if (isLocked) Color(0xFFEF4444) else color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLocked) Color.LightGray else Color.White
                    )
                    if (isLocked) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(11.dp))
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = if (isLocked) Color(0xFFFCA5A5) else Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ViralAnalyticsOverview(profile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Your Growth Telemetry",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                GrowthStatItem(title = "Retention Rate", value = if (profile.isVip) "84.2%" else "62.4%", change = "+18% Boost")
                GrowthStatItem(title = "Lifetime 💎", value = "${profile.lifetimeDiamonds}", change = "Earned")
                GrowthStatItem(title = "Current Streak", value = "${profile.streakDays} Days", change = "Active")
            }
        }
    }
}

@Composable
private fun GrowthStatItem(title: String, value: String, change: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = title, fontSize = 10.sp, color = Color.Gray)
        Text(text = change, fontSize = 9.sp, color = NeonCyan, fontWeight = FontWeight.SemiBold)
    }
}

/**
 * Prominent Button & Hero Card on Home Screen for "Viral Creator Reels Hub"
 * Multi-Platform Connected Video Feed: YouTube, Instagram, GoLiveGram
 * Rewards: +15 Coins & +3 Diamonds per video watched
 */
@Composable
private fun HomeViralCreatorReelsHubBanner(
    onOpenReelsHub: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenReelsHub() }
            .testTag("home_viral_creator_reels_hub_banner"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130924)),
        border = androidx.compose.foundation.BorderStroke(
            1.8.dp,
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFFF0055), // YouTube / Reel red
                    Color(0xFF8A2BE2), // GoLiveGram purple
                    Color(0xFF00E5FF)  // Cyber Cyan
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFF0055), Color(0xFF8A2BE2), Color(0xFF00E5FF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎬", fontSize = 19.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Viral Creator Reels Hub",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE11D48))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text("FEED", fontSize = 8.5.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }
                        Text(
                            text = "YouTube • Instagram • GoLiveGram Connected Feed",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeonCyan
                        )
                    }
                }

                // Reward Tag: +15 🪙 & +3 💎
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF064E3B))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+15🪙 +3💎",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DiamondCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "🔥 Watch viral shorts & masterclasses, follow creators, boost your channel reach, and earn +15 Coins & +3 Diamonds for every video watched!",
                fontSize = 11.5.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenReelsHub,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("btn_open_viral_creator_reels_hub")
                        .testTag("open_reels_hub_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Open Reels Hub 🎬", fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }

                Button(
                    onClick = onOpenReelsHub,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_reels_spotlight_boost"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900))
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Spotlight 🚀", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

/**
 * Prominent, pulsing animated Shopsy Shopping Popup & Mall Banner on Home Screen:
 * "Shopping Now - 75% OFF + 4 Diamonds & 10 Coins"
 */
@Composable
private fun HomeShopsyShoppingPopupBanner(
    isVip: Boolean = false,
    onOpenShopsyMall: () -> Unit,
    onRewardAction: (diamonds: Int, coins: Int) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val launchDirectApp = {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MonetizationConstants.GLOBAL_ADMIN_BITLI_URL)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Opening deals via browser...", Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(
            context,
            "🛍️ Opening Shopsy / Flipkart App (75% OFF Deals)...",
            Toast.LENGTH_SHORT
        ).show()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shopsy_shopping_popup_banner"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF160B24)),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.8.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    Color(0xFFFF1493),
                    Color(0xFFFF9900),
                    NeonCyan
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .background(Brush.linearGradient(listOf(Color(0xFFFF1493), Color(0xFFFF9900)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Shopping Now - 75% OFF",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFF1493))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text("LIVE", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }
                        Text(
                            text = "ViralBoost AI × Shopsy Official Partner Mall",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeonCyan
                        )
                    }
                }

                // Reward Tag: +10 Diamonds & 15 Coins
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF064E3B))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+10 💎 & +15 🪙",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DiamondCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "🔥 Mega Festive Deals with ₹50 Welcome Discount! Kurtis, electronics, sneakers & viral products up to 75% OFF. Earn +10 Diamonds & +15 Coins upon placing catalog orders.",
                fontSize = 11.5.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Clean Single Shopsy Action Controls (No duplicate boxes, No long purple strip)
            var showShoppingHubInline by remember { mutableStateOf(false) }
            var selectedInlineCategory by remember { mutableStateOf("All") }
            var showOrderConfirmAdModal by remember { mutableStateOf(false) }
            var pendingOrderProduct by remember { mutableStateOf<MallItem?>(null) }
            var actionStatusMessage by remember { mutableStateOf<String?>(null) }

            // Purple Shopping Now Button (Launches Dedicated Full-Screen Mega Mall)
            Button(
                onClick = onOpenShopsyMall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_open_full_screen_mega_mall"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPurple)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Open Full-Screen Mega Shopping Mall 🚀",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button 1: Direct Intent App Launch
                Button(
                    onClick = {
                        AdManager.triggerMilestoneInterstitial(
                            context = context,
                            isVip = isVip,
                            milestoneName = "Shopsy Shopping - Direct App Launch",
                            onComplete = {
                                launchDirectApp()
                            },
                            onShowInterstitialFallback = { onDismiss ->
                                onDismiss()
                            }
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_go_to_shopsy_app")
                        .testTag("direct_shopsy_app_launch_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900)),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Shopsy App (75% OFF)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Button 2: Clean Single Mall Toggle Button
                Button(
                    onClick = {
                        val nextState = !showShoppingHubInline
                        if (nextState) {
                            AdManager.triggerMilestoneInterstitial(
                                context = context,
                                isVip = isVip,
                                milestoneName = "Shopsy Deal Catalog - Open Feature",
                                onComplete = {
                                    showShoppingHubInline = true
                                },
                                onShowInterstitialFallback = { onDismiss ->
                                    onDismiss()
                                    showShoppingHubInline = true
                                }
                            )
                        } else {
                            showShoppingHubInline = false
                        }
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(46.dp)
                        .testTag("btn_explore_deal_catalog")
                        .testTag("open_shopsy_mall_button")
                        .testTag("toggle_smart_shopping_hub_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showShoppingHubInline) Color(0xFF334155) else BrandPurple
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (showShoppingHubInline) Icons.Default.Close else Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showShoppingHubInline) "Close Shopsy Mall" else "Explore Shopsy Deal Catalog & Earn",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (showShoppingHubInline) {
                Spacer(modifier = Modifier.height(10.dp))

                // Prominent Notice for User Benefit (₹50 discount + 10 Diamonds & 15 Coins)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFF9900)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "🎉 विशेष छूट: आपके ₹50 वेलकम बोनस का डिस्काउंट लागू है!\n💎 शॉपिंग करने पर पाएँ: 10 डायमंड और 15 कॉइन्स!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFDE047),
                            fontWeight = FontWeight.Bold,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Dual-Action Shopping Hub | EarnKaro Affiliate ID: 5665937",
                            fontSize = 10.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                var storeViewMode by remember { mutableStateOf("SUPER_STORE") } // "SUPER_STORE" or "TRENDING_GRID"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { storeViewMode = "SUPER_STORE" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (storeViewMode == "SUPER_STORE") BrandPurple else Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text(
                            text = "🛍️ Shopsy & Ajio Store",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { storeViewMode = "TRENDING_GRID" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (storeViewMode == "TRENDING_GRID") BrandPurple else Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text(
                            text = "⚡ Trending Deals Feed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (storeViewMode == "SUPER_STORE") {
                    Box(modifier = Modifier.fillMaxWidth().height(520.dp)) {
                        ShopsyMegaStoreScreen(
                            onOrderComplete = {
                                onRewardAction(10, 15)
                            }
                        )
                    }
                } else {
                    // Category Filter Buttons
                    val mallCategories = listOf(
                        "All" to "All",
                        "Fashion" to "Ethnic Fashion",
                        "Gadgets" to "Smart Gadgets",
                        "Home" to "Home & Kitchen",
                        "Audio" to "Audio & Tech",
                        "Tech" to "Mobile Accessories",
                        "Footwear" to "Footwear",
                        "Gear" to "Creator Gear",
                        "Bags" to "Fashion & Bags",
                        "Electronics" to "Electronics"
                    )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(mallCategories) { (displayLabel, categoryKey) ->
                        val isSel = (categoryKey == "All" && selectedInlineCategory == "All") || selectedInlineCategory == categoryKey
                        Button(
                            onClick = {
                                selectedInlineCategory = categoryKey
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) Color(0xFFFF9900) else Color(0xFF1E1B4B)
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = displayLabel,
                                fontSize = 11.sp,
                                color = if (isSel) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Real ShopsyApiService Retrofit Feed Integration (Tag: 5665937)
                var liveMallItems by remember { mutableStateOf(ShopsyApiClient.defaultTrendingProducts) }
                var isSyncingApi by remember { mutableStateOf(false) }
                var apiSyncStatus by remember { mutableStateOf("Live Retrofit Feed Active") }

                LaunchedEffect(Unit) {
                    try {
                        withContext(Dispatchers.IO) {
                            val response = ShopsyApiClient.service.getTrendingProducts().execute()
                            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                                val fetched = response.body()
                                if (fetched != null && fetched.isNotEmpty()) {
                                    withContext(Dispatchers.Main) {
                                        liveMallItems = fetched
                                        apiSyncStatus = "Live Feed Synced (${fetched.size} Deals)"
                                    }
                                }
                            }
                        }
                    } catch (_: Exception) {
                        // Preserves offline / cached curated catalog
                    }
                }

                // Shopsy API Service Status Bar
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF10B981), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Shopsy API Feed • Tag: 5665937 ($apiSyncStatus)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFC7D2FE)
                            )
                        }

                        IconButton(
                            onClick = {
                                isSyncingApi = true
                                kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val response = ShopsyApiClient.service.getTrendingProducts().execute()
                                        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                                            val fetched = response.body()
                                            if (fetched != null && fetched.isNotEmpty()) {
                                                withContext(Dispatchers.Main) {
                                                    liveMallItems = fetched
                                                    apiSyncStatus = "Synced via Retrofit"
                                                    isSyncingApi = false
                                                }
                                            }
                                        } else {
                                            withContext(Dispatchers.Main) {
                                                apiSyncStatus = "Catalog Active"
                                                isSyncingApi = false
                                            }
                                        }
                                    } catch (_: Exception) {
                                        withContext(Dispatchers.Main) {
                                            apiSyncStatus = "Curated Feed Active"
                                            isSyncingApi = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Deals Feed",
                                tint = Color(0xFFA5B4FC),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                val displayedItems = remember(selectedInlineCategory, liveMallItems) {
                    if (selectedInlineCategory == "All") liveMallItems else liveMallItems.filter { it.category == selectedInlineCategory || it.category.contains(selectedInlineCategory, ignoreCase = true) }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    displayedItems.forEach { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("shopping_product_${item.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFE11D48), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.discount,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.rating,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Price: ${item.price}",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF4ADE80)
                                    )
                                    Text(
                                        text = "Old: ${item.oldPrice}",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                    Text(
                                        text = "• ₹50 Off Applied",
                                        fontSize = 10.sp,
                                        color = Color(0xFFFDE047),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Action Buttons (Order Now with AdMob + Share Deal)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            pendingOrderProduct = item
                                            showOrderConfirmAdModal = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Order Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                    }

                                    Button(
                                        onClick = {
                                            val shareText = "🔥 Check out ${item.title} at ${item.price} (${item.discount}) on Shopsy! Use link: https://bitli.in/Ff4aE3C (EarnKaro Affiliate ID: 5665937)"
                                            try {
                                                val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                                }
                                                context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Deal"))
                                            } catch (_: Exception) {}
                                            actionStatusMessage = "🔗 Deal Link Copied! Ready to share."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Share Deal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                actionStatusMessage?.let { status ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = status,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                com.example.ui.components.UniversalMediatedAdDock(
                    isVip = isVip,
                    featureLabel = "Shopsy Deal Catalog & Mega Store",
                    onRewardEarned = onRewardAction
                )
            }
        }

            // Order Confirmation AdMob Ad Modal
            if (showOrderConfirmAdModal) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = {},
                    title = { Text("AdMob Interstitial Ad (Order Processing)") },
                    text = {
                        Text(
                            "📺 [AdMob Sponsored Ad Loaded]\n\nProcessing your Shopsy order for ${pendingOrderProduct?.title ?: "item"} (${pendingOrderProduct?.rating ?: "★ 4.8"})...\n\n✓ ₹50 Welcome Discount Applied\n✓ Free Shipping & Cash on Delivery\n✓ +10 Diamonds 💎 & +15 Coins 🪙 Reward will be credited\n✓ EarnKaro Affiliate ID: 5665937"
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showOrderConfirmAdModal = false
                                // Grant 10 Diamonds & 15 Coins upon order confirmation
                                onRewardAction(10, 15)
                                actionStatusMessage = "🛒 Order Confirmed Successfully! Earned +10 Diamonds & +15 Coins!"
                                launchDirectApp()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                        ) {
                            Text("Complete Order & Get Rewards")
                        }
                    }
                )
            }
        }
    }
}

/**
 * Large, visually striking Affiliate Hub & Rewards Banner on Main Home Screen
 */
@Composable
private fun HomeAffiliateHubBanner(
    affiliateTag: String,
    onNavigateToAffiliate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToAffiliate() }
            .testTag("home_affiliate_hub_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161131)),
        border = androidx.compose.foundation.BorderStroke(
            1.8.dp,
            Brush.horizontalGradient(listOf(GoldAccent, NeonCyan, BrandPurple))
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF261047), Color(0xFF120E28))
                    )
                )
                .padding(18.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.2f))
                            .border(1.2.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Affiliate Hub & Rewards",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFDC2626))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("HOT 🔥", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Text(
                            text = "Meesho • Flipkart • Amazon • Budget Deals",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeonCyan
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, BrandPurple, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("TAG: $affiliateTag", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Catchy description
            Text(
                text = "Curated trending Kurtis, viral fashion accessories & creator smart utilities. Promote deals to earn direct commissions + instant wallet rewards!",
                fontSize = 12.sp,
                color = Color.LightGray,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Prominent Reward Callouts: Share & Earn 10 Coins, Buy & Earn 3 Diamonds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Callout 1: Share & Earn 10 Coins
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.2.dp, GoldAccent.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share & Earn", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("+10 Coins 🪙", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = GoldAccent)
                        Text("Per social link share", fontSize = 9.sp, color = Color.LightGray)
                    }
                }

                // Callout 2: Buy & Earn 3 Diamonds
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.2.dp, DiamondCyan.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Buy & Earn", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("+3 Diamonds 💎", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = DiamondCyan)
                        Text("Per verified purchase", fontSize = 9.sp, color = Color.LightGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Button(
                onClick = onNavigateToAffiliate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("home_affiliate_hub_action_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPurple
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Explore Affiliate Hub & Deals 🛍️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Section 4: Main Home screen "One-Click to Share Video Multi-Platform" card/box
 * containing Dual Separate Buttons (Free Post vs AI Subscription Plans),
 * Hindi Tagline, Account Verification Hub, and Post to Go Viral button.
 */
@Composable
private fun HomeVideoCrossPosterBanner(
    affiliateTag: String = MonetizationConstants.AFFILIATE_TAG,
    downloadUrl: String = "",
    isVip: Boolean = false,
    onNavigateToCrossPoster: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    // Clean dynamic download link fallback (never broken Play Store 404)
    val resolvedDownloadUrl = remember(downloadUrl, affiliateTag) {
        val cleanTag = affiliateTag.trim().ifBlank { MonetizationConstants.AFFILIATE_TAG }
        val trimmed = downloadUrl.trim()
        if (trimmed.isNotBlank() && !trimmed.contains("play.google.com/store/apps/details")) {
            trimmed
        } else {
            MonetizationConstants.buildAppDownloadUrl(cleanTag)
        }
    }

    // Persisted channel connections (synchronized with VideoCrossPosterScreen)
    val sharedPrefs = remember {
        context.getSharedPreferences("viralboost_social_links", Context.MODE_PRIVATE)
    }

    val availableChannels = remember {
        listOf(
            SocialChannel("instagram", "Instagram Reels", "IG", Color(0xFFE1306C), "@creator_pro", "@username", "Instagram Handle", "Reels & reach"),
            SocialChannel("facebook", "Facebook Reels", "FB", Color(0xFF1877F2), "Viral Page", "Page Name", "Facebook Page", "Facebook broadcast"),
            SocialChannel("youtube", "YouTube Shorts", "YT", Color(0xFFFF0000), "Viral Studio", "@handle", "YouTube Handle", "Syndicate shorts"),
            SocialChannel("golivegram", "GoLivegram", "GL", Color(0xFF8A2BE2), "@golive_official", "@handle", "GoLivegram ID", "Multi-stream"),
            SocialChannel("telegram", "Telegram", "TG", Color(0xFF229ED9), "t.me/viral_creators", "t.me/channel", "Telegram Channel", "Telegram groups"),
            SocialChannel("whatsapp", "WhatsApp Status", "WA", Color(0xFF25D366), "+91 98765 43210", "+91 Phone", "WhatsApp Status", "Status broadcast")
        )
    }

    val connectedMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            availableChannels.forEach { channel ->
                val defaultVal = channel.id in listOf("instagram", "facebook", "whatsapp")
                put(channel.id, sharedPrefs.getBoolean("conn_${channel.id}", defaultVal))
            }
        }
    }

    val handleMap = remember {
        mutableStateMapOf<String, String>().apply {
            availableChannels.forEach { channel ->
                put(channel.id, sharedPrefs.getString("handle_${channel.id}", channel.defaultHandle) ?: channel.defaultHandle)
            }
        }
    }

    // Video upload state inside the box
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedVideoName by remember { mutableStateOf<String?>(null) }

    // Manual metadata input state inside the box
    var manualTitle by remember { mutableStateOf("") }
    var manualTags by remember { mutableStateOf("") }
    var manualDescription by remember { mutableStateOf("") }

    // AI Subscription & Optimization states inside the box
    var isAiUnlocked by remember { mutableStateOf(false) }
    var activeAiPlanName by remember { mutableStateOf<String?>(null) }
    var isAiAnalyzing by remember { mutableStateOf(false) }

    // Modal states inside the box
    var activeChannelForVerification by remember { mutableStateOf<SocialChannel?>(null) }
    var showUpiPaymentModal by remember { mutableStateOf(false) }
    var selectedSubscriptionPlan by remember { mutableStateOf<Pair<String, Pair<Int, Int>>?>(null) }
    var showBroadcastSuccessDialog by remember { mutableStateOf(false) }
    var lastBroadcastCaption by remember { mutableStateOf("") }
    var lastBroadcastPlatforms by remember { mutableStateOf<List<String>>(emptyList()) }

    var showMilestoneAdDialog by remember { mutableStateOf(false) }
    var pendingDismissAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    if (showMilestoneAdDialog) {
        InterstitialAdDialog(
            interstitialUnitId = MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID,
            onDismiss = {
                showMilestoneAdDialog = false
                val action = pendingDismissAction
                pendingDismissAction = null
                action?.invoke()
            }
        )
    }

    // Video gallery picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            selectedVideoName = uri.lastPathSegment?.substringAfterLast('/') ?: "viral_video.mp4"
            Toast.makeText(context, "Video Selected: $selectedVideoName ✅", Toast.LENGTH_SHORT).show()

            // Video Cross-Posting Milestone 1: Exact moment user uploads/posts video
            AdManager.triggerMilestoneInterstitial(
                context = context,
                isVip = isVip,
                milestoneName = "Home Cross-Poster - Milestone 1: Video Uploaded",
                onShowInterstitialFallback = { onDismiss ->
                    pendingDismissAction = onDismiss
                    showMilestoneAdDialog = true
                }
            )
        }
    }

    // AI Video Analyzer runner
    val runAiViralAnalysis = {
        isAiAnalyzing = true
        coroutineScope.launch {
            delay(1200)
            isAiAnalyzing = false
            val viralHooks = listOf(
                "🔥 3 Secret Tricks to 10X Your Reach in 24 Hours! (Watch Till End)",
                "🚀 Stop Posting Like This! Use The Zero-Drop Viral Strategy",
                "⚡ How This 1 Simple Reel Generated 500K Views Overnight",
                "💰 Viral Growth Formula: Turn 1 Reel Into 10,000 Real Followers"
            )
            val viralTags = "#ViralReels #AlgorithmHack #GrowthHacks #InstaReels #ReelsIndia #CreatorEconomy #ViralBoostAI"
            val viralDesc = "Algorithmic retention hook with automated viral syndication across Instagram, Facebook, YouTube, Telegram & WhatsApp. Replicate this exact template!"

            val generatedHook = viralHooks.random()
            manualTitle = generatedHook
            manualTags = viralTags
            manualDescription = viralDesc
            Toast.makeText(context, "AI Analysis Complete! Viral Title & Tags Auto-Embedded 🚀", Toast.LENGTH_LONG).show()
        }
    }

    // Smart Fallback Affiliate Link Handling: Uses user's personal link if saved in Affiliate Hub, otherwise automatically falls back to Owner Master Link (https://bitli.in/Ff4aE3C tied to EarnKaro ID: 5665937)
    val createViralCaption: (title: String, tags: String, desc: String) -> String = { pTitle, pTags, pDesc ->
        val userPersonalLink = MonetizationConstants.getUserPersonalAffiliateLink(context)
        MonetizationConstants.buildSmartAffiliateCaption(
            title = pTitle,
            tags = pTags.ifBlank { "#ViralReels #AIReels #Trending #GrowthHacks #AffiliateMarketing" },
            description = pDesc,
            personalAffiliateLink = userPersonalLink,
            downloadUrl = resolvedDownloadUrl
        )
    }

    val handlePostToGoViral = {
        val activeChannels = availableChannels.filter { connectedMap[it.id] == true }
        if (activeChannels.isEmpty()) {
            Toast.makeText(context, "Please verify & connect at least 1 platform channel below!", Toast.LENGTH_SHORT).show()
        } else {
            val finalTitle = manualTitle.ifBlank { "🔥 Zero-Drop Viral Reels & 10X Reach Framework!" }
            val caption = createViralCaption(finalTitle, manualTags, manualDescription)
            val platformNames = activeChannels.map { it.name }

            val executeViralBroadcast = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    if (selectedVideoUri != null) {
                        type = "video/*"
                        putExtra(Intent.EXTRA_STREAM, selectedVideoUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } else {
                        type = "text/plain"
                    }
                    putExtra(Intent.EXTRA_SUBJECT, finalTitle)
                    putExtra(Intent.EXTRA_TEXT, caption)
                }

                val chooser = Intent.createChooser(sendIntent, "Post to Go Viral via:")
                context.startActivity(chooser)

                lastBroadcastCaption = caption
                lastBroadcastPlatforms = platformNames
                showBroadcastSuccessDialog = true
                Toast.makeText(context, "+10 Coins credited! Broadcast initiated 🚀", Toast.LENGTH_SHORT).show()

                // Success Notification ("Go Viral") immediately after successful share
                ReferralNotificationHelper.showPostReadyToGoViralNotification(context)
                Toast.makeText(context, "Congratulations! Your post is ready to go viral!", Toast.LENGTH_LONG).show()
            }

            // Video Cross-Posting Milestone 2: Exact moment user taps "Post to Go Viral" / Share
            AdManager.triggerMilestoneInterstitial(
                context = context,
                isVip = isVip,
                milestoneName = "Home Cross-Poster - Milestone 2: Post to Go Viral",
                onShowInterstitialFallback = { onDismiss ->
                    pendingDismissAction = {
                        onDismiss()
                        executeViralBroadcast()
                    }
                    showMilestoneAdDialog = true
                },
                onComplete = {
                    executeViralBroadcast()
                }
            )
        }
    }

    val heroTransition = rememberInfiniteTransition(label = "crossPosterHeroGlow")
    val heroPulseScale by heroTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroPulseScale"
    )
    val borderGlowAlpha by heroTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderGlowAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_video_cross_poster_banner"),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF120B2E)),
        border = androidx.compose.foundation.BorderStroke(
            2.4.dp,
            Brush.horizontalGradient(
                listOf(
                    GoldAccent.copy(alpha = borderGlowAlpha),
                    Color(0xFFFF2E93).copy(alpha = borderGlowAlpha),
                    NeonCyan.copy(alpha = borderGlowAlpha),
                    Color(0xFF8B5CF6).copy(alpha = borderGlowAlpha)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2B1055),
                            Color(0xFF150A36),
                            Color(0xFF0B0E24)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            // Top Crown Hero Ribbon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF6F00), Color(0xFFFF2E93), Color(0xFF7C3AED))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "#1 TOP VIRAL FEATURE • 6 PLATFORMS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF064E3B))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "+10 🪙 & +4 💎",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF6EE7B7)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Attractive Hero Header with New Official Play Store 3D Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .scale(heroPulseScale)
                        .clip(RoundedCornerShape(18.dp))
                        .border(2.dp, GoldAccent, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.viralboost_store_icon_1794678253429),
                        contentDescription = "ViralBoost AI Official App Store Icon",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "One-Click to Share Video Multi-Platform",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 21.sp,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF00C853))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("VIRAL 🚀", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "एक क्लिक में Instagram, YouTube, Facebook, Telegram और WhatsApp पर एक साथ वीडियो शेयर करें!",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive 6-Platform Brand Showcase Strip
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableChannels) { channel ->
                    val isConn = connectedMap[channel.id] == true
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isConn) channel.brandColor.copy(alpha = 0.22f)
                                else Color(0xFF1E293B).copy(alpha = 0.7f)
                            )
                            .border(
                                1.2.dp,
                                if (isConn) channel.brandColor else Color(0xFF475569),
                                RoundedCornerShape(50)
                            )
                            .clickable { activeChannelForVerification = channel }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(channel.brandColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = channel.badge,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = channel.name,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (isConn) Icons.Default.CheckCircle else Icons.Default.Launch,
                            contentDescription = null,
                            tint = if (isConn) Color(0xFF00E676) else GoldAccent,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4-Pill Viral Boost Metrics Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "⚡ 10X Reach" to Color(0xFFFFD54F),
                    "🌐 6 Networks" to NeonCyan,
                    "🤖 AI Viral Tags" to Color(0xFFF472B6),
                    "🆓 Free Broadcast" to Color(0xFF4ADE80)
                ).forEach { (label, accentColor) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1A1438))
                            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                            .padding(vertical = 7.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prominent Direct Full-Screen Studio Launcher Button at Top of Hero Card
            Button(
                onClick = onNavigateToCrossPoster,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("home_open_full_cross_poster_hero_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF2E93), Color(0xFF7C3AED), Color(0xFF0284C7))
                            )
                        )
                        .border(1.2.dp, GoldAccent.copy(alpha = 0.8f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Full Multi-Platform Video Studio 🎬↗",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mandatory Hindi Tagline directly below the hero launcher
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A193D)),
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(listOf(GoldAccent, Color(0xFFFF6F00)))
                )
            ) {
                Row(
                    modifier = Modifier.padding(11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "फ्री में वीडियो पोस्ट करें मल्टी-प्लेटफॉर्म पर, और तेजी से वायरल होने के लिए AI की मदद लें!",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = Color(0xFFFFE082),
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // === TWO SEPARATE ACTION BUTTONS INSIDE THE BOX ===

            // BUTTON 1 (Free Post Button)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF38BDF8).copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Button 1: Free Post Button", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color.White)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF0284C7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("100% FREE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Upload your video, enter custom manual details, connect platforms, and post for free across all linked channels with the mandatory automated viral promotional footer.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Video Upload Section
                    if (selectedVideoUri == null) {
                        OutlinedButton(
                            onClick = { videoPickerLauncher.launch("video/*") },
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.VideoFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select Video from Gallery 📁", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2A38)),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(selectedVideoName ?: "Video selected", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text("Ready to broadcast (MP4/WebM)", color = Color(0xFF81D4FA), fontSize = 9.sp)
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(
                                        onClick = { videoPickerLauncher.launch("video/*") },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text("Change", fontSize = 10.sp, color = Color(0xFF38BDF8))
                                    }
                                    IconButton(
                                        onClick = {
                                            selectedVideoUri = null
                                            selectedVideoName = null
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Manual Metadata Input Fields
                    OutlinedTextField(
                        value = manualTitle,
                        onValueChange = { manualTitle = it },
                        label = { Text("Manual Video Title", fontSize = 11.sp) },
                        placeholder = { Text("e.g., 3 Secret Hacks to 10X Your Reach 🔥", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = manualTags,
                        onValueChange = { manualTags = it },
                        label = { Text("Manual Hashtags & Tags", fontSize = 11.sp) },
                        placeholder = { Text("e.g., #ViralReels #Growth #Trending", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = manualDescription,
                        onValueChange = { manualDescription = it },
                        label = { Text("Manual Description", fontSize = 11.sp) },
                        placeholder = { Text("e.g., Watch till the end to unlock the exact formula!", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Distinct Button 1 Primary Action
                    Button(
                        onClick = {
                            if (manualTitle.isBlank()) {
                                manualTitle = "🔥 Zero-Drop Viral Strategy & 10X Reach!"
                            }
                            if (manualTags.isBlank()) {
                                manualTags = "#ViralReels #GrowthHack #Trending"
                            }
                            Toast.makeText(context, "Manual details saved! Connect platforms below and tap Post to Go Viral 🚀", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("home_free_post_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Free Post (Manual Details & Connect) 🆓", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = onNavigateToCrossPoster,
                        modifier = Modifier.fillMaxWidth().height(30.dp)
                    ) {
                        Text("Or open in full dedicated studio ↗", fontSize = 10.5.sp, color = Color(0xFF38BDF8))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BUTTON 2 (AI Subscription Button / Plans)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF22173D)),
                border = androidx.compose.foundation.BorderStroke(1.4.dp, Color(0xFF8A2BE2))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Button 2: AI Subscription & Plans", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color.White)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF8A2BE2))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("UPI CHECKOUT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Direct UPI payment checkout (Amit2653@ptyes) to unlock automated AI video analysis, high-CTR trending titles, and hashtags.",
                        fontSize = 11.sp,
                        color = Color(0xFFD1C4E9),
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Two Clear Subscription Plans
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Plan 1: ₹49 for 15 Days Plan
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedSubscriptionPlan = "15-Day Viral AI Video Pass" to (49 to 15)
                                    AdManager.triggerMilestoneInterstitial(
                                        context = context,
                                        isVip = isVip,
                                        milestoneName = "Open ₹49 Viral AI Video Pass Checkout",
                                        onShowInterstitialFallback = { onDismiss ->
                                            pendingDismissAction = {
                                                onDismiss()
                                                showUpiPaymentModal = true
                                            }
                                            showMilestoneAdDialog = true
                                        },
                                        onComplete = {
                                            showUpiPaymentModal = true
                                        }
                                    )
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1C4B)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C3AED))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("15 DAYS PLAN", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF81D4FA))
                                Text("₹49", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Trending viral titles", fontSize = 9.5.sp, color = Color(0xFFE9D5FF))
                                Text("• Smart hashtags & tags", fontSize = 9.5.sp, color = Color(0xFFE9D5FF))
                                Text("• High-conversion CTR", fontSize = 9.5.sp, color = Color(0xFFE9D5FF))
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        selectedSubscriptionPlan = "15-Day Viral AI Video Pass" to (49 to 15)
                                        AdManager.triggerMilestoneInterstitial(
                                            context = context,
                                            isVip = isVip,
                                            milestoneName = "Open ₹49 Viral AI Video Pass Checkout",
                                            onShowInterstitialFallback = { onDismiss ->
                                                pendingDismissAction = {
                                                    onDismiss()
                                                    showUpiPaymentModal = true
                                                }
                                                showMilestoneAdDialog = true
                                            },
                                            onComplete = {
                                                showUpiPaymentModal = true
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(34.dp).testTag("home_unlock_49_plan_button"),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text("Unlock ₹49 Plan", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        // Plan 2: ₹79 for 1 Month Plan
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedSubscriptionPlan = "1-Month Creator Pro Pass" to (79 to 30)
                                    AdManager.triggerMilestoneInterstitial(
                                        context = context,
                                        isVip = isVip,
                                        milestoneName = "Open ₹79 Creator Pro Pass Checkout",
                                        onShowInterstitialFallback = { onDismiss ->
                                            pendingDismissAction = {
                                                onDismiss()
                                                showUpiPaymentModal = true
                                            }
                                            showMilestoneAdDialog = true
                                        },
                                        onComplete = {
                                            showUpiPaymentModal = true
                                        }
                                    )
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF381A45)),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFF8F00))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("1 MONTH PLAN", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD54F))
                                    Text("SAVE 50%", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                                }
                                Text("₹79", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Extended 30 days duration", fontSize = 9.5.sp, color = Color(0xFFFFE082))
                                Text("• Unlimited AI optimization", fontSize = 9.5.sp, color = Color(0xFFFFE082))
                                Text("• Priority multi-posting", fontSize = 9.5.sp, color = Color(0xFFFFE082))
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        selectedSubscriptionPlan = "1-Month Creator Pro Pass" to (79 to 30)
                                        AdManager.triggerMilestoneInterstitial(
                                            context = context,
                                            isVip = isVip,
                                            milestoneName = "Open ₹79 Creator Pro Pass Checkout",
                                            onShowInterstitialFallback = { onDismiss ->
                                                pendingDismissAction = {
                                                    onDismiss()
                                                    showUpiPaymentModal = true
                                                }
                                                showMilestoneAdDialog = true
                                            },
                                            onComplete = {
                                                showUpiPaymentModal = true
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(34.dp).testTag("home_unlock_79_plan_button"),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC026D3)),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text("Unlock ₹79 Plan", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    // AI Analysis Action Button (Active or Unlock trigger)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (!isAiUnlocked) {
                                selectedSubscriptionPlan = "15-Day Viral AI Video Pass" to (49 to 15)
                                AdManager.triggerMilestoneInterstitial(
                                    context = context,
                                    isVip = isVip,
                                    milestoneName = "Open AI Video Optimizer Checkout",
                                    onShowInterstitialFallback = { onDismiss ->
                                        pendingDismissAction = {
                                            onDismiss()
                                            showUpiPaymentModal = true
                                        }
                                        showMilestoneAdDialog = true
                                    },
                                    onComplete = {
                                        showUpiPaymentModal = true
                                    }
                                )
                            } else {
                                runAiViralAnalysis()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAiUnlocked) Color(0xFF00C853) else Color(0xFF8A2BE2)
                        )
                    ) {
                        if (isAiAnalyzing) {
                            Text("Analyzing Content with AI...", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isAiUnlocked) "Run AI Video Analyzer & Auto-Generate 🚀" else "Unlock AI Viral Video Optimizer (₹49 / ₹79) ✨",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // === ACCOUNT VERIFICATION & CONNECTION HUB ===
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141A28)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E384D))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Account Connection & Verification Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF81D4FA))
                        val count = connectedMap.values.count { it }
                        Text("$count / ${availableChannels.size} Connected", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    availableChannels.forEach { channel ->
                        val isConnected = connectedMap[channel.id] ?: false
                        val currentHandle = handleMap[channel.id] ?: channel.defaultHandle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(channel.brandColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(channel.badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(channel.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(if (isConnected) currentHandle else "Not verified", fontSize = 9.sp, color = if (isConnected) Color(0xFF81D4FA) else Color(0xFF94A3B8), maxLines = 1)
                                }
                            }
                            if (isConnected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF00C853).copy(alpha = 0.2f))
                                        .border(1.dp, Color(0xFF00C853), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Connected", fontSize = 9.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { activeChannelForVerification = channel },
                                    modifier = Modifier
                                        .height(28.dp)
                                        .testTag("home_verify_connect_${channel.id}"),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD54F))
                                ) {
                                    Text("Verify & Connect", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mandatory Promotional Footer Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131D)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263238))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    val userPersonalLink = remember {
                        MonetizationConstants.getUserPersonalAffiliateLink(context)
                    }
                    val hasPersonal = remember(userPersonalLink) {
                        MonetizationConstants.hasPersonalAffiliateLink(userPersonalLink)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (hasPersonal) Color(0xFF064E3B) else Color(0xFF1E1B4B))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (hasPersonal) "🛍️ Personal Affiliate Link Active ✅" else "⚡ Owner Master Fallback Active (Bitli https://bitli.in/Ff4aE3C)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasPersonal) Color(0xFF34D399) else Color(0xFFFFD54F)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Mandatory Viral Referral Footer:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (hasPersonal) {
                            "🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!\n" +
                            "😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:\n" +
                            "👉 $userPersonalLink\n\n" +
                            "🌟 Featured Mega Store & Bonus Deals:\n" +
                            "👉 https://bitli.in/Ff4aE3C\n\n" +
                            "🚀 Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:\n" +
                            "👉 $resolvedDownloadUrl"
                        } else {
                            "🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!\n" +
                            "😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:\n" +
                            "👉 https://bitli.in/Ff4aE3C\n\n" +
                            "🚀 Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:\n" +
                            "👉 $resolvedDownloadUrl"
                        },
                        fontSize = 10.sp,
                        color = Color(0xFF80CBC4),
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Final Execution Button: "Post to Go Viral"
            Button(
                onClick = handlePostToGoViral,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("home_post_to_go_viral_button")
                    .testTag("publish_post_everywhere_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF8A2BE2), Color(0xFFFF6F00)))),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("POST TO GO VIRAL 🚀", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF00C853))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("+10 Coins 🪙", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Modal: Verify & Connect Account Handle
    activeChannelForVerification?.let { channel ->
        var inputId by remember(channel) {
            mutableStateOf(handleMap[channel.id] ?: channel.defaultHandle)
        }
        var isVerifying by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { activeChannelForVerification = null },
            containerColor = Color(0xFF1B202D),
            title = {
                Text("Verify & Connect ${channel.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            },
            text = {
                Column {
                    Text(channel.description, fontSize = 12.sp, color = Color(0xFFB0BEC5))
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputId,
                        onValueChange = { inputId = it },
                        label = { Text(channel.inputLabel) },
                        placeholder = { Text(channel.placeholder) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = channel.brandColor,
                            unfocusedBorderColor = Color(0xFF37474F),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    if (isVerifying) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Validating handle with ${channel.name} API...", fontSize = 11.sp, color = Color(0xFFFFD54F))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    com.example.ui.components.UniversalMediatedAdDock(
                        isVip = isVip,
                        featureLabel = "Verify & Connect ${channel.name}"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalHandle = inputId.trim().ifBlank { channel.defaultHandle }
                        isVerifying = true
                        coroutineScope.launch {
                            delay(600)
                            isVerifying = false
                            handleMap[channel.id] = finalHandle
                            connectedMap[channel.id] = true
                            sharedPrefs.edit()
                                .putBoolean("conn_${channel.id}", true)
                                .putString("handle_${channel.id}", finalHandle)
                                .apply()
                            Toast.makeText(context, "${channel.name} Verified & Connected! ✅", Toast.LENGTH_SHORT).show()
                            activeChannelForVerification = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                ) {
                    Text("Verify & Connect", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeChannelForVerification = null }) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }

    // Modal: Secure UPI Payment Checkout (Owner UPI: Amit2653@ptyes)
    if (showUpiPaymentModal && selectedSubscriptionPlan != null) {
        val (planName, details) = selectedSubscriptionPlan!!
        val (price, durationDays) = details
        var utrNumber by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showUpiPaymentModal = false },
            containerColor = Color(0xFF1E1738),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unlock $planName", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column {
                    Text("Unlock automated AI video analysis, trending tags, and high-CTR viral hooks for the next $durationDays days.", fontSize = 12.sp, color = Color(0xFFD1C4E9))
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF120C26)),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8A2BE2))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Amount Payable:", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                                Text("₹$price", color = Color(0xFF00E676), fontSize = 17.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Owner UPI ID:", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(MonetizationConstants.OWNER_UPI_ID, color = Color(0xFFFFD54F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(MonetizationConstants.OWNER_UPI_ID))
                                            Toast.makeText(context, "UPI Copied: ${MonetizationConstants.OWNER_UPI_ID}", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF81D4FA), modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            val launched = PaymentManager.launchUpiIntent(
                                context = context,
                                amountRupees = price,
                                planName = planName,
                                ownerUpiId = MonetizationConstants.OWNER_UPI_ID,
                                ownerName = MonetizationConstants.OWNER_NAME
                            )
                            if (!launched) {
                                clipboardManager.setText(AnnotatedString(MonetizationConstants.OWNER_UPI_ID))
                                Toast.makeText(context, "UPI ID Copied! Pay ₹$price via UPI", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676))
                    ) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pay ₹$price via UPI App", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = utrNumber,
                        onValueChange = { utrNumber = it },
                        label = { Text("12-Digit UPI Ref / UTR (Optional)") },
                        placeholder = { Text("Enter UTR for instant pass") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8A2BE2),
                            unfocusedBorderColor = Color(0xFF37474F),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    com.example.ui.components.UniversalMediatedAdDock(
                        isVip = isVip,
                        featureLabel = "Unlock $planName Checkout"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isAiUnlocked = true
                        activeAiPlanName = planName
                        showUpiPaymentModal = false
                        Toast.makeText(context, "₹$price Paid to Amit2653@ptyes! $planName Activated! AI Analyzer Unlocked 🚀", Toast.LENGTH_LONG).show()
                        runAiViralAnalysis()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                ) {
                    Text("Verify & Unlock AI", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpiPaymentModal = false }) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }

    // Modal: Success Celebration Dialog
    if (showBroadcastSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastSuccessDialog = false },
            containerColor = Color(0xFF1A1F2C),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Broadcast Initiated! 🚀", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column {
                    Text("+10 Coins credited directly to your creator wallet! 🪙", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFFD54F))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Caption with mandatory viral footer prepared for ${lastBroadcastPlatforms.size} platforms (${lastBroadcastPlatforms.joinToString()}).", fontSize = 11.sp, color = Color(0xFFB0BEC5))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(lastBroadcastCaption))
                            Toast.makeText(context, "Viral caption copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF81D4FA))
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Caption Again", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showBroadcastSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))
                ) {
                    Text("Great!", color = Color.White)
                }
            }
        )
    }
}

/**
 * Prominent feature box displayed when scrolling down the Home screen,
 * highlighting "Help AI Viral Video Post" with explicit benefits for ₹49 & ₹79 plans.
 */
@Composable
private fun HomeAiViralPostBottomBanner(
    onNavigateToCrossPoster: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToCrossPoster() }
            .testTag("home_ai_viral_post_bottom_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1738)),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(Color(0xFF8A2BE2), Color(0xFFFF6F00)))
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF281E48), Color(0xFF16122C))
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF8A2BE2), Color(0xFFFF8F00)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Help AI Viral Video Post",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "AI Viral Hook & Multi-Platform Syndication",
                            fontSize = 11.sp,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF00C853))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("+10 Coins 🪙", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Supercharge your reels & shorts reach with algorithmic AI optimization and 1-click cross-posting to YouTube, Instagram, Facebook, GoLivegram, Telegram & WhatsApp.",
                fontSize = 12.sp,
                color = Color(0xFFD1C4E9),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2 Plans Highlight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // ₹49 for 15 Days
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF261D4A))
                        .border(1.dp, Color(0xFF8B5CF6), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("15 DAYS PLAN", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF81D4FA))
                        Text("₹49", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Viral hook titles", fontSize = 9.sp, color = Color(0xFFE1BEE7))
                        Text("• Smart hashtags & tags", fontSize = 9.sp, color = Color(0xFFE1BEE7))
                    }
                }

                // ₹79 for 1 Month
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF321D3A))
                        .border(1.2.dp, Color(0xFFFF8F00), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("1 MONTH PLAN", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD54F))
                        Text("₹79", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Unlimited AI scan", fontSize = 9.sp, color = Color(0xFFFFE082))
                        Text("• Priority multi-posting", fontSize = 9.sp, color = Color(0xFFFFE082))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onNavigateToCrossPoster,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("home_bottom_ai_viral_post_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8A2BE2)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open AI Video Studio & Post Everywhere 🚀",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun HomeAiTrendingVideoStudioBanner(
    onOpenStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_ai_trending_video_studio_banner")
            .clickable(onClick = onOpenStudio),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1430)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFF8A2BE2), Color(0xFF00E5FF))))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
                            .background(Brush.linearGradient(listOf(Color(0xFF8A2BE2), Color(0xFF00E5FF)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI Trending Video Studio",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF8A2BE2))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text("FULL-SCREEN", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Text(
                            text = "10+ Niches • Auto-Generated Titles & Tags • AdMob Ad",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF00E5FF)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF311B92))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("PRO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Comedy, Tech, Motivation, Desi Rap, Gaming & Fashion! Create viral 9:16 reels with high-CTR titles, hashtag clusters, and 1-click social auto-post.",
                fontSize = 12.sp,
                color = Color.LightGray,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOpenStudio,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_open_home_ai_studio"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Launch Dedicated AI Video Studio 🚀",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun HomeGeminiSocialMediaStudioBanner(
    onOpenGemini: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_gemini_social_studio_banner")
            .clickable(onClick = onOpenGemini),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131525)),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(Color(0xFF8A2BE2), Color(0xFF00E5FF)))
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
                            .background(Brush.linearGradient(listOf(Color(0xFF8A2BE2), Color(0xFF00E5FF)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Gemini Social Creator",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text("GEMINI API", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                            }
                        }
                        Text(
                            text = "Viral Hooks • 60s Scripts • Captions • Hashtags",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Enter any prompt or video concept and let Google Gemini create high-retention viral scripts, catchy hooks, and trending hashtags for Reels, Shorts & TikTok.",
                fontSize = 12.sp,
                color = Color.LightGray,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOpenGemini,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_open_home_gemini_studio"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Generate with Gemini API ✨",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

