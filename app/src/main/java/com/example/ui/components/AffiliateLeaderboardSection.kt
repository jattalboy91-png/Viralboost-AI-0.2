package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.UserProfile
import com.example.data.model.AffiliateProduct
import com.example.data.model.SampleAffiliateProducts
import com.example.monetization.MonetizationConstants
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

/**
 * Data model for top affiliate earners ranked strictly by Coins Earned and Diamonds Collected.
 * Zero cash / monetary figures.
 */
data class AffiliateTopEarner(
    val rank: Int,
    val name: String,
    val handle: String,
    val sharedLinks: Int,
    val clicks: Int,
    val coinsEarned: Int,
    val diamondsEarned: Int,
    val isVip: Boolean,
    val primaryPlatform: String,
    val badge: String,
    val bestDealTitle: String,
    val winningStrategy: String,
    val isUser: Boolean = false
)

object SampleAffiliateLeaderboardData {
    fun getTopEarners(userProfile: UserProfile?, timeframe: Int): List<AffiliateTopEarner> {
        val userCoins = userProfile?.coins ?: 100
        val userDiamonds = userProfile?.diamonds ?: 12
        val isVip = userProfile?.isVip ?: false
        val userSharedLinks = (userCoins / 10).coerceAtLeast(1)
        val userClicks = userSharedLinks * 14 + 18

        val multiplier = when (timeframe) {
            1 -> 3.8
            2 -> 11.5
            else -> 1.0
        }

        val baseList = listOf(
            AffiliateTopEarner(
                rank = 1,
                name = "Aarav 'Viral' Sharma",
                handle = "@aarav_growth",
                sharedLinks = (482 * multiplier).toInt(),
                clicks = (5840 * multiplier).toInt(),
                coinsEarned = (4820 * multiplier).toInt(),
                diamondsEarned = (145 * multiplier).toInt(),
                isVip = true,
                primaryPlatform = "WhatsApp & Reels",
                badge = "👑 Affiliate King",
                bestDealTitle = "Anarkali Embroidered Kurti Set (+10 Coins / +3 Diamonds)",
                winningStrategy = "Shares high-demand Kurti deals daily at 7:00 PM to WhatsApp status and VIP broadcast channels with direct COD purchase link."
            ),
            AffiliateTopEarner(
                rank = 2,
                name = "Priya TechCraft",
                handle = "@priya_crafts",
                sharedLinks = (391 * multiplier).toInt(),
                clicks = (4620 * multiplier).toInt(),
                coinsEarned = (3910 * multiplier).toInt(),
                diamondsEarned = (118 * multiplier).toInt(),
                isVip = true,
                primaryPlatform = "Telegram & Shorts",
                badge = "⚡ Viral Sharer",
                bestDealTitle = "Pure Cotton Straight Floral Print Kurti (+10 Coins / +3 Diamonds)",
                winningStrategy = "Pins limited-stock budget deals to Telegram channel with urgency flash alerts & auto-syndication."
            ),
            AffiliateTopEarner(
                rank = 3,
                name = "Rohan Finance Fast",
                handle = "@rohan_money",
                sharedLinks = (325 * multiplier).toInt(),
                clicks = (3980 * multiplier).toInt(),
                coinsEarned = (3250 * multiplier).toInt(),
                diamondsEarned = (96 * multiplier).toInt(),
                isVip = true,
                primaryPlatform = "Instagram Reels",
                badge = "🔥 Deal Hunter",
                bestDealTitle = "Multi-Functional Vegetable Slicer & Chopper (+10 Coins / +3 Diamonds)",
                winningStrategy = "Creates 10-second snappy kitchen utility reels with product link in bio and broadcast channel."
            ),
            AffiliateTopEarner(
                rank = 4,
                name = "Kavya Vlog Studio",
                handle = "@kavya_daily",
                sharedLinks = (284 * multiplier).toInt(),
                clicks = (3120 * multiplier).toInt(),
                coinsEarned = (2840 * multiplier).toInt(),
                diamondsEarned = (82 * multiplier).toInt(),
                isVip = true,
                primaryPlatform = "WhatsApp Status",
                badge = "🛍️ Trendsetter",
                bestDealTitle = "Festive Chikankari Georgette Kurta (+10 Coins / +3 Diamonds)",
                winningStrategy = "Curates seasonal festive fashion looks with direct links to verified Meesho & Flipkart listings."
            ),
            AffiliateTopEarner(
                rank = 5,
                name = "Dev Gaming Nexus",
                handle = "@dev_nexus",
                sharedLinks = (219 * multiplier).toInt(),
                clicks = (2540 * multiplier).toInt(),
                coinsEarned = (2190 * multiplier).toInt(),
                diamondsEarned = (64 * multiplier).toInt(),
                isVip = false,
                primaryPlatform = "Telegram Channel",
                badge = "🎮 Tech Booster",
                bestDealTitle = "Wireless Bluetooth Neckband with Fast Charge (+10 Coins / +3 Diamonds)",
                winningStrategy = "Shares budget gadget deals tailored for mobile gamers to earn maximum Coins & Diamonds."
            ),
            AffiliateTopEarner(
                rank = 6,
                name = "Neha Comedy Bites",
                handle = "@neha_bites",
                sharedLinks = (182 * multiplier).toInt(),
                clicks = (1980 * multiplier).toInt(),
                coinsEarned = (1820 * multiplier).toInt(),
                diamondsEarned = (52 * multiplier).toInt(),
                isVip = false,
                primaryPlatform = "Facebook Reels",
                badge = "✨ Rising Star",
                bestDealTitle = "Silk Blend Party Wear Kurti (+10 Coins / +3 Diamonds)",
                winningStrategy = "Uses organic humor in short videos with link embedded in the top comment to gain coin shares."
            ),
            AffiliateTopEarner(
                rank = 7,
                name = "Vikram Fitness Lab",
                handle = "@fit_vikram",
                sharedLinks = (143 * multiplier).toInt(),
                clicks = (1650 * multiplier).toInt(),
                coinsEarned = (1430 * multiplier).toInt(),
                diamondsEarned = (41 * multiplier).toInt(),
                isVip = false,
                primaryPlatform = "Instagram",
                badge = "💪 Health Deals",
                bestDealTitle = "Stainless Steel Insulated Water Bottle 1L (+10 Coins / +3 Diamonds)",
                winningStrategy = "Recommends daily fitness utilities to gym clients and followers."
            ),
            AffiliateTopEarner(
                rank = 8,
                name = "Ananya Beauty Trend",
                handle = "@ananya_glam",
                sharedLinks = (98 * multiplier).toInt(),
                clicks = (1120 * multiplier).toInt(),
                coinsEarned = (980 * multiplier).toInt(),
                diamondsEarned = (29 * multiplier).toInt(),
                isVip = false,
                primaryPlatform = "WhatsApp Groups",
                badge = "💄 Beauty Pro",
                bestDealTitle = "Matte Liquid Lipstick Combo Set (+10 Coins / +3 Diamonds)",
                winningStrategy = "Creates genuine shade swatch videos on WhatsApp groups with direct buy links."
            )
        )

        val userEarner = AffiliateTopEarner(
            rank = 9,
            name = "ViralCreator_99 (You)",
            handle = "@creator_99",
            sharedLinks = (userSharedLinks * multiplier).toInt(),
            clicks = (userClicks * multiplier).toInt(),
            coinsEarned = userCoins,
            diamondsEarned = userDiamonds,
            isVip = isVip,
            primaryPlatform = "Multi-Platform",
            badge = "🚀 Your Position",
            bestDealTitle = "All Trending Deals (+10 Coins on Share)",
            winningStrategy = "Share affiliate links across WhatsApp, Instagram, Telegram & YouTube with ViralBoost automated viral footers.",
            isUser = true
        )

        val all = (baseList + userEarner).sortedByDescending { it.coinsEarned }
        return all.mapIndexed { index, earner -> earner.copy(rank = index + 1) }
    }
}

/**
 * Teaser Spotlight Card displayed on the main Deals Feed tab
 * linking directly to the full Affiliate Coins & Diamonds Leaderboard.
 */
@Composable
fun AffiliateLeaderboardSpotlightCard(
    userProfile: UserProfile?,
    onViewFullLeaderboard: () -> Unit
) {
    val topEarners = remember(userProfile) {
        SampleAffiliateLeaderboardData.getTopEarners(userProfile, 0)
    }
    val top1 = topEarners.firstOrNull()
    val userEarner = topEarners.find { it.isUser }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("affiliate_leaderboard_spotlight"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16112C)),
        border = androidx.compose.foundation.BorderStroke(
            1.4.dp,
            Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFF8A2BE2), Color(0xFF00E676)))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
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
                            .background(Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFFFA000)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Top Creators Leaderboard - Coins & Diamonds",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Ranked strictly by Coins earned & Diamonds collected from shares",
                            fontSize = 10.5.sp,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("+100 💎 Pool", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // #1 Leader Spotlight Row
            if (top1 != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF231840)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🥇", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(top1.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(top1.badge, fontSize = 9.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = "🔗 ${top1.sharedLinks} shared links • 👁️ ${top1.clicks} clicks",
                                    fontSize = 10.sp,
                                    color = Color(0xFF81D4FA)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("${top1.coinsEarned} 🪙 Coins", fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = GoldAccent)
                            Text("${top1.diamondsEarned} 💎 Diamonds", fontSize = 10.5.sp, color = DiamondCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Standing Teaser
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0E1322))
                    .border(1.dp, Color(0xFF283548), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Your Standing: Rank #${userEarner?.rank ?: 9} (${userEarner?.coinsEarned ?: 100} 🪙 • ${userEarner?.diamondsEarned ?: 12} 💎)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                Text("+10 🪙 per share", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // View Full Leaderboard Button
            Button(
                onClick = onViewFullLeaderboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("open_affiliate_leaderboard_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View Coins & Diamonds Leaderboard 🏆", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

/**
 * Complete Gamified Affiliate Coins & Diamonds Leaderboard
 * Displays top earners strictly by Coins earned and Diamonds collected.
 * Zero cash / rupee amounts.
 */
@Composable
fun AffiliateLeaderboardView(
    userProfile: UserProfile?,
    affiliateTag: String = MonetizationConstants.AFFILIATE_TAG,
    onShareProduct: (AffiliateProduct) -> Unit,
    onSwitchToDeals: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    var selectedTimeframe by remember { mutableIntStateOf(0) } // 0: Weekly Sprint, 1: Monthly, 2: All-Time
    var selectedEarnerForStrategy by remember { mutableStateOf<AffiliateTopEarner?>(null) }

    val topEarners = remember(userProfile, selectedTimeframe) {
        SampleAffiliateLeaderboardData.getTopEarners(userProfile, selectedTimeframe)
    }

    val userEarner = topEarners.find { it.isUser }
    val top1 = topEarners.getOrNull(0)
    val top2 = topEarners.getOrNull(1)
    val top3 = topEarners.getOrNull(2)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("affiliate_leaderboard_container"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Banner & Title
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Top Creators Leaderboard - Coins & Diamonds",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Ranked strictly by Total Coins Earned & Total Diamonds Collected",
                            fontSize = 11.5.sp,
                            color = Color.LightGray
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
            }
        }

        // 2. Gamified Weekly Reward Pool Card (Coins & Diamonds only)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1238)),
                border = androidx.compose.foundation.BorderStroke(
                    1.4.dp,
                    Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFF9333EA)))
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Weekly Creator Sprint Rewards 🎁", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text("Resets Sunday ⏳", fontSize = 10.sp, color = Color(0xFFFFE082), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PrizeChip(place = "1st", reward = "👑 +100 💎 + 1,000 🪙", color = Color(0xFFFFD54F), modifier = Modifier.weight(1f))
                        PrizeChip(place = "2nd", reward = "🥈 +50 💎 + 500 🪙", color = Color(0xFFE2E8F0), modifier = Modifier.weight(1f))
                        PrizeChip(place = "3rd", reward = "🥉 +25 💎 + 250 🪙", color = Color(0xFFF97316), modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "⭐ Every link shared earns +10 Coins 🪙 and orders credit +3 Diamonds 💎. Top 10 creators earn Verified Booster badges & weekly Diamond drops!",
                        fontSize = 10.5.sp,
                        color = Color(0xFFD8B4FE),
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // 3. Timeframe Filter Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTimeframe,
                containerColor = Color(0xFF131722),
                contentColor = NeonCyan,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTimeframe == 0,
                    onClick = { selectedTimeframe = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚡ Weekly Sprint", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTimeframe == 1,
                    onClick = { selectedTimeframe = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🗓️ Monthly Leaders", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTimeframe == 2,
                    onClick = { selectedTimeframe = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌟 All-Time Legends", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }

        // 4. Top 3 Podium (Gamification Showcase with Coins & Diamonds)
        item {
            if (top1 != null && top2 != null && top3 != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("affiliate_leaderboard_podium"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131126)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E244D))
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF22163E), Color(0xFF100C22))
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Top 3 Champions (Coins & Diamonds)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFFFD54F),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // 2nd Place (Silver - Left)
                            PodiumColumn(
                                earner = top2,
                                medal = "🥈",
                                borderColor = Color(0xFFC0C0C0),
                                pedestalHeight = 85.dp,
                                onClick = { selectedEarnerForStrategy = top2 }
                            )

                            // 1st Place (Gold - Center, Taller)
                            PodiumColumn(
                                earner = top1,
                                medal = "🥇",
                                borderColor = Color(0xFFFFD54F),
                                pedestalHeight = 115.dp,
                                isCenter = true,
                                onClick = { selectedEarnerForStrategy = top1 }
                            )

                            // 3rd Place (Bronze - Right)
                            PodiumColumn(
                                earner = top3,
                                medal = "🥉",
                                borderColor = Color(0xFFCD7F32),
                                pedestalHeight = 70.dp,
                                onClick = { selectedEarnerForStrategy = top3 }
                            )
                        }
                    }
                }
            }
        }

        // 5. User Standing & Progress to Next Milestone Card
        item {
            if (userEarner != null) {
                val currentRank = userEarner.rank
                val nextRankTarget = topEarners.getOrNull((currentRank - 2).coerceAtLeast(0))
                val linksNeeded = if (nextRankTarget != null && nextRankTarget != userEarner) {
                    (nextRankTarget.sharedLinks - userEarner.sharedLinks + 1).coerceAtLeast(1)
                } else {
                    0
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("affiliate_user_standing_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131F33)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, DiamondCyan)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
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
                                        .background(DiamondCyan.copy(alpha = 0.2f))
                                        .border(1.2.dp, DiamondCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Your Standing: Rank #$currentRank", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Text("🔗 ${userEarner.sharedLinks} links shared • ${userEarner.clicks} clicks", fontSize = 11.sp, color = Color(0xFF81D4FA))
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("${userEarner.coinsEarned} 🪙", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldAccent)
                                Text("${userEarner.diamondsEarned} 💎", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DiamondCyan)
                            }
                        }

                        if (linksNeeded > 0 && nextRankTarget != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val progress = (userEarner.sharedLinks.toFloat() / nextRankTarget.sharedLinks.toFloat()).coerceIn(0.1f, 0.95f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NeonCyan,
                                trackColor = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "⚡ Share $linksNeeded more links to overtake #${currentRank - 1} (${nextRankTarget.name})!",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFFD54F)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onSwitchToDeals,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Next Deal (+10 Coins per link) 🚀", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 6. Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Creators Leaderboard - Coins & Diamonds (${topEarners.size})",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Ranked by Coins & Diamonds",
                    fontSize = 11.sp,
                    color = Color(0xFFFFD54F)
                )
            }
        }

        // 7. Full Earner List (strictly Coins & Diamonds)
        items(topEarners, key = { "${it.rank}_${it.handle}" }) { earner ->
            AffiliateEarnerRowItem(
                earner = earner,
                onViewStrategy = { selectedEarnerForStrategy = earner }
            )
        }
    }

    // Modal: Earner Winning Strategy & Top Shared Deal
    selectedEarnerForStrategy?.let { earner ->
        Dialog(onDismissRequest = { selectedEarnerForStrategy = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1635)),
                border = androidx.compose.foundation.BorderStroke(1.4.dp, if (earner.rank == 1) Color(0xFFFFD54F) else BrandPurple)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (earner.rank) {
                                    1 -> "🥇"
                                    2 -> "🥈"
                                    3 -> "🥉"
                                    else -> "#${earner.rank}"
                                },
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(earner.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                Text(earner.handle, fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        IconButton(onClick = { selectedEarnerForStrategy = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Matrix Card (Coins & Diamonds only, zero cash)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF120C24))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Links Shared", fontSize = 10.sp, color = Color.Gray)
                                Text("${earner.sharedLinks}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF81D4FA))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Clicks", fontSize = 10.sp, color = Color.Gray)
                                Text("${earner.clicks}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFD54F))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Coins Earned", fontSize = 10.sp, color = Color.Gray)
                                Text("${earner.coinsEarned} 🪙", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldAccent)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Diamonds", fontSize = 10.sp, color = Color.Gray)
                                Text("${earner.diamondsEarned} 💎", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DiamondCyan)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("⭐ Best-Performing Affiliate Deal:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF281C45))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(earner.bestDealTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Channel: ${earner.primaryPlatform}", fontSize = 10.sp, color = Color(0xFF81D4FA))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("💡 Winning Growth Strategy:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = earner.winningStrategy,
                        fontSize = 11.5.sp,
                        color = Color.LightGray,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action: Share the deal immediately
                    val sampleDeal = SampleAffiliateProducts.catalog.firstOrNull()
                    Button(
                        onClick = {
                            selectedEarnerForStrategy = null
                            if (sampleDeal != null) {
                                val shareText = sampleDeal.getShareText(affiliateTag)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val chooser = Intent.createChooser(sendIntent, "Share this winning deal:")
                                context.startActivity(chooser)
                                onShareProduct(sampleDeal)

                                // Trigger Go Viral Push Notification
                                com.example.util.ReferralNotificationHelper.showPostReadyToGoViralNotification(context)
                                Toast.makeText(context, "Congratulations! Your post is ready to go viral!", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("share_earner_top_deal_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share This Winning Deal (+10 Coins) 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrizeChip(place: String, reward: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF261842))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(place, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, color = color)
            Text(reward, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color.White, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun PodiumColumn(
    earner: AffiliateTopEarner,
    medal: String,
    borderColor: Color,
    pedestalHeight: androidx.compose.ui.unit.Dp,
    isCenter: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(96.dp)
            .clickable { onClick() }
    ) {
        // Avatar circle with badge
        Box(
            modifier = Modifier
                .size(if (isCenter) 48.dp else 40.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(borderColor, Color(0xFF1E1B4B))))
                .border(1.5.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(medal, fontSize = if (isCenter) 22.sp else 18.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = earner.name.substringBefore(" "),
            fontWeight = FontWeight.Bold,
            fontSize = if (isCenter) 11.5.sp else 10.5.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${earner.coinsEarned} 🪙",
            fontWeight = FontWeight.Black,
            fontSize = if (isCenter) 12.sp else 10.5.sp,
            color = GoldAccent
        )
        Text(
            text = "${earner.diamondsEarned} 💎",
            fontWeight = FontWeight.Bold,
            fontSize = if (isCenter) 10.sp else 9.sp,
            color = DiamondCyan
        )
        Text(
            text = "${earner.sharedLinks} links",
            fontSize = 8.5.sp,
            color = Color(0xFF81D4FA)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pedestal base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pedestalHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(borderColor.copy(alpha = 0.35f), Color(0xFF1A122E))
                    )
                )
                .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(medal, fontSize = 20.sp)
                Text(
                    text = "Rank ${earner.rank}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AffiliateEarnerRowItem(
    earner: AffiliateTopEarner,
    onViewStrategy: () -> Unit
) {
    val isUser = earner.isUser
    val rankColor = when (earner.rank) {
        1 -> Color(0xFFFFD54F)
        2 -> Color(0xFFE2E8F0)
        3 -> Color(0xFFF97316)
        else -> NeonCyan
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewStrategy() }
            .testTag("affiliate_leaderboard_item_${earner.rank}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUser) Color(0xFF14223A) else Color(0xFF131726)
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isUser) 1.5.dp else 1.dp,
            if (isUser) DiamondCyan else Color(0xFF263248)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Rank badge
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when (earner.rank) {
                                    1 -> Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFF59E0B)))
                                    2 -> Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8)))
                                    3 -> Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFC2410C)))
                                    else -> Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (earner.rank) {
                                1 -> "🥇"
                                2 -> "🥈"
                                3 -> "🥉"
                                else -> "#${earner.rank}"
                            },
                            fontSize = if (earner.rank <= 3) 15.sp else 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = earner.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (earner.isVip) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.WorkspacePremium, contentDescription = "VIP", tint = VipGold, modifier = Modifier.size(13.dp))
                            }
                            if (isUser) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(DiamondCyan)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("YOU", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(earner.handle, fontSize = 10.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("• ${earner.badge}", fontSize = 9.sp, color = rankColor, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Coins & Diamonds Badges (zero rupees)
                Column(horizontalAlignment = Alignment.End) {
                    Text("${earner.coinsEarned} 🪙", fontWeight = FontWeight.Black, fontSize = 13.sp, color = GoldAccent)
                    Text("${earner.diamondsEarned} 💎", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DiamondCyan)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Activity Stats Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A0F1D))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${earner.sharedLinks} Links Shared", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF81D4FA))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${earner.clicks} Clicks", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFFE082))
                }

                Text(
                    text = earner.primaryPlatform,
                    fontSize = 9.5.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
