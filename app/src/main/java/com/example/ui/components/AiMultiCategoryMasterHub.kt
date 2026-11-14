package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * 🌟 STUNNING DASHBOARD POP-UP CARD:
 * 1-Click AI Trending Video Generator & Auto-Publisher
 * Matches exact user prompt:
 * - Title: "🔥 1-Click AI Trending Video Generator & Auto-Publisher"
 * - Subtitle: "✨ जादूई फीचर: बिना कुछ टाइप किए चुनें अपनी मनपसंद कैटेगरी (Comedy, Action, Vlogs, Sci-Fi आदि), और सीधे शेयर करें अपने सभी जुड़े हुए सोशल अकाउंट्स (FB, IG, YT, GoLiveGram) पर ऑटो-टाइटल और हैशटैग के साथ!"
 * - Action: "🚀 Launch AI Video Generator"
 */
@Composable
fun AiMultiCategoryMasterCard(
    onLaunchMasterHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.8f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_multi_category_master_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFFFF5722), GoldAccent))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "🔥 1-Click AI Trending Video Generator & Auto-Publisher",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Connected Platform Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val platforms = listOf("FB ✓", "IG ✓", "YT ✓", "GoLiveGram ✓")
                platforms.forEach { p ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1B1533).copy(alpha = 0.8f))
                            .border(0.8.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = p,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "✨ जादूई फीचर: बिना कुछ टाइप किए चुनें अपनी मनपसंद कैटेगरी (Comedy, Action, Vlogs, Sci-Fi आदि), और सीधे शेयर करें अपने सभी जुड़े हुए सोशल अकाउंट्स (FB, IG, YT, GoLiveGram) पर ऑटो-टाइटल और हैशटैग के साथ!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLaunchMasterHub,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("launch_ai_video_generator_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🚀 Launch AI Video Generator",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Master Hub Dialog with Multi-Category Selection
 * Matches exact prompt layout:
 * - Categories: Comedy & Fun, Action & Thrill, Vlogs & Lifestyle, Sci-Fi & Tech
 * - Connected Accounts: FB ✓ | IG ✓ | YT ✓ | GoLiveGram ✓
 * - Generate Video (Watch Ad) -> triggers 10s ad
 * - 1-Click Auto-Post to All Connected Socials
 * - Download to Phone Gallery
 */
@Composable
fun AiMultiCategoryMasterHubDialog(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    actionStatus: String?,
    videoReadyState: Boolean,
    onGenerateVideoClick: () -> Unit,
    onAutoPostAllClick: () -> Unit,
    onDownloadGalleryClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val categories = listOf(
        "Haryanvi Comedy & Rap" to "Haryanvi Rap",
        "Comedy & Fun" to "Comedy",
        "Action & Thrill" to "Action",
        "Vlogs & Lifestyle" to "Vlogs",
        "Sci-Fi & Tech" to "Sci-Fi"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_master_hub_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🎬 AI Trending Video Generator",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "अपनी कैटेगरी चुनें:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Category Selection Buttons inside Dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { (fullCat, label) ->
                        val isSelected = selectedCategory == fullCat
                        Button(
                            onClick = { onSelectCategory(fullCat) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("cat_button_$label")
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF13112A)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandPurple.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Active Selection: $selectedCategory",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Connected Accounts: FB ✓ | IG ✓ | YT ✓ | GoLiveGram ✓",
                            style = MaterialTheme.typography.bodySmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                actionStatus?.let { status ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E19)),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodySmall,
                            color = SuccessGreen,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (videoReadyState) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1938)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "✅ $selectedCategory Video Ready with Auto-Title, Tags & Hashtags!",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Auto-Tags: #${selectedCategory.replace(Regex("[^a-zA-Z0-9]"), "")} #ViralReels #Trending #GoLiveGram #Explore",
                                fontSize = 10.5.sp,
                                color = NeonCyan
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!videoReadyState) {
                    Button(
                        onClick = onGenerateVideoClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("generate_video_watch_ad_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate Video (Watch Ad)", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onAutoPostAllClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("auto_post_all_socials_button")
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🌐 1-Click Auto-Post to All Connected Socials", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }

                    Button(
                        onClick = onDownloadGalleryClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("download_phone_gallery_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📥 Download to Phone Gallery", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color.LightGray)
                }

                UniversalMediatedAdDock(
                    isVip = false,
                    featureLabel = "Multi-Category AI Hub ($selectedCategory)"
                )
            }
        }
    )
}

/**
 * 10-Second Delay AdMob & Unity Ads Mediated Interstitial Ad Simulation
 */
@Composable
fun AiMultiCategoryAdDelayModal(
    selectedCategory: String,
    onSkipAdAndViewOptions: () -> Unit
) {
    val mediationState by com.example.monetization.AdManager.mediationState.collectAsState()
    val lastDecision by com.example.monetization.AdManager.lastInterstitialDecision.collectAsState()
    var secondsRemaining by remember { mutableIntStateOf(10) }

    LaunchedEffect(Unit) {
        com.example.monetization.AdManager.selectNextMediatedAd(
            format = com.example.monetization.AdFormat.INTERSTITIAL,
            admobUnitId = com.example.monetization.AdManager.activeInterstitialAdUnitId,
            unityPlacementId = com.example.monetization.AdManager.activeUnityInterstitialPlacementId
        )
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
    }

    val unityGameId = mediationState.unityGameId.ifBlank { com.example.monetization.AdManager.DEFAULT_UNITY_GAME_ID }
    val unityPlacementId = mediationState.unityInterstitialPlacementId.ifBlank { com.example.monetization.AdManager.DEFAULT_UNITY_INTERSTITIAL_PLACEMENT_ID }
    val isUnitySelected = lastDecision?.selectedNetwork == com.example.monetization.AdNetwork.UNITY_ADS

    val progress by animateFloatAsState(
        targetValue = (10 - secondsRemaining) / 10f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "ad_progress_cat"
    )

    AlertDialog(
        onDismissRequest = {},
        containerColor = Color(0xFF130E26),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, GoldAccent.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
            .testTag("admob_multi_cat_10s_ad_modal"),
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
                        text = "AdMob Interstitial Ad (10s)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (secondsRemaining > 0) "Rendering '$selectedCategory' clip (${secondsRemaining}s)..." else "Rendering Complete ✓",
                        fontSize = 11.sp,
                        color = if (secondsRemaining > 0) GoldAccent else SuccessGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C081A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📺 [AdMob Ad Triggered during Video Generation]\n\nRendering trending clip for '$selectedCategory' with auto-tags...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isUnitySelected) {
                                "Unity Ads [$unityGameId] • Placement: $unityPlacementId (Active Rotation)"
                            } else {
                                "AdMob Active • Unity Fallback: $unityPlacementId (Game ID: $unityGameId)"
                            },
                            fontSize = 10.sp,
                            color = NeonCyan,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GoldAccent,
                            trackColor = Color(0xFF261D3E)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (secondsRemaining > 0) "Sponsored Ad active • ${secondsRemaining}s remaining" else "Ready to unlock AI Clip & Social Auto-Post!",
                            fontSize = 10.5.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSkipAdAndViewOptions,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (secondsRemaining <= 0) SuccessGreen else BrandPurple
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("skip_ad_and_view_options_button")
            ) {
                Text(
                    text = "Skip Ad & View Options",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    )
}
