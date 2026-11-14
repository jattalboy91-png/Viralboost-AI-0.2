package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.data.local.LeaderboardCreator
import com.example.data.local.ProductOrderRecord
import com.example.data.local.UserProfile
import com.example.data.model.AffiliateProduct
import com.example.data.model.SampleAffiliateProducts
import com.example.monetization.AdManager
import com.example.monetization.MonetizationConstants
import com.example.util.ReferralNotificationHelper
import com.example.ui.components.AiAutoDealMatchmakerCard
import com.example.ui.components.AffiliateLeaderboardSpotlightCard
import com.example.ui.components.AffiliateLeaderboardView
import com.example.ui.components.BannerAdView
import com.example.ui.components.InterstitialAdDialog
import com.example.ui.components.NativeAdCard
import com.example.ui.components.NativeVideoAdCard
import com.example.ui.components.UniversalEcommerceProductLinkCard
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AffiliateHubScreen(
    userProfile: UserProfile?,
    affiliateTag: String = MonetizationConstants.AFFILIATE_TAG,
    placedOrders: List<ProductOrderRecord> = emptyList(),
    leaderboard: List<LeaderboardCreator> = emptyList(),
    bannerAdUnitId: String = MonetizationConstants.ADMOB_BANNER_UNIT_ID,
    nativeAdUnitId: String = MonetizationConstants.ADMOB_NATIVE_UNIT_ID,
    nativeVideoAdUnitId: String = MonetizationConstants.ADMOB_NATIVE_VIDEO_UNIT_ID,
    onNavigateToVip: () -> Unit,
    onShareProduct: (AffiliateProduct) -> Unit = {},
    onPlaceOrder: (product: AffiliateProduct, name: String, phone: String, address: String) -> Unit = { _, _, _, _ -> },
    onClaimPurchaseReward: (AffiliateProduct) -> Unit = {},
    onNavigateToAiTools: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val profile = userProfile ?: UserProfile()
    val isVip = profile.isVip
    var isAutoPublishActive by remember { mutableStateOf(false) }

    // Top Navigation Tab: 0 = Deals & Feed, 1 = Top Earners Leaderboard
    var activeAffiliateTab by remember { mutableIntStateOf(0) }

    // Product Feed Filtering State
    val categories = listOf("All Items", "Kurtis & Ethnic", "Fashion & Jewelry", "Daily Utilities", "Under ₹299")
    var selectedCategory by remember { mutableStateOf("All Items") }

    val platforms = listOf("All Stores", "Meesho", "Flipkart", "Amazon")
    var selectedPlatform by remember { mutableStateOf("All Stores") }

    // Dialog state for order/redirect
    var activeProductForOrder by remember { mutableStateOf<AffiliateProduct?>(null) }
    var orderCelebrationProduct by remember { mutableStateOf<AffiliateProduct?>(null) }

    // Persisted channel connections & handles (shared with VideoCrossPosterScreen & HomeScreen)
    val sharedPrefs = remember {
        context.getSharedPreferences("viralboost_social_links", Context.MODE_PRIVATE)
    }

    // Social Media Account Linking & One-Click Promotion State
    var isWhatsAppConnected by remember {
        mutableStateOf(sharedPrefs.getBoolean("conn_whatsapp", true))
    }
    var isInstagramConnected by remember {
        mutableStateOf(sharedPrefs.getBoolean("conn_instagram", true))
    }
    var isTelegramConnected by remember {
        mutableStateOf(sharedPrefs.getBoolean("conn_telegram", true))
    }
    var isFacebookConnected by remember {
        mutableStateOf(sharedPrefs.getBoolean("conn_facebook", true))
    }

    var whatsAppTarget by remember {
        mutableStateOf(sharedPrefs.getString("handle_whatsapp", "+91 98765 43210") ?: "+91 98765 43210")
    }
    var instagramHandle by remember {
        mutableStateOf(sharedPrefs.getString("handle_instagram", "@viralcreator_99") ?: "@viralcreator_99")
    }
    var telegramChannel by remember {
        mutableStateOf(sharedPrefs.getString("handle_telegram", "t.me/trending_deal_vault") ?: "t.me/trending_deal_vault")
    }
    var facebookPage by remember {
        mutableStateOf(sharedPrefs.getString("handle_facebook", "fb.com/creatorgrowthhub") ?: "fb.com/creatorgrowthhub")
    }

    var editingSocialChannel by remember { mutableStateOf<String?>(null) }
    var userPersonalAffiliateLink by remember {
        mutableStateOf(MonetizationConstants.getUserPersonalAffiliateLink(context))
    }
    var showPromoteEverywhereDialog by remember { mutableStateOf(false) }
    var broadcastCelebrationDeal by remember { mutableStateOf<AffiliateProduct?>(null) }

    var showMilestoneAdDialog by remember { mutableStateOf(false) }
    var pendingDismissAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    if (showMilestoneAdDialog) {
        InterstitialAdDialog(
            interstitialUnitId = MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID,
            onDismiss = {
                showMilestoneAdDialog = false
                val action = pendingDismissAction
                pendingDismissAction = null
                action?.invoke()
            }
        )
    }

    val handleBroadcastCustomProductLink: (String, String) -> Unit = { link, platform ->
        val cleanTag = affiliateTag.trim().ifBlank { MonetizationConstants.AFFILIATE_TAG }
        val taggedUrl = when {
            link.contains("tag=") || link.contains("referral_code=") || link.contains("affid=") || link.contains("?r=") -> link
            link.contains("amazon.in") -> if (link.contains("?")) "$link&tag=$cleanTag&linkCode=ll1" else "$link?tag=$cleanTag&linkCode=ll1"
            link.contains("flipkart.com") -> if (link.contains("?")) "$link&affid=$cleanTag&affExtParam1=viralboost" else "$link?affid=$cleanTag&affExtParam1=viralboost"
            link.contains("meesho.com") -> if (link.contains("?")) "$link&referral_code=$cleanTag&source=viralboost_app" else "$link?referral_code=$cleanTag&source=viralboost_app"
            link.contains("myntra.com") -> if (link.contains("?")) "$link&affid=$cleanTag&utm_source=viralboost" else "$link?affid=$cleanTag&utm_source=viralboost"
            link.contains("earnkaro.com") -> if (link.contains("?")) "$link&r=$cleanTag" else "$link?r=$cleanTag"
            link.contains("?") -> "$link&tag=$cleanTag&ref=$cleanTag"
            else -> "$link?tag=$cleanTag&ref=$cleanTag"
        }

        val promoText = """
            🔥 Trending Deal Alert on $platform!
            🛍️ Tap to Order: $taggedUrl
            🚚 Free Express Delivery & Cash on Delivery Available!

            ━━━━━━━━━━━━━━━━━━━━━━
            🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!
            😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:
            👉 ${MonetizationConstants.PERMANENT_OWNER_AFFILIATE_LINK}

            🚀 Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:
            👉 ${MonetizationConstants.OFFICIAL_STORE_DOWNLOAD_URL}
        """.trimIndent()

        val executeBroadcast = {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, promoText)
                type = "text/plain"
            }
            val chooser = Intent.createChooser(sendIntent, "Broadcast $platform Deal (WhatsApp / IG / TG / FB)")
            context.startActivity(chooser)

            val customProduct = AffiliateProduct(
                id = "custom_${System.currentTimeMillis()}",
                title = "$platform Exclusive Product Deal",
                category = "E-Commerce",
                platform = platform,
                priceRupees = 499,
                originalPriceRupees = 999,
                discountPercent = 50,
                rating = 4.8,
                reviewCount = 145,
                imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30",
                productSlug = "deal",
                description = "E-Commerce deal from $platform broadcasted across social channels"
            )
            onShareProduct(customProduct)
            broadcastCelebrationDeal = customProduct
            Toast.makeText(context, "$platform Deal Broadcasted! +10 Coins Credited! 🪙", Toast.LENGTH_SHORT).show()

            // Success Notification ("Go Viral")
            ReferralNotificationHelper.showPostReadyToGoViralNotification(context)
            Toast.makeText(context, "Congratulations! Your post is ready to go viral!", Toast.LENGTH_LONG).show()
        }

        // Affiliate Hub Milestone 2: Exact moment user taps to share/broadcast
        AdManager.triggerMilestoneInterstitial(
            context = context,
            isVip = userProfile?.isVip == true,
            milestoneName = "Affiliate Hub - Milestone 2: Custom Deal Broadcast",
            onShowInterstitialFallback = { onDismiss ->
                pendingDismissAction = {
                    onDismiss()
                    executeBroadcast()
                }
                showMilestoneAdDialog = true
            },
            onComplete = {
                executeBroadcast()
            }
        )
    }

    val handleCopyCustomPromoLink: (String, String) -> Unit = { link, platform ->
        val cleanTag = affiliateTag.trim().ifBlank { MonetizationConstants.AFFILIATE_TAG }
        val taggedUrl = when {
            link.contains("tag=") || link.contains("referral_code=") || link.contains("affid=") || link.contains("?r=") -> link
            link.contains("amazon.in") -> if (link.contains("?")) "$link&tag=$cleanTag&linkCode=ll1" else "$link?tag=$cleanTag&linkCode=ll1"
            link.contains("flipkart.com") -> if (link.contains("?")) "$link&affid=$cleanTag&affExtParam1=viralboost" else "$link?affid=$cleanTag&affExtParam1=viralboost"
            link.contains("meesho.com") -> if (link.contains("?")) "$link&referral_code=$cleanTag&source=viralboost_app" else "$link?referral_code=$cleanTag&source=viralboost_app"
            link.contains("myntra.com") -> if (link.contains("?")) "$link&affid=$cleanTag&utm_source=viralboost" else "$link?affid=$cleanTag&utm_source=viralboost"
            link.contains("earnkaro.com") -> if (link.contains("?")) "$link&r=$cleanTag" else "$link?r=$cleanTag"
            link.contains("?") -> "$link&tag=$cleanTag&ref=$cleanTag"
            else -> "$link?tag=$cleanTag&ref=$cleanTag"
        }

        val promoText = """
            🔥 Trending Deal Alert on $platform!
            🛍️ Tap to Order: $taggedUrl
            🚚 Free Express Delivery & Cash on Delivery Available!

            ━━━━━━━━━━━━━━━━━━━━━━
            🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!
            😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:
            👉 ${MonetizationConstants.PERMANENT_OWNER_AFFILIATE_LINK}

            🚀 Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:
            👉 ${MonetizationConstants.OFFICIAL_STORE_DOWNLOAD_URL}
        """.trimIndent()

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("ViralBoost Promo Link", promoText))
        Toast.makeText(context, "Promo copy copied with tag #$cleanTag & download link! 📋", Toast.LENGTH_SHORT).show()

        // Affiliate Hub Milestone 2: Exact moment user copies/shares promotional link
        AdManager.triggerMilestoneInterstitial(
            context = context,
            isVip = userProfile?.isVip == true,
            milestoneName = "Affiliate Hub - Milestone 2: Copy Deal Link",
            onShowInterstitialFallback = { onDismiss ->
                pendingDismissAction = onDismiss
                showMilestoneAdDialog = true
            }
        )

        ReferralNotificationHelper.showPostReadyToGoViralNotification(context)
        Toast.makeText(context, "Congratulations! Your post is ready to go viral!", Toast.LENGTH_LONG).show()
    }

    // Filter products
    val filteredProducts = remember(selectedCategory, selectedPlatform) {
        SampleAffiliateProducts.catalog.filter { product ->
            val categoryMatches = when (selectedCategory) {
                "All Items" -> true
                "Kurtis & Ethnic" -> product.category.contains("Kurti", ignoreCase = true)
                "Fashion & Jewelry" -> product.category.contains("Fashion", ignoreCase = true)
                "Daily Utilities" -> product.category.contains("Utilities", ignoreCase = true)
                "Under ₹299" -> product.priceRupees <= 299
                else -> true
            }
            val platformMatches = when (selectedPlatform) {
                "All Stores" -> true
                else -> product.platform.equals(selectedPlatform, ignoreCase = true)
            }
            categoryMatches && platformMatches
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Gamified Top Tab Selector: Deals Feed vs Shared Links Leaderboard
        TabRow(
            selectedTabIndex = activeAffiliateTab,
            containerColor = Color(0xFF131128),
            contentColor = NeonCyan,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = activeAffiliateTab == 0,
                onClick = {
                    if (activeAffiliateTab != 0) {
                        AdManager.triggerMilestoneInterstitial(
                            context = context,
                            isVip = isVip,
                            milestoneName = "Affiliate Hub Tab: Deals & Feed",
                            onShowInterstitialFallback = { onDismiss ->
                                pendingDismissAction = {
                                    onDismiss()
                                    activeAffiliateTab = 0
                                }
                                showMilestoneAdDialog = true
                            },
                            onComplete = { activeAffiliateTab = 0 }
                        )
                    }
                },
                modifier = Modifier.testTag("affiliate_tab_deals"),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Deals & Feed", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = activeAffiliateTab == 1,
                onClick = {
                    if (activeAffiliateTab != 1) {
                        AdManager.triggerMilestoneInterstitial(
                            context = context,
                            isVip = isVip,
                            milestoneName = "Affiliate Hub Tab: Coins & Diamonds Leaderboard",
                            onShowInterstitialFallback = { onDismiss ->
                                pendingDismissAction = {
                                    onDismiss()
                                    activeAffiliateTab = 1
                                }
                                showMilestoneAdDialog = true
                            },
                            onComplete = { activeAffiliateTab = 1 }
                        )
                    }
                },
                modifier = Modifier.testTag("affiliate_tab_leaderboard"),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Coins & Diamonds 🏆", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        if (activeAffiliateTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Header with Wallet Balances & Tag
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Affiliate Growth Vault",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "High-paying partner offers & trending e-commerce feed",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1B4B))
                                .border(1.dp, BrandPurple, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("TAG: $affiliateTag", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Creator Wallet Balance Chips with Rewards Announcement
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                                )
                            )
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${profile.diamonds} 💎", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)

                            Spacer(modifier = Modifier.width(12.dp))

                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${profile.coins} 🪙", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Text(
                            text = "100% Rewards Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SuccessGreen
                        )
                    }
                }
            }

            // Gamification Rewards Banner (10 Coins on Share, 3 Diamonds on Purchase)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131127)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "E-Commerce Reward & Gamification Rules",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("📤 Social Share", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Earn +10 Coins 🪙", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = GoldAccent)
                                    Text("Instant reward on share", fontSize = 9.sp, color = Color.LightGray)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, DiamondCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("🛍️ Buy / Order", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Earn +3 Diamonds 💎", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = DiamondCyan)
                                    Text("Direct to in-app wallet", fontSize = 9.sp, color = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }

            // Affiliate Leaderboard Spotlight Teaser Card
            item {
                AffiliateLeaderboardSpotlightCard(
                    userProfile = profile,
                    onViewFullLeaderboard = { activeAffiliateTab = 1 }
                )
            }

            // =========================================================================
            // SECTION: AI AUTO-DEAL MATCHMAKER & PERSONAL AFFILIATE LINK SETTINGS
            // =========================================================================
            item {
                AiAutoDealMatchmakerCard(
                    userPersonalLink = userPersonalAffiliateLink,
                    onSavePersonalLink = { newLink ->
                        val clean = newLink.trim()
                        userPersonalAffiliateLink = clean
                        MonetizationConstants.setUserPersonalAffiliateLink(context, clean)
                        Toast.makeText(context, "Personal Affiliate Link Saved! AI Videos will now route commissions to your link 🛍️", Toast.LENGTH_LONG).show()

                        // Affiliate Hub Milestone 1: Exact moment user saves/pastes affiliate link
                        AdManager.triggerMilestoneInterstitial(
                            context = context,
                            isVip = userProfile?.isVip == true,
                            milestoneName = "Affiliate Hub - Milestone 1: Personal Affiliate Link Saved",
                            onShowInterstitialFallback = { onDismiss ->
                                pendingDismissAction = onDismiss
                                showMilestoneAdDialog = true
                            }
                        )
                    },
                    onClearPersonalLink = {
                        userPersonalAffiliateLink = ""
                        MonetizationConstants.clearUserPersonalAffiliateLink(context)
                        Toast.makeText(context, "Reverted to Owner Master Link (https://bitli.in/Ff4aE3C) Fallback ⚡", Toast.LENGTH_SHORT).show()
                    },
                    onTriggerMilestone1 = {
                        // Affiliate Hub Milestone 1: Exact moment user pastes/enters affiliate link
                        AdManager.triggerMilestoneInterstitial(
                            context = context,
                            isVip = userProfile?.isVip == true,
                            milestoneName = "Affiliate Hub - Milestone 1: Personal Link Pasted/Entered",
                            onShowInterstitialFallback = { onDismiss ->
                                pendingDismissAction = onDismiss
                                showMilestoneAdDialog = true
                            }
                        )
                    }
                )
            }

            // =========================================================================
            // SECTION: UNIVERSAL E-COMMERCE & AFFILIATE LINK INPUT BOX
            // =========================================================================
            item {
                UniversalProductLinkBroadcastCard(
                    affiliateTag = affiliateTag,
                    connectedChannelsCount = listOf(isWhatsAppConnected, isInstagramConnected, isTelegramConnected, isFacebookConnected).count { it },
                    onTriggerMilestone1 = {
                        AdManager.triggerMilestoneInterstitial(
                            context = context,
                            isVip = userProfile?.isVip == true,
                            milestoneName = "Affiliate Hub - Milestone 1: Link Pasted/Entered",
                            onShowInterstitialFallback = { onDismiss ->
                                pendingDismissAction = onDismiss
                                showMilestoneAdDialog = true
                            }
                        )
                    },
                    onBroadcastCustomLink = handleBroadcastCustomProductLink,
                    onCopyPromoLink = handleCopyCustomPromoLink
                )
            }

            // =========================================================================
            // SECTION: SOCIAL MEDIA ACCOUNT LINKING & 1-CLICK PROMOTION
            // =========================================================================
            item {
                SocialAccountsAndPromoteCard(
                    isWhatsAppConnected = isWhatsAppConnected,
                    isInstagramConnected = isInstagramConnected,
                    isTelegramConnected = isTelegramConnected,
                    isFacebookConnected = isFacebookConnected,
                    whatsAppTarget = whatsAppTarget,
                    instagramHandle = instagramHandle,
                    telegramChannel = telegramChannel,
                    facebookPage = facebookPage,
                    onToggleWhatsApp = {
                        val newVal = !isWhatsAppConnected
                        isWhatsAppConnected = newVal
                        sharedPrefs.edit().putBoolean("conn_whatsapp", newVal).apply()
                    },
                    onToggleInstagram = {
                        val newVal = !isInstagramConnected
                        isInstagramConnected = newVal
                        sharedPrefs.edit().putBoolean("conn_instagram", newVal).apply()
                    },
                    onToggleTelegram = {
                        val newVal = !isTelegramConnected
                        isTelegramConnected = newVal
                        sharedPrefs.edit().putBoolean("conn_telegram", newVal).apply()
                    },
                    onToggleFacebook = {
                        val newVal = !isFacebookConnected
                        isFacebookConnected = newVal
                        sharedPrefs.edit().putBoolean("conn_facebook", newVal).apply()
                    },
                    onEditChannel = { channelName -> editingSocialChannel = channelName },
                    onPromoteEverywhere = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        showPromoteEverywhereDialog = true
                    }
                )
            }

            // =========================================================================
            // SECTION: SOCIAL MEDIA GROWTH & REACH ACCELERATION TOOLS
            // =========================================================================
            item {
                SocialGrowthToolsSuite(
                    onNavigateToAiTools = onNavigateToAiTools
                )
            }

            // VIP Lock Banner if Free User
            if (!isVip) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("affiliate_vip_locked_banner"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1065)),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, VipGold)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = VipGold, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "VIP Exclusive Growth Modules",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Unlock pre-negotiated 40%-60% recurring creator affiliate commissions with tag $affiliateTag and 1-Click Video Auto-Publisher.",
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToVip,
                                colors = ButtonDefaults.buttonColors(containerColor = VipGold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Upgrade to VIP to Unlock", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 1-Click Video Auto-Publisher Module
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                        .background(NeonCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "1-Click Video Auto-Publisher",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Queue & Auto-Schedule Reels/Shorts",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            if (!isVip) {
                                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Automates zero-drop publishing schedules to maximize initial algorithmic push between 6:00 PM - 9:00 PM peak engagement hours.",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (isVip) {
                                    isAutoPublishActive = !isAutoPublishActive
                                    Toast.makeText(context, if (isAutoPublishActive) "Auto-Publisher Active! Queue initialized." else "Auto-Publisher Paused", Toast.LENGTH_SHORT).show()
                                } else {
                                    onNavigateToVip()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isVip) (if (isAutoPublishActive) SuccessGreen else BrandPurple) else Color(0xFF334155)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (!isVip) "Locked (Requires VIP Plan)" else if (isAutoPublishActive) "Auto-Publish Queue Active ✅" else "Activate Auto-Publish Queue",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Organic AdMob Native Ad Card in content stream
            item {
                NativeAdCard(
                    isVip = isVip,
                    adUnitId = nativeAdUnitId
                )
            }

            // High-Paying Affiliate Offers Section
            item {
                Text(
                    text = "High-Converting Creator Offers",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                AffiliateOfferCard(
                    title = "AI Video Production Suite Pro",
                    payout = "45% Recurring Monthly",
                    category = "Software & Creator Tools",
                    affiliateUrl = "https://viralboost.ai/partner/tools?tag=$affiliateTag",
                    isVip = isVip,
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Affiliate Link", "https://viralboost.ai/partner/tools?tag=$affiliateTag"))
                        Toast.makeText(context, "Partner link copied!", Toast.LENGTH_SHORT).show()
                    },
                    onUpgrade = onNavigateToVip
                )
            }

            item {
                AffiliateOfferCard(
                    title = "Creator Microphone & Lighting Kit",
                    payout = "12% Per Sale (+10 Coins / +3 Diamonds)",
                    category = "Studio Hardware",
                    affiliateUrl = "https://viralboost.ai/partner/gear?tag=$affiliateTag",
                    isVip = isVip,
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Affiliate Link", "https://viralboost.ai/partner/gear?tag=$affiliateTag"))
                        Toast.makeText(context, "Partner link copied!", Toast.LENGTH_SHORT).show()
                    },
                    onUpgrade = onNavigateToVip
                )
            }

            // =========================================================================
            // SECTION: TRENDING & AFFORDABLE PRODUCTS FEED (MEESHO, FLIPKART, AMAZON)
            // =========================================================================
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Trending & Affordable Products Feed",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Meesho • Flipkart • Amazon • Budget Deals",
                                    fontSize = 11.sp,
                                    color = NeonCyan
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SuccessGreen.copy(alpha = 0.2f))
                                .border(1.dp, SuccessGreen, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text("100% Verified", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Filter Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSel = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) BrandPurple else DarkSurfaceElevated)
                                    .border(1.dp, if (isSel) BrandPurple else DarkSurfaceBorder, RoundedCornerShape(16.dp))
                                    .clickable {
                                        if (selectedCategory != cat) {
                                            AdManager.triggerMilestoneInterstitial(
                                                context = context,
                                                isVip = isVip,
                                                milestoneName = "Affiliate Filter Category: $cat",
                                                onShowInterstitialFallback = { onDismiss ->
                                                    pendingDismissAction = {
                                                        onDismiss()
                                                        selectedCategory = cat
                                                    }
                                                    showMilestoneAdDialog = true
                                                },
                                                onComplete = { selectedCategory = cat }
                                            )
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(cat, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (isSel) Color.White else Color.LightGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Platform Filter Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(platforms) { plat ->
                            val isSel = selectedPlatform == plat
                            val platColor = when (plat) {
                                "Meesho" -> Color(0xFFEC4899)
                                "Flipkart" -> Color(0xFF3B82F6)
                                "Amazon" -> Color(0xFFF59E0B)
                                else -> NeonCyan
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSel) platColor.copy(alpha = 0.3f) else DarkSurface)
                                    .border(1.dp, if (isSel) platColor else DarkSurfaceBorder, RoundedCornerShape(14.dp))
                                    .clickable {
                                        if (selectedPlatform != plat) {
                                            AdManager.triggerMilestoneInterstitial(
                                                context = context,
                                                isVip = isVip,
                                                milestoneName = "Affiliate Filter Store: $plat",
                                                onShowInterstitialFallback = { onDismiss ->
                                                    pendingDismissAction = {
                                                        onDismiss()
                                                        selectedPlatform = plat
                                                    }
                                                    showMilestoneAdDialog = true
                                                },
                                                onComplete = { selectedPlatform = plat }
                                            )
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(plat, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) platColor else Color.Gray)
                            }
                        }
                    }
                }
            }

            // Products Feed Items
            items(filteredProducts, key = { it.id }) { product ->
                AffiliateProductFeedCard(
                    product = product,
                    affiliateTag = affiliateTag,
                    onShare = {
                        val shareText = product.getShareText(affiliateTag)
                        val executeShare = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareChooser = Intent.createChooser(sendIntent, "Share deal on WhatsApp/Social")
                            context.startActivity(shareChooser)
                            // Credit +10 Coins immediately
                            onShareProduct(product)

                            // Success Notification ("Go Viral") immediately after successful share
                            ReferralNotificationHelper.showPostReadyToGoViralNotification(context)
                            Toast.makeText(context, "Congratulations! Your post is ready to go viral!", Toast.LENGTH_LONG).show()
                        }

                        // Affiliate Hub Milestone 2: Exact moment user taps to share/broadcast link
                        AdManager.triggerMilestoneInterstitial(
                            context = context,
                            isVip = userProfile?.isVip == true,
                            milestoneName = "Affiliate Hub - Milestone 2: Product Feed Share",
                            onShowInterstitialFallback = { onDismiss ->
                                pendingDismissAction = {
                                    onDismiss()
                                    executeShare()
                                }
                                showMilestoneAdDialog = true
                            },
                            onComplete = {
                                executeShare()
                            }
                        )
                    },
                    onBuyOrOrder = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        activeProductForOrder = product
                    }
                )
            }

            // Organic AdMob Native Video Ad in middle of product stream
            item {
                NativeVideoAdCard(
                    isVip = isVip,
                    adUnitId = nativeVideoAdUnitId
                )
            }

            // User's Placed Orders & Rewarded Diamonds History
            item {
                PlacedOrdersHistorySection(placedOrders = placedOrders)
            }

            // Universal Mediated Rewarded Ad + Permanent Banner Ad (BP_Banner_Android / BP_Rewarded_Android / AdMob)
            item {
                UniversalMediatedAdDock(
                    isVip = isVip,
                    admobBannerUnitId = bannerAdUnitId,
                    featureLabel = "Affiliate Growth Vault"
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AffiliateLeaderboardView(
                    userProfile = profile,
                    affiliateTag = affiliateTag,
                    onShareProduct = onShareProduct,
                    onSwitchToDeals = { activeAffiliateTab = 0 }
                )
            }
            UniversalMediatedAdDock(
                isVip = isVip,
                admobBannerUnitId = bannerAdUnitId,
                featureLabel = "Affiliate Leaderboard"
            )
        }
    }
}

    // Direct In-App Ordering & Redirect Dialog
    activeProductForOrder?.let { product ->
        ProductOrderDialog(
            product = product,
            affiliateTag = affiliateTag,
            onDismiss = { activeProductForOrder = null },
            onConfirmInAppOrder = { name, phone, address ->
                activeProductForOrder = null
                onPlaceOrder(product, name, phone, address)
                orderCelebrationProduct = product
            },
            onOpenPlatformRedirect = {
                val url = product.getAffiliateUrl(affiliateTag)
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(browserIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open browser. Link copied!", Toast.LENGTH_SHORT).show()
                }
            },
            onClaimPlatformReward = {
                activeProductForOrder = null
                onClaimPurchaseReward(product)
                orderCelebrationProduct = product
            }
        )
    }

    // Order & 3 Diamonds Celebration Dialog
    orderCelebrationProduct?.let { product ->
        Dialog(onDismissRequest = { orderCelebrationProduct = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, DiamondCyan)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(DiamondCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Diamond, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "🎉 +3 Diamonds Credited!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Congratulations! Your order for \"${product.title}\" has been recorded. 3 Diamonds have been credited directly to your wallet for unlocking AI video and image features!",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { orderCelebrationProduct = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Awesome!", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // Dialog 1: Social Channel Handle Edit Dialog
    editingSocialChannel?.let { channelName ->
        val currentHandle = when (channelName) {
            "WhatsApp" -> whatsAppTarget
            "Instagram" -> instagramHandle
            "Telegram" -> telegramChannel
            else -> facebookPage
        }
        EditSocialChannelDialog(
            channelName = channelName,
            initialHandle = currentHandle,
            onDismiss = { editingSocialChannel = null },
            onSave = { updatedHandle ->
                val clean = updatedHandle.trim()
                when (channelName) {
                    "WhatsApp" -> {
                        whatsAppTarget = clean
                        isWhatsAppConnected = true
                        sharedPrefs.edit()
                            .putString("handle_whatsapp", clean)
                            .putBoolean("conn_whatsapp", true)
                            .apply()
                    }
                    "Instagram" -> {
                        instagramHandle = clean
                        isInstagramConnected = true
                        sharedPrefs.edit()
                            .putString("handle_instagram", clean)
                            .putBoolean("conn_instagram", true)
                            .apply()
                    }
                    "Telegram" -> {
                        telegramChannel = clean
                        isTelegramConnected = true
                        sharedPrefs.edit()
                            .putString("handle_telegram", clean)
                            .putBoolean("conn_telegram", true)
                            .apply()
                    }
                    "Facebook" -> {
                        facebookPage = clean
                        isFacebookConnected = true
                        sharedPrefs.edit()
                            .putString("handle_facebook", clean)
                            .putBoolean("conn_facebook", true)
                            .apply()
                    }
                }
                editingSocialChannel = null
                Toast.makeText(context, "$channelName verified & linked successfully! Synced across all screens. ✅", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog 2: 1-Click Multi-Channel "Promote Everywhere" Dialog
    if (showPromoteEverywhereDialog) {
        PromoteEverywhereDialog(
            affiliateTag = affiliateTag,
            products = SampleAffiliateProducts.catalog,
            connectedChannels = listOfNotNull(
                if (isWhatsAppConnected) "WhatsApp ($whatsAppTarget)" else null,
                if (isInstagramConnected) "Instagram ($instagramHandle)" else null,
                if (isTelegramConnected) "Telegram ($telegramChannel)" else null,
                if (isFacebookConnected) "Facebook ($facebookPage)" else null
            ),
            onDismiss = { showPromoteEverywhereDialog = false },
            onConfirmBroadcast = { productToBroadcast ->
                showPromoteEverywhereDialog = false
                val shareText = productToBroadcast.getShareText(affiliateTag)
                val executeBroadcast = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val chooser = Intent.createChooser(sendIntent, "Broadcast Deal Everywhere (WhatsApp / IG / TG / FB)")
                    context.startActivity(chooser)
                    // Credit +10 Coins immediately
                    onShareProduct(productToBroadcast)
                    broadcastCelebrationDeal = productToBroadcast

                    // Trigger Success Notification ("Go Viral")
                    ReferralNotificationHelper.showPostReadyToGoViralNotification(context)
                    Toast.makeText(context, "Congratulations! Your post is ready to go viral!", Toast.LENGTH_LONG).show()
                }

                // Affiliate Hub Milestone 2: Exact moment user taps to share/broadcast link
                AdManager.triggerMilestoneInterstitial(
                    context = context,
                    isVip = userProfile?.isVip == true,
                    milestoneName = "Affiliate Hub - Milestone 2: Promote Everywhere Broadcast",
                    onShowInterstitialFallback = { onDismiss ->
                        pendingDismissAction = {
                            onDismiss()
                            executeBroadcast()
                        }
                        showMilestoneAdDialog = true
                    },
                    onComplete = {
                        executeBroadcast()
                    }
                )
            }
        )
    }

    // Dialog 3: Broadcast Celebration Dialog (+10 Coins Credited)
    broadcastCelebrationDeal?.let { product ->
        Dialog(onDismissRequest = { broadcastCelebrationDeal = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "🎉 +10 Coins Credited!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Broadcast complete! \"${product.title}\" has been published across your connected channels with your affiliate tracking tag ($affiliateTag). 10 Coins have been credited directly to your creator wallet!",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { broadcastCelebrationDeal = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Awesome!", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Individual Product Card in the Trending & Affordable Products Feed
 */
@Composable
private fun AffiliateProductFeedCard(
    product: AffiliateProduct,
    affiliateTag: String,
    onShare: () -> Unit,
    onBuyOrOrder: () -> Unit
) {
    val platformColor = when (product.platform.uppercase()) {
        "MEESHO" -> Color(0xFFEC4899)
        "FLIPKART" -> Color(0xFF3B82F6)
        "AMAZON" -> Color(0xFFF59E0B)
        else -> NeonCyan
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Platform Badge + Trending Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(platformColor.copy(alpha = 0.2f))
                        .border(1.dp, platformColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(product.platform, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = platformColor)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(product.badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product Image with discount overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
            ) {
                // Fallback placeholder layer underneath
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = platformColor,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.platform,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                val cardContext = LocalContext.current
                val productImgRequest = remember(product.imageUrl) {
                    ImageRequest.Builder(cardContext)
                        .data(product.imageUrl)
                        .crossfade(150)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build()
                }

                AsyncImage(
                    model = productImgRequest,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Discount Overlay Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDC2626))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("-${product.discountPercent}% OFF", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title and Description
            Text(
                text = product.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = product.description,
                fontSize = 11.sp,
                color = Color.LightGray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rating & Free Delivery Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${product.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(" (${product.reviewCount} reviews)", fontSize = 10.sp, color = Color.Gray)
                }

                Text("🚚 Free Express Delivery", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${product.priceRupees}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "₹${product.originalPriceRupees}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save ₹${product.originalPriceRupees - product.priceRupees}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SuccessGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Gamification Reward Badges for user
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+10 Coins on Share", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Diamond, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+3 Diamonds on Purchase", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DiamondCyan)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dual Action Buttons: Share & Buy/Order
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button 1: Share (Earns 10 Coins)
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("product_share_button_${product.id}"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share (10 🪙)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Button 2: Buy / Order Now (Earns 3 Diamonds)
                Button(
                    onClick = onBuyOrOrder,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp)
                        .testTag("product_buy_button_${product.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = platformColor)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Buy Now (3 💎)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

/**
 * Direct In-App Ordering & Platform Redirect Dialog
 */
@Composable
private fun ProductOrderDialog(
    product: AffiliateProduct,
    affiliateTag: String,
    onDismiss: () -> Unit,
    onConfirmInAppOrder: (name: String, phone: String, address: String) -> Unit,
    onOpenPlatformRedirect: () -> Unit,
    onClaimPlatformReward: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val safeDismiss = {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        onDismiss()
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Direct In-App Order, 1: Open on Platform (Tagged)

    // Form inputs for Direct In-App Order
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var shippingAddress by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = safeDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("product_order_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, BrandPurple)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Order & Claim Reward", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("+3 Diamonds credited upon confirmation", fontSize = 11.sp, color = DiamondCyan)
                    }
                    IconButton(onClick = safeDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Product Preview Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                        Text("Platform: ${product.platform} • Free Delivery", fontSize = 10.sp, color = NeonCyan)
                        Text("Price: ₹${product.priceRupees}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = GoldAccent)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: In-App Direct vs External Platform
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurfaceElevated,
                    contentColor = NeonCyan
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Direct In-App", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Open on ${product.platform}", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    // TAB 0: DIRECT IN-APP ORDER
                    Text("Delivery & Contact Information", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Cash on Delivery / UPI on Delivery. No advance payment required.", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it; formError = null },
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g. Rahul Sharma") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("order_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPurple,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = {
                            if (it.length <= 10 && it.all { ch -> ch.isDigit() }) {
                                customerPhone = it
                                formError = null
                            }
                        },
                        label = { Text("10-Digit Mobile Number") },
                        placeholder = { Text("e.g. 9876543210") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("order_phone_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPurple,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = shippingAddress,
                        onValueChange = { shippingAddress = it; formError = null },
                        label = { Text("Full Delivery Address & House No.") },
                        placeholder = { Text("Flat 402, Green Heights, Street 5, City") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("order_address_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPurple,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pincode,
                        onValueChange = {
                            if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                pincode = it
                                formError = null
                            }
                        },
                        label = { Text("6-Digit Pincode") },
                        placeholder = { Text("e.g. 110001") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("order_pincode_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPurple,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    if (formError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(formError!!, color = Color(0xFFEF4444), fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (customerName.isBlank() || customerPhone.length < 10 || shippingAddress.isBlank() || pincode.length < 6) {
                                formError = "Please fill in all delivery details completely"
                                return@Button
                            }
                            focusManager.clearFocus(force = true)
                            keyboardController?.hide()
                            val fullAddress = "$shippingAddress, Pincode: $pincode"
                            onConfirmInAppOrder(customerName, customerPhone, fullAddress)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_in_app_order_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm Order & Claim 3 💎 Diamonds", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                    }
                } else {
                    // TAB 1: REDIRECT TO MEESHO / FLIPKART / AMAZON
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Official ${product.platform} Affiliate Redirect",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Opens the official ${product.platform} app/store with owner tracking tag ($affiliateTag). Complete the purchase to generate affiliate commissions!",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onOpenPlatformRedirect,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("open_platform_redirect_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPurple)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open ${product.platform} (With Tag $affiliateTag)", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Secondary: Claim Purchase Reward
                        OutlinedButton(
                            onClick = onClaimPlatformReward,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("claim_platform_purchase_reward_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, DiamondCyan)
                        ) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = DiamondCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("I Completed Purchase: Claim 3 💎 Diamonds", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DiamondCyan)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section displaying Placed Orders and Claimed Diamond History
 */
@Composable
private fun PlacedOrdersHistorySection(
    placedOrders: List<ProductOrderRecord>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Your E-Commerce Order History", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("${placedOrders.size} Orders", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (placedOrders.isEmpty()) {
                Text(
                    text = "No orders placed yet. Browse the trending feed above to buy deals and earn 3 💎 Diamonds per purchase!",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.US) }
                    placedOrders.take(5).forEach { order ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(order.productTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                                Text("${order.platform} • ₹${order.priceRupees} • ${sdf.format(Date(order.timestamp))}", fontSize = 10.sp, color = Color.Gray)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DiamondCyan.copy(alpha = 0.2f))
                                    .border(1.dp, DiamondCyan, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("+3 💎 Earned", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DiamondCyan)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AffiliateOfferCard(
    title: String,
    payout: String,
    category: String,
    affiliateUrl: String,
    isVip: Boolean,
    onCopy: () -> Unit,
    onUpgrade: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = category, fontSize = 10.sp, color = Color.Gray)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldAccent.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = payout, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isVip) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = affiliateUrl.take(36) + "...",
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy link", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1B4B))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Partner Link: [Locked]", fontSize = 11.sp, color = Color.Gray)
                    Text("VIP Only", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VipGold)
                }
            }
        }
    }
}

/**
 * Universal E-Commerce & Affiliate Link Broadcast Card
 * Explicitly supports pasting and broadcasting links from all major online shopping & affiliate platforms:
 * Amazon, Flipkart, Meesho, Myntra, and EarnKaro.
 */
@Composable
private fun UniversalProductLinkBroadcastCard(
    affiliateTag: String,
    connectedChannelsCount: Int,
    onTriggerMilestone1: () -> Unit = {},
    onBroadcastCustomLink: (link: String, detectedPlatform: String) -> Unit,
    onCopyPromoLink: (link: String, detectedPlatform: String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var inputUrl by remember { mutableStateOf("") }

    val detectedPlatform = remember(inputUrl) {
        val lower = inputUrl.lowercase().trim()
        when {
            lower.contains("amazon") -> "Amazon"
            lower.contains("flipkart") -> "Flipkart"
            lower.contains("meesho") -> "Meesho"
            lower.contains("myntra") -> "Myntra"
            lower.contains("earnkaro") -> "EarnKaro"
            lower.isNotBlank() -> "Universal E-Commerce"
            else -> ""
        }
    }

    val platformColor = remember(detectedPlatform) {
        when (detectedPlatform) {
            "Amazon" -> Color(0xFFFF9900)
            "Flipkart" -> Color(0xFF2874F0)
            "Meesho" -> Color(0xFFE91E63)
            "Myntra" -> Color(0xFFFF3F6C)
            "EarnKaro" -> Color(0xFF00C853)
            "Universal E-Commerce" -> NeonCyan
            else -> BrandPurple
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("universal_product_link_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row with Gold & Cyan Accents
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Universal Product Link Broadcast",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Amazon • Flipkart • Meesho • Myntra • EarnKaro",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = GoldAccent
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldAccent.copy(alpha = 0.15f))
                        .border(1.dp, GoldAccent, RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "+10 Coins 🪙",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Guide description matching exact prompt specification
            Text(
                text = "Paste any product link from Amazon, Flipkart, Meesho, Myntra, or EarnKaro to generate and share your promotional links instantly.",
                fontSize = 11.5.sp,
                color = Color.LightGray,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Platform Support Badges / Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf(
                    Triple("Amazon", "🛒", Color(0xFFFF9900)),
                    Triple("Flipkart", "🛍️", Color(0xFF2874F0)),
                    Triple("Meesho", "🏷️", Color(0xFFE91E63)),
                    Triple("Myntra", "👗", Color(0xFFFF3F6C)),
                    Triple("EarnKaro", "💰", Color(0xFF00C853))
                ).forEach { (store, icon, color) ->
                    val isSelected = detectedPlatform.equals(store, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) color.copy(alpha = 0.3f) else Color(0xFF1E293B))
                            .border(
                                1.dp,
                                if (isSelected) color else Color(0xFF334155),
                                RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                if (inputUrl.isBlank()) {
                                    val sampleUrl = when (store) {
                                        "Amazon" -> "https://www.amazon.in/dp/B0BDK6Z5VR"
                                        "Flipkart" -> "https://www.flipkart.com/p/itm4892k39"
                                        "Meesho" -> "https://www.meesho.com/s/p/trending-deal"
                                        "Myntra" -> "https://www.myntra.com/fashion-essentials"
                                        else -> "https://earnkaro.com/deal/smart-electronics"
                                    }
                                    inputUrl = sampleUrl
                                }
                            }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$icon $store",
                            fontSize = 9.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Universal Product Link Input Box
            OutlinedTextField(
                value = inputUrl,
                onValueChange = { newVal ->
                    val wasEmpty = inputUrl.isBlank()
                    inputUrl = newVal
                    val trimmed = newVal.trim()
                    if (wasEmpty && (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.contains("amazon") || trimmed.contains("flipkart") || trimmed.contains("meesho") || trimmed.contains("myntra") || trimmed.contains("earnkaro"))) {
                        onTriggerMilestone1()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("universal_product_link_input"),
                placeholder = {
                    Text(
                        text = "Paste any product link from Amazon, Flipkart, Meesho, Myntra, or EarnKaro to generate and share your promotional links instantly.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = if (detectedPlatform.isNotEmpty()) platformColor else NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        if (inputUrl.isNotBlank()) {
                            IconButton(
                                onClick = { inputUrl = "" },
                                modifier = Modifier.size(28.dp).testTag("clear_product_link_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                val text = clipboardManager.getText()?.text
                                if (!text.isNullOrBlank()) {
                                    inputUrl = text.trim()
                                    Toast.makeText(context, "Link pasted from clipboard! 📋", Toast.LENGTH_SHORT).show()
                                    onTriggerMilestone1()
                                } else {
                                    Toast.makeText(context, "Clipboard is empty!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(32.dp).testTag("paste_product_link_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Paste Link",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (detectedPlatform.isNotEmpty()) platformColor else BrandPurple,
                    unfocusedBorderColor = if (detectedPlatform.isNotEmpty()) platformColor.copy(alpha = 0.6f) else DarkSurfaceBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A)
                ),
                maxLines = 3
            )

            // Detected Platform Banner
            if (detectedPlatform.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(platformColor.copy(alpha = 0.15f))
                        .border(1.dp, platformColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = platformColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$detectedPlatform Product Link Detected",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Tag #$affiliateTag Attached",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (inputUrl.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please paste a product link from Amazon, Flipkart, Meesho, Myntra, or EarnKaro first!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            onBroadcastCustomLink(inputUrl.trim(), detectedPlatform.ifBlank { "E-Commerce Store" })
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("broadcast_custom_link_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPurple
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Broadcast Link 🚀",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = {
                        if (inputUrl.isBlank()) {
                            Toast.makeText(context, "Paste a link first to generate promo copy!", Toast.LENGTH_SHORT).show()
                        } else {
                            onCopyPromoLink(inputUrl.trim(), detectedPlatform.ifBlank { "E-Commerce Store" })
                        }
                    },
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("copy_custom_promo_link_button"),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NeonCyan
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Copy Promo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Social Media Account Linking & 1-Click Multi-Channel Promotion Card
 */
@Composable
private fun SocialAccountsAndPromoteCard(
    isWhatsAppConnected: Boolean,
    isInstagramConnected: Boolean,
    isTelegramConnected: Boolean,
    isFacebookConnected: Boolean,
    whatsAppTarget: String,
    instagramHandle: String,
    telegramChannel: String,
    facebookPage: String,
    onToggleWhatsApp: () -> Unit,
    onToggleInstagram: () -> Unit,
    onToggleTelegram: () -> Unit,
    onToggleFacebook: () -> Unit,
    onEditChannel: (String) -> Unit,
    onPromoteEverywhere: () -> Unit
) {
    val activeChannelsCount = listOf(isWhatsAppConnected, isInstagramConnected, isTelegramConnected, isFacebookConnected).count { it }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("social_accounts_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, BrandPurple)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
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
                            .background(NeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Connected Social Channels",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Link accounts & broadcast deals in 1 click",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (activeChannelsCount > 0) SuccessGreen.copy(alpha = 0.2f) else Color(0xFF334155))
                        .border(1.dp, if (activeChannelsCount > 0) SuccessGreen else Color.Gray, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$activeChannelsCount / 4 Ready",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeChannelsCount > 0) SuccessGreen else Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Social Channels Grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SocialChannelRowItem(
                    name = "WhatsApp",
                    targetInfo = whatsAppTarget,
                    brandColor = Color(0xFF25D366),
                    iconEmoji = "💬",
                    isConnected = isWhatsAppConnected,
                    onToggle = onToggleWhatsApp,
                    onConnectVerify = { onEditChannel("WhatsApp") },
                    onEdit = { onEditChannel("WhatsApp") }
                )

                SocialChannelRowItem(
                    name = "Instagram",
                    targetInfo = instagramHandle,
                    brandColor = Color(0xFFE1306C),
                    iconEmoji = "📸",
                    isConnected = isInstagramConnected,
                    onToggle = onToggleInstagram,
                    onConnectVerify = { onEditChannel("Instagram") },
                    onEdit = { onEditChannel("Instagram") }
                )

                SocialChannelRowItem(
                    name = "Telegram",
                    targetInfo = telegramChannel,
                    brandColor = Color(0xFF0088CC),
                    iconEmoji = "✈️",
                    isConnected = isTelegramConnected,
                    onToggle = onToggleTelegram,
                    onConnectVerify = { onEditChannel("Telegram") },
                    onEdit = { onEditChannel("Telegram") }
                )

                SocialChannelRowItem(
                    name = "Facebook",
                    targetInfo = facebookPage,
                    brandColor = Color(0xFF1877F2),
                    iconEmoji = "👥",
                    isConnected = isFacebookConnected,
                    onToggle = onToggleFacebook,
                    onConnectVerify = { onEditChannel("Facebook") },
                    onEdit = { onEditChannel("Facebook") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Striking "Promote Everywhere" Single Action Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onPromoteEverywhere() }
                    .testTag("promote_everywhere_banner"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.horizontalGradient(listOf(GoldAccent, NeonCyan))
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF831843), Color(0xFF581C87), Color(0xFF1E1B4B))
                            )
                        )
                        .padding(14.dp)
                ) {
                    Column {
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
                                        .background(GoldAccent.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "1-CLICK PROMOTION SUITE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldAccent,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Multi-Channel Viral Broadcast",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, GoldAccent, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("+10 Coins 🪙", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = GoldAccent)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Instantly publish and broadcast your affiliate deals across all $activeChannelsCount connected social platforms in a single tap.",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onPromoteEverywhere,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("promote_everywhere_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandPurple
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Promote Everywhere (1-Click Broadcast) 🚀",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialChannelRowItem(
    name: String,
    targetInfo: String,
    brandColor: Color,
    iconEmoji: String,
    isConnected: Boolean,
    onToggle: () -> Unit,
    onConnectVerify: () -> Unit,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, if (isConnected) brandColor.copy(alpha = 0.5f) else DarkSurfaceBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(brandColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(iconEmoji, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (isConnected) {
                        Text("Active ✅", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    } else {
                        Text("Paused ⏸️", fontSize = 9.sp, color = Color.Gray)
                    }
                }
                Text(
                    text = targetInfo,
                    fontSize = 10.5.sp,
                    color = if (isConnected) NeonCyan else Color.Gray,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Prominent "Connect & Verify" Action Button
            Button(
                onClick = onConnectVerify,
                modifier = Modifier
                    .height(32.dp)
                    .testTag("connect_verify_button_${name.lowercase()}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConnected) brandColor.copy(alpha = 0.22f) else brandColor
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isConnected) brandColor.copy(alpha = 0.7f) else Color.Transparent
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Default.Verified else Icons.Default.Link,
                    contentDescription = null,
                    tint = if (isConnected) brandColor else Color.White,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Connect & Verify",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isConnected) Color.White else Color.White
                )
            }

            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(28.dp).testTag("edit_channel_${name.lowercase()}")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit $name",
                    tint = Color.LightGray,
                    modifier = Modifier.size(14.dp)
                )
            }

            Switch(
                checked = isConnected,
                onCheckedChange = { onToggle() },
                modifier = Modifier
                    .testTag("toggle_channel_${name.lowercase()}")
                    .scale(0.85f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = brandColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF1E293B)
                )
            )
        }
    }
}

/**
 * Dialog to edit and link a social channel handle or target
 */
@Composable
private fun EditSocialChannelDialog(
    channelName: String,
    initialHandle: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var input by remember { mutableStateOf(initialHandle) }

    Dialog(onDismissRequest = {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        onDismiss()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
                .testTag("edit_social_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, BrandPurple)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connect & Verify $channelName",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Enter and securely verify your $channelName username, channel link, or broadcast handle for 1-click promotion routing (synced with Video Cross-Poster):",
                    fontSize = 11.5.sp,
                    color = Color.LightGray,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("$channelName Handle / Link") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Link, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.fillMaxWidth().testTag("social_channel_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPurple,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        onSave(if (input.isNotBlank()) input.trim() else initialHandle)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("save_social_channel_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Verify & Connect Handle ✅", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

/**
 * 1-Click Multi-Channel Broadcast Dialog
 */
@Composable
private fun PromoteEverywhereDialog(
    affiliateTag: String,
    products: List<AffiliateProduct>,
    connectedChannels: List<String>,
    onDismiss: () -> Unit,
    onConfirmBroadcast: (AffiliateProduct) -> Unit
) {
    var selectedProductIndex by remember { mutableIntStateOf(0) }
    val featuredProduct = products.getOrElse(selectedProductIndex) { products.first() }
    val shareCopy = remember(featuredProduct, affiliateTag) {
        featuredProduct.getShareText(affiliateTag)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("promote_everywhere_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(GoldAccent, BrandPurple)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Header
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
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "1-Click Multi-Channel Broadcast",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Instant +10 Coins Credited on Broadcast 🪙",
                                fontSize = 10.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Product Selection
                Text("Select Product to Promote:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(products.take(6).size) { index ->
                        val prod = products[index]
                        val isSelected = index == selectedProductIndex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) BrandPurple else Color(0xFF1E293B))
                                .border(1.dp, if (isSelected) GoldAccent else DarkSurfaceBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedProductIndex = index }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${prod.platform}: ${prod.title.take(18)}...",
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else Color.LightGray,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Product Preview Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                    ) {
                        AsyncImage(
                            model = featuredProduct.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(featuredProduct.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                        Text("${featuredProduct.platform} • Deal: ₹${featuredProduct.priceRupees} (Flat ${featuredProduct.discountPercent}% OFF)", fontSize = 10.sp, color = NeonCyan)
                        Text("Affiliate Tag: $affiliateTag (Commissions Active)", fontSize = 9.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Target Channels
                Text("Broadcast Targets (${connectedChannels.size} Active):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    connectedChannels.forEach { channelName ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(channelName, fontSize = 11.sp, color = Color.LightGray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Generated Copy Preview
                Text("Generated Multi-Platform Copy:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .padding(8.dp)
                ) {
                    Text(
                        text = shareCopy,
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        lineHeight = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Single Broadcast Button
                Button(
                    onClick = { onConfirmBroadcast(featuredProduct) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_broadcast_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessGreen
                    )
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Broadcast Now Everywhere (+10 🪙)",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * Dedicated Social Media Growth & Reach Acceleration Suite in Affiliate Hub
 */
@Composable
private fun SocialGrowthToolsSuite(
    onNavigateToAiTools: () -> Unit
) {
    val context = LocalContext.current
    var selectedToolTab by remember { mutableIntStateOf(0) } // 0: Viral Hooks, 1: Growth Scripts, 2: Reach Optimization

    // State for Hook Generator
    val platforms = listOf("Instagram Reels", "Facebook Reels", "Shorts")
    var selectedPlatform by remember { mutableStateOf("Instagram Reels") }

    val hookAngles = listOf("Pattern Interrupt", "Curiosity Gap", "Shock Value", "Contrarian")
    var selectedAngle by remember { mutableStateOf("Pattern Interrupt") }

    val currentHookText = remember(selectedPlatform, selectedAngle) {
        when (selectedAngle) {
            "Pattern Interrupt" -> "Stop scrolling if you want to 10x your reach! 99% of creators are promoting products completely backward..."
            "Curiosity Gap" -> "I tested this hidden algorithm trick on $selectedPlatform for 14 days and views jumped 400%..."
            "Shock Value" -> "Do NOT buy this trending product until you see what happens when you pair it with this 1 growth hack!"
            else -> "Everyone says you need 100k followers to earn from affiliate deals. Here is why that's 100% false in 2026..."
        }
    }

    // State for Engagement Scripts
    val scriptStrategies = listOf("Comment Trigger (DM Bot)", "120% Loop Retention", "Follower Lead Magnet")
    var selectedStrategy by remember { mutableStateOf("Comment Trigger (DM Bot)") }

    val currentScriptText = remember(selectedStrategy) {
        when (selectedStrategy) {
            "Comment Trigger (DM Bot)" -> """
                [0:00 - 0:03] THE HOOK: "Want the direct link with 60% discount? Drop 'DEAL' in the comments below!"
                [0:03 - 0:15] THE SHOWCASE: Quick 1.5s cuts highlighting the top features and quality finish.
                [0:15 - 0:45] THE PROOF: Show real customer unboxing reaction and verified 4.8-star rating.
                [0:45 - 0:60] THE CALL TO ACTION: "Comment 'DEAL' right now and my automated AI assistant will DM you the instant link!"
            """.trimIndent()
            "120% Loop Retention" -> """
                [0:00 - 0:03] THE SEAMLESS START: "The reason top creators never reveal their supplier is because..."
                [0:03 - 0:25] THE VALUE DROP: Breakdown of budget price vs high-end boutique markup.
                [0:25 - 0:55] THE AFFILIATE LINK: "Save this Reel for later, link is in my bio with an extra coupon..."
                [0:55 - 0:60] THE SEAMLESS LOOP: "...and that is why..." (Loops smoothly into opening sentence)
            """.trimIndent()
            else -> """
                [0:00 - 0:03] THE HOOK: "Here is the exact 3-step blueprint I used to turn 500 followers into 25,000 Coins & 500 Diamonds affiliate rewards..."
                [0:03 - 0:30] STEP-BY-STEP:
                • Step 1: Pick trending Kurtis or daily utilities under ₹399 with free express delivery.
                • Step 2: Use ViralBoost AI to auto-generate 9:16 high-retention video scripts.
                • Step 3: Embed your affiliate link in bio and broadcast everywhere.
                [0:30 - 0:60] CTA: "Follow @myhandle for daily creator growth blueprints and grab this deal from link in bio!"
            """.trimIndent()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("social_growth_tools_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
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
                            .background(NeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Social Growth & Reach Suite",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Grow Instagram & Facebook Reach Fast",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BrandPurple.copy(alpha = 0.3f))
                        .border(1.dp, BrandPurple, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("AI Viral Suite", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedToolTab,
                containerColor = DarkSurfaceElevated,
                contentColor = NeonCyan
            ) {
                Tab(
                    selected = selectedToolTab == 0,
                    onClick = { selectedToolTab = 0 },
                    text = { Text("Viral Hooks", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedToolTab == 1,
                    onClick = { selectedToolTab = 1 },
                    text = { Text("Growth Scripts", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedToolTab == 2,
                    onClick = { selectedToolTab = 2 },
                    text = { Text("Reach Playbook", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedToolTab) {
                0 -> {
                    // TAB 0: VIRAL HOOKS
                    Column {
                        Text("Target Platform:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(platforms.size) { i ->
                                val p = platforms[i]
                                val isSel = selectedPlatform == p
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) BrandPurple else Color(0xFF1E293B))
                                        .border(1.dp, if (isSel) NeonCyan else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedPlatform = p }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(p, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (isSel) Color.White else Color.LightGray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Hook Angle:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(hookAngles.size) { i ->
                                val angle = hookAngles[i]
                                val isSel = selectedAngle == angle
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) GoldAccent.copy(alpha = 0.25f) else Color(0xFF1E293B))
                                        .border(1.dp, if (isSel) GoldAccent else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedAngle = angle }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(angle, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) GoldAccent else Color.LightGray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Generated Hook Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚡ 3-Sec Pattern Interrupt Hook", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SuccessGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("98.9% Retention", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "\"$currentHookText\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Viral Hook", currentHookText))
                                        Toast.makeText(context, "Viral hook copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy Viral Hook", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: GROWTH & ENGAGEMENT SCRIPTS
                    Column {
                        Text("Conversion Strategy:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(scriptStrategies.size) { i ->
                                val s = scriptStrategies[i]
                                val isSel = selectedStrategy == s
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) BrandPurple else Color(0xFF1E293B))
                                        .border(1.dp, if (isSel) GoldAccent else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedStrategy = s }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(s, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (isSel) Color.White else Color.LightGray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Script Content
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = currentScriptText,
                                    fontSize = 11.sp,
                                    color = Color.LightGray,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Engagement Script", currentScriptText))
                                        Toast.makeText(context, "Full script copied! 📋", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy Full Script", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: ALGORITHM REACH OPTIMIZATION PLAYBOOK
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        AlgorithmTipCard(
                            title = "1. Peak Algorithm Hours",
                            detail = "Post Reels between 11:30 AM or 6:30 PM - 9:00 PM IST when peak mobile consumer activity triggers recommendation algorithms."
                        )
                        AlgorithmTipCard(
                            title = "2. 3-Second Retention Jump Cut",
                            detail = "Cut every breath pause using 1.2x pacing. Shift visual b-roll every 1.8 seconds to prevent viewers from swiping away."
                        )
                        AlgorithmTipCard(
                            title = "3. Reel Caption SEO",
                            detail = "Insert 3 primary keywords in the top 2 lines of your caption so Instagram & Facebook AI categorizes your Reel for Explore reach."
                        )
                        AlgorithmTipCard(
                            title = "4. Pinned Comment Conversion",
                            detail = "Pin your affiliate redirect link with an urgent promo coupon ('Flat 60% OFF Today') in Comment #1 immediately after posting."
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Direct Bridge Button to AI Boost Suite
            Button(
                onClick = onNavigateToAiTools,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("launch_ai_boost_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C1D95))
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Open Full AI Boost Suite (Video & Thumbnail) 🚀",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AlgorithmTipCard(title: String, detail: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            Spacer(modifier = Modifier.height(2.dp))
            Text(detail, fontSize = 10.sp, color = Color.LightGray, lineHeight = 13.sp)
        }
    }
}
