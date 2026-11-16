package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.collectAsState
import com.example.monetization.AdFormat
import com.example.monetization.AdNetwork
import com.example.ui.components.UniversalMediatedAdDock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.data.service.GeminiSocialContentService
import com.example.data.service.SocialContentResult
import com.example.monetization.AdManager
import com.example.monetization.MonetizationConstants
import com.example.ui.components.BannerAdView
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GeminiSocialMediaGeneratorScreen(
    onNavigateBack: () -> Unit,
    isVip: Boolean = false,
    userDiamonds: Int = 10,
    bannerAdUnitId: String = MonetizationConstants.ADMOB_BANNER_UNIT_ID,
    interstitialAdUnitId: String = MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID,
    rewardedAdUnitId: String = MonetizationConstants.ADMOB_REWARDED_UNIT_ID,
    onUpgradeToVip: () -> Unit = {},
    onRedeemDiamondsForVip: () -> Unit = {},
    onRewardEarned: (diamonds: Int, coins: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val geminiService = remember { GeminiSocialContentService() }

    // State
    var promptInput by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("Instagram Reels") }
    var selectedContentType by remember { mutableStateOf("Viral 60s Script") }
    var selectedTone by remember { mutableStateOf("High Energy & Viral") }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<SocialContentResult?>(null) }
    val generationHistory = remember { mutableStateListOf<SocialContentResult>() }

    var showApiKeyInfoDialog by remember { mutableStateOf(false) }
    var statusRewardMessage by remember { mutableStateOf<String?>(null) }

    // =========================================================================
    // 📺 INTERSTITIAL AD & REWARDED AD STATE FOR GENERATE & EVERY OPTION CLICK
    // =========================================================================
    var showOptionInterstitialAd by remember { mutableStateOf(false) }
    var activeAdTriggerReason by remember { mutableStateOf("Gemini Studio Option Selected") }
    var pendingOptionAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    var showStudioRewardedAd by remember { mutableStateOf(false) }
    var rewardedAdBonusUnlocked by remember { mutableStateOf(false) }

    BackHandler {
        if (showOptionInterstitialAd) {
            showOptionInterstitialAd = false
            val action = pendingOptionAction
            pendingOptionAction = null
            action?.invoke()
        } else if (showStudioRewardedAd) {
            showStudioRewardedAd = false
        } else {
            onNavigateBack()
        }
    }

    /**
     * Triggers an AdMob Interstitial Ad whenever the user clicks ANY option or the Generate button
     * (unless the user has an active Ad-Free VIP subscription).
     */
    val triggerOptionWithInterstitialAd: (reason: String, action: () -> Unit) -> Unit = { reason, action ->
        if (isVip) {
            action()
        } else {
            activeAdTriggerReason = reason
            AdManager.triggerMilestoneInterstitial(
                context = context,
                isVip = false,
                milestoneName = "Gemini Social Studio: $reason",
                interstitialUnitId = interstitialAdUnitId,
                onShowInterstitialFallback = {
                    pendingOptionAction = action
                    showOptionInterstitialAd = true
                },
                onComplete = {
                    action()
                }
            )
        }
    }

    val platforms = listOf("Instagram Reels", "YouTube Shorts", "TikTok", "X / Twitter", "LinkedIn")
    val contentTypes = listOf("Viral 60s Script", "Engaging Caption", "Pattern Hooks", "Complete Suite")
    val tones = listOf("High Energy & Viral", "Educational & Tips", "Funny & Relatable", "Curiosity Loop", "Authoritative")

    val samplePrompts = listOf(
        "5 productivity hacks that save 10 hours a week",
        "Stop making this 1 morning mistake for better energy",
        "How I gained 50k followers in 30 days without ads",
        "AI tools every content creator should use in 2026",
        "Why 99% of people quit their budget (and how to fix it)",
        "The psychological trick to double your sales conversion"
    )

    val apiKeyPresent = true

    val executeGeminiContentGeneration: () -> Unit = {
        focusManager.clearFocus()
        keyboardController?.hide()
        val effectivePrompt = promptInput.trim().ifBlank { "Top secrets to master social growth in 2026" }

        isGenerating = true
        coroutineScope.launch {
            val result = geminiService.generateSocialContent(
                prompt = effectivePrompt,
                platform = selectedPlatform,
                tone = selectedTone,
                contentType = selectedContentType
            )
            isGenerating = false
            generatedResult = result
            generationHistory.add(0, result)
            Toast.makeText(context, "✨ Content created with Gemini!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(BrandPurple, NeonCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Gemini Social Studio",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (apiKeyPresent) SuccessGreen.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f))
                                        .border(1.dp, if (apiKeyPresent) SuccessGreen else NeonCyan, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = if (apiKeyPresent) "Gemini Live" else "Gemini Ready",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (apiKeyPresent) SuccessGreen else NeonCyan
                                    )
                                }
                            }
                            Text(
                                text = "Powered by Google Gemini API",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("gemini_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Quick Ad-Free VIP Badge in TopBar
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                                )
                            )
                            .clickable { onUpgradeToVip() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("gemini_top_bar_ad_free_badge")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isVip) "VIP AD-FREE" else "GO AD-FREE",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                    }

                    IconButton(
                        onClick = { showApiKeyInfoDialog = true },
                        modifier = Modifier.testTag("gemini_api_info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "API Info",
                            tint = if (apiKeyPresent) SuccessGreen else GoldAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        bottomBar = {
            // Persistent Bottom Mediated Banner Ad (BP_Banner_Android / AdMob) + Rewarded Ad Trigger (BP_Rewarded_Android / AdMob) + Quick Ad-Free VIP Strip
            if (!isVip) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B091B))
                        .testTag("gemini_studio_bottom_banner_ad_container")
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
                    UniversalMediatedAdDock(
                        isVip = false,
                        admobBannerUnitId = bannerAdUnitId,
                        admobRewardedUnitId = rewardedAdUnitId,
                        featureLabel = "Gemini Social Studio",
                        onRewardEarned = onRewardEarned,
                        onUpgradeClick = onUpgradeToVip
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // =========================================================================
            // 0. PROMINENT AD-FREE SUBSCRIPTION DESCRIPTION & UPGRADE CARD
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
                            .testTag("gemini_ad_free_subscription_card")
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
                                            text = "🚫 Ads से छुटकारा पाएं — Ad-Free VIP लें!",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFFFD700)
                                        )
                                        Text(
                                            text = "Remove Interstitial, Rewarded & Banner Ads",
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
                                        text = "AD-FREE",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "📌 साफ-साफ जानकारी: फ्री प्लान में Gemini Social Studio के किसी भी ऑप्शन (Platform, Format, Tone, Quick Ideas) या 'Generate' बटन पर क्लिक करने पर AdMob Interstitial Ad, Rewarded Ad और Banner Ad आते हैं। बिना किसी विज्ञापन के सुपरफास्ट AI कंटेंट बनाने के लिए अभी VIP सब्सक्रिप्शन लें!",
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
                                        .testTag("btn_gemini_upgrade_ad_free_vip")
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
                                    onClick = onRedeemDiamondsForVip,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.2.dp, Color(0xFF00E5FF)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("btn_gemini_redeem_diamonds_vip")
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

            // Hero Intro Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BrandPurple, NeonCyan)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AI Social Media Content Engine",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Generate zero-drop viral hooks, retention-engineered scripts, engaging captions, and trending hashtags in seconds using Gemini AI.",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // =========================================================================
            // 🎁 REWARDED VIDEO AD CARD (रिवॉर्ड एड देखें और कमाएं +5 💎 डायमंड्स)
            // =========================================================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF102027)),
                    border = BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFFFD700)))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gemini_rewarded_ad_card")
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
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E5FF).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircleFilled,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "🎁 Rewarded Video Ad (रिवॉर्ड एड देखें)",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "वीडियो विज्ञापन देखें और पाएं +5 💎 डायमंड्स + 15 🪙 कॉइन्स और बोनस AI हुक्स! (Balance: $userDiamonds 💎)",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB2EBF2),
                                        lineHeight = 15.sp
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
                                onClick = {
                                    if (activity != null) {
                                        AdManager.showRewardedAd(
                                            activity = activity,
                                            rewardedUnitId = rewardedAdUnitId,
                                            onUserEarnedReward = {
                                                rewardedAdBonusUnlocked = true
                                                onRewardEarned(5, 15)
                                                statusRewardMessage = "🎉 बधाई हो! Rewarded Ad देखने पर आपको +5 💎 डायमंड्स और +15 🪙 कॉइन्स मिले!"
                                            },
                                            onFallbackSimulation = {
                                                showStudioRewardedAd = true
                                            }
                                        )
                                    } else {
                                        showStudioRewardedAd = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B8D4)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("btn_watch_gemini_rewarded_ad")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OndemandVideo,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (rewardedAdBonusUnlocked) "Watch Rewarded Ad Again (+5 💎)" else "Watch Rewarded Ad (+5 💎)",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            }

                            if (!isVip) {
                                OutlinedButton(
                                    onClick = onUpgradeToVip,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFD700)),
                                    modifier = Modifier.height(42.dp)
                                ) {
                                    Text(
                                        text = "Skip Ads (VIP)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD700)
                                    )
                                }
                            }
                        }

                        statusRewardMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = msg,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }

            // Platform Selector Chips (Clicking any platform triggers Interstitial Ad)
            item {
                Column {
                    Text(
                        text = "1. Select Target Platform",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(platforms) { platform ->
                            val isSelected = selectedPlatform == platform
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BrandPurple else DarkSurfaceElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonCyan else DarkSurfaceBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        selectedPlatform = platform
                                        triggerOptionWithInterstitialAd("Target Platform: $platform") {
                                            selectedPlatform = platform
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = platform,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            // Content Format Chips (Clicking any format triggers Interstitial Ad)
            item {
                Column {
                    Text(
                        text = "2. Content Format & Goal",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(contentTypes) { type ->
                            val isSelected = selectedContentType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BrandPurple else DarkSurfaceElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonCyan else DarkSurfaceBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        selectedContentType = type
                                        triggerOptionWithInterstitialAd("Content Format: $type") {
                                            selectedContentType = type
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            // Tone Selector Chips (Clicking any tone triggers Interstitial Ad)
            item {
                Column {
                    Text(
                        text = "3. Tone of Voice",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tones) { tone ->
                            val isSelected = selectedTone == tone
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BrandPurple else DarkSurfaceElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonCyan else DarkSurfaceBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        selectedTone = tone
                                        triggerOptionWithInterstitialAd("Tone of Voice: $tone") {
                                            selectedTone = tone
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = tone,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            // Text Prompt Input Field & Generate Button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkSurfaceBorder, DarkSurfaceBorder)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Topic, Concept or Video Idea",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (promptInput.isNotBlank()) {
                                Text(
                                    text = "${promptInput.length} chars",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = {
                                Text(
                                    text = "E.g. 5 productivity habits that save 10 hours a week for busy professionals...",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("prompt_input_field"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPurple,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = NeonCyan
                            ),
                            trailingIcon = {
                                if (promptInput.isNotBlank()) {
                                    IconButton(
                                        onClick = { promptInput = "" },
                                        modifier = Modifier.testTag("clear_prompt_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Inspiration Chips (Clicking any Quick Idea fills prompt & triggers Interstitial Ad)
                        Text(
                            text = "💡 Quick Ideas (Tap to fill):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            samplePrompts.forEach { prompt ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurfaceElevated)
                                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            promptInput = prompt
                                            triggerOptionWithInterstitialAd("Quick Idea Selected") {
                                                promptInput = prompt
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = prompt,
                                        fontSize = 10.sp,
                                        color = Color.LightGray,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 'Generate' Button (Triggers AdMob Interstitial Ad + Generates Content)
                        Button(
                            onClick = {
                                executeGeminiContentGeneration()
                                triggerOptionWithInterstitialAd("Generate Social Media Content ($selectedPlatform)") {}
                            },
                            enabled = !isGenerating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("generate_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandPurple
                            )
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Interfacing with Gemini API...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Generate Social Media Content",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Generating Progress Feedback Indicator
            if (isGenerating) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BrandPurple, NeonCyan)))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = NeonCyan,
                                trackColor = DarkSurfaceBorder
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Synthesizing viral algorithm hooks, caption, and hashtags for $selectedPlatform...",
                                fontSize = 12.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Generated Output Presentation Card
            generatedResult?.let { result ->
                item {
                    GeneratedContentDisplayCard(
                        result = result,
                        platform = selectedPlatform,
                        contentType = selectedContentType,
                        onCopy = { text, label ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Gemini Content", text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "✅ $label copied to clipboard!", Toast.LENGTH_SHORT).show()
                            triggerOptionWithInterstitialAd("Copy $label") {}
                        },
                        onShare = { text ->
                            triggerOptionWithInterstitialAd("Share Social Post ($selectedPlatform)") {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Social Media Post ($selectedPlatform)")
                                    putExtra(Intent.EXTRA_TEXT, text)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                            }
                        },
                        onRegenerate = {
                            executeGeminiContentGeneration()
                            triggerOptionWithInterstitialAd("Regenerate Social Media Content") {}
                        }
                    )
                }
            }

            // Previous Generations History
            if (generationHistory.size > 1) {
                item {
                    Column {
                        Text(
                            text = "Recent Creations (${generationHistory.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                items(generationHistory.drop(1).take(4)) { pastItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                generatedResult = pastItem
                                triggerOptionWithInterstitialAd("Load Past Creation") {
                                    generatedResult = pastItem
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = pastItem.hook.take(40) + "...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Load",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = pastItem.bodyCaption.take(70) + "...",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // 📺 FULL-SCREEN ADMOB INTERSTITIAL AD MODAL ON GENERATE / OPTION CLICK
    // =========================================================================
    if (showOptionInterstitialAd) {
        GeminiStudioInterstitialAdModal(
            triggerReason = activeAdTriggerReason,
            onDismissAndContinue = {
                showOptionInterstitialAd = false
                val action = pendingOptionAction
                pendingOptionAction = null
                action?.invoke()
            },
            onUpgradeToVip = {
                showOptionInterstitialAd = false
                pendingOptionAction = null
                onUpgradeToVip()
            }
        )
    }

    // =========================================================================
    // 🎁 FULL-SCREEN ADMOB REWARDED VIDEO AD MODAL (+5 DIAMONDS & +15 COINS)
    // =========================================================================
    if (showStudioRewardedAd) {
        GeminiStudioRewardedAdModal(
            onDismiss = { showStudioRewardedAd = false },
            onRewardCompleted = {
                showStudioRewardedAd = false
                rewardedAdBonusUnlocked = true
                onRewardEarned(5, 15)
                statusRewardMessage = "🎉 बधाई हो! Rewarded Ad पूरा देखने पर आपको +5 💎 डायमंड्स और +15 🪙 कॉइन्स मिले!"
                Toast.makeText(
                    context,
                    "💎 +5 Diamonds & +15 Coins credited from Rewarded Ad!",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onUpgradeToVip = {
                showStudioRewardedAd = false
                onUpgradeToVip()
            }
        )
    }

    // Dialog for API Key Info
    if (showApiKeyInfoDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gemini API Setup", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "✅ Firebase AI Logic (Gemini 3.8 Flash) is provisioned and active with App Check attestation! Your requests interface directly with Google's Gemini models via Firebase.ai with no client-side API key required.",
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showApiKeyInfoDialog = false }) {
                    Text("Got It", color = NeonCyan)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
private fun GeminiStudioInterstitialAdModal(
    triggerReason: String,
    onDismissAndContinue: () -> Unit,
    onUpgradeToVip: () -> Unit
) {
    val mediationState by AdManager.mediationState.collectAsState()
    val lastDecision by AdManager.lastInterstitialDecision.collectAsState()
    val unityGameId = mediationState.unityGameId.ifBlank { AdManager.DEFAULT_UNITY_GAME_ID }
    val unityPlacementId = mediationState.unityInterstitialPlacementId.ifBlank { AdManager.DEFAULT_UNITY_INTERSTITIAL_PLACEMENT_ID }
    val isUnitySelected = lastDecision?.selectedNetwork == AdNetwork.UNITY_ADS

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
        label = "gemini_interstitial_progress"
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
                .testTag("gemini_studio_interstitial_ad_modal"),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isUnitySelected) Color(0xFF00E5FF) else Color(0xFFFFD700))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isUnitySelected) "Unity Ads [$unityGameId] • $unityPlacementId" else "AdMob Interstitial Ad",
                                    fontSize = 10.sp,
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
                                .testTag("btn_close_gemini_interstitial_ad")
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
                                Brush.linearGradient(listOf(BrandPurple, NeonCyan))
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
                        text = if (isUnitySelected) {
                            "📺 Unity Interstitial Ad ($unityPlacementId)"
                        } else {
                            "📺 Google AdMob Interstitial Ad"
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Triggered by: $triggerReason • Game ID: $unityGameId",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00E5FF),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Clear Ad-Free VIP Pitch inside the Interstitial Ad
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
                                text = "फ्री यूजर के लिए Gemini Social Studio में ऑप्शन या Generate पर क्लिक करने पर विज्ञापन (Ad) आता है। सभी ऐड्स को 100% बंद करने के लिए अभी VIP सब्सक्रिप्शन लें!",
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
                                    .testTag("btn_gemini_interstitial_upgrade_vip")
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
                            .testTag("btn_skip_gemini_interstitial_ad")
                    ) {
                        Text(
                            text = if (secondsLeft > 0) "Skip Ad & Continue (${secondsLeft}s) →" else "Continue to Gemini Studio →",
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
private fun GeminiStudioRewardedAdModal(
    onDismiss: () -> Unit,
    onRewardCompleted: () -> Unit,
    onUpgradeToVip: () -> Unit
) {
    val mediationState by AdManager.mediationState.collectAsState()
    val lastDecision by AdManager.lastRewardedDecision.collectAsState()
    val unityGameId = mediationState.unityGameId.ifBlank { AdManager.DEFAULT_UNITY_GAME_ID }
    val unityPlacementId = mediationState.unityRewardedPlacementId.ifBlank { AdManager.DEFAULT_UNITY_REWARDED_PLACEMENT_ID }
    val isUnitySelected = lastDecision?.selectedNetwork == AdNetwork.UNITY_ADS

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
        label = "gemini_rewarded_progress"
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
                .testTag("gemini_studio_rewarded_ad_modal"),
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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF00E5FF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isUnitySelected) "Unity Ads [$unityGameId] • $unityPlacementId" else "AdMob Rewarded Video Ad",
                                fontSize = 10.sp,
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
                        text = if (isUnitySelected) {
                            "🎁 Unity Rewarded Ad ($unityPlacementId) • +5 💎 +15 🪙"
                        } else {
                            "🎁 Watch & Earn +5 💎 Diamonds + 15 🪙 Coins"
                        },
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
                            .testTag("btn_claim_gemini_rewarded_ad")
                    ) {
                        Text(
                            text = if (secondsLeft > 0) "Claim +5 💎 Reward (${secondsLeft}s)" else "🎉 Claim +5 💎 Diamonds & Continue",
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

@Composable
fun GeneratedContentDisplayCard(
    result: SocialContentResult,
    platform: String,
    contentType: String,
    onCopy: (text: String, label: String) -> Unit,
    onShare: (text: String) -> Unit,
    onRegenerate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("generated_content_card"),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(NeonCyan, BrandPurple)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Platform & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (result.isRealApiCall) SuccessGreen else NeonCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (result.isRealApiCall) "Gemini Live Output" else "AI Generated Package",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BrandPurple.copy(alpha = 0.3f))
                        .border(1.dp, BrandPurple, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = platform,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }

            result.errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = msg,
                    fontSize = 10.sp,
                    color = GoldAccent
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 1: 3-Second Viral Hook
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎣 3-Sec Pattern Interrupt Hook",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        IconButton(
                            onClick = { onCopy(result.hook, "Hook") },
                            modifier = Modifier.size(24.dp).testTag("copy_hook_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Hook",
                                tint = Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result.hook,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION 2: Body / Script / Caption
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📝 Content Script / Caption",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        IconButton(
                            onClick = { onCopy(result.bodyCaption, "Caption") },
                            modifier = Modifier.size(24.dp).testTag("copy_caption_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Caption",
                                tint = Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result.bodyCaption,
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION 3: Call to Action (CTA)
            if (result.callToAction.isNotBlank()) {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "🎯 High-Converting Call to Action",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiamondCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.callToAction,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // SECTION 4: Hashtags
            if (result.hashtags.isNotEmpty()) {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#️⃣ Curated Viral Hashtags",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            IconButton(
                                onClick = { onCopy(result.hashtags.joinToString(" "), "Hashtags") },
                                modifier = Modifier.size(24.dp).testTag("copy_tags_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Tags",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = result.hashtags.joinToString(" "),
                            fontSize = 12.sp,
                            color = NeonCyan,
                            lineHeight = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // SECTION 5: Platform Strategy Tip
            if (result.platformTips.isNotBlank()) {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 Algorithm & Posting Strategy",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.platformTips,
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Action Buttons: Copy All, Share, Regenerate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Copy Complete Package Button
                Button(
                    onClick = {
                        val fullPackage = """
                            ${result.hook}
                            
                            ${result.bodyCaption}
                            
                            ${result.callToAction}
                            
                            ${result.hashtags.joinToString(" ")}
                        """.trimIndent()
                        onCopy(fullPackage, "Complete Post Package")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("copy_complete_package_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Share to Social Button
                Button(
                    onClick = {
                        val shareText = """
                            ${result.hook}
                            
                            ${result.bodyCaption}
                            
                            ${result.callToAction}
                            
                            ${result.hashtags.joinToString(" ")}
                        """.trimIndent()
                        onShare(shareText)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("share_post_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                // Regenerate Button
                OutlinedButton(
                    onClick = onRegenerate,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("regenerate_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Regenerate",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
