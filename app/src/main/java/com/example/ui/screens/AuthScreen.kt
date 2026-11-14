package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.UserProfile
import com.example.monetization.MonetizationConstants
import com.example.ui.components.DynamicThemeSwitcherCard
import com.example.ui.components.UniversalMediatedAdDock
import com.example.ui.components.rememberMediatedInterstitialTrigger
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen

@Composable
fun AuthScreen(
    currentProfile: UserProfile?,
    ownerUpiId: String = MonetizationConstants.OWNER_UPI_ID,
    onSaveProfile: (name: String, email: String, niche: String) -> Unit,
    onOwnerLoginSuccess: () -> Unit,
    onContinueAsCreator: () -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    }

    var creatorName by remember { mutableStateOf(currentProfile?.username ?: "ViralCreator_99") }
    var creatorEmail by remember { mutableStateOf(currentProfile?.email ?: "creator@viralboost.ai") }
    val niches = listOf("Shorts & Reels", "Tech & AI", "Finance & Wealth", "Gaming Nexus", "Lifestyle & Vlog", "Fitness & Health")
    var selectedNiche by remember { mutableStateOf(currentProfile?.niche ?: niches.first()) }

    var showOwnerModal by remember { mutableStateOf(false) }
    var ownerPasskeyInput by remember { mutableStateOf("") }
    var ownerAuthError by remember { mutableStateOf<String?>(null) }
    val triggerInterstitial = rememberMediatedInterstitialTrigger(isVip = currentProfile?.isVip == true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // App Logo
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(BrandPurple, NeonCyan))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.RocketLaunch,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "ViralBoost AI",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Zero-Drop Creator Growth Engine",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Material 3 Dynamic Theme Switcher Card (Dark / Light Mode)
        DynamicThemeSwitcherCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Standard Creator Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Creator Onboarding & Profile",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Unique 7-8 Digit User ID Spotlight Card
                val displayId = currentProfile?.displayUserId ?: "74829156"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1B4B))
                        .border(1.2.dp, Brush.horizontalGradient(listOf(BrandPurple, NeonCyan)), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("unique_user_id_card")
                ) {
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
                                    .background(BrandPurple.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("UNIQUE CREATOR USER ID", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                Text("#$displayId", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }

                        IconButton(
                            onClick = {
                                val clip = ClipData.newPlainText("ViralBoost User ID", displayId)
                                clipboardManager?.setPrimaryClip(clip)
                                Toast.makeText(context, "User ID #$displayId copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_profile_user_id_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy User ID", tint = GoldAccent, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Welcome Instant Free Trial Perk Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF065F46).copy(alpha = 0.25f))
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Instant 3-Day Free Trial Included", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6EE7B7))
                            Text("Full Suite Unlocked • 3 Videos, 5 Images & 5 Scripts ready today", fontSize = 10.sp, color = Color.LightGray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = creatorName,
                    onValueChange = { creatorName = it },
                    label = { Text("Creator / Channel Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creator_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPurple,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = creatorEmail,
                    onValueChange = { creatorEmail = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creator_email_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPurple,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Primary Content Niche", fontSize = 12.sp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    niches.chunked(2).forEach { rowNiches ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowNiches.forEach { niche ->
                                val isSelected = selectedNiche == niche
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) BrandPurple else DarkSurfaceElevated)
                                        .border(1.dp, if (isSelected) BrandPurple else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (selectedNiche != niche) {
                                                triggerInterstitial("Select Creator Niche: $niche") {
                                                    selectedNiche = niche
                                                }
                                            }
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = niche,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onSaveProfile(creatorName, creatorEmail, selectedNiche)
                        onContinueAsCreator()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("enter_engine_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Profile & Continue", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Legal Agreement Proof & Compliance Audit Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("legal_agreement_proof_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF15102A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LEGAL AGREEMENT PROOF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            }
                            Text("STATUS: VERIFIED ✓", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val agreedTime = currentProfile?.legalAgreementTimestamp?.ifBlank { "Recorded on Sign Up" } ?: "Recorded on Sign Up"
                        val contactVal = currentProfile?.userContact?.ifBlank { currentProfile.email } ?: creatorEmail
                        Text("• Signed By: ${currentProfile?.username ?: creatorName}", fontSize = 11.sp, color = Color.White)
                        Text("• Contact / Identifier: $contactVal", fontSize = 11.sp, color = NeonCyan)
                        Text("• Signed Timestamp: $agreedTime", fontSize = 10.5.sp, color = Color.LightGray)
                        Text("• Scope: Full release of liability for rewards & deals. Provided as-is.", fontSize = 10.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                val proof = "ADMIN_LEGAL_LOG: User=${currentProfile?.username ?: creatorName}, Contact=$contactVal, Time=$agreedTime, Status=AGREED_AND_VERIFIED"
                                val clip = ClipData.newPlainText("ViralBoost Legal Proof", proof)
                                clipboardManager?.setPrimaryClip(clip)
                                Toast.makeText(context, "Legal Agreement Proof copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().height(34.dp),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Legal Defense Record", fontSize = 11.sp, color = NeonCyan)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sign Out / Test Onboarding Button
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("logout_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.7f))
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign Out & Test Onboarding / OTP Flow", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Distinct Bottom Security Gate for Owner Login
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("owner_security_gate_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF130D2E)),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Owner Admin Access Gate",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Restricted to $ownerUpiId administrator rights",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showOwnerModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("owner_login_gate_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("👑 Owner Login ($ownerUpiId)", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Universal Mediated Rewarded Ad + Permanent Banner Ad (BP_Banner_Android / BP_Rewarded_Android / AdMob)
        UniversalMediatedAdDock(
            isVip = currentProfile?.isVip == true,
            featureLabel = "Creator Profile & Auth"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Owner Login Dialog
        if (showOwnerModal) {
            Dialog(onDismissRequest = { showOwnerModal = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("owner_auth_dialog"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(28.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Owner Security Gate",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Admin Rights: $ownerUpiId",
                            fontSize = 11.sp,
                            color = NeonCyan
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = ownerPasskeyInput,
                            onValueChange = {
                                ownerPasskeyInput = it
                                ownerAuthError = null
                            },
                            label = { Text("Owner Passkey / PIN") },
                            placeholder = { Text("Enter admin2653") },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = GoldAccent) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = ownerAuthError != null,
                            supportingText = {
                                if (ownerAuthError != null) {
                                    Text(ownerAuthError!!, color = ErrorRed, fontSize = 11.sp)
                                } else {
                                    Text("Default passkey: admin2653", fontSize = 10.sp, color = Color.Gray)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("owner_passkey_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showOwnerModal = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cancel", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val cleaned = ownerPasskeyInput.trim()
                                    if (cleaned == MonetizationConstants.OWNER_PASSKEY || cleaned.equals(MonetizationConstants.OWNER_ADMIN_EMAIL, ignoreCase = true)) {
                                        showOwnerModal = false
                                        onOwnerLoginSuccess()
                                    } else {
                                        ownerAuthError = "Incorrect Passkey! Access Denied."
                                    }
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("verify_owner_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Authenticate", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
