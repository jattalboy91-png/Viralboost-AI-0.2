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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfile
import com.example.monetization.MonetizationConstants
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
import com.example.ui.viewmodel.PlanOption

@Composable
fun VipPaywallScreen(
    userProfile: UserProfile?,
    plans: List<PlanOption>,
    onSelectPlan: (PlanOption) -> Unit,
    onOpenRedeemDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = userProfile ?: UserProfile()
    val triggerInterstitial = rememberMediatedInterstitialTrigger(isVip = profile.isVip)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner
            item {
                HeroPaywallHeader(isVip = profile.isVip)
            }

            // Diamond Redemption Callout Card
            item {
                DiamondRedemptionCallout(
                    diamonds = profile.diamonds,
                    onOpenRedeem = {
                        triggerInterstitial("Open VIP Diamond Redemption") {
                            onOpenRedeemDialog()
                        }
                    }
                )
            }

            // Referral 1-Day Free Subscription Callout Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("paywall_referral_free_sub_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1538)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFF00E676))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎁", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Want 100% Free Full-App VIP?", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                    Text("Refer a friend & get 1-Day Full-App Free Subscription!", fontSize = 10.5.sp, color = Color(0xFFFFD54F))
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF065F46))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("FREE PASS", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Earn +10 Coins & +2 Diamonds instantly on share. When your friend explores the app for 1-2 minutes, you instantly unlock 24 hours of unrestricted full-app VIP Pro tools.",
                            fontSize = 10.5.sp,
                            color = Color.LightGray,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Free vs VIP Comparison Table
            item {
                FreeVsVipComparisonCard()
            }

            // Subscription Plan Cards
            item {
                Text(
                    text = "Choose Your VIP Growth Tier",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(plans) { plan ->
                PlanCard(
                    plan = plan,
                    onSelect = {
                        triggerInterstitial("Select VIP Tier: ${plan.name}") {
                            onSelectPlan(plan)
                        }
                    }
                )
            }

            // Universal Mediated Rewarded Ad + Permanent Banner Ad (BP_Banner_Android / BP_Rewarded_Android / AdMob)
            item {
                UniversalMediatedAdDock(
                    isVip = profile.isVip,
                    featureLabel = "VIP Creator Pro Paywall"
                )
            }
        }
    }
}

@Composable
private fun HeroPaywallHeader(isVip: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3B0764), Color(0xFF1E1B4B))
                )
            )
            .border(1.5.dp, BrandPurple, RoundedCornerShape(20.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(VipGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = VipGold,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isVip) "You are a VIP Creator!" else "ViralBoost VIP Creator Pro",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Text(
                text = "100% Ad-Free • Unlimited AI Generations • Instant Priority",
                fontSize = 12.sp,
                color = NeonCyan,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DiamondRedemptionCallout(
    diamonds: Int,
    onOpenRedeem: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("diamond_redemption_callout"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, DiamondCyan)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Diamond, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Have Diamonds? ($diamonds 💎)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "500 💎 unlocks 1 Month VIP 100% Free",
                        fontSize = 11.sp,
                        color = GoldAccent
                    )
                }
            }

            Button(
                onClick = onOpenRedeem,
                colors = ButtonDefaults.buttonColors(containerColor = DiamondCyan),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Redeem", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FreeVsVipComparisonCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tier Comparison (Free vs VIP)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            ComparisonRow("Daily AI Videos", "Max 2/day", "3/day (1-Mo) • Unlimited (3-Mo)")
            ComparisonRow("Daily AI Photos", "Max 2/day", "15/day (1-Mo) • Unlimited (3-Mo)")
            ComparisonRow("Ad Experience", "Banners & Interstitials", "100% Ad-Free 🚫")
            ComparisonRow("Output Watermark", "Yes [Watermarked]", "100% Clean")
            ComparisonRow("Gemini Creator AI", "Limited", "Full 24/7 Access 🤖")
            ComparisonRow("Affiliate Engine (Tag ${MonetizationConstants.AFFILIATE_TAG})", "Locked 🔒", "Full Access ✅")
            ComparisonRow("VIP Creator Profile Badge", "None", "Gleaming Gold 👑")
        }
    }
}

@Composable
private fun ComparisonRow(feature: String, free: String, vip: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = feature, fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.weight(1.3f))
        Text(text = free, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        Text(text = vip, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VipGold, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
    }
}

@Composable
private fun PlanCard(
    plan: PlanOption,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plan_card_${plan.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (plan.badge.isNotBlank()) BrandPurple else DarkSurfaceBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = plan.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = plan.durationText,
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }

                if (plan.badge.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BrandPurple)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = plan.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "₹${plan.priceRupees}",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            plan.features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = feature,
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("select_plan_button_${plan.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPurple)
            ) {
                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Upgrade for ₹${plan.priceRupees}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}
