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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.example.monetization.AdManager
import com.example.monetization.MonetizationConstants
import com.example.util.ReferralNotificationHelper
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen

@Composable
fun UniversalEcommerceProductLinkCard(
    affiliateTag: String = MonetizationConstants.AFFILIATE_TAG,
    downloadUrl: String = MonetizationConstants.OFFICIAL_STORE_DOWNLOAD_URL,
    isVip: Boolean = false,
    interstitialUnitId: String = MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID,
    connectedChannelsCount: Int = 4,
    onBroadcastSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    }

    var inputProductUrl by remember { mutableStateOf("") }
    var customDealTitle by remember { mutableStateOf("") }

    var showMilestoneAdDialog by remember { mutableStateOf(false) }
    var pendingDismissAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    if (showMilestoneAdDialog) {
        InterstitialAdDialog(
            interstitialUnitId = interstitialUnitId,
            onDismiss = {
                showMilestoneAdDialog = false
                val action = pendingDismissAction
                pendingDismissAction = null
                action?.invoke()
            }
        )
    }

    // Real-time Platform Detection for Amazon, Flipkart, Meesho, Myntra, EarnKaro
    val detectedPlatform by remember(inputProductUrl) {
        derivedStateOf {
            val lower = inputProductUrl.lowercase().trim()
            when {
                lower.contains("amazon") || lower.contains("amzn") -> "Amazon"
                lower.contains("flipkart") || lower.contains("fkrt") -> "Flipkart"
                lower.contains("meesho") -> "Meesho"
                lower.contains("myntra") -> "Myntra"
                lower.contains("earnkaro") -> "EarnKaro"
                lower.contains("http://") || lower.contains("https://") -> "Online Store"
                lower.isNotBlank() -> "Custom Product"
                else -> null
            }
        }
    }

    val platformColor = remember(detectedPlatform) {
        when (detectedPlatform) {
            "Amazon" -> Color(0xFFFF9900)
            "Flipkart" -> Color(0xFF2874F0)
            "Meesho" -> Color(0xFFE91E63)
            "Myntra" -> Color(0xFFFF3F6C)
            "EarnKaro" -> Color(0xFF00C853)
            else -> NeonCyan
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("universal_ecommerce_product_link_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131028)),
        border = androidx.compose.foundation.BorderStroke(
            1.4.dp,
            Brush.horizontalGradient(listOf(Color(0xFFFF9900), Color(0xFF8A2BE2), Color(0xFF00E676)))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title & Earning Tag
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
                            .background(Brush.linearGradient(listOf(Color(0xFFFF9900), Color(0xFFE91E63)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Universal Product Link Broadcaster",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Earn +10 🪙 per share across connected channels",
                            fontSize = 10.5.sp,
                            color = Color(0xFFFFD54F)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("+10 🪙", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GoldAccent)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Supported Platform Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PlatformChip(name = "Amazon", emoji = "📦", color = Color(0xFFFF9900))
                PlatformChip(name = "Flipkart", emoji = "🛍️", color = Color(0xFF2874F0))
                PlatformChip(name = "Meesho", emoji = "👗", color = Color(0xFFE91E63))
                PlatformChip(name = "Myntra", emoji = "👠", color = Color(0xFFFF3F6C))
                PlatformChip(name = "EarnKaro", emoji = "💰", color = Color(0xFF00C853))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product Link Input Field with Paste & Clear Buttons
            OutlinedTextField(
                value = inputProductUrl,
                onValueChange = { newVal ->
                    val wasEmpty = inputProductUrl.isBlank()
                    inputProductUrl = newVal
                    val trimmed = newVal.trim()
                    if (wasEmpty && (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.contains("amazon") || trimmed.contains("flipkart") || trimmed.contains("meesho") || trimmed.contains("myntra") || trimmed.contains("earnkaro"))) {
                        // Affiliate Hub Milestone 1: Exact moment a user enters an affiliate link
                        AdManager.triggerMilestoneInterstitial(
                            context = context,
                            isVip = isVip,
                            milestoneName = "Affiliate Hub - Milestone 1: Link Entered",
                            interstitialUnitId = interstitialUnitId,
                            onShowInterstitialFallback = { onDismiss ->
                                pendingDismissAction = onDismiss
                                showMilestoneAdDialog = true
                            }
                        )
                    }
                },
                label = { Text("Product / Affiliate Link") },
                placeholder = {
                    Text(
                        text = "Paste any product link from Amazon, Flipkart, Meesho, Myntra, or EarnKaro to generate and share your promotional links instantly.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Link, contentDescription = null, tint = platformColor)
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (inputProductUrl.isNotBlank()) {
                            IconButton(onClick = { inputProductUrl = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                            }
                        }
                        IconButton(
                            onClick = {
                                val clip = clipboardManager?.primaryClip
                                if (clip != null && clip.itemCount > 0) {
                                    val text = clip.getItemAt(0).text?.toString() ?: ""
                                    if (text.isNotBlank()) {
                                        inputProductUrl = text.trim()
                                        Toast.makeText(context, "Link pasted from clipboard! 📋", Toast.LENGTH_SHORT).show()
                                        // Affiliate Hub Milestone 1: Exact moment user pastes affiliate/product link
                                        AdManager.triggerMilestoneInterstitial(
                                            context = context,
                                            isVip = isVip,
                                            milestoneName = "Affiliate Hub - Milestone 1: Link Pasted",
                                            interstitialUnitId = interstitialUnitId,
                                            onShowInterstitialFallback = { onDismiss ->
                                                pendingDismissAction = onDismiss
                                                showMilestoneAdDialog = true
                                            }
                                        )
                                    }
                                } else {
                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.testTag("paste_product_link_button")
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("universal_product_link_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = platformColor,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0B0F19)
                )
            )

            // Platform Detection Feedback Badge
            AnimatedVisibility(visible = detectedPlatform != null) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(platformColor.copy(alpha = 0.15f))
                            .border(1.dp, platformColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = platformColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Detected: $detectedPlatform Deal Link",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text("Affiliate Tag #$affiliateTag ready", fontSize = 10.sp, color = platformColor, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Optional Deal Title or Product Name
            OutlinedTextField(
                value = customDealTitle,
                onValueChange = { customDealTitle = it },
                label = { Text("Deal Title / Description (Optional)") },
                placeholder = { Text("e.g. Trendy Cotton Kurti Set or Noise Smartwatch Deal", fontSize = 11.sp, color = Color.Gray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("universal_deal_title_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPurple,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0B0F19)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dual Action Buttons: Broadcast Everywhere vs Copy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Broadcast Button
                Button(
                    onClick = {
                        val trimmedUrl = inputProductUrl.trim()
                        if (trimmedUrl.isBlank()) {
                            Toast.makeText(context, "Please paste an Amazon, Flipkart, Meesho, Myntra, or EarnKaro product link first!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val title = if (customDealTitle.isNotBlank()) customDealTitle.trim() else "Trending ${detectedPlatform ?: "Shopping"} Deal"
                        val fullPromotionalPost = buildString {
                            append("🔥 Special Deal Alert: $title\n\n")
                            append("🛒 Shop Deal Here:\n")
                            append("$trimmedUrl\n\n")
                            append("━━━━━━━━━━━━━━━━━━━━━━\n")
                            append("🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!\n")
                            append("😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:\n")
                            append("👉 ${MonetizationConstants.PERMANENT_OWNER_AFFILIATE_LINK}\n\n")
                            append("🚀 Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:\n")
                            append("👉 $downloadUrl")
                        }

                        val executeBroadcast = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, fullPromotionalPost)
                                type = "text/plain"
                            }
                            val chooser = Intent.createChooser(sendIntent, "Broadcast $detectedPlatform Deal across channels:")
                            context.startActivity(chooser)

                            onBroadcastSuccess()
                            Toast.makeText(context, "Deal Broadcasted to $connectedChannelsCount Social Channels! +10 Coins 🪙 credited!", Toast.LENGTH_SHORT).show()

                            // Success Notification ("Go Viral") immediately after successful share
                            ReferralNotificationHelper.showPostReadyToGoViralNotification(context)
                            Toast.makeText(context, "Congratulations! Your post is ready to go viral!", Toast.LENGTH_LONG).show()
                        }

                        // Affiliate Hub Milestone 2: Exact moment user taps to share/broadcast link
                        AdManager.triggerMilestoneInterstitial(
                            context = context,
                            isVip = isVip,
                            milestoneName = "Affiliate Hub - Milestone 2: Broadcast Link",
                            interstitialUnitId = interstitialUnitId,
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
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("broadcast_product_link_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A2BE2))
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Broadcast Deal 🚀",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Copy with Viral Footer Button
                OutlinedButton(
                    onClick = {
                        val trimmedUrl = inputProductUrl.trim()
                        if (trimmedUrl.isBlank()) {
                            Toast.makeText(context, "Paste a product link first to copy!", Toast.LENGTH_SHORT).show()
                            return@OutlinedButton
                        }

                        val title = if (customDealTitle.isNotBlank()) customDealTitle.trim() else "Trending ${detectedPlatform ?: "Shopping"} Deal"
                        val fullPromotionalPost = buildString {
                            append("🔥 Special Deal Alert: $title\n\n")
                            append("🛒 Shop Deal Here:\n")
                            append("$trimmedUrl\n\n")
                            append("━━━━━━━━━━━━━━━━━━━━━━\n")
                            append("🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!\n")
                            append("😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:\n")
                            append("👉 ${MonetizationConstants.PERMANENT_OWNER_AFFILIATE_LINK}\n\n")
                            append("🚀 Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:\n")
                            append("👉 $downloadUrl")
                        }

                        val clip = ClipData.newPlainText("Promotional Deal", fullPromotionalPost)
                        clipboardManager?.setPrimaryClip(clip)

                        // Affiliate Hub Milestone 2: Exact moment user copies/shares promotional link
                        AdManager.triggerMilestoneInterstitial(
                            context = context,
                            isVip = isVip,
                            milestoneName = "Affiliate Hub - Milestone 2: Copy Deal Link",
                            interstitialUnitId = interstitialUnitId,
                            onShowInterstitialFallback = { onDismiss ->
                                pendingDismissAction = onDismiss
                                showMilestoneAdDialog = true
                            }
                        )

                        ReferralNotificationHelper.showPostReadyToGoViralNotification(context)
                        Toast.makeText(context, "Congratulations! Your post is ready to go viral!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("copy_deal_with_footer_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6B7280)),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun PlatformChip(name: String, emoji: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.8.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 10.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
