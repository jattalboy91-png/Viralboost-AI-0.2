package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.monetization.MonetizationConstants
import com.example.ui.theme.BrandPurple
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
fun TrialPaywallDialog(
    reasonMessage: String,
    userDiamonds: Int,
    plans: List<PlanOption> = emptyList(),
    ownerUpiId: String = MonetizationConstants.OWNER_UPI_ID,
    onDismiss: () -> Unit,
    onSelectPlan: (PlanOption) -> Unit = {},
    onUpgradeViaUpi: () -> Unit = {},
    onRedeemViaDiamonds: () -> Unit
) {
    // Default 4 exact subscription plans if none provided
    val defaultPlans = if (plans.isNotEmpty()) plans else listOf(
        PlanOption(
            id = "pass_10_days",
            name = "10-Day Starter Pass",
            priceRupees = MonetizationConstants.PRICE_PASS_10_DAYS,
            durationText = "10 Days Access",
            features = listOf(
                "Max 2 AI Videos daily",
                "Up to 5 Photo Generations daily",
                "100% Ad-Free (No Banners & Interstitials)"
            ),
            badge = "STARTER"
        ),
        PlanOption(
            id = "pass_15_days",
            name = "15-Day Pro Pass",
            priceRupees = MonetizationConstants.PRICE_PASS_15_DAYS,
            durationText = "15 Days Access",
            features = listOf(
                "Max 2 AI Videos daily",
                "Up to 5 Photo Generations daily",
                "100% Ad-Free VIP Experience"
            ),
            badge = "GROWTH"
        ),
        PlanOption(
            id = "vip_monthly",
            name = "1-Month Mega VIP Pass",
            priceRupees = MonetizationConstants.PRICE_VIP_MONTHLY,
            durationText = "30 Days Access",
            features = listOf(
                "Up to 3 AI Videos per day",
                "Up to 15 Photo Generations daily",
                "Enhanced Photo, Thumbnail & Script Access",
                "100% Ad-Free & Zero Watermarks"
            ),
            badge = "MOST POPULAR"
        ),
        PlanOption(
            id = "pro_3_months",
            name = "3-Month Ultimate Pro Plan",
            priceRupees = MonetizationConstants.PRICE_VIP_3_MONTHS_PRO,
            durationText = "90 Days Access",
            features = listOf(
                "FULL UNLIMITED AI Video Generations",
                "FULL UNLIMITED Photo & Thumbnail Generations",
                "100% Ad-Free Experience (Zero Ads)",
                "Interactive Gemini AI Chatbot Included"
            ),
            badge = "BEST VALUE"
        )
    )

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val safeDismiss = {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        onDismiss()
    }

    var selectedPlanId by remember { mutableStateOf(defaultPlans.getOrNull(2)?.id ?: defaultPlans.first().id) }
    val currentSelectedPlan = defaultPlans.firstOrNull { it.id == selectedPlanId } ?: defaultPlans.first()

    Dialog(
        onDismissRequest = safeDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 12.dp)
                .testTag("trial_paywall_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, VipGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF7F1D1D).copy(alpha = 0.4f))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LIMIT EXPIRED • VIP PAYWALL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCA5A5)
                            )
                        }
                    }

                    IconButton(
                        onClick = safeDismiss,
                        modifier = Modifier.size(28.dp).testTag("trial_paywall_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Glowing VIP Badge
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(VipGold.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                        .border(2.dp, VipGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = VipGold,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Upgrade to ViralBoost VIP Pro",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = reasonMessage.ifBlank {
                        "Your daily quota has ended. Choose a VIP tier below to unlock high-speed AI generations & 100% ad-free experience!"
                    },
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4 Tiered Subscription Plans
                Text(
                    text = "Select Subscription Tier",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    defaultPlans.forEach { plan ->
                        val isSelected = plan.id == selectedPlanId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPlanId = plan.id }
                                .testTag("paywall_plan_${plan.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E1538) else DarkSurfaceElevated
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.8.dp else 1.dp,
                                if (isSelected) VipGold else DarkSurfaceBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) VipGold else Color(0xFF334155))
                                            .border(1.dp, if (isSelected) VipGold else Color.Gray, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = plan.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            if (plan.badge.isNotBlank()) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(if (plan.badge == "BEST VALUE") VipGold else BrandPurple)
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = plan.badge,
                                                        fontSize = 8.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (plan.badge == "BEST VALUE") Color.Black else Color.White
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = plan.features.firstOrNull() ?: plan.durationText,
                                            fontSize = 10.5.sp,
                                            color = if (isSelected) GoldAccent else Color.Gray
                                        )
                                    }
                                }

                                Text(
                                    text = "₹${plan.priceRupees}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) VipGold else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Upgrade Button
                Button(
                    onClick = {
                        onSelectPlan(currentSelectedPlan)
                        onUpgradeViaUpi()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("trial_paywall_upgrade_upi_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VipGold)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upgrade ${currentSelectedPlan.name} (₹${currentSelectedPlan.priceRupees})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Instant UPI Intent / QR to: $ownerUpiId",
                    fontSize = 10.sp,
                    color = NeonCyan
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Action: Redeem with Diamonds
                OutlinedButton(
                    onClick = onRedeemViaDiamonds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("trial_paywall_redeem_diamonds_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DiamondCyan)
                ) {
                    Icon(Icons.Default.Diamond, contentDescription = null, tint = DiamondCyan, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Redeem 500 Diamonds (Balance: $userDiamonds 💎)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiamondCyan
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                UniversalMediatedAdDock(
                    isVip = false,
                    featureLabel = "VIP Upgrade Paywall"
                )
            }
        }
    }
}
