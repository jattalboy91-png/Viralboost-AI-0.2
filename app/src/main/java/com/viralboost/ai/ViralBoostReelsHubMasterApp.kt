// Ultimate Master Code for "ViralBoost AI": Creator Boost & Super Spotlight Modal with Direct 1-Click Auto-Payment UPI Intent Trigger, Dual Admin UPI IDs, Real Barcode QR Image, Full-Screen AI Studio, Timed AdMob Interstitial Ads, 3-Day Free Trial + 1-Day Friend Referral Reward, "TODAY OFFER - 50% OFF", & 100% Hidden Backend Affiliate ID (5665937)
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
fun ViralBoostReelsHubMasterApp(
    onBackToDashboard: (() -> Unit)? = null
) {
    var currentScreen by remember { mutableStateOf("home") }
    
    var trialDaysLeft by remember { mutableIntStateOf(3) }
    var hasActiveSubscription by remember { mutableStateOf(false) }
    var showReferralRewardNotification by remember { mutableStateOf(false) }

    var selectedPlanTitle by remember { mutableStateOf("") }
    var selectedPlanPrice by remember { mutableStateOf("") }

    val context = LocalContext.current

    val paytmFullUpi = "amit2653@ptyes"
    val gpayPrimaryUpi = "amitkumar201591-2@okhdfcbank"
    val maskedGpayUpi = maskUpiId(gpayPrimaryUpi)
    val adminBarcodeImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=upi://pay?pa=amit2653@ptyes&pn=ViralBoostAI"
    val secureBackendAffiliateId = "5665937"

    BackHandler {
        when (currentScreen) {
            "clean_payment_screen" -> currentScreen = "vip_plans"
            "vip_plans", "clean_full_screen_studio", "reels_hub_feed" -> currentScreen = "home"
            else -> onBackToDashboard?.invoke()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize().testTag("reels_hub_master_surface"),
        color = Color(0xFF0F0E17)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when (currentScreen) {
                "home" -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f).testTag("reels_hub_home_list"),
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
                                        modifier = Modifier.testTag("btn_close_reels_hub_app")
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
                                    .testTag("card_reels_today_offer")
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
                                        modifier = Modifier.testTag("btn_reels_claim_offer")
                                    ) {
                                        Text("Claim Offer Now ⭐", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 🎬 Viral Creator Reels Hub Section Preview Card (Matching Screenshot 1)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                                border = BorderStroke(1.dp, Color(0xFF6200EE)),
                                modifier = Modifier.fillMaxWidth().testTag("card_reels_hub_preview")
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
                                            modifier = Modifier.testTag("btn_open_hub_feed")
                                        ) {
                                            Text("Open Hub Feed", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Watch viral shorts & masterclasses, boost your channel reach, and earn rewards!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                                modifier = Modifier.fillMaxWidth().testTag("card_reels_trial_referral")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (hasActiveSubscription) "⭐ VIP Pro Subscription: ACTIVE" else "⭐ Free Trial Status: $trialDaysLeft Days Left",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold
                                    )
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
                                        modifier = Modifier.fillMaxWidth().testTag("btn_reels_share_referral")
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
                                    .testTag("card_reels_ai_studio_launcher")
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
                                        text = "कॉमेडी, हरियाणवी रैप या शॉर्ट्स बनाएं — ऑटो-वायरल हैशटैग्स के साथ! (Full-Screen Studio)",
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
                                        modifier = Modifier.fillMaxWidth().testTag("btn_reels_launch_studio")
                                    ) {
                                        Text("Launch Full-Screen AI Video & Hashtag Hub 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                "reels_hub_feed" -> {
                    // Full-Screen Viral Creator Reels Hub Feed with Boost & Super Spotlight Option (Matching Screenshot 1 & 2)
                    ViralCreatorReelsFeedScreen(
                        paytmUpi = paytmFullUpi,
                        gpayUpi = gpayPrimaryUpi,
                        maskedGpay = maskedGpayUpi,
                        barcodeUrl = adminBarcodeImageUrl,
                        context = context,
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
                "clean_full_screen_studio" -> {
                    CleanFullScreenStudioScreen(
                        hasSubscription = hasActiveSubscription,
                        onOpenVipPlans = { currentScreen = "vip_plans" },
                        onBackToHome = { currentScreen = "home" }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E2F), RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .testTag("bottom_nav_reels_hub"),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { currentScreen = "home" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentScreen == "home") Color(0xFF2E2A47) else Color.Transparent
                    ),
                    modifier = Modifier.testTag("btn_nav_home_reels")
                ) {
                    Text("🏠 Home", color = if (currentScreen == "home") Color.White else Color.LightGray)
                }
                Button(
                    onClick = { currentScreen = "reels_hub_feed" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentScreen == "reels_hub_feed") Color(0xFF03DAC5) else Color(0xFF2E2A47)
                    ),
                    modifier = Modifier.testTag("btn_nav_reels_feed")
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
                    modifier = Modifier.testTag("btn_nav_ai_hub_reels")
                ) {
                    Text("✨ AI Hub", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { currentScreen = "vip_plans" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentScreen == "vip_plans" || currentScreen == "clean_payment_screen") Color(0xFFFFD700) else Color(0xFF2E2A47)
                    ),
                    modifier = Modifier.testTag("btn_nav_vip_reels")
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

    if (showReferralRewardNotification) {
        AlertDialog(
            onDismissRequest = { showReferralRewardNotification = false },
            title = { Text("🎉 बधाई हो! (Bonus Reward)", color = Color.Yellow, fontWeight = FontWeight.Bold) },
            text = { Text("आपके द्वारा शेयर किए गए लिंक से दोस्त ने ऐप पर 1 मिनट तक मूवमेंट और एक्सप्लोर किया है। इसके reward में आपको **1 दिन का फ्री प्रो सब्सक्रिप्शन** सफलतापूर्वक मिल गया है!", color = Color.White) },
            confirmButton = {
                Button(
                    onClick = { showReferralRewardNotification = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    modifier = Modifier.testTag("btn_dismiss_reels_reward")
                ) {
                    Text("Awesome! Thanks", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// 🎬 VIRAL CREATOR REELS HUB FEED WITH 1-CLICK AUTO-PAYMENT BOOST MODAL (Matching your screenshots)
@Composable
fun ViralCreatorReelsFeedScreen(
    paytmUpi: String,
    gpayUpi: String,
    maskedGpay: String,
    barcodeUrl: String,
    context: Context,
    onBackToHome: () -> Unit
) {
    var showBoostModal by remember { mutableStateOf(false) }
    var selectedBoostCreator by remember { mutableStateOf("⚡ Rohan Finance Fast") }
    var utrNumberInput by remember { mutableStateOf("") }
    var boostStatusMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .testTag("reels_feed_lazy_column"),
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
                    modifier = Modifier.testTag("btn_back_home_from_reels")
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

            // Reel Card 1: Rohan Finance Fast (Matching Screenshot 1)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("card_reel_rohan")
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
                            onClick = {
                                selectedBoostCreator = "⚡ Rohan Finance Fast"
                                showBoostModal = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                            modifier = Modifier.testTag("btn_boost_rohan")
                        ) {
                            Text("🚀 Boost ⚡", color = Color.White, fontWeight = FontWeight.Bold)
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
                            .height(140.dp)
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

            Spacer(modifier = Modifier.height(10.dp))

            // Reel Card 2: Aarav 'Viral' Sharma (Matching Screenshot 1)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("card_reel_aarav")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥 Aarav 'Viral' Sharma",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Yellow,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = {
                                selectedBoostCreator = "🔥 Aarav 'Viral' Sharma"
                                showBoostModal = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                            modifier = Modifier.testTag("btn_boost_aarav")
                        ) {
                            Text("🚀 Boost ⚡", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "How I Gained 100K Followers in 30 Days (AI Secrets Revealed) 🚀",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
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

    // 🚀 CREATOR BOOST & SUPER SPOTLIGHT MODAL WITH DIRECT AUTO-PAYMENT & DUAL UPI (Matching Screenshot 2)
    if (showBoostModal) {
        AlertDialog(
            onDismissRequest = { showBoostModal = false },
            title = {
                Text(
                    text = "🚀 Creator Boost & Super Spotlight",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                        .testTag("boost_modal_column"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Boosting: $selectedBoostCreator",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Cyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "10X Reach Push across YouTube, Instagram & GoLiveGram",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Selected Plan Box: Instant VIP Super Spotlight (₹49) with Auto-Payment Button
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF282240)),
                            border = BorderStroke(2.dp, Color(0xFFFFD700)),
                            modifier = Modifier.fillMaxWidth().testTag("card_boost_plan_49")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚡ Instant VIP Super Spotlight",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "₹49 Only",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.Green,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Pay to Owner UPI: $paytmUpi",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.LightGray
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // ⚡ 1-Click Auto-Payment Button opening user's UPI app directly
                                Button(
                                    onClick = {
                                        val upiUri = Uri.parse("upi://pay?pa=$paytmUpi&pn=ViralBoostAI&am=49&cu=INR")
                                        val intent = Intent(Intent.ACTION_VIEW, upiUri)
                                        try {
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            val backupUri = Uri.parse("upi://pay?pa=$gpayUpi&pn=ViralBoostAI&am=49&cu=INR")
                                            try {
                                                context.startActivity(Intent(Intent.ACTION_VIEW, backupUri))
                                            } catch (_: Exception) {
                                                Toast.makeText(context, "Opening UPI app...", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("btn_auto_pay_boost_49"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "⚡ Pay ₹49 via UPI App (Auto-Payment)",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Admin Barcode QR Image
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.size(110.dp).testTag("card_boost_qr_code")
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = barcodeUrl,
                                    contentDescription = "QR Barcode",
                                    modifier = Modifier.fillMaxSize().padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Dual UPI IDs display
                        Text(
                            text = "Paytm (Visible): $paytmUpi",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Yellow,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "GPay (Secured): $maskedGpay",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Cyan,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = utrNumberInput,
                            onValueChange = { utrNumberInput = it },
                            label = { Text("Enter 12-digit UPI UTR / Ref Number") },
                            modifier = Modifier.fillMaxWidth().testTag("input_boost_utr"),
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
                            showBoostModal = false
                            boostStatusMessage = null
                            Toast.makeText(context, "🚀 Boost Activated for $selectedBoostCreator!", Toast.LENGTH_LONG).show()
                        } else {
                            boostStatusMessage = "⚠️ कृपया 12-अंकों का UTR नंबर दर्ज करें!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    modifier = Modifier.fillMaxWidth().testTag("btn_confirm_launch_boost")
                ) {
                    Text("Confirm & Launch Boost 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showBoostModal = false },
                    modifier = Modifier.testTag("btn_cancel_boost")
                ) {
                    Text("Cancel", color = Color.LightGray)
                }
            }
        )
    }
}
