package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.viralboost.ai.MegaStoreProduct
import com.viralboost.ai.ShopsyMegaStoreScreen

typealias MegaStoreProduct = com.viralboost.ai.MegaStoreProduct

@Composable
fun ShopsyMegaStoreScreen(
    onOrderComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    com.viralboost.ai.ShopsyMegaStoreScreen(
        onOrderComplete = onOrderComplete,
        modifier = modifier
    )
}
