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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalMall
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FestiveMallProduct(
    val id: Int,
    val title: String,
    val category: String,
    val badgeText: String,
    val priceText: String,
    val rating: String,
    val bannerColor: Color,
    val description: String,
    val photoList: List<String>,
    val customerReviews: List<String>
)

@Composable
fun ViralBoostFestiveMallApp() {
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
                        .testTag("home_festive_mall_launcher_card")
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
                            text = "Festive Special Partner Mall (Direct Home Delivery)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showFullMegaMallScreen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_open_festive_mega_mall")
                        ) {
                            Text(
                                text = "Open Festive Mega Shopping Mall 🚀",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            FullSizeFestiveMallScreen(
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
fun FullSizeFestiveMallScreen(
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
    var selectedProductForDetail by remember { mutableStateOf<FestiveMallProduct?>(null) }
    var activePhotoIndex by remember { mutableIntStateOf(0) }

    // Internal navigation handler
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

    // BACKEND AFFILIATE ID (Securely stored on backend to track commissions)
    val backendAffiliateId = "5665937"

    // Festive Catalog matching user's Meesho / Shopsy reference specification
    val festiveCatalog = remember {
        listOf(
            FestiveMallProduct(
                id = 1,
                title = "Men's Stylish Formal & Casual Shirts",
                category = "Men's Fashion",
                badgeText = "FLASH DEALS",
                priceText = "From ₹149",
                rating = "★ 4.7",
                bannerColor = Color(0xFF880E4F),
                description = "Premium cotton blend shirts designed for comfort, breathability, and modern festive look. Shopsy & Meesho Assured Delivery with easy returns.",
                photoList = listOf(
                    "🖼️ Photo 1: Shirt Front View",
                    "🖼️ Photo 2: Collar & Cuff Detail",
                    "🖼️ Photo 3: Fabric Texture Close-up",
                    "🖼️ Photo 4: Model Look & Fit"
                ),
                customerReviews = listOf(
                    "“Fitting is absolute perfection!” - Rohit ★★★★★",
                    "“Great fabric quality for the price.” - Amit ★★★★★",
                    "“Color did not fade after wash.” - Rahul ★★★★☆"
                )
            ),
            FestiveMallProduct(
                id = 2,
                title = "Pro Wireless Noise-Cancelling Earbuds",
                category = "Electronics",
                badgeText = "FLASH DEALS",
                priceText = "From ₹149",
                rating = "★ 4.8",
                bannerColor = Color(0xFF4A148C),
                description = "High bass TWS wireless earbuds with smart touch control, voice assistant support, and fast charging case delivering 30 hours of music playtime.",
                photoList = listOf(
                    "🖼️ Photo 1: Earbuds with Case",
                    "🖼️ Photo 2: Touch Control Angle",
                    "🖼️ Photo 3: LED Battery View",
                    "🖼️ Photo 4: Compact Pocket Design"
                ),
                customerReviews = listOf(
                    "“Sound is super loud and crisp.” - Aman ★★★★★",
                    "“Battery lasts all day long.” - Vishal ★★★★☆",
                    "“Microphone works great on bike calls.” - Deepak ★★★★★"
                )
            ),
            FestiveMallProduct(
                id = 3,
                title = "Professional Makeup & Glow Kit",
                category = "Beauty",
                badgeText = "FLASH DEALS",
                priceText = "Under ₹199",
                rating = "★ 4.6",
                bannerColor = Color(0xFF0D47A1),
                description = "Complete festive makeup combo including matte lipstick, compact powder, liquid eyeliner, and kajal for an all-day glowing flawless look.",
                photoList = listOf(
                    "🖼️ Photo 1: Complete Kit Items",
                    "🖼️ Photo 2: Lipstick Shades Swatch",
                    "🖼️ Photo 3: Compact Powder Finish",
                    "🖼️ Photo 4: Long-Lasting Wear"
                ),
                customerReviews = listOf(
                    "“Very skin-friendly and smooth.” - Priya ★★★★★",
                    "“Amazing festive discount!” - Neha ★★★★★",
                    "“Lipstick shade is very pretty.” - Ananya ★★★★★"
                )
            ),
            FestiveMallProduct(
                id = 4,
                title = "Men's Graphic Printed Casual T-Shirts",
                category = "Men's Fashion",
                badgeText = "TRENDING",
                priceText = "From ₹99",
                rating = "★ 4.5",
                bannerColor = Color(0xFF1B5E20),
                description = "100% cotton bio-washed trendy graphic t-shirts for daily college wear, outings, and gym workouts. Pre-shrunk durable stitching.",
                photoList = listOf(
                    "🖼️ Photo 1: Graphic T-Shirt Front",
                    "🖼️ Photo 2: Back Print Design",
                    "🖼️ Photo 3: Cotton Fabric Close-up",
                    "🖼️ Photo 4: Stretch Fit Angle"
                ),
                customerReviews = listOf(
                    "“Cotton is very soft and cool.” - Rahul ★★★★★",
                    "“Value for money deal.” - Deepak ★★★★☆",
                    "“Print is high resolution.” - Manoj ★★★★★"
                )
            ),
            FestiveMallProduct(
                id = 5,
                title = "Automatic Electric Mixer Grinder 750W",
                category = "Home & Appliances",
                badgeText = "BESTSELLER",
                priceText = "From ₹899",
                rating = "★ 4.9",
                bannerColor = Color(0xFFE65100),
                description = "Powerful 3-jar heavy duty copper-motor mixer grinder for fine masala grinding, smooth chutney blending, and wet batter mixing.",
                photoList = listOf(
                    "🖼️ Photo 1: Grinder with 3 Jars",
                    "🖼️ Photo 2: Heavy Motor Base",
                    "🖼️ Photo 3: Stainless Steel Blades",
                    "🖼️ Photo 4: 3-Speed Controller"
                ),
                customerReviews = listOf(
                    "“Grinds spices in seconds!” - Sunita ★★★★★",
                    "“Very powerful motor and safe lock.” - Rajesh ★★★★★"
                )
            ),
            FestiveMallProduct(
                id = 6,
                title = "Herbal Brightening & Glowing Face Washes",
                category = "Beauty",
                badgeText = "TOP DEAL",
                priceText = "From ₹79",
                rating = "★ 4.7",
                bannerColor = Color(0xFF311B92),
                description = "Deep cleansing herbal face wash enriched with vitamin C, aloe vera, and natural extracts for clear, oil-free glowing skin.",
                photoList = listOf(
                    "🖼️ Photo 1: Face Wash Tube",
                    "🖼️ Photo 2: Natural Ingredients",
                    "🖼️ Photo 3: Foam Texture Demo",
                    "🖼️ Photo 4: Glow Results Before/After"
                ),
                customerReviews = listOf(
                    "“Makes skin fresh instantly.” - Pooja ★★★★★",
                    "“Chemical-free and gentle on skin.” - Swati ★★★★★"
                )
            ),
            FestiveMallProduct(
                id = 7,
                title = "Designer Embroidered Kurti & Palazzo Sets",
                category = "Women's Fashion",
                badgeText = "FESTIVE SALE",
                priceText = "From ₹99",
                rating = "★ 4.8",
                bannerColor = Color(0xFF880E4F),
                description = "Stunning festive kurti with matching palazzo pants and embroidered dupatta. Direct Meesho and Shopsy verified delivery.",
                photoList = listOf(
                    "🖼️ Photo 1: Kurti Palazzo Set",
                    "🖼️ Photo 2: Neck Embroidery Detail",
                    "🖼️ Photo 3: Dupatta Border Work",
                    "🖼️ Photo 4: Elegant Festive Wear"
                ),
                customerReviews = listOf(
                    "“Looked gorgeous at family function.” - Anita ★★★★★",
                    "“Cloth quality is top notch.” - Rekha ★★★★★"
                )
            ),
            FestiveMallProduct(
                id = 8,
                title = "Super Soft Winter Blankets & Bedsheets",
                category = "Home & Living",
                badgeText = "WINTER SPECIAL",
                priceText = "From ₹199",
                rating = "★ 4.6",
                bannerColor = Color(0xFF006064),
                description = "Plush flannel warm blankets and premium cotton double bedsheets with 2 matching pillow covers for cozy winter nights.",
                photoList = listOf(
                    "🖼️ Photo 1: Blanket Spread View",
                    "🖼️ Photo 2: Soft Fabric Texture",
                    "🖼️ Photo 3: Bedsheet Design",
                    "🖼️ Photo 4: Cozy Warm Feel"
                ),
                customerReviews = listOf(
                    "“Extremely warm and soft blanket.” - Meena ★★★★★",
                    "“Bedsheet colors are vibrant.” - Geeta ★★★★★"
                )
            ),
            FestiveMallProduct(
                id = 9,
                title = "Waterproof Hand & Mobile Phone Holder",
                category = "Mobile Accessories",
                badgeText = "VIRAL UTILITY",
                priceText = "Under ₹155",
                rating = "★ 4.8",
                bannerColor = Color(0xFF263238),
                description = "Universal waterproof phone mount pouch for motorbikes, bicycles, and bathroom wall use with high touch-sensitivity film.",
                photoList = listOf(
                    "🖼️ Photo 1: Holder Case View",
                    "🖼️ Photo 2: Waterproof Seal Test",
                    "🖼️ Photo 3: Bike Mounting Strap",
                    "🖼️ Photo 4: Touch Screen Friendly"
                ),
                customerReviews = listOf(
                    "“Protects phone completely from rain.” - Vikas ★★★★★",
                    "“Very sturdy grip on bike handle.” - Aman ★★★★★"
                )
            )
        )
    }

    val filteredItems = remember(searchQuery, selectedCategory, festiveCatalog) {
        festiveCatalog.filter { item ->
            val matchesCategory = when (selectedCategory) {
                "For You" -> true
                "Women" -> item.category.contains("Women", ignoreCase = true)
                "Men's" -> item.category.contains("Men", ignoreCase = true)
                "Beauty" -> item.category.contains("Beauty", ignoreCase = true)
                "Home" -> item.category.contains("Home", ignoreCase = true)
                "Electronics" -> item.category.contains("Electronics", ignoreCase = true) || item.category.contains("Mobile", ignoreCase = true)
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
        // Main Festive Mall UI (Exact Meesho / Shopsy Style)
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("festive_mega_mall_screen"),
            verticalArrangement = Arrangement.Top
        ) {
            // Top Bar with Back Home Button & Wallet Coins Counter
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
                    modifier = Modifier.testTag("btn_back_from_festive_mall")
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
                            text = "🪙 $userCoins Coins | 💎 $userDiamonds",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Search Bar matching user's Meesho/Shopsy reference design
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "🔍 Search Kurtis, Earbuds, Shoes & more...",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_mall_search")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Tabs Row (For You, Women, Men's, Beauty, Home, Electronics)
            val tabs = listOf("For You", "Women", "Men's", "Beauty", "Home", "Electronics")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tabs) { tab ->
                    val isSelected = selectedCategory == tab
                    Button(
                        onClick = { selectedCategory = tab },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("tab_${tab.replace(" ", "_")}")
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Festive Store Hero Banner matching screenshot
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF6A1B9A)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                            .testTag("festive_store_hero_banner")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = "🪔 FESTIVE STORE 🪔",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFDE047)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Mega Festive Deals with ₹50 Welcome Discount!",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { actionStatus = "🎉 Festive Store Flash Deals Activated! Enjoy extra discounts." },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(
                                        text = "SHOP NOW →",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Products Flash Deals Grid
                items(filteredItems) { product ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedProductForDetail = product
                                activePhotoIndex = 0
                            }
                            .testTag("festive_product_${product.id}")
                    ) {
                        Column {
                            // Product Image Banner with Flash Deal Badge
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .background(product.bannerColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "⚡ ${product.badgeText}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFDE047)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = product.photoList[0],
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Tap for 4 Photos & Details ➔",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.85f)
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

                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = product.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(3.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = product.priceText,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF10B981)
                                    )
                                    Text(
                                        text = "Free Delivery 🚚",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

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
                                            .testTag("btn_order_${product.id}")
                                    ) {
                                        Text(
                                            text = "Order Now 🛒",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val shareText = "🔥 Check out ${product.title} (${product.priceText}) on Festive Mega Mall! Link: https://bitli.in/Ff4aE3C (ID: $backendAffiliateId)"
                                            try {
                                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "Share Festive Deal"))
                                            } catch (_: Exception) {}
                                            actionStatus = "📋 Deal Link Copied Successfully!"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("btn_share_${product.id}")
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
        }
    } else {
        // Detailed Product Page View with Multi-Photo Slider, Description & Customer Reviews
        val item = selectedProductForDetail!!
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("festive_product_detail_screen"),
            verticalArrangement = Arrangement.Top
        ) {
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
                    modifier = Modifier.testTag("btn_back_to_festive_mall")
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
                        text = "Direct Delivery Assured",
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
                    // Multi-Photo Slider Showcase Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = item.bannerColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("festive_photo_slider_card")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "⚡ ${item.badgeText} • Slide ${activePhotoIndex + 1} of ${item.photoList.size}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFDE047)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = item.photoList[activePhotoIndex],
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Next / Previous Slide Navigation Controls
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            activePhotoIndex = if (activePhotoIndex > 0) activePhotoIndex - 1 else item.photoList.size - 1
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Prev", fontSize = 11.sp, color = Color.White)
                                    }

                                    // Dot indicators
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        item.photoList.indices.forEach { idx ->
                                            Box(
                                                modifier = Modifier
                                                    .size(if (idx == activePhotoIndex) 8.dp else 6.dp)
                                                    .background(
                                                        if (idx == activePhotoIndex) Color.White else Color.White.copy(alpha = 0.4f),
                                                        CircleShape
                                                    )
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            activePhotoIndex = (activePhotoIndex + 1) % item.photoList.size
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Next", fontSize = 11.sp, color = Color.White)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
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

                    Spacer(modifier = Modifier.height(6.dp))

                    // Pricing Card
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
                                Text(
                                    text = "Deal Price: ${item.priceText}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "✓ ₹50 Welcome Discount Applied",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = "Free Delivery 🚚",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
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
                                text = "📖 Product Description:",
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
                                    text = "💬 Customer Reviews & Feedback:",
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

                    Button(
                        onClick = { showAddressForm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_proceed_place_order_festive")
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
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }
    }

    // Delivery Address Form Dialog
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
                            showCheckoutAdModal = true
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
                    text = "📺 [AdMob Ad Loaded]\n\nProcessing order with secure backend tracking and applying ₹50 discount...\n\nReceiver: $customerName\nPhone: $customerPhone\nDelivery: $customerAddress\n\n✓ Free Shipping & Cash on Delivery\n✓ Reward: +10 Diamonds 💎 & +15 Coins 🪙"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCheckoutAdModal = false
                        onOrderSuccessful()
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
