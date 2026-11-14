package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ReferralRecord
import com.example.data.local.UserProfile
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

@Composable
fun HomeReferralRewardBanner(
    userProfile: UserProfile?,
    referrals: List<ReferralRecord>,
    notificationMessage: String? = null,
    friendJoinedStatus: String = "Waiting for friend to download & explore...",
    onDismissNotification: () -> Unit = {},
    onShareReferral: (context: Context, code: String) -> Unit,
    onSimulateDownload: (context: Context, friendName: String, code: String) -> Unit = { _, _, _ -> },
    onSimulateExploration: (context: Context, friendName: String, code: String) -> Unit = { _, _, _ -> },
    onVerifyActiveReferral: (context: Context, id: Long, friendName: String) -> Unit,
    onSimulateActiveReferral: (context: Context, friendName: String, code: String) -> Unit,
    onRedeemDiamondsForVip: () -> Unit = {},
    onTriggerWelcomePopup: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile = userProfile ?: UserProfile()
    val referralCode = "VIRAL" + (profile.id.toString().padStart(4, '0'))
    val referralLink = "${MonetizationConstants.GLOBAL_ADMIN_BITLI_URL}?ref=${profile.displayUserId}"

    val isFullAppFreeSubActive = profile.isVip &&
            (profile.currentPlanTier == "FULL_APP_FREE_1_DAY" || profile.vipExpiryDate > System.currentTimeMillis())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_referral_reward_hub")
    ) {
        // Section Title & Wallet Status Header (Matching Layout)
        Text(
            text = "ViralBoost AI - Refer & Earn Hub",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Wallet -> Diamonds: ${profile.diamonds} | Coins: ${profile.coins}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = NeonCyan
        )
        Text(
            text = "VIP Pro Status: ${if (isFullAppFreeSubActive) "Active (24 Hours Free)" else "Standard"}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isFullAppFreeSubActive) VipGold else Color.LightGray
        )

        Spacer(modifier = Modifier.height(14.dp))

        // In-App Notification Banner Simulation
        notificationMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E103A)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("referral_notification_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "🔔 Notification: $msg",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                    }
                    IconButton(
                        onClick = onDismissNotification,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss Notification",
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Refer & Earn Box UI (Matching Specified App Layout)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("refer_and_earn_box"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF15102A)),
            border = androidx.compose.foundation.BorderStroke(
                1.4.dp,
                Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFF8A2BE2), Color(0xFF00E676)))
            )
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
                                .background(Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Refer Friends & Earn Free VIP Pro",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF065F46))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("FREE VIP 👑", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Benefit Bullets
                Text(
                    text = "• +10 Coins & +4 Diamonds on share",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "• +10 Diamonds instantly when friend downloads & opens",
                    style = MaterialTheme.typography.bodySmall,
                    color = DiamondCyan,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "• 1-Day Free Full-App VIP Access when friend explores for 2 mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6EE7B7),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Unique Referral Link Box
                OutlinedTextField(
                    value = "$referralCode ($referralLink)",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("referral_link_field"),
                    label = { Text("Your Unique Referral Link") },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Referral Link", "$referralCode ($referralLink)")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Referral link copied! Share to earn rewards.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_referral_code_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldAccent, modifier = Modifier.size(18.dp))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Button 1: Share Referral Link (+10 Coins & +4 Diamonds)
                Button(
                    onClick = {
                        val shareText = """
                            🚀 Boost your Views, Followers & Reach on Instagram & Facebook with ViralBoost AI!
                            
                            Join using my referral code: $referralCode
                            Get high-CTR trending titles, smart hashtags, 4K AI video tools, and earn real Affiliate Rewards!
                            
                            Download the app now:
                            $referralLink
                        """.trimIndent()

                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val chooser = Intent.createChooser(sendIntent, "Share Referral Link (+10 Coins & +4 Diamonds):")
                        context.startActivity(chooser)

                        onShareReferral(context, referralCode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("share_referral_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Referral Link (+10 Coins & +4 Diamonds)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Button 2: Simulate Friend Download & Open App
                Button(
                    onClick = {
                        onSimulateDownload(context, "Amit Verma", referralCode)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0284C7) // Material secondary cyan/blue
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("simulate_friend_download_button")
                ) {
                    Icon(Icons.Default.Diamond, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simulate Friend Download & Open App",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Button 3: Simulate Friend Active 2-Min Exploration
                Button(
                    onClick = {
                        onSimulateExploration(context, "Amit Verma", referralCode)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF059669) // Material tertiary emerald green
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("simulate_friend_2min_exploration_button")
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simulate Friend Active 2-Min Exploration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Referral Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Referral Status:",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = friendJoinedStatus,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dedicated Wallet & Reward Hub Card (Distinct UI Section)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("wallet_and_rewards_hub_card"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ VIP & Wallet Hub",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Wallet Balance: ₹${if (profile.bonusRupees % 1.0 == 0.0) profile.bonusRupees.toInt() else profile.bonusRupees} (Use for VIP/Shopping)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Coins: ${profile.coins} (Shopping Discounts)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoldAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                    val calculatedRupeeValue = (profile.diamonds * 2) / 10
                    Text(
                        text = "Diamonds: ${profile.diamonds} (₹$calculatedRupeeValue Value)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DiamondCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "• Rule: 10 Diamonds = ₹2 (Exact Value Calculation).",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Text(
                    text = "• Rule: 150 Diamonds (₹30 Value) unlocks 10-Day VIP Pro Plan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Text(
                    text = "• Shopping Hub Affiliate ID: 5665937 | Link: bitli.in/Ff4aE3C",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Welcome Bonus Claim / Status Button
                if (profile.bonusRupees == 0.0 && !profile.hasClaimedWelcomeBonus) {
                    Button(
                        onClick = onTriggerWelcomePopup,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("claim_welcome_bonus_hub_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "🎁 Claim ₹50 Welcome Bonus (Pop-up Dialog)",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                } else if (profile.hasClaimedWelcomeBonus) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF13221C)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "✓ ₹50 Welcome Bonus Active in Wallet Balance",
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            color = SuccessGreen,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Redeem Diamonds for 10-Day VIP Plan Button
                Button(
                    onClick = onRedeemDiamondsForVip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("redeem_150_diamonds_for_vip_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFullAppFreeSubActive) SuccessGreen else BrandPurple
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isFullAppFreeSubActive) "10-Day VIP Pro Plan Active ✓" else "Redeem 150 Diamonds for 10-Day VIP Plan (₹30 Value)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "All other features, Shopping Hub, and Owner UPI (Amit2653@ptyes) remain 100% active and smooth.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.LightGray
        )
        Text(
            text = "Shopping Hub & Multi-Platform Feeds are 100% Secure & Active.",
            style = MaterialTheme.typography.bodySmall,
            color = SuccessGreen,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Permanent Affiliate & UPI Lock Footnote
        Text(
            text = "Note: Retains Master Affiliate Link (${MonetizationConstants.GLOBAL_ADMIN_BITLI_URL}, EarnKaro ID: ${MonetizationConstants.OWNER_EARNKARO_ID}) & Owner UPI (${MonetizationConstants.OWNER_UPI_ID}) in shopping & payout hubs.",
            fontSize = 9.5.sp,
            color = Color.Gray,
            lineHeight = 13.sp
        )

        // Existing referred friends breakdown if any
        if (referrals.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Recent Referred Friends (${referrals.size}):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                referrals.take(3).forEach { record ->
                    ReferralRecordRow(
                        record = record,
                        onVerify = { onVerifyActiveReferral(context, record.id, record.friendName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReferralRecordRow(
    record: ReferralRecord,
    onVerify: () -> Unit
) {
    val isVerified = record.status == "VERIFIED_ACTIVE" || record.explorationSeconds >= 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isVerified) Color(0xFF063327) else Color(0xFF131B2E)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isVerified) SuccessGreen.copy(alpha = 0.6f) else Color(0xFF263248)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(record.friendAvatar, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(record.friendName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            text = if (isVerified) "Active 1-2 min verified ✅ (VIP Active)" else "Exploring app (${record.explorationSeconds}s / 120s) ⏳",
                            fontSize = 9.5.sp,
                            color = if (isVerified) Color(0xFF6EE7B7) else Color(0xFF81D4FA)
                        )
                    }
                }

                if (isVerified) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF047857))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text("1-Day Free Sub Unlocked 👑", fontSize = 8.5.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = onVerify,
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text("Verify Active ⚡", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (!isVerified) {
                Spacer(modifier = Modifier.height(6.dp))
                val progress = (record.explorationSeconds.toFloat() / 120f).coerceIn(0.15f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = NeonCyan,
                    trackColor = Color(0xFF1E293B)
                )
            }
        }
    }
}
