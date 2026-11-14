package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

/**
 * AI Tab Magic Button: "1-Click Trending AI Reel Creator"
 * Attractive Design matching exact user specification:
 * - Title: "✨ 1-Click Trending AI Reel Creator"
 * - Subtitle: "डालें अपना टाइटल और एक सेकंड में बनाएं धांसू AI वीडियो, स्क्रिप्ट और हैशटैग्स!"
 * - Action: "Open AI Reel Creator Magic Box"
 */
@Composable
fun AiReelCreatorMagicCard(
    onOpenMagicBox: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_reel_creator_magic_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(BrandPurple, NeonCyan))),
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
                    Text(
                        text = "✨ 1-Click Trending AI Reel Creator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldAccent.copy(alpha = 0.2f))
                        .border(1.dp, GoldAccent, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "FAST MAGIC",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "डालें अपना टाइटल और एक सेकंड में बनाएं धांसू AI वीडियो, स्क्रिप्ट और हैशटैग्स!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onOpenMagicBox,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("open_ai_reel_creator_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPurple,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Open AI Reel Creator Magic Box",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        }
    }
}

/**
 * AI Reel Creator Dialog with Title Input & 10-Second Ad Trigger
 * Matches exact prompt specification:
 * - Title: "🎬 AI Reel & Video Generator"
 * - Label: "Enter Video Title / Topic"
 * - Helper: "अपनी वीडियो का टाइटल या आइडिया यहाँ दर्ज करें:"
 * - Action buttons: "Generate AI Reel" & "Close"
 */
@Composable
fun AiReelCreatorDialog(
    videoTitleInput: String,
    onTitleChange: (String) -> Unit,
    generatedResult: String?,
    onGenerateClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sampleIdeas = listOf(
        "Top 5 AI Websites in 2026",
        "How to grow 100K Followers in 30 Days",
        "Secret Money Making App 2026",
        "Shocking Psychological Facts"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_reel_creator_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "🎬 AI Reel & Video Generator",
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
                    text = "अपनी वीडियो का टाइटल या आइडिया यहाँ दर्ज करें:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = videoTitleInput,
                    onValueChange = onTitleChange,
                    label = { Text("Enter Video Title / Topic") },
                    placeholder = { Text("e.g. AI Monetization Secrets") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("video_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = NeonCyan
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Fast-fill sample ideas chips
                Text(
                    text = "💡 Quick Ideas:",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(sampleIdeas) { idea ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { onTitleChange(idea) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = idea,
                                fontSize = 10.sp,
                                color = NeonCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Display generated AI result if available
                generatedResult?.let { res ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_generated_result_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Generated AI Reel Package",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = NeonCyan
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("AI Reel", res))
                                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = res,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onGenerateClick,
                    enabled = videoTitleInput.isNotBlank(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPurple,
                        disabledContainerColor = DarkSurfaceElevated
                    ),
                    modifier = Modifier.testTag("generate_ai_reel_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate AI Reel")
                }
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Text("Close", color = Color.LightGray)
                }
                UniversalMediatedAdDock(
                    isVip = false,
                    featureLabel = "1-Click AI Reel Creator"
                )
            }
        }
    )
}

/**
 * 10-Second Delay AdMob & Unity Ads Mediated Interstitial Ad Simulation for AI Feature
 */
@Composable
fun AiReelAdDelayModal(
    onSkipAdAndViewResult: () -> Unit
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
        label = "ad_progress"
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
            .testTag("admob_ai_10s_ad_modal"),
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
                        text = if (secondsRemaining > 0) "Reward unlocking in ${secondsRemaining}s..." else "Ad Complete ✓",
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
                            text = "📺 [AdMob Ad Triggered after 10 seconds inside AI Feature]\n\nSponsored Ad displayed successfully.",
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

                        // Live 10-Second Progress Indicator
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
                            text = if (secondsRemaining > 0) "Ad stream active • ${secondsRemaining}s remaining" else "Ready to unlock AI Result!",
                            fontSize = 10.5.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSkipAdAndViewResult,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (secondsRemaining <= 0) SuccessGreen else BrandPurple
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("skip_ad_and_view_ai_result_button")
            ) {
                Text(
                    text = "Skip Ad & View AI Result",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    )
}
