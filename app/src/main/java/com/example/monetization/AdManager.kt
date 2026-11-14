package com.example.monetization

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

enum class AdNetwork(val displayName: String, val badgeLabel: String) {
    ADMOB("Google AdMob", "AdMob"),
    UNITY_ADS("Unity Ads", "Unity Ads")
}

enum class AdFormat {
    BANNER,
    INTERSTITIAL,
    REWARDED
}

enum class UnityRewardedAdStatus(val label: String) {
    IDLE("Ready to Load"),
    LOADING("Loading Unity Ad..."),
    READY("Unity Ad Loaded & Ready"),
    PLAYING("Playing Unity Rewarded Ad"),
    REWARD_GRANTED("Bonus Diamonds Credited!"),
    FALLBACK_ACTIVE("Fallback Rewarded Ready")
}

data class MediatedAdDecision(
    val network: AdNetwork,
    val format: AdFormat,
    val unitOrPlacementId: String,
    val unityGameId: String = MonetizationConstants.UNITY_GAME_ID,
    val requestNumber: Int = 1,
    val isFallback: Boolean = false,
    val statusNote: String = ""
) {
    val selectedNetwork: AdNetwork get() = network
    val reason: String get() = statusNote
}

data class MediationTelemetryState(
    val isAdMobInitialized: Boolean = false,
    val isUnityAdsInitialized: Boolean = false,
    val isAdMobActive: Boolean = true,
    val isUnityAdsActive: Boolean = true,
    val unityGameId: String = MonetizationConstants.UNITY_GAME_ID,
    val unityBannerPlacementId: String = MonetizationConstants.UNITY_BANNER_PLACEMENT_ID,
    val unityInterstitialPlacementId: String = MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID,
    val unityRewardedPlacementId: String = MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID,
    val bannerRequestsServed: Int = 0,
    val interstitialRequestsServed: Int = 0,
    val rewardedRequestsServed: Int = 0,
    val lastBannerNetwork: AdNetwork = AdNetwork.ADMOB,
    val lastInterstitialNetwork: AdNetwork = AdNetwork.ADMOB,
    val lastRewardedNetwork: AdNetwork = AdNetwork.ADMOB,
    val lastMediationLog: String = "Smart AdMob + Unity Ads (800427608) Mediation Ready"
) {
    val admobActive: Boolean get() = isAdMobActive
    val unityActive: Boolean get() = isUnityAdsActive
}

object AdManager {
    private const val TAG = "ViralBoostAdsMediation"

    const val DEFAULT_UNITY_GAME_ID = MonetizationConstants.UNITY_GAME_ID
    const val DEFAULT_UNITY_BANNER_PLACEMENT_ID = MonetizationConstants.UNITY_BANNER_PLACEMENT_ID
    const val DEFAULT_UNITY_INTERSTITIAL_PLACEMENT_ID = MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID
    const val DEFAULT_UNITY_REWARDED_PLACEMENT_ID = MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID
    const val TEST_REWARDED_ID = MonetizationConstants.ADMOB_REWARDED_UNIT_ID

    // Cached AdMob instances
    private var admobInterstitialAd: InterstitialAd? = null
    private var admobRewardedAd: RewardedAd? = null

    // Unity Ads placement readiness states
    private var isUnityInterstitialLoaded = false
    private var isUnityRewardedLoaded = false

    // Initialization & active network flags
    @Volatile
    var isAdMobInitialized: Boolean = false
        private set

    @Volatile
    var isUnityAdsInitialized: Boolean = false
        private set

    @Volatile
    var isAdMobActive: Boolean = true
        private set

    @Volatile
    var isUnityAdsActive: Boolean = true
        private set

    // Dynamic Room DB-backed Unity Ads & AdMob Credentials (survive app restarts via AppConfig)
    @Volatile
    var activeUnityGameId: String = MonetizationConstants.UNITY_GAME_ID
        private set

    @Volatile
    var activeUnityBannerPlacementId: String = MonetizationConstants.UNITY_BANNER_PLACEMENT_ID
        private set

    @Volatile
    var activeUnityInterstitialPlacementId: String = MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID
        private set

    @Volatile
    var activeUnityRewardedPlacementId: String = MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID
        private set

    @Volatile
    var activeAdMobBannerUnitId: String = MonetizationConstants.ADMOB_BANNER_UNIT_ID
        private set

    @Volatile
    var activeAdMobInterstitialUnitId: String = MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID
        private set

    @Volatile
    var activeAdMobRewardedUnitId: String = MonetizationConstants.ADMOB_REWARDED_UNIT_ID
        private set

    val activeBannerAdUnitId: String get() = activeAdMobBannerUnitId
    val activeInterstitialAdUnitId: String get() = activeAdMobInterstitialUnitId
    val activeRewardedAdUnitId: String get() = activeAdMobRewardedUnitId

    // Consecutive error counters for automatic network restriction/failure detection
    private val admobConsecutiveFailures = AtomicInteger(0)
    private val unityConsecutiveFailures = AtomicInteger(0)
    private const val MAX_CONSECUTIVE_FAILURES_BEFORE_BYPASS = 3

    // Round-robin rotation counters (1-indexed: odd -> AdMob, even -> Unity Ads when both active)
    private val bannerRotationCounter = AtomicInteger(0)
    private val interstitialRotationCounter = AtomicInteger(0)
    private val rewardedRotationCounter = AtomicInteger(0)

    private val _isRewardedAdLoading = MutableStateFlow(false)
    val isRewardedAdLoading: StateFlow<Boolean> = _isRewardedAdLoading.asStateFlow()

    private val _unityRewardedStatus = MutableStateFlow(UnityRewardedAdStatus.READY)
    val unityRewardedStatus: StateFlow<UnityRewardedAdStatus> = _unityRewardedStatus.asStateFlow()

    private val _totalUnityRewardedAdsWatched = MutableStateFlow(0)
    val totalUnityRewardedAdsWatched: StateFlow<Int> = _totalUnityRewardedAdsWatched.asStateFlow()

    private val _totalUnityRewardedDiamondsEarned = MutableStateFlow(0)
    val totalUnityRewardedDiamondsEarned: StateFlow<Int> = _totalUnityRewardedDiamondsEarned.asStateFlow()

    private val _mediationState = MutableStateFlow(MediationTelemetryState())
    val mediationState: StateFlow<MediationTelemetryState> = _mediationState.asStateFlow()

    private val _lastInterstitialDecision = MutableStateFlow(
        MediatedAdDecision(
            network = AdNetwork.ADMOB,
            format = AdFormat.INTERSTITIAL,
            unitOrPlacementId = MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID
        )
    )
    val lastInterstitialDecision: StateFlow<MediatedAdDecision> = _lastInterstitialDecision.asStateFlow()

    private val _lastRewardedDecision = MutableStateFlow(
        MediatedAdDecision(
            network = AdNetwork.ADMOB,
            format = AdFormat.REWARDED,
            unitOrPlacementId = MonetizationConstants.ADMOB_REWARDED_UNIT_ID
        )
    )
    val lastRewardedDecision: StateFlow<MediatedAdDecision> = _lastRewardedDecision.asStateFlow()

    private val _lastBannerDecision = MutableStateFlow(
        MediatedAdDecision(
            network = AdNetwork.ADMOB,
            format = AdFormat.BANNER,
            unitOrPlacementId = MonetizationConstants.ADMOB_BANNER_UNIT_ID
        )
    )
    val lastBannerDecision: StateFlow<MediatedAdDecision> = _lastBannerDecision.asStateFlow()

    fun isRunningOnEmulator(): Boolean {
        return (android.os.Build.FINGERPRINT.startsWith("generic")
            || android.os.Build.FINGERPRINT.startsWith("unknown")
            || android.os.Build.MODEL.contains("google_sdk")
            || android.os.Build.MODEL.contains("Emulator")
            || android.os.Build.MODEL.contains("Android SDK built for")
            || android.os.Build.MANUFACTURER.contains("Genymotion")
            || (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
            || android.os.Build.PRODUCT.contains("sdk_gphone")
            || android.os.Build.PRODUCT.contains("google_sdk")
            || android.os.Build.HARDWARE.contains("goldfish")
            || android.os.Build.HARDWARE.contains("ranchu"))
    }

    /**
     * Initializes BOTH Google Mobile Ads SDK (AdMob) and Unity Ads SDK (Game ID: 800427608)
     * on application startup with full fault tolerance.
     */
    fun initialize(
        context: Context,
        unityGameId: String = activeUnityGameId,
        testMode: Boolean = false
    ) {
        val appContext = context.applicationContext ?: context
        val resolvedGameId = unityGameId.trim().ifBlank { activeUnityGameId.ifBlank { MonetizationConstants.UNITY_GAME_ID } }
        activeUnityGameId = resolvedGameId
        initializeAdMobSdk(appContext)
        initializeUnityAdsSdk(appContext, resolvedGameId, testMode)
        syncTelemetry("Initialized AdMob + Unity Ads (Game ID: $resolvedGameId)")
    }

    /**
     * Dynamically syncs Unity Ads & AdMob credentials stored in the local Room Database (AppConfig)
     * so all Banners, Interstitials, and Rewarded Ads immediately pick up owner edits across the app.
     */
    fun syncCredentialsFromConfig(
        config: com.example.data.local.AppConfig,
        context: Context? = null
    ) {
        val newGameId = config.unityGameId.trim().ifBlank { MonetizationConstants.UNITY_GAME_ID }
        val newBannerPlacement = config.unityBannerPlacementId.trim().ifBlank { MonetizationConstants.UNITY_BANNER_PLACEMENT_ID }
        val newInterstitialPlacement = config.unityInterstitialPlacementId.trim().ifBlank { MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID }
        val newRewardedPlacement = config.unityRewardedPlacementId.trim().ifBlank { MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID }

        val gameIdChanged = newGameId != activeUnityGameId
        activeUnityGameId = newGameId
        activeUnityBannerPlacementId = newBannerPlacement
        activeUnityInterstitialPlacementId = newInterstitialPlacement
        activeUnityRewardedPlacementId = newRewardedPlacement

        activeAdMobBannerUnitId = config.admobBannerUnitId.trim().ifBlank { MonetizationConstants.ADMOB_BANNER_UNIT_ID }
        activeAdMobInterstitialUnitId = config.admobInterstitialUnitId.trim().ifBlank { MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID }
        activeAdMobRewardedUnitId = config.admobRewardedUnitId.trim().ifBlank { MonetizationConstants.ADMOB_REWARDED_UNIT_ID }

        if (context != null) {
            if (gameIdChanged) {
                isUnityAdsInitialized = false
                initializeUnityAdsSdk(context.applicationContext ?: context, activeUnityGameId, false)
            }
            loadInterstitial(
                context = context,
                unitId = activeAdMobInterstitialUnitId,
                unityPlacementId = activeUnityInterstitialPlacementId
            )
            loadRewarded(
                context = context,
                unitId = activeAdMobRewardedUnitId,
                unityPlacementId = activeUnityRewardedPlacementId
            )
        }
        syncTelemetry(
            "Room DB Synced: Unity Game ID=$activeUnityGameId | B=$activeUnityBannerPlacementId | I=$activeUnityInterstitialPlacementId | R=$activeUnityRewardedPlacementId"
        )
    }

    private fun initializeAdMobSdk(context: Context) {
        if (isAdMobInitialized) return
        if (isRunningOnEmulator()) {
            Log.d(TAG, "AdMob Emulator Mode: safe zero-error mediation active")
            isAdMobInitialized = true
            isAdMobActive = true
            syncTelemetry("AdMob Initialized (Safe/Emulator Mode)")
            return
        }
        try {
            val requestConfig = com.google.android.gms.ads.RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            MobileAds.initialize(context) { initializationStatus ->
                Log.d(TAG, "AdMob Initialized: $initializationStatus")
                isAdMobInitialized = true
                isAdMobActive = true
                admobConsecutiveFailures.set(0)
                syncTelemetry("Google AdMob SDK Initialized & Active")
                loadAdMobInterstitial(context)
                loadAdMobRewarded(context)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "AdMob initialization failed gracefully, routing to Unity Ads fallback: ${e.message}")
            isAdMobInitialized = false
            isAdMobActive = false
            syncTelemetry("AdMob Init Failed -> Unity Ads (800427608) Exclusive Fallback Active")
        }
    }

    private fun initializeUnityAdsSdk(
        context: Context,
        gameId: String = MonetizationConstants.UNITY_GAME_ID,
        testMode: Boolean = false
    ) {
        if (isUnityAdsInitialized) return
        val cleanGameId = gameId.trim().ifBlank { MonetizationConstants.UNITY_GAME_ID }

        if (isRunningOnEmulator()) {
            Log.d(TAG, "Unity Ads Emulator Mode: Game ID $cleanGameId initialized in safe mediation mode")
            isUnityAdsInitialized = true
            isUnityAdsActive = true
            isUnityInterstitialLoaded = true
            isUnityRewardedLoaded = true
            syncTelemetry("Unity Ads ($cleanGameId) Initialized & Active")
            return
        }

        try {
            if (UnityAds.isInitialized) {
                isUnityAdsInitialized = true
                isUnityAdsActive = true
                unityConsecutiveFailures.set(0)
                loadUnityInterstitial(cleanGameId)
                loadUnityRewarded(cleanGameId)
                syncTelemetry("Unity Ads ($cleanGameId) Already Initialized & Active")
                return
            }

            UnityAds.initialize(
                context,
                cleanGameId,
                testMode,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        Log.d(TAG, "Unity Ads Initialized Successfully (Game ID: $cleanGameId)")
                        isUnityAdsInitialized = true
                        isUnityAdsActive = true
                        unityConsecutiveFailures.set(0)
                        syncTelemetry("Unity Ads ($cleanGameId) Initialized & Active")
                        loadUnityInterstitial()
                        loadUnityRewarded()
                    }

                    override fun onInitializationFailed(
                        error: UnityAds.UnityAdsInitializationError?,
                        message: String?
                    ) {
                        Log.w(TAG, "Unity Ads Initialization Failed [$error]: $message — AdMob Fallback Active")
                        isUnityAdsInitialized = false
                        isUnityAdsActive = false
                        syncTelemetry("Unity Ads Init Restricted ($error) -> AdMob Exclusive Fallback Active")
                    }
                }
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Unity Ads initialization exception handled safely: ${e.message}")
            // Keep mediation alive via AdMob or safe fallback
            isUnityAdsInitialized = false
            isUnityAdsActive = false
            syncTelemetry("Unity Ads Exception Handled -> AdMob Fallback Active")
        }
    }

    /**
     * Allows dynamic runtime/admin/test configuration of network active states.
     * If one network is marked inactive, all requests automatically bypass it and serve exclusively from the active network.
     */
    fun setNetworkActiveState(
        adMobActive: Boolean = isAdMobActive,
        unityAdsActive: Boolean = isUnityAdsActive
    ) {
        isAdMobActive = adMobActive
        isUnityAdsActive = unityAdsActive
        if (adMobActive) admobConsecutiveFailures.set(0)
        if (unityAdsActive) unityConsecutiveFailures.set(0)
        syncTelemetry(
            buildString {
                append("Network State Updated: AdMob=")
                append(if (adMobActive) "ACTIVE" else "INACTIVE")
                append(", UnityAds($activeUnityGameId)=")
                append(if (unityAdsActive) "ACTIVE" else "INACTIVE")
            }
        )
    }

    fun resetMediationCounters() {
        bannerRotationCounter.set(0)
        interstitialRotationCounter.set(0)
        rewardedRotationCounter.set(0)
        admobConsecutiveFailures.set(0)
        unityConsecutiveFailures.set(0)
        isAdMobActive = true
        isUnityAdsActive = true
        syncTelemetry("Mediation rotation counters & network health reset")
    }

    /**
     * Reports a network success so its consecutive failure counter is cleared and it stays active.
     */
    fun reportNetworkSuccess(network: AdNetwork, format: AdFormat) {
        when (network) {
            AdNetwork.ADMOB -> {
                admobConsecutiveFailures.set(0)
                isAdMobActive = true
            }
            AdNetwork.UNITY_ADS -> {
                unityConsecutiveFailures.set(0)
                isUnityAdsActive = true
            }
        }
        syncTelemetry("${network.displayName} served $format ad successfully")
    }

    /**
     * Reports a network load/show failure. If consecutive failures reach threshold, marks that network
     * temporarily inactive so subsequent requests automatically bypass it and use the healthy network.
     */
    fun reportNetworkFailure(network: AdNetwork, format: AdFormat, errorReason: String = "") {
        when (network) {
            AdNetwork.ADMOB -> {
                val failures = admobConsecutiveFailures.incrementAndGet()
                if (failures >= MAX_CONSECUTIVE_FAILURES_BEFORE_BYPASS && isUnityAdsActive) {
                    isAdMobActive = false
                    Log.w(TAG, "AdMob marked inactive after $failures failures; bypassing to Unity Ads")
                }
            }
            AdNetwork.UNITY_ADS -> {
                val failures = unityConsecutiveFailures.incrementAndGet()
                if (failures >= MAX_CONSECUTIVE_FAILURES_BEFORE_BYPASS && isAdMobActive) {
                    isUnityAdsActive = false
                    Log.w(TAG, "Unity Ads marked inactive after $failures failures; bypassing to AdMob")
                }
            }
        }
        syncTelemetry("${network.displayName} $format failure ($errorReason) -> Fallback engaged")
    }

    /**
     * Core Smart Mediation Decision Engine:
     * - If BOTH AdMob and Unity Ads are active: alternates 50/50 (Request 1 = AdMob, Request 2 = Unity Ads, Request 3 = AdMob, etc.).
     * - If ONLY ONE network is active: bypasses the inactive network and serves 100% from the active one.
     * - If both are marked inactive (e.g., transient offline state): re-enables Unity Ads / AdMob rotation for permanent fallback visibility.
     */
    fun selectNextMediatedAd(
        format: AdFormat,
        admobUnitId: String = defaultAdMobUnitForFormat(format),
        unityPlacementId: String = defaultUnityPlacementForFormat(format)
    ): MediatedAdDecision {
        val counter = when (format) {
            AdFormat.BANNER -> bannerRotationCounter.incrementAndGet()
            AdFormat.INTERSTITIAL -> interstitialRotationCounter.incrementAndGet()
            AdFormat.REWARDED -> rewardedRotationCounter.incrementAndGet()
        }

        val cleanAdMobUnit = admobUnitId.trim().ifBlank { defaultAdMobUnitForFormat(format) }
        val cleanUnityPlacement = unityPlacementId.trim().ifBlank { defaultUnityPlacementForFormat(format) }

        val chosenNetwork: AdNetwork
        val isFallback: Boolean
        val note: String

        when {
            isAdMobActive && isUnityAdsActive -> {
                // Both networks active -> strict 1:1 fair rotation (Odd = AdMob, Even = Unity Ads)
                chosenNetwork = if (counter % 2 == 1) AdNetwork.ADMOB else AdNetwork.UNITY_ADS
                isFallback = false
                note = "Smart Rotation Turn #$counter (${chosenNetwork.displayName})"
            }
            !isAdMobActive && isUnityAdsActive -> {
                // AdMob inactive/restricted -> Bypass AdMob and serve Unity Ads exclusively
                chosenNetwork = AdNetwork.UNITY_ADS
                isFallback = true
                note = "AdMob Bypassed -> Exclusive Unity Ads ($activeUnityGameId)"
            }
            isAdMobActive && !isUnityAdsActive -> {
                // Unity Ads inactive/restricted -> Bypass Unity Ads and serve AdMob exclusively
                chosenNetwork = AdNetwork.ADMOB
                isFallback = true
                note = "Unity Ads Bypassed -> Exclusive Google AdMob"
            }
            else -> {
                // Both had errors (e.g., offline): still rotate between Unity Ads & AdMob for permanent fallback UI
                chosenNetwork = if (counter % 2 == 1) AdNetwork.UNITY_ADS else AdNetwork.ADMOB
                isFallback = true
                note = "Offline/Safe Resilience Mode (${chosenNetwork.displayName})"
            }
        }

        val unitOrPlacement = if (chosenNetwork == AdNetwork.ADMOB) cleanAdMobUnit else cleanUnityPlacement
        val decision = MediatedAdDecision(
            network = chosenNetwork,
            format = format,
            unitOrPlacementId = unitOrPlacement,
            unityGameId = activeUnityGameId,
            requestNumber = counter,
            isFallback = isFallback,
            statusNote = note
        )

        when (format) {
            AdFormat.BANNER -> _lastBannerDecision.value = decision
            AdFormat.INTERSTITIAL -> _lastInterstitialDecision.value = decision
            AdFormat.REWARDED -> _lastRewardedDecision.value = decision
        }
        syncTelemetry(note)
        return decision
    }

    /**
     * Returns the immediate fallback decision for the opposite network if the primary network fails mid-request.
     */
    fun getFallbackDecisionFor(
        failedDecision: MediatedAdDecision,
        admobUnitId: String = defaultAdMobUnitForFormat(failedDecision.format),
        unityPlacementId: String = defaultUnityPlacementForFormat(failedDecision.format)
    ): MediatedAdDecision {
        val fallbackNetwork = if (failedDecision.network == AdNetwork.ADMOB) {
            AdNetwork.UNITY_ADS
        } else {
            AdNetwork.ADMOB
        }
        val cleanAdMobUnit = admobUnitId.trim().ifBlank { defaultAdMobUnitForFormat(failedDecision.format) }
        val cleanUnityPlacement = unityPlacementId.trim().ifBlank { defaultUnityPlacementForFormat(failedDecision.format) }
        val unitOrPlacement = if (fallbackNetwork == AdNetwork.ADMOB) cleanAdMobUnit else cleanUnityPlacement

        val fallbackDecision = failedDecision.copy(
            network = fallbackNetwork,
            unitOrPlacementId = unitOrPlacement,
            isFallback = true,
            statusNote = "Auto-Fallback to ${fallbackNetwork.displayName} ($unitOrPlacement)"
        )
        when (failedDecision.format) {
            AdFormat.BANNER -> _lastBannerDecision.value = fallbackDecision
            AdFormat.INTERSTITIAL -> _lastInterstitialDecision.value = fallbackDecision
            AdFormat.REWARDED -> _lastRewardedDecision.value = fallbackDecision
        }
        syncTelemetry(fallbackDecision.statusNote)
        return fallbackDecision
    }

    private fun defaultAdMobUnitForFormat(format: AdFormat): String = when (format) {
        AdFormat.BANNER -> activeAdMobBannerUnitId.ifBlank { MonetizationConstants.ADMOB_BANNER_UNIT_ID }
        AdFormat.INTERSTITIAL -> activeAdMobInterstitialUnitId.ifBlank { MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID }
        AdFormat.REWARDED -> activeAdMobRewardedUnitId.ifBlank { MonetizationConstants.ADMOB_REWARDED_UNIT_ID }
    }

    private fun defaultUnityPlacementForFormat(format: AdFormat): String = when (format) {
        AdFormat.BANNER -> activeUnityBannerPlacementId.ifBlank { MonetizationConstants.UNITY_BANNER_PLACEMENT_ID }
        AdFormat.INTERSTITIAL -> activeUnityInterstitialPlacementId.ifBlank { MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID }
        AdFormat.REWARDED -> activeUnityRewardedPlacementId.ifBlank { MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID }
    }

    private fun createAdRequest(): AdRequest {
        val extras = android.os.Bundle().apply {
            putString("npa", "1")
        }
        return AdRequest.Builder()
            .addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter::class.java, extras)
            .build()
    }

    // =========================================================================
    // INTERSTITIAL AD LOADING (AdMob + Unity Ads "BP_Interstitial_Android")
    // =========================================================================

    fun loadInterstitial(
        context: Context,
        unitId: String = activeAdMobInterstitialUnitId,
        unityPlacementId: String = activeUnityInterstitialPlacementId
    ) {
        loadAdMobInterstitial(context, unitId)
        loadUnityInterstitial(unityPlacementId)
    }

    private fun loadAdMobInterstitial(
        context: Context,
        unitId: String = activeAdMobInterstitialUnitId
    ) {
        if (isRunningOnEmulator() || admobInterstitialAd != null) return
        try {
            val adRequest = createAdRequest()
            InterstitialAd.load(
                context,
                unitId.ifBlank { activeAdMobInterstitialUnitId.ifBlank { MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID } },
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        admobInterstitialAd = ad
                        reportNetworkSuccess(AdNetwork.ADMOB, AdFormat.INTERSTITIAL)
                        Log.d(TAG, "AdMob Interstitial loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        admobInterstitialAd = null
                        reportNetworkFailure(AdNetwork.ADMOB, AdFormat.INTERSTITIAL, error.message)
                        Log.w(TAG, "AdMob Interstitial failed to load: ${error.message}")
                    }
                }
            )
        } catch (e: Throwable) {
            Log.w(TAG, "AdMob interstitial load exception: ${e.message}")
        }
    }

    fun loadUnityInterstitial(
        placementId: String = activeUnityInterstitialPlacementId
    ) {
        val cleanPlacement = placementId.trim().ifBlank { activeUnityInterstitialPlacementId.ifBlank { MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID } }
        if (isRunningOnEmulator()) {
            isUnityInterstitialLoaded = true
            return
        }
        try {
            if (!UnityAds.isInitialized) return
            UnityAds.load(
                cleanPlacement,
                object : IUnityAdsLoadListener {
                    override fun onUnityAdsAdLoaded(placementId: String?) {
                        isUnityInterstitialLoaded = true
                        reportNetworkSuccess(AdNetwork.UNITY_ADS, AdFormat.INTERSTITIAL)
                        Log.d(TAG, "Unity Ads Interstitial loaded: $placementId")
                    }

                    override fun onUnityAdsFailedToLoad(
                        placementId: String?,
                        error: UnityAds.UnityAdsLoadError?,
                        message: String?
                    ) {
                        isUnityInterstitialLoaded = false
                        reportNetworkFailure(AdNetwork.UNITY_ADS, AdFormat.INTERSTITIAL, "$error: $message")
                        Log.w(TAG, "Unity Ads Interstitial failed to load [$placementId]: $error - $message")
                    }
                }
            )
        } catch (e: Throwable) {
            Log.w(TAG, "Unity Ads interstitial load exception: ${e.message}")
        }
    }

    // =========================================================================
    // SMART MEDIATED INTERSTITIAL DISPLAY (Rotates AdMob <-> Unity Ads + Fallback)
    // =========================================================================

    fun triggerMilestoneInterstitial(
        context: Context,
        isVip: Boolean = false,
        milestoneName: String,
        interstitialUnitId: String = activeAdMobInterstitialUnitId,
        unityPlacementId: String = activeUnityInterstitialPlacementId,
        onShowInterstitialFallback: (onDismiss: () -> Unit) -> Unit = { it() },
        onComplete: () -> Unit = {}
    ) {
        if (isVip) {
            onComplete()
            return
        }

        // Ensure both SDKs are initialized if not yet called
        if (!isAdMobInitialized && !isUnityAdsInitialized) {
            initialize(context)
        }

        val decision = selectNextMediatedAd(
            format = AdFormat.INTERSTITIAL,
            admobUnitId = interstitialUnitId,
            unityPlacementId = unityPlacementId
        )
        val activity = context.findActivity()
        Log.d(
            TAG,
            "Mediated Interstitial Triggered [$milestoneName] -> #${decision.requestNumber} via ${decision.network.displayName} (${decision.unitOrPlacementId})"
        )

        if (isRunningOnEmulator() || activity == null) {
            onShowInterstitialFallback {
                onComplete()
            }
            return
        }

        when (decision.network) {
            AdNetwork.ADMOB -> {
                showAdMobInterstitialWithUnityFallback(
                    activity = activity,
                    admobUnitId = interstitialUnitId,
                    unityPlacementId = unityPlacementId,
                    primaryDecision = decision,
                    onShowInterstitialFallback = onShowInterstitialFallback,
                    onComplete = onComplete
                )
            }
            AdNetwork.UNITY_ADS -> {
                showUnityInterstitialWithAdMobFallback(
                    activity = activity,
                    unityPlacementId = unityPlacementId,
                    admobUnitId = interstitialUnitId,
                    primaryDecision = decision,
                    onShowInterstitialFallback = onShowInterstitialFallback,
                    onComplete = onComplete
                )
            }
        }
    }

    private fun showAdMobInterstitialWithUnityFallback(
        activity: Activity,
        admobUnitId: String,
        unityPlacementId: String,
        primaryDecision: MediatedAdDecision,
        onShowInterstitialFallback: (onDismiss: () -> Unit) -> Unit,
        onComplete: () -> Unit
    ) {
        val ad = admobInterstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    admobInterstitialAd = null
                    reportNetworkSuccess(AdNetwork.ADMOB, AdFormat.INTERSTITIAL)
                    onComplete()
                    loadAdMobInterstitial(activity, admobUnitId)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    admobInterstitialAd = null
                    reportNetworkFailure(AdNetwork.ADMOB, AdFormat.INTERSTITIAL, error.message)
                    loadAdMobInterstitial(activity, admobUnitId)
                    // Automatic Fallback to Unity Ads ("BP_Interstitial_Android")
                    getFallbackDecisionFor(primaryDecision, admobUnitId, unityPlacementId)
                    tryShowUnityInterstitialDirect(
                        activity = activity,
                        unityPlacementId = unityPlacementId,
                        onFailed = {
                            onShowInterstitialFallback {
                                onComplete()
                            }
                        },
                        onComplete = onComplete
                    )
                }
            }
            try {
                ad.show(activity)
            } catch (e: Throwable) {
                reportNetworkFailure(AdNetwork.ADMOB, AdFormat.INTERSTITIAL, e.message ?: "show error")
                getFallbackDecisionFor(primaryDecision, admobUnitId, unityPlacementId)
                tryShowUnityInterstitialDirect(
                    activity = activity,
                    unityPlacementId = unityPlacementId,
                    onFailed = { onShowInterstitialFallback { onComplete() } },
                    onComplete = onComplete
                )
            }
        } else {
            loadAdMobInterstitial(activity, admobUnitId)
            // AdMob not ready -> Immediately try Unity Ads ("BP_Interstitial_Android") before UI fallback
            if (isUnityAdsActive && isUnityInterstitialLoaded) {
                getFallbackDecisionFor(primaryDecision, admobUnitId, unityPlacementId)
                tryShowUnityInterstitialDirect(
                    activity = activity,
                    unityPlacementId = unityPlacementId,
                    onFailed = { onShowInterstitialFallback { onComplete() } },
                    onComplete = onComplete
                )
            } else {
                loadUnityInterstitial(unityPlacementId)
                onShowInterstitialFallback {
                    onComplete()
                }
            }
        }
    }

    private fun showUnityInterstitialWithAdMobFallback(
        activity: Activity,
        unityPlacementId: String,
        admobUnitId: String,
        primaryDecision: MediatedAdDecision,
        onShowInterstitialFallback: (onDismiss: () -> Unit) -> Unit,
        onComplete: () -> Unit
    ) {
        tryShowUnityInterstitialDirect(
            activity = activity,
            unityPlacementId = unityPlacementId,
            onFailed = {
                // Automatic Fallback to Google AdMob Interstitial
                val fallbackAdMob = admobInterstitialAd
                if (isAdMobActive && fallbackAdMob != null) {
                    getFallbackDecisionFor(primaryDecision, admobUnitId, unityPlacementId)
                    fallbackAdMob.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            admobInterstitialAd = null
                            reportNetworkSuccess(AdNetwork.ADMOB, AdFormat.INTERSTITIAL)
                            onComplete()
                            loadAdMobInterstitial(activity, admobUnitId)
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            admobInterstitialAd = null
                            loadAdMobInterstitial(activity, admobUnitId)
                            onShowInterstitialFallback { onComplete() }
                        }
                    }
                    try {
                        fallbackAdMob.show(activity)
                    } catch (_: Throwable) {
                        onShowInterstitialFallback { onComplete() }
                    }
                } else {
                    loadAdMobInterstitial(activity, admobUnitId)
                    onShowInterstitialFallback { onComplete() }
                }
            },
            onComplete = onComplete
        )
    }

    private fun tryShowUnityInterstitialDirect(
        activity: Activity,
        unityPlacementId: String,
        onFailed: () -> Unit,
        onComplete: () -> Unit
    ) {
        val cleanPlacement = unityPlacementId.trim().ifBlank { activeUnityInterstitialPlacementId.ifBlank { MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID } }
        try {
            if (!UnityAds.isInitialized) {
                initializeUnityAdsSdk(activity.applicationContext, activeUnityGameId, false)
                onFailed()
                return
            }
            UnityAds.show(
                activity,
                cleanPlacement,
                UnityAdsShowOptions(),
                object : IUnityAdsShowListener {
                    override fun onUnityAdsShowStart(placementId: String?) {
                        isUnityInterstitialLoaded = false
                        reportNetworkSuccess(AdNetwork.UNITY_ADS, AdFormat.INTERSTITIAL)
                    }

                    override fun onUnityAdsShowClick(placementId: String?) {}

                    override fun onUnityAdsShowComplete(
                        placementId: String?,
                        state: UnityAds.UnityAdsShowCompletionState?
                    ) {
                        isUnityInterstitialLoaded = false
                        loadUnityInterstitial(cleanPlacement)
                        onComplete()
                    }

                    override fun onUnityAdsShowFailure(
                        placementId: String?,
                        error: UnityAds.UnityAdsShowError?,
                        message: String?
                    ) {
                        isUnityInterstitialLoaded = false
                        reportNetworkFailure(AdNetwork.UNITY_ADS, AdFormat.INTERSTITIAL, "$error: $message")
                        loadUnityInterstitial(cleanPlacement)
                        onFailed()
                    }
                }
            )
        } catch (e: Throwable) {
            Log.w(TAG, "Unity Ads interstitial show exception: ${e.message}")
            onFailed()
        }
    }

    fun triggerNavigationInterstitial(
        activity: Activity,
        isVip: Boolean,
        interstitialUnitId: String = activeAdMobInterstitialUnitId,
        unityPlacementId: String = activeUnityInterstitialPlacementId,
        onShowInterstitialFallback: (onDismiss: () -> Unit) -> Unit = { it() },
        onNavigate: () -> Unit
    ) {
        triggerMilestoneInterstitial(
            context = activity,
            isVip = isVip,
            milestoneName = "Tab Navigation",
            interstitialUnitId = interstitialUnitId,
            unityPlacementId = unityPlacementId,
            onShowInterstitialFallback = onShowInterstitialFallback,
            onComplete = onNavigate
        )
    }

    fun onTabSwitch(
        activity: Activity,
        isVip: Boolean,
        interstitialUnitId: String = activeAdMobInterstitialUnitId
    ) {
        triggerNavigationInterstitial(activity, isVip, interstitialUnitId) {}
    }

    // =========================================================================
    // REWARDED AD LOADING & SMART MEDIATION (AdMob <-> Unity Ads "BP_Rewarded_Android")
    // =========================================================================

    fun loadRewarded(
        context: Context,
        unitId: String = activeAdMobRewardedUnitId,
        unityPlacementId: String = activeUnityRewardedPlacementId
    ) {
        loadAdMobRewarded(context, unitId)
        loadUnityRewarded(unityPlacementId)
    }

    private fun loadAdMobRewarded(
        context: Context,
        unitId: String = activeAdMobRewardedUnitId
    ) {
        if (isRunningOnEmulator() || _isRewardedAdLoading.value || admobRewardedAd != null) return
        _isRewardedAdLoading.value = true
        try {
            val adRequest = createAdRequest()
            RewardedAd.load(
                context,
                unitId.ifBlank { activeAdMobRewardedUnitId.ifBlank { MonetizationConstants.ADMOB_REWARDED_UNIT_ID } },
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        admobRewardedAd = ad
                        _isRewardedAdLoading.value = false
                        reportNetworkSuccess(AdNetwork.ADMOB, AdFormat.REWARDED)
                        Log.d(TAG, "AdMob Rewarded ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        admobRewardedAd = null
                        _isRewardedAdLoading.value = false
                        reportNetworkFailure(AdNetwork.ADMOB, AdFormat.REWARDED, error.message)
                        Log.w(TAG, "AdMob Rewarded ad failed: ${error.message}")
                    }
                }
            )
        } catch (e: Throwable) {
            _isRewardedAdLoading.value = false
            Log.w(TAG, "AdMob Rewarded ad load exception: ${e.message}")
        }
    }

    fun loadUnityRewarded(
        placementId: String = activeUnityRewardedPlacementId
    ) {
        val cleanPlacement = placementId.trim().ifBlank { activeUnityRewardedPlacementId.ifBlank { MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID } }
        if (isRunningOnEmulator()) {
            isUnityRewardedLoaded = true
            return
        }
        try {
            if (!UnityAds.isInitialized) return
            UnityAds.load(
                cleanPlacement,
                object : IUnityAdsLoadListener {
                    override fun onUnityAdsAdLoaded(placementId: String?) {
                        isUnityRewardedLoaded = true
                        reportNetworkSuccess(AdNetwork.UNITY_ADS, AdFormat.REWARDED)
                        Log.d(TAG, "Unity Ads Rewarded loaded: $placementId")
                    }

                    override fun onUnityAdsFailedToLoad(
                        placementId: String?,
                        error: UnityAds.UnityAdsLoadError?,
                        message: String?
                    ) {
                        isUnityRewardedLoaded = false
                        reportNetworkFailure(AdNetwork.UNITY_ADS, AdFormat.REWARDED, "$error: $message")
                        Log.w(TAG, "Unity Ads Rewarded failed to load [$placementId]: $error - $message")
                    }
                }
            )
        } catch (e: Throwable) {
            Log.w(TAG, "Unity Ads rewarded load exception: ${e.message}")
        }
    }

    /**
     * Smart Mediated Rewarded Ad Display:
     * - Rotates between AdMob (Request 1) and Unity Ads (Request 2) when both are active.
     * - Automatically bypasses an inactive network and falls back to the active network if one fails.
     * - Guarantees user reward delivery via fallback simulation when offline or on emulator.
     */
    fun showRewardedAd(
        activity: Activity,
        rewardedUnitId: String = activeAdMobRewardedUnitId,
        unityPlacementId: String = activeUnityRewardedPlacementId,
        onUserEarnedReward: () -> Unit,
        onFallbackSimulation: () -> Unit
    ) {
        if (!isAdMobInitialized && !isUnityAdsInitialized) {
            initialize(activity)
        }

        val decision = selectNextMediatedAd(
            format = AdFormat.REWARDED,
            admobUnitId = rewardedUnitId,
            unityPlacementId = unityPlacementId
        )
        Log.d(
            TAG,
            "Mediated Rewarded Ad Requested -> #${decision.requestNumber} via ${decision.network.displayName} (${decision.unitOrPlacementId})"
        )

        if (isRunningOnEmulator()) {
            onFallbackSimulation()
            return
        }

        when (decision.network) {
            AdNetwork.ADMOB -> {
                showAdMobRewardedWithUnityFallback(
                    activity = activity,
                    rewardedUnitId = rewardedUnitId,
                    unityPlacementId = unityPlacementId,
                    primaryDecision = decision,
                    onUserEarnedReward = onUserEarnedReward,
                    onFallbackSimulation = onFallbackSimulation
                )
            }
            AdNetwork.UNITY_ADS -> {
                showUnityRewardedWithAdMobFallback(
                    activity = activity,
                    unityPlacementId = unityPlacementId,
                    rewardedUnitId = rewardedUnitId,
                    primaryDecision = decision,
                    onUserEarnedReward = onUserEarnedReward,
                    onFallbackSimulation = onFallbackSimulation
                )
            }
        }
    }

    private fun showAdMobRewardedWithUnityFallback(
        activity: Activity,
        rewardedUnitId: String,
        unityPlacementId: String,
        primaryDecision: MediatedAdDecision,
        onUserEarnedReward: () -> Unit,
        onFallbackSimulation: () -> Unit
    ) {
        val currentAd = admobRewardedAd
        if (currentAd != null) {
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    admobRewardedAd = null
                    loadAdMobRewarded(activity, rewardedUnitId)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    admobRewardedAd = null
                    reportNetworkFailure(AdNetwork.ADMOB, AdFormat.REWARDED, error.message)
                    loadAdMobRewarded(activity, rewardedUnitId)
                    // Immediate fallback to Unity Ads ("BP_Rewarded_Android")
                    getFallbackDecisionFor(primaryDecision, rewardedUnitId, unityPlacementId)
                    tryShowUnityRewardedDirect(
                        activity = activity,
                        unityPlacementId = unityPlacementId,
                        onUserEarnedReward = onUserEarnedReward,
                        onFailed = onFallbackSimulation
                    )
                }
            }
            try {
                currentAd.show(activity) { _ ->
                    reportNetworkSuccess(AdNetwork.ADMOB, AdFormat.REWARDED)
                    onUserEarnedReward()
                }
            } catch (e: Throwable) {
                reportNetworkFailure(AdNetwork.ADMOB, AdFormat.REWARDED, e.message ?: "show error")
                getFallbackDecisionFor(primaryDecision, rewardedUnitId, unityPlacementId)
                tryShowUnityRewardedDirect(
                    activity = activity,
                    unityPlacementId = unityPlacementId,
                    onUserEarnedReward = onUserEarnedReward,
                    onFailed = onFallbackSimulation
                )
            }
        } else {
            loadAdMobRewarded(activity, rewardedUnitId)
            // AdMob not ready -> Immediately try Unity Ads ("BP_Rewarded_Android") before simulation fallback
            if (isUnityAdsActive && isUnityRewardedLoaded) {
                getFallbackDecisionFor(primaryDecision, rewardedUnitId, unityPlacementId)
                tryShowUnityRewardedDirect(
                    activity = activity,
                    unityPlacementId = unityPlacementId,
                    onUserEarnedReward = onUserEarnedReward,
                    onFailed = onFallbackSimulation
                )
            } else {
                loadUnityRewarded(unityPlacementId)
                onFallbackSimulation()
            }
        }
    }

    private fun showUnityRewardedWithAdMobFallback(
        activity: Activity,
        unityPlacementId: String,
        rewardedUnitId: String,
        primaryDecision: MediatedAdDecision,
        onUserEarnedReward: () -> Unit,
        onFallbackSimulation: () -> Unit
    ) {
        tryShowUnityRewardedDirect(
            activity = activity,
            unityPlacementId = unityPlacementId,
            onUserEarnedReward = onUserEarnedReward,
            onFailed = {
                // Automatic Fallback to Google AdMob Rewarded Ad
                val fallbackAdMob = admobRewardedAd
                if (isAdMobActive && fallbackAdMob != null) {
                    getFallbackDecisionFor(primaryDecision, rewardedUnitId, unityPlacementId)
                    fallbackAdMob.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            admobRewardedAd = null
                            loadAdMobRewarded(activity, rewardedUnitId)
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            admobRewardedAd = null
                            loadAdMobRewarded(activity, rewardedUnitId)
                            onFallbackSimulation()
                        }
                    }
                    try {
                        fallbackAdMob.show(activity) { _ ->
                            reportNetworkSuccess(AdNetwork.ADMOB, AdFormat.REWARDED)
                            onUserEarnedReward()
                        }
                    } catch (_: Throwable) {
                        onFallbackSimulation()
                    }
                } else {
                    loadAdMobRewarded(activity, rewardedUnitId)
                    onFallbackSimulation()
                }
            }
        )
    }

    private fun tryShowUnityRewardedDirect(
        activity: Activity,
        unityPlacementId: String,
        onUserEarnedReward: () -> Unit,
        onFailed: () -> Unit
    ) {
        val cleanPlacement = unityPlacementId.trim().ifBlank { activeUnityRewardedPlacementId.ifBlank { MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID } }
        try {
            if (!UnityAds.isInitialized) {
                initializeUnityAdsSdk(activity.applicationContext, activeUnityGameId, false)
                onFailed()
                return
            }
            UnityAds.show(
                activity,
                cleanPlacement,
                UnityAdsShowOptions(),
                object : IUnityAdsShowListener {
                    override fun onUnityAdsShowStart(placementId: String?) {
                        isUnityRewardedLoaded = false
                        _unityRewardedStatus.value = UnityRewardedAdStatus.PLAYING
                        reportNetworkSuccess(AdNetwork.UNITY_ADS, AdFormat.REWARDED)
                    }

                    override fun onUnityAdsShowClick(placementId: String?) {}

                    override fun onUnityAdsShowComplete(
                        placementId: String?,
                        state: UnityAds.UnityAdsShowCompletionState?
                    ) {
                        isUnityRewardedLoaded = false
                        loadUnityRewarded(cleanPlacement)
                        if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED || state == null) {
                            _unityRewardedStatus.value = UnityRewardedAdStatus.REWARD_GRANTED
                            onUserEarnedReward()
                        } else {
                            _unityRewardedStatus.value = UnityRewardedAdStatus.READY
                        }
                    }

                    override fun onUnityAdsShowFailure(
                        placementId: String?,
                        error: UnityAds.UnityAdsShowError?,
                        message: String?
                    ) {
                        isUnityRewardedLoaded = false
                        _unityRewardedStatus.value = UnityRewardedAdStatus.FALLBACK_ACTIVE
                        reportNetworkFailure(AdNetwork.UNITY_ADS, AdFormat.REWARDED, "$error: $message")
                        loadUnityRewarded(cleanPlacement)
                        onFailed()
                    }
                }
            )
        } catch (e: Throwable) {
            Log.w(TAG, "Unity Ads rewarded show exception: ${e.message}")
            onFailed()
        }
    }

    /**
     * Dedicated Unity Ads SDK Rewarded Ad Flow (`UnityAds.load` + `UnityAds.show` with
     * `IUnityAdsLoadListener` and `IUnityAdsShowListener`) that specifically requests
     * Unity placement `BP_Rewarded_Android` (Game ID `800427608`) and grants the user
     * bonus diamonds when completed, with seamless AdMob / interactive fallback.
     */
    fun showUnityRewardedAd(
        activity: Activity,
        unityPlacementId: String = activeUnityRewardedPlacementId,
        rewardedUnitId: String = activeAdMobRewardedUnitId,
        bonusDiamonds: Int = MonetizationConstants.REWARDED_AD_DIAMOND_REWARD,
        onUserEarnedReward: () -> Unit,
        onFallbackSimulation: () -> Unit
    ) {
        if (!isUnityAdsInitialized && !isAdMobInitialized) {
            initialize(activity)
        }

        val cleanPlacement = unityPlacementId.trim().ifBlank {
            activeUnityRewardedPlacementId.ifBlank { MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID }
        }
        val reqNum = rewardedRotationCounter.incrementAndGet()
        val unityDecision = MediatedAdDecision(
            network = AdNetwork.UNITY_ADS,
            format = AdFormat.REWARDED,
            unitOrPlacementId = cleanPlacement,
            unityGameId = activeUnityGameId,
            requestNumber = reqNum,
            isFallback = false,
            statusNote = "Unity Ads SDK Rewarded ($cleanPlacement • Game ID: $activeUnityGameId)"
        )
        _lastRewardedDecision.value = unityDecision
        syncTelemetry(unityDecision.statusNote)

        if (isRunningOnEmulator()) {
            _unityRewardedStatus.value = UnityRewardedAdStatus.PLAYING
            onFallbackSimulation()
            return
        }

        showUnityRewardedWithAdMobFallback(
            activity = activity,
            unityPlacementId = cleanPlacement,
            rewardedUnitId = rewardedUnitId,
            primaryDecision = unityDecision,
            onUserEarnedReward = {
                recordUnityRewardedDiamondGrant(bonusDiamonds)
                onUserEarnedReward()
            },
            onFallbackSimulation = onFallbackSimulation
        )
    }

    fun recordUnityRewardedDiamondGrant(diamonds: Int = MonetizationConstants.REWARDED_AD_DIAMOND_REWARD) {
        _totalUnityRewardedAdsWatched.value += 1
        _totalUnityRewardedDiamondsEarned.value += diamonds.coerceAtLeast(1)
        _unityRewardedStatus.value = UnityRewardedAdStatus.REWARD_GRANTED
    }

    private fun syncTelemetry(logMessage: String) {
        _mediationState.value = MediationTelemetryState(
            isAdMobInitialized = isAdMobInitialized,
            isUnityAdsInitialized = isUnityAdsInitialized,
            isAdMobActive = isAdMobActive,
            isUnityAdsActive = isUnityAdsActive,
            unityGameId = activeUnityGameId,
            unityBannerPlacementId = activeUnityBannerPlacementId,
            unityInterstitialPlacementId = activeUnityInterstitialPlacementId,
            unityRewardedPlacementId = activeUnityRewardedPlacementId,
            bannerRequestsServed = bannerRotationCounter.get(),
            interstitialRequestsServed = interstitialRotationCounter.get(),
            rewardedRequestsServed = rewardedRotationCounter.get(),
            lastBannerNetwork = _lastBannerDecision.value.network,
            lastInterstitialNetwork = _lastInterstitialDecision.value.network,
            lastRewardedNetwork = _lastRewardedDecision.value.network,
            lastMediationLog = logMessage
        )
    }
}
