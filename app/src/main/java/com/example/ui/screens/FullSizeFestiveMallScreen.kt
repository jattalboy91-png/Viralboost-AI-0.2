package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.viralboost.ai.FestiveMallProduct
import com.viralboost.ai.FullSizeFestiveMallScreen

typealias FestiveMallProduct = com.viralboost.ai.FestiveMallProduct

@Composable
fun FullSizeFestiveMallScreen(
    userDiamonds: Int = 10,
    userCoins: Int = 250,
    onBackToHome: () -> Unit = {},
    onOrderSuccessful: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    com.viralboost.ai.FullSizeFestiveMallScreen(
        userDiamonds = userDiamonds,
        userCoins = userCoins,
        onBackToHome = onBackToHome,
        onOrderSuccessful = onOrderSuccessful,
        modifier = modifier
    )
}
