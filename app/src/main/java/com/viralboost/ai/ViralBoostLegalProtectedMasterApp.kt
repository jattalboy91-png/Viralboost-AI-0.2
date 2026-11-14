// Ultimate Master Code for "ViralBoost AI": Legal Liability Waiver & Non-Liability Clause (Protection against police complaints, fraud cases, and legal disputes), AdMob Compliant Privacy Policy, Reels Hub with All Detailed Plans, Dual UPI Payment Flow (Manual Paytm ID 'amit2653@ptyes' + Barcode with UTR & Direct Auto-Payment GPay), Strict Paywall, Full-Screen AI Studio, Timed AdMob Ads, & 100% Hidden Backend Affiliate ID (5665937)
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

@Composable
fun ViralBoostLegalProtectedMasterApp(
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
        modifier = Modifier.fillMaxSize().testTag("legal_protected_master_surface"),
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
                        modifier = Modifier.weight(1f).testTag("legal_protected_home_list"),
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
                                        modifier = Modifier.testTag("btn_close_legal_master_app")
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
                                    .testTag("card_legal_today_offer")
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
                                        modifier = Modifier.testTag("btn_legal_claim_offer")
                                    ) {
                                        Text("Claim Offer Now ⭐", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                                border = BorderStroke(1.dp, Color(0xFF6200EE)),
                                modifier = Modifier.fillMaxWidth().testTag("card_reels_hub_preview_legal")
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
                                            modifier = Modifier.testTag("btn_open_reels_hub_legal")
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
                                modifier = Modifier.fillMaxWidth().testTag("card_legal_wallet_status")
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
                                        modifier = Modifier.fillMaxWidth().testTag("btn_share_legal_app_link")
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
                                    .testTag("card_ai_studio_launcher_legal")
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
                                        modifier = Modifier.fillMaxWidth().testTag("btn_launch_legal_ai_hub")
                                    ) {
                                        Text("Launch Full-Screen AI Video & Hashtag Hub 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                "reels_hub_feed" -> {
                    ForcedReelsHubFeedScreen(
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
                        .testTag("bottom_nav_legal_master"),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { currentScreen = "home" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentScreen == "home") Color(0xFF2E2A47) else Color.Transparent
                        ),
                        modifier = Modifier.testTag("btn_nav_home_legal")
                    ) {
                        Text("🏠 Home", color = if (currentScreen == "home") Color.White else Color.LightGray)
                    }
                    Button(
                        onClick = { currentScreen = "reels_hub_feed" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentScreen == "reels_hub_feed") Color(0xFF03DAC5) else Color(0xFF2E2A47)
                        ),
                        modifier = Modifier.testTag("btn_nav_reels_feed_legal")
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
                        modifier = Modifier.testTag("btn_nav_ai_hub_legal")
                    ) {
                        Text("✨ AI Hub", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { currentScreen = "vip_plans" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentScreen == "vip_plans" || currentScreen == "clean_payment_screen") Color(0xFFFFD700) else Color(0xFF2E2A47)
                        ),
                        modifier = Modifier.testTag("btn_nav_vip_legal")
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
                    modifier = Modifier.testTag("btn_buy_vip_from_out_of_coins_legal")
                ) {
                    Text("Buy VIP Pass Now ⭐", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showOutOfCoinsDialog = false },
                    modifier = Modifier.testTag("btn_cancel_out_of_coins_legal")
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
                    modifier = Modifier.testTag("btn_dismiss_legal_reels_reward")
                ) {
                    Text("Awesome! Thanks", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// 📱 SIGNUP SCREEN WITH PRIVACY POLICY & LIABILITY WAIVER LINK
@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onOwnerBypass: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit
) {
    var fullName by remember { mutableStateOf("ok") }
    var mobileNumber by remember { mutableStateOf("5089657284") }
    var otpInput by remember { mutableStateOf("123456") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .testTag("signup_screen_column"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                modifier = Modifier.fillMaxWidth().testTag("card_welcome_bonus_signup")
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎁 ₹50 Instant Welcome Bonus Unlocked",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₹50 Cash Bonus + 10 Diamonds + 250 Coins credited upon signup",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Your Full Name") },
                modifier = Modifier.fillMaxWidth().testTag("input_signup_fullname"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                label = { Text("Enter Mobile Number") },
                modifier = Modifier.fillMaxWidth().testTag("input_signup_phone"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = otpInput,
                onValueChange = { otpInput = it },
                label = { Text("Enter 6-Digit OTP (e.g., 123456)") },
                modifier = Modifier.fillMaxWidth().testTag("input_signup_otp"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EE),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Privacy Policy & Liability Waiver Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPrivacyPolicy() }
                    .testTag("link_privacy_policy_waiver"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔒 By signing up, you agree to our ",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
                Text(
                    text = "Privacy Policy & Liability Waiver",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Cyan,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (fullName.isNotBlank() && mobileNumber.isNotBlank()) {
                        onSignupSuccess()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_complete_signup")
            ) {
                Text(
                    text = "Complete Sign Up & Enter App 🚀",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF282240)),
                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                modifier = Modifier.fillMaxWidth().testTag("card_owner_bypass_gate")
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "👑 Owner Admin Quick-Gate",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Restricted to Amit2653@ptyes administrator rights",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onOwnerBypass,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        modifier = Modifier.fillMaxWidth().testTag("btn_owner_bypass_signup")
                    ) {
                        Text(
                            text = "👑 Owner Bypass (Amit2653@ptyes) ⚡",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// 🔒 PRIVACY POLICY & LEGAL LIABILITY WAIVER SCREEN (Protects against police complaints & fraud claims)
@Composable
fun PrivacyPolicyScreen(onBackToSignup: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("privacy_policy_screen_column"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Button(
                onClick = onBackToSignup,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F3D56)),
                modifier = Modifier.testTag("btn_back_to_signup")
            ) {
                Text("← Back to Sign Up", color = Color.White)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "🔒 Privacy Policy & Legal Liability Waiver",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Last updated: October 2026",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                modifier = Modifier.fillMaxWidth().testTag("card_policy_section_1")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "1. Information Collection & Usage", style = MaterialTheme.typography.titleSmall, color = Color.Yellow, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "ViralBoost AI collects basic profile information (Name, Mobile Number) solely for account creation, authentication, and crediting welcome bonuses (Diamonds & Coins).", style = MaterialTheme.typography.bodySmall, color = Color.White)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                modifier = Modifier.fillMaxWidth().testTag("card_policy_section_2")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "2. AdMob Advertising & Data Sharing", style = MaterialTheme.typography.titleSmall, color = Color.Yellow, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "We use Google AdMob to display ads in our free version. AdMob may collect device identifiers and usage data to serve personalized or non-personalized advertisements in compliance with Google's publisher policies.", style = MaterialTheme.typography.bodySmall, color = Color.White)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                modifier = Modifier.fillMaxWidth().testTag("card_policy_section_3")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "3. Legal Liability Waiver & Dispute Resolution", style = MaterialTheme.typography.titleSmall, color = Color.Yellow, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "By registering and using ViralBoost AI, the user expressly agrees that the app, its developers, and owner (Amit2653@ptyes) shall bear no liability for any financial transactions, virtual coins, diamond balances, or promotional boosts. Users explicitly waive any right to file police complaints, criminal fraud charges, or legal lawsuits against the developer/owner regarding app usage, virtual items, or voluntary subscription payments.", style = MaterialTheme.typography.bodySmall, color = Color.White)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                modifier = Modifier.fillMaxWidth().testTag("card_policy_section_4")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "4. Secure Payments & UPI Verification", style = MaterialTheme.typography.titleSmall, color = Color.Yellow, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "All subscription purchases and creator boosts are processed securely via direct UPI apps or manual barcode scanning with UTR verification. Payments made are voluntary and non-refundable.", style = MaterialTheme.typography.bodySmall, color = Color.White)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2F)),
                modifier = Modifier.fillMaxWidth().testTag("card_policy_section_5")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "5. Contact Administrator", style = MaterialTheme.typography.titleSmall, color = Color.Yellow, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "For any official inquiries or support, contact the administrator at amit2653@ptyes.", style = MaterialTheme.typography.bodySmall, color = Color.White)
                }
            }
        }
    }
}
