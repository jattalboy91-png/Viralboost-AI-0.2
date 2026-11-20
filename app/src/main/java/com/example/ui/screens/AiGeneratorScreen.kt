package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AiGenerationRecord
import com.example.data.local.UserProfile
import com.example.data.repository.TrialStatus
import com.example.monetization.MonetizationConstants
import com.example.ui.components.AiReelAdDelayModal
import com.example.ui.components.AiReelCreatorDialog
import com.example.ui.components.AiReelCreatorMagicCard
import com.example.ui.components.NativeAdCard
import com.example.ui.components.UniversalMediatedAdDock
import com.example.ui.components.rememberMediatedInterstitialTrigger
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

data class CoreAiTool(
    val id: String,
    val title: String,
    val shortSubtitle: String,
    val icon: ImageVector,
    val category: String, // VIDEO, PHOTO, TEXT, CHAT
    val planQuotaLabel: String,
    val isLocked: (TrialStatus) -> Boolean
)

data class ChatMessage(
    val sender: String, // "USER" or "GEMINI"
    val text: String,
    val timestamp: String = "Just now"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiGeneratorScreen(
    userProfile: UserProfile?,
    trialStatus: TrialStatus = TrialStatus(1, true, false, 2, 2, 5, 2, 2, 5, false),
    aiHistory: List<AiGenerationRecord>,
    isGenerating: Boolean,
    bannerAdUnitId: String = MonetizationConstants.ADMOB_BANNER_UNIT_ID,
    nativeAdUnitId: String = MonetizationConstants.ADMOB_NATIVE_UNIT_ID,
    onGenerate: (type: String, topic: String, niche: String) -> Unit,
    onNavigateToVip: () -> Unit,
    onTriggerPaywall: (reason: String) -> Unit = {},
    onGenerate1ClickReel: ((topic: String, onResult: (String) -> Unit) -> Unit)? = null,
    onOpenGeminiSocialStudio: (() -> Unit)? = null,
    onSaveContentToHistory: ((type: String, topic: String, niche: String, content: String, platform: String, hashtags: String) -> Unit)? = null,
    onToggleBookmark: ((id: Long, isBookmarked: Boolean) -> Unit)? = null,
    onDeleteHistoryItem: ((id: Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile = userProfile ?: UserProfile()
    val triggerInterstitial = rememberMediatedInterstitialTrigger(isVip = profile.isVip)

    // 6 Core AI Tools as mandated
    val coreTools = listOf(
        CoreAiTool(
            id = "TEXT_TO_VIDEO",
            title = "Text-to-Video Engine",
            shortSubtitle = "Prompt to 9:16 Veo Video",
            icon = Icons.Default.VideoLibrary,
            category = "VIDEO",
            planQuotaLabel = if (trialStatus.isVip) {
                if (trialStatus.planTier == "PRO_3_MONTHS") "Unlimited AI Videos" else "${trialStatus.videosRemaining}/${trialStatus.maxVideosToday} Videos Ready"
            } else {
                "${trialStatus.videosRemaining}/2 Free Today"
            },
            isLocked = { it.isExpired || it.videosRemaining <= 0 }
        ),
        CoreAiTool(
            id = "PHOTO_TO_VIDEO",
            title = "Photo-to-Video & Image",
            shortSubtitle = "Motion Animation & Text-to-Image",
            icon = Icons.Default.Image,
            category = "PHOTO",
            planQuotaLabel = if (trialStatus.isVip) {
                if (trialStatus.planTier == "PRO_3_MONTHS") "Unlimited Photos" else "${trialStatus.imagesRemaining}/${trialStatus.maxImagesToday} Photos Ready"
            } else {
                "${trialStatus.imagesRemaining}/2 Free Today"
            },
            isLocked = { it.isExpired || it.imagesRemaining <= 0 }
        ),
        CoreAiTool(
            id = "THUMBNAIL",
            title = "AI Thumbnail Creator",
            shortSubtitle = "High-CTR 9:16 & 16:9 Cover Art",
            icon = Icons.Default.AutoAwesome,
            category = "PHOTO",
            planQuotaLabel = if (trialStatus.isVip) {
                if (trialStatus.planTier == "PRO_3_MONTHS") "Unlimited Thumbnails" else "${trialStatus.imagesRemaining}/${trialStatus.maxImagesToday} Available"
            } else {
                "${trialStatus.imagesRemaining}/2 Free Today"
            },
            isLocked = { it.isExpired || it.imagesRemaining <= 0 }
        ),
        CoreAiTool(
            id = "HOOKS_SCRIPTS",
            title = "Viral Hooks & 60s Scripts",
            shortSubtitle = "Zero-Drop Retention Engine",
            icon = Icons.Default.Bolt,
            category = "TEXT",
            planQuotaLabel = if (trialStatus.isVip) "Unlimited Scripts" else "${trialStatus.textRemaining}/5 Free Today",
            isLocked = { it.isExpired || it.textRemaining <= 0 }
        ),
        CoreAiTool(
            id = "SEO_TAGS",
            title = "Titles, Tags & SEO",
            shortSubtitle = "Algorithm Discovery & Tags",
            icon = Icons.Default.TrendingUp,
            category = "TEXT",
            planQuotaLabel = if (trialStatus.isVip) "Unlimited SEO Tags" else "${trialStatus.textRemaining}/5 Free Today",
            isLocked = { it.isExpired || it.textRemaining <= 0 }
        ),
        CoreAiTool(
            id = "GEMINI_CHAT",
            title = "Interactive Gemini AI Chat",
            shortSubtitle = "Real-time Viral Strategist",
            icon = Icons.Default.Star,
            category = "CHAT",
            planQuotaLabel = "24/7 AI Assistant",
            isLocked = { false }
        )
    )

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var selectedToolId by remember { mutableStateOf(coreTools.first().id) }
    val activeTool = coreTools.firstOrNull { it.id == selectedToolId } ?: coreTools.first()

    LaunchedEffect(selectedToolId) {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }

    // 1-Click AI Reel Creator States
    var showAiCreatorDialog by remember { mutableStateOf(false) }
    var videoTitleInput by remember { mutableStateOf("") }
    var generatedResult by remember { mutableStateOf<String?>(null) }
    var showAiAdWithDelay by remember { mutableStateOf(false) }

    var topicInput by remember { mutableStateOf("") }
    val niches = listOf("Shorts & Reels", "Tech & AI", "Finance & Wealth", "Gaming Nexus", "Fitness & Health", "Lifestyle & Vlog")
    var selectedNiche by remember { mutableStateOf(profile.niche.ifBlank { niches.first() }) }

    // Video Engine options
    var cameraMotion by remember { mutableStateOf("Dynamic Dolly Zoom") }
    val cameraMotions = listOf("Dynamic Dolly Zoom", "360° Orbit", "Glitch Transition", "Drone Fly-through")

    // Image/Photo options
    var photoStyle by remember { mutableStateOf("Cinematic 8K") }
    val photoStyles = listOf("Cinematic 8K", "Photorealistic", "Anime Pop", "3D Render")

    // Thumbnail options
    var thumbnailEmotion by remember { mutableStateOf("Shocked & Curious") }
    val thumbnailEmotions = listOf("Shocked & Curious", "Extreme Urgency", "Explosive Secret", "Millionaire Flex")
    var thumbnailOverlayText by remember { mutableStateOf("DON'T DO THIS!") }

    // Simulated Video Player state
    var isVideoPlaying by remember { mutableStateOf(true) }

    // Interactive Gemini Chat state
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("GEMINI", "Hey creator! I'm your Gemini AI Viral Strategist. Ask me how to fix your hook, optimize your retention curve, or generate video ideas for $selectedNiche!")
        )
    }
    var chatInputText by remember { mutableStateOf("") }

    val isCurrentToolLocked = activeTool.isLocked(trialStatus)
    val lockReason = when {
        trialStatus.isExpired -> "Your Free Trial has expired! All AI generation features are locked. Upgrade to VIP Pro to unlock unlimited access!"
        activeTool.category == "VIDEO" && trialStatus.videosRemaining <= 0 -> {
            if (trialStatus.isVip) "Daily video generation quota exhausted (${trialStatus.maxVideosToday}/${trialStatus.maxVideosToday}). Upgrade to 3-Month Ultimate Pro for UNLIMITED videos!"
            else "Free trial daily limit of 2 AI Videos reached (2/2 used). Upgrade to VIP to generate more videos!"
        }
        activeTool.category == "PHOTO" && trialStatus.imagesRemaining <= 0 -> {
            if (trialStatus.isVip) "Daily photo generation quota exhausted (${trialStatus.maxImagesToday}/${trialStatus.maxImagesToday}). Upgrade to 3-Month Ultimate Pro for UNLIMITED photos!"
            else "Free trial daily limit of 2 photos reached (2/2 used). Upgrade to VIP to generate more photos!"
        }
        trialStatus.textRemaining <= 0 -> "Daily text creation limit reached. Upgrade to VIP Pro to remove all daily limits!"
        else -> "This premium tool is locked for your plan tier. Upgrade now!"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Screen Title & Quota Status
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI Boost Studio",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Algorithmic generation engine & real-time creator tools",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    if (trialStatus.isVip) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(VipGold.copy(alpha = 0.2f))
                                .border(1.dp, VipGold, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (trialStatus.planTier == "PRO_3_MONTHS") "PRO UNLIMITED 👑" else "VIP ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VipGold
                            )
                        }
                    } else if (trialStatus.isExpired) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF7F1D1D).copy(alpha = 0.4f))
                                .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("TRIAL EXPIRED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF334155))
                                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Free Trial (Day ${trialStatus.trialDay}/3)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                    }
                }
            }

            // Quotas Status Banner
            item {
                TrialGatingSummaryCard(
                    trialStatus = trialStatus,
                    onUpgradeClick = { onTriggerPaywall(lockReason) }
                )
            }

            // Feature: Gemini Social Media Content Generator
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenGeminiSocialStudio?.invoke() }
                        .testTag("gemini_social_studio_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BrandPurple, NeonCyan)))
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(BrandPurple, NeonCyan))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Gemini Social Creator",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NeonCyan.copy(alpha = 0.2f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("GEMINI API", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Prompt to Viral Hooks, Captions & Hashtags",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        Button(
                            onClick = { onOpenGeminiSocialStudio?.invoke() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("launch_gemini_studio_button")
                        ) {
                            Text("Launch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // AI Tab Magic Button: "1-Click Trending AI Reel Creator" with Attractive Design
            item {
                AiReelCreatorMagicCard(
                    onOpenMagicBox = { showAiCreatorDialog = true }
                )
            }

            // Interactive Keyword-Driven Gemini AI Content Generator Component
            item {
                com.example.ui.components.GeminiKeywordContentGeneratorCard(
                    isVip = profile.isVip,
                    onOpenFullGeminiStudio = onOpenGeminiSocialStudio,
                    onContentGenerated = { result ->
                        onSaveContentToHistory?.invoke(
                            "GEMINI_SOCIAL",
                            result.hook.take(60),
                            selectedNiche,
                            "${result.hook}\n\n${result.bodyCaption}\n\n${result.callToAction}",
                            "Multi-Platform",
                            result.hashtags.joinToString(" ")
                        )
                    }
                )
            }

            // SECTION 1: MANDATORY VISIBILITY OF ALL 6 CORE AI TOOLS GRID
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Core AI Creative Suite",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Tap tool to launch",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2-Column Responsive Grid of All 6 Core Tools
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 2
                    ) {
                        coreTools.forEach { tool ->
                            val isSelected = tool.id == selectedToolId
                            val toolLocked = tool.isLocked(trialStatus)
                            val itemWidth = Modifier.weight(1f)

                            Card(
                                modifier = itemWidth
                                    .clickable {
                                        if (selectedToolId != tool.id) {
                                            triggerInterstitial("AI Tool Switch: ${tool.title}") {
                                                selectedToolId = tool.id
                                                if (toolLocked) {
                                                    onTriggerPaywall(lockReason)
                                                }
                                            }
                                        } else if (toolLocked) {
                                            onTriggerPaywall(lockReason)
                                        }
                                    }
                                    .testTag("ai_tool_card_${tool.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF26123D) else DarkSurface
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 1.8.dp else 1.dp,
                                    if (isSelected) BrandPurple else if (toolLocked) Color(0xFFEF4444).copy(alpha = 0.4f) else DarkSurfaceBorder
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) BrandPurple else DarkSurfaceElevated),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = tool.icon,
                                                contentDescription = tool.title,
                                                tint = if (toolLocked) Color.Gray else (if (isSelected) Color.White else NeonCyan),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        if (toolLocked) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF7F1D1D).copy(alpha = 0.5f))
                                                    .border(0.8.dp, Color(0xFFEF4444), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(10.dp))
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text("LOCKED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
                                                }
                                            }
                                        } else if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(NeonCyan.copy(alpha = 0.2f))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text("ACTIVE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = tool.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )

                                    Text(
                                        text = tool.shortSubtitle,
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = tool.planQuotaLabel,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (toolLocked) Color(0xFFFCA5A5) else GoldAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 2: ACTIVE TOOL WORKSPACE & REAL-TIME PREVIEW
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCurrentToolLocked) Color(0xFFEF4444).copy(alpha = 0.5f) else DarkSurfaceBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Tool Title Bar with Lock Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(activeTool.icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(activeTool.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(activeTool.shortSubtitle, fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            if (isCurrentToolLocked) {
                                Button(
                                    onClick = { onTriggerPaywall(lockReason) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp).testTag("tool_locked_paywall_button")
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Upgrade VIP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Tool-Specific Input Controls
                        when (activeTool.id) {
                            "TEXT_TO_VIDEO" -> {
                                Text("Video Concept / Script Prompt", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = topicInput,
                                    onValueChange = { topicInput = it },
                                    placeholder = { Text("e.g. 5 AI tools that will replace graphic designers in 2026") },
                                    modifier = Modifier.fillMaxWidth().testTag("topic_input_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandPurple,
                                        unfocusedBorderColor = DarkSurfaceBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Camera Motion Pattern", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(cameraMotions) { motion ->
                                        val isSel = cameraMotion == motion
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) BrandPurple else DarkSurfaceElevated)
                                                .border(1.dp, if (isSel) BrandPurple else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                                .clickable { cameraMotion = motion }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(motion, fontSize = 11.sp, color = if (isSel) Color.White else Color.LightGray)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                // Real-Time Video Preview Simulator
                                RealTimeVideoPlayerPreview(
                                    topic = topicInput.ifBlank { "5 AI Tools Replacing Designers" },
                                    cameraMotion = cameraMotion,
                                    isPlaying = isVideoPlaying,
                                    onTogglePlay = { isVideoPlaying = !isVideoPlaying }
                                )
                            }

                            "PHOTO_TO_VIDEO" -> {
                                Text("Image Concept & Animation Prompt", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = topicInput,
                                    onValueChange = { topicInput = it },
                                    placeholder = { Text("e.g. Glowing neon creator studio with floating viral metrics") },
                                    modifier = Modifier.fillMaxWidth().testTag("topic_input_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandPurple,
                                        unfocusedBorderColor = DarkSurfaceBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Art & Lighting Style", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(photoStyles) { style ->
                                        val isSel = photoStyle == style
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) BrandPurple else DarkSurfaceElevated)
                                                .border(1.dp, if (isSel) BrandPurple else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                                .clickable { photoStyle = style }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(style, fontSize = 11.sp, color = if (isSel) Color.White else Color.LightGray)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                RealTimePhotoPreviewCard(
                                    concept = topicInput.ifBlank { "Neon Creator Avatar" },
                                    style = photoStyle
                                )
                            }

                            "THUMBNAIL" -> {
                                Text("Video Title / Concept", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = topicInput,
                                    onValueChange = { topicInput = it },
                                    placeholder = { Text("e.g. How I Gained 100K Followers in 30 Days") },
                                    modifier = Modifier.fillMaxWidth().testTag("topic_input_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandPurple,
                                        unfocusedBorderColor = DarkSurfaceBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Impact Overlay Text (3 Words Max)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = thumbnailOverlayText,
                                    onValueChange = { thumbnailOverlayText = it },
                                    placeholder = { Text("DON'T DO THIS") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandPurple,
                                        unfocusedBorderColor = DarkSurfaceBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Focal Emotional Expression", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(thumbnailEmotions) { emo ->
                                        val isSel = thumbnailEmotion == emo
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) BrandPurple else DarkSurfaceElevated)
                                                .border(1.dp, if (isSel) BrandPurple else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                                .clickable { thumbnailEmotion = emo }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(emo, fontSize = 11.sp, color = if (isSel) Color.White else Color.LightGray)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                RealTimeThumbnailPreviewCard(
                                    overlayText = thumbnailOverlayText.ifBlank { "DON'T MISS!" },
                                    emotion = thumbnailEmotion
                                )
                            }

                            "GEMINI_CHAT" -> {
                                // Live Interactive Gemini AI Chat Assistant (Firebase AI Logic)
                                val chatCoroutineScope = rememberCoroutineScope()
                                val liveGeminiService = remember { com.example.data.service.GeminiSocialContentService() }
                                InteractiveGeminiChatView(
                                    messages = chatMessages,
                                    inputText = chatInputText,
                                    onInputChange = { chatInputText = it },
                                    onSendMessage = { userMsg ->
                                        val clean = userMsg.trim()
                                        if (clean.isNotBlank()) {
                                            chatMessages.add(ChatMessage("USER", clean))
                                            chatInputText = ""
                                            chatMessages.add(ChatMessage("GEMINI", "⏳ Gemini 3.8 Flash thinking..."))
                                            val replyIdx = chatMessages.lastIndex
                                            chatCoroutineScope.launch {
                                                try {
                                                    var streamedAny = false
                                                    liveGeminiService.generateSocialContentStream(
                                                        prompt = clean,
                                                        platform = "Instagram Reels & YouTube Shorts",
                                                        tone = "Actionable Creator Strategist",
                                                        contentType = "Creator Growth Advice",
                                                        targetAudience = "$selectedNiche Creators"
                                                    ).collect { partial ->
                                                        streamedAny = true
                                                        if (replyIdx in chatMessages.indices) {
                                                            chatMessages[replyIdx] = ChatMessage("GEMINI", partial)
                                                        }
                                                    }
                                                    if (!streamedAny) {
                                                        val fallback = liveGeminiService.generateSocialContent(
                                                            prompt = clean,
                                                            platform = "Instagram Reels & YouTube Shorts",
                                                            tone = "Actionable Creator Strategist",
                                                            contentType = "Creator Growth Advice"
                                                        )
                                                        if (replyIdx in chatMessages.indices) {
                                                            chatMessages[replyIdx] = ChatMessage("GEMINI", fallback.fullResponse)
                                                        }
                                                    }
                                                } catch (e: kotlinx.coroutines.CancellationException) {
                                                    throw e
                                                } catch (e: Exception) {
                                                    if (replyIdx in chatMessages.indices) {
                                                        chatMessages[replyIdx] = ChatMessage(
                                                            "GEMINI",
                                                            "⚠️ Gemini AI error: ${e.localizedMessage ?: "Network unavailable. Please try again."}"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    onQuickPrompt = { prompt ->
                                        chatMessages.add(ChatMessage("USER", prompt))
                                        chatMessages.add(ChatMessage("GEMINI", "⏳ Gemini 3.8 Flash thinking..."))
                                        val replyIdx = chatMessages.lastIndex
                                        chatCoroutineScope.launch {
                                            try {
                                                liveGeminiService.generateSocialContentStream(
                                                    prompt = prompt,
                                                    platform = "Instagram Reels & YouTube Shorts",
                                                    tone = "Actionable Creator Strategist",
                                                    contentType = "Creator Growth Advice",
                                                    targetAudience = "$selectedNiche Creators"
                                                ).collect { partial ->
                                                    if (replyIdx in chatMessages.indices) {
                                                        chatMessages[replyIdx] = ChatMessage("GEMINI", partial)
                                                    }
                                                }
                                            } catch (e: kotlinx.coroutines.CancellationException) {
                                                throw e
                                            } catch (e: Exception) {
                                                if (replyIdx in chatMessages.indices) {
                                                    chatMessages[replyIdx] = ChatMessage(
                                                        "GEMINI",
                                                        "⚠️ Gemini AI error: ${e.localizedMessage ?: "Network unavailable."}"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            else -> {
                                // HOOKS_SCRIPTS or SEO_TAGS
                                Text("Content Topic or Concept", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = topicInput,
                                    onValueChange = { topicInput = it },
                                    placeholder = { Text("e.g. 3-second retention tricks for viral Reels") },
                                    modifier = Modifier.fillMaxWidth().testTag("topic_input_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandPurple,
                                        unfocusedBorderColor = DarkSurfaceBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Content Niche", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(niches) { niche ->
                                        val isSel = selectedNiche == niche
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) BrandPurple else DarkSurfaceElevated)
                                                .border(1.dp, if (isSel) BrandPurple else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                                .clickable { selectedNiche = niche }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(niche, fontSize = 11.sp, color = if (isSel) Color.White else Color.LightGray)
                                        }
                                    }
                                }
                            }
                        }

                        // Generate Button for non-chat tools
                        if (activeTool.id != "GEMINI_CHAT") {
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    focusManager.clearFocus(force = true)
                                    keyboardController?.hide()
                                    if (isCurrentToolLocked) {
                                        onTriggerPaywall(lockReason)
                                    } else {
                                        val toolIdToRun = activeTool.id
                                        val topicToRun = topicInput
                                        val nicheToRun = selectedNiche
                                        triggerInterstitial("Generate AI Content: ${activeTool.title}") {
                                            onGenerate(toolIdToRun, topicToRun, nicheToRun)
                                        }
                                    }
                                },
                                enabled = !isGenerating,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("generate_viral_content_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCurrentToolLocked) Color(0xFFDC2626) else BrandPurple
                                )
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generating Algorithmic Output...", color = Color.White)
                                } else if (isCurrentToolLocked) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Limit Expired • Upgrade VIP Pro",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                } else {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Generate ${activeTool.title} (${activeTool.planQuotaLabel})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Organic AdMob Native Ad Card in content stream
            item {
                NativeAdCard(
                    isVip = profile.isVip,
                    adUnitId = nativeAdUnitId
                )
            }

            // SECTION 3: GENERATION HISTORY
            item {
                Text(
                    text = "Generated Viral Assets & History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (aiHistory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No generated assets yet.\nSelect any tool above to generate zero-drop hooks, videos, and thumbnails!",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(aiHistory) { record ->
                    GenerationHistoryItemCard(
                        record = record,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("ViralBoost Content", record.content)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            com.example.util.SocialShareHelper.shareRawAiAsset(
                                context = context,
                                type = record.type,
                                topic = record.topic,
                                niche = record.niche,
                                content = record.content
                            )
                        },
                        onToggleBookmark = {
                            onToggleBookmark?.invoke(record.id, !record.isBookmarked)
                        },
                        onDelete = {
                            onDeleteHistoryItem?.invoke(record.id)
                        }
                    )
                }
            }

            // Universal Mediated Rewarded Ad + Permanent Banner Ad (BP_Banner_Android / BP_Rewarded_Android / AdMob)
            item {
                UniversalMediatedAdDock(
                    isVip = profile.isVip,
                    admobBannerUnitId = bannerAdUnitId,
                    featureLabel = "AI Creative Suite"
                )
            }
        }

        // AI Reel Creator Dialog with 10-Second Ad Trigger & Title Input
        if (showAiCreatorDialog) {
            AiReelCreatorDialog(
                videoTitleInput = videoTitleInput,
                onTitleChange = { videoTitleInput = it },
                generatedResult = generatedResult,
                onGenerateClick = {
                    if (videoTitleInput.isNotBlank()) {
                        showAiAdWithDelay = true
                    }
                },
                onDismiss = { showAiCreatorDialog = false }
            )
        }

        // 10-Second Delay AdMob Interstitial Ad Simulation for AI Tool
        if (showAiAdWithDelay) {
            AiReelAdDelayModal(
                onSkipAdAndViewResult = {
                    showAiAdWithDelay = false
                    val safeTopic = videoTitleInput.trim().ifBlank { "Trending Topic" }
                    if (onGenerate1ClickReel != null) {
                        onGenerate1ClickReel(safeTopic) { res ->
                            generatedResult = res
                        }
                    } else {
                        generatedResult = "✨ Success! AI Script & Hashtags generated for: '$safeTopic'. Ready for Instagram/YouTube!\n\n🔥 3-Sec Viral Hook:\n\"Stop scrolling! Everyone doing '$safeTopic' is making this huge mistake...\"\n\n🎬 60-Sec Retention Script:\n• 00-03s: Instant visual shocker & hook\n• 04-20s: The 3 core principles of $safeTopic\n• 21-45s: Step-by-step secret trick that works in 2026\n• 46-60s: Call to action: Follow @ViralBoostAI for Part 2!\n\n🏷️ Hashtags:\n#${safeTopic.replace(Regex("[^a-zA-Z0-9]"), "")} #ViralReels #TrendingVideo #CreatorBoost #AIReels #ExplorePage"
                    }
                }
            )
        }
    }
}

@Composable
private fun TrialGatingSummaryCard(
    trialStatus: TrialStatus,
    onUpgradeClick: () -> Unit
) {
    if (trialStatus.isVip) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, VipGold)
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
                    Text("${trialStatus.planName} Active 👑", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VipGold)
                    Text("Daily Allowance: ${trialStatus.maxVideosToday} Videos • ${trialStatus.maxImagesToday} Photos • 100% Ad-Free", fontSize = 11.sp, color = Color.LightGray)
                }
            }
        }
        return
    }

    if (trialStatus.isExpired) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("trial_expired_alert_card"),
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
                        onClick = onUpgradeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = VipGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("trial_expired_unlock_button")
                    ) {
                        Text("Upgrade VIP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Free allowance has expired. Choose from 4 VIP tiers (₹49 for 10 Days, ₹99 for 15 Days, ₹149 for 1 Month, ₹399 for 3 Months Pro) to unlock unlimited tools!",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    lineHeight = 15.sp
                )
            }
        }
        return
    }

    // Active Free Trial Quota Summary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trial_active_allowance_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13112E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Free Trial Quotas (Day ${trialStatus.trialDay}/3)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onUpgradeClick,
                    colors = ButtonDefaults.buttonColors(containerColor = VipGold),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text("Plans from ₹49", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuotaPill(
                    label = "AI Videos",
                    remaining = trialStatus.videosRemaining,
                    max = trialStatus.maxVideosToday,
                    modifier = Modifier.weight(1f)
                )
                QuotaPill(
                    label = "AI Photos",
                    remaining = trialStatus.imagesRemaining,
                    max = trialStatus.maxImagesToday,
                    modifier = Modifier.weight(1f)
                )
                QuotaPill(
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
private fun QuotaPill(
    label: String,
    remaining: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val isExhausted = remaining <= 0
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isExhausted) Color(0xFF450A0A) else Color(0xFF0F172A))
            .border(
                1.dp,
                if (isExhausted) Color(0xFFEF4444) else Color(0xFF10B981).copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 9.5.sp, color = Color.LightGray)
            Text(
                text = if (isExhausted) "0/$max (Used)" else "$remaining/$max Ready",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isExhausted) Color(0xFFFCA5A5) else Color(0xFF34D399)
            )
        }
    }
}

@Composable
private fun RealTimeVideoPlayerPreview(
    topic: String,
    cameraMotion: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) Color(0xFF10B981) else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("9:16 Veo Fast Cinematic Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("1080x1920 • 60 FPS", fontSize = 9.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Simulated 9:16 Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2E1065), Color(0xFF0F172A), Color(0xFF020617))
                        )
                    )
                    .border(1.dp, Color(0xFF3B0764), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, VipGold, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = VipGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = topic,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Motion: $cameraMotion • Viral Retention Score: 98.8%",
                        fontSize = 10.sp,
                        color = GoldAccent
                    )
                }

                // Watermark for free tier
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("ViralBoost AI Veo 2", fontSize = 9.sp, color = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timeline bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("00:04", fontSize = 10.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = { 0.45f },
                    modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = NeonCyan,
                    trackColor = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("00:15", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun RealTimePhotoPreviewCard(
    concept: String,
    style: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandPurple.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Generated 9:16 Photo Motion Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BrandPurple.copy(alpha = 0.3f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("SDXL & Midjourney Prompt", fontSize = 9.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF4C1D95), Color(0xFF0E7490), Color(0xFF1E1B4B))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(concept, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                    Text("Render Style: $style • 4K HDR", fontSize = 10.sp, color = NeonCyan)
                }
            }
        }
    }
}

@Composable
private fun RealTimeThumbnailPreviewCard(
    overlayText: String,
    emotion: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Viral High-CTR Thumbnail Canvas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VipGold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF065F46))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Predicted CTR: 97.4% 🔥", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6EE7B7))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E1B4B), Color(0xFF831843), Color(0xFF701A75))
                        )
                    )
                    .border(2.dp, GoldAccent, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Yellow)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = overlayText.uppercase(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Trigger: $emotion",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun InteractiveGeminiChatView(
    messages: List<ChatMessage>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onQuickPrompt: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Interactive Gemini Creator Strategist", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Ask anything to optimize hooks, pacing, hashtags, or monetization", fontSize = 11.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        // Quick prompt chips
        val quickPrompts = listOf(
            "Fix my 3s hook",
            "Give me 3 viral ideas",
            "Best time to post today",
            "How to get 10k views fast"
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(quickPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, BrandPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable {
                            focusManager.clearFocus(force = true)
                            keyboardController?.hide()
                            onQuickPrompt(prompt)
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(prompt, fontSize = 10.sp, color = NeonCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message stream
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0B0F19))
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    val isGemini = msg.sender == "GEMINI"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isGemini) Arrangement.Start else Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isGemini) Color(0xFF1E1B4B) else BrandPurple)
                                .border(1.dp, if (isGemini) NeonCyan.copy(alpha = 0.4f) else BrandPurple, RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (isGemini) "🤖 Gemini Creator AI" else "👤 You",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isGemini) NeonCyan else Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = msg.text,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input and Send row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                placeholder = { Text("Ask Gemini AI for creator advice...") },
                modifier = Modifier.weight(1f).testTag("gemini_chat_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPurple,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                    onSendMessage(inputText)
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandPurple)
                    .testTag("gemini_chat_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun GenerationHistoryItemCard(
    record: AiGenerationRecord,
    onCopy: () -> Unit,
    onShare: () -> Unit = {},
    onToggleBookmark: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_history_card_${record.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (record.isBookmarked) GoldAccent else DarkSurfaceBorder
        )
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
                            .clip(RoundedCornerShape(4.dp))
                            .background(BrandPurple.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = record.type,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = record.niche,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("bookmark_ai_history_button_${record.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Bookmark Saved Content",
                            tint = if (record.isBookmarked) GoldAccent else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("share_ai_history_button_${record.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share AI Content",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Content",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (record.topic.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Topic: \"${record.topic}\"",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldAccent
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceElevated)
                    .padding(10.dp)
            ) {
                Text(
                    text = record.content,
                    fontSize = 11.5.sp,
                    color = Color.White,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
