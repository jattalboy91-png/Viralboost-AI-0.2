package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.viralboost.ai.RealPhotoMallProduct
import com.viralboost.ai.ViralBoostRealImageMallApp

typealias RealPhotoMallProduct = com.viralboost.ai.RealPhotoMallProduct

@Composable
fun RealPhotoMallScreen(
    bonusRupees: Double = 50.0,
    userDiamonds: Int = 10,
    userCoins: Int = 250,
    onBackToHome: () -> Unit = {},
    onOrderSuccessful: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ViralBoostRealImageMallApp(
        bonusRupees = bonusRupees,
        userDiamonds = userDiamonds,
        userCoins = userCoins,
        onBackToHome = onBackToHome,
        onOrderSuccessful = onOrderSuccessful,
        modifier = modifier
    )
}
