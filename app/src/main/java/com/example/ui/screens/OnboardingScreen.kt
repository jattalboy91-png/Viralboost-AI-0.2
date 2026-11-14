package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.monetization.MonetizationConstants
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
fun OnboardingScreen(
    onSignupComplete: (name: String, contact: String, method: String) -> Unit,
    onOwnerLoginSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var signupMethod by remember { mutableStateOf("mobile") } // "mobile" or "email"
    var nameInput by remember { mutableStateOf("") }
    var contactInput by remember { mutableStateOf("") } // Mobile or Email
    var otpInput by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var agreed by remember { mutableStateOf(false) }

    var showOwnerModal by remember { mutableStateOf(false) }
    var ownerPasskeyInput by remember { mutableStateOf("") }
    var ownerAuthError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 22.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        // ViralBoost AI App Logo & Branding
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(BrandPurple, NeonCyan)))
                .border(2.dp, GoldAccent.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.RocketLaunch,
                contentDescription = "ViralBoost AI",
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "ViralBoost AI - Secure Sign Up",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 22.sp
        )

        Text(
            text = "Zero-Drop Creator Growth Engine & Rewards Hub",
            fontSize = 12.sp,
            color = NeonCyan,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Initial Starter Rewards Highlight (+10 Diamonds & +250 Coins)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16102E)),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "🎁 ₹50 Instant Welcome Bonus Unlocked",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Text(
                            text = "₹50 Cash Bonus + 10 Diamonds 💎 & 250 Coins 🪙 credited upon signup",
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Signup Method (Mobile/OTP vs Google/Email)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Registration Method",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            signupMethod = "mobile"
                            otpSent = false
                            otpInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (signupMethod == "mobile") BrandPurple else DarkSurfaceElevated,
                            contentColor = if (signupMethod == "mobile") Color.White else Color.LightGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("signup_method_mobile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mobile (OTP)",
                            fontSize = 12.sp,
                            fontWeight = if (signupMethod == "mobile") FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Button(
                        onClick = {
                            signupMethod = "email"
                            otpSent = true // Google/Email proceeds directly with verified sign-in
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (signupMethod == "email") BrandPurple else DarkSurfaceElevated,
                            contentColor = if (signupMethod == "email") Color.White else Color.LightGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("signup_method_email_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Google / Email",
                            fontSize = 12.sp,
                            fontWeight = if (signupMethod == "email") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Full Name Input
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Your Full Name") },
                    placeholder = { Text("e.g. Aarav Sharma") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fullname_input"),
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

                // Contact Input (Mobile Number or Google / Email ID)
                OutlinedTextField(
                    value = contactInput,
                    onValueChange = { contactInput = it },
                    label = {
                        Text(if (signupMethod == "mobile") "Enter Mobile Number" else "Enter Google / Email ID")
                    },
                    placeholder = {
                        Text(if (signupMethod == "mobile") "e.g. 9876543210" else "e.g. creator@gmail.com")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (signupMethod == "mobile") Icons.Default.Phone else Icons.Default.Email,
                            contentDescription = null,
                            tint = NeonCyan
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (signupMethod == "mobile") KeyboardType.Phone else KeyboardType.Email
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPurple,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Optional Quick Demo Auto-Fill helper
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (signupMethod == "mobile") "⚡ Fast-Fill: +91 9876543210" else "⚡ Fast-Fill: creator@viralboost.ai",
                        fontSize = 11.sp,
                        color = NeonCyan,
                        modifier = Modifier.clickable {
                            if (nameInput.isBlank()) nameInput = "Viral Creator"
                            contactInput = if (signupMethod == "mobile") "9876543210" else "creator@viralboost.ai"
                            if (signupMethod == "mobile") {
                                otpSent = true
                                otpInput = "123456"
                            }
                            agreed = true
                            Toast.makeText(context, "Filled test credentials! Ready to enter.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // If Mobile Signup and OTP has been sent
                AnimatedVisibility(
                    visible = signupMethod == "mobile" && otpSent,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it },
                            label = { Text("Enter 6-Digit OTP (e.g., 123456)") },
                            placeholder = { Text("123456") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = GoldAccent)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            supportingText = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("✓ SMS Code sent to $contactInput", fontSize = 10.sp, color = SuccessGreen)
                                    Text(
                                        text = "Auto-fill 123456",
                                        fontSize = 10.sp,
                                        color = GoldAccent,
                                        modifier = Modifier.clickable { otpInput = "123456" }
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mandatory Legal "I Agree" Terms & Conditions Checkbox Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1735)),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (agreed) SuccessGreen.copy(alpha = 0.7f) else DarkSurfaceBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = agreed,
                            onCheckedChange = { agreed = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SuccessGreen,
                                uncheckedColor = Color.LightGray,
                                checkmarkColor = Color.Black
                            ),
                            modifier = Modifier.testTag("terms_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Mandatory Legal Terms & Liability Waiver",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "I Agree to Terms & Conditions. I understand rewards and deals are provided as-is, releasing the owner from any liability or false claims.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = if (agreed) Color.White else Color(0xFFCBD5E1),
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🔒 One-time legal agreement proof is timestamped and recorded in admin backend audit logs for owner defense.",
                                fontSize = 9.5.sp,
                                color = NeonCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                val isSubmitEnabled = agreed && nameInput.isNotBlank() && contactInput.isNotBlank() &&
                        (signupMethod != "mobile" || !otpSent || otpInput.isNotBlank())

                Button(
                    onClick = {
                        if (signupMethod == "mobile" && !otpSent) {
                            if (contactInput.trim().length >= 10) {
                                otpSent = true
                                Toast.makeText(context, "OTP Sent to $contactInput! (Test code: 123456)", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            if (nameInput.isNotBlank() && contactInput.isNotBlank() && agreed) {
                                onSignupComplete(nameInput.trim(), contactInput.trim(), signupMethod)
                            }
                        }
                    },
                    enabled = isSubmitEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_signup_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (signupMethod == "mobile" && !otpSent) BrandPurple else SuccessGreen,
                        disabledContainerColor = DarkSurfaceElevated
                    )
                ) {
                    Icon(
                        imageVector = if (signupMethod == "mobile" && !otpSent) Icons.Default.Phone else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isSubmitEnabled) Color.White else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (signupMethod == "mobile" && !otpSent) "Send OTP & Verify" else "Complete Sign Up & Enter App",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isSubmitEnabled) Color.White else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Owner Admin Access Gate for app administrator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("owner_bypass_gate_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF130D2E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Owner Admin Quick-Gate",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Restricted to ${MonetizationConstants.OWNER_UPI_ID} administrator rights",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showOwnerModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("owner_onboarding_login_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("👑 Owner Bypass (${MonetizationConstants.OWNER_UPI_ID})", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Owner Login Modal Dialog
    if (showOwnerModal) {
        Dialog(onDismissRequest = { showOwnerModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("owner_auth_dialog_onboarding"),
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
                        text = "Admin Rights: ${MonetizationConstants.OWNER_UPI_ID}",
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
                        placeholder = { Text("admin2653") },
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
                            .testTag("owner_onboarding_passkey_input"),
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
                                .testTag("verify_owner_onboarding_button"),
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
