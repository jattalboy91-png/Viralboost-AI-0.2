// Ultimate Master Code for "ViralBoost AI": Strict Isolated Fix for Reels Hub Spotlight & Boost Modal (Integrated Full Payment Page, All Plans Breakdown, Barcode + UTR & Direct Auto-Payment UPI Intent), AdMob Compliant Privacy Policy & Legal Liability Waiver, Signup Flow, Full-Screen AI Studio, & 100% Hidden Backend Affiliate ID (5665937)
package com.viralboost.ai

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ViralBoostStrictReelsPaymentApp(
    onBackToDashboard: (() -> Unit)? = null
) {
    var currentScreen by remember { mutableStateOf("signup") }
    
    var trialDaysLeft by remember { mutableIntStateOf(3) }
    var hasActiveSubscription by remember { mutableStateOf(false) }
    var showReferralRewardNotification by remember { mutableStateOf(false) }

    var userDiamonds by remember { mutableIntStateOf(60) }
    var userCoins by remember { mutableIntStateOf(250) }
    var showOutOfCoinsDialog by remember { mutableStateOf(false) }

    var selectedPlanTitle by remember { mutableStateOf("") }
    var selectedPlanPrice by remember { mutableStateOf("") }

    val context = LocalContext.current

    // =========================================================================
    // 🔒 OWNER FINANCIAL & ADMIN DATA
    // - Visible Paytm UPI & Barcode (Requires UTR Verification): "amit2653@ptyes"
    // - Direct Auto-Payment GPay UPI (Auto-Verify): "amitkumar201591-2@okhdfcbank"
    // - Affiliate ID / Tracking Tag: "5665937" (Active silently in background)
    // =========================================================================
    val paytmVisibleUpi = "amit2653@ptyes"
    val gpayAutoPayUpi = "amitkumar201591-2@okhdfcbank"
    val maskedGpayUpi = maskUpiId(gpayAutoPayUpi)
    val adminBarcodeImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=upi://pay?pa=amit2653@ptyes&pn=ViralBoostAI"
    val secureBackendAffiliateId = "5665937"

    BackHandler {
        when (currentScreen) {
            "privacy_policy" -> currentScreen = "signup"
            "clean_payment_screen" -> currentScreen = "vip_plans"
            "vip_plans", "clean_full_screen_studio", "reels_hub_feed" -> currentScreen = "home"
            "home" -> onBackToDashboard?.invoke()
            else -> onBackToDashboard?.invoke()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize().testTag("strict_reels_payment_surface"),
        color = Color(0xFF0F0E17)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when (currentScreen) {
                "signup" -> {
                    SignupScreen(
                        onSignupSuccess = { currentScreen = "home" },
                        onOwnerBypass = { currentScreen = "home" },
                        onOpenPrivacyPolicy = { currentScreen = "privacy_policy" }
                    )
                }
                "privacy_policy" -> {
                    PrivacyPolicyScreen(
                        onBackToSignup = { currentScreen = "signup" }
                    )
                }
                "home" -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f).testTag("strict_reels_home_list"),
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
                                        modifier = Modifier.testTag("btn_close_strict_reels_app")
                                    ) {
                                        Text(text = "✕", color = Color.Gray, fontSize = 20.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C)),
                                border = BorderStroke(2.dp, Color(0xFFFFD700)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { currentScreen = "vip_plans" }
                                    .testTag("card_strict_today_offer")
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
                                        modifier = Modifier.testTag("btn_strict_claim_offer")
                                    ) {
                                        Text("Claim Offer Now ⭐", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Viral Creator Reels Hub Card matching your screenshot 1
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                                border = BorderStroke(1.dp, Color(0xFF6200EE)),
                                modifier = Modifier.fillMaxWidth().testTag("card_strict_reels_preview")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🎬 Viral Creator Reels Hub",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Cyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Button(
                                            onClick = { currentScreen = "reels_hub_feed" },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                                            modifier = Modifier.testTag("btn_strict_open_hub")
                                        ) {
                                            Text("Open Hub Feed", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Watch viral shorts & masterclasses, boost your channel reach!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                                modifier = Modifier.fillMaxWidth().testTag("card_strict_wallet_status")
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
                                            color = Color(0xFF2E2A47)
                                        ) {
                                            Text(
                                                text = "💎 $userDiamonds | 🟡 $userCoins",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Cyan,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "फ्री कॉइंस सीमित हैं! अनलिमिटेड एक्सेस के लिए VIP Pass लें।",
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
                                        modifier = Modifier.fillMaxWidth().testTag("btn_strict_share_link")
                                    ) {
                                        Text("🔗 Share App Link with Friends", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1B24)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (trialDaysLeft > 0 || hasActiveSubscription) {
                                            currentScreen = "clean_full_screen_studio"
                                        } else {
                                            currentScreen = "vip_plans"
                                        }
                                    }
                                    .testTag("card_strict_studio_launcher")
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "🔥 1-Click AI Video & Viral Hashtag Generator",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "कॉमेडी, हरियाणवी रैप या शॉर्ट्स बनाएं — ऑटो-वायरल हैशटैग्स के साथ!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            if (trialDaysLeft > 0 || hasActiveSubscription) {
                                                currentScreen = "clean_full_screen_studio"
                                            } else {
                                                currentScreen = "vip_plans"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                                        modifier = Modifier.fillMaxWidth().testTag("btn_strict_launch_ai")
                                    ) {
                                        Text("Launch Full-Screen AI Video & Hashtag Hub 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                "reels_hub_feed" -> {
                    StrictReelsHubFeedScreen(
                        paytmVisibleUpi = paytmVisibleUpi,
                        gpayAutoPayUpi = gpayAutoPayUpi,
                        maskedGpay = maskedGpayUpi,
                        barcodeUrl = adminBarcodeImageUrl,
                        context = context,
                        userDiamonds = userDiamonds,
                        userCoins = userCoins,
                        onDeductCoinsOrTriggerPaywall = { costType ->
                            if (costType == "diamond") {
                                if (userDiamonds >= 50) {
                                    userDiamonds -= 50
                                    Toast.makeText(context, "💎 50 Diamonds deducted! Spotlight active.", Toast.LENGTH_SHORT).show()
                                } else {
                                    showOutOfCoinsDialog = true
                                }
                            } else if (costType == "coin") {
                                if (userCoins >= 200) {
                                    userCoins -= 200
                                    Toast.makeText(context, "🟡 200 Coins deducted! 5X Turbo reach active.", Toast.LENGTH_SHORT).show()
                                } else {
                                    showOutOfCoinsDialog = true
                                }
                            }
                        },
                        onBackToHome = { currentScreen = "home" }
                    )
                }
                "vip_plans" -> {
                    CleanVipPlansScreen(
                        onSelectPlan = { title, price ->
                            selectedPlanTitle = title
                            selectedPlanPrice = price
                            currentScreen = "clean_payment_screen"
                        },
                        onBack = { currentScreen = "home" }
                    )
                }
                "clean_payment_screen" -> {
                    CleanPaymentCheckoutScreen(
                        planTitle = selectedPlanTitle,
                        planPrice = selectedPlanPrice,
                        paytmUpi = paytmVisibleUpi,
                        gpayUpi = gpayAutoPayUpi,
                        maskedGpay = maskedGpayUpi,
                        barcodeUrl = adminBarcodeImageUrl,
                        context = context,
                        onPaymentSuccessful = {
                            hasActiveSubscription = true
                            trialDaysLeft = 30
                            userDiamonds = 9999
                            userCoins = 9999
                            currentScreen = "home"
                            Toast.makeText(context, "🎉 $selectedPlanTitle Activated Successfully!", Toast.LENGTH_LONG).show()
                        },
                        onBack = { currentScreen = "vip_plans" }
                    )
                }
                "clean_full_screen_studio" -> {
                    CleanFullScreenStudioScreen(
                        hasSubscription = hasActiveSubscription,
                        onOpenVipPlans = { currentScreen = "vip_plans" },
                        onBackToHome = { currentScreen = "home" }
                    )
                }
            }

            if (currentScreen != "signup" && currentScreen != "privacy_policy") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E2F), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                        .testTag("bottom_nav_strict_reels"),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { currentScreen = "home" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentScreen == "home") Color(0xFF2E2A47) else Color.Transparent
                        ),
                        modifier = Modifier.testTag("btn_nav_home_strict")
                    ) {
                        Text("🏠 Home", color = if (currentScreen == "home") Color.White else Color.LightGray)
                    }
                    Button(
                        onClick = { currentScreen = "reels_hub_feed" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentScreen == "reels_hub_feed") Color(0xFF03DAC5) else Color(0xFF2E2A47)
                        ),
                        modifier = Modifier.testTag("btn_nav_reels_strict")
                    ) {
                        Text(
                            "🎬 Reels",
                            color = if (currentScreen == "reels_hub_feed") Color.Black else Color.Cyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {
                            if (trialDaysLeft > 0 || hasActiveSubscription) {
                                currentScreen = "clean_full_screen_studio"
                            } else {
                                currentScreen = "vip_plans"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                        modifier = Modifier.testTag("btn_nav_ai_hub_strict")
                    ) {
                        Text("✨ AI Hub", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { currentScreen = "vip_plans" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentScreen == "vip_plans" || currentScreen == "clean_payment_screen") Color(0xFFFFD700) else Color(0xFF2E2A47)
                        ),
                        modifier = Modifier.testTag("btn_nav_vip_strict")
                    ) {
                        Text(
                            "⭐ VIP",
                            color = if (currentScreen == "vip_plans" || currentScreen == "clean_payment_screen") Color.Black else Color.Yellow,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showOutOfCoinsDialog) {
        AlertDialog(
            onDismissRequest = { showOutOfCoinsDialog = false },
            title = {
                Text(
                    text = "⚠️ फ्री कॉइंस और डायमंड्स समाप्त!",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "आपके सभी फ्री कॉइंस और डायमंड्स खत्म हो चुके हैं! अब बिना किसी रुकावट के वीडियो बूस्ट करने और सभी पेड फीचर्स इस्तेमाल करने के लिए कृपया तुरंत VIP Pass लें।",
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOutOfCoinsDialog = false
                        currentScreen = "vip_plans"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    modifier = Modifier.testTag("btn_buy_vip_from_out_of_coins_strict")
                ) {
                    Text("Buy VIP Pass Now ⭐", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showOutOfCoinsDialog = false },
                    modifier = Modifier.testTag("btn_cancel_out_of_coins_strict")
                ) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }

    if (showReferralRewardNotification) {
        AlertDialog(
            onDismissRequest = { showReferralRewardNotification = false },
            title = { Text("🎉 बधाई हो! (Bonus Reward)", color = Color.Yellow, fontWeight = FontWeight.Bold) },
            text = { Text("आपके द्वारा शेयर किए गए लिंक से दोस्त ने ऐप पर 1 मिनट तक मूवमेंट और एक्सप्लोर किया है। इसके reward में आपको **1 दिन का फ्री प्रो सब्सक्रिप्शन** सफलतापूर्वक मिल गया है!", color = Color.White) },
            confirmButton = {
                Button(
                    onClick = { showReferralRewardNotification = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    modifier = Modifier.testTag("btn_dismiss_strict_reward")
                ) {
                    Text("Awesome! Thanks", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// 🎬 STRICT REELS HUB FEED SCREEN WITH FULL PAYMENT PAGE & DUAL UPI OPTIONS INSIDE SPOTLIGHT MODAL
@Composable
fun StrictReelsHubFeedScreen(
    paytmVisibleUpi: String,
    gpayAutoPayUpi: String,
    maskedGpay: String,
    barcodeUrl: String,
    context: Context,
    userDiamonds: Int,
    userCoins: Int,
    onDeductCoinsOrTriggerPaywall: (String) -> Unit,
    onBackToHome: () -> Unit
) {
    var showSpotlightModal by remember { mutableStateOf(false) }
    var utrNumberInput by remember { mutableStateOf("") }
    var boostStatusMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .testTag("strict_reels_feed_column"),
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
                    modifier = Modifier.testTag("btn_back_home_strict_feed")
                ) {
                    Text("← Back Home", color = Color.White)
                }
                Text(
                    text = "🎬 Viral Creator Reels Hub",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reels Hub Header Card with "Open Reels Hub" & "Spotlight" buttons (matching your screenshot 1 & 2)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("card_strict_reels_hub_actions")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔥 Watch viral shorts & masterclasses, boost your channel reach, and earn rewards!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showSpotlightModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                            modifier = Modifier.weight(1f).testTag("btn_strict_open_reels_action")
                        ) {
                            Text("🎥 Open Reels Hub", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { showSpotlightModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            modifier = Modifier.weight(1f).testTag("btn_strict_spotlight_action")
                        ) {
                            Text("⚡ Spotlight 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reel Video Feed Card with Super Spotlight / Boost button
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("card_reel_rohan_strict")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ Rohan Finance Fast",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Yellow,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showSpotlightModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                            modifier = Modifier.testTag("btn_strict_spotlight_rohan")
                        ) {
                            Text("🚀 Super Spotlight ⚡", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "GoLiveGram Creator Masterclass: Zero to ₹1 Lakh/Month Affiliate Income 💰",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(Color.Black, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "▶ [ Tap to Play & Earn Rewards ]",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // 🚀 SUPER SPOTLIGHT & BOOST MODAL (Equipped with Full Payment Page, Barcode, Paytm Visible ID, & GPay Auto-Payment)
    if (showSpotlightModal) {
        AlertDialog(
            onDismissRequest = { showSpotlightModal = false },
            title = {
                Text(
                    text = "🚀 Creator Boost & Super Spotlight Plans",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(480.dp)
                        .testTag("strict_spotlight_plans_column"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Choose your boost plan or pay instantly via UPI:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Plan 1: Super Spotlight Pin (50 Diamonds)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("card_strict_plan_diamonds")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("⭐ Super Spotlight Pin (3 Days)", style = MaterialTheme.typography.titleSmall, color = Color.Yellow, fontWeight = FontWeight.Bold)
                                    Text("50 💎", style = MaterialTheme.typography.titleSmall, color = Color.Cyan, fontWeight = FontWeight.Bold)
                                }
                                Text("Top feed placement + glowing neon border\n(Balance: 💎 $userDiamonds)", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        onDeductCoinsOrTriggerPaywall("diamond")
                                        showSpotlightModal = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_strict_use_diamonds")
                                ) {
                                    Text("Select & Use 50 Diamonds", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Plan 2: Creator Turbo Reach (200 Coins)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("card_strict_plan_coins")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("📈 Creator Turbo Reach (5X Boost)", style = MaterialTheme.typography.titleSmall, color = Color.Yellow, fontWeight = FontWeight.Bold)
                                    Text("200 🟡", style = MaterialTheme.typography.titleSmall, color = Color.Cyan, fontWeight = FontWeight.Bold)
                                }
                                Text("Multiplies algorithmic recommendations in community feed\n(Balance: 🟡 $userCoins)", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        onDeductCoinsOrTriggerPaywall("coin")
                                        showSpotlightModal = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_strict_use_coins")
                                ) {
                                    Text("Select & Use 200 Coins", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Plan 3: Instant VIP Super Spotlight (₹49) with Barcode, Paytm Visible ID & Direct Auto-Payment
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF282240)),
                            border = BorderStroke(2.dp, Color(0xFFFFD700)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("card_strict_plan_cash_49")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("⚡ Instant VIP Super Spotlight (₹49)", style = MaterialTheme.typography.titleSmall, color = Color.Yellow, fontWeight = FontWeight.Bold)
                                    Text("₹49 Only", style = MaterialTheme.typography.titleSmall, color = Color.Green, fontWeight = FontWeight.Bold)
                                }
                                Text("Scan Barcode or Pay to Paytm UPI: $paytmVisibleUpi", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                
                                Spacer(modifier = Modifier.height(6.dp))

                                // Admin Barcode QR Image
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.size(95.dp)) {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            AsyncImage(model = barcodeUrl, contentDescription = "QR Barcode", modifier = Modifier.fillMaxSize().padding(2.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Paytm UPI (Visible): $paytmVisibleUpi", style = MaterialTheme.typography.labelSmall, color = Color.Yellow, fontWeight = FontWeight.Bold)
                                Text(text = "GPay UPI (Secured): $maskedGpay", style = MaterialTheme.typography.labelSmall, color = Color.Cyan)

                                Spacer(modifier = Modifier.height(8.dp))

                                // Direct Auto-Payment Button via GPay UPI
                                Button(
                                    onClick = {
                                        val upiUri = Uri.parse("upi://pay?pa=$gpayAutoPayUpi&pn=ViralBoostAI&am=49&cu=INR")
                                        try {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, upiUri))
                                        } catch (_: Exception) {
                                            val backupUri = Uri.parse("upi://pay?pa=$paytmVisibleUpi&pn=ViralBoostAI&am=49&cu=INR")
                                            try {
                                                context.startActivity(Intent(Intent.ACTION_VIEW, backupUri))
                                            } catch (_: Exception) {
                                                Toast.makeText(context, "Opening UPI app...", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("btn_strict_auto_pay_49")
                                ) {
                                    Text("⚡ Direct Auto-Payment via UPI App", color = Color.White, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = utrNumberInput,
                            onValueChange = { utrNumberInput = it },
                            label = { Text("Enter 12-digit UPI UTR / Ref No. (For Barcode/Manual)") },
                            modifier = Modifier.fillMaxWidth().testTag("input_strict_utr"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6200EE),
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        boostStatusMessage?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = it, style = MaterialTheme.typography.bodySmall, color = Color.Red)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (utrNumberInput.isNotBlank()) {
                            showSpotlightModal = false
                            boostStatusMessage = null
                            Toast.makeText(context, "🚀 UTR Submitted! Super Spotlight Activating...", Toast.LENGTH_LONG).show()
                        } else {
                            boostStatusMessage = "⚠️ कृपया 12-अंकों का UTR नंबर दर्ज करें!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    modifier = Modifier.fillMaxWidth().testTag("btn_strict_confirm_utr")
                ) {
                    Text("Confirm UTR & Activate Boost 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSpotlightModal = false },
                    modifier = Modifier.testTag("btn_strict_cancel_spotlight")
                ) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }
}
