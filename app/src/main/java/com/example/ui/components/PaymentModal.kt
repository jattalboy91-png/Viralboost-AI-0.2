package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.monetization.MonetizationConstants
import com.example.monetization.PaymentManager
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.PlanOption

@Composable
fun PaymentModal(
    plan: PlanOption,
    userDiamonds: Int,
    ownerUpiId: String = MonetizationConstants.OWNER_UPI_ID,
    ownerName: String = MonetizationConstants.OWNER_NAME,
    ownerQrUrl: String = MonetizationConstants.OWNER_QR_CODE_URL,
    onDismiss: () -> Unit,
    onSubmitUtr: (plan: PlanOption, utr: String, method: String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val safeDismiss = {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        onDismiss()
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: UPI Intent, 1: QR & 12-Digit UTR
    var utrInput by remember { mutableStateOf("") }
    var utrError by remember { mutableStateOf<String?>(null) }
    var applyDiamondsDiscount by remember { mutableStateOf(false) }

    // Diamond discount power calculation (~5 diamonds = ₹1-₹2 discount)
    val maxDiamondsApplicable = (userDiamonds / 10) * 10
    val discountRupees = if (applyDiamondsDiscount && maxDiamondsApplicable >= 50) {
        (maxDiamondsApplicable / MonetizationConstants.DIAMONDS_PER_RUPEE_DISCOUNT).toInt().coerceAtMost(plan.priceRupees - 49)
    } else 0
    val finalPriceRupees = plan.priceRupees - discountRupees

    Dialog(
        onDismissRequest = safeDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("dual_payment_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, BrandPurple)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Upgrade to VIP Pro",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = plan.name,
                            fontSize = 13.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = safeDismiss,
                        modifier = Modifier.testTag("close_payment_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price & Diamond Utility Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF2E1065), Color(0xFF1E1B4B))
                            )
                        )
                        .border(1.dp, BrandPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Amount Payable",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "₹$finalPriceRupees",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (discountRupees > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "₹${plan.priceRupees}",
                                        fontSize = 15.sp,
                                        color = Color.Gray,
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    )
                                }
                            }
                        }

                        // Diamond discount switch if user has 50+ diamonds
                        if (userDiamonds >= 50) {
                            OutlinedButton(
                                onClick = { applyDiamondsDiscount = !applyDiamondsDiscount },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (applyDiamondsDiscount) GoldAccent else DiamondCyan
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (applyDiamondsDiscount) GoldAccent else DiamondCyan
                                )
                            ) {
                                Text(
                                    text = if (applyDiamondsDiscount) "-₹$discountRupees Applied" else "Use Diamonds",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E1B4B),
                    contentColor = NeonCyan,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("UPI Deep Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("QR & 12-Digit UTR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // Method 1: UPI Intent Deep Linking
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Direct Instant App-to-App Payment",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Opens installed UPI apps (GPay, PhonePe, Paytm, BHIM) with pre-filled amount and recipient.",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val success = PaymentManager.launchUpiIntent(
                                    context = context,
                                    amountRupees = finalPriceRupees,
                                    planName = plan.name,
                                    ownerUpiId = ownerUpiId,
                                    ownerName = ownerName
                                )
                                if (!success) {
                                    // Switch to QR Tab fallback automatically
                                    selectedTab = 1
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("launch_upi_intent_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPurple)
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pay ₹$finalPriceRupees via UPI App",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Switch to QR Fallback
                        OutlinedButton(
                            onClick = { selectedTab = 1 },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Prefer QR Code or Manual UTR? Click here", fontSize = 12.sp)
                        }
                    }
                } else {
                    // Method 2: QR Code Fallback & 12-Digit UTR Verification
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Scan Owner QR Code & Verify UTR",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Scan QR or pay to UPI ID below, then enter your 12-digit transaction UTR to activate VIP.",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // QR Code Image
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(2.dp, BrandPurple, RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            SubcomposeAsyncImage(
                                model = ownerQrUrl,
                                contentDescription = "Owner Payment QR Code",
                                modifier = Modifier.size(154.dp),
                                contentScale = ContentScale.Fit,
                                loading = {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp), color = BrandPurple)
                                },
                                error = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.Black, modifier = Modifier.size(48.dp))
                                        Text("Scan to Pay", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                        Text(ownerUpiId, fontSize = 9.sp, color = Color.DarkGray)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Copy UPI ID row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E1B4B))
                                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Owner UPI ID", fontSize = 10.sp, color = Color.Gray)
                                Text(
                                    ownerUpiId,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("UPI ID", ownerUpiId)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "UPI ID copied: $ownerUpiId", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy UPI ID", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 12-Digit UTR Input
                        OutlinedTextField(
                            value = utrInput,
                            onValueChange = {
                                if (it.length <= 12 && it.all { char -> char.isDigit() }) {
                                    utrInput = it
                                    utrError = null
                                }
                            },
                            label = { Text("12-Digit UTR Number / Ref ID") },
                            placeholder = { Text("e.g. 426891024519") },
                            singleLine = true,
                            isError = utrError != null,
                            supportingText = {
                                if (utrError != null) {
                                    Text(utrError!!, color = ErrorRed, fontSize = 11.sp)
                                } else {
                                    Text("${utrInput.length}/12 Digits", fontSize = 11.sp, color = Color.Gray)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (PaymentManager.isValidUtr(utrInput)) {
                                        focusManager.clearFocus(force = true)
                                        keyboardController?.hide()
                                        onSubmitUtr(plan, utrInput, "QR_CODE_MANUAL")
                                    } else {
                                        utrError = "Must be exactly 12 digits"
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPurple,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("utr_input_field")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (PaymentManager.isValidUtr(utrInput)) {
                                    focusManager.clearFocus(force = true)
                                    keyboardController?.hide()
                                    onSubmitUtr(plan, utrInput, "QR_CODE_MANUAL")
                                } else {
                                    utrError = "Please enter all 12 digits of the transaction UTR"
                                }
                            },
                            enabled = utrInput.length == 12,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_utr_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit 12-Digit UTR for Verification",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Security Note
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Encrypted UPI Payment to $ownerUpiId",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                UniversalMediatedAdDock(
                    isVip = false,
                    featureLabel = "UPI Checkout (${plan.name})"
                )
            }
        }
    }
}
