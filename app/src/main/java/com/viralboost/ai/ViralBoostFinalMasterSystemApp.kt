package com.viralboost.ai

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * Ultimate Master Code for "ViralBoost AI":
 * 100% Paid AI Ecosystem (Home Hub Button + All AI Tools VIP Locked),
 * 3-Day Free Trial + 1-Day Friend Referral Reward,
 * Gorgeous "TODAY OFFER - 50% OFF" Neon Boxes,
 * ₹79 Special Offer & ₹179 Pro Passes,
 * Gallery Photo Picker, AdMob Ads,
 * & 100% Hidden Backend Affiliate ID (5665937)
 */
@Composable
fun ViralBoostFinalMasterSystemApp(
    onBackToDashboard: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf("home") }
    var selectedAiTool by remember { mutableStateOf("") }

    // Trial & VIP Subscription States
    var trialDaysLeft by remember { mutableIntStateOf(3) } // 3 Days Free Trial
    var hasActiveSubscription by remember { mutableStateOf(false) }

    // SECURE HIDDEN BACKEND AFFILIATE ID (Active silently for commissions)
    val secureBackendAffiliateId = "5665937"

    // System Back Handler
    BackHandler {
        if (currentScreen != "home") {
            currentScreen = "home"
        } else {
            onBackToDashboard?.invoke()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0F0E17)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when (currentScreen) {
                "home" -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("master_system_dashboard_list"),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⭐ ViralBoost AI Dashboard",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White
                                )
                                if (onBackToDashboard != null) {
                                    IconButton(
                                        onClick = { onBackToDashboard() },
                                        modifier = Modifier.testTag("btn_close_master_system_app")
                                    ) {
                                        Text(text = "✕", color = Color.Gray, fontSize = 20.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // 🔥 DASHBOARD "TODAY OFFER - 50% OFF" ATTRACTIVE BOX
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C)),
                                border = BorderStroke(2.dp, Color(0xFFFFD700)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { currentScreen = "vip_plans" }
                                    .testTag("dashboard_today_offer_card")
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🔥 TODAY OFFER - 50% OFF 🔥",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "AI Studio Pro Pass मात्र ₹79 में या Special Starter Pass ₹49 में पाएं!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(
                                        onClick = { currentScreen = "vip_plans" },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                        modifier = Modifier.testTag("btn_claim_offer_now")
                                    ) {
                                        Text("Claim Offer Now ⭐", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Trial & Referral Status Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("trial_referral_status_card")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (hasActiveSubscription) "⭐ VIP Pro Subscription: ACTIVE" else "⭐ Free Trial Status: $trialDaysLeft Days Left",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Color.Yellow,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (hasActiveSubscription || trialDaysLeft > 0) Color(0xFF00C853) else Color.Red
                                        ) {
                                            Text(
                                                text = if (hasActiveSubscription || trialDaysLeft > 0) "UNLOCKED ✓" else "LOCKED 🔒",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "सभी AI टूल्स और हब पेड हैं। दोस्त को शेयर करें और 2 मिनट रुकने पर 1 दिन का एक्स्ट्रा फ्री एक्सेस पाएं!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            trialDaysLeft += 1
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "🔥 Get ViralBoost AI! Create viral reels with 100% free VIP pass: https://viralboost.ai/app?ref=$secureBackendAffiliateId"
                                                )
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share with Friend"))
                                            Toast.makeText(context, "🎉 Friend Link Copied! +1 Day Free Added!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_share_app_with_friend")
                                    ) {
                                        Text("🔗 Share App with Friend (Unlock +1 Day Free)", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // AI Tool 1: Home Dashboard "Launch AI Video & Hashtag Hub" (VIP Locked)
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1B24)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (trialDaysLeft > 0 || hasActiveSubscription) {
                                            selectedAiTool = "1-Click AI Video & Viral Hashtag Generator"
                                            currentScreen = "ai_tool_workspace"
                                        } else {
                                            currentScreen = "vip_plans"
                                        }
                                    }
                                    .testTag("card_ai_hub_tool_1click")
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🔥 1-Click AI Video & Viral Hashtag Generator",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Yellow,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            imageVector = if (trialDaysLeft > 0 || hasActiveSubscription) Icons.Default.CheckCircle else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (trialDaysLeft > 0 || hasActiveSubscription) Color.Green else Color.Red,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "कॉमेडी, हरियाणवी रैप या शॉर्ट्स बनाएं — ऑटो-वायरल हैशटैग्स के साथ! (VIP Locked)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            if (trialDaysLeft > 0 || hasActiveSubscription) {
                                                selectedAiTool = "1-Click AI Video & Viral Hashtag Generator"
                                                currentScreen = "ai_tool_workspace"
                                            } else {
                                                currentScreen = "vip_plans"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_launch_ai_hub")
                                    ) {
                                        Text("Launch AI Video & Hashtag Hub 🚀 (VIP)", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // AI Tool 2: AI Trending Video Studio
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1B24)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (trialDaysLeft > 0 || hasActiveSubscription) {
                                            selectedAiTool = "AI Trending Video Studio"
                                            currentScreen = "ai_tool_workspace"
                                        } else {
                                            currentScreen = "vip_plans"
                                        }
                                    }
                                    .testTag("card_ai_studio_tool")
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "✨ AI Trending Video Studio (VIP Locked)",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Cyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            imageVector = if (trialDaysLeft > 0 || hasActiveSubscription) Icons.Default.CheckCircle else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (trialDaysLeft > 0 || hasActiveSubscription) Color.Green else Color.Red,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Comedy, Tech, Motivation, Desi Rap, Gaming & Fashion! Create viral 9:16 reels.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }

                        // AI Tool 3: Photo-to-Video & Face-Swap AI
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1B24)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (trialDaysLeft > 0 || hasActiveSubscription) {
                                            selectedAiTool = "Photo-to-Video & Face-Swap AI"
                                            currentScreen = "ai_tool_workspace"
                                        } else {
                                            currentScreen = "vip_plans"
                                        }
                                    }
                                    .testTag("card_ai_faceswap_tool")
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📸 Photo-to-Video & Face-Swap AI (VIP Locked)",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color(0xFFFF4081),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            imageVector = if (trialDaysLeft > 0 || hasActiveSubscription) Icons.Default.CheckCircle else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (trialDaysLeft > 0 || hasActiveSubscription) Color.Green else Color.Red,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "गैलरी से फोटो लगाएं और सीधे वायरल रील में अपना चेहरा या मोशन लाएं।",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }

                "vip_plans" -> {
                    VipPassesModalScreen(
                        onPassPurchased = {
                            hasActiveSubscription = true
                            trialDaysLeft = 30
                            currentScreen = "home"
                        },
                        onBack = { currentScreen = "home" }
                    )
                }

                "ai_tool_workspace" -> {
                    AiToolWorkspaceScreen(
                        toolName = selectedAiTool,
                        onBackToHome = { currentScreen = "home" }
                    )
                }
            }

            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E2F), RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .testTag("master_bottom_nav_bar"),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { currentScreen = "home" },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentScreen == "home") Color(0xFF2E2A47) else Color.Transparent),
                    modifier = Modifier.testTag("master_btn_nav_home")
                ) {
                    Text("🏠 Home", color = if (currentScreen == "home") Color.White else Color.LightGray)
                }
                Button(
                    onClick = {
                        if (trialDaysLeft > 0 || hasActiveSubscription) {
                            selectedAiTool = "AI Trending Video Studio"
                            currentScreen = "ai_tool_workspace"
                        } else {
                            currentScreen = "vip_plans"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier.testTag("master_btn_nav_ai_boost")
                ) {
                    Text("✨ AI Boost", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { currentScreen = "vip_plans" },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentScreen == "vip_plans") Color(0xFFFFD700) else Color(0xFF2E2A47)),
                    modifier = Modifier.testTag("master_btn_nav_vip_passes")
                ) {
                    Text("⭐ VIP Passes", color = if (currentScreen == "vip_plans") Color.Black else Color.Yellow, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
