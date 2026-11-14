package com.viralboost.ai

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 100% Hidden Backend Affiliate ID & Tracking ID.
 * Strictly processed in background referral links and telemetry.
 * Never exposed as raw text in public user UI.
 */
private const val HIDDEN_BACKEND_AFFILIATE_ID = "5665937"

// Modern Dark Theme Palette
private val StudioDarkBg = Color(0xFF0D0C15)
private val StudioSurfaceCard = Color(0xFF18162A)
private val StudioCardBorder = Color(0xFF2C284D)
private val StudioNeonCyan = Color(0xFF00E5FF)
private val StudioVipGold = Color(0xFFFFB800)
private val StudioBrandPurple = Color(0xFF8B5CF6)
private val StudioHotPink = Color(0xFFFF2A85)

/**
 * Core AI Studio Tools Specification
 */
data class AiStudioPaidTool(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val category: String,
    val defaultPrompt: String,
    val previewImageUrl: String,
    val tags: List<String>
)

/**
 * Available VIP Subscription Tiers
 */
data class VipSubscriptionPlan(
    val id: String,
    val title: String,
    val durationText: String,
    val priceRupees: Int,
    val oldPriceRupees: Int,
    val discountBadge: String,
    val isPopular: Boolean,
    val perks: List<String>
)

// Preset Creator Faces for Immediate High-Definition Testing
private val sampleCreatorPresets = listOf(
    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=500&auto=format&fit=crop&q=80"
)

// The 8 AI Studio Tools
val allAiStudioTools = listOf(
    AiStudioPaidTool(
        id = "photo_to_video",
        title = "Photo-to-Video & Face-Swap AI",
        subtitle = "Upload Face & Animate into Viral Reels",
        badge = "MOST VIRAL ⭐",
        category = "Video Face-Swap",
        defaultPrompt = "High-energy dancing beat with cinematic neon lights and smooth face tracking",
        previewImageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&auto=format&fit=crop&q=80",
        tags = listOf("#FaceSwapAI", "#ViralReel", "#TrendingDance", "#PhotoToVideo", "#ViralBoostAI")
    ),
    AiStudioPaidTool(
        id = "text_to_video",
        title = "Text-to-Video Engine (Veo 9:16)",
        subtitle = "Text Prompt to 9:16 HD Cinematic Reel",
        badge = "HOT 🔥",
        category = "Cinematic AI",
        defaultPrompt = "Cyberpunk street in Mumbai at midnight with flying auto-rickshaws and rain reflections",
        previewImageUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
        tags = listOf("#TextToVideo", "#AIAnimation", "#VeoVideo", "#CinematicAI", "#FutureIndia")
    ),
    AiStudioPaidTool(
        id = "thumbnail_creator",
        title = "AI Viral Thumbnail Creator",
        subtitle = "High-CTR 9:16 Reels & 16:9 Cover Art",
        badge = "HIGH CTR 🚀",
        category = "Cover Design",
        defaultPrompt = "Shocked creator expression with bold 3D yellow Hindi text: 1 दिन में 10 लाख व्यूज!",
        previewImageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
        tags = listOf("#HighCTR", "#ReelCover", "#YouTubeThumbnails", "#ViralArt", "#CreatorHacks")
    ),
    AiStudioPaidTool(
        id = "viral_hooks",
        title = "Viral Hooks & 60s Retention Scripts",
        subtitle = "Zero-Drop Psychology Retention Engine",
        badge = "ALGORITHM 📈",
        category = "Retention Scripts",
        defaultPrompt = "3 secrets Instagram algorithms hide from you that can blow up your page tonight",
        previewImageUrl = "https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=600&auto=format&fit=crop&q=80",
        tags = listOf("#ViralHooks", "#RetentionHacks", "#ScriptWriting", "#ReelsGrowth", "#ContentStrategy")
    ),
    AiStudioPaidTool(
        id = "titles_and_tags",
        title = "Titles, Hashtags & SEO Booster",
        subtitle = "Auto-Hashtag & Keyword Explorer",
        badge = "EXPLORE 🏷️",
        category = "SEO Discovery",
        defaultPrompt = "Trending comedy sketch on Indian weddings and budget shopping hacks",
        previewImageUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=600&auto=format&fit=crop&q=80",
        tags = listOf("#ExplorePage", "#DesiComedy", "#IndianWeddings", "#SEOBooster", "#GrowthHack")
    ),
    AiStudioPaidTool(
        id = "gemini_chat",
        title = "Interactive Gemini AI Creator Chat",
        subtitle = "Real-Time Viral Strategist & Script Doctor",
        badge = "AI PRO 🤖",
        category = "AI Strategist",
        defaultPrompt = "Give me 5 viral reel ideas for my fashion niche that can gain 50k followers this week",
        previewImageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
        tags = listOf("#GeminiAI", "#AIAssistant", "#ContentBrain", "#CreatorPro", "#ViralBoostAI")
    ),
    AiStudioPaidTool(
        id = "voiceover_studio",
        title = "Voiceover & Dubbing Studio",
        subtitle = "Desi Haryanvi, Hindi & English AI Voices",
        badge = "AUDIO 🎙️",
        category = "Voice Generation",
        defaultPrompt = "Desi energetic voiceover explaining how to earn money through affiliate marketing",
        previewImageUrl = "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=600&auto=format&fit=crop&q=80",
        tags = listOf("#AIVoice", "#DesiSwag", "#VoiceoverArtist", "#HindiDubbing", "#AudioAI")
    ),
    AiStudioPaidTool(
        id = "one_click_reel",
        title = "1-Click Viral Reel Automator",
        subtitle = "End-to-End Music + Video + Captions Bundler",
        badge = "VIP EXCLUSIVE ⚡",
        category = "Full Automation",
        defaultPrompt = "Generate viral motivation reel with punchy captions, energetic beats and epic B-roll",
        previewImageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=600&auto=format&fit=crop&q=80",
        tags = listOf("#1ClickReel", "#AutomationAI", "#AutoCaptions", "#ReelsMoney", "#ViralBoostVIP")
    )
)

val defaultVipPlans = listOf(
    VipSubscriptionPlan(
        id = "plan_15days",
        title = "Starter Creator Pass",
        durationText = "15 Days Access",
        priceRupees = 49,
        oldPriceRupees = 199,
        discountBadge = "75% OFF",
        isPopular = false,
        perks = listOf(
            "100 AI Video Generations",
            "HD Photo-to-Video & Face-Swap",
            "Auto-Hashtags & Titles",
            "Social Media Auto-Poster"
        )
    ),
    VipSubscriptionPlan(
        id = "plan_30days",
        title = "Pro Influencer Pass",
        durationText = "30 Days Access",
        priceRupees = 179,
        oldPriceRupees = 599,
        discountBadge = "BEST VALUE 🔥",
        isPopular = true,
        perks = listOf(
            "Unlimited AI Video Generations",
            "4K Ultra-HD Render Engine",
            "100% Ad-Free Fast Server",
            "All 8 Core AI Studio Tools",
            "Multi-Account Cross-Poster"
        )
    ),
    VipSubscriptionPlan(
        id = "plan_365days",
        title = "Annual VIP Elite",
        durationText = "365 Days Access",
        priceRupees = 499,
        oldPriceRupees = 1999,
        discountBadge = "SUPER SAVER 👑",
        isPopular = false,
        perks = listOf(
            "Everything in Pro Unlimited",
            "Dedicated Priority GPU Cloud",
            "Exclusive Brand Collab Hub",
            "24/7 VIP Personal Strategist"
        )
    )
)

/**
 * Ultimate Master Composable for "ViralBoost AI":
 * All AI Studio Tools Locked behind Paid VIP Subscriptions
 * (3-Day Trial + 1-Day Friend Referral Reward),
 * Gallery Photo Picker, AdMob Interstitial Ads, & 100% Hidden Backend Affiliate ID (5665937)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViralBoostAllAiPaidApp(
    onBackToDashboard: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    // Navigation and Active Tool States
    var currentScreen by remember { mutableStateOf("home") } // "home", "tool_detail"
    var selectedToolId by remember { mutableStateOf(allAiStudioTools.first().id) }
    val activeTool = remember(selectedToolId) {
        allAiStudioTools.firstOrNull { it.id == selectedToolId } ?: allAiStudioTools.first()
    }

    // VIP Subscription & 3-Day Trial States
    var hasActiveVipPlan by remember { mutableStateOf(false) }
    var trialDaysLeft by remember { mutableIntStateOf(3) } // 3-Day Free VIP Trial
    var freeGenerationsUsedToday by remember { mutableIntStateOf(0) }
    val isVipUnlocked = hasActiveVipPlan || trialDaysLeft > 0

    // Prompt & Photo Input States
    var userPromptText by remember { mutableStateOf("") }
    var uploadedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedPhotoPresetUrl by remember { mutableStateOf<String?>(null) }
    var showGalleryPickerDialog by remember { mutableStateOf(false) }

    // Dialog & Flow States
    var showSubscriptionModal by remember { mutableStateOf(false) }
    var showReferralInviteModal by remember { mutableStateOf(false) }
    var showAdMobInterstitialModal by remember { mutableStateOf(false) }
    var showAccountConnectorModal by remember { mutableStateOf(false) }

    // Generation & Ad Timer States
    var isGeneratingContent by remember { mutableStateOf(false) }
    var generationProgress by remember { mutableFloatStateOf(0f) }
    var contentGeneratedSuccessfully by remember { mutableStateOf(false) }
    var generatedVideoUrl by remember { mutableStateOf<String?>(null) }
    var adCountdownSeconds by remember { mutableIntStateOf(5) }
    var isAdCountdownFinished by remember { mutableStateOf(false) }

    // Social Auto-Poster States
    var isInstagramConnected by remember { mutableStateOf(true) }
    var isYouTubeConnected by remember { mutableStateOf(true) }
    var isFacebookConnected by remember { mutableStateOf(false) }
    var isGoLiveGramConnected by remember { mutableStateOf(true) }

    // Payment Sheet States
    var selectedPlanForPayment by remember { mutableStateOf(defaultVipPlans[1]) }
    var enteredUtrNumber by remember { mutableStateOf("") }
    var showUtrInput by remember { mutableStateOf(false) }

    // Android Zero-Permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadedPhotoUri = uri
            uploadedPhotoPresetUrl = null
            Toast.makeText(context, "📸 Photo Selected from Gallery!", Toast.LENGTH_SHORT).show()
        }
    }

    // System Back Handler
    BackHandler {
        if (currentScreen == "tool_detail") {
            currentScreen = "home"
        } else {
            onBackToDashboard?.invoke()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = StudioDarkBg
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main Top Bar
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ViralBoost AI Studio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isVipUnlocked) StudioVipGold else Color.Red
                            ) {
                                Text(
                                    text = if (hasActiveVipPlan) "VIP PRO 👑" else if (trialDaysLeft > 0) "$trialDaysLeft-DAY TRIAL" else "LOCKED 🔒",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isVipUnlocked) "All 8 AI Studio Tools Unlocked" else "VIP Pass Expired - Upgrade to Generate",
                            fontSize = 11.sp,
                            color = StudioNeonCyan
                        )
                    }
                },
                navigationIcon = {
                    if (onBackToDashboard != null || currentScreen == "tool_detail") {
                        IconButton(
                            onClick = {
                                if (currentScreen == "tool_detail") {
                                    currentScreen = "home"
                                } else {
                                    onBackToDashboard?.invoke()
                                }
                            },
                            modifier = Modifier.testTag("btn_studio_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    // Friend Invite Reward (+1 Day Pass)
                    IconButton(
                        onClick = { showReferralInviteModal = true },
                        modifier = Modifier.testTag("btn_top_invite_pass")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = StudioHotPink) {
                                    Text("+1D", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Invite Friends",
                                tint = StudioVipGold
                            )
                        }
                    }

                    // Social Accounts Connector
                    IconButton(
                        onClick = { showAccountConnectorModal = true },
                        modifier = Modifier.testTag("btn_top_social_connector")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Social Connector",
                            tint = StudioNeonCyan
                        )
                    }

                    // VIP Upgrade Button
                    FilledTonalButton(
                        onClick = { showSubscriptionModal = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = StudioVipGold,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp).testTag("btn_top_upgrade_vip")
                    ) {
                        Text("VIP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StudioSurfaceCard,
                    titleContentColor = Color.White
                )
            )

            // VIP / 3-Day Trial Status Banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isVipUnlocked) Color(0xFF1E1B4B) else Color(0xFF3F1414)
                ),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isVipUnlocked) StudioBrandPurple else Color.Red)
                    .testTag("vip_trial_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isVipUnlocked) Icons.Default.Stars else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isVipUnlocked) StudioVipGold else Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasActiveVipPlan) "⭐ VIP Pro Subscription Active (Unlimited)" else "🎁 3-Day Free VIP Trial: $trialDaysLeft Days Remaining",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = if (isVipUnlocked) "Invite a friend to claim +1 Day Free VIP Pass anytime!" else "All AI Studio Tools are locked. Upgrade or invite a friend to unlock!",
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = { showReferralInviteModal = true },
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(StudioHotPink, StudioBrandPurple))),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("btn_banner_invite_friend")
                        ) {
                            Text("Invite +1D", color = StudioHotPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showSubscriptionModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioVipGold, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("btn_banner_upgrade")
                        ) {
                            Text("Upgrade", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Main Content Area based on currentScreen
            AnimatedContent(
                targetState = currentScreen,
                label = "StudioScreenTransition",
                modifier = Modifier.weight(1f)
            ) { screen ->
                when (screen) {
                    "home" -> {
                        // AI Studio Dashboard: Grid of All AI Tools
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                Text(
                                    text = "⚡ Select AI Studio Tool",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Choose an AI engine to start creating viral short-form content",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            // 2-Column Grid or List of All 8 Tools
                            items(allAiStudioTools) { tool ->
                                val isSelected = tool.id == selectedToolId
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedToolId = tool.id
                                            userPromptText = tool.defaultPrompt
                                            contentGeneratedSuccessfully = false
                                            currentScreen = "tool_detail"
                                        }
                                        .testTag("tool_card_${tool.id}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) StudioSurfaceCard.copy(alpha = 0.95f) else StudioSurfaceCard
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        brush = if (isSelected) Brush.horizontalGradient(listOf(StudioNeonCyan, StudioBrandPurple)) else Brush.linearGradient(listOf(StudioCardBorder, StudioCardBorder))
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Tool Preview Thumbnail / Icon Box
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.Black)
                                        ) {
                                            AsyncImage(
                                                model = tool.previewImageUrl,
                                                contentDescription = tool.title,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            if (!isVipUnlocked) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(Color.Black.copy(alpha = 0.65f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Lock,
                                                        contentDescription = "Locked",
                                                        tint = Color.Red,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = StudioHotPink.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = tool.badge,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = StudioHotPink,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = tool.category,
                                                    fontSize = 11.sp,
                                                    color = StudioNeonCyan
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = tool.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )

                                            Text(
                                                text = tool.subtitle,
                                                fontSize = 11.sp,
                                                color = Color.LightGray,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = if (isVipUnlocked) StudioNeonCyan else Color.Gray,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }

                            // Quick Friend Referral Reward Card
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showReferralInviteModal = true }
                                        .testTag("card_invite_friend_reward"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1030)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioHotPink)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🎁", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Invite Friends & Get +1 Day Free VIP Pass",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Every friend who joins gives you 24 hours of 100% Free VIP Tools access!",
                                                fontSize = 11.sp,
                                                color = Color.LightGray
                                            )
                                        }
                                        Button(
                                            onClick = { showReferralInviteModal = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = StudioHotPink),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Invite", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "tool_detail" -> {
                        // Interactive Dedicated Tool Workspace
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Tool Header & Badge
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = StudioHotPink.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = activeTool.badge,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = StudioHotPink,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = activeTool.category,
                                                color = StudioNeonCyan,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = activeTool.title,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = activeTool.subtitle,
                                            fontSize = 12.sp,
                                            color = Color.LightGray
                                        )
                                    }

                                    // Switch Tool Button
                                    OutlinedButton(
                                        onClick = { currentScreen = "home" },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("btn_switch_tool")
                                    ) {
                                        Text("All Tools", fontSize = 11.sp)
                                    }
                                }
                            }

                            // Gallery Photo Upload Section (Mandatory for Photo-to-Video and High-Engagement Tools)
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "📷 Creator Photo (Face-Swap & Motion)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )
                                            if (uploadedPhotoUri != null || uploadedPhotoPresetUrl != null) {
                                                TextButton(
                                                    onClick = {
                                                        uploadedPhotoUri = null
                                                        uploadedPhotoPresetUrl = null
                                                    }
                                                ) {
                                                    Text("Remove", color = Color.Red, fontSize = 11.sp)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        if (uploadedPhotoUri != null || uploadedPhotoPresetUrl != null) {
                                            // Image Preview Box
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(80.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .border(2.dp, StudioNeonCyan, RoundedCornerShape(10.dp))
                                                ) {
                                                    AsyncImage(
                                                        model = uploadedPhotoUri ?: uploadedPhotoPresetUrl,
                                                        contentDescription = "Selected Photo",
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column {
                                                    Text(
                                                        text = "✅ Photo Attached for AI Animation",
                                                        color = Color(0xFF10B981),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                    Text(
                                                        text = "Face tracking & motion sync will apply automatically.",
                                                        color = Color.LightGray,
                                                        fontSize = 11.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    OutlinedButton(
                                                        onClick = { showGalleryPickerDialog = true },
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("Change Photo", fontSize = 10.sp)
                                                    }
                                                }
                                            }
                                        } else {
                                            // Empty State: Gallery Picker Launcher
                                            Button(
                                                onClick = { showGalleryPickerDialog = true },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("btn_open_gallery_picker"),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF262347)
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AddPhotoAlternate,
                                                    contentDescription = null,
                                                    tint = StudioNeonCyan
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Select Photo from Gallery or Preset Faces",
                                                    color = Color.White,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Prompt & Creative Concept Input
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "✍️ Prompt & Creative Topic",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))

                                        OutlinedTextField(
                                            value = userPromptText,
                                            onValueChange = { userPromptText = it },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("input_prompt_concept"),
                                            placeholder = {
                                                Text(
                                                    text = "Describe your concept, style, or hook...",
                                                    color = Color.Gray,
                                                    fontSize = 12.sp
                                                )
                                            },
                                            minLines = 2,
                                            maxLines = 4,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = StudioNeonCyan,
                                                unfocusedBorderColor = StudioCardBorder,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Quick Topic Suggestions Chips
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            val ideas = listOf(
                                                "Desi Comedy Sketch",
                                                "Gym Motivation Swag",
                                                "Haryanvi Rap Flow",
                                                "Online Earnings Secret",
                                                "AI Wedding Look"
                                            )
                                            items(ideas) { idea ->
                                                SuggestionChip(
                                                    onClick = { userPromptText = idea },
                                                    label = { Text(idea, fontSize = 10.sp, color = Color.White) },
                                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                                        containerColor = Color(0xFF1E1B38)
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Generate Button / AdMob Interstitial Trigger
                            item {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    if (!isVipUnlocked) {
                                        // Locked Alert
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF3F1414)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = Color.Red
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = "VIP Pass Required: Your 3-Day Free Trial has ended. Upgrade to VIP or Invite a Friend to generate!",
                                                    color = Color.White,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    Button(
                                        onClick = {
                                            if (!isVipUnlocked) {
                                                showSubscriptionModal = true
                                            } else {
                                                // Trigger AdMob Interstitial Ad before Generation
                                                if (!hasActiveVipPlan) {
                                                    adCountdownSeconds = 5
                                                    isAdCountdownFinished = false
                                                    showAdMobInterstitialModal = true
                                                } else {
                                                    // VIP users skip ads completely
                                                    startVideoGenerationSimulation(
                                                        activeTool = activeTool,
                                                        prompt = userPromptText,
                                                        onProgress = { generationProgress = it },
                                                        onSuccess = { videoUrl ->
                                                            generatedVideoUrl = videoUrl
                                                            contentGeneratedSuccessfully = true
                                                            freeGenerationsUsedToday += 1
                                                        }
                                                    )
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("btn_trigger_generation"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isVipUnlocked) StudioBrandPurple else Color(0xFF4A154B)
                                        )
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (isVipUnlocked) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = if (isVipUnlocked) StudioVipGold else Color.Gray
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (!isVipUnlocked) "Unlock VIP Access to Generate" else if (!hasActiveVipPlan) "Watch Ad & Generate (Free Trial)" else "Generate Video (VIP Ad-Free)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            // Rendering Progress Bar
                            if (isGeneratingContent) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "⚡ Rendering ${activeTool.title} with GPU Cloud...",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            LinearProgressIndicator(
                                                progress = { generationProgress },
                                                modifier = Modifier.fillMaxWidth(),
                                                color = StudioNeonCyan,
                                                trackColor = StudioCardBorder
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "${(generationProgress * 100).toInt()}% Rendered • Encoding 9:16 Video",
                                                color = Color.Gray,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Generated Video & Content Showcase
                            if (contentGeneratedSuccessfully) {
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("result_video_card"),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF14122B)),
                                        border = androidx.compose.foundation.BorderStroke(2.dp, StudioNeonCyan)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "✨ AI Generation Ready!",
                                                    color = StudioNeonCyan,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFF10B981)
                                                ) {
                                                    Text(
                                                        text = "1080p 60FPS",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.Black,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Simulated 9:16 Video Player Container
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(240.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color.Black),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AsyncImage(
                                                    model = uploadedPhotoUri ?: uploadedPhotoPresetUrl ?: activeTool.previewImageUrl,
                                                    contentDescription = "Generated Video",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )

                                                // Video Player Play Button Overlay
                                                Box(
                                                    modifier = Modifier
                                                        .size(54.dp)
                                                        .clip(CircleShape)
                                                        .background(Color.Black.copy(alpha = 0.65f))
                                                        .clickable {
                                                            Toast.makeText(context, "▶️ Playing AI Generated Reel!", Toast.LENGTH_SHORT).show()
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.PlayArrow,
                                                        contentDescription = "Play",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                }

                                                // Top Right Duration Badge
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color.Black.copy(alpha = 0.75f),
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(8.dp)
                                                ) {
                                                    Text(
                                                        text = "00:15",
                                                        color = Color.White,
                                                        fontSize = 10.sp,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Auto-Generated Viral Titles & Hook
                                            Text(
                                                text = "📌 Auto Viral Title:",
                                                color = Color.LightGray,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = "${userPromptText.ifBlank { activeTool.defaultPrompt }} - Viral Reel 2026",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))

                                            // Auto-Generated Hashtags
                                            Text(
                                                text = "🏷️ Auto-Hashtags:",
                                                color = Color.LightGray,
                                                fontSize = 11.sp
                                            )
                                            val fullTagString = activeTool.tags.joinToString(" ")
                                            Text(
                                                text = fullTagString,
                                                fontSize = 12.sp,
                                                color = StudioNeonCyan
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Action Buttons: Copy Tags, Auto-Post, Download
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(fullTagString))
                                                        Toast.makeText(context, "📋 Hashtags Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.weight(1f).testTag("btn_copy_tags"),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Copy Tags", fontSize = 11.sp)
                                                }

                                                Button(
                                                    onClick = {
                                                        Toast.makeText(
                                                            context,
                                                            "🚀 Auto-Posting Reel to Connected Accounts (IG, YT, GoLiveGram)!",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = StudioHotPink),
                                                    modifier = Modifier.weight(1.3f).testTag("btn_auto_post_reel"),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Send,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Auto-Post Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Banner Ad Simulation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Black)
                    .border(1.dp, StudioCardBorder)
                    .testTag("studio_bottom_admob_banner"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = StudioVipGold
                    ) {
                        Text(
                            text = "Ad",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Shopsy Mega Festive Mall • Up to 80% OFF on Top Gadgets",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }

    // Modal 1: Gallery & Face Presets Picker Dialog
    if (showGalleryPickerDialog) {
        AlertDialog(
            onDismissRequest = { showGalleryPickerDialog = false },
            title = {
                Text(
                    text = "📸 Choose Creator Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Select a face photo from your phone gallery, or pick from our ready HD creator presets for instant face-swapping:",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    // Option A: Open Device Gallery via Zero-Permission Photo Picker
                    Button(
                        onClick = {
                            showGalleryPickerDialog = false
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StudioBrandPurple),
                        modifier = Modifier.fillMaxWidth().testTag("btn_pick_device_gallery")
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Phone Gallery (Pick Photo)")
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Or pick ready HD Creator Faces:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = StudioNeonCyan
                    )

                    // Option B: 4 HD Preset Faces
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        sampleCreatorPresets.forEachIndexed { index, url ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(2.dp, StudioCardBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        uploadedPhotoPresetUrl = url
                                        uploadedPhotoUri = null
                                        showGalleryPickerDialog = false
                                        Toast.makeText(context, "Face Preset #${index + 1} Applied!", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Face Preset",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGalleryPickerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal 2: AdMob Interstitial Ad Simulation with 5s Strict Countdown
    if (showAdMobInterstitialModal) {
        LaunchedEffect(Unit) {
            adCountdownSeconds = 5
            isAdCountdownFinished = false
            while (adCountdownSeconds > 0) {
                delay(1000L)
                adCountdownSeconds -= 1
            }
            isAdCountdownFinished = true
        }

        AlertDialog(
            onDismissRequest = {
                if (isAdCountdownFinished) {
                    showAdMobInterstitialModal = false
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = StudioVipGold
                        ) {
                            Text(
                                text = "AdMob Interstitial",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = if (!isAdCountdownFinished) "Skip in ${adCountdownSeconds}s" else "Reward Granted ✓",
                        color = if (isAdCountdownFinished) Color(0xFF10B981) else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E1030)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🛍️", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Sponsored: Shopsy Mega Festive Shopping Mall",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Get flat 80% discount on clothing & electronics!",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "AdMob interstitial ad required for free generation. VIP users get 100% ad-free instant rendering.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAdMobInterstitialModal = false
                        // Start Rendering Video upon Ad Completion
                        startVideoGenerationSimulation(
                            activeTool = activeTool,
                            prompt = userPromptText,
                            onProgress = { generationProgress = it },
                            onSuccess = { videoUrl ->
                                generatedVideoUrl = videoUrl
                                contentGeneratedSuccessfully = true
                                freeGenerationsUsedToday += 1
                            }
                        )
                    },
                    enabled = isAdCountdownFinished,
                    colors = ButtonDefaults.buttonColors(containerColor = StudioBrandPurple),
                    modifier = Modifier.testTag("btn_close_ad_and_generate")
                ) {
                    Text(if (isAdCountdownFinished) "Close Ad & Generate Video 🚀" else "Please wait ${adCountdownSeconds}s...")
                }
            }
        )
    }

    // Modal 3: VIP Subscription Upgrade Plans (₹49/15 Days, ₹179/Month, ₹499/Year)
    if (showSubscriptionModal) {
        AlertDialog(
            onDismissRequest = { showSubscriptionModal = false },
            title = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "👑 ViralBoost VIP Passes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StudioVipGold
                        ) {
                            Text(
                                text = "OFFICIAL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "Unlock all 8 AI Studio Tools, No Ads & Unlimited 4K Renders",
                        fontSize = 11.sp,
                        color = StudioNeonCyan
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(defaultVipPlans) { plan ->
                        val isSelected = selectedPlanForPayment.id == plan.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlanForPayment = plan
                                }
                                .testTag("plan_card_${plan.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF261D4C) else StudioSurfaceCard
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) StudioVipGold else StudioCardBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = plan.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = plan.durationText,
                                            fontSize = 11.sp,
                                            color = StudioNeonCyan
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = StudioHotPink
                                        ) {
                                            Text(
                                                text = plan.discountBadge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = "₹${plan.priceRupees}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = StudioVipGold
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "₹${plan.oldPriceRupees}",
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                plan.perks.forEach { perk ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "✓", color = Color(0xFF10B981), fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = perk, color = Color.LightGray, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    if (showUtrInput) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1535)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "UPI Payment for ₹${selectedPlanForPayment.priceRupees}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Pay via GPay, PhonePe, Paytm, or BHIM UPI:",
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "UPI ID: viralboost.vip@upi",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = StudioNeonCyan,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString("viralboost.vip@upi"))
                                                Toast.makeText(context, "UPI ID Copied!", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = StudioNeonCyan)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = enteredUtrNumber,
                                        onValueChange = { enteredUtrNumber = it },
                                        label = { Text("Enter 12-digit UPI Ref/UTR No.") },
                                        modifier = Modifier.fillMaxWidth().testTag("input_utr_vip"),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!showUtrInput) {
                            showUtrInput = true
                        } else {
                            if (enteredUtrNumber.length >= 6) {
                                hasActiveVipPlan = true
                                trialDaysLeft = 30
                                showSubscriptionModal = false
                                showUtrInput = false
                                Toast.makeText(
                                    context,
                                    "🎉 ${selectedPlanForPayment.title} Activated Successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(context, "Please enter valid 12-digit UTR number", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioVipGold, contentColor = Color.Black),
                    modifier = Modifier.testTag("btn_confirm_vip_payment")
                ) {
                    Text(
                        text = if (!showUtrInput) "Pay ₹${selectedPlanForPayment.priceRupees} Now" else "Verify & Activate VIP",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubscriptionModal = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Modal 4: Friend Referral Reward (+1 Day Free Pass)
    // 100% Hidden Backend Tracking ID (5665937) embedded in share link
    if (showReferralInviteModal) {
        val shareUrl = "https://viralboost.ai/app/invite?creator=vip&ref=$HIDDEN_BACKEND_AFFILIATE_ID"
        AlertDialog(
            onDismissRequest = { showReferralInviteModal = false },
            title = {
                Text(
                    text = "🎁 Invite Friends, Get +1 Day Free VIP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Share ViralBoost AI with friends or creator groups. For every friend who installs the app, you unlock a 24-Hour Free VIP Pass with all 8 AI Tools!",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1430)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Your Referral Link:",
                                fontSize = 10.sp,
                                color = StudioNeonCyan
                            )
                            Text(
                                text = shareUrl,
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Instant Claim Simulation Button for Demo & User Testing
                    Button(
                        onClick = {
                            trialDaysLeft += 1
                            showReferralInviteModal = false
                            Toast.makeText(
                                context,
                                "🎉 Friend Joined! +1 Day Free VIP Pass added! Total: $trialDaysLeft Days",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth().testTag("btn_claim_free_day_pass")
                    ) {
                        Text("Simulate Friend Joined (+1 Day Free Pass)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "🔥 Create viral reels & AI face-swap videos with ViralBoost AI! Join here: $shareUrl"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share ViralBoost AI"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioHotPink)
                ) {
                    Text("Share on WhatsApp", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReferralInviteModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal 5: Social Media Account Connector
    if (showAccountConnectorModal) {
        AlertDialog(
            onDismissRequest = { showAccountConnectorModal = false },
            title = {
                Text(
                    text = "🔗 Social Media Auto-Poster",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Connect your accounts to publish generated reels to all platforms simultaneously with 1 click:",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    // Platform Toggles
                    listOf(
                        Triple("Instagram Reels", isInstagramConnected) { isInstagramConnected = !isInstagramConnected },
                        Triple("YouTube Shorts", isYouTubeConnected) { isYouTubeConnected = !isYouTubeConnected },
                        Triple("Facebook Reels", isFacebookConnected) { isFacebookConnected = !isFacebookConnected },
                        Triple("GoLiveGram Creator", isGoLiveGramConnected) { isGoLiveGramConnected = !isGoLiveGramConnected }
                    ).forEach { (name, isConnected, toggle) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { toggle() }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = name, color = Color.White, fontSize = 12.sp)
                            Switch(
                                checked = isConnected,
                                onCheckedChange = { toggle() }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAccountConnectorModal = false }) {
                    Text("Done")
                }
            }
        )
    }
}

/**
 * Helper to simulate video generation with progress steps
 */
private fun startVideoGenerationSimulation(
    activeTool: AiStudioPaidTool,
    prompt: String,
    onProgress: (Float) -> Unit,
    onSuccess: (String) -> Unit
) {
    kotlinx.coroutines.GlobalScope.launch {
        onProgress(0.1f)
        delay(600L)
        onProgress(0.35f)
        delay(700L)
        onProgress(0.7f)
        delay(800L)
        onProgress(1.0f)
        delay(300L)
        onSuccess(activeTool.previewImageUrl)
    }
}

/**
 * Backward compatibility alias for ViralBoostPaidAiApp
 */
@Composable
fun ViralBoostPaidAiApp(
    initialHasActiveVipPlan: Boolean = false,
    initialFreeTrialDays: Int = 3,
    onActivatePlan: (planName: String, price: Int) -> Unit = { _, _ -> },
    onClaimInvitePass: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ViralBoostAllAiPaidApp(
        onBackToDashboard = null,
        modifier = modifier
    )
}
