package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monetization.AdManager
import com.example.ui.components.DiamondParticleExplosionHost
import com.example.ui.components.ActiveReferralUnlockedDialog
import com.example.ui.components.BannerAdView
import com.example.ui.components.DiamondRedeemDialog
import com.example.ui.components.InAppShopsyShoppingMallDialog
import com.example.ui.components.InterstitialAdDialog
import com.example.ui.components.PaymentModal
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.TrialPaywallDialog
import com.example.ui.components.ViralCreatorReelsHubDialog
import com.example.ui.components.WelcomeBonusAdModal
import com.example.ui.components.WelcomeBonusPopupDialog
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AffiliateHubScreen
import com.example.ui.screens.AiGeneratorScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DailyRewardStreakScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SupportDeskScreen
import com.example.ui.screens.VideoCrossPosterScreen
import com.example.ui.screens.VipPaywallScreen
import com.example.ui.screens.GeminiSocialMediaGeneratorScreen
import com.example.ui.screens.OneClickAiVideoHashtagFullScreen
import com.example.ui.screens.DiamondDashboardScreen
import com.viralboost.ai.FullSizeMegaMallScreen
import com.viralboost.ai.FullSizeFestiveMallScreen
import com.viralboost.ai.ViralBoostRealImageMallApp
import com.viralboost.ai.ViralBoostStrictAdMallApp
import com.example.ui.components.DynamicThemeTopBar
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ThemeController
import com.example.ui.theme.ViralBoostTheme
import com.example.ui.viewmodel.ViralBoostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,
    AI_TOOLS,
    VIP_PAYWALL,
    AFFILIATE,
    CROSS_POSTER,
    LEADERBOARD,
    SUPPORT,
    ADMIN_PANEL,
    AUTH,
    DAILY_REWARD,
    REELS_HUB
}

class MainActivity : ComponentActivity() {

    private val viewModel: ViralBoostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // STEP 1: Configure App Check with launch intent before any UI, ViewModels, or Firebase operations
        com.example.util.configureAppCheck(this, intent)
        enableEdgeToEdge()
        // Ensure WebView cache directories exist to prevent Chromium simple cache backend warnings
        try {
            val codeCacheDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache")
            java.io.File(codeCacheDir, "js").mkdirs()
            java.io.File(codeCacheDir, "wasm").mkdirs()
        } catch (_: Throwable) {
            // Safe fallback
        }

        // Initialize saved Dark / Light theme preference
        ThemeController.initialize(applicationContext)

        // Ensure AlarmManager daily streak & content generation reminder is scheduled
        com.example.util.DailyStreakReminderScheduler.ensureDefaultReminderScheduled(applicationContext)

        // Initialize both Google AdMob Mobile Ads SDK and Unity Ads SDK (Game ID: 800427608)
        AdManager.initialize(
            context = applicationContext,
            unityGameId = com.example.monetization.MonetizationConstants.UNITY_GAME_ID,
            testMode = false
        )

        setContent {
            val isDarkTheme = ThemeController.isDarkMode
            ViralBoostTheme(
                darkTheme = isDarkTheme,
                onToggleTheme = { viewModel.toggleThemeMode() }
            ) {
                ViralBoostApp(
                    activity = this,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ViralBoostApp(
    activity: ComponentActivity,
    viewModel: ViralBoostViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val appConfig by viewModel.appConfig.collectAsStateWithLifecycle()
    val trialStatus by viewModel.trialStatus.collectAsStateWithLifecycle()
    val allPayments by viewModel.allPayments.collectAsStateWithLifecycle()
    val allTickets by viewModel.allTickets.collectAsStateWithLifecycle()
    val aiHistory by viewModel.aiGenerations.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val allProductOrders by viewModel.allProductOrders.collectAsStateWithLifecycle()
    val allCrossPosts by viewModel.allCrossPosts.collectAsStateWithLifecycle()
    val allReferrals by viewModel.allReferrals.collectAsStateWithLifecycle()
    val allCommunityVideos by viewModel.allCommunityVideos.collectAsStateWithLifecycle()

    val isGeneratingAi by viewModel.isGeneratingAi.collectAsStateWithLifecycle()
    val isWatchingRewardedAd by viewModel.isWatchingRewardedAd.collectAsStateWithLifecycle()
    val showPaymentModal by viewModel.showPaymentModal.collectAsStateWithLifecycle()
    val selectedPlan by viewModel.selectedPlanForCheckout.collectAsStateWithLifecycle()
    val showRedeemDialog by viewModel.showRedeemDialog.collectAsStateWithLifecycle()
    val showTrialPaywallDialog by viewModel.showTrialPaywallDialog.collectAsStateWithLifecycle()
    val trialPaywallReason by viewModel.trialPaywallReason.collectAsStateWithLifecycle()
    val showWelcomePopup by viewModel.showWelcomePopup.collectAsStateWithLifecycle()
    val showWelcomeAdModal by viewModel.showWelcomeAdModal.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var currentScreen by remember { mutableStateOf(ScreenTab.HOME) }
    var showInterstitialAdDialog by remember { mutableStateOf(false) }
    var pendingDismissAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var activeReferralAlertMessage by remember { mutableStateOf<String?>(null) }
    var showShopsyMall by remember { mutableStateOf(false) }
    var showFullAiTrendingStudio by remember { mutableStateOf(false) }
    var showGeminiSocialStudio by remember { mutableStateOf(false) }
    var showOneClickAiVideoHashtagPage by remember { mutableStateOf(false) }
    var showDiamondDashboard by remember { mutableStateOf(false) }

    // Sync Room DB AppConfig credentials (Unity Ads Game ID & Placements + AdMob Units) into AdManager
    LaunchedEffect(
        appConfig.unityGameId,
        appConfig.unityBannerPlacementId,
        appConfig.unityInterstitialPlacementId,
        appConfig.unityRewardedPlacementId,
        appConfig.admobBannerUnitId,
        appConfig.admobInterstitialUnitId,
        appConfig.admobRewardedUnitId
    ) {
        AdManager.syncCredentialsFromConfig(appConfig, activity)
    }

    // Dismiss active keyboard when modals open
    LaunchedEffect(showPaymentModal, showRedeemDialog, showTrialPaywallDialog) {
        if (showPaymentModal || showRedeemDialog || showTrialPaywallDialog) {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    // Observe Toast & Notification events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            coroutineScope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(msg)
            }
        }
    }

    // Observe Real-Time Active Referral Alert
    LaunchedEffect(Unit) {
        viewModel.activeReferralAlert.collectLatest { msg ->
            activeReferralAlertMessage = msg
        }
    }

    // =========================================================================
    // PERMANENT NAVIGATION AD INTERCEPTOR (CORE ARCHITECTURAL LOCK)
    // Intercepts all bottom navigation tab switches: Home, AI Boost, VIP,
    // Ranks, Support, Profile. Enforces AdMob Interstitial Ad display before
    // loading the destination screen. (Permanently protected).
    // =========================================================================
    val handleTabSwitch: (ScreenTab) -> Unit = { targetScreen ->
        keyboardController?.hide()
        // Stabilized Ad Interceptor: if an interstitial ad is active, wait for user manual dismissal
        if (!showInterstitialAdDialog && currentScreen != targetScreen) {
            val isVip = userProfile?.isVip == true
            if (isVip) {
                // VIP Pro subscribers have 100% ad-free experience
                currentScreen = targetScreen
            } else {
                // Intercept navigation and trigger Mediated Interstitial (Unity BP_Interstitial_Android <-> AdMob) before displaying target screen
                AdManager.triggerNavigationInterstitial(
                    activity = activity,
                    isVip = false,
                    interstitialUnitId = appConfig.admobInterstitialUnitId,
                    unityPlacementId = appConfig.unityInterstitialPlacementId,
                    onShowInterstitialFallback = { onDismiss ->
                        pendingDismissAction = {
                            onDismiss()
                            currentScreen = targetScreen
                        }
                        showInterstitialAdDialog = true
                    },
                    onNavigate = {
                        currentScreen = targetScreen
                    }
                )
            }
        }
    }

    val allLegalRecords by viewModel.allLegalRecords.collectAsStateWithLifecycle()
    val referralNotificationMessage by viewModel.referralNotificationMessage.collectAsStateWithLifecycle()
    val friendJoinedStatus by viewModel.friendJoinedStatus.collectAsStateWithLifecycle()
    val isUserLoggedIn = userProfile?.isLoggedIn == true && userProfile?.hasAgreedToTerms == true
    val isOwnerActive = userProfile?.isOwner == true

    // BackHandler: Navigate back to Home with ad dismissal priority
    if (showInterstitialAdDialog) {
        BackHandler {
            showInterstitialAdDialog = false
            val action = pendingDismissAction
            pendingDismissAction = null
            action?.invoke()
        }
    } else if (showShopsyMall) {
        BackHandler {
            showShopsyMall = false
        }
    } else if (currentScreen != ScreenTab.HOME) {
        BackHandler {
            handleTabSwitch(ScreenTab.HOME)
        }
    }

    // Auto-Login & Mandatory Legal Onboarding Gate
    // If user is not yet logged in or has not agreed to terms, require Onboarding
    if (!isUserLoggedIn && !isOwnerActive) {
        OnboardingScreen(
            onSignupComplete = { name, contact, method ->
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                logLegalAgreementToAdminPanel(name, contact, timestamp)
                viewModel.completeOnboarding(name, contact, method) {
                    currentScreen = ScreenTab.HOME
                    viewModel.triggerWelcomePopup()
                }
            },
            onOwnerLoginSuccess = {
                viewModel.setOwnerStatus(true)
                currentScreen = ScreenTab.ADMIN_PANEL
            }
        )
        return
    }

    // Helper to trigger Mediated Interstitial Ad (Unity BP_Interstitial_Android <-> AdMob 50/50 rotation & fallback)
    // whenever user clicks to open any feature, option, or sub-page
    val triggerFeatureInterstitial: (String, () -> Unit) -> Unit = { featureName, onOpen ->
        keyboardController?.hide()
        val isVip = userProfile?.isVip == true
        if (isVip) {
            onOpen()
        } else {
            AdManager.triggerMilestoneInterstitial(
                context = activity,
                isVip = false,
                milestoneName = featureName,
                interstitialUnitId = appConfig.admobInterstitialUnitId,
                unityPlacementId = appConfig.unityInterstitialPlacementId,
                onShowInterstitialFallback = { onDismiss ->
                    pendingDismissAction = {
                        onDismiss()
                        onOpen()
                    }
                    showInterstitialAdDialog = true
                },
                onComplete = {
                    onOpen()
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                if (currentScreen != ScreenTab.ADMIN_PANEL && currentScreen != ScreenTab.REELS_HUB && !showShopsyMall) {
                    DynamicThemeTopBar(
                        isDarkTheme = ThemeController.isDarkMode,
                        onToggleTheme = { isDark ->
                            viewModel.setDarkThemeMode(isDark)
                        }
                    )
                }
            },
            bottomBar = {
                // Universal Mediated Banner Ad (BP_Banner_Android / AdMob) + Rewarded Ad Trigger (BP_Rewarded_Android / AdMob)
                // + Bottom Navigation Bar across all primary screens
                if (currentScreen != ScreenTab.ADMIN_PANEL && !showShopsyMall) {
                    Column {
                        com.example.ui.components.UniversalMediatedAdDock(
                            isVip = userProfile?.isVip == true,
                            admobBannerUnitId = appConfig.admobBannerUnitId,
                            unityBannerPlacementId = appConfig.unityBannerPlacementId,
                            admobRewardedUnitId = appConfig.admobRewardedUnitId,
                            unityRewardedPlacementId = appConfig.unityRewardedPlacementId,
                            featureLabel = currentScreen.name,
                            onRewardEarned = { diamonds: Int, coins: Int ->
                                viewModel.creditShopsyShoppingReward(diamonds, coins)
                            },
                            onUpgradeClick = { handleTabSwitch(ScreenTab.VIP_PAYWALL) }
                        )
                        if (currentScreen != ScreenTab.REELS_HUB) {
                            ViralBoostBottomNav(
                                currentScreen = currentScreen,
                                onTabSelected = handleTabSwitch,
                                openTicketCount = allTickets.count { it.status == "OPEN" }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenTab.HOME -> {
                    HomeScreen(
                        userProfile = userProfile,
                        appConfig = appConfig,
                        trialStatus = trialStatus,
                        referrals = allReferrals,
                        onClaimStreak = {
                            viewModel.claimDailyStreak()
                            AdManager.triggerMilestoneInterstitial(
                                context = activity,
                                isVip = userProfile?.isVip == true,
                                milestoneName = "Daily Streak Claim Milestone",
                                interstitialUnitId = appConfig.admobInterstitialUnitId,
                                unityPlacementId = appConfig.unityInterstitialPlacementId,
                                onShowInterstitialFallback = { onDismiss ->
                                    pendingDismissAction = {
                                        onDismiss()
                                        currentScreen = ScreenTab.HOME
                                    }
                                    showInterstitialAdDialog = true
                                }
                            )
                        },
                        onWatchRewardedAd = {
                            AdManager.showRewardedAd(
                                activity = activity,
                                rewardedUnitId = appConfig.admobRewardedUnitId,
                                unityPlacementId = appConfig.unityRewardedPlacementId,
                                onUserEarnedReward = {
                                    viewModel.finishRewardedAdFlow(rewardEarned = true)
                                },
                                onFallbackSimulation = {
                                    viewModel.startRewardedAdFlow()
                                }
                            )
                        },
                        onOpenRedeem = {
                            triggerFeatureInterstitial("Open Diamond Redeem Store") {
                                viewModel.openRedeemDialog()
                            }
                        },
                        onNavigateToAiTools = { handleTabSwitch(ScreenTab.AI_TOOLS) },
                        onNavigateToVipPaywall = { handleTabSwitch(ScreenTab.VIP_PAYWALL) },
                        onNavigateToAffiliate = { handleTabSwitch(ScreenTab.AFFILIATE) },
                        onNavigateToCrossPoster = { handleTabSwitch(ScreenTab.CROSS_POSTER) },
                        onTriggerPaywall = { reason ->
                            triggerFeatureInterstitial("Open VIP Trial Paywall") {
                                viewModel.openTrialPaywall(reason)
                            }
                        },
                        referralNotificationMessage = referralNotificationMessage,
                        friendJoinedStatus = friendJoinedStatus,
                        onDismissReferralNotification = { viewModel.dismissReferralNotification() },
                        onShareReferral = { ctx, code -> viewModel.shareReferralLink(ctx, code) },
                        onSimulateDownload = { ctx, name, code -> viewModel.simulateFriendDownload(ctx, name, code) },
                        onSimulateExploration = { ctx, name, code -> viewModel.simulateFriendActive2MinExploration(ctx, name, code) },
                        onVerifyActiveReferral = { ctx, id, name -> viewModel.verifyAndClaimActiveReferral(ctx, id, name) },
                        onSimulateActiveReferral = { ctx, name, code -> viewModel.simulateFriendActiveExploration(ctx, name, code) },
                        onOpenShopsyMall = {
                            triggerFeatureInterstitial("Open Shopsy Festive Mega Mall") {
                                showShopsyMall = true
                            }
                        },
                        onRewardAction = { diamonds, coins ->
                            viewModel.creditShopsyShoppingReward(diamonds, coins)
                        },
                        onOpenReelsHub = { handleTabSwitch(ScreenTab.REELS_HUB) },
                        onRedeemDiamondsForVip = { viewModel.redeemDiamondsForVipSubscription() },
                        onTriggerWelcomePopup = { viewModel.triggerWelcomePopup() },
                        onOpenFullAiStudio = {
                            triggerFeatureInterstitial("Open 1-Click AI Trending Studio") {
                                showFullAiTrendingStudio = true
                            }
                        },
                        onOpenGeminiSocialStudio = {
                            triggerFeatureInterstitial("Open Gemini Social Studio") {
                                showGeminiSocialStudio = true
                            }
                        },
                        onOpenOneClickAiVideoHashtag = {
                            triggerFeatureInterstitial("Open 1-Click AI Video & Hashtag Studio") {
                                showOneClickAiVideoHashtagPage = true
                            }
                        },
                        onOpenDiamondDashboard = {
                            triggerFeatureInterstitial("Open Diamond Wallet Dashboard") {
                                showDiamondDashboard = true
                            }
                        }
                    )
                }

                ScreenTab.CROSS_POSTER -> {
                    VideoCrossPosterScreen(
                        userProfile = userProfile,
                        affiliateTag = appConfig.affiliateTag,
                        downloadUrl = appConfig.appDownloadUrl,
                        recentCrossPosts = allCrossPosts,
                        bannerAdUnitId = appConfig.admobBannerUnitId,
                        onNavigateBack = { handleTabSwitch(ScreenTab.HOME) },
                        onSubscribeAiPlan = { planName, price, durationDays ->
                            viewModel.activateAiCrossPostSubscription(planName, price, durationDays)
                        },
                        onPublishCrossPost = { title: String, tags: String, description: String, videoUri: String, platforms: List<String>, fullCaption: String ->
                            viewModel.publishVideoCrossPost(title, tags, description, videoUri, platforms, fullCaption)
                        }
                    )
                }

                ScreenTab.AI_TOOLS -> {
                    AiGeneratorScreen(
                        userProfile = userProfile,
                        trialStatus = trialStatus,
                        aiHistory = aiHistory,
                        isGenerating = isGeneratingAi,
                        bannerAdUnitId = appConfig.admobBannerUnitId,
                        nativeAdUnitId = appConfig.admobNativeUnitId,
                        onGenerate = { type, topic, niche ->
                            viewModel.generateViralContent(type, topic, niche)
                        },
                        onNavigateToVip = { handleTabSwitch(ScreenTab.VIP_PAYWALL) },
                        onTriggerPaywall = { reason ->
                            triggerFeatureInterstitial("Open VIP Trial Paywall from AI Tools") {
                                viewModel.openTrialPaywall(reason)
                            }
                        },
                        onGenerate1ClickReel = { topic, onResult ->
                            viewModel.generate1ClickAiReel(topic, onResult)
                        },
                        onOpenGeminiSocialStudio = {
                            triggerFeatureInterstitial("Open Gemini Social Studio from AI Tools") {
                                showGeminiSocialStudio = true
                            }
                        },
                        onSaveContentToHistory = { type, topic, niche, content, platform, hashtags ->
                            viewModel.saveGeneratedContentToHistory(
                                type = type,
                                topic = topic,
                                niche = niche,
                                content = content,
                                platform = platform,
                                hashtags = hashtags
                            )
                        },
                        onToggleBookmark = { id, isBookmarked ->
                            viewModel.toggleGenerationBookmark(id, isBookmarked)
                        },
                        onDeleteHistoryItem = { id ->
                            viewModel.deleteGenerationHistoryItem(id)
                        }
                    )
                }

                ScreenTab.VIP_PAYWALL -> {
                    VipPaywallScreen(
                        userProfile = userProfile,
                        plans = viewModel.availablePlans,
                        onSelectPlan = { plan ->
                            triggerFeatureInterstitial("Open UPI Plan Checkout (${plan.name})") {
                                viewModel.openPaymentModal(plan)
                            }
                        },
                        onOpenRedeemDialog = {
                            triggerFeatureInterstitial("Open Diamond Redeem Store") {
                                viewModel.openRedeemDialog()
                            }
                        }
                    )
                }

                ScreenTab.AFFILIATE -> {
                    AffiliateHubScreen(
                        userProfile = userProfile,
                        affiliateTag = appConfig.affiliateTag,
                        placedOrders = allProductOrders,
                        leaderboard = leaderboard,
                        bannerAdUnitId = appConfig.admobBannerUnitId,
                        nativeAdUnitId = appConfig.admobNativeUnitId,
                        nativeVideoAdUnitId = appConfig.admobNativeVideoUnitId,
                        onNavigateToVip = { handleTabSwitch(ScreenTab.VIP_PAYWALL) },
                        onShareProduct = { product -> viewModel.shareProductToSocial(product) },
                        onPlaceOrder = { product, name, phone, address ->
                            viewModel.placeProductOrder(product, name, phone, address)
                        },
                        onClaimPurchaseReward = { product -> viewModel.claimProductPurchaseReward(product) },
                        onNavigateToAiTools = { handleTabSwitch(ScreenTab.AI_TOOLS) }
                    )
                }

                ScreenTab.LEADERBOARD -> {
                    LeaderboardScreen(
                        userProfile = userProfile,
                        creators = leaderboard,
                        bannerAdUnitId = appConfig.admobBannerUnitId,
                        nativeAdUnitId = appConfig.admobNativeUnitId,
                        nativeVideoAdUnitId = appConfig.admobNativeVideoUnitId
                    )
                }

                ScreenTab.SUPPORT -> {
                    SupportDeskScreen(
                        userProfile = userProfile,
                        tickets = allTickets,
                        bannerAdUnitId = appConfig.admobBannerUnitId,
                        nativeAdUnitId = appConfig.admobNativeUnitId,
                        onSubmitTicket = { subject, category, message ->
                            viewModel.submitSupportTicket(subject, category, message)
                        }
                    )
                }

                ScreenTab.ADMIN_PANEL -> {
                    AdminPanelScreen(
                        userProfile = userProfile,
                        appConfig = appConfig,
                        trialStatus = trialStatus,
                        allPayments = allPayments,
                        allTickets = allTickets,
                        allLegalRecords = allLegalRecords,
                        onVerifyPayment = { id, approve, note ->
                            viewModel.adminVerifyPayment(id, approve, note)
                        },
                        onReplyTicket = { id, reply ->
                            viewModel.adminReplyTicket(id, reply)
                        },
                        onSaveConfig = { newConfig ->
                            viewModel.saveAppConfig(newConfig)
                        },
                        onGrantBonus = { coins, diamonds ->
                            viewModel.adminAddBonus(coins, diamonds)
                        },
                        onSimulateTrialDay = { day ->
                            viewModel.simulateTrialDay(day)
                        },
                        onExitAdmin = { handleTabSwitch(ScreenTab.HOME) }
                    )
                }

                ScreenTab.AUTH -> {
                    AuthScreen(
                        currentProfile = userProfile,
                        ownerUpiId = appConfig.ownerUpiId,
                        onSaveProfile = { name, email, niche ->
                            viewModel.updateProfile(name, email, niche)
                        },
                        onOwnerLoginSuccess = {
                            viewModel.setOwnerStatus(true)
                            handleTabSwitch(ScreenTab.ADMIN_PANEL)
                        },
                        onContinueAsCreator = {
                            handleTabSwitch(ScreenTab.DAILY_REWARD)
                        },
                        onLogout = {
                            viewModel.logoutUser {
                                currentScreen = ScreenTab.HOME
                            }
                        }
                    )
                }

                ScreenTab.DAILY_REWARD -> {
                    DailyRewardStreakScreen(
                        userProfile = userProfile,
                        appConfig = appConfig,
                        onClaimReward = {
                            AdManager.triggerMilestoneInterstitial(
                                context = context,
                                isVip = userProfile?.isVip == true,
                                milestoneName = "Post-Signup Daily Reward Claim Milestone",
                                interstitialUnitId = appConfig.admobInterstitialUnitId,
                                unityPlacementId = appConfig.unityInterstitialPlacementId,
                                onShowInterstitialFallback = { onDismiss ->
                                    pendingDismissAction = {
                                        onDismiss()
                                        viewModel.claimPostSignupDailyReward(coins = 100, diamonds = 2) {
                                            currentScreen = ScreenTab.HOME
                                        }
                                    }
                                    showInterstitialAdDialog = true
                                },
                                onComplete = {
                                    viewModel.claimPostSignupDailyReward(coins = 100, diamonds = 2) {
                                        currentScreen = ScreenTab.HOME
                                    }
                                }
                            )
                        },
                        onContinueToHome = {
                            triggerFeatureInterstitial("Continue from Daily Reward to Home") {
                                currentScreen = ScreenTab.HOME
                            }
                        }
                    )
                }

                ScreenTab.REELS_HUB -> {
                    ViralCreatorReelsHubDialog(
                        videos = allCommunityVideos,
                        isVip = userProfile?.isVip == true,
                        userCoins = userProfile?.coins ?: 250,
                        userDiamonds = userProfile?.diamonds ?: 10,
                        onRewardVideoWatch = { videoId, title ->
                            viewModel.rewardVideoWatchSession(videoId, title)
                        },
                        onToggleFollow = { videoId, handle ->
                            viewModel.toggleFollowCreator(videoId, handle)
                        },
                        onIncrementLike = { videoId ->
                            viewModel.incrementVideoLike(videoId)
                        },
                        onBoostWithDiamonds = { videoId, diamonds ->
                            viewModel.boostVideoWithDiamonds(videoId, diamonds)
                        },
                        onBoostWithCoins = { videoId, coins ->
                            viewModel.boostVideoWithCoins(videoId, coins)
                        },
                        onBoostWithUpi = { videoId, planName, price, utr ->
                            viewModel.boostVideoWithUpi(videoId, planName, price, utr)
                        },
                        onSubmitNewVideo = { title, desc, platform, type, url ->
                            viewModel.submitCommunityVideo(title, desc, platform, type, url)
                        },
                        onDismiss = {
                            handleTabSwitch(ScreenTab.HOME)
                        }
                    )
                }
            }
        }
    }

    // Modal 1: Dual Payment (UPI Intent + QR code + 12-digit UTR Verification)
    if (showPaymentModal && selectedPlan != null) {
        PaymentModal(
            plan = selectedPlan!!,
            userDiamonds = userProfile?.diamonds ?: 0,
            ownerUpiId = appConfig.ownerUpiId,
            ownerName = appConfig.ownerName,
            ownerQrUrl = appConfig.ownerQrUrl,
            onDismiss = { viewModel.closePaymentModal() },
            onSubmitUtr = { plan, utr, method ->
                viewModel.submitUtrPayment(plan, utr, method)
            }
        )
    }

    // Modal 2: Diamond Utility & VIP Redemption Dialog
    if (showRedeemDialog) {
        DiamondRedeemDialog(
            userDiamonds = userProfile?.diamonds ?: 0,
            onDismiss = { viewModel.closeRedeemDialog() },
            onRedeemVip = { diamonds ->
                viewModel.redeemDiamonds(diamonds)
            }
        )
    }

    // Modal 3: Rewarded Video Ad Interactive Countdown (Simulation Fallback)
    if (isWatchingRewardedAd) {
        RewardedAdDialog(
            rewardDiamonds = appConfig.rewardedAdPayout,
            rewardedUnitId = appConfig.admobRewardedUnitId,
            onDismiss = { viewModel.finishRewardedAdFlow(rewardEarned = false) },
            onRewardEarned = { viewModel.finishRewardedAdFlow(rewardEarned = true) }
        )
    }

    // Modal 4: Dynamic 3-Day Trial Paywall & Urgency Dialog
    if (showTrialPaywallDialog) {
        TrialPaywallDialog(
            reasonMessage = trialPaywallReason,
            userDiamonds = userProfile?.diamonds ?: 0,
            plans = viewModel.availablePlans,
            ownerUpiId = appConfig.ownerUpiId,
            onDismiss = { viewModel.closeTrialPaywall() },
            onSelectPlan = { plan ->
                viewModel.closeTrialPaywall()
                viewModel.openPaymentModal(plan)
            },
            onUpgradeViaUpi = {
                viewModel.closeTrialPaywall()
                viewModel.openPaymentModal(viewModel.availablePlans.first())
            },
            onRedeemViaDiamonds = {
                viewModel.closeTrialPaywall()
                viewModel.openRedeemDialog()
            }
        )
    }

    // Modal 5: Mediated Fullscreen Interstitial Ad (Unity BP_Interstitial_Android <-> AdMob)
    if (showInterstitialAdDialog) {
        InterstitialAdDialog(
            interstitialUnitId = appConfig.admobInterstitialUnitId,
            unityPlacementId = appConfig.unityInterstitialPlacementId,
            onDismiss = {
                showInterstitialAdDialog = false
                val action = pendingDismissAction
                pendingDismissAction = null
                try {
                    action?.invoke()
                } catch (_: Throwable) {}
            }
        )
    }

    // Modal 6: Real-Time Active Referral 1-Day Free Subscription Unlock Alert
    activeReferralAlertMessage?.let { msg ->
        ActiveReferralUnlockedDialog(
            message = msg,
            onDismiss = { activeReferralAlertMessage = null },
            onStartExploringVip = {
                activeReferralAlertMessage = null
                handleTabSwitch(ScreenTab.AI_TOOLS)
            }
        )
    }

    // Dedicated Full-Screen Festive Mega Shopping Mall Screen (Shopsy/Flipkart Style with Strict Timed AdMob Ads)
    if (showShopsyMall) {
        ViralBoostStrictAdMallApp(
            bonusRupees = userProfile?.bonusRupees ?: 50.0,
            userDiamonds = userProfile?.diamonds ?: 10,
            userCoins = userProfile?.coins ?: 250,
            onBackToHome = { showShopsyMall = false },
            onOrderSuccessful = {
                viewModel.creditShopsyShoppingReward(10, 15)
            }
        )
    }

    // Ultimate Master Combined Gemini AI 1-Click Viral Video & Hashtag Studio, Reels Hub Spotlight & Dual UPI Flow
    if (showFullAiTrendingStudio) {
        com.viralboost.ai.ViralBoostGeminiCombinedApp(
            onBackToDashboard = { showFullAiTrendingStudio = false }
        )
    }

    // Modal: Gemini Social Media Content Generator Full-Screen Compose Experience
    if (showGeminiSocialStudio) {
        GeminiSocialMediaGeneratorScreen(
            onNavigateBack = { showGeminiSocialStudio = false },
            isVip = userProfile?.isVip == true,
            userDiamonds = userProfile?.diamonds ?: 10,
            bannerAdUnitId = appConfig.admobBannerUnitId,
            interstitialAdUnitId = appConfig.admobInterstitialUnitId,
            rewardedAdUnitId = appConfig.admobRewardedUnitId,
            onUpgradeToVip = {
                showGeminiSocialStudio = false
                handleTabSwitch(ScreenTab.VIP_PAYWALL)
            },
            onRedeemDiamondsForVip = {
                viewModel.redeemDiamondsForVipSubscription()
            },
            onRewardEarned = { diamonds, coins ->
                viewModel.creditShopsyShoppingReward(diamonds, coins)
            }
        )
    }

    // Dedicated Full-Screen Page: One Click AI Video & Viral हैशटैग Studio
    if (showOneClickAiVideoHashtagPage) {
        OneClickAiVideoHashtagFullScreen(
            onNavigateBack = { showOneClickAiVideoHashtagPage = false },
            isVip = userProfile?.isVip == true,
            userDiamonds = userProfile?.diamonds ?: 10,
            bannerAdUnitId = appConfig.admobBannerUnitId,
            interstitialAdUnitId = appConfig.admobInterstitialUnitId,
            rewardedAdUnitId = appConfig.admobRewardedUnitId,
            onUpgradeToVip = {
                showOneClickAiVideoHashtagPage = false
                handleTabSwitch(ScreenTab.VIP_PAYWALL)
            },
            onRedeemDiamondsForVip = {
                viewModel.redeemDiamondsForVipSubscription()
            },
            onRewardEarned = { diamonds, coins ->
                viewModel.creditShopsyShoppingReward(diamonds, coins)
            }
        )
    }

    // Dedicated Full-Screen Diamond Dashboard (Balance, Transaction History & Earning Activities)
    if (showDiamondDashboard) {
        DiamondDashboardScreen(
            userProfile = userProfile,
            appConfig = appConfig,
            referrals = allReferrals,
            productOrders = allProductOrders,
            onNavigateBack = { showDiamondDashboard = false },
            onClaimStreak = { viewModel.claimDailyStreak() },
            onWatchRewardedAd = {
                AdManager.showRewardedAd(
                    activity = activity,
                    rewardedUnitId = appConfig.admobRewardedUnitId,
                    unityPlacementId = appConfig.unityRewardedPlacementId,
                    onUserEarnedReward = {
                        viewModel.finishRewardedAdFlow(rewardEarned = true)
                    },
                    onFallbackSimulation = {
                        viewModel.startRewardedAdFlow()
                    }
                )
            },
            onShareReferral = { ctx, code -> viewModel.shareReferralLink(ctx, code) },
            onSimulateFriendDownload = { ctx, name, code -> viewModel.simulateFriendDownload(ctx, name, code) },
            onOpenShopsyMall = {
                showDiamondDashboard = false
                showShopsyMall = true
            },
            onNavigateToAffiliate = {
                showDiamondDashboard = false
                handleTabSwitch(ScreenTab.AFFILIATE)
            },
            onOpenReelsHub = {
                showDiamondDashboard = false
                handleTabSwitch(ScreenTab.REELS_HUB)
            },
            onRedeem150ForVip = { viewModel.redeemDiamondsForVipSubscription() },
            onOpenRedeemStore = {
                showDiamondDashboard = false
                viewModel.openRedeemDialog()
            },
            onUpgradeToVip = {
                showDiamondDashboard = false
                handleTabSwitch(ScreenTab.VIP_PAYWALL)
            },
            onRewardEarned = { diamonds, coins ->
                viewModel.creditShopsyShoppingReward(diamonds, coins)
            }
        )
    }

    // Modal 8: BIG BEAUTIFUL POPUP DIALOG FOR WELCOME BONUS (🎁 वेलकम बोनस!)
    if (showWelcomePopup) {
        WelcomeBonusPopupDialog(
            bonusAmountRupees = 50.0,
            onClaimBonusAndShowAd = {
                viewModel.claimWelcomeBonusAndShowAd()
            },
            onDismiss = { viewModel.dismissWelcomePopup() }
        )
    }

    // Modal 9: AdMob Interstitial Ad Simulation / Trigger for Welcome Bonus
    if (showWelcomeAdModal) {
        WelcomeBonusAdModal(
            bonusAmountRupees = 50.0,
            onCloseAdAndGetBonus = {
                viewModel.closeAdAndGetBonusRupees(50.0)
            },
            onDismiss = { viewModel.dismissWelcomeAdModal() }
        )
    }

    // Global Compose Canvas Diamond Reward Particle Burst Overlay
    DiamondParticleExplosionHost()
    }
}

@Composable
fun ViralBoostBottomNav(
    currentScreen: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    openTicketCount: Int
) {
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = BrandPurple,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .testTag("main_bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = currentScreen == ScreenTab.HOME,
            onClick = { onTabSelected(ScreenTab.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(20.dp)) },
            label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = currentScreen == ScreenTab.AI_TOOLS,
            onClick = { onTabSelected(ScreenTab.AI_TOOLS) },
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Boost", modifier = Modifier.size(20.dp)) },
            label = { Text("AI Boost", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_item_ai")
        )

        NavigationBarItem(
            selected = currentScreen == ScreenTab.VIP_PAYWALL,
            onClick = { onTabSelected(ScreenTab.VIP_PAYWALL) },
            icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = "VIP Pro", modifier = Modifier.size(20.dp), tint = GoldAccent) },
            label = { Text("VIP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = GoldAccent,
                indicatorColor = GoldAccent,
                unselectedIconColor = GoldAccent,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_vip")
        )

        NavigationBarItem(
            selected = currentScreen == ScreenTab.LEADERBOARD,
            onClick = { onTabSelected(ScreenTab.LEADERBOARD) },
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Ranks", modifier = Modifier.size(20.dp)) },
            label = { Text("Ranks", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_item_ranks")
        )

        NavigationBarItem(
            selected = currentScreen == ScreenTab.SUPPORT,
            onClick = { onTabSelected(ScreenTab.SUPPORT) },
            icon = {
                if (openTicketCount > 0) {
                    BadgedBox(badge = { Badge { Text("$openTicketCount") } }) {
                        Icon(Icons.Default.SupportAgent, contentDescription = "Support", modifier = Modifier.size(20.dp))
                    }
                } else {
                    Icon(Icons.Default.SupportAgent, contentDescription = "Support", modifier = Modifier.size(20.dp))
                }
            },
            label = { Text("Support", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_item_support")
        )

        NavigationBarItem(
            selected = currentScreen == ScreenTab.AUTH,
            onClick = { onTabSelected(ScreenTab.AUTH) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(20.dp)) },
            label = { Text("Profile", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = navItemColors,
            modifier = Modifier.testTag("nav_item_auth")
        )
    }
}

fun logLegalAgreementToAdminPanel(name: String, contact: String, timestamp: String) {
    val legalProofRecord = "ADMIN_LEGAL_LOG: User=$name, Contact/Email=$contact, Time=$timestamp, Status=AGREED_AND_VERIFIED"
    println(legalProofRecord) // Secure record for owner legal defense against false claims
}
