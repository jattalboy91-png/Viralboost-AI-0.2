package com.example.ui.components

import android.view.ViewGroup
import android.widget.LinearLayout
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.monetization.AdFormat
import com.example.monetization.AdManager
import com.example.monetization.AdNetwork
import com.example.monetization.MediatedAdDecision
import com.example.monetization.MonetizationConstants
import com.example.monetization.findActivity
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import kotlinx.coroutines.delay

@Composable
fun BannerAdView(
    isVip: Boolean,
    adUnitId: String = AdManager.activeAdMobBannerUnitId,
    unityBannerPlacementId: String = AdManager.activeUnityBannerPlacementId,
    unityGameId: String = AdManager.activeUnityGameId,
    modifier: Modifier = Modifier
) {
    // 100% Ad-Free for VIP Creators
    if (isVip) return

    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val isEmulator = remember { AdManager.isRunningOnEmulator() }
    val mediationState by AdManager.mediationState.collectAsState()

    val resolvedUnityGameId = unityGameId.trim().ifBlank {
        mediationState.unityGameId.ifBlank { MonetizationConstants.UNITY_GAME_ID }
    }
    val resolvedUnityBannerPlacement = unityBannerPlacementId.trim().ifBlank {
        mediationState.unityBannerPlacementId.ifBlank { MonetizationConstants.UNITY_BANNER_PLACEMENT_ID }
    }
    val resolvedAdMobBannerUnit = adUnitId.trim().ifBlank {
        AdManager.activeAdMobBannerUnitId.ifBlank { MonetizationConstants.ADMOB_BANNER_UNIT_ID }
    }

    var rotationTick by remember { mutableIntStateOf(0) }
    var admobBannerFailed by remember { mutableStateOf(false) }
    var unityBannerFailed by remember { mutableStateOf(false) }

    // Ensure both AdMob and Unity Ads are initialized with the Room DB Game ID
    LaunchedEffect(resolvedUnityGameId) {
        if (!isPreview) {
            AdManager.initialize(context, resolvedUnityGameId)
        }
    }

    // Periodically rotate banner turn between active networks (AdMob <-> Unity Ads)
    LaunchedEffect(mediationState.isAdMobActive, mediationState.isUnityAdsActive) {
        while (true) {
            delay(15000L)
            rotationTick++
        }
    }

    val currentDecision: MediatedAdDecision = remember(
        rotationTick,
        resolvedAdMobBannerUnit,
        resolvedUnityBannerPlacement,
        resolvedUnityGameId,
        mediationState.isAdMobActive,
        mediationState.isUnityAdsActive
    ) {
        admobBannerFailed = false
        unityBannerFailed = false
        AdManager.selectNextMediatedAd(
            format = AdFormat.BANNER,
            admobUnitId = resolvedAdMobBannerUnit,
            unityPlacementId = resolvedUnityBannerPlacement
        )
    }

    // Determine effective network after any immediate view-level fallback
    val effectiveNetwork = when {
        currentDecision.network == AdNetwork.ADMOB && admobBannerFailed && !unityBannerFailed && mediationState.isUnityAdsActive ->
            AdNetwork.UNITY_ADS
        currentDecision.network == AdNetwork.UNITY_ADS && unityBannerFailed && !admobBannerFailed && mediationState.isAdMobActive ->
            AdNetwork.ADMOB
        else -> currentDecision.network
    }

    val shouldShowPermanentFallbackBox =
        isPreview || isEmulator || (admobBannerFailed && unityBannerFailed) ||
            (effectiveNetwork == AdNetwork.ADMOB && admobBannerFailed) ||
            (effectiveNetwork == AdNetwork.UNITY_ADS && unityBannerFailed)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admob_banner_view")
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        if (shouldShowPermanentFallbackBox) {
            // Permanent high-visibility banner box guarantees uninterrupted banner presence
            FallbackBannerBox(
                network = effectiveNetwork,
                admobUnitId = resolvedAdMobBannerUnit,
                unityPlacementId = resolvedUnityBannerPlacement,
                unityGameId = resolvedUnityGameId,
                requestTurn = currentDecision.requestNumber,
                isFallback = currentDecision.isFallback || admobBannerFailed || unityBannerFailed,
                onRotateNow = { rotationTick++ }
            )
        } else if (effectiveNetwork == AdNetwork.UNITY_ADS) {
            // Serve Unity Ads Banner linked to saved Room DB Game ID & Placement ID
            val activity = context.findActivity()
            if (activity == null) {
                FallbackBannerBox(
                    network = AdNetwork.UNITY_ADS,
                    admobUnitId = resolvedAdMobBannerUnit,
                    unityPlacementId = resolvedUnityBannerPlacement,
                    unityGameId = resolvedUnityGameId,
                    requestTurn = currentDecision.requestNumber,
                    isFallback = true,
                    onRotateNow = { rotationTick++ }
                )
            } else {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("unity_banner_android_view"),
                    factory = { ctx ->
                        val hostActivity = ctx.findActivity() ?: activity
                        BannerView(
                            hostActivity,
                            resolvedUnityBannerPlacement,
                            UnityBannerSize(320, 50)
                        ).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            listener = object : BannerView.IListener {
                                override fun onBannerLoaded(bannerAdView: BannerView?) {
                                    unityBannerFailed = false
                                    AdManager.reportNetworkSuccess(AdNetwork.UNITY_ADS, AdFormat.BANNER)
                                }

                                override fun onBannerShown(bannerAdView: BannerView?) {
                                    AdManager.reportNetworkSuccess(AdNetwork.UNITY_ADS, AdFormat.BANNER)
                                }

                                override fun onBannerClick(bannerAdView: BannerView?) {}

                                override fun onBannerFailedToLoad(
                                    bannerAdView: BannerView?,
                                    errorInfo: BannerErrorInfo?
                                ) {
                                    AdManager.reportNetworkFailure(
                                        AdNetwork.UNITY_ADS,
                                        AdFormat.BANNER,
                                        errorInfo?.errorMessage ?: "Unity Banner load failed"
                                    )
                                    AdManager.getFallbackDecisionFor(currentDecision, adUnitId, unityBannerPlacementId)
                                    unityBannerFailed = true
                                }

                                override fun onBannerLeftApplication(bannerAdView: BannerView?) {}
                            }
                            try {
                                load()
                            } catch (e: Throwable) {
                                AdManager.reportNetworkFailure(AdNetwork.UNITY_ADS, AdFormat.BANNER, e.message ?: "load exception")
                                unityBannerFailed = true
                            }
                        }
                    }
                )
            }
        } else {
            // Serve Google AdMob Banner
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                factory = { ctx ->
                    AdView(ctx).apply {
                        setAdSize(AdSize.BANNER)
                        this.adUnitId = adUnitId.ifBlank { MonetizationConstants.ADMOB_BANNER_UNIT_ID }
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        adListener = object : AdListener() {
                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                super.onAdFailedToLoad(loadAdError)
                                AdManager.reportNetworkFailure(
                                    AdNetwork.ADMOB,
                                    AdFormat.BANNER,
                                    loadAdError.message
                                )
                                AdManager.getFallbackDecisionFor(currentDecision, adUnitId, unityBannerPlacementId)
                                admobBannerFailed = true
                            }

                            override fun onAdLoaded() {
                                super.onAdLoaded()
                                admobBannerFailed = false
                                AdManager.reportNetworkSuccess(AdNetwork.ADMOB, AdFormat.BANNER)
                            }
                        }
                        try {
                            val extras = android.os.Bundle().apply {
                                putString("npa", "1")
                            }
                            val adRequest = AdRequest.Builder()
                                .addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter::class.java, extras)
                                .build()
                            loadAd(adRequest)
                        } catch (e: Throwable) {
                            AdManager.reportNetworkFailure(AdNetwork.ADMOB, AdFormat.BANNER, e.message ?: "load exception")
                            admobBannerFailed = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun FallbackBannerBox(
    network: AdNetwork = AdNetwork.UNITY_ADS,
    admobUnitId: String = MonetizationConstants.ADMOB_BANNER_UNIT_ID,
    unityPlacementId: String = MonetizationConstants.UNITY_BANNER_PLACEMENT_ID,
    unityGameId: String = MonetizationConstants.UNITY_GAME_ID,
    requestTurn: Int = 1,
    isFallback: Boolean = false,
    onRotateNow: () -> Unit = {}
) {
    val isUnity = network == AdNetwork.UNITY_ADS
    val badgeColor = if (isUnity) Color(0xFF00C853) else Color(0xFF2563EB)
    val subtitleText = if (isUnity) {
        "Unity Ads [$unityGameId] • Placement: $unityPlacementId • Turn #$requestTurn"
    } else {
        "Google AdMob Banner • Unit: ${admobUnitId.takeLast(10)} • Turn #$requestTurn"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("fallback_banner_box")
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (isUnity) NeonCyan.copy(alpha = 0.65f) else DarkSurfaceBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onRotateNow() }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Ad Notice",
                tint = if (isUnity) GoldAccent else NeonCyan,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = if (isUnity) {
                        "SPONSORED • Unity Ads ($unityPlacementId)"
                    } else {
                        "SPONSORED: Supercharge Viral Growth"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitleText,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = "Rotate Network",
                tint = NeonCyan,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isUnity) "Unity Ad" else "AdMob",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
