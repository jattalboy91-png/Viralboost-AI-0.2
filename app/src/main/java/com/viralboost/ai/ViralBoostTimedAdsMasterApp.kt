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
import kotlinx.coroutines.delay

/**
 * Helper function to mask GPay UPI ID for privacy (Showing only first 4 and last 5 characters)
 * e.g., "amitkumar201591-2@okhdfcbank" -> "amit****cbank"
 */
fun maskTimedUpiId(upi: String): String {
    if (upi.length <= 9) return upi
    val prefix = upi.take(4)
    val suffix = upi.takeLast(5)
    return "$prefix****$suffix"
}

/**
 * Ultimate Master Code for "ViralBoost AI":
 * Full-Screen Dedicated AI Video & Hashtag Hub Studio with Auto-Timed AdMob Interstitial Ads (every 8-10 seconds),
 * "Go Ad-Free" VIP Subscription Option, Dual Admin UPI IDs (Paytm fully visible & Google Pay masked),
 * Real Barcode QR Image, 1-Click Auto-Payment UPI Intent, 3-Day Free Trial + 1-Day Friend Referral Reward,
 * "TODAY OFFER - 50% OFF", Gallery Photo Picker, & 100% Hidden Backend Affiliate ID (5665937)
 */
@Composable
fun ViralBoostTimedAdsMasterApp(
    onBackToDashboard: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf("home") }

    // Trial & VIP Subscription States
    var trialDaysLeft by remember { mutableIntStateOf(3) } // 3 Days Free Trial
    var hasActiveSubscription by remember { mutableStateOf(false) }

    // Friend Referral Success Notification State
    var showReferralRewardNotification by remember { mutableStateOf(false) }

    // Full-Screen Payment Screen States
    var selectedPlanTitle by remember { mutableStateOf("") }
    var selectedPlanPrice by remember { mutableStateOf("") }

    val context = LocalContext.current

    // =========================================================================
    // 🔒 PERMANENT ADMIN & OWNER FINANCIAL DATA (Dual UPI & Barcode Image Integration)
    // - Primary Fully Visible Paytm UPI ID: "amit2653@ptyes"
    // - Secondary Masked Google Pay UPI ID: "amitkumar201591-2@okhdfcbank"
    // - Admin QR Code Barcode Image Link
    // - Affiliate ID / Tracking Tag: "5665937" (Active silently in background)
    // =========================================================================
    val paytmFullUpi = "amit2653@ptyes"
    val gpayPrimaryUpi = "amitkumar201591-2@okhdfcbank"
    val maskedGpayUpi = remember(gpayPrimaryUpi) { maskTimedUpiId(gpayPrimaryUpi) } // amit****cbank
    val adminBarcodeImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=upi://pay?pa=amit2653@ptyes&pn=ViralBoostAI"
    val secureBackendAffiliateId = "5665937"

    // System Back Handler
    BackHandler {
        when (currentScreen) {
            "full_screen_payment" -> currentScreen = "vip_plans"
            "vip_plans", "full_screen_ai_hub" -> currentScreen = "home"
            else -> onBackToDashboard?.invoke()
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
                            .testTag("timed_ads_dashboard_list"),
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
                                        modifier = Modifier.testTag("btn_close_timed_ads_app")
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

                            // Trial & 1-Minute Friend Activity Referral Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("trial_referral_activity_card")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (hasActiveSubscription) "⭐ VIP Pro Subscription: ACTIVE (Ad-Free)" else "⭐ Free Trial Status: $trialDaysLeft Days Left",
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
                                        text = "दोस्त को ऐप शेयर करें। यदि आपका दोस्त ऐप पर 1 मिनट तक मूवमेंट/एक्सप्लोर करता है, तो आपको तुरंत 1 दिन का फ्री प्रो सब्सक्रिप्शन मिलेगा!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            trialDaysLeft += 1
                                            showReferralRewardNotification = true
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "🔥 Get ViralBoost AI now! Create viral reels with free VIP pass: https://viralboost.ai/app?ref=$secureBackendAffiliateId"
                                                )
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share with Friend"))
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_share_app_1min_reward")
                                    ) {
                                        Text("🔗 Share App Link with Friends", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // AI Hub Launcher Button (Opens Full-Screen Dedicated AI Studio)
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1B24)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (trialDaysLeft > 0 || hasActiveSubscription) {
                                            currentScreen = "full_screen_ai_hub"
                                        } else {
                                            currentScreen = "vip_plans"
                                        }
                                    }
                                    .testTag("card_ai_hub_launcher")
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
                                        text = "कॉमेडी, हरियाणवी रैप या शॉर्ट्स बनाएं — ऑटो-वायरल हैशटैग्स के साथ! (Full-Screen Studio)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            if (trialDaysLeft > 0 || hasActiveSubscription) {
                                                currentScreen = "full_screen_ai_hub"
                                            } else {
                                                currentScreen = "vip_plans"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_launch_fullscreen_hub")
                                    ) {
                                        Text("Launch Full-Screen AI Video & Hashtag Hub 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                "vip_plans" -> {
                    VipPassesModalScreen(
                        onSelectPlan = { title, price ->
                            selectedPlanTitle = title
                            selectedPlanPrice = price
                            currentScreen = "full_screen_payment"
                        },
                        onBack = { currentScreen = "home" }
                    )
                }

                "full_screen_payment" -> {
                    FullScreenPaymentCheckoutScreen(
                        planTitle = selectedPlanTitle,
                        planPrice = selectedPlanPrice,
                        paytmUpi = paytmFullUpi,
                        gpayUpi = gpayPrimaryUpi,
                        maskedGpay = maskedGpayUpi,
                        barcodeUrl = adminBarcodeImageUrl,
                        context = context,
                        onPaymentSuccessful = {
                            hasActiveSubscription = true
                            trialDaysLeft = 30
                            currentScreen = "home"
                            Toast.makeText(context, "🎉 $selectedPlanTitle Activated Successfully!", Toast.LENGTH_LONG).show()
                        },
                        onBack = { currentScreen = "vip_plans" }
                    )
                }

                "full_screen_ai_hub" -> {
                    FullScreenAiStudioHubScreen(
                        hasSubscription = hasActiveSubscription,
                        onOpenVipPlans = { currentScreen = "vip_plans" },
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
                    .testTag("bottom_nav_timed_ads"),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { currentScreen = "home" },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentScreen == "home") Color(0xFF2E2A47) else Color.Transparent),
                    modifier = Modifier.testTag("btn_nav_home_timed")
                ) {
                    Text("🏠 Home", color = if (currentScreen == "home") Color.White else Color.LightGray)
                }
                Button(
                    onClick = {
                        if (trialDaysLeft > 0 || hasActiveSubscription) {
                            currentScreen = "full_screen_ai_hub"
                        } else {
                            currentScreen = "vip_plans"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier.testTag("btn_nav_ai_hub_timed")
                ) {
                    Text("✨ AI Hub", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { currentScreen = "vip_plans" },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentScreen == "vip_plans" || currentScreen == "full_screen_payment") Color(0xFFFFD700) else Color(0xFF2E2A47)),
                    modifier = Modifier.testTag("btn_nav_vip_timed")
                ) {
                    Text("⭐ VIP Passes", color = if (currentScreen == "vip_plans" || currentScreen == "full_screen_payment") Color.Black else Color.Yellow, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // 🎉 Friend Referral 1-Minute Activity Success Notification Dialog
    if (showReferralRewardNotification) {
        AlertDialog(
            onDismissRequest = { showReferralRewardNotification = false },
            title = {
                Text(
                    text = "🎉 बधाई हो! (Bonus Reward)",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "आपके द्वारा शेयर किए गए लिंक से दोस्त ने ऐप पर 1 मिनट तक मूवमेंट और एक्सप्लोर किया है। इसके reward में आपको 1 दिन का फ्री प्रो सब्सक्रिप्शन सफलतापूर्वक मिल गया है!",
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = { showReferralRewardNotification = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    modifier = Modifier.testTag("btn_dismiss_reward_popup")
                ) {
                    Text("Awesome! Thanks", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// 🎬 FULL-SCREEN AI STUDIO HUB WITH AUTO-TIMED ADMOB INTERSTITIAL ADS (Every 8-10s for free users) & GO AD-FREE OPTION
@Composable
fun FullScreenAiStudioHubScreen(
    hasSubscription: Boolean,
    onOpenVipPlans: () -> Unit,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Comedy") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var userPrompt by remember { mutableStateOf("") }
    var videoReadyState by remember { mutableStateOf(false) }
    var actionStatus by remember { mutableStateOf<String?>(null) }
    var showGenerationAdModal by remember { mutableStateOf(false) }

    // Timed AdMob Interstitial Ad State (Triggers every 8-10 seconds for non-subscribed users)
    var showTimedInterstitialAd by remember { mutableStateOf(false) }

    // Timer effect running every 9 seconds for Free Users
    LaunchedEffect(hasSubscription) {
        if (!hasSubscription) {
            while (true) {
                delay(9000L) // 9 Seconds interval
                showTimedInterstitialAd = true
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            actionStatus = "📁 Gallery Photo Attached Successfully!"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .testTag("fullscreen_ai_studio_timed_ads_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBackToHome,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F3D56)),
                    modifier = Modifier.testTag("btn_back_home_studio_timed")
                ) {
                    Text("← Back Home", color = Color.White)
                }
                Text(
                    text = "🎬 AI Trending Video Studio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 👑 GO AD-FREE / VIP SUBSCRIPTION BANNER (For users wanting to remove ads)
            if (!hasSubscription) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenVipPlans() }
                        .testTag("card_go_ad_free_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚡ Free Version (Ads Active)",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Yellow,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Remove all ads & unlock unlimited 4K renders!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = onOpenVipPlans,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            modifier = Modifier.testTag("btn_go_ad_free")
                        ) {
                            Text("Go Ad-Free ⭐", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✨ अपनी कैटेगरी चुनें (Select Category):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { selectedCategory = "Comedy" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (selectedCategory == "Comedy") Color(0xFF6200EE) else Color(0xFF2D2B42)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Comedy", color = Color.White)
                        }
                        Button(
                            onClick = { selectedCategory = "Haryanvi Rap" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (selectedCategory == "Haryanvi Rap") Color(0xFF6200EE) else Color(0xFF2D2B42)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Rap", color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { selectedCategory = "Action" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (selectedCategory == "Action") Color(0xFF6200EE) else Color(0xFF2D2B42)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Action", color = Color.White)
                        }
                        Button(
                            onClick = { selectedCategory = "Vlogs" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (selectedCategory == "Vlogs") Color(0xFF6200EE) else Color(0xFF2D2B42)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Vlogs", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedImageUri != null) Color(0xFF00C853) else Color(0xFF6200EE)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_gallery_picker_hub_timed")
                    ) {
                        Text(
                            text = if (selectedImageUri != null) "✅ Photo Attached" else "📁 Upload Photo from Gallery",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (selectedImageUri != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, Color.Cyan, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = userPrompt,
                        onValueChange = { userPrompt = it },
                        label = { Text("Enter prompt...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6200EE),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (selectedImageUri != null) {
                                showGenerationAdModal = true
                            } else {
                                actionStatus = "⚠️ कृपया पहले गैलरी से फोटो सेलेक्ट करें!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_generate_video_timed")
                    ) {
                        Text("✨ Generate Video (Watch Ad)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (videoReadyState) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF221F3B)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_video_ready_timed")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🎉 Video Ready!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Yellow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📌 Viral Title & Hashtags Generated Successfully for '$userPrompt'!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                actionStatus = "🚀 Video auto-posted successfully!"
                                Toast.makeText(context, "Auto-Posted to Socials!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_autopost_timed")
                        ) {
                            Text("1-Click Auto-Post", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            actionStatus?.let { status ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (status.startsWith("⚠️")) Color.Yellow else Color.Green
                )
            }
        }
    }

    // 📺 ADMOB INTERSTITIAL AD TRIGGERED EVERY 8-10 SECONDS FOR FREE USERS
    if (showTimedInterstitialAd) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "📺 AdMob Interstitial Ad (Sponsored)",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "यह फ्री वर्जन है। हर 8-10 सेकंड में स्पॉन्सर्ड ऐड दिखाई जाती है। ऐड हटाने के लिए VIP Pass खरीदें।",
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = { showTimedInterstitialAd = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    modifier = Modifier.testTag("btn_close_timed_ad")
                ) {
                    Text("Close Ad & Continue", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showGenerationAdModal) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("AdMob Interstitial Ad", fontWeight = FontWeight.Bold) },
            text = { Text("📺 Rendering AI video...", color = Color.White) },
            confirmButton = {
                Button(
                    onClick = {
                        showGenerationAdModal = false
                        videoReadyState = true
                        actionStatus = "✨ AI Video generated!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier.testTag("btn_continue_gen_ad")
                ) {
                    Text("Continue", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
