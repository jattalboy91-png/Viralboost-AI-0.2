package com.viralboost.ai

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MallProduct(
    val id: Int,
    val title: String,
    val category: String,
    val discountText: String,
    val price: String,
    val oldPrice: String,
    val rating: String,
    val bannerColor: Color = Color(0xFF6200EE),
    val description: String = "Premium designer quality crafted with authentic materials, durable build, and verified vendor warranty.",
    val customerReviews: List<String> = listOf(
        "“Superb quality! Value for money product.” - Verified Buyer ★★★★★",
        "“Fast delivery and accurate product description.” - Happy Customer ★★★★☆"
    )
)

@Composable
fun ViralBoostMallApp() {
    var bonusRupees by remember { mutableStateOf(50.0) } // ₹50 Welcome Bonus
    var userDiamonds by remember { mutableStateOf(10) }
    var userCoins by remember { mutableStateOf(250) }

    var showFullMegaMallScreen by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!showFullMegaMallScreen) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⭐ ViralBoost AI Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💰 My Wallet & Rewards Hub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Wallet: ₹$bonusRupees | 💎 Diamonds: $userDiamonds | 🪙 Coins: $userCoins",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_shopping_now_launcher_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🛍️ Shopping Now - 75% OFF",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Shopsy & Ajio Official Partner Mall (Affiliate ID: 5665937)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showFullMegaMallScreen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_open_full_screen_mega_mall")
                        ) {
                            Text(
                                text = "Open Full-Screen Mega Shopping Mall 🚀",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            FullSizeMegaMallScreen(
                bonusRupees = bonusRupees,
                userDiamonds = userDiamonds,
                userCoins = userCoins,
                onBackToHome = { showFullMegaMallScreen = false },
                onOrderSuccessful = {
                    userDiamonds += 10
                    userCoins += 15
                }
            )
        }
    }
}

@Composable
fun ViralBoostAppMain() {
    ViralBoostMallApp()
}

@Composable
fun FullSizeMegaMallScreen(
    bonusRupees: Double = 50.0,
    userDiamonds: Int = 10,
    userCoins: Int = 250,
    onBackToHome: () -> Unit = {},
    onOrderSuccessful: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    var actionStatus by remember { mutableStateOf<String?>(null) }

    // Detailed Product Page State
    var selectedProductForDetail by remember { mutableStateOf<MallProduct?>(null) }

    // BackHandler manages internal navigation: if in Product Detail, returns to Mall; else returns to Home
    if (selectedProductForDetail != null) {
        BackHandler {
            selectedProductForDetail = null
        }
    } else {
        BackHandler(onBack = onBackToHome)
    }

    // Checkout & Address Form States
    var showAddressForm by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var showCheckoutAdModal by remember { mutableStateOf(false) }

    // Rich Product Catalog with Stunning Banners, Descriptions & Customer Reviews
    val mallCatalog = remember {
        listOf(
            MallProduct(
                id = 1,
                title = "Min 65% Off on Label Ritu Kumar & Ethnic Sets",
                category = "Ethnic Fashion",
                discountText = "Min 65% OFF",
                price = "₹551",
                oldPrice = "₹1,599",
                rating = "★ 4.6",
                bannerColor = Color(0xFF6200EE),
                description = "Premium designer ethnic wear crafted with fine embroidery and comfortable fabric for festive occasions. Includes kurti, dupatta, and matching trousers.",
                customerReviews = listOf(
                    "“Superb quality! Fabric is very soft and comfortable.” - Priya ★★★★★",
                    "“Loved the fitting and vibrant colors. Got many compliments!” - Anita ★★★★☆",
                    "“Worth every rupee, looks exactly as pictured.” - Suman ★★★★★"
                )
            ),
            MallProduct(
                id = 2,
                title = "Min. 65% Off on GAS Apprarels & Designer Shoes",
                category = "Footwear",
                discountText = "Min. 65% OFF",
                price = "₹499",
                oldPrice = "₹1,499",
                rating = "★ 4.8",
                bannerColor = Color(0xFF00897B),
                description = "Lightweight running shoes and trendy casual wear designed for ultimate comfort, breathable mesh, and long-lasting durability on all terrains.",
                customerReviews = listOf(
                    "“Very comfortable for daily morning walk and gym workouts.” - Rohit ★★★★★",
                    "“Value for money product! Grip is top notch.” - Vikas ★★★★★",
                    "“Fits true to size, stylish finish.” - Karan ★★★★☆"
                )
            ),
            MallProduct(
                id = 3,
                title = "Pro Wireless Noise-Cancelling Earbuds & Tech",
                category = "Audio & Tech",
                discountText = "78% OFF",
                price = "₹349",
                oldPrice = "₹1,499",
                rating = "★ 4.7",
                bannerColor = Color(0xFFE65100),
                description = "Crystal clear sound with deep punchy bass, ENC microphone for clear calls, and 30-hour long playtime with ultra-fast Type-C charging case.",
                customerReviews = listOf(
                    "“Bass quality is amazing at this price. Better than expensive buds!” - Aman ★★★★★",
                    "“Battery backup lasts 3-4 days easily on normal use.” - Rahul ★★★★☆",
                    "“Bluetooth connection is instant and stable.” - Sahil ★★★★★"
                )
            ),
            MallProduct(
                id = 4,
                title = "Smart Touch Fitness Watch HR+SpO2 Trackers",
                category = "Smart Gadgets",
                discountText = "76% OFF",
                price = "₹599",
                oldPrice = "₹2,499",
                rating = "★ 4.9",
                bannerColor = Color(0xFF3949AB),
                description = "Advanced health tracking with 24/7 heart rate, blood oxygen (SpO2) monitoring, sleep analyzer, and 20+ active sports fitness modes with water resistance.",
                customerReviews = listOf(
                    "“Looks very premium on the wrist, screen is very bright outdoors.” - Neha ★★★★★",
                    "“Step counter and sleep tracker work with pinpoint accuracy.” - Deepak ★★★★★",
                    "“Battery lasts over a week. Best budget smartwatch!” - Mohit ★★★★★"
                )
            ),
            MallProduct(
                id = 5,
                title = "Waterproof 10-inch LED Ring Light & Creator Tripod",
                category = "Creator Gear",
                discountText = "75% OFF",
                price = "₹299",
                oldPrice = "₹1,299",
                rating = "★ 4.8",
                bannerColor = Color(0xFFC2185B),
                description = "Professional video creator ring light with 3 light modes (warm, cool, daylight), 10 brightness steps, 360-degree phone holder, and 7-foot sturdy tripod.",
                customerReviews = listOf(
                    "“Must-have for Reels and Shorts creators. Video quality improved instantly.” - Ajay ★★★★★",
                    "“Tripod height is great and remote shutter works smoothly.” - Pooja ★★★★★"
                )
            ),
            MallProduct(
                id = 6,
                title = "3-Piece Non-Stick Granite Kitchen Cookware Set",
                category = "Home & Kitchen",
                discountText = "75% OFF",
                price = "₹799",
                oldPrice = "₹3,199",
                rating = "★ 4.5",
                bannerColor = Color(0xFF2E7D32),
                description = "Heavy gauge virgin aluminium cookware with durable granite non-stick coating. PFOA-free, gas and induction compatible with soft-touch cool handles.",
                customerReviews = listOf(
                    "“Food cooks evenly with minimum oil. Easy to clean.” - Meenakshi ★★★★★",
                    "“Solid build and granite coating looks very elegant.” - Rekha ★★★★☆"
                )
            )
        )
    }

    val filteredItems = remember(selectedCategory, mallCatalog) {
        if (selectedCategory == "All") mallCatalog else mallCatalog.filter { it.category == selectedCategory }
    }

    if (selectedProductForDetail == null) {
        // Main Mega Mall Grid View
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("full_size_mega_mall_screen"),
            verticalArrangement = Arrangement.Top
        ) {
            // Top Header with Back Home Button & Mall Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBackToHome,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_back_home_from_mall")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Home",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "← Back Home",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalMall,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🛍️ Mega Shopping Mall",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Wallet Balance & Discount Dashboard Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mall_wallet_rewards_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⭐ Your Active Rewards & Wallet",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "₹50 BONUS ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🎁 ₹50 वेलकम बोनस डिस्काउंट सक्रिय! | 💎 डायमंड्स: $userDiamonds | 🪙 कॉइन्स: $userCoins\n(Affiliate ID: 5665937)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Pills
            val filterTabs = listOf(
                "All" to "All",
                "Fashion" to "Ethnic Fashion",
                "Gadgets" to "Smart Gadgets",
                "Footwear" to "Footwear",
                "Audio" to "Audio & Tech",
                "Creator Gear" to "Creator Gear",
                "Home" to "Home & Kitchen"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterTabs) { (tabLabel, categoryKey) ->
                    val isSelected = selectedCategory == categoryKey || (categoryKey == "All" && selectedCategory == "All")
                    Button(
                        onClick = { selectedCategory = categoryKey },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("filter_tab_${categoryKey.replace(" ", "_")}")
                    ) {
                        Text(
                            text = tabLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Banner-Style Rich Product Cards List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredItems) { product ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Opens Dedicated Detailed Product Page
                                selectedProductForDetail = product
                            }
                            .testTag("product_card_${product.id}")
                    ) {
                        Column {
                            // Stunning Product Banner / Visual Image Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                                    .background(product.bannerColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text(
                                        text = "🌟 ${product.discountText} • FESTIVE SALE",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Tap to view reviews & details ➔",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = product.category,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = product.rating,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFB300)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = product.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Price: ${product.price}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF10B981)
                                        )
                                        Text(
                                            text = "(Old: ${product.oldPrice})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                    }

                                    Text(
                                        text = "Affiliate ID: 5665937",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            selectedProductForDetail = product
                                            showAddressForm = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("btn_order_now_${product.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Order Now 🛒",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val shareText = "🔥 Check out ${product.title} at ${product.price} (${product.discountText}) on Shopsy/Ajio! Use official link: https://bitli.in/Ff4aE3C (Affiliate ID: 5665937)"
                                            try {
                                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "Share Shopsy Deal"))
                                            } catch (_: Exception) {}
                                            actionStatus = "📋 Affiliate Deal Link Copied! (ID: 5665937)"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("btn_share_deal_${product.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Share Deal",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            actionStatus?.let { status ->
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mall_action_status_banner")
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    } else {
        // =========================================================================
        // Dedicated Product Detail Page View with Description, Star Ratings & Reviews
        // =========================================================================
        val item = selectedProductForDetail!!

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("product_detail_screen"),
            verticalArrangement = Arrangement.Top
        ) {
            // Header: Back to Mall Button & Screen Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { selectedProductForDetail = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_back_to_mall")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Mall",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "← Back to Mall",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Verified Deal",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Large Stunning Hero Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = item.bannerColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .testTag("detail_hero_banner")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = item.discountText,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Category: ${item.category} • Official Partner Mall",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title & Category
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Rating & Verified Buyer Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = item.rating,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }

                        Text(
                            text = "(Verified Buyers & Quality Checked)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Deal Price: ${item.price}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF10B981)
                                    )
                                    Text(
                                        text = "MRP: ${item.oldPrice}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.outline,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }
                                Text(
                                    text = "✓ ₹50 Welcome Discount applied at checkout",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = "Tag: 5665937",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Product Description Section
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "📖 Product Description:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Customer Reviews & Feedback Section
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RateReview,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "💬 Customer Reviews & Feedback:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            item.customerReviews.forEach { review ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = review,
                                        modifier = Modifier.padding(10.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Bottom Order Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showAddressForm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("btn_proceed_place_order")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Proceed to Place Order 🛒",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = {
                                val shareText = "🔥 Check out ${item.title} at ${item.price} (${item.discountText}) on Shopsy/Ajio! Use official link: https://bitli.in/Ff4aE3C (Affiliate ID: 5665937)"
                                try {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Shopsy Deal"))
                                } catch (_: Exception) {}
                                actionStatus = "📋 Affiliate Deal Link Copied! (ID: 5665937)"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_share_from_detail")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Share Deal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Address Form Dialog
    if (showAddressForm) {
        AlertDialog(
            onDismissRequest = { showAddressForm = false },
            title = {
                Text(
                    text = "📦 Delivery Address Form",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Product: ${selectedProductForDetail?.title ?: "Selected Deal"}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Receiver Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_receiver_name")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Mobile Number") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_receiver_phone")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("Full Delivery Address & Pincode") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_receiver_address")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customerName.isNotBlank() && customerPhone.isNotBlank() && customerAddress.isNotBlank()) {
                            showAddressForm = false
                            showCheckoutAdModal = true // Triggers AdMob Ad before final order success
                        }
                    },
                    enabled = customerName.isNotBlank() && customerPhone.isNotBlank() && customerAddress.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_place_order_verify")
                ) {
                    Text(
                        text = "Place Order & Verify (View Ad)",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressForm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // AdMob Interstitial Ad during Checkout
    if (showCheckoutAdModal) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "AdMob Interstitial Ad (Processing Order)",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "📺 [AdMob Ad Loaded]\n\nProcessing order with Affiliate ID: 5665937 and applying ₹50 discount...\n\nReceiver: $customerName\nPhone: $customerPhone\nDelivery: $customerAddress\n\n✓ Free Shipping & Cash on Delivery\n✓ Reward: +10 Diamonds 💎 & +15 Coins 🪙"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCheckoutAdModal = false
                        onOrderSuccessful() // Adds +10 Diamonds and +15 Coins
                        actionStatus = "✅ Order Placed Successfully! Earned +10 Diamonds & +15 Coins!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_complete_order_rewards")
                ) {
                    Text(
                        text = "Complete Order & Get Rewards",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}
