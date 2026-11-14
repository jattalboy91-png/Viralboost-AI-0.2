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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import com.example.data.local.AppConfig
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.PaymentRecord
import com.example.data.local.SupportTicket
import com.example.data.local.UserProfile
import com.example.data.local.LegalAgreementRecord
import com.example.data.repository.TrialStatus
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
import com.example.ui.theme.VipGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPanelScreen(
    userProfile: UserProfile?,
    appConfig: AppConfig = AppConfig(),
    trialStatus: TrialStatus = TrialStatus(1, true, false, 3, 5, 5, 3, 5, 5, false),
    allPayments: List<PaymentRecord>,
    allTickets: List<SupportTicket>,
    allLegalRecords: List<LegalAgreementRecord> = emptyList(),
    onVerifyPayment: (paymentId: Long, approve: Boolean, note: String) -> Unit,
    onReplyTicket: (ticketId: Long, reply: String) -> Unit,
    onSaveConfig: (AppConfig) -> Unit = {},
    onGrantBonus: (coins: Int, diamonds: Int) -> Unit,
    onSimulateTrialDay: (day: Int) -> Unit = {},
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var adminTab by remember { mutableIntStateOf(0) } // 0: UTR Payments, 1: Support Desk, 2: Economy & Stats

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Admin Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onExitAdmin, modifier = Modifier.testTag("admin_exit_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Exit Admin", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Owner Admin Panel",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Verified, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "Authenticated: ${appConfig.ownerUpiId}",
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF7F1D1D).copy(alpha = 0.5f))
                    .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("SECURE GATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
            }
        }

        // Admin Navigation Tabs (4 dedicated panels)
        TabRow(
            selectedTabIndex = adminTab,
            containerColor = DarkSurface,
            contentColor = GoldAccent,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = adminTab == 0,
                onClick = { adminTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("UTR (${allPayments.count { it.status == "PENDING" }})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = adminTab == 1,
                onClick = { adminTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Desk (${allTickets.count { it.status == "OPEN" }})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = adminTab == 2,
                onClick = { adminTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("AdMob & Config", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = adminTab == 3,
                onClick = { adminTab = 3 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Stats", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = adminTab == 4,
                onClick = { adminTab = 4 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Legal (${allLegalRecords.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (adminTab) {
                0 -> {
                    // TAB 0: UTR Payment Verifications
                    item {
                        Text(
                            text = "Submitted 12-Digit UTR Payments",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Verify payment received in UPI ${appConfig.ownerUpiId} and click Approve to activate VIP.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    if (allPayments.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No payment records yet.", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    } else {
                        items(allPayments) { payment ->
                            AdminPaymentVerificationCard(
                                payment = payment,
                                onApprove = { onVerifyPayment(payment.id, true, "Approved by Owner") },
                                onReject = { onVerifyPayment(payment.id, false, "Declined: Invalid UTR") }
                            )
                        }
                    }
                }

                1 -> {
                    // TAB 1: Support Desk & Tickets
                    item {
                        Text(
                            text = "Creator Support Messages & Tickets",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "View user questions and send direct replies.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    if (allTickets.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No support tickets currently open.", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    } else {
                        items(allTickets) { ticket ->
                            AdminSupportTicketReplyCard(
                                ticket = ticket,
                                onSendReply = { replyText -> onReplyTicket(ticket.id, replyText) }
                            )
                        }
                    }
                }

                2 -> {
                    // TAB 2: Dedicated Secure AdMob Suite & Owner Credentials Section
                    item {
                        AdminAdMobSettingsDashboard(
                            config = appConfig,
                            onSaveConfig = onSaveConfig
                        )
                    }
                }

                3 -> {
                    // TAB 3: Economy & Telemetry
                    item {
                        AdminStatsDashboard(
                            payments = allPayments,
                            tickets = allTickets,
                            profile = userProfile,
                            trialStatus = trialStatus,
                            onGrantBonus = onGrantBonus,
                            onSimulateTrialDay = onSimulateTrialDay
                        )
                    }
                }

                4 -> {
                    // TAB 4: Legal Proof Audit & Agreement Records
                    item {
                        Text(
                            text = "Legal Agreement Proof & Compliance Audit",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Permanent audit trail of user agreements, timestamps, and liability release records for owner legal defense.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    if (allLegalRecords.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No External Signups Yet", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(
                                        "New registrations via Mobile (OTP) or Google/Email will automatically appear here with exact timestamp and liability waiver proof.",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(allLegalRecords) { record ->
                            AdminLegalProofCard(record = record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminLegalProofCard(record: LegalAgreementRecord) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val clipboardManager = remember {
        context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_legal_proof_card_${record.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
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
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(record.userName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(record.contactOrEmail, fontSize = 11.sp, color = NeonCyan)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f))
                        .border(1.dp, SuccessGreen, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = record.status,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF130F26))
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("• Signup Method: ", fontSize = 10.5.sp, color = Color.Gray)
                    Text(if (record.signupMethod == "mobile") "Mobile Number (OTP)" else "Google / Email Sign-In", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("• Verified Timestamp: ", fontSize = 10.5.sp, color = Color.Gray)
                    Text(record.agreementTimestamp, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("• Legal Liability Waiver:", fontSize = 10.5.sp, color = Color.Gray)
                Text(
                    text = "\"${record.termsSummary}\"",
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    val proofString = "ADMIN_LEGAL_LOG: User=${record.userName}, Contact/Email=${record.contactOrEmail}, Time=${record.agreementTimestamp}, Status=AGREED_AND_VERIFIED"
                    val clip = android.content.ClipData.newPlainText("Legal Proof Record", proofString)
                    clipboardManager?.setPrimaryClip(clip)
                    android.widget.Toast.makeText(context, "Copied legal proof record to clipboard! 📋", android.widget.Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Legal Defense Record for Admin Logs", fontSize = 10.5.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AdminPaymentVerificationCard(
    payment: PaymentRecord,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isPending = payment.status == "PENDING"
    val isApproved = payment.status == "APPROVED"
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(payment.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_payment_card_${payment.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPending) GoldAccent else (if (isApproved) SuccessGreen else DarkSurfaceBorder)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = payment.planName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "User: ${payment.userName} • ID #${payment.userId}", fontSize = 11.sp, color = NeonCyan)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (payment.status) {
                                "APPROVED" -> SuccessGreen.copy(alpha = 0.2f)
                                "REJECTED" -> ErrorRed.copy(alpha = 0.2f)
                                else -> GoldAccent.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = payment.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (payment.status) {
                            "APPROVED" -> SuccessGreen
                            "REJECTED" -> ErrorRed
                            else -> GoldAccent
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // UTR Details Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("12-Digit UTR Ref:", fontSize = 11.sp, color = Color.Gray)
                        Text(payment.utrNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DiamondCyan)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Amount Received:", fontSize = 11.sp, color = Color.Gray)
                        Text("₹${payment.amountRupees}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Submission Time:", fontSize = 10.sp, color = Color.Gray)
                        Text(dateStr, fontSize = 10.sp, color = Color.LightGray)
                    }
                }
            }

            if (isPending) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify & Activate VIP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminSupportTicketReplyCard(
    ticket: SupportTicket,
    onSendReply: (replyText: String) -> Unit
) {
    var replyInput by remember { mutableStateOf("") }
    val isResolved = ticket.status == "RESOLVED"
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date(ticket.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_ticket_card_${ticket.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = ticket.subject, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "From: ${ticket.userName} (${ticket.userEmail})", fontSize = 10.sp, color = NeonCyan)
                }

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
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ticket.message,
                fontSize = 12.sp,
                color = Color.LightGray
            )

            if (ticket.adminReply.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Your Sent Reply: ${ticket.adminReply}",
                        fontSize = 11.sp,
                        color = SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reply input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyInput,
                    onValueChange = { replyInput = it },
                    placeholder = { Text("Type reply to creator...") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPurple,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        onSendReply(replyInput)
                        replyInput = ""
                    },
                    enabled = replyInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Reply", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun AdminStatsDashboard(
    payments: List<PaymentRecord>,
    tickets: List<SupportTicket>,
    profile: UserProfile?,
    trialStatus: TrialStatus,
    onGrantBonus: (coins: Int, diamonds: Int) -> Unit,
    onSimulateTrialDay: (day: Int) -> Unit
) {
    val totalRevenue = payments.filter { it.status == "APPROVED" }.sumOf { it.amountRupees }
    val pendingCount = payments.count { it.status == "PENDING" }
    val openTicketCount = tickets.count { it.status == "OPEN" }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Telemetry Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Monetization & System Telemetry",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Verified Revenue", fontSize = 10.sp, color = Color.Gray)
                        Text("₹$totalRevenue", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Column {
                        Text("Pending UTRs", fontSize = 10.sp, color = Color.Gray)
                        Text("$pendingCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    }
                    Column {
                        Text("Open Tickets", fontSize = 10.sp, color = Color.Gray)
                        Text("$openTicketCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                }
            }
        }

        // 3-Day Free Trial Simulator & Gating Card
        AdminTrialSimulatorCard(
            trialStatus = trialStatus,
            onSimulateTrialDay = onSimulateTrialDay
        )

        // Grant Bonus Controls (for testing / creator goodwill)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Economy Controller (Quick Grants)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onGrantBonus(100, 0) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+100 Coins", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onGrantBonus(0, 50) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+50 💎", fontSize = 11.sp, color = DiamondCyan, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onGrantBonus(0, 500) },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+500 💎 (VIP)", fontSize = 11.sp, color = VipGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAdMobSettingsDashboard(
    config: AppConfig,
    onSaveConfig: (AppConfig) -> Unit
) {
    var ownerUpiId by remember(config) { mutableStateOf(config.ownerUpiId) }
    var ownerName by remember(config) { mutableStateOf(config.ownerName) }
    var ownerQrUrl by remember(config) { mutableStateOf(config.ownerQrUrl) }
    var admobPublisherId by remember(config) { mutableStateOf(config.admobPublisherId) }
    var admobBannerUnitId by remember(config) { mutableStateOf(config.admobBannerUnitId) }
    var admobInterstitialUnitId by remember(config) { mutableStateOf(config.admobInterstitialUnitId) }
    var admobRewardedUnitId by remember(config) { mutableStateOf(config.admobRewardedUnitId) }
    var admobNativeUnitId by remember(config) { mutableStateOf(config.admobNativeUnitId) }
    var admobNativeVideoUnitId by remember(config) { mutableStateOf(config.admobNativeVideoUnitId) }
    var unityGameId by remember(config) { mutableStateOf(config.unityGameId) }
    var unityBannerPlacementId by remember(config) { mutableStateOf(config.unityBannerPlacementId) }
    var unityInterstitialPlacementId by remember(config) { mutableStateOf(config.unityInterstitialPlacementId) }
    var unityRewardedPlacementId by remember(config) { mutableStateOf(config.unityRewardedPlacementId) }
    var affiliateTag by remember(config) { mutableStateOf(config.affiliateTag) }
    var appDownloadUrl by remember(config) { mutableStateOf(config.appDownloadUrl) }
    var dailyStreakReward by remember(config) { mutableStateOf(config.dailyStreakReward.toString()) }
    var rewardedAdPayout by remember(config) { mutableStateOf(config.rewardedAdPayout.toString()) }
    var isSavedSuccessfully by remember { mutableStateOf(false) }
    var statusActionNote by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = androidx.compose.ui.platform.LocalContext.current

    val buildUpdatedConfig: () -> AppConfig = {
        config.copy(
            ownerUpiId = ownerUpiId.trim().ifBlank { MonetizationConstants.OWNER_UPI_ID },
            ownerName = ownerName.trim().ifBlank { MonetizationConstants.OWNER_NAME },
            ownerQrUrl = ownerQrUrl.trim().ifBlank { MonetizationConstants.OWNER_QR_CODE_URL },
            admobPublisherId = admobPublisherId.trim().ifBlank { MonetizationConstants.ADMOB_PUBLISHER_APP_ID },
            admobBannerUnitId = admobBannerUnitId.trim().ifBlank { MonetizationConstants.ADMOB_BANNER_UNIT_ID },
            admobInterstitialUnitId = admobInterstitialUnitId.trim().ifBlank { MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID },
            admobRewardedUnitId = admobRewardedUnitId.trim().ifBlank { MonetizationConstants.ADMOB_REWARDED_UNIT_ID },
            admobNativeUnitId = admobNativeUnitId.trim().ifBlank { MonetizationConstants.ADMOB_NATIVE_UNIT_ID },
            admobNativeVideoUnitId = admobNativeVideoVideoClean(admobNativeVideoUnitId),
            unityGameId = unityGameId.trim().ifBlank { MonetizationConstants.UNITY_GAME_ID },
            unityBannerPlacementId = unityBannerPlacementId.trim().ifBlank { MonetizationConstants.UNITY_BANNER_PLACEMENT_ID },
            unityInterstitialPlacementId = unityInterstitialPlacementId.trim().ifBlank { MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID },
            unityRewardedPlacementId = unityRewardedPlacementId.trim().ifBlank { MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID },
            affiliateTag = affiliateTag.trim().ifBlank { MonetizationConstants.AFFILIATE_TAG },
            appDownloadUrl = appDownloadUrl.trim().ifBlank { MonetizationConstants.buildAppDownloadUrl(affiliateTag) },
            dailyStreakReward = dailyStreakReward.trim().toIntOrNull() ?: 2,
            rewardedAdPayout = rewardedAdPayout.trim().toIntOrNull() ?: 5
        )
    }

    val commitConfigToRoomAndMediation: (String) -> Unit = { note ->
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        val updated = buildUpdatedConfig()
        onSaveConfig(updated)
        com.example.monetization.AdManager.syncCredentialsFromConfig(updated, context)
        isSavedSuccessfully = true
        statusActionNote = note
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Architecture Overview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, BrandPurple)
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
                                .background(BrandPurple.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AdMob Monetization & Architecture",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Production App ID, Units & Financial Routing",
                                fontSize = 11.sp,
                                color = NeonCyan
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SuccessGreen.copy(alpha = 0.2f))
                            .border(1.dp, SuccessGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("ROOM DB COMMIT", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "All AdMob ad units (Banner, Interstitial, Rewarded Video, Native, Native Video), Owner UPI ID, and Affiliate credentials are saved to local Room Database with immediate synchronous commit. When test IDs are replaced with live production credentials, changes survive app restarts or refreshes without resetting.",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    lineHeight = 15.sp
                )
            }
        }

        // Live Active Credentials Table Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Current Active Room DB Credentials",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.height(8.dp))

                ActiveConfigRow("Publisher App ID", config.admobPublisherId)
                ActiveConfigRow("Banner Ad Unit ID", config.admobBannerUnitId)
                ActiveConfigRow("Interstitial Unit ID", config.admobInterstitialUnitId)
                ActiveConfigRow("Rewarded Video Unit ID", config.admobRewardedUnitId)
                ActiveConfigRow("Native Ad Unit ID", config.admobNativeUnitId)
                ActiveConfigRow("Native Video Unit ID", config.admobNativeVideoUnitId)
                ActiveConfigRow("Unity Ads Game ID", config.unityGameId)
                ActiveConfigRow("Unity Banner Placement", config.unityBannerPlacementId)
                ActiveConfigRow("Unity Interstitial Placement", config.unityInterstitialPlacementId)
                ActiveConfigRow("Unity Rewarded Placement", config.unityRewardedPlacementId)
                ActiveConfigRow("Owner UPI ID", config.ownerUpiId)
                ActiveConfigRow("Affiliate Tag", config.affiliateTag)
                ActiveConfigRow("App Download Link", config.appDownloadUrl)
            }
        }

        // Smart AdMob + Unity Ads (800427608) Mediation & Fallback Live Status Card
        val mediationTelemetry by com.example.monetization.AdManager.mediationState.collectAsState()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_mediation_status_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111C38)),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonCyan)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚡ Smart AdMob + Unity Ads (800427608) Mediation Engine",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = mediationTelemetry.lastMediationLog,
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                ActiveConfigRow(
                    "Google AdMob Network",
                    if (mediationTelemetry.isAdMobActive) "ACTIVE (Rotating 50/50)" else "INACTIVE (Bypassed)"
                )
                ActiveConfigRow(
                    "Unity Ads [${mediationTelemetry.unityGameId}]",
                    if (mediationTelemetry.isUnityAdsActive) "ACTIVE (Rotating 50/50)" else "INACTIVE (Bypassed)"
                )
                ActiveConfigRow(
                    "Requests Served (B / I / R)",
                    "${mediationTelemetry.bannerRequestsServed} / ${mediationTelemetry.interstitialRequestsServed} / ${mediationTelemetry.rewardedRequestsServed}"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            com.example.monetization.AdManager.setNetworkActiveState(
                                adMobActive = true,
                                unityAdsActive = true
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Both Active (Rotate)", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = {
                            com.example.monetization.AdManager.setNetworkActiveState(
                                adMobActive = false,
                                unityAdsActive = true
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Unity Only Fallback", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = {
                            com.example.monetization.AdManager.setNetworkActiveState(
                                adMobActive = true,
                                unityAdsActive = false
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("AdMob Only Fallback", fontSize = 10.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Confirmation Banner on Save
        if (isSavedSuccessfully || statusActionNote != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF065F46))
                    .border(1.dp, Color(0xFF34D399), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusActionNote
                            ?: "Changes saved permanently to local Room Database! All AdMob & Unity Ads credentials are running live across the app.",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        // Editable Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_editable_credentials_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandPurple)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Section 1: AdMob Advertising Unit IDs
                Text(
                    text = "1. Google AdMob Advertising Unit IDs",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Replace default test unit IDs with your official production AdMob IDs:",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "Production AdMob Publisher App ID",
                    value = admobPublisherId,
                    onValueChange = { admobPublisherId = it },
                    testTag = "config_admob_publisher_id"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "AdMob Banner Ad Unit ID",
                    value = admobBannerUnitId,
                    onValueChange = { admobBannerUnitId = it },
                    testTag = "config_admob_banner_id"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "AdMob Interstitial Ad Unit ID",
                    value = admobInterstitialUnitId,
                    onValueChange = { admobInterstitialUnitId = it },
                    testTag = "config_admob_interstitial_id"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "AdMob Rewarded Video Ad Unit ID",
                    value = admobRewardedUnitId,
                    onValueChange = { admobRewardedUnitId = it },
                    testTag = "config_admob_rewarded_id"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "AdMob Native Ad Unit ID",
                    value = admobNativeUnitId,
                    onValueChange = { admobNativeUnitId = it },
                    testTag = "config_admob_native_id"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "AdMob Native Video Ad Unit ID",
                    value = admobNativeVideoUnitId,
                    onValueChange = { admobNativeVideoUnitId = it },
                    onSaveField = { commitConfigToRoomAndMediation("Saved AdMob Native Video Ad Unit ID to Room DB!") },
                    testTag = "config_admob_native_video_id"
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Unity Ads Credentials (Editable with Save, Edit, and Clear/Delete)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "2. Unity Ads Mediation & Fallback Credentials",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Edit, Save, or Clear/Delete Unity Game ID & Placement IDs (Synced to Room DB):",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00C853).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF00E676), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "UNITY SDK LIVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4ADE80)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "Unity Ads Game ID (Default: 800427608)",
                    value = unityGameId,
                    onValueChange = { unityGameId = it },
                    onSaveField = {
                        commitConfigToRoomAndMediation("Saved Unity Ads Game ID (${unityGameId.ifBlank { MonetizationConstants.UNITY_GAME_ID }}) to Room DB!")
                    },
                    onClearField = {
                        unityGameId = ""
                        statusActionNote = "Cleared Unity Ads Game ID field — enter a new ID or click Save to restore default (800427608)."
                    },
                    testTag = "config_unity_game_id"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "Unity Banner Placement ID (Default: BP_Banner_Android)",
                    value = unityBannerPlacementId,
                    onValueChange = { unityBannerPlacementId = it },
                    onSaveField = {
                        commitConfigToRoomAndMediation("Saved Unity Banner Placement ID (${unityBannerPlacementId.ifBlank { MonetizationConstants.UNITY_BANNER_PLACEMENT_ID }}) to Room DB!")
                    },
                    onClearField = {
                        unityBannerPlacementId = ""
                        statusActionNote = "Cleared Unity Banner Placement ID field — enter a new ID or Save to restore default (BP_Banner_Android)."
                    },
                    testTag = "config_unity_banner_id"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "Unity Interstitial Placement ID (Default: BP_Interstitial_Android)",
                    value = unityInterstitialPlacementId,
                    onValueChange = { unityInterstitialPlacementId = it },
                    onSaveField = {
                        commitConfigToRoomAndMediation("Saved Unity Interstitial Placement ID (${unityInterstitialPlacementId.ifBlank { MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID }}) to Room DB!")
                    },
                    onClearField = {
                        unityInterstitialPlacementId = ""
                        statusActionNote = "Cleared Unity Interstitial Placement ID field — enter a new ID or Save to restore default (BP_Interstitial_Android)."
                    },
                    testTag = "config_unity_interstitial_id"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "Unity Rewarded Placement ID (Default: BP_Rewarded_Android)",
                    value = unityRewardedPlacementId,
                    onValueChange = { unityRewardedPlacementId = it },
                    onSaveField = {
                        commitConfigToRoomAndMediation("Saved Unity Rewarded Placement ID (${unityRewardedPlacementId.ifBlank { MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID }}) to Room DB!")
                    },
                    onClearField = {
                        unityRewardedPlacementId = ""
                        statusActionNote = "Cleared Unity Rewarded Placement ID field — enter a new ID or Save to restore default (BP_Rewarded_Android)."
                    },
                    testTag = "config_unity_rewarded_id"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Unity Ads Action Bar: Save Unity IDs, Clear/Delete Unity IDs, Restore Unity Defaults
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            commitConfigToRoomAndMediation(
                                "✅ Unity Ads Credentials Saved to Room DB! (Game ID: ${unityGameId.ifBlank { MonetizationConstants.UNITY_GAME_ID }})"
                            )
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(38.dp)
                            .testTag("save_unity_credentials_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Unity IDs",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Save Unity IDs",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            unityGameId = ""
                            unityBannerPlacementId = ""
                            unityInterstitialPlacementId = ""
                            unityRewardedPlacementId = ""
                            isSavedSuccessfully = false
                            statusActionNote = "🗑️ Cleared all 4 Unity Ads input fields. Type custom IDs or tap Restore Defaults."
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("clear_unity_credentials_button"),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Unity IDs",
                            tint = ErrorRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Clear / Delete",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            unityGameId = MonetizationConstants.UNITY_GAME_ID
                            unityBannerPlacementId = MonetizationConstants.UNITY_BANNER_PLACEMENT_ID
                            unityInterstitialPlacementId = MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID
                            unityRewardedPlacementId = MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID
                            commitConfigToRoomAndMediation(
                                "🔄 Restored & Saved Default Unity IDs (800427608, BP_Banner_Android, BP_Interstitial_Android, BP_Rewarded_Android)"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("reset_unity_credentials_button"),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restore Unity Defaults",
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Defaults",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Owner Payment & Affiliate Tag
                Text(
                    text = "3. Owner Payment UPI & Affiliate Tag",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "Owner UPI ID (Default: Amit2653@ptyes)",
                    value = ownerUpiId,
                    onValueChange = { ownerUpiId = it },
                    onSaveField = { commitConfigToRoomAndMediation("Saved Owner UPI ID to Room DB!") },
                    testTag = "config_owner_upi_id"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "Affiliate Tag (Default: 5665937)",
                    value = affiliateTag,
                    onValueChange = { affiliateTag = it },
                    onSaveField = { commitConfigToRoomAndMediation("Saved Affiliate Tag to Room DB!") },
                    testTag = "config_affiliate_tag"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "Owner Payee Name",
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    onSaveField = { commitConfigToRoomAndMediation("Saved Owner Payee Name to Room DB!") },
                    testTag = "config_owner_name"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "Owner Payment QR Image URL",
                    value = ownerQrUrl,
                    onValueChange = { ownerQrUrl = it },
                    onSaveField = { commitConfigToRoomAndMediation("Saved Owner QR URL to Room DB!") },
                    testTag = "config_owner_qr_url"
                )
                Spacer(modifier = Modifier.height(8.dp))

                AdminConfigTextField(
                    label = "App Download / Landing Page URL (Viral Footer)",
                    value = appDownloadUrl,
                    onValueChange = { appDownloadUrl = it },
                    onSaveField = { commitConfigToRoomAndMediation("Saved App Download URL to Room DB!") },
                    testTag = "config_app_download_url"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Permanent Owner Affiliate & High-Conversion Caption Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_permanent_affiliate_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🔒 Official Owner Affiliate Link (Locked)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SuccessGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("EarnKaro ID: 5665937", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = MonetizationConstants.PERMANENT_OWNER_AFFILIATE_LINK,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Auto-Embedded High-Conversion Share Hook & Footer:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0B0F19))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!\n" +
                                       "😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:\n" +
                                       "👉 https://bitli.in/Ff4aE3C\n\n" +
                                       "🚀 Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:\n" +
                                       "👉 $appDownloadUrl",
                                fontSize = 10.sp,
                                color = Color(0xFF80CBC4),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: Diamond Economy Tuning
                Text(
                    text = "4. Streak & Rewarded Ad Diamond Economy",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminConfigTextField(
                        label = "Daily Streak Reward 💎",
                        value = dailyStreakReward,
                        onValueChange = { if (it.all { c -> c.isDigit() }) dailyStreakReward = it },
                        modifier = Modifier.weight(1f),
                        testTag = "config_streak_diamonds"
                    )

                    AdminConfigTextField(
                        label = "Rewarded Ad Payout 💎",
                        value = rewardedAdPayout,
                        onValueChange = { if (it.all { c -> c.isDigit() }) rewardedAdPayout = it },
                        modifier = Modifier.weight(1f),
                        testTag = "config_rewarded_diamonds"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Save Action: Syncs AdMob + Unity Ads + UPI to local Room Database
                Button(
                    onClick = {
                        commitConfigToRoomAndMediation(
                            "Changes saved permanently to local Room Database! All AdMob & Unity Ads (${unityGameId.ifBlank { MonetizationConstants.UNITY_GAME_ID }}) credentials are running live across the app."
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_credentials_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Credentials Permanently (Room DB Sync)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Reset Action: Reverts inputs to defaults
                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        ownerUpiId = MonetizationConstants.OWNER_UPI_ID
                        ownerName = MonetizationConstants.OWNER_NAME
                        ownerQrUrl = MonetizationConstants.OWNER_QR_CODE_URL
                        admobPublisherId = MonetizationConstants.ADMOB_PUBLISHER_APP_ID
                        admobBannerUnitId = MonetizationConstants.ADMOB_BANNER_UNIT_ID
                        admobInterstitialUnitId = MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID
                        admobRewardedUnitId = MonetizationConstants.ADMOB_REWARDED_UNIT_ID
                        admobNativeUnitId = MonetizationConstants.ADMOB_NATIVE_UNIT_ID
                        admobNativeVideoUnitId = MonetizationConstants.ADMOB_NATIVE_VIDEO_UNIT_ID
                        unityGameId = MonetizationConstants.UNITY_GAME_ID
                        unityBannerPlacementId = MonetizationConstants.UNITY_BANNER_PLACEMENT_ID
                        unityInterstitialPlacementId = MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID
                        unityRewardedPlacementId = MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID
                        affiliateTag = MonetizationConstants.AFFILIATE_TAG
                        appDownloadUrl = MonetizationConstants.buildAppDownloadUrl(MonetizationConstants.AFFILIATE_TAG)
                        dailyStreakReward = MonetizationConstants.DAILY_STREAK_DIAMOND_REWARD.toString()
                        rewardedAdPayout = MonetizationConstants.REWARDED_AD_DIAMOND_REWARD.toString()
                        commitConfigToRoomAndMediation(
                            "Reset all AdMob, Unity Ads (800427608), and Owner fields to default system IDs in Room DB."
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("reset_credentials_defaults_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reset Fields to Default System IDs",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

private fun admobNativeVideoVideoClean(raw: String): String {
    return raw.trim().ifBlank { MonetizationConstants.ADMOB_NATIVE_VIDEO_UNIT_ID }
}

@Composable
private fun ActiveConfigRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 10.5.sp, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.5f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
private fun AdminConfigTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onSaveField: (() -> Unit)? = null,
    onClearField: (() -> Unit)? = null
) {
    var isEditing by remember { mutableStateOf(true) }
    OutlinedTextField(
        value = value,
        onValueChange = {
            isEditing = true
            onValueChange(it)
        },
        readOnly = !isEditing,
        label = { Text(label, fontSize = 11.sp) },
        singleLine = true,
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                // Edit / Unlock toggle button
                IconButton(
                    onClick = { isEditing = !isEditing },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag(if (testTag.isNotEmpty()) "${testTag}_edit_btn" else "")
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.Lock,
                        contentDescription = "Edit $label",
                        tint = if (isEditing) NeonCyan else Color.Gray,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Clear / Delete field value button
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            isEditing = true
                            if (onClearField != null) {
                                onClearField()
                            } else {
                                onValueChange("")
                            }
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag(if (testTag.isNotEmpty()) "${testTag}_clear_btn" else "")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear or Delete $label",
                            tint = ErrorRed,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Inline Save field button
                if (onSaveField != null) {
                    IconButton(
                        onClick = { onSaveField() },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag(if (testTag.isNotEmpty()) "${testTag}_save_btn" else "")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save $label",
                            tint = SuccessGreen,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = DarkSurfaceBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = NeonCyan,
            unfocusedLabelColor = Color.Gray
        )
    )
}

@Composable
private fun AdminTrialSimulatorCard(
    trialStatus: TrialStatus,
    onSimulateTrialDay: (day: Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_trial_simulator_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF191436)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "3-Day Free Trial Simulator & Gating",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (trialStatus.isExpired) Color(0xFF7F1D1D) else Color(0xFF1E293B))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (trialStatus.isVip) "VIP PRO" else (if (trialStatus.isExpired) "EXPIRED (Day 4+)" else "DAY ${trialStatus.trialDay}"),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (trialStatus.isExpired) Color(0xFFFCA5A5) else NeonCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Simulate elapsed trial days to test user urgency, decreased daily allowances, and strict paywall locks.",
                fontSize = 10.5.sp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Current Quotas telemetry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TrialStatBadge("Videos", "${trialStatus.videosRemaining}/${trialStatus.maxVideosToday}", modifier = Modifier.weight(1f))
                TrialStatBadge("Images", "${trialStatus.imagesRemaining}/${trialStatus.maxImagesToday}", modifier = Modifier.weight(1f))
                TrialStatBadge("Text/Tools", "${trialStatus.textRemaining}/${trialStatus.maxTextToday}", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onSimulateTrialDay(1) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (trialStatus.trialDay == 1 && !trialStatus.isExpired) BrandPurple else Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Day 1 (High)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = { onSimulateTrialDay(2) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (trialStatus.trialDay == 2) BrandPurple else Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Day 2 (Mid)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = { onSimulateTrialDay(3) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (trialStatus.trialDay == 3) BrandPurple else Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Day 3 (Min)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onSimulateTrialDay(4) },
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate Expired", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = { onSimulateTrialDay(1) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Reset Day 1", fontSize = 10.5.sp, color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
private fun TrialStatBadge(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkSurfaceElevated)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 9.sp, color = Color.Gray)
            Text(value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
        }
    }
}

