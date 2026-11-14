package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.CommunityVideoPost
import com.example.monetization.AdManager
import com.example.monetization.MonetizationConstants
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Unified Multi-Platform Connected Video Feed (YouTube, Instagram, GoLiveGram)
 * Features:
 * 1. Community feed with interactive Follow & View Count mechanisms
 * 2. Per-Video Reward System (+15 Coins & +3 Diamonds per video watched)
 * 3. Creator Boost & Super Spotlight (Second income stream for app owner via UPI / Coins / Diamonds)
 * 4. Balanced AdMob Integration:
 *    - 10-second entry ad after opening Reels Hub
 *    - 1 ad for short videos during playback
 *    - 2 ads for long videos during playback
 */
@Composable
fun ViralCreatorReelsHubDialog(
    videos: List<CommunityVideoPost>,
    isVip: Boolean = false,
    userCoins: Int = 250,
    userDiamonds: Int = 10,
    onRewardVideoWatch: (videoId: Long, videoTitle: String) -> Unit,
    onToggleFollow: (videoId: Long, creatorHandle: String) -> Unit,
    onIncrementLike: (videoId: Long) -> Unit,
    onBoostWithDiamonds: (videoId: Long, diamonds: Int) -> Unit,
    onBoostWithCoins: (videoId: Long, coins: Int) -> Unit,
    onBoostWithUpi: (videoId: Long, planName: String, price: Int, utr: String) -> Unit,
    onSubmitNewVideo: (title: String, desc: String, platform: String, type: String, url: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("All") }
    var showBoostModalForVideo by remember { mutableStateOf<CommunityVideoPost?>(null) }
    var showSubmitVideoModal by remember { mutableStateOf(false) }

    // Interstitial Fallback Dialog
    var showAdFallbackDialog by remember { mutableStateOf(false) }
    var pendingAdDismissAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val triggerAd: (milestoneName: String, onAfter: () -> Unit) -> Unit = { name, onAfter ->
        AdManager.triggerMilestoneInterstitial(
            context = context,
            isVip = isVip,
            milestoneName = name,
            onShowInterstitialFallback = { onDismissFallback ->
                pendingAdDismissAction = {
                    onDismissFallback()
                    onAfter()
                }
                showAdFallbackDialog = true
            },
            onComplete = onAfter
        )
    }

    // 10-Second Entry AdMob Interstitial Trigger
    var entryAdTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(10000L)
        if (!entryAdTriggered) {
            entryAdTriggered = true
            triggerAd("Viral Creator Reels Hub - 10s Entry Ad") {}
        }
    }

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("viral_creator_reels_hub_dialog")
    ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // =============================================================
                // TOP HEADER: BRANDING + COINS/DIAMOND EARNING TAG + SUBMIT BUTTON
                // =============================================================
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reels_hub_header"),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFFFF0055), Color(0xFF8A2BE2), Color(0xFF00E5FF))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎬", fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Viral Creator Reels Hub",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFE11D48))
                                                .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                        ) {
                                            Text("LIVE FEED", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                    Text(
                                        text = "YouTube • Instagram • GoLiveGram Connected Community",
                                        fontSize = 9.5.sp,
                                        color = DiamondCyan
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Watch Reward Tag: +15 Coins & +3 Diamonds
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF064E3B))
                                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "+15🪙 & +3💎/video",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DiamondCyan
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B))
                                        .testTag("close_reels_hub_button")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Bar: "Submit Video Link" & "Super Spotlight Info"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showSubmitVideoModal = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("btn_submit_creator_video"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Link / Submit Video 🎬", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    val firstVideo = videos.firstOrNull()
                                    if (firstVideo != null) {
                                        showBoostModalForVideo = firstVideo
                                    } else {
                                        Toast.makeText(context, "Link a video first to activate Creator Boost!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("btn_creator_super_spotlight"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Super Spotlight 🚀", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Platform Filter Pills
                        val filterCategories = listOf("All", "YouTube 🔴", "Instagram 📸", "GoLiveGram 💜", "⚡ Shorts", "🎬 Long", "🌟 Spotlight")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(filterCategories) { filter ->
                                val isSelected = selectedFilter == filter
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFFFF9900) else Color(0xFF1E1B4B))
                                        .border(1.dp, if (isSelected) Color(0xFFFF9900) else Color(0xFF3730A3), RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (selectedFilter != filter) {
                                                triggerAd("Reels Hub Filter: $filter") {
                                                    selectedFilter = filter
                                                }
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        filter,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.Black else Color(0xFFE0E7FF)
                                    )
                                }
                            }
                        }
                    }
                }

                // =============================================================
                // VIDEO FEED STREAM (60-120 FPS BUTTERY SMOOTH LAZY COLUMN)
                // =============================================================
                val filteredVideos = remember(videos, selectedFilter) {
                    videos.filter { video ->
                        when (selectedFilter) {
                            "YouTube 🔴" -> video.platform.contains("YouTube", ignoreCase = true)
                            "Instagram 📸" -> video.platform.contains("Instagram", ignoreCase = true)
                            "GoLiveGram 💜" -> video.platform.contains("GoLiveGram", ignoreCase = true) || video.platform.contains("GoLive", ignoreCase = true)
                            "⚡ Shorts" -> video.videoType.equals("SHORT", ignoreCase = true)
                            "🎬 Long" -> video.videoType.equals("LONG", ignoreCase = true)
                            "🌟 Spotlight" -> video.isBoosted
                            else -> true
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("reels_feed_list"),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredVideos, key = { it.id }) { video ->
                        CommunityVideoCard(
                            video = video,
                            isVip = isVip,
                            onWatchCompleted = {
                                onRewardVideoWatch(video.id, video.title)
                            },
                            onToggleFollow = {
                                onToggleFollow(video.id, video.creatorHandle)
                            },
                            onIncrementLike = {
                                onIncrementLike(video.id)
                            },
                            onOpenBoost = {
                                showBoostModalForVideo = video
                            },
                            onTriggerAd = triggerAd
                        )
                    }
                }

                // Universal Mediated Rewarded Ad + Permanent Banner Ad (BP_Banner_Android / BP_Rewarded_Android / AdMob)
                UniversalMediatedAdDock(
                    isVip = isVip,
                    featureLabel = "Viral Creator Reels Hub"
                )
            }

            // Creator Boost & Super Spotlight Modal
            showBoostModalForVideo?.let { targetVideo ->
                CreatorBoostSpotlightDialog(
                    video = targetVideo,
                    userCoins = userCoins,
                    userDiamonds = userDiamonds,
                    onBoostWithDiamonds = { cost ->
                        onBoostWithDiamonds(targetVideo.id, cost)
                        showBoostModalForVideo = null
                    },
                    onBoostWithCoins = { cost ->
                        onBoostWithCoins(targetVideo.id, cost)
                        showBoostModalForVideo = null
                    },
                    onBoostWithUpi = { planName, price, utr ->
                        onBoostWithUpi(targetVideo.id, planName, price, utr)
                        showBoostModalForVideo = null
                    },
                    onDismiss = { showBoostModalForVideo = null }
                )
            }

            // Link / Submit Video Modal
            if (showSubmitVideoModal) {
                SubmitVideoLinkDialog(
                    onSubmit = { title, desc, platform, type, url ->
                        onSubmitNewVideo(title, desc, platform, type, url)
                        showSubmitVideoModal = false
                    },
                    onDismiss = { showSubmitVideoModal = false }
                )
            }

            // Fallback Interstitial Ad Dialog
            if (showAdFallbackDialog) {
                InterstitialAdDialog(
                    interstitialUnitId = MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID,
                    onDismiss = {
                        showAdFallbackDialog = false
                        val action = pendingAdDismissAction
                        pendingAdDismissAction = null
                        action?.invoke()
                    }
                )
            }
        }
}

/**
 * Individual Community Video Card in Multi-Platform Feed
 * Handles:
 * - Follow button & engagement increment
 * - Interactive video player simulation with realistic duration progress
 * - Playback ads: 1 ad for short videos, 2 ads for long videos
 * - Rewards credit: +15 Coins & +3 Diamonds
 */
@Composable
private fun CommunityVideoCard(
    video: CommunityVideoPost,
    isVip: Boolean,
    onWatchCompleted: () -> Unit,
    onToggleFollow: () -> Unit,
    onIncrementLike: () -> Unit,
    onOpenBoost: () -> Unit,
    onTriggerAd: (milestoneName: String, onAfter: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember(video.id) { mutableStateOf(false) }
    var playbackProgressSeconds by remember(video.id) { mutableIntStateOf(0) }
    var isSessionRewardClaimed by remember(video.id) { mutableStateOf(false) }
    var midRollAdTriggered by remember(video.id) { mutableStateOf(false) }
    var hasLiked by remember(video.id) { mutableStateOf(false) }

    val totalDuration = if (video.videoType == "SHORT") 30 else 80
    val isLongVideo = video.videoType == "LONG"

    // High performance video playback coroutine (single loop, zero coroutine cancellation thrashing)
    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (isPlaying && playbackProgressSeconds < totalDuration) {
            delay(1000L)
            playbackProgressSeconds += 1

            // Long video feature: Trigger 1st ad at mid-point (50% progress)
            if (isLongVideo && !midRollAdTriggered && playbackProgressSeconds >= (totalDuration / 2)) {
                midRollAdTriggered = true
                isPlaying = false
                onTriggerAd("Reels Hub - Long Video Mid-Roll Ad (1/2)") {
                    isPlaying = true
                }
                break
            }

            // Completion check
            if (playbackProgressSeconds >= totalDuration && !isSessionRewardClaimed) {
                isPlaying = false
                isSessionRewardClaimed = true

                // Completion ad trigger:
                // 1 ad for short videos, 2nd ad for long videos
                val adLabel = if (isLongVideo) "Reels Hub - Long Video Completion Ad (2/2)" else "Reels Hub - Short Video Completion Ad"
                onTriggerAd(adLabel) {
                    onWatchCompleted()
                }
                break
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("video_post_card_${video.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            width = if (video.isBoosted) 1.8.dp else 1.dp,
            brush = if (video.isBoosted) {
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFF9900),
                        Color(0xFFFF1493),
                        NeonCyan
                    )
                )
            } else {
                androidx.compose.ui.graphics.SolidColor(DarkSurfaceBorder)
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // =============================================================
            // CREATOR ROW: AVATAR, NAME, PLATFORM BADGE, SPOTLIGHT & FOLLOW BUTTON
            // =============================================================
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
                                when (video.platform) {
                                    "YouTube" -> Color(0xFFFF0000)
                                    "Instagram" -> Color(0xFFE1306C)
                                    "GoLiveGram" -> Color(0xFF8A2BE2)
                                    else -> Color(0xFF3B82F6)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(video.creatorAvatar.ifBlank { "🎬" }, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = video.creatorName,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (video.isBoosted) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFF9900))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("SPOTLIGHT", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = video.creatorHandle,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Text(" • ", fontSize = 10.sp, color = Color.Gray)
                            // Platform Indicator
                            Text(
                                text = video.platform,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (video.platform) {
                                    "YouTube" -> Color(0xFFFF4D4D)
                                    "Instagram" -> Color(0xFFFF6699)
                                    "GoLiveGram" -> Color(0xFFBF77F6)
                                    else -> NeonCyan
                                }
                            )
                        }
                    }
                }

                // Interactive Follow Button
                Button(
                    onClick = onToggleFollow,
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("follow_creator_button_${video.id}"),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (video.isFollowing) Color(0xFF1E293B) else Color(0xFF2563EB)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    if (video.isFollowing) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Following", fontSize = 10.sp, color = Color.LightGray)
                    } else {
                        Text("+ Follow", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Description
            Text(
                text = video.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 17.sp
            )

            if (video.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = video.description,
                    fontSize = 10.5.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =============================================================
            // VIDEO PLAYER CANVAS SIMULATION (WITH LIVE PLAYBACK ENGINE)
            // =============================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF020617))
                        )
                    )
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .clickable { isPlaying = !isPlaying }
                    .testTag("video_player_box_${video.id}"),
                contentAlignment = Alignment.Center
            ) {
                // Background visual art for video reel
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B).copy(alpha = 0.85f))
                            .border(1.5.dp, if (isPlaying) NeonCyan else Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isPlaying) "Playing ${video.platform} Stream..." else "Tap to Play & Earn Rewards",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                // Top Badge: Platform & Video Duration Type
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${video.platform} ${if (isLongVideo) "• 2 Ads" else "• 1 Ad"}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF064E3B).copy(alpha = 0.85f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isSessionRewardClaimed) "✅ +15🪙 +3💎 Earned" else "+15🪙 & +3💎 on Finish",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DiamondCyan
                        )
                    }
                }

                // Bottom Progress Bar & Timer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "00:${playbackProgressSeconds.toString().padStart(2, '0')} / 00:${totalDuration.toString().padStart(2, '0')}",
                            fontSize = 9.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (isLongVideo) "Long Video" else "Short Reel",
                            fontSize = 9.sp,
                            color = Color.LightGray
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { (playbackProgressSeconds.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = NeonCyan,
                        trackColor = Color(0xFF334155)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =============================================================
            // ENGAGEMENT BAR: VIEWS, LIKES, CREATOR BOOST, SHARE & SOURCE
            // =============================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Views Count
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${video.viewsCount + if (playbackProgressSeconds > 5) 1 else 0} views",
                        fontSize = 10.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Likes Count & Interactive Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                if (!hasLiked) {
                                    hasLiked = true
                                    onIncrementLike()
                                }
                            }
                            .testTag("like_button_${video.id}")
                    ) {
                        Icon(
                            imageVector = if (hasLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (hasLiked) Color(0xFFE11D48) else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${video.likesCount + if (hasLiked) 1 else 0}",
                            fontSize = 10.sp,
                            color = if (hasLiked) Color(0xFFE11D48) else Color.LightGray
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Creator Boost Button
                    Button(
                        onClick = onOpenBoost,
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("boost_video_button_${video.id}"),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3730A3)),
                        contentPadding = PaddingValues(horizontal = 7.dp)
                    ) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Boost 🚀", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // External Social Intent Launcher (Watch on YouTube/Instagram/GoLiveGram)
                    IconButton(
                        onClick = {
                            val targetUrl = video.videoUrl.ifBlank {
                                when (video.platform) {
                                    "YouTube" -> "https://youtube.com"
                                    "Instagram" -> "https://instagram.com"
                                    "GoLiveGram" -> "https://golivegram.com"
                                    else -> MonetizationConstants.GLOBAL_ADMIN_BITLI_URL
                                }
                            }
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Opening ${video.platform}...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .testTag("open_external_social_button_${video.id}")
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = "Open Social", tint = NeonCyan, modifier = Modifier.size(13.dp))
                    }
                }
            }
        }
    }
}

/**
 * Creator Boost & Super Spotlight Dialog:
 * Second income stream for the app owner using UPI / Coins / Diamonds
 */
@Composable
private fun CreatorBoostSpotlightDialog(
    video: CommunityVideoPost,
    userCoins: Int,
    userDiamonds: Int,
    onBoostWithDiamonds: (diamonds: Int) -> Unit,
    onBoostWithCoins: (coins: Int) -> Unit,
    onBoostWithUpi: (planName: String, price: Int, utr: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedOption by remember { mutableStateOf("DIAMONDS") } // DIAMONDS, COINS, UPI
    var utrInput by remember { mutableStateOf("") }

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {} // Consume click
                )
                .testTag("creator_boost_dialog"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.horizontalGradient(listOf(Color(0xFFFF9900), Color(0xFFFF1493), NeonCyan))
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                                    .background(Color(0xFFFF9900)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Creator Boost & Super Spotlight",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "10X Reach Push across YouTube, Instagram & GoLiveGram",
                                    fontSize = 9.sp,
                                    color = GoldAccent
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Target Video: \"${video.title}\"",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 1: 50 Diamonds -> Super Spotlight
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = "DIAMONDS" },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedOption == "DIAMONDS") Color(0xFF1E1B4B) else Color(0xFF0F172A)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedOption == "DIAMONDS") NeonCyan else Color(0xFF334155)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("🌟 Super Spotlight Pin (3 Days)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Top feed placement + glowing neon border", fontSize = 9.sp, color = Color.Gray)
                            }
                            Text("50 💎", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = DiamondCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 2: 200 Coins -> Creator Boost
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = "COINS" },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedOption == "COINS") Color(0xFF1E1B4B) else Color(0xFF0F172A)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedOption == "COINS") GoldAccent else Color(0xFF334155)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("🚀 Creator Turbo Reach (5X Boost)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Multiplies algorithmic recommendations in community feed", fontSize = 9.sp, color = Color.Gray)
                            }
                            Text("200 🪙", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = GoldAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 3: Owner UPI Income Stream: ₹49 Super Spotlight
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = "UPI" },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedOption == "UPI") Color(0xFF2E1065) else Color(0xFF0F172A)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedOption == "UPI") Color(0xFFFF9900) else Color(0xFF334155)
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("⚡ Instant VIP Super Spotlight (₹49)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Pay to Owner UPI: ${MonetizationConstants.OWNER_UPI_ID}", fontSize = 9.sp, color = Color(0xFFFDE047))
                                }
                                Text("₹49 Only", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4ADE80))
                            }
                            if (selectedOption == "UPI") {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = utrInput,
                                    onValueChange = { utrInput = it },
                                    placeholder = { Text("Enter 12-digit UPI UTR / Ref Number", fontSize = 10.sp, color = Color.Gray) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("boost_upi_utr_input"),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFF9900),
                                        unfocusedBorderColor = DarkSurfaceBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF0F172A),
                                        unfocusedContainerColor = Color(0xFF0B1120)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Confirmation Button
                    Button(
                        onClick = {
                            when (selectedOption) {
                                "DIAMONDS" -> {
                                    if (userDiamonds < 50) {
                                        Toast.makeText(context, "You need 50 💎 (You have $userDiamonds 💎)", Toast.LENGTH_SHORT).show()
                                    } else {
                                        onBoostWithDiamonds(50)
                                    }
                                }
                                "COINS" -> {
                                    if (userCoins < 200) {
                                        Toast.makeText(context, "You need 200 🪙 (You have $userCoins 🪙)", Toast.LENGTH_SHORT).show()
                                    } else {
                                        onBoostWithCoins(200)
                                    }
                                }
                                "UPI" -> {
                                    if (utrInput.trim().length < 6) {
                                        Toast.makeText(context, "Please enter a valid 12-digit UPI UTR number", Toast.LENGTH_SHORT).show()
                                    } else {
                                        onBoostWithUpi("Instant VIP Super Spotlight", 49, utrInput.trim())
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("confirm_boost_spotlight_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900))
                    ) {
                        Text("Confirm & Launch Boost 🚀", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    }
                }
            }
        }
    }

/**
 * Submit / Link User Video to Multi-Platform Connected Community Feed
 */
@Composable
private fun SubmitVideoLinkDialog(
    onSubmit: (title: String, desc: String, platform: String, type: String, url: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("YouTube") }
    var videoType by remember { mutableStateOf("SHORT") }
    var url by remember { mutableStateOf("") }

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {} // Consume click
                )
                .testTag("submit_video_dialog"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Link / Submit Video 🎬", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Connected Platform", fontSize = 11.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("YouTube", "Instagram", "GoLiveGram").forEach { p ->
                            val isSel = platform == p
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF7C3AED) else Color(0xFF1E293B))
                                    .clickable { platform = p }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(p, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Video Type", fontSize = 11.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("SHORT" to "Short Reel (<60s)", "LONG" to "Long Video (>60s)").forEach { (t, label) ->
                            val isSel = videoType == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFFFF9900) else Color(0xFF1E293B))
                                    .clickable { videoType = t }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.Black else Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Video Title *", fontSize = 11.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Catchy title that grabs viewers", fontSize = 11.sp, color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_video_title_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0B1120)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Video URL / Link *", fontSize = 11.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        placeholder = { Text("https://youtube.com/shorts/... or instagram.com/reel/...", fontSize = 10.sp, color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_video_url_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0B1120)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (title.isBlank() || url.isBlank()) {
                                Toast.makeText(context, "Please enter both video title and URL link", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            onSubmit(title, description, platform, videoType, url)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_confirm_submit_video"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                    ) {
                        Text("Add Video to Community Feed (+10 🪙)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
}
