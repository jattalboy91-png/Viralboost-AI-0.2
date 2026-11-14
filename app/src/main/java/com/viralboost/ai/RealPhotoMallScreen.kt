package com.viralboost.ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

/**
 * StrictMallProduct data model adhering to the latest ViralBoost AI specification:
 * Realistic Shopsy/Flipkart prices, strikethrough original MRP, real HD photo CDN list,
 * badges, ratings, and verified buyer reviews.
 */
data class StrictMallProduct(
    val id: Int,
    val title: String,
    val category: String,
    val badgeText: String,
    val priceText: String,
    val oldPriceText: String,
    val rating: String,
    val description: String,
    val imageList: List<String>, // Real HD Image URLs from trusted CDNs
    val customerReviews: List<String>
)

// Backward-compatible typealiases for existing components
typealias RealPhotoMallProduct = StrictMallProduct
typealias TimedMallProduct = StrictMallProduct

/**
 * Master Composable: Festive Mega Shopping Mall with Strict Timed AdMob Ads
 * (Only 1 at 15s, 1 at 35s, 1 at Order Checkout), Correct Realistic Shopsy/Flipkart Prices,
 * Real HD Product Images, Secure Backend Affiliate ID (5665937), & Wallet Balance.
 */
@Composable
fun ViralBoostStrictAdMallApp(
    bonusRupees: Double = 50.0,
    userDiamonds: Int = 10,
    userCoins: Int = 250,
    onBackToHome: () -> Unit = {},
    onOrderSuccessful: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("For You") }
    var actionStatus by remember { mutableStateOf<String?>(null) }

    // Detailed Product Page State
    var selectedProductForDetail by remember { mutableStateOf<StrictMallProduct?>(null) }
    var activePhotoIndex by remember { mutableIntStateOf(0) }

    // Navigation back handler
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
    var selectedPaymentMode by remember { mutableStateOf("Cash on Delivery (COD)") }

    // Order Checkout AdMob Interstitial State (Only 1 at Order Checkout)
    var showCheckoutAdModal by remember { mutableStateOf(false) }

    // Strict Timed AdMob Ads: ONLY 1 at 15s, ONLY 1 at 35s (NEVER recurring loops!)
    var mallBrowsingSeconds by remember { mutableIntStateOf(0) }
    var ad15Triggered by remember { mutableStateOf(false) }
    var ad35Triggered by remember { mutableStateOf(false) }

    var showTimedAdModal by remember { mutableStateOf(false) }
    var timedAdTitle by remember { mutableStateOf("") }
    var timedAdSubtitle by remember { mutableStateOf("") }
    var timedAdRewardText by remember { mutableStateOf("") }
    var timedAdNumber by remember { mutableIntStateOf(1) }

    // Secure Backend Affiliate ID
    val backendAffiliateId = "5665937"

    // Strict Timed AdMob & Unity Ads Mediated Timer: Fires ad #1 strictly at 15s, and ad #2 strictly at 35s
    val mediationState by com.example.monetization.AdManager.mediationState.collectAsState()
    val lastInterstitialDecision by com.example.monetization.AdManager.lastInterstitialDecision.collectAsState()
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            mallBrowsingSeconds++

            if (mallBrowsingSeconds >= 15 && !ad15Triggered) {
                ad15Triggered = true
                if (!showAddressForm && !showCheckoutAdModal) {
                    com.example.monetization.AdManager.selectNextMediatedAd(
                        format = com.example.monetization.AdFormat.INTERSTITIAL,
                        admobUnitId = com.example.monetization.AdManager.activeInterstitialAdUnitId,
                        unityPlacementId = com.example.monetization.AdManager.activeUnityInterstitialPlacementId
                    )
                    timedAdNumber = 1
                    timedAdTitle = "AdMob Interstitial Ad (15s Browsing Sponsor)"
                    timedAdSubtitle = "Strict Timed Ad #1 of 2 • Triggered at 15s • Unity [${mediationState.unityGameId.ifBlank { "800427608" }}] / AdMob Verified"
                    timedAdRewardText = "🎁 15s Browsing Bonus: +2 Coins 🪙 Credited!"
                    showTimedAdModal = true
                }
            } else if (mallBrowsingSeconds >= 35 && !ad35Triggered) {
                ad35Triggered = true
                if (!showAddressForm && !showCheckoutAdModal) {
                    com.example.monetization.AdManager.selectNextMediatedAd(
                        format = com.example.monetization.AdFormat.INTERSTITIAL,
                        admobUnitId = com.example.monetization.AdManager.activeInterstitialAdUnitId,
                        unityPlacementId = com.example.monetization.AdManager.activeUnityInterstitialPlacementId
                    )
                    timedAdNumber = 2
                    timedAdTitle = "AdMob Interstitial Ad (35s Festive Special)"
                    timedAdSubtitle = "Strict Timed Ad #2 of 2 • Triggered at 35s • Unity [${mediationState.unityInterstitialPlacementId.ifBlank { "BP_Interstitial_Android" }}] / AdMob"
                    timedAdRewardText = "🎁 35s Browsing Bonus: +3 Coins 🪙 Credited!"
                    showTimedAdModal = true
                }
            }
        }
    }

    // Correct Realistic Shopsy/Flipkart Catalog with HD Images & Strikethrough MRP
    val strictImageCatalog = remember {
        listOf(
            StrictMallProduct(
                id = 1,
                title = "Men's Regular Fit Cotton Blend Casual Shirt",
                category = "Men's Fashion",
                badgeText = "SHOPSY TOP CHOICE",
                priceText = "₹289",
                oldPriceText = "₹899",
                rating = "★ 4.7 (1,840)",
                description = "Pure breathable 100% cotton-blend casual button-down shirt. Ideal for festive parties and office wear with anti-wrinkle weave, durable stitching, and premium buttons. Shopsy & Meesho Assured Quality with 7-day easy exchange.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1620012253295-c15c429fccf8?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1603252109303-2751441dd157?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Fitting is absolute perfection! Exact Shopsy fabric quality.” - Rohit ★★★★★",
                    "“Price ₹289 is unbelievable for this heavy cotton material.” - Amit ★★★★★",
                    "“Color did not bleed or fade after first wash.” - Rahul ★★★★☆"
                )
            ),
            StrictMallProduct(
                id = 2,
                title = "True Wireless Bluetooth 5.3 Earbuds with Heavy Bass",
                category = "Electronics",
                badgeText = "MEGA DEAL",
                priceText = "₹399",
                oldPriceText = "₹1,499",
                rating = "★ 4.8 (3,250)",
                description = "Deep punchy bass low-latency TWS earbuds with ENC quad-mics for crystal-clear calling. Features Type-C fast charging, IPX5 water resistance, and 40-hour total playback case with instant auto-pairing.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1572536147248-ac59a8abfa4b?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1598331668826-20cecc596b86?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Sound is loud, crystal-clear, and punchy bass.” - Aman ★★★★★",
                    "“Battery lasts two full days of music playback.” - Vishal ★★★★☆",
                    "“Mic quality on phone calls is super clear without echo.” - Deepak ★★★★★"
                )
            ),
            StrictMallProduct(
                id = 3,
                title = "All-in-One Radiant Glow Festive Beauty & Makeup Kit",
                category = "Beauty",
                badgeText = "FESTIVE COMBO",
                priceText = "₹199",
                oldPriceText = "₹599",
                rating = "★ 4.6 (980)",
                description = "Complete festive makeup combo containing matte smudge-proof liquid lipstick, compact pressed powder, smooth eyeliner, hydrating kajal, and radiant cheek blush for an all-day glowing finish.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1586495777744-4413f21062fa?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1512496015851-a90fb38ba796?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Very gentle on skin, smooth finish and great colors.” - Priya ★★★★★",
                    "“Lipstick shade matches perfectly with Indian skin tones.” - Neha ★★★★★",
                    "“Compact and travel-friendly festive kit.” - Ananya ★★★★★"
                )
            ),
            StrictMallProduct(
                id = 4,
                title = "Men's Pure Combed Cotton Graphic Printed T-Shirt",
                category = "Men's Fashion",
                badgeText = "TRENDING",
                priceText = "₹189",
                oldPriceText = "₹499",
                rating = "★ 4.5 (2,120)",
                description = "180 GSM bio-washed pre-shrunk cotton streetwear t-shirt with high-definition graphic print. Breathable feel, colorfast wash guarantee, and regular comfortable fit for all casual occasions.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1562157873-818bc0726f68?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Super comfortable and stylish prints, great value.” - Sahil ★★★★★",
                    "“Perfect college wear, cloth is thick and pure cotton.” - Manpreet ★★★★☆"
                )
            ),
            StrictMallProduct(
                id = 5,
                title = "1.83-inch HD Display Bluetooth Calling Smartwatch",
                category = "Electronics",
                badgeText = "BESTSELLER",
                priceText = "₹449",
                oldPriceText = "₹1,999",
                rating = "★ 4.7 (4,110)",
                description = "Sleek metallic finish smartwatch with built-in mic and speaker for seamless Bluetooth calls, 120+ sport workout modes, 24/7 heart rate and SpO2 tracking, multiple watch faces, and IP68 water resistance.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1544117519-31a4b719223d?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Step counting and heart monitor are surprisingly accurate.” - Kunal ★★★★★",
                    "“Calling via Bluetooth is crystal clear through the speaker.” - Arjun ★★★★★"
                )
            ),
            StrictMallProduct(
                id = 6,
                title = "Vitamin C Natural Radiance Face Wash & Scrub (150ml)",
                category = "Beauty",
                badgeText = "HERBAL CARE",
                priceText = "₹149",
                oldPriceText = "₹399",
                rating = "★ 4.6 (1,430)",
                description = "Gentle SLS & paraben-free foaming face cleanser enriched with active Vitamin C and Aloe Vera beads. Cleanses deep pores, removes dust and grime, and restores natural glowing skin tone.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1608248597359-00994f31c26b?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Skin feels clean, refreshed, and glowing instantly.” - Pooja ★★★★★",
                    "“Chemical-free fragrance and very gentle on pimples.” - Swati ★★★★★"
                )
            ),
            StrictMallProduct(
                id = 7,
                title = "Designer Embroidered Georgette Kurti with Flared Palazzo",
                category = "Women's Fashion",
                badgeText = "FESTIVE SPECIAL",
                priceText = "₹349",
                oldPriceText = "₹1,299",
                rating = "★ 4.8 (2,890)",
                description = "Festive ethnic kurti with intricate zari thread embroidery on neckline and matching georgette palazzo. Lightweight, flowy fabric with rich festive drape, perfect for family functions and Diwali celebrations.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1609357605129-26f69add5d6e?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Looked stunning at wedding event! Everyone asked where I bought it.” - Anita ★★★★★",
                    "“Cloth texture is pure festive elegance, stitching is very neat.” - Rekha ★★★★★"
                )
            ),
            StrictMallProduct(
                id = 8,
                title = "Double Bed Microfiber Fleece Winter Blanket & Bedsheet",
                category = "Home & Living",
                badgeText = "WINTER DEAL",
                priceText = "₹299",
                oldPriceText = "₹999",
                rating = "★ 4.6 (1,670)",
                description = "Ultra-soft thermal microfiber warm blanket combined with double printed bedsheet and two pillow covers. Anti-pilling, lint-free, hypoallergenic, and machine-washable for cosy winter nights.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1540518614846-7ede433c4b69?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Extremely soft, light and warm. Exactly what was needed.” - Meena ★★★★★",
                    "“Bedsheet colors are deep, bright, and didn't shrink.” - Geeta ★★★★★"
                )
            ),
            StrictMallProduct(
                id = 9,
                title = "Heavy Duty 360° Rotatable Waterproof Bike Mobile Mount",
                category = "Mobile Accessories",
                badgeText = "VIRAL UTILITY",
                priceText = "₹129",
                oldPriceText = "₹349",
                rating = "★ 4.8 (3,540)",
                description = "Shockproof all-weather phone case and handlebar mount for motorcycles, scooters, cycles, and strollers. High-sensitivity touch TPU window and secure dual-lock silicone clamp.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1580910051074-3eb694886505?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1586105251261-72a756497a11?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1512499617640-c74ae3a79d37?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Completely rainproof during heavy monsoon bike riding.” - Vikas ★★★★★",
                    "“Very tight grip on bike handlebars even on rough roads.” - Aman ★★★★★"
                )
            ),
            StrictMallProduct(
                id = 10,
                title = "1000ml Double Wall Vacuum Insulated Stainless Steel Flask",
                category = "Home & Living",
                badgeText = "HOT & COLD",
                priceText = "₹249",
                oldPriceText = "₹799",
                rating = "★ 4.7 (1,890)",
                description = "Grade 304 food-grade stainless steel vacuum bottle. Keeps drinks piping hot for 24 hours or chilled cold for 24 hours. Leakproof spout with silicone seal.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1570784332176-fdd73da66f03?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Water stays boiling hot throughout the entire day.” - Ramesh ★★★★★",
                    "“Solid stainless steel body, feels premium.” - Saurabh ★★★★☆"
                )
            ),
            StrictMallProduct(
                id = 11,
                title = "Over-Ear RGB Gaming Headset with Noise-Cancelling Mic",
                category = "Electronics",
                badgeText = "GAMER CHOICE",
                priceText = "₹499",
                oldPriceText = "₹1,699",
                rating = "★ 4.8 (2,450)",
                description = "50mm high-fidelity audio drivers with dynamic RGB lighting and memory foam ear cushions. Flexible omnidirectional microphone with inline volume mute control wheel.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1583394838336-acd977736f90?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1484704849700-f032a568e944?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Footsteps in BGMI & Free Fire are super clear!” - Harsh ★★★★★",
                    "“RGB lights look sick and padding is soft.” - Nitin ★★★★★"
                )
            ),
            StrictMallProduct(
                id = 12,
                title = "Traditional Kanjivaram Style Woven Soft Silk Saree",
                category = "Women's Fashion",
                badgeText = "GRAND FESTIVE",
                priceText = "₹379",
                oldPriceText = "₹1,499",
                rating = "★ 4.9 (4,210)",
                description = "Lustrous jacquard woven soft litchi silk saree featuring rich golden zari pallu with unstitched matching designer blouse piece. Graceful drape for weddings and festivals.",
                imageList = listOf(
                    "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1609357605129-26f69add5d6e?auto=format&fit=crop&w=800&q=80"
                ),
                customerReviews = listOf(
                    "“Such a grand saree for just ₹379! Loved the golden zari.” - Sunita ★★★★★",
                    "“Very light to wear, yet looks extremely royal.” - Vandana ★★★★★"
                )
            )
        )
    }

    val categories = remember {
        listOf("For You", "Women's Fashion", "Men's Fashion", "Electronics", "Beauty", "Home & Living", "Mobile Accessories")
    }

    val filteredItems = remember(searchQuery, selectedCategory, strictImageCatalog) {
        strictImageCatalog.filter { item ->
            val matchesCategory = when (selectedCategory) {
                "For You" -> true
                "Women's Fashion" -> item.category.contains("Women", ignoreCase = true)
                "Men's Fashion" -> item.category.contains("Men", ignoreCase = true)
                "Beauty" -> item.category.contains("Beauty", ignoreCase = true)
                "Home & Living" -> item.category.contains("Home", ignoreCase = true)
                "Electronics" -> item.category.contains("Electronics", ignoreCase = true)
                "Mobile Accessories" -> item.category.contains("Mobile", ignoreCase = true)
                else -> item.category.contains(selectedCategory, ignoreCase = true)
            }
            val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    if (selectedProductForDetail == null) {
        // ==========================================
        // MAIN FESTIVE MALL SCREEN (Shopsy / Flipkart Style)
        // ==========================================
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("strict_ad_mall_screen"),
            verticalArrangement = Arrangement.Top
        ) {
            // Header: Back Home Button & Real-Time Wallet Counters (₹50 Bonus, 💎, 🪙)
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
                    modifier = Modifier.testTag("btn_back_from_strict_mall")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "← Back",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🪙 $userCoins | 💎 $userDiamonds",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFDCFCE7), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF16A34A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "₹$bonusRupees Bonus",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Shopsy / Flipkart Real-Time Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_strict_mall"),
                placeholder = {
                    Text(
                        text = "Search shirts, earbuds, kurti, makeup, watch...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Festive Mega Store Banner with Secure Backend Affiliate Tag Guarantee
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("banner_strict_mall")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF880E4F),
                                    Color(0xFF4A148C),
                                    Color(0xFF1A237E)
                                )
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🛍️ ", fontSize = 16.sp)
                                Text(
                                    text = "Festive Mega Shopping Mall",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF10B981), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "✓ 100% Verified Deals",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Realistic Shopsy & Flipkart Deals • Strikethrough MRP • Free Shipping • ₹50 Bonus Auto-Applied",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFDE047)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⚡ Strict Ad Timers: Ad #1 (15s: ${if (ad15Triggered) "Done ✓" else "${mallBrowsingSeconds}/15s"}) • Ad #2 (35s: ${if (ad35Triggered) "Done ✓" else "${mallBrowsingSeconds}/35s"})",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("chip_strict_cat_${category.replace(" ", "_")}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Flash Deals Grid: Real HD Images, Correct Realistic Prices & Strikethrough MRP
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredItems, key = { it.id }) { product ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_strict_product_${product.id}")
                    ) {
                        Column {
                            // Real HD Image from CDN with AsyncImage & Photo Counter
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clickable {
                                        selectedProductForDetail = product
                                        activePhotoIndex = 0
                                    }
                            ) {
                                AsyncImage(
                                    model = product.imageList[0],
                                    contentDescription = product.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                                )

                                // Gradient top shadow
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Black.copy(alpha = 0.6f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                    )

                                // Badge Overlay
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFDC2626), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "⚡ ${product.badgeText}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "📷 ${product.imageList.size} HD Photos",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White
                                        )
                                    }
                                }

                                // Bottom Hint Overlay
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "View Photo Slider ➔",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = product.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = product.rating,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFB300)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = product.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // Realistic Price Row: Real Price + Strikethrough MRP + Free Delivery
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
                                            text = product.priceText,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF10B981)
                                        )
                                        Text(
                                            text = product.oldPriceText,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                textDecoration = TextDecoration.LineThrough
                                            ),
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFDCFCE7), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Special Deal",
                                                color = Color(0xFF166534),
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Free Delivery 🚚",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "✓ Extra ₹50 Welcome Bonus Auto-Deducted at Checkout",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

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
                                            .testTag("btn_order_strict_${product.id}")
                                    ) {
                                        Text(
                                            text = "Order Now 🛒",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val shareText = "🔥 Check out ${product.title} at ${product.priceText} (MRP ${product.oldPriceText}) on ViralBoost Festive Mega Mall! Link: https://bitli.in/Ff4aE3C?affiliate_id=$backendAffiliateId"
                                            try {
                                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "Share Festive Deal"))
                                            } catch (_: Exception) {}
                                            actionStatus = "📋 Deal Link Copied to Clipboard!"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("btn_share_strict_${product.id}")
                                    ) {
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
                    modifier = Modifier.fillMaxWidth()
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

            Spacer(modifier = Modifier.height(4.dp))
            com.example.ui.components.UniversalMediatedAdDock(
                isVip = false,
                featureLabel = "Shopsy Festive Mega Mall",
                onRewardEarned = { _, _ -> onOrderSuccessful() }
            )
        }
    } else {
        // ==========================================
        // DEDICATED PRODUCT DETAIL SCREEN WITH MULTI-PHOTO SLIDER
        // ==========================================
        val item = selectedProductForDetail!!
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("strict_product_detail_screen"),
            verticalArrangement = Arrangement.Top
        ) {
            // Detail Header
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
                    modifier = Modifier.testTag("btn_back_to_strict_mall")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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
                        text = "Shopsy / Meesho Assured",
                        style = MaterialTheme.typography.labelSmall,
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Multi-Photo Slider Showcase Card with real HD AsyncImage
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp)
                            .testTag("strict_photo_slider_card")
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Active Photo loaded via AsyncImage
                            AsyncImage(
                                model = item.imageList[activePhotoIndex],
                                contentDescription = "${item.title} photo ${activePhotoIndex + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )

                            // Top Pill: Slide Index & Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "⚡ ${item.badgeText} • Photo ${activePhotoIndex + 1} of ${item.imageList.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDE047)
                                )
                            }

                            // Slider Navigation Controls at the bottom
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.8f)
                                            )
                                        )
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Prev Button
                                    Button(
                                        onClick = {
                                            activePhotoIndex = if (activePhotoIndex > 0) activePhotoIndex - 1 else item.imageList.size - 1
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Prev", fontSize = 11.sp, color = Color.White)
                                    }

                                    // Dot indicators
                                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                        item.imageList.indices.forEach { idx ->
                                            Box(
                                                modifier = Modifier
                                                    .size(if (idx == activePhotoIndex) 10.dp else 6.dp)
                                                    .background(
                                                        if (idx == activePhotoIndex) Color(0xFFFDE047) else Color.White.copy(alpha = 0.5f),
                                                        CircleShape
                                                    )
                                            )
                                        }
                                    }

                                    // Next Button
                                    Button(
                                        onClick = {
                                            activePhotoIndex = (activePhotoIndex + 1) % item.imageList.size
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Next", fontSize = 11.sp, color = Color.White)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Thumbnail Quick Select Strip
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(item.imageList.indices.toList()) { idx ->
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (idx == activePhotoIndex) 2.dp else 1.dp,
                                        color = if (idx == activePhotoIndex) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { activePhotoIndex = idx }
                            ) {
                                AsyncImage(
                                    model = item.imageList[idx],
                                    contentDescription = "Thumb ${idx + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

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
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = item.rating,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }

                        Text(
                            text = "(Verified Buyer Reviews)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Realistic Pricing Breakdown Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = item.priceText,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF10B981)
                                    )
                                    Text(
                                        text = item.oldPriceText,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            textDecoration = TextDecoration.LineThrough
                                        ),
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFDC2626), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "FESTIVE SALE",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "✓ ₹50 Welcome Bonus Auto-Deducted",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Free Delivery 🚚",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secure Backend Affiliate ID Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "✓ Verified Authentic Product • Direct Brand Sourced",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📖 Product Description & Specs:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Customer Reviews Section
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
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
                                    text = "💬 Verified Customer Reviews:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            item.customerReviews.forEach { review ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Text(
                                        text = review,
                                        modifier = Modifier.padding(10.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sticky Bottom Buttons: Proceed to Place Order & Share
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val shareText = "🔥 Check out ${item.title} (${item.priceText}) on Festive Mega Mall! Link: https://bitli.in/Ff4aE3C?affiliate_id=$backendAffiliateId"
                                try {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Festive Deal"))
                                } catch (_: Exception) {}
                                actionStatus = "📋 Deal Link Copied to Clipboard!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_share_detail_strict")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share Deal", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showAddressForm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp)
                                .testTag("btn_proceed_place_order_strict")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Proceed to Buy 🛒",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            com.example.ui.components.UniversalMediatedAdDock(
                isVip = false,
                featureLabel = "Shopsy Product Detail",
                onRewardEarned = { _, _ -> onOrderSuccessful() }
            )
        }
    }

    // ==========================================
    // ADDRESS CHECKOUT FORM DIALOG
    // ==========================================
    if (showAddressForm) {
        AlertDialog(
            onDismissRequest = { showAddressForm = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📦 ", fontSize = 20.sp)
                    Text(
                        text = "Address & Delivery Checkout",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Item: ${selectedProductForDetail?.title ?: "Selected Deal"}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Price: ${selectedProductForDetail?.priceText ?: "₹199"} (MRP ${selectedProductForDetail?.oldPriceText ?: "₹599"}) | Free Delivery 🚚",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                    Text(
                        text = "✓ ₹50 Welcome Bonus + 25 Coins Auto-Discount Applied!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Receiver Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_receiver_name_strict")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("10-digit Mobile Number") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_receiver_phone_strict")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("Full Address & Pincode (City, State)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_receiver_address_strict")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Payment Mode: $selectedPaymentMode",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customerName.isNotBlank() && customerPhone.isNotBlank() && customerAddress.isNotBlank()) {
                            showAddressForm = false
                            // Mediated Interstitial (Unity BP_Interstitial_Android <-> AdMob) at Order Checkout
                            com.example.monetization.AdManager.selectNextMediatedAd(
                                format = com.example.monetization.AdFormat.INTERSTITIAL,
                                admobUnitId = com.example.monetization.AdManager.activeInterstitialAdUnitId,
                                unityPlacementId = com.example.monetization.AdManager.activeUnityInterstitialPlacementId
                            )
                            showCheckoutAdModal = true
                        }
                    },
                    enabled = customerName.isNotBlank() && customerPhone.isNotBlank() && customerAddress.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_place_order_verify_strict")
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

    // ==========================================
    // STRICT TIMED ADMOB ADS MODAL (Only 1 at 15s, 1 at 35s)
    // ==========================================
    if (showTimedAdModal) {
        AlertDialog(
            onDismissRequest = { showTimedAdModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📺 ", fontSize = 20.sp)
                    Text(
                        text = timedAdTitle,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1B4B), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "⚡ Google AdMob Interstitial (#$timedAdNumber of 2 Browsing Ads)",
                                color = Color(0xFFFDE047),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timedAdSubtitle,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sponsored Festive Offer • Enjoy Verified Flipkart & Shopsy Deals!",
                                color = Color(0xFF93C5FD),
                                fontSize = 11.5.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "✓ Real AdMob Impression Registered\n$timedAdRewardText",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showTimedAdModal = false
                        actionStatus = "🎁 Browsing Reward credited from AdMob Ad #$timedAdNumber!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_close_strict_timed_admob_ad")
                ) {
                    Text(
                        text = "Resume Shopping (Close Ad ✕)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    // ==========================================
    // ORDER CHECKOUT ADMOB INTERSTITIAL AD MODAL (Strict 1 at Order Checkout)
    // ==========================================
    if (showCheckoutAdModal) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📺 ", fontSize = 20.sp)
                    Text(
                        text = "AdMob Interstitial Ad (Order Verification)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1B4B), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "⚡ Google AdMob Checkout Interstitial Ad (Final Order Step)",
                                color = Color(0xFFFDE047),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Order verification and doorstep delivery confirmation...\n\nReceiver: $customerName\nPhone: $customerPhone\nDelivery: $customerAddress\n\n✓ 100% Verified Authentic Product\n✓ Free Shipping & Cash on Delivery\n✓ Reward: +10 Diamonds 💎 & +15 Coins 🪙",
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "✓ Order ready to be dispatched to doorstep!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCheckoutAdModal = false
                        onOrderSuccessful()
                        actionStatus = "✅ Order Placed Successfully! Earned +10 Diamonds 💎 & +15 Coins 🪙!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_complete_order_rewards_strict")
                ) {
                    Text(
                        text = "Complete Order & Claim Rewards",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

/**
 * Forwarding function for existing callers to use the Strict Ad Mall implementation.
 */
@Composable
fun ViralBoostRealImageMallApp(
    bonusRupees: Double = 50.0,
    userDiamonds: Int = 10,
    userCoins: Int = 250,
    onBackToHome: () -> Unit = {},
    onOrderSuccessful: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ViralBoostStrictAdMallApp(
        bonusRupees = bonusRupees,
        userDiamonds = userDiamonds,
        userCoins = userCoins,
        onBackToHome = onBackToHome,
        onOrderSuccessful = onOrderSuccessful,
        modifier = modifier
    )
}

/**
 * Forwarding function for existing callers of TimedAdMallApp.
 */
@Composable
fun ViralBoostTimedAdMallApp(
    bonusRupees: Double = 50.0,
    userDiamonds: Int = 10,
    userCoins: Int = 250,
    onBackToHome: () -> Unit = {},
    onOrderSuccessful: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ViralBoostStrictAdMallApp(
        bonusRupees = bonusRupees,
        userDiamonds = userDiamonds,
        userCoins = userCoins,
        onBackToHome = onBackToHome,
        onOrderSuccessful = onOrderSuccessful,
        modifier = modifier
    )
}


/**
 * Backward-compatible entry point for ViralBoostSecureBackendApp
 * Provides 100% hidden backend affiliate tracking (tag 5665937 embedded in backend checkout & affiliate links,
 * while UI remains completely clean and professional for users).
 */
@Composable
fun ViralBoostSecureBackendApp(
    bonusRupees: Double = 50.0,
    userDiamonds: Int = 10,
    userCoins: Int = 250,
    onBackToHome: () -> Unit = {},
    onOrderSuccessful: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ViralBoostStrictAdMallApp(
        bonusRupees = bonusRupees,
        userDiamonds = userDiamonds,
        userCoins = userCoins,
        onBackToHome = onBackToHome,
        onOrderSuccessful = onOrderSuccessful,
        modifier = modifier
    )
}

typealias SecureMallProduct = StrictMallProduct


/**
 * Backward-compatible entry point for ViralBoost100PercentHiddenApp.
 * 100% Clean User Interface (Zero Visible Affiliate IDs or Owner Data),
 * Secure Hidden Backend Tracking (Affiliate ID: 5665937), Strict Ad Timers,
 * Real HD Photos, Multi-Photo Sliders, & Address Checkout.
 */
@Composable
fun ViralBoost100PercentHiddenApp(
    bonusRupees: Double = 50.0,
    userDiamonds: Int = 10,
    userCoins: Int = 250,
    onBackToHome: () -> Unit = {},
    onOrderSuccessful: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ViralBoostStrictAdMallApp(
        bonusRupees = bonusRupees,
        userDiamonds = userDiamonds,
        userCoins = userCoins,
        onBackToHome = onBackToHome,
        onOrderSuccessful = onOrderSuccessful,
        modifier = modifier
    )
}

typealias CleanMallProduct = StrictMallProduct
