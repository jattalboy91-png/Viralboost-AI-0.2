package com.viralboost.ai

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Rich Product Data Model for Shopsy & Ajio Style Mega Shopping Store
 * Pre-tagged with EarnKaro / Shopsy Affiliate ID: 5665937.
 */
data class MegaStoreProduct(
    val id: Int,
    val title: String,
    val category: String,
    val discountText: String,
    val price: String,
    val oldPrice: String,
    val commission: String,
    val rating: String
)

@Composable
fun ShopsyMegaStoreScreen(
    onOrderComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    var actionStatus by remember { mutableStateOf<String?>(null) }

    // Checkout & Address Form States
    var showAddressForm by remember { mutableStateOf(false) }
    var selectedProductTitle by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var showCheckoutAdModal by remember { mutableStateOf(false) }

    // Rich Banner-Style Store Catalog matching Ajio / Shopsy Super Sellers Look
    val megaStoreCatalog = remember {
        listOf(
            MegaStoreProduct(
                id = 1,
                title = "Min 65% Off on Label Ritu Kumar & Ethnic Sets",
                category = "Ethnic Fashion",
                discountText = "Min 65% OFF",
                price = "₹551",
                oldPrice = "₹1,599",
                commission = "Upto 12.5% Profit | ID: 5665937",
                rating = "★ 4.6"
            ),
            MegaStoreProduct(
                id = 2,
                title = "Min. 65% Off on GAS Apprarels & Designer Shoes",
                category = "Footwear",
                discountText = "Min. 65% OFF",
                price = "₹499",
                oldPrice = "₹1,499",
                commission = "Upto 10% Profit | ID: 5665937",
                rating = "★ 4.8"
            ),
            MegaStoreProduct(
                id = 3,
                title = "Pro Wireless Noise-Cancelling Earbuds & Tech",
                category = "Audio & Tech",
                discountText = "78% OFF",
                price = "₹349",
                oldPrice = "₹1,499",
                commission = "Affiliate Commission | ID: 5665937",
                rating = "★ 4.7"
            ),
            MegaStoreProduct(
                id = 4,
                title = "Smart Touch Fitness Watch HR+SpO2 Trackers",
                category = "Smart Gadgets",
                discountText = "76% OFF",
                price = "₹599",
                oldPrice = "₹2,499",
                commission = "Affiliate Commission | ID: 5665937",
                rating = "★ 4.9"
            ),
            MegaStoreProduct(
                id = 5,
                title = "Waterproof 10-inch LED Ring Light & Creator Tripod",
                category = "Creator Gear",
                discountText = "75% OFF",
                price = "₹299",
                oldPrice = "₹1,299",
                commission = "Affiliate Commission | ID: 5665937",
                rating = "★ 4.8"
            ),
            MegaStoreProduct(
                id = 6,
                title = "3-Piece Non-Stick Granite Kitchen Cookware Set",
                category = "Home & Kitchen",
                discountText = "75% OFF",
                price = "₹799",
                oldPrice = "₹3,199",
                commission = "Affiliate Commission | ID: 5665937",
                rating = "★ 4.5"
            )
        )
    }

    val filteredItems = remember(selectedCategory, megaStoreCatalog) {
        if (selectedCategory == "All") megaStoreCatalog else megaStoreCatalog.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("shopsy_mega_store_screen")
    ) {
        // Top Benefit Notice Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mega_store_benefit_banner")
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "🎉 ₹50 वेलकम डिस्काउंट सक्रिय!\n💎 आर्डर करने पर पाएँ: +10 डायमंड और +15 कॉइन्स (Affiliate ID: 5665937)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Shopsy & Ajio Super Sellers • Verified Highest Commission Tag: 5665937",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Pills
        val storeCategories = listOf(
            "All" to "All",
            "Fashion" to "Ethnic Fashion",
            "Footwear" to "Footwear",
            "Audio" to "Audio & Tech",
            "Gadgets" to "Smart Gadgets",
            "Creator Gear" to "Creator Gear",
            "Home" to "Home & Kitchen"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(storeCategories) { (displayLabel, categoryKey) ->
                val isSelected = selectedCategory == categoryKey || (categoryKey == "All" && selectedCategory == "All")
                Button(
                    onClick = { selectedCategory = categoryKey },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = displayLabel,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Banner-Style Rich Product Cards List (Ajio / Shopsy Look)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredItems) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mega_store_product_${item.id}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE11D48), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.discountText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = item.rating,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB300)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Price: ${item.price}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "(Old: ${item.oldPrice})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }

                            Text(
                                text = item.commission,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: Order Now & Share Deal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    selectedProductTitle = item.title
                                    showAddressForm = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_order_now_${item.id}")
                            ) {
                                Text(
                                    text = "Order Now 🛒",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = {
                                    val shareText = "🔥 Check out ${item.title} at ${item.price} (${item.discountText}) on Shopsy/Ajio! Use official link: https://bitli.in/Ff4aE3C (Affiliate ID: 5665937)"
                                    try {
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Shopsy Deal"))
                                    } catch (_: Exception) {}
                                    actionStatus = "📋 Affiliate Deal Link Copied! (ID: 5665937)"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_share_deal_${item.id}")
                            ) {
                                Text(
                                    text = "Share Deal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        actionStatus?.let { status ->
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }

    // Address Form Dialog
    if (showAddressForm) {
        AlertDialog(
            onDismissRequest = { showAddressForm = false },
            title = {
                Text(
                    text = "📦 Delivery Address Form",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Product: $selectedProductTitle",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Receiver Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_receiver_name")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Mobile Number") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_receiver_phone")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("Full Delivery Address & Pincode") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_receiver_address")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customerName.isNotBlank() && customerPhone.isNotBlank() && customerAddress.isNotBlank()) {
                            showAddressForm = false
                            showCheckoutAdModal = true // Triggers AdMob Ad before success
                        }
                    },
                    enabled = customerName.isNotBlank() && customerPhone.isNotBlank() && customerAddress.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_place_order_checkout")
                ) {
                    Text(
                        text = "Place Order & Verify (View Ad)",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressForm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // AdMob Interstitial Ad during Checkout
    if (showCheckoutAdModal) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "AdMob Interstitial Ad (Processing Order)",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "📺 [AdMob Ad Loaded]\n\nProcessing order with Affiliate ID: 5665937 and ₹50 discount...\n\nReceiver: $customerName\nPhone: $customerPhone\nDelivery: $customerAddress\n\n✓ Cash on Delivery / Free Delivery\n✓ Reward: +10 Diamonds 💎 & +15 Coins 🪙"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCheckoutAdModal = false
                        onOrderComplete() // Rewards +10 Diamonds & +15 Coins
                        actionStatus = "✅ Order Placed Successfully! Earned +10 Diamonds & +15 Coins!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_complete_order_rewards")
                ) {
                    Text(
                        text = "Complete Order & Get Rewards",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}
