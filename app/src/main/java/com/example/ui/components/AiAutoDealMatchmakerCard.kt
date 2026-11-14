package com.example.ui.components

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Store
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
import com.example.monetization.MonetizationConstants
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan

/**
 * AI Auto-Deal Matchmaker & Personal Affiliate Link Settings Card
 *
 * Implements Smart Fallback Affiliate Link Handling:
 * - If user has not entered/saved a personal affiliate link, automatically falls back to Owner Master Link
 *   (https://bitli.in/Ff4aE3C tied to EarnKaro ID: 5665937) with high-conversion captions and viral app footer.
 * - If user enters/saves their personal link, the system uses their link for their earnings while
 *   maintaining the background owner structure.
 */
@Composable
fun AiAutoDealMatchmakerCard(
    userPersonalLink: String,
    onSavePersonalLink: (String) -> Unit,
    onClearPersonalLink: () -> Unit,
    modifier: Modifier = Modifier,
    onTriggerMilestone1: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    }

    var inputLink by remember(userPersonalLink) { mutableStateOf(userPersonalLink) }
    val hasPersonalLink = MonetizationConstants.hasPersonalAffiliateLink(userPersonalLink)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_auto_deal_matchmaker_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(
                    if (hasPersonalLink) Color(0xFF00E676) else Color(0xFFFF9900),
                    Color(0xFF8A2BE2),
                    NeonCyan
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: AI Matchmaker Title & Live Status Badge
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF8A2BE2), Color(0xFFFF9900)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI Auto-Deal Matchmaker",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Smart Fallback Affiliate Link Engine",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = GoldAccent
                        )
                    }
                }

                // Active Routing Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (hasPersonalLink) Color(0xFF064E3B) else Color(0xFF311042))
                        .border(
                            1.dp,
                            if (hasPersonalLink) Color(0xFF10B981) else Color(0xFFFF9900),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (hasPersonalLink) "Personal Link Active ✅" else "Owner Fallback Active ⚡",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasPersonalLink) Color(0xFF34D399) else Color(0xFFFFD54F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Explanation / Fallback Mechanism Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = if (hasPersonalLink) Color(0xFF34D399) else GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hasPersonalLink) {
                                "Direct Creator Earning Mode Active"
                            } else {
                                "Automated Owner Master Fallback Active"
                            },
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (hasPersonalLink) {
                            "Your personal affiliate link is attached to all AI video posts and deal broadcasts so viewers buy through your link. The owner master store & viral download footer are preserved in the background for continuous growth."
                        } else {
                            "You haven't added a personal affiliate link yet. The system automatically attaches the high-converting Owner Master Deal Link (https://bitli.in/Ff4aE3C • EarnKaro: 5665937) with high-conversion urgency captions so your posts convert viewers immediately!"
                        },
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Input Field: Personal Affiliate Link
            OutlinedTextField(
                value = inputLink,
                onValueChange = { newVal ->
                    val wasEmpty = inputLink.isBlank()
                    inputLink = newVal
                    val trimmed = newVal.trim()
                    if (wasEmpty && (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.contains("earnkaro") || trimmed.contains("amazon") || trimmed.contains("flipkart"))) {
                        onTriggerMilestone1?.invoke()
                    }
                },
                label = { Text("Your Personal Affiliate / Store Link") },
                placeholder = {
                    Text(
                        text = "Paste your EarnKaro, Amazon, Flipkart, Meesho, or Myntra affiliate link here...",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Link, contentDescription = null, tint = if (hasPersonalLink) Color(0xFF00E676) else GoldAccent)
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (inputLink.isNotBlank()) {
                            IconButton(onClick = { inputLink = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                            }
                        }
                        IconButton(
                            onClick = {
                                val clip = clipboardManager?.primaryClip
                                if (clip != null && clip.itemCount > 0) {
                                    val text = clip.getItemAt(0).text?.toString() ?: ""
                                    if (text.isNotBlank()) {
                                        inputLink = text.trim()
                                        Toast.makeText(context, "Link pasted! 📋 Tap 'Save Personal Link' to activate.", Toast.LENGTH_SHORT).show()
                                        onTriggerMilestone1?.invoke()
                                    }
                                } else {
                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.testTag("paste_personal_affiliate_link_button")
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = NeonCyan, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("personal_affiliate_link_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (hasPersonalLink) Color(0xFF00E676) else NeonCyan,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF0A0F1D),
                    unfocusedContainerColor = Color(0xFF080C17)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Save Personal Link vs Reset to Owner Fallback
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val clean = inputLink.trim()
                        if (clean.isBlank() || (!clean.startsWith("http://") && !clean.startsWith("https://"))) {
                            Toast.makeText(context, "Please enter a valid URL starting with http:// or https://", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onSavePersonalLink(clean)
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp)
                        .testTag("save_personal_affiliate_link_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save Personal Link 💾",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (hasPersonalLink) {
                    OutlinedButton(
                        onClick = {
                            inputLink = ""
                            onClearPersonalLink()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("clear_personal_affiliate_link_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB300)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "Reset to Fallback 🔄",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Quick Preset / Master Link Preview
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0B1120))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Owner Master Deal: https://bitli.in/Ff4aE3C",
                        fontSize = 10.sp,
                        color = Color(0xFF90A4AE)
                    )
                    Text(
                        text = "EarnKaro: 5665937",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
            }
        }
    }
}
