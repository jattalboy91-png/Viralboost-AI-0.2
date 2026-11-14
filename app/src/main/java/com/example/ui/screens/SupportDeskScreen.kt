package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SupportTicket
import com.example.data.local.UserProfile
import com.example.monetization.MonetizationConstants
import com.example.ui.components.BannerAdView
import com.example.ui.components.NativeAdCard
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SupportDeskScreen(
    userProfile: UserProfile?,
    tickets: List<SupportTicket>,
    bannerAdUnitId: String = MonetizationConstants.ADMOB_BANNER_UNIT_ID,
    nativeAdUnitId: String = MonetizationConstants.ADMOB_NATIVE_UNIT_ID,
    onSubmitTicket: (subject: String, category: String, message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = userProfile ?: UserProfile()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val triggerInterstitial = rememberMediatedInterstitialTrigger(isVip = profile.isVip)
    var subjectInput by remember { mutableStateOf("") }
    var messageInput by remember { mutableStateOf("") }
    val categories = listOf("Payment & 12-Digit UTR", "VIP Pro Activation", "AI Tool Feedback", "Diamond Utility", "General Inquiry")
    var selectedCategory by remember { mutableStateOf(categories.first()) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Help Desk & Creator Support",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Direct assistance from the Owner & Administration team",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Ticket Creation Form
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("support_ticket_form_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Create Support Ticket",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Category",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { cat ->
                                val isSelected = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) BrandPurple else DarkSurfaceElevated)
                                        .border(1.dp, if (isSelected) BrandPurple else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (selectedCategory != cat) {
                                                triggerInterstitial("Support Category: $cat") {
                                                    selectedCategory = cat
                                                }
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White else Color.LightGray,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            label = { Text("Issue Subject") },
                            placeholder = { Text("e.g. UTR payment pending verification") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ticket_subject_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPurple,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            label = { Text("Describe your issue or feedback") },
                            placeholder = { Text("Please provide transaction details or describe the issue...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("ticket_message_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPurple,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                val sub = subjectInput
                                val cat = selectedCategory
                                val msg = messageInput
                                triggerInterstitial("Submit Support Ticket") {
                                    onSubmitTicket(sub, cat, msg)
                                    subjectInput = ""
                                    messageInput = ""
                                }
                            },
                            enabled = subjectInput.isNotBlank() && messageInput.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_ticket_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit Support Message", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Organic AdMob Native Ad Card in content stream
            item {
                NativeAdCard(
                    isVip = profile.isVip,
                    adUnitId = nativeAdUnitId
                )
            }

            // Ticket History Header
            item {
                Text(
                    text = "Your Support History (${tickets.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (tickets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tickets submitted yet.\nIf you encounter any issues with payments or tools, reach out above!",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(tickets) { ticket ->
                    SupportTicketItemCard(ticket = ticket)
                }
            }

            // Universal Mediated Rewarded Ad + Permanent Banner Ad (BP_Banner_Android / BP_Rewarded_Android / AdMob)
            item {
                UniversalMediatedAdDock(
                    isVip = profile.isVip,
                    admobBannerUnitId = bannerAdUnitId,
                    featureLabel = "Help Desk & Support"
                )
            }
        }
    }
}

@Composable
private fun SupportTicketItemCard(ticket: SupportTicket) {
    val isResolved = ticket.status == "RESOLVED"
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date(ticket.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ticket_card_${ticket.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isResolved) SuccessGreen.copy(alpha = 0.5f) else DarkSurfaceBorder
        )
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
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isResolved) SuccessGreen.copy(alpha = 0.2f) else GoldAccent.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = ticket.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isResolved) SuccessGreen else GoldAccent
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = ticket.category, fontSize = 11.sp, color = NeonCyan)
                }

                Text(text = dateStr, fontSize = 10.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ticket.subject,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = ticket.message,
                fontSize = 12.sp,
                color = Color.LightGray
            )

            // Owner Admin Reply Box
            if (ticket.adminReply.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Admin Response (Owner Amit2653@ptyes)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ticket.adminReply,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HourglassTop, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Awaiting Owner/Admin Review",
                        fontSize = 10.sp,
                        color = GoldAccent
                    )
                }
            }
        }
    }
}
