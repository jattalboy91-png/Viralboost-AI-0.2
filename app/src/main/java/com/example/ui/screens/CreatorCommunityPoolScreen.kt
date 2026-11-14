package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.monetization.AdManager
import com.example.monetization.MonetizationConstants
import com.example.ui.components.BannerAdView
import com.example.ui.components.DiamondParticleExplosionHost
import com.example.ui.components.DiamondRewardParticleController
import com.example.ui.components.NativeAdCard
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.delay

private val NeonPink = Color(0xFFEC4899)
private val PurplePrimary = BrandPurple

data class CreatorPoolMember(
    val id: String,
    val handle: String,
    val displayName: String,
    val platform: String,
    val followersCount: String,
    val niche: String,
    val profileUrl: String,
    val isSpotlight: Boolean = false,
    val isFollowed: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorCommunityPoolScreen(
    isVip: Boolean = false,
    userDiamonds: Int = 0,
    userCoins: Int = 0,
    userName: String = "Creator",
    bannerAdUnitId: String = MonetizationConstants.ADMOB_BANNER_UNIT_ID,
    interstitialAdUnitId: String = MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID,
    rewardedAdUnitId: String = MonetizationConstants.ADMOB_REWARDED_UNIT_ID,
    nativeAdUnitId: String = MonetizationConstants.ADMOB_NATIVE_UNIT_ID,
    onNavigateBack: () -> Unit,
    onUpgradeToVip: () -> Unit = {},
    onRedeemDiamondsForVip: () -> Unit = {},
    onRewardEarned: (diamonds: Int, coins: Int) -> Unit = { _, _ -> }
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val activity = context as? Activity

    // Preload AdMob Interstitial & Rewarded ads
    LaunchedEffect(Unit) {
        if (!isVip) {
            AdManager.loadInterstitial(context, interstitialAdUnitId)
            AdManager.loadRewarded(context, rewardedAdUnitId)
        }
    }

    // AdMob Modal states
    var showInterstitialAdModal by remember { mutableStateOf(false) }
    var interstitialTriggerReason by remember { mutableStateOf("") }
    var pendingActionAfterAd by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showRewardedAdModal by remember { mutableStateOf(false) }
    var lastScrollAdTriggerMs by remember { mutableLongStateOf(0L) }

    // Helper: Trigger AdMob Interstitial Ad on any click, option change, or scroll movement
    val triggerMovementWithInterstitialAd: (String, () -> Unit) -> Unit = { reason, action ->
        if (!isVip) {
            interstitialTriggerReason = reason
            AdManager.triggerMilestoneInterstitial(
                context = context,
                isVip = false,
                milestoneName = "Creator Pool: $reason",
                interstitialUnitId = interstitialAdUnitId,
                onShowInterstitialFallback = {
                    pendingActionAfterAd = action
                    showInterstitialAdModal = true
                },
                onComplete = {
                    action()
                }
            )
        } else {
            action()
        }
    }

    // Helper: Trigger AdMob Rewarded Video Ad
    val triggerRewardedVideoAd: () -> Unit = {
        if (activity != null) {
            AdManager.showRewardedAd(
                activity = activity,
                rewardedUnitId = rewardedAdUnitId,
                onUserEarnedReward = {
                    onRewardEarned(5, 15)
                },
                onFallbackSimulation = {
                    showRewardedAdModal = true
                }
            )
        } else {
            showRewardedAdModal = true
        }
    }

    // Pool state & interactive data
    var selectedPlatformFilter by remember { mutableStateOf("All") }
    val platformOptions = listOf("All", "YouTube", "Instagram", "GoLiveGram", "Facebook")

    var userChannelInput by remember { mutableStateOf("") }
    var selectedSubmitPlatform by remember { mutableStateOf("YouTube") }
    var selectedBoostGoal by remember { mutableStateOf("+100 Followers Fast") }
    var poolStatusBanner by remember { mutableStateOf<String?>(null) }
    var isUserSpotlighted by remember { mutableStateOf(isVip) }
    var gainedFollowersToday by remember { mutableIntStateOf(28) }
    var followedCreatorsCount by remember { mutableIntStateOf(3) }

    var creatorsList by remember {
        mutableStateOf(
            listOf(
                CreatorPoolMember(
                    id = "c1",
                    handle = "@rahul_vines",
                    displayName = "Rahul Vines Official",
                    platform = "YouTube",
                    followersCount = "45.2K Subscribers",
                    niche = "Desi Comedy & Shorts",
                    profileUrl = "https://www.youtube.com",
                    isSpotlight = true,
                    isFollowed = false
                ),
                CreatorPoolMember(
                    id = "c2",
                    handle = "@desi_priya",
                    displayName = "Priya Lifestyle & Reels",
                    platform = "Instagram",
                    followersCount = "120.8K Followers",
                    niche = "Trending Dance & Fashion",
                    profileUrl = "https://www.instagram.com",
                    isSpotlight = true,
                    isFollowed = false
                ),
                CreatorPoolMember(
                    id = "c3",
                    handle = "@vikas_shorts",
                    displayName = "Vikas Viral Shorts",
                    platform = "GoLiveGram",
                    followersCount = "88.4K Followers",
                    niche = "Tech Hacks & AI Reels",
                    profileUrl = "https://www.youtube.com",
                    isSpotlight = false,
                    isFollowed = false
                ),
                CreatorPoolMember(
                    id = "c4",
                    handle = "@haryanvi_swag_king",
                    displayName = "Ankit Haryanvi Rap",
                    platform = "YouTube",
                    followersCount = "64.1K Subscribers",
                    niche = "Music & Attitude Status",
                    profileUrl = "https://www.youtube.com",
                    isSpotlight = false,
                    isFollowed = false
                ),
                CreatorPoolMember(
                    id = "c5",
                    handle = "@neha_fitness_reels",
                    displayName = "Neha Daily Fitness",
                    platform = "Instagram",
                    followersCount = "93.5K Followers",
                    niche = "Home Workout & Diet",
                    profileUrl = "https://www.instagram.com",
                    isSpotlight = false,
                    isFollowed = false
                ),
                CreatorPoolMember(
                    id = "c6",
                    handle = "@rohit_gaming_live",
                    displayName = "Rohit BGMI & FreeFire",
                    platform = "Facebook",
                    followersCount = "150.2K Followers",
                    niche = "Gaming Shorts & Clips",
                    profileUrl = "https://www.facebook.com",
                    isSpotlight = false,
                    isFollowed = false
                ),
                CreatorPoolMember(
                    id = "c7",
                    handle = "@ai_motivation_guru",
                    displayName = "Sandeep Motivation AI",
                    platform = "GoLiveGram",
                    followersCount = "52.9K Followers",
                    niche = "Success & Business Quotes",
                    profileUrl = "https://www.instagram.com",
                    isSpotlight = false,
                    isFollowed = false
                )
            )
        )
    }

    val filteredCreators = remember(creatorsList, selectedPlatformFilter) {
        if (selectedPlatformFilter == "All") {
            creatorsList
        } else {
            creatorsList.filter { it.platform.equals(selectedPlatformFilter, ignoreCase = true) }
        }
    }

    // Detect scroll movement in the pool to trigger AdMob Interstitial Ad
    val listState = rememberLazyListState()
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress && !isVip && listState.firstVisibleItemIndex >= 1) {
            val now = System.currentTimeMillis()
            if (now - lastScrollAdTriggerMs > 18000L && !showInterstitialAdModal && !showRewardedAdModal) {
                lastScrollAdTriggerMs = now
                interstitialTriggerReason = "Creator Community Pool Scroll Movement"
                AdManager.triggerMilestoneInterstitial(
                    context = context,
                    isVip = false,
                    milestoneName = "Creator Pool Scroll Movement",
                    interstitialUnitId = interstitialAdUnitId,
                    onShowInterstitialFallback = {
                        pendingActionAfterAd = null
                        showInterstitialAdModal = true
                    },
                    onComplete = {}
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("creator_community_pool_screen"),
        containerColor = Color(0xFF070B19),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🤝 Creator Community Pool",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFF00C853).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFF00E676))
                            ) {
                                Text(
                                    text = "LIVE F4F",
                                    color = Color(0xFF4ADE80),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Follow-for-Follow • 100% Real Creator Growth",
                            fontSize = 11.sp,
                            color = NeonCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("creator_pool_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (!isVip) {
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.16f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, GoldAccent),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { onUpgradeToVip() }
                                .testTag("creator_pool_top_adfree_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = "Go Ad-Free",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AD-FREE",
                                    color = GoldAccent,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        },
        bottomBar = {
            if (!isVip) {
                Surface(
                    color = Color(0xFF090D16),
                    tonalElevation = 10.dp,
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creator_pool_bottom_ad_container")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        // Ad-Free strip right above the Banner Ad
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF1E1B4B), Color(0xFF311042))
                                    )
                                )
                                .clickable { onUpgradeToVip() }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Creator Pool से सभी Ads हटाएं (100% Ad-Free VIP)",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "REMOVE ADS →",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Persistent Bottom Mediated Rewarded Ad Trigger (BP_Rewarded_Android / AdMob) + Mediated Banner Ad (BP_Banner_Android / AdMob)
                        com.example.ui.components.UniversalMediatedAdDock(
                            isVip = isVip,
                            admobBannerUnitId = bannerAdUnitId,
                            admobRewardedUnitId = rewardedAdUnitId,
                            featureLabel = "Creator Community Follow Pool",
                            onRewardEarned = { diamonds, coins ->
                                onRewardEarned(diamonds, coins)
                            },
                            onUpgradeClick = onUpgradeToVip,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("creator_pool_banner_ad")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
        ) {
            // 1. Live Follow-for-Follow Growth Stats Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creator_pool_hero_stats_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111C38)),
                    border = BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(NeonCyan, PurplePrimary, NeonPink))
                    )
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
                                            Brush.linearGradient(
                                                listOf(Color(0xFF0284C7), NeonCyan)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Follow-for-Follow Growth Pool",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "यूट्यूब, इंस्टाग्राम और गोलाइवग्राम पर 10x फॉलोअर्स बढ़ाएं!",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3 Live Metrics Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PoolStatPill(
                                label = "Followers Gained",
                                value = "+$gainedFollowersToday",
                                subtext = "Today Live",
                                accentColor = Color(0xFF4ADE80),
                                modifier = Modifier.weight(1f)
                            )
                            PoolStatPill(
                                label = "You Followed",
                                value = "$followedCreatorsCount Creators",
                                subtext = "100% Followback",
                                accentColor = NeonCyan,
                                modifier = Modifier.weight(1f)
                            )
                            PoolStatPill(
                                label = "Pool Rank",
                                value = if (isUserSpotlighted) "#1 VIP 🔥" else "#14 Pool",
                                subtext = if (isUserSpotlighted) "Top Pinned" else "Boost Available",
                                accentColor = GoldAccent,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 2. Prominent Ad-Free VIP Description Card (for non-VIP users)
            if (!isVip) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("creator_pool_ad_free_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1230)),
                        border = BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(GoldAccent, NeonPink, NeonCyan))
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "🚫 100% Ad-Free Creator Pool + #1 Spotlight",
                                        color = GoldAccent,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                }
                                Surface(
                                    color = NeonPink.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "VIP OFFER",
                                        color = NeonPink,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Creator Community Pool में किसी भी बटन या क्रिएटर पर क्लिक करने पर आने वाले सभी Interstitial Ads, Video Ads और नीचे के Banner Ad से हमेशा के लिए छुटकारा पाएं! साथ ही अपना चैनल #1 VIP Spotlight पर पिन करें।",
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onUpgradeToVip,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldAccent,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("creator_pool_buy_vip_btn"),
                                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = "👑 Go Ad-Free (₹49)",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                }
                                OutlinedButton(
                                    onClick = onRedeemDiamondsForVip,
                                    border = BorderStroke(1.dp, NeonCyan),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("creator_pool_redeem_vip_btn"),
                                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = "💎 150 Diamonds = VIP",
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Submit / Add Your Channel to Follow-for-Follow Pool Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creator_pool_submit_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF0284C7))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "📢 अपना चैनल / प्रोफाइल पूल में जोड़ें (Add to Pool)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "अपना @handle या चैनल नाम डालें ताकि कम्युनिटी के हजारों क्रिएटर्स आपको तुरंत फॉलो करें:",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Platform selector chips for submitting user's channel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("YouTube", "Instagram", "GoLiveGram", "Facebook").forEach { plat ->
                                val isSelected = selectedSubmitPlatform == plat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        triggerMovementWithInterstitialAd("Select Channel Platform: $plat") {
                                            selectedSubmitPlatform = plat
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = plat,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF0284C7).copy(alpha = 0.3f),
                                        selectedLabelColor = NeonCyan,
                                        containerColor = Color(0xFF1E293B),
                                        labelColor = Color(0xFFCBD5E1)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = userChannelInput,
                            onValueChange = { userChannelInput = it },
                            placeholder = {
                                Text(
                                    text = "e.g., @${userName.lowercase().replace(" ", "_")}_official",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("creator_pool_channel_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Target Goal Selector Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("+100 Followers Fast", "+500 Subscribers", "+1K Viral Views").forEach { goal ->
                                val selected = selectedBoostGoal == goal
                                Surface(
                                    color = if (selected) PurplePrimary.copy(alpha = 0.28f) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (selected) NeonCyan else Color(0xFF334155)
                                    ),
                                    modifier = Modifier.clickable {
                                        triggerMovementWithInterstitialAd("Select Growth Goal: $goal") {
                                            selectedBoostGoal = goal
                                        }
                                    }
                                ) {
                                    Text(
                                        text = goal,
                                        color = if (selected) NeonCyan else Color(0xFFCBD5E1),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                triggerMovementWithInterstitialAd("Join & Boost My Followers") {
                                    val cleanHandle = userChannelInput.trim().let {
                                        if (it.isEmpty()) "@${userName.lowercase().replace(" ", "_")}_official"
                                        else if (it.startsWith("@")) it else "@$it"
                                    }
                                    val newEntry = CreatorPoolMember(
                                        id = "user_${System.currentTimeMillis()}",
                                        handle = cleanHandle,
                                        displayName = "$userName ($selectedBoostGoal)",
                                        platform = selectedSubmitPlatform,
                                        followersCount = "LIVE BOOSTING NOW",
                                        niche = "Pinned Creator • $selectedBoostGoal",
                                        profileUrl = "https://www.youtube.com",
                                        isSpotlight = true,
                                        isFollowed = true
                                    )
                                    creatorsList = listOf(newEntry) + creatorsList
                                    gainedFollowersToday += 15
                                    onRewardEarned(3, 10)
                                    DiamondRewardParticleController.triggerDiamondRewardBurst(
                                        diamondsEarned = 3,
                                        coinsEarned = 10,
                                        taskLabel = "Joined Creator Follow Pool!"
                                    )
                                    poolStatusBanner =
                                        "✅ You joined the Follow-for-Follow Pool! ($cleanHandle listed on $selectedSubmitPlatform • +3 💎 +10 🪙 Bonus Added!)"
                                    userChannelInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_join_and_boost_followers")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Join & Boost My Followers (+3 💎 +10 🪙)",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }

                        AnimatedVisibility(visible = poolStatusBanner != null) {
                            poolStatusBanner?.let { msg ->
                                Surface(
                                    color = Color(0xFF052E16),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFF22C55E)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                        .testTag("creator_pool_status_banner")
                                ) {
                                    Text(
                                        text = msg,
                                        color = Color(0xFF4ADE80),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. AdMob Rewarded Video Ad Card inside Creator Pool (Watch Ad -> #1 Spotlight + +5 Diamonds + 15 Coins)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creator_pool_rewarded_ad_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = BorderStroke(1.5.dp, GoldAccent)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.OndemandVideo,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "🎬 Watch Rewarded Ad • Pin Channel #1",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "वीडियो ऐड देखें और पाएं +5 💎 Diamonds, +15 🪙 Coins + 25 Auto-Followers!",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Surface(
                                color = GoldAccent.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "+5 💎 +15 🪙",
                                    color = GoldAccent,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { triggerRewardedVideoAd() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("creator_pool_watch_rewarded_ad_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleFilled,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Watch Rewarded Video Ad (+5 💎 + #1 Pool Boost)",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // 5. Platform Filter Tabs + 1-Click Auto-Follow All Button
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥 Active Community Creators (${filteredCreators.size})",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                        TextButton(
                            onClick = {
                                triggerMovementWithInterstitialAd("Auto-Follow All Creators in Pool") {
                                    val unfollowedCount = creatorsList.count { !it.isFollowed }
                                    if (unfollowedCount > 0) {
                                        creatorsList = creatorsList.map { it.copy(isFollowed = true) }
                                        followedCreatorsCount += unfollowedCount
                                        gainedFollowersToday += unfollowedCount * 6
                                        onRewardEarned(unfollowedCount * 2, unfollowedCount * 5)
                                        DiamondRewardParticleController.triggerDiamondRewardBurst(
                                            diamondsEarned = unfollowedCount * 2,
                                            coinsEarned = unfollowedCount * 5,
                                            taskLabel = "Followed All $unfollowedCount Creators!"
                                        )
                                        poolStatusBanner =
                                            "🚀 Followed all $unfollowedCount creators! Earned +${unfollowedCount * 2} 💎 & +${unfollowedCount * 5} 🪙 + Mutual Followbacks incoming!"
                                    } else {
                                        poolStatusBanner = "✅ You are already following all active creators in this pool!"
                                    }
                                }
                            },
                            modifier = Modifier.testTag("creator_pool_follow_all_btn")
                        ) {
                            Text(
                                text = "⚡ Follow All (+Bonus)",
                                color = NeonCyan,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        platformOptions.forEach { platform ->
                            val selected = selectedPlatformFilter == platform
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    triggerMovementWithInterstitialAd("Filter Pool by $platform") {
                                        selectedPlatformFilter = platform
                                    }
                                },
                                label = {
                                    Text(
                                        text = if (platform == "All") "🌐 All Platforms" else platform,
                                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonCyan.copy(alpha = 0.22f),
                                    selectedLabelColor = NeonCyan,
                                    containerColor = Color(0xFF1E293B),
                                    labelColor = Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.testTag("creator_pool_filter_${platform.lowercase()}")
                            )
                        }
                    }
                }
            }

            // 6. Active Creators List + In-Feed AdMob Native Ad
            itemsIndexed(filteredCreators, key = { _, item -> item.id }) { index, creator ->
                CreatorPoolItemCard(
                    creator = creator,
                    onFollowToggle = {
                        triggerMovementWithInterstitialAd("Follow Creator ${creator.handle}") {
                            if (!creator.isFollowed) {
                                creatorsList = creatorsList.map {
                                    if (it.id == creator.id) it.copy(isFollowed = true) else it
                                }
                                followedCreatorsCount += 1
                                gainedFollowersToday += 4
                                onRewardEarned(2, 5)
                                DiamondRewardParticleController.triggerDiamondRewardBurst(
                                    diamondsEarned = 2,
                                    coinsEarned = 5,
                                    taskLabel = "Followed ${creator.handle}!"
                                )
                                poolStatusBanner =
                                    "✅ Followed ${creator.handle}! Earned +2 💎 & +5 🪙 • Follow-back guaranteed within minutes!"
                            }
                        }
                    },
                    onOpenProfile = {
                        triggerMovementWithInterstitialAd("Open Channel ${creator.handle}") {
                            runCatching {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(creator.profileUrl))
                                context.startActivity(intent)
                            }
                        }
                    }
                )

                // Show an In-Feed AdMob Native Ad after the 2nd creator card for non-VIP users
                if (index == 1 && !isVip) {
                    Spacer(modifier = Modifier.height(12.dp))
                    NativeAdCard(
                        isVip = isVip,
                        adUnitId = nativeAdUnitId,
                        onAdClicked = onUpgradeToVip
                    )
                }
            }
        }
    }

    // Full-Screen AdMob Interstitial Ad Modal (triggered on any movement/click/scroll)
    if (showInterstitialAdModal && !isVip) {
        CreatorPoolInterstitialAdModal(
            triggerReason = interstitialTriggerReason,
            adUnitId = interstitialAdUnitId,
            onDismissAd = {
                showInterstitialAdModal = false
                val action = pendingActionAfterAd
                pendingActionAfterAd = null
                action?.invoke()
            },
            onUpgradeToVip = {
                showInterstitialAdModal = false
                pendingActionAfterAd = null
                onUpgradeToVip()
            }
        )
    }

    // Full-Screen AdMob Rewarded Video Ad Modal
    if (showRewardedAdModal) {
        CreatorPoolRewardedAdModal(
            adUnitId = rewardedAdUnitId,
            onRewardCompleted = {
                showRewardedAdModal = false
                isUserSpotlighted = true
                gainedFollowersToday += 25
                onRewardEarned(5, 15)
                DiamondRewardParticleController.triggerDiamondRewardBurst(
                    diamondsEarned = 5,
                    coinsEarned = 15,
                    taskLabel = "Creator Pool Rewarded Ad!"
                )
                poolStatusBanner =
                    "🎉 Rewarded Ad Complete! +5 💎 Diamonds, +15 🪙 Coins & #1 VIP Pool Spotlight Activated (+25 Followers)!"
            },
            onDismiss = {
                showRewardedAdModal = false
            },
            onUpgradeToVip = {
                showRewardedAdModal = false
                onUpgradeToVip()
            }
        )
    }

    // Compose Canvas Diamond Reward Particle Burst Overlay
    DiamondParticleExplosionHost()
}

@Composable
private fun PoolStatPill(
    label: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = accentColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                maxLines = 1
            )
            Text(
                text = subtext,
                color = Color(0xFFCBD5E1),
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CreatorPoolItemCard(
    creator: CreatorPoolMember,
    onFollowToggle: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val platformColor = when (creator.platform.lowercase()) {
        "youtube" -> Color(0xFFEF4444)
        "instagram" -> Color(0xFFEC4899)
        "golivegram" -> Color(0xFF06B6D4)
        else -> Color(0xFF3B82F6)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("creator_pool_item_${creator.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
        border = BorderStroke(
            1.dp,
            if (creator.isSpotlight) GoldAccent.copy(alpha = 0.7f) else Color(0xFF1E293B)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(platformColor.copy(alpha = 0.2f))
                            .border(1.5.dp, platformColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = creator.handle.removePrefix("@").take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = creator.handle,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            if (creator.isSpotlight) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = GoldAccent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "⭐ TOP SPOTLIGHT",
                                        color = GoldAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${creator.displayName} • ${creator.niche}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    color = platformColor.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, platformColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = creator.platform,
                        color = platformColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📊 ${creator.followersCount}",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "🤝 100% Instant Follow-Back Verified",
                        color = Color(0xFF4ADE80),
                        fontSize = 10.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onOpenProfile,
                        border = BorderStroke(1.dp, Color(0xFF475569)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Open",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onFollowToggle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (creator.isFollowed) Color(0xFF15803D) else Color(0xFF0284C7)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_follow_${creator.id}")
                    ) {
                        Text(
                            text = if (creator.isFollowed) "Followed ✓" else "Follow (+2💎 +5🪙)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorPoolInterstitialAdModal(
    triggerReason: String,
    adUnitId: String,
    onDismissAd: () -> Unit,
    onUpgradeToVip: () -> Unit
) {
    val decision by AdManager.lastInterstitialDecision.collectAsState()
    val isUnity = decision.network == com.example.monetization.AdNetwork.UNITY_ADS
    var countdown by remember { mutableIntStateOf(3) }
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
    }

    Dialog(
        onDismissRequest = { if (countdown == 0) onDismissAd() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = countdown == 0,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("creator_pool_interstitial_ad_modal"),
            color = Color(0xF5070B19)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, GoldAccent)
                    ) {
                        Text(
                            text = if (isUnity) {
                                "Unity Ads [${MonetizationConstants.UNITY_GAME_ID}] • ${MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID}"
                            } else {
                                "AdMob Interstitial Ad • $adUnitId"
                            },
                            color = GoldAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Button(
                        onClick = { if (countdown == 0) onDismissAd() },
                        enabled = countdown == 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (countdown == 0) Color(0xFF334155) else Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("creator_pool_close_interstitial_btn")
                    ) {
                        Text(
                            text = if (countdown > 0) "Skip in ${countdown}s" else "✕ Continue",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Center Ad Creative
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111C38)),
                    border = BorderStroke(
                        2.dp,
                        Brush.horizontalGradient(listOf(NeonCyan, NeonPink, GoldAccent))
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = NeonCyan.copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "SPONSORED: 10x Creator Growth Turbo",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Action: $triggerReason",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Creator Community Pool में बिना किसी रुकावट (No Ads) के अनलिमिटेड फॉलोअर्स बढ़ाने और अपना चैनल #1 पर पिन करने के लिए अभी VIP लें!",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onUpgradeToVip,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Remove All Ads Forever (VIP ₹49)",
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // Bottom info
                Text(
                    text = "Google AdMob Verified Interstitial • Upgrade to VIP for 100% Ad-Free Experience",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CreatorPoolRewardedAdModal(
    adUnitId: String,
    onRewardCompleted: () -> Unit,
    onDismiss: () -> Unit,
    onUpgradeToVip: () -> Unit
) {
    val decision by AdManager.lastRewardedDecision.collectAsState()
    val isUnity = decision.network == com.example.monetization.AdNetwork.UNITY_ADS
    var secondsLeft by remember { mutableIntStateOf(5) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
    }

    Dialog(
        onDismissRequest = { if (secondsLeft == 0) onRewardCompleted() else onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = secondsLeft == 0,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("creator_pool_rewarded_ad_modal"),
            color = Color(0xF5070B19)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, GoldAccent)
                    ) {
                        Text(
                            text = if (isUnity) {
                                "Unity Ads [${MonetizationConstants.UNITY_GAME_ID}] • ${MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID}"
                            } else {
                                "AdMob Rewarded Video • $adUnitId"
                            },
                            color = GoldAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = if (secondsLeft > 0) "Reward in ${secondsLeft}s..." else "✅ Reward Unlocked!",
                        color = if (secondsLeft == 0) Color(0xFF4ADE80) else Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF181436)),
                    border = BorderStroke(2.dp, GoldAccent)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.OndemandVideo,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "🎬 Creator Spotlight Video Ad",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "यह वीडियो पूरा देखने पर आपको तुरंत +5 💎 Diamonds, +15 🪙 Coins और पूल में #1 VIP Spotlight Boost मिलेगा!",
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { (5 - secondsLeft) / 5f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldAccent,
                            trackColor = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = onRewardCompleted,
                            enabled = secondsLeft == 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00C853),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("creator_pool_claim_reward_btn")
                        ) {
                            Text(
                                text = if (secondsLeft > 0) "Watching Ad ($secondsLeft s)..." else "🎁 Claim +5 💎 +15 🪙 & #1 Boost",
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                TextButton(onClick = onUpgradeToVip) {
                    Text(
                        text = "👑 Skip Ads Forever with VIP Subscription →",
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
