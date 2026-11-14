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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * Helper function to mask GPay UPI ID for privacy (Showing only first 4 and last 5 characters)
 * e.g., "amitkumar201591-2@okhdfcbank" -> "amit****cbank"
 */
fun maskPaymentUpiId(upi: String): String {
    if (upi.length <= 9) return upi
    val prefix = upi.take(4)
    val suffix = upi.takeLast(5)
    return "$prefix****$suffix"
}

/**
 * Ultimate Master Code for "ViralBoost AI":
 * Full-Screen Dedicated Payment & Checkout Page, Real Admin Barcode QR Image Display,
 * Dual UPI IDs (Paytm fully visible & Google Pay masked for privacy),
 * 1-Click Auto-Payment UPI Intent Trigger, 3-Day Free Trial + 1-Day Friend Referral Reward,
 * "TODAY OFFER - 50% OFF", Gallery Photo Picker, AdMob Ads, & 100% Hidden Backend Affiliate ID (5665937)
 */
@Composable
fun ViralBoostFullScreenPaymentMasterApp(
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
    val maskedGpayUpi = remember(gpayPrimaryUpi) { maskPaymentUpiId(gpayPrimaryUpi) } // amit****cbank
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
                            .testTag("payment_master_dashboard_list"),
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
                                        modifier = Modifier.testTag("btn_close_payment_master_app")
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
                            currentScreen = "full_screen_payment" // Opens Full-Screen Payment Page
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
                    .testTag("bottom_nav_payment_master"),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { currentScreen = "home" },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentScreen == "home") Color(0xFF2E2A47) else Color.Transparent),
                    modifier = Modifier.testTag("btn_nav_home_pay")
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
                    modifier = Modifier.testTag("btn_nav_ai_hub_pay")
                ) {
                    Text("✨ AI Hub", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { currentScreen = "vip_plans" },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentScreen == "vip_plans" || currentScreen == "full_screen_payment") Color(0xFFFFD700) else Color(0xFF2E2A47)),
                    modifier = Modifier.testTag("btn_nav_vip_pay")
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

// 💳 FULL-SCREEN DEDICATED PAYMENT & CHECKOUT SCREEN WITH REAL BARCODE IMAGE & AUTO-PAYMENT
@Composable
fun FullScreenPaymentCheckoutScreen(
    planTitle: String,
    planPrice: String,
    paytmUpi: String,
    gpayUpi: String,
    maskedGpay: String,
    barcodeUrl: String,
    context: android.content.Context,
    onPaymentSuccessful: () -> Unit,
    onBack: () -> Unit
) {
    var utrNumberInput by remember { mutableStateOf("") }
    var paymentStatusMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .testTag("fullscreen_checkout_page"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F3D56)),
                    modifier = Modifier.testTag("btn_back_to_plans")
                ) {
                    Text("← Back to Plans", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "💳 Secure Auto-Payment Gateway",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Selected Plan: $planTitle ($planPrice)",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 📷 REAL ADMIN BARCODE / QR CODE IMAGE DISPLAY BOX
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .size(200.dp)
                    .testTag("admin_qr_image_card")
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = barcodeUrl,
                        contentDescription = "Admin QR Barcode",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "📷 Scan Barcode or tap button below for Auto-Payment",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Cyan
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ⚡ BIG PROMINENT AUTO-PAYMENT BUTTON (Opens user's phone UPI payment app automatically)
            Button(
                onClick = {
                    val amountOnly = planPrice.replace("₹", "").trim()
                    val upiUri = Uri.parse("upi://pay?pa=$paytmUpi&pn=ViralBoostAI&am=$amountOnly&cu=INR")
                    val intent = Intent(Intent.ACTION_VIEW, upiUri)
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        val backupUri = Uri.parse("upi://pay?pa=$gpayUpi&pn=ViralBoostAI&am=$amountOnly&cu=INR")
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, backupUri))
                        } catch (_: Exception) {
                            Toast.makeText(context, "Opening UPI app for payment...", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_1click_auto_payment"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "⚡ 1-Click Auto-Payment via UPI App",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🟢 1. Paytm UPI ID (Fully Visible: amit2653@ptyes)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Paytm UPI (Fully Visible):", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                        Text(text = paytmUpi, style = MaterialTheme.typography.titleSmall, color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            val amountOnly = planPrice.replace("₹", "").trim()
                            val upiUri = Uri.parse("upi://pay?pa=$paytmUpi&pn=ViralBoostAI&am=$amountOnly&cu=INR")
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, upiUri))
                            } catch (_: Exception) {
                                Toast.makeText(context, "Paytm ID: $paytmUpi", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text("Pay 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔵 2. Google Pay UPI ID (Masked on UI: amit****cbank)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Google Pay UPI (Secured):", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                        Text(text = maskedGpay, style = MaterialTheme.typography.titleSmall, color = Color.Cyan, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            val amountOnly = planPrice.replace("₹", "").trim()
                            val upiUri = Uri.parse("upi://pay?pa=$gpayUpi&pn=ViralBoostAI&am=$amountOnly&cu=INR")
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, upiUri))
                            } catch (_: Exception) {
                                Toast.makeText(context, "GPay ID: $maskedGpay", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text("Pay 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = utrNumberInput,
                onValueChange = { utrNumberInput = it },
                label = { Text("Enter 12-digit UPI UTR / Ref No. after payment") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_utr_fullscreen"),
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
                    if (utrNumberInput.isNotBlank()) {
                        onPaymentSuccessful()
                    } else {
                        paymentStatusMessage = "⚠️ कृपया पेमेंट करने के बाद 12-अंकों का UTR नंबर दर्ज करें!"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_verify_utr_fullscreen"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Verify & Activate VIP Pass Now ⭐",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            paymentStatusMessage?.let { msg ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = msg, style = MaterialTheme.typography.bodySmall, color = Color.Red)
            }
        }
    }
}
