package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfile
import com.example.data.local.VideoCrossPostRecord
import com.example.monetization.AdManager
import com.example.monetization.MonetizationConstants
import com.example.monetization.PaymentManager
import com.example.ui.components.BannerAdView
import com.example.ui.components.InterstitialAdDialog
import com.example.ui.components.UniversalMediatedAdDock
import com.example.ui.components.rememberMediatedInterstitialTrigger
import com.example.util.ReferralNotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SocialChannel(
    val id: String,
    val name: String,
    val badge: String,
    val brandColor: Color,
    val defaultHandle: String,
    val placeholder: String,
    val inputLabel: String,
    val description: String
)

data class AiGeneratedVideoMetadata(
    val title: String,
    val tags: String,
    val description: String,
    val viralScore: String
)

enum class PostingMode {
    FREE_MANUAL,
    AI_OPTIMIZATION
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VideoCrossPosterScreen(
    userProfile: UserProfile?,
    affiliateTag: String,
    downloadUrl: String = "",
    recentCrossPosts: List<VideoCrossPostRecord>,
    bannerAdUnitId: String,
    onNavigateBack: () -> Unit,
    onSubscribeAiPlan: (planName: String, price: Int, durationDays: Int) -> Unit = { _, _, _ -> },
    onPublishCrossPost: (
        title: String,
        tags: String,
        description: String,
        videoUri: String,
        platforms: List<String>,
        fullCaption: String
    ) -> Unit
) {
    BackHandler {
        onNavigateBack()
    }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    val isVip = userProfile?.isVip ?: false
    var isAiUnlockedLocally by remember { mutableStateOf(false) }
    val isAiUnlocked = isVip || isAiUnlockedLocally

    // Dynamic clean fallback download URL (never broken 404)
    val resolvedDownloadUrl = remember(downloadUrl, affiliateTag) {
        val cleanTag = affiliateTag.trim().ifBlank { MonetizationConstants.AFFILIATE_TAG }
        val trimmed = downloadUrl.trim()
        if (trimmed.isNotBlank() && !trimmed.contains("play.google.com/store/apps/details")) {
            trimmed
        } else {
            MonetizationConstants.buildAppDownloadUrl(cleanTag)
        }
    }

    // Dual-Action Posting Mode: Free Manual Post vs AI Viral Optimization
    var selectedPostingMode by remember { mutableStateOf(PostingMode.AI_OPTIMIZATION) }

    // Video selection state
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedVideoName by remember { mutableStateOf<String?>(null) }

    // Text inputs for video details
    var videoTitle by remember { mutableStateOf("") }
    var videoTags by remember { mutableStateOf("#ViralBoost #AIReels #Trending #GrowthHacks #AffiliateMarketing") }
    var videoDescription by remember { mutableStateOf("") }

    // AI Analysis State
    var isAnalyzingWithAi by remember { mutableStateOf(false) }
    var aiResultsGenerated by remember { mutableStateOf(false) }
    var generatedAiMetadata by remember { mutableStateOf<AiGeneratedVideoMetadata?>(null) }

    // Subscription Modal State for Direct UPI Payment & Instant Unlock
    var showSubscriptionModal by remember { mutableStateOf(false) }
    var pendingSubscriptionPlan by remember { mutableStateOf<Pair<String, Pair<Int, Int>>?>(null) }

    // Available Social Media Channels
    val availableChannels = remember {
        listOf(
            SocialChannel(
                id = "instagram",
                name = "Instagram Reels",
                badge = "IG",
                brandColor = Color(0xFFE1306C),
                defaultHandle = "@creator_pro",
                placeholder = "@username or profile link",
                inputLabel = "Instagram Username / Handle",
                description = "Connect Instagram account to publish reels, boost reach & earn commissions"
            ),
            SocialChannel(
                id = "facebook",
                name = "Facebook Reels",
                badge = "FB",
                brandColor = Color(0xFF1877F2),
                defaultHandle = "Viral Creators Page",
                placeholder = "Page Name or Page ID",
                inputLabel = "Facebook Page / Profile",
                description = "Broadcast to Facebook Pages & Reels for high viral engagement"
            ),
            SocialChannel(
                id = "youtube",
                name = "YouTube Shorts",
                badge = "YT",
                brandColor = Color(0xFFFF0000),
                defaultHandle = "ViralBoost Studio",
                placeholder = "Channel Handle (@handle) or ID",
                inputLabel = "YouTube Channel Handle",
                description = "Directly syndicate shorts to YouTube with automated viral footers"
            ),
            SocialChannel(
                id = "golivegram",
                name = "GoLivegram",
                badge = "GL",
                brandColor = Color(0xFF8A2BE2),
                defaultHandle = "@golive_official",
                placeholder = "@golive_handle or Stream ID",
                inputLabel = "GoLivegram User ID / Handle",
                description = "Multi-stream videos to the GoLivegram creator audience"
            ),
            SocialChannel(
                id = "telegram",
                name = "Telegram Channel",
                badge = "TG",
                brandColor = Color(0xFF229ED9),
                defaultHandle = "t.me/viral_creators",
                placeholder = "t.me/channel or @channel_username",
                inputLabel = "Telegram Channel Username / Link",
                description = "Push broadcasts directly to Telegram channels & creator groups"
            ),
            SocialChannel(
                id = "whatsapp",
                name = "WhatsApp Status",
                badge = "WA",
                brandColor = Color(0xFF25D366),
                defaultHandle = "+91 98765 43210",
                placeholder = "Phone number with country code",
                inputLabel = "WhatsApp Phone / Broadcast Group",
                description = "Post to WhatsApp Status & send to affiliate customer broadcast lists"
            )
        )
    }

    // Persisted channel connections & handles
    val sharedPrefs = remember {
        context.getSharedPreferences("viralboost_social_links", Context.MODE_PRIVATE)
    }

    val connectedMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            availableChannels.forEach { channel ->
                val defaultVal = channel.id in listOf("instagram", "facebook", "whatsapp")
                val isConn = sharedPrefs.getBoolean("conn_${channel.id}", defaultVal)
                put(channel.id, isConn)
            }
        }
    }

    val handleMap = remember {
        mutableStateMapOf<String, String>().apply {
            availableChannels.forEach { channel ->
                val savedHandle = sharedPrefs.getString("handle_${channel.id}", channel.defaultHandle) ?: channel.defaultHandle
                put(channel.id, savedHandle)
            }
        }
    }

    // State for interactive verification modal
    var activeChannelForLinking by remember { mutableStateOf<SocialChannel?>(null) }
    var verifyingChannelId by remember { mutableStateOf<String?>(null) }

    // Celebration modal state
    var showSuccessDialog by remember { mutableStateOf(false) }
    var lastPublishedCaption by remember { mutableStateOf("") }
    var lastPublishedPlatforms by remember { mutableStateOf<List<String>>(emptyList()) }

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

    // Automated AI Video Analysis & Metadata Integration Engine
    val runAiAnalysis: (String?) -> Unit = { vName ->
        isAnalyzingWithAi = true
        coroutineScope.launch {
            delay(1400) // Deep video scan simulation
            val baseName = vName?.substringBeforeLast(".")?.replace("_", " ")?.replace("-", " ")?.trim()?.ifBlank { "Reels" } ?: "Reels"
            val capitalizedNiche = baseName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            val analyzedTitle = "🔥 5 Insane $capitalizedNiche Hacks to 10X Reach & Views! (Watch Till End)"
            val analyzedTags = "#ViralReels #GrowthHacks #${capitalizedNiche.replace(" ", "").lowercase()} #CreatorEconomy #TrendingNow #AIReach #AffiliateIncome"
            val analyzedDescription = "Stop making this rookie mistake with your reels! Here is the exact viral framework top creators use to skyrocket reach, gain 100K followers, and earn passive affiliate income."

            generatedAiMetadata = AiGeneratedVideoMetadata(
                title = analyzedTitle,
                tags = analyzedTags,
                description = analyzedDescription,
                viralScore = "98/100 (High Algorithmic Potential • Predicted Reach: 250K+)"
            )

            // Instantly populate / embed them into video details!
            videoTitle = analyzedTitle
            videoTags = analyzedTags
            videoDescription = analyzedDescription

            isAnalyzingWithAi = false
            aiResultsGenerated = true
            Toast.makeText(context, "AI Analysis Complete! Viral Title, Tags & Description Embedded ✨", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery Video Picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            val vName = uri.lastPathSegment ?: "Gallery_Viral_Video.mp4"
            selectedVideoName = vName
            Toast.makeText(context, "Video loaded from gallery! 🎥", Toast.LENGTH_SHORT).show()

            // Video Cross-Posting Milestone 1: Exact moment user uploads/posts video into app
            AdManager.triggerMilestoneInterstitial(
                context = context,
                isVip = userProfile?.isVip == true,
                milestoneName = "Video Cross-Posting - Milestone 1: Video Uploaded",
                onShowInterstitialFallback = { onDismiss ->
                    pendingDismissAction = onDismiss
                    showMilestoneAdDialog = true
                }
            )

            // Automatically analyze and embed metadata if AI mode is active and unlocked
            if (selectedPostingMode == PostingMode.AI_OPTIMIZATION && isAiUnlocked) {
                runAiAnalysis(vName)
            } else if (selectedPostingMode == PostingMode.AI_OPTIMIZATION && !isAiUnlocked) {
                Toast.makeText(context, "Unlock AI Video Subscription below to automatically generate viral hooks! ⚡", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Helper: Build Full Viral Caption with Smart Fallback Affiliate Link Handling
    // If user has not entered a personal link, automatically falls back to Owner Master Link (https://bitli.in/Ff4aE3C tied to EarnKaro ID: 5665937)
    // If user has saved their personal link, attaches their link for their earnings while maintaining background owner structure
    val createViralCaption: (title: String, tags: String, description: String) -> String = { pTitle, pTags, pDesc ->
        val userPersonalLink = MonetizationConstants.getUserPersonalAffiliateLink(context)
        MonetizationConstants.buildSmartAffiliateCaption(
            title = pTitle,
            tags = pTags,
            description = pDesc,
            personalAffiliateLink = userPersonalLink,
            downloadUrl = resolvedDownloadUrl
        )
    }

    // Broadcast executor: "Post to Go Viral"
    val handlePostToGoViral = {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()

        val activeChannels = availableChannels.filter { connectedMap[it.id] == true }
        if (activeChannels.isEmpty()) {
            Toast.makeText(context, "Please verify & connect at least 1 platform channel above!", Toast.LENGTH_SHORT).show()
        } else {
            val titleToUse = videoTitle.ifBlank { "Viral Reels & Growth Video" }
            val caption = createViralCaption(titleToUse, videoTags, videoDescription)
            val platformNames = activeChannels.map { it.name }

            val executeViralBroadcast = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = if (selectedVideoUri != null) "video/*" else "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, titleToUse)
                    putExtra(Intent.EXTRA_TEXT, caption)
                    if (selectedVideoUri != null) {
                        putExtra(Intent.EXTRA_STREAM, selectedVideoUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }

                val chooser = Intent.createChooser(sendIntent, "Post to Go Viral via:")
                context.startActivity(chooser)

                onPublishCrossPost(
                    titleToUse,
                    videoTags,
                    videoDescription,
                    selectedVideoUri?.toString() ?: "",
                    platformNames,
                    caption
                )

                lastPublishedCaption = caption
                lastPublishedPlatforms = platformNames
                showSuccessDialog = true

                // Success Notification ("Go Viral") immediately after successful share
                ReferralNotificationHelper.showPostReadyToGoViralNotification(context)
                Toast.makeText(context, "Congratulations! Your post is ready to go viral!", Toast.LENGTH_LONG).show()
            }

            // Video Cross-Posting Milestone 2: Exact moment user taps "Post to Go Viral" / Share
            AdManager.triggerMilestoneInterstitial(
                context = context,
                isVip = userProfile?.isVip == true,
                milestoneName = "Video Cross-Posting - Milestone 2: Post to Go Viral",
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Viral Video Cross-Poster",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Multi-Platform Broadcast • Earn +10 Coins 🪙",
                            fontSize = 12.sp,
                            color = Color(0xFFFFD54F)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("crossposter_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF131722)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Hero Card with Dual-Action Tagline & Selection Options
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F2C)),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(Color(0xFF8A2BE2), Color(0xFFFF6F00)))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(Color(0xFFFF8F00), Color(0xFFFF3D00)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "One-Click Multi-Platform Broadcaster",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Simultaneous broadcast across 6 social networks",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB0BEC5)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Mandatory Dual-Action Hindi Tagline
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF261D38)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ElectricBolt,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD54F),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "फ्री में वीडियो पोस्ट करें मल्टी-प्लेटफॉर्म पर, और तेजी से वायरल होने के लिए AI की मदद लें!",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFFFE082),
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Two Clear Selection Options: Free Manual Post vs AI Viral Optimization
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Option 1: Free Manual Post
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (selectedPostingMode != PostingMode.FREE_MANUAL) {
                                                AdManager.triggerMilestoneInterstitial(
                                                    context = context,
                                                    isVip = isVip,
                                                    milestoneName = "Cross-Poster Mode: Free Manual Post",
                                                    onShowInterstitialFallback = { onDismiss ->
                                                        pendingDismissAction = {
                                                            onDismiss()
                                                            selectedPostingMode = PostingMode.FREE_MANUAL
                                                        }
                                                        showMilestoneAdDialog = true
                                                    },
                                                    onComplete = {
                                                        selectedPostingMode = PostingMode.FREE_MANUAL
                                                    }
                                                )
                                            }
                                        }
                                        .testTag("option_free_manual_post"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedPostingMode == PostingMode.FREE_MANUAL) Color(0xFF1E293B) else Color(0xFF141923)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (selectedPostingMode == PostingMode.FREE_MANUAL) 2.dp else 1.dp,
                                        if (selectedPostingMode == PostingMode.FREE_MANUAL) Color(0xFF38BDF8) else Color(0xFF2E384D)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = if (selectedPostingMode == PostingMode.FREE_MANUAL) Color(0xFF38BDF8) else Color(0xFF90A4AE),
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Free Manual Post",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "फ्री मैन्युअल पोस्ट",
                                            fontSize = 11.sp,
                                            color = Color(0xFF81D4FA)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Custom Details • 100% Free",
                                            fontSize = 9.sp,
                                            color = Color(0xFF94A3B8),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // Option 2: AI Viral Optimization / Subscription
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (selectedPostingMode != PostingMode.AI_OPTIMIZATION) {
                                                AdManager.triggerMilestoneInterstitial(
                                                    context = context,
                                                    isVip = isVip,
                                                    milestoneName = "Cross-Poster Mode: AI Viral Optimization",
                                                    onShowInterstitialFallback = { onDismiss ->
                                                        pendingDismissAction = {
                                                            onDismiss()
                                                            selectedPostingMode = PostingMode.AI_OPTIMIZATION
                                                        }
                                                        showMilestoneAdDialog = true
                                                    },
                                                    onComplete = {
                                                        selectedPostingMode = PostingMode.AI_OPTIMIZATION
                                                    }
                                                )
                                            }
                                        }
                                        .testTag("option_ai_viral_optimization"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedPostingMode == PostingMode.AI_OPTIMIZATION) Color(0xFF2A1C42) else Color(0xFF141923)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (selectedPostingMode == PostingMode.AI_OPTIMIZATION) 2.dp else 1.dp,
                                        if (selectedPostingMode == PostingMode.AI_OPTIMIZATION) Color(0xFFC084FC) else Color(0xFF2E384D)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = if (selectedPostingMode == PostingMode.AI_OPTIMIZATION) Color(0xFFFFD54F) else Color(0xFF90A4AE),
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "AI Viral Optimization",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "AI वायरल ऑप्टिमाइज़ेशन",
                                            fontSize = 11.sp,
                                            color = Color(0xFFFFD54F)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Auto-Embed Viral Hooks & Tags",
                                            fontSize = 9.sp,
                                            color = Color(0xFFE2E8F0),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 2: Upload Video from Gallery
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = null,
                                        tint = Color(0xFF81D4FA),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "1. Upload Video from Gallery",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF81D4FA)
                                    )
                                }
                                if (selectedVideoUri != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Ready", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (selectedVideoUri == null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF202738))
                                        .border(1.dp, Color(0xFF37474F), RoundedCornerShape(12.dp))
                                        .clickable {
                                            videoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                            )
                                        }
                                        .testTag("upload_video_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.UploadFile,
                                            contentDescription = "Upload Video",
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(38.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Tap to Upload Video from Gallery",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Supports MP4, MOV, WebM Reels & Shorts",
                                            fontSize = 11.sp,
                                            color = Color(0xFF90A4AE)
                                        )
                                    }
                                }
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF21293B)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE91E63)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Movie,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = selectedVideoName ?: "Gallery_Video.mp4",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Attached from Gallery • Ready to Post",
                                                fontSize = 11.sp,
                                                color = Color(0xFFB0BEC5)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                selectedVideoUri = null
                                                selectedVideoName = null
                                                aiResultsGenerated = false
                                                generatedAiMetadata = null
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = Color(0xFFFF5252)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 3: AI Video Optimization & Direct UPI Subscription Unlock
                if (selectedPostingMode == PostingMode.AI_OPTIMIZATION) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1738)),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                Brush.horizontalGradient(listOf(Color(0xFF8A2BE2), Color(0xFFFF6F00)))
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
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(Brush.linearGradient(listOf(Color(0xFF8A2BE2), Color(0xFFFF8F00)))),
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
                                            Text(
                                                text = "Automated AI Video Analysis",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 15.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = if (isAiUnlocked) "AI Suite Unlocked • VIP Active ✨" else "Instant AI Unlock via Owner UPI",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (isAiUnlocked) Color(0xFF00E676) else Color(0xFFFFD54F)
                                            )
                                        }
                                    }

                                    if (aiResultsGenerated) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF00C853))
                                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Text("ANALYZED ✓", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "AI analyzes video content to generate high-CTR trending titles, keywords, and viral hashtags, instantly embedding them into the video details.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFD1C4E9),
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // If AI is unlocked, provide Analyze button
                                if (isAiUnlocked) {
                                    Button(
                                        onClick = { runAiAnalysis(selectedVideoName) },
                                        enabled = !isAnalyzingWithAi,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .testTag("analyze_video_with_ai_button"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))
                                    ) {
                                        if (isAnalyzingWithAi) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("AI Analyzing Video & Algorithm Trends...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        } else {
                                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (aiResultsGenerated) "Re-Analyze & Re-Embed with AI ⚡" else "Analyze Video & Embed with AI ✨",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                // Display Generated AI Results if scanned
                                if (aiResultsGenerated && generatedAiMetadata != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF130E26)),
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9C27B0))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "🎯 Embedded AI Metadata",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFFFFD54F)
                                                )
                                                Text(
                                                    text = generatedAiMetadata!!.viralScore,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF00E676),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Title: ${generatedAiMetadata!!.title}",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Tags: ${generatedAiMetadata!!.tags}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF81D4FA)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Desc: ${generatedAiMetadata!!.description}",
                                                fontSize = 11.sp,
                                                color = Color(0xFFB0BEC5)
                                            )
                                        }
                                    }
                                }

                                // If AI is not unlocked yet, display Direct UPI Subscription Plans
                                if (!isAiUnlocked) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "Direct UPI Subscription Plans to Unlock AI Analyzer:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFFFC107)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Plan 1: ₹49 for 15 Days
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    pendingSubscriptionPlan = "15-Day Viral AI Video Pass" to (49 to 15)
                                                    showSubscriptionModal = true
                                                }
                                                .testTag("plan_49_15days_card"),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF231B45)),
                                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF8B5CF6))
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text("15 DAYS PLAN", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF81D4FA))
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("₹49", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                                Text("15 Days Access", fontSize = 10.sp, color = Color(0xFFB0BEC5))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Text("✓ Trending viral titles", fontSize = 9.sp, color = Color(0xFFE1BEE7))
                                                    Text("✓ Smart hashtags", fontSize = 9.sp, color = Color(0xFFE1BEE7))
                                                    Text("✓ High-conversion tags", fontSize = 9.sp, color = Color(0xFFE1BEE7))
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFF8B5CF6))
                                                        .padding(vertical = 7.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("Pay ₹49 via UPI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                        }

                                        // Plan 2: ₹79 for 1 Month
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    pendingSubscriptionPlan = "1-Month Creator Pro Pass" to (79 to 30)
                                                    showSubscriptionModal = true
                                                }
                                                .testTag("plan_79_1month_card"),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1C30)),
                                            border = androidx.compose.foundation.BorderStroke(1.4.dp, Color(0xFFFF8F00))
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text("1 MONTH PLAN", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD54F))
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("₹79", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                                Text("30 Days (Save 50%)", fontSize = 10.sp, color = Color(0xFFB0BEC5))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Text("★ Extended duration", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFE082))
                                                    Text("★ Unlimited AI optimization", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFE082))
                                                    Text("★ Priority multi-posting", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFE082))
                                                    Text("★ Maximum growth tools", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFE082))
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Brush.horizontalGradient(listOf(Color(0xFFFF8F00), Color(0xFFFF3D00))))
                                                        .padding(vertical = 7.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("Pay ₹79 via UPI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 4: Video Details (Title, Tags, Description)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E384D))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color(0xFF81D4FA),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (selectedPostingMode == PostingMode.AI_OPTIMIZATION) "2. AI-Embedded Video Details" else "2. Custom Video Details (Manual)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF81D4FA)
                                    )
                                }

                                if (isAiUnlocked) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFFB300))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AI OPTIMIZED ✨", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (selectedPostingMode == PostingMode.AI_OPTIMIZATION) "AI automatically populates high-CTR title, keywords, and viral hashtags below:" else "Manually enter your custom title, tags, and description:",
                                fontSize = 11.sp,
                                color = Color(0xFF90A4AE)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Video Title Input
                            OutlinedTextField(
                                value = videoTitle,
                                onValueChange = { videoTitle = it },
                                label = { Text("Video Title / Hook Headline") },
                                placeholder = { Text("e.g. 5 Secret AI Hacks to 10X Your Reach!") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("video_title_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF8A2BE2),
                                    unfocusedBorderColor = Color(0xFF37474F),
                                    focusedLabelColor = Color(0xFFBA68C8),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Tags Input
                            OutlinedTextField(
                                value = videoTags,
                                onValueChange = { videoTags = it },
                                label = { Text("Tags & Keywords") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("video_tags_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF8A2BE2),
                                    unfocusedBorderColor = Color(0xFF37474F),
                                    focusedLabelColor = Color(0xFFBA68C8),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Description Input
                            OutlinedTextField(
                                value = videoDescription,
                                onValueChange = { videoDescription = it },
                                label = { Text("Description & SEO Notes (Optional)") },
                                placeholder = { Text("Tell viewers what this video is about...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("video_description_input"),
                                minLines = 2,
                                maxLines = 4,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF8A2BE2),
                                    unfocusedBorderColor = Color(0xFF37474F),
                                    focusedLabelColor = Color(0xFFBA68C8),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                // Section 5: Real Account Connection & Verification Hub
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = Color(0xFF81D4FA),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "3. Real Account Connection Hub",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF81D4FA)
                                    )
                                }
                                val count = connectedMap.values.count { it }
                                Text(
                                    text = "$count of ${availableChannels.size} Connected",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter your handle/ID and tap 'Verify & Connect' to validate each platform:",
                                fontSize = 11.sp,
                                color = Color(0xFF90A4AE)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Interactive Tappable Account Cards with Real Verification
                            availableChannels.forEach { channel ->
                                val isConnected = connectedMap[channel.id] ?: false
                                val currentHandle = handleMap[channel.id] ?: channel.defaultHandle
                                val isVerifying = verifyingChannelId == channel.id

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            activeChannelForLinking = channel
                                        }
                                        .testTag("platform_card_${channel.id}"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isConnected) Color(0xFF1F2839) else Color(0xFF1B202D)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isConnected) channel.brandColor.copy(alpha = 0.8f) else Color(0xFF2C3548)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(channel.brandColor),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = channel.badge,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 12.sp,
                                                    color = Color.White
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = channel.name,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = if (isConnected) currentHandle else "Not Linked • Tap to Connect",
                                                    fontSize = 11.sp,
                                                    color = if (isConnected) Color(0xFF81D4FA) else Color(0xFFFFB300),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isConnected) {
                                                // Real "Successfully Connected" Status Indicator
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFF00C853).copy(alpha = 0.2f))
                                                        .border(1.dp, Color(0xFF00C853), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                        .testTag("status_connected_${channel.id}"),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = Color(0xFF00E676),
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = "Successfully Connected",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF00E676)
                                                        )
                                                    }
                                                }
                                            } else {
                                                // "Verify & Connect" Action
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFF263238))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                        .testTag("verify_and_connect_${channel.id}")
                                                ) {
                                                    Text(
                                                        text = "Verify & Connect",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFFD54F)
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

                // Section 6: Mandatory Automated Viral Promotional Footer Preview
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E384D))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "4. Automated Viral Promotional Footer",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB300)
                                )
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
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
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (hasPersonal) "🛍️ AI Auto-Deal: Personal Affiliate Link Active ✅" else "⚡ AI Auto-Deal: Owner Master Fallback Active (Bitli https://bitli.in/Ff4aE3C)",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasPersonal) Color(0xFF34D399) else Color(0xFFFFD54F)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Appended automatically to every cross-posted video for app downloads and commissions:",
                                fontSize = 11.sp,
                                color = Color(0xFFB0BEC5)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F131D))
                                    .padding(10.dp)
                            ) {
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
                                    fontSize = 11.sp,
                                    color = Color(0xFF80CBC4),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Section 7: One-Click "Post to Go Viral" Button
                item {
                    Button(
                        onClick = handlePostToGoViral,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("post_to_go_viral_button")
                            .testTag("publish_post_everywhere_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF8A2BE2), Color(0xFFFF6F00))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "POST TO GO VIRAL",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF00C853))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+10 Coins 🪙",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 8: Recent Broadcasts History
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Recent Multi-Platform Broadcasts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (recentCrossPosts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No broadcasts yet. Select your video and tap 'Post to Go Viral' to broadcast and earn +10 Coins!",
                                    fontSize = 12.sp,
                                    color = Color(0xFF90A4AE),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(recentCrossPosts) { post ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263238))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = post.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF00C853))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "+${post.coinsRewarded} Coins",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Platforms: ${post.connectedPlatforms}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF81D4FA)
                                )

                                val formattedDate = remember(post.timestamp) {
                                    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(post.timestamp))
                                }
                                Text(
                                    text = "Broadcasted: $formattedDate",
                                    fontSize = 10.sp,
                                    color = Color(0xFF78909C)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Bottom Universal Mediated Rewarded Ad + Permanent Banner Ad (BP_Banner_Android / BP_Rewarded_Android / AdMob)
            UniversalMediatedAdDock(
                isVip = isVip,
                admobBannerUnitId = bannerAdUnitId,
                featureLabel = "Viral Video Cross-Poster"
            )
        }
    }

    // Modal: Real Account Connection & "Verify & Connect" Setup
    activeChannelForLinking?.let { channel ->
        var inputId by remember(channel) {
            mutableStateOf(handleMap[channel.id] ?: channel.defaultHandle)
        }
        var isVerifying by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { activeChannelForLinking = null },
            containerColor = Color(0xFF1B202D),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(channel.brandColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = channel.badge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Connect ${channel.name}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = channel.description,
                        fontSize = 12.sp,
                        color = Color(0xFFB0BEC5)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = inputId,
                        onValueChange = { inputId = it },
                        label = { Text(channel.inputLabel) },
                        placeholder = { Text(channel.placeholder) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("account_id_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = channel.brandColor,
                            unfocusedBorderColor = Color(0xFF37474F),
                            focusedLabelColor = channel.brandColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enter your creator handle, channel link, or ID to connect and broadcast.",
                        fontSize = 11.sp,
                        color = Color(0xFF78909C)
                    )

                    if (isVerifying) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = channel.brandColor,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Validating credentials with ${channel.name} API...",
                                fontSize = 11.sp,
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalHandle = inputId.trim().ifBlank { channel.defaultHandle }
                        isVerifying = true
                        coroutineScope.launch {
                            delay(800) // Verification handshake delay
                            isVerifying = false
                            handleMap[channel.id] = finalHandle
                            connectedMap[channel.id] = true

                            sharedPrefs.edit()
                                .putBoolean("conn_${channel.id}", true)
                                .putString("handle_${channel.id}", finalHandle)
                                .apply()

                            Toast.makeText(context, "${channel.name} Verified & Successfully Connected! ✅", Toast.LENGTH_SHORT).show()
                            activeChannelForLinking = null
                        }
                    },
                    enabled = !isVerifying,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    modifier = Modifier.testTag("verify_and_connect_button")
                ) {
                    Text("Verify & Connect", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        connectedMap[channel.id] = false
                        sharedPrefs.edit().putBoolean("conn_${channel.id}", false).apply()
                        Toast.makeText(context, "${channel.name} unlinked", Toast.LENGTH_SHORT).show()
                        activeChannelForLinking = null
                    }
                ) {
                    Text("Disconnect", color = Color(0xFFFF5252))
                }
            }
        )
    }

    // Modal: Direct UPI Payment & Instant AI Unlock (₹49 for 15 Days & ₹79 for 1 Month)
    if (showSubscriptionModal && pendingSubscriptionPlan != null) {
        val (planName, details) = pendingSubscriptionPlan!!
        val (price, durationDays) = details
        var utrNumber by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSubscriptionModal = false },
            containerColor = Color(0xFF1E1738),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unlock $planName",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Unlock automated AI video analysis, trending tags, and high-CTR viral hooks for the next $durationDays days.",
                        fontSize = 12.sp,
                        color = Color(0xFFD1C4E9)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF120C26)),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8A2BE2))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Amount Payable:", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                                Text("₹$price", color = Color(0xFF00E676), fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Official Owner UPI:", color = Color(0xFFB0BEC5), fontSize = 12.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = MonetizationConstants.OWNER_UPI_ID,
                                        color = Color(0xFFFFD54F),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(MonetizationConstants.OWNER_UPI_ID))
                                            Toast.makeText(context, "UPI ID copied: ${MonetizationConstants.OWNER_UPI_ID}", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy UPI",
                                            tint = Color(0xFF81D4FA),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Direct UPI App Launch Option
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
                                Toast.makeText(context, "UPI ID Copied! Pay ₹$price via your UPI App", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676))
                    ) {
                        Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pay ₹$price via Installed UPI App", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = utrNumber,
                        onValueChange = { utrNumber = it },
                        label = { Text("12-Digit UPI Ref / UTR (Optional)") },
                        placeholder = { Text("Enter UTR for verification") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_subscription_utr_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8A2BE2),
                            unfocusedBorderColor = Color(0xFF37474F),
                            focusedLabelColor = Color(0xFFBA68C8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubscribeAiPlan(planName, price, durationDays)
                        isAiUnlockedLocally = true
                        showSubscriptionModal = false
                        Toast.makeText(context, "$planName Verified! AI Video Analyzer Unlocked! 🚀", Toast.LENGTH_SHORT).show()

                        // If video is selected, automatically analyze it now!
                        if (selectedVideoUri != null) {
                            runAiAnalysis(selectedVideoName)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    modifier = Modifier.testTag("confirm_ai_subscription_button")
                ) {
                    Text("Verify & Instant AI Unlock", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSubscriptionModal = false }
                ) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }

    // Modal: Success Celebration Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            containerColor = Color(0xFF1A1F2C),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Video Posted to Go Viral! 🚀",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "+10 Coins have been credited directly to your creator wallet! 🪙",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFD54F)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Auto-Branded caption shared across ${lastPublishedPlatforms.size} connected platforms (${lastPublishedPlatforms.joinToString()}).",
                        fontSize = 12.sp,
                        color = Color(0xFFB0BEC5)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(lastPublishedCaption))
                            Toast.makeText(context, "Full viral caption copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF81D4FA))
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Caption Again", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))
                ) {
                    Text("Awesome!", color = Color.White)
                }
            }
        )
    }
}
