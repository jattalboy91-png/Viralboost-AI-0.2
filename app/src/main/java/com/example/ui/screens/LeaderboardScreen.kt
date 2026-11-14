package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LeaderboardCreator
import com.example.data.local.UserProfile
import com.example.monetization.MonetizationConstants
import com.example.ui.components.BannerAdView
import com.example.ui.components.NativeAdCard
import com.example.ui.components.NativeVideoAdCard
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
import com.example.ui.theme.VipGold

@Composable
fun LeaderboardScreen(
    userProfile: UserProfile?,
    creators: List<LeaderboardCreator>,
    bannerAdUnitId: String = MonetizationConstants.ADMOB_BANNER_UNIT_ID,
    nativeAdUnitId: String = MonetizationConstants.ADMOB_NATIVE_UNIT_ID,
    nativeVideoAdUnitId: String = MonetizationConstants.ADMOB_NATIVE_VIDEO_UNIT_ID,
    modifier: Modifier = Modifier
) {
    val profile = userProfile ?: UserProfile()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Coins & Diamonds, 1: Total Coins, 2: Diamonds
    val triggerInterstitial = rememberMediatedInterstitialTrigger(isVip = profile.isVip)

    val sortedCreators = remember(creators, selectedTab) {
        when (selectedTab) {
            0 -> creators.sortedByDescending { it.coins + (it.diamonds * 10) }
            1 -> creators.sortedByDescending { it.coins }
            else -> creators.sortedByDescending { it.diamonds }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Top Creators Leaderboard - Coins & Diamonds
            item {
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
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ranked strictly by Total Coins Earned & Total Diamonds Collected",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Reward Rules Banner (Zero cash, Coins & Diamonds only)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1635)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🪙 +10 Coins/Share", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("💎 +3 Diamonds/Sale", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DiamondCyan)
                        }
                        Text("Weekly Drops 🎁", fontSize = 10.sp, color = Color(0xFFFFE082), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Metric Tabs: Coins & Diamonds
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurface,
                    contentColor = NeonCyan,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            if (selectedTab != 0) {
                                triggerInterstitial("Leaderboard Tab: Overall") {
                                    selectedTab = 0
                                }
                            }
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙💎 Overall", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            if (selectedTab != 1) {
                                triggerInterstitial("Leaderboard Tab: Coins") {
                                    selectedTab = 1
                                }
                            }
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙 Coins", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = {
                            if (selectedTab != 2) {
                                triggerInterstitial("Leaderboard Tab: Diamonds") {
                                    selectedTab = 2
                                }
                            }
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Diamond, contentDescription = null, modifier = Modifier.size(13.dp), tint = DiamondCyan)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Diamonds", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }

            // User's Own Standing Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_leaderboard_standing_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, BrandPurple)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BrandPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("#9", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${profile.username} (You)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (profile.isVip) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = VipGold, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Text(text = "${profile.niche} • Standing", fontSize = 10.sp, color = NeonCyan)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${profile.coins} 🪙 Coins",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldAccent
                            )
                            Text(
                                text = "${profile.diamonds} 💎 Diamonds",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiamondCyan
                            )
                        }
                    }
                }
            }

            // Leaderboard Items with organic native ad integration
            itemsIndexed(sortedCreators) { index, creator ->
                LeaderboardItemRow(rank = index + 1, creator = creator, selectedTab = selectedTab)

                // Organic AdMob Native Ad Card placed after top 3 creators
                if (index == 2 && !profile.isVip) {
                    Spacer(modifier = Modifier.height(4.dp))
                    NativeAdCard(
                        isVip = profile.isVip,
                        adUnitId = nativeAdUnitId
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Organic AdMob Native Video Ad Card placed after rank 6
                if (index == 5 && !profile.isVip) {
                    Spacer(modifier = Modifier.height(4.dp))
                    NativeVideoAdCard(
                        isVip = profile.isVip,
                        adUnitId = nativeVideoAdUnitId
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Universal Mediated Rewarded Ad + Permanent Banner Ad (BP_Banner_Android / BP_Rewarded_Android / AdMob)
            item {
                UniversalMediatedAdDock(
                    isVip = profile.isVip,
                    admobBannerUnitId = bannerAdUnitId,
                    featureLabel = "Creator Leaderboard"
                )
            }
        }
    }
}

@Composable
private fun LeaderboardItemRow(
    rank: Int,
    creator: LeaderboardCreator,
    selectedTab: Int
) {
    val rankBadgeColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> DarkSurfaceElevated
    }

    val rankTextColor = when (rank) {
        1, 2, 3 -> Color.Black
        else -> Color.LightGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_row_$rank"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (rank <= 3) rankBadgeColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rank Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(rankBadgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$rank",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = rankTextColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = creator.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (creator.isVip) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.WorkspacePremium, contentDescription = "VIP Creator", tint = VipGold, modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        text = "${creator.handle} • ${creator.niche}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Strictly Coins & Diamonds (Zero cash/rupees)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${creator.coins} 🪙",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldAccent
                )
                Text(
                    text = "${creator.diamonds} 💎",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiamondCyan
                )
            }
        }
    }
}
