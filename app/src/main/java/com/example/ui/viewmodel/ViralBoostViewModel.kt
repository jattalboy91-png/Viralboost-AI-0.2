package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AiGenerationRecord
import com.example.data.local.AppConfig
import com.example.data.local.AppDatabase
import com.example.data.local.CommunityVideoPost
import com.example.data.local.LeaderboardCreator
import com.example.data.local.LegalAgreementRecord
import com.example.data.local.PaymentRecord
import com.example.data.local.ProductOrderRecord
import com.example.data.local.ReferralRecord
import com.example.data.local.SupportTicket
import com.example.data.local.UserProfile
import com.example.data.local.VideoCrossPostRecord
import com.example.data.model.AffiliateProduct
import com.example.data.repository.StreakResult
import com.example.data.repository.TrialStatus
import com.example.data.repository.ViralBoostRepository
import com.example.data.service.FirestoreSyncRepository
import com.example.data.service.GeminiSocialContentService
import com.example.data.service.GoogleAuthManager
import com.example.monetization.AdManager
import com.example.monetization.MonetizationConstants
import com.example.ui.components.DiamondRewardParticleController
import com.example.util.ReferralNotificationHelper
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PlanOption(
    val id: String,
    val name: String,
    val priceRupees: Int,
    val durationText: String,
    val features: List<String>,
    val badge: String = ""
)

class ViralBoostViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val cloudSync = FirestoreSyncRepository(application)
    private val geminiService = GeminiSocialContentService()
    private val repository = ViralBoostRepository(
        database.userDao(),
        database.paymentDao(),
        database.supportDao(),
        database.aiDao(),
        database.leaderboardDao(),
        database.configDao(),
        database.productOrderDao(),
        database.videoCrossPostDao(),
        database.referralDao(),
        database.communityVideoPostDao(),
        database.legalDao(),
        database.savedContentHistoryDao(),
        database.userStreakDiamondLedgerDao(),
        cloudSync
    )

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val streakCountFlow: StateFlow<Int> = repository.streakCountFlow
        .map { it ?: 1 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val diamondBalanceFlow: StateFlow<Int> = repository.diamondBalanceFlow
        .map { it ?: MonetizationConstants.INITIAL_DIAMONDS_BALANCE }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonetizationConstants.INITIAL_DIAMONDS_BALANCE)

    val allLegalRecords: StateFlow<List<LegalAgreementRecord>> = repository.allLegalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProductOrders: StateFlow<List<ProductOrderRecord>> = repository.allProductOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCrossPosts: StateFlow<List<VideoCrossPostRecord>> = repository.allCrossPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReferrals: StateFlow<List<ReferralRecord>> = repository.allReferrals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCommunityVideos: StateFlow<List<CommunityVideoPost>> = repository.allCommunityVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appConfig: StateFlow<AppConfig> = repository.appConfig
        .map { it ?: AppConfig() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppConfig())

    val allPayments: StateFlow<List<PaymentRecord>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTickets: StateFlow<List<SupportTicket>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiGenerations: StateFlow<List<AiGenerationRecord>> = repository.aiGenerations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedGenerations: StateFlow<List<AiGenerationRecord>> = repository.bookmarkedGenerations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedContentHistory: StateFlow<List<com.example.data.local.SavedContentHistoryEntity>> = repository.savedContentHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveGeneratedContentToHistory(
        type: String,
        topic: String,
        niche: String,
        content: String,
        platform: String = "Instagram Reels",
        keywords: String = "",
        hashtags: String = "",
        isBookmarked: Boolean = true
    ) {
        viewModelScope.launch {
            repository.saveGeneratedContentToHistory(
                type = type,
                topic = topic,
                niche = niche,
                content = content,
                platform = platform,
                keywords = keywords,
                hashtags = hashtags,
                isBookmarked = isBookmarked
            )
            _toastEvent.emit("✅ Content saved to local Room history!")
        }
    }

    fun toggleGenerationBookmark(id: Long, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleGenerationBookmark(id, isBookmarked)
            val msg = if (isBookmarked) "⭐ Bookmarked in Saved Content History!" else "Bookmark removed"
            _toastEvent.emit(msg)
        }
    }

    fun deleteGenerationHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteGenerationHistoryItem(id)
            _toastEvent.emit("🗑️ Removed from Saved Content History")
        }
    }

    fun clearGenerationHistory() {
        viewModelScope.launch {
            repository.clearGenerationHistory()
            _toastEvent.emit("🧹 Saved Content History cleared")
        }
    }

    val leaderboard: StateFlow<List<LeaderboardCreator>> = repository.leaderboardCreators
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trialStatus: StateFlow<TrialStatus> = repository.userProfile
        .map { profile ->
            if (profile != null) repository.getTrialStatus(profile)
            else TrialStatus(1, true, false, 3, 5, 5, 3, 5, 5, false)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TrialStatus(1, true, false, 3, 5, 5, 3, 5, 5, false)
        )

    // Dynamic Material 3 Theme Switcher State (Dark / Light Mode)
    val isDarkTheme: StateFlow<Boolean> = com.example.ui.theme.ThemeController.isDarkModeFlow

    fun toggleThemeMode() {
        com.example.ui.theme.ThemeController.toggleTheme(getApplication())
    }

    fun setDarkThemeMode(isDark: Boolean) {
        com.example.ui.theme.ThemeController.setDarkMode(isDark, getApplication())
    }

    // UI state
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _activeReferralAlert = MutableSharedFlow<String>()
    val activeReferralAlert: SharedFlow<String> = _activeReferralAlert.asSharedFlow()

    private val _referralNotificationMessage = MutableStateFlow<String?>(null)
    val referralNotificationMessage: StateFlow<String?> = _referralNotificationMessage.asStateFlow()

    private val _friendJoinedStatus = MutableStateFlow("Waiting for friend to download & explore...")
    val friendJoinedStatus: StateFlow<String> = _friendJoinedStatus.asStateFlow()

    fun dismissReferralNotification() {
        _referralNotificationMessage.value = null
    }

    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi = _isGeneratingAi.asStateFlow()

    private val _isWatchingRewardedAd = MutableStateFlow(false)
    val isWatchingRewardedAd = _isWatchingRewardedAd.asStateFlow()

    private val _selectedPlanForCheckout = MutableStateFlow<PlanOption?>(null)
    val selectedPlanForCheckout = _selectedPlanForCheckout.asStateFlow()

    private val _showPaymentModal = MutableStateFlow(false)
    val showPaymentModal = _showPaymentModal.asStateFlow()

    private val _showRedeemDialog = MutableStateFlow(false)
    val showRedeemDialog = _showRedeemDialog.asStateFlow()

    private val _showTrialPaywallDialog = MutableStateFlow(false)
    val showTrialPaywallDialog = _showTrialPaywallDialog.asStateFlow()

    // Pop-up Welcome Bonus Dialog State & AdMob Interstitial Ad Trigger
    private val _showWelcomePopup = MutableStateFlow(false)
    val showWelcomePopup: StateFlow<Boolean> = _showWelcomePopup.asStateFlow()

    private val _showWelcomeAdModal = MutableStateFlow(false)
    val showWelcomeAdModal: StateFlow<Boolean> = _showWelcomeAdModal.asStateFlow()

    fun triggerWelcomePopup() {
        _showWelcomePopup.value = true
    }

    fun dismissWelcomePopup() {
        _showWelcomePopup.value = false
    }

    fun claimWelcomeBonusAndShowAd() {
        _showWelcomePopup.value = false
        _showWelcomeAdModal.value = true
    }

    fun dismissWelcomeAdModal() {
        _showWelcomeAdModal.value = false
    }

    fun closeAdAndGetBonusRupees(amount: Double = 50.0) {
        _showWelcomeAdModal.value = false
        viewModelScope.launch {
            repository.creditWelcomeBonusRupees(amount)
            val msg = "🎉 बधाई हो! आपके वॉलेट में ₹${amount.toInt()} का वेलकम बोनस क्रेडिट कर दिया गया है!"
            _referralNotificationMessage.value = msg
            _toastEvent.emit(msg)
            _activeReferralAlert.emit(msg)
        }
    }

    private val _trialPaywallReason = MutableStateFlow("Your 3-Day Free Trial has expired! Unlock unlimited VIP Pro access.")
    val trialPaywallReason = _trialPaywallReason.asStateFlow()

    fun openTrialPaywall(reason: String = "") {
        if (reason.isNotBlank()) _trialPaywallReason.value = reason
        _showTrialPaywallDialog.value = true
    }

    fun closeTrialPaywall() {
        _showTrialPaywallDialog.value = false
    }

    val availablePlans = listOf(
        PlanOption(
            id = "pass_10_days",
            name = "10-Day Starter Pass",
            priceRupees = MonetizationConstants.PRICE_PASS_10_DAYS,
            durationText = "10 Days Access",
            features = listOf(
                "Max 2 AI Videos daily",
                "Up to 5 Photo Generations daily",
                "100% Ad-Free (No Banners & Interstitials)",
                "Zero Watermark on Scripts & Output",
                "Creator Affiliate Tag Access"
            ),
            badge = "STARTER"
        ),
        PlanOption(
            id = "pass_15_days",
            name = "VIP Pass (15 Days) 🚀",
            priceRupees = MonetizationConstants.PRICE_PASS_15_DAYS,
            durationText = "15 Days Access",
            features = listOf(
                "Custom Face-Upload AI Video Generator",
                "Max 2 AI Videos daily with Auto-Hashtags",
                "100% Ad-Free VIP Experience",
                "1-Click Auto-Post to FB, IG, YT & GoLiveGram",
                "Full Thumbnail & Script Toolkit"
            ),
            badge = "PLAN 1 • ₹49"
        ),
        PlanOption(
            id = "vip_monthly",
            name = "Mega VIP Pass (1 Month) ⭐",
            priceRupees = MonetizationConstants.PRICE_VIP_MONTHLY,
            durationText = "30 Days Access",
            features = listOf(
                "Unlimited Custom Photo Face-Swap AI",
                "Up to 3 AI Videos per day",
                "Up to 15 Photo Generations daily",
                "1-Click Multi-Social Broadcast Engine",
                "Creator Follower Boost Pool Access",
                "Priority 1-on-1 Growth Support Vault"
            ),
            badge = "PLAN 2 • ₹179"
        ),
        PlanOption(
            id = "pro_3_months",
            name = "3-Month Ultimate Pro Plan",
            priceRupees = MonetizationConstants.PRICE_VIP_3_MONTHS_PRO,
            durationText = "90 Days Access",
            features = listOf(
                "FULL UNLIMITED AI Video Generations (Zero Restrictions)",
                "FULL UNLIMITED Photo & Thumbnail Generations",
                "100% Ad-Free VIP Experience (Zero Ads)",
                "All Advanced AI Toolkits (Bulk & Auto-Publisher)",
                "Gemini AI Chat & Creator Strategist Access",
                "Lifetime Gold Leaderboard Recognition"
            ),
            badge = "BEST VALUE"
        )
    )

    init {
        viewModelScope.launch {
            repository.getProfileOnce()
            repository.syncLeaderboardAndCloud()
            if (cloudSync.isSignedIn()) {
                repository.restoreFromCloudIfAvailable()
            }
            val savedConfig = repository.getAppConfigOnce()
            AdManager.syncCredentialsFromConfig(savedConfig, getApplication())
        }
    }

    private val _isGoogleSigningIn = MutableStateFlow(false)
    val isGoogleSigningIn = _isGoogleSigningIn.asStateFlow()

    fun signInWithGoogle(activityContext: Context, onComplete: () -> Unit = {}) {
        if (_isGoogleSigningIn.value) return
        viewModelScope.launch {
            _isGoogleSigningIn.value = true
            val result = GoogleAuthManager.signInWithGoogle(activityContext)
            _isGoogleSigningIn.value = false
            result.onSuccess { firebaseUser ->
                val name = firebaseUser.displayName?.takeIf { it.isNotBlank() }
                    ?: firebaseUser.email?.substringBefore("@")
                    ?: "ViralCreator"
                val email = firebaseUser.email ?: "creator@viralboost.ai"
                val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                    .format(java.util.Date())
                repository.completeOnboarding(name, email, "email", timestamp)
                repository.restoreFromCloudIfAvailable()
                repository.syncLeaderboardAndCloud()
                DiamondRewardParticleController.triggerDiamondRewardBurst(
                    diamondsEarned = 10,
                    coinsEarned = 250,
                    taskLabel = "Google Sign-In Verified!"
                )
                _toastEvent.emit("✅ Signed in with Google ($email)! Cloud sync active.")
                onComplete()
            }.onFailure { err ->
                _toastEvent.emit("⚠️ ${err.localizedMessage ?: "Google Sign-In cancelled or failed."}")
            }
        }
    }

    fun convertCoinsToDiamonds(coinsToSpend: Int = 100) {
        viewModelScope.launch {
            val ok = repository.convertCoinsToDiamonds(coinsToSpend)
            if (ok) {
                val earned = coinsToSpend / 20
                DiamondRewardParticleController.triggerDiamondRewardBurst(
                    diamondsEarned = earned,
                    coinsEarned = 0,
                    taskLabel = "Swapped $coinsToSpend Coins!"
                )
                _toastEvent.emit("🔄 Swapped $coinsToSpend 🪙 Coins for +$earned 💎 Diamonds!")
            } else {
                _toastEvent.emit("⚠️ Need at least $coinsToSpend Coins to swap for Diamonds!")
            }
        }
    }

    fun openPaymentModal(plan: PlanOption) {
        _selectedPlanForCheckout.value = plan
        _showPaymentModal.value = true
    }

    fun closePaymentModal() {
        _showPaymentModal.value = false
    }

    fun openRedeemDialog() {
        _showRedeemDialog.value = true
    }

    fun closeRedeemDialog() {
        _showRedeemDialog.value = false
    }

    fun claimDailyStreak() {
        viewModelScope.launch {
            when (val result = repository.claimDailyStreak()) {
                is StreakResult.Success -> {
                    val bonusText = if (result.milestoneBonus > 0) " (Includes +${result.milestoneBonus} 💎 Milestone Bonus! 🎉)" else ""
                    DiamondRewardParticleController.triggerDiamondRewardBurst(
                        diamondsEarned = result.diamondsAwarded,
                        coinsEarned = 0,
                        taskLabel = "Daily Login Streak (Day ${result.streakDays})!"
                    )
                    _toastEvent.emit("🔥 Streak Claimed! Day ${result.streakDays} (+${result.diamondsAwarded} Diamonds credited)$bonusText")
                }
                is StreakResult.AlreadyClaimed -> {
                    _toastEvent.emit("✅ Today's streak reward (+2 Diamonds) already claimed! Come back tomorrow.")
                }
                is StreakResult.Error -> {
                    _toastEvent.emit("⚠️ ${result.message}")
                }
            }
        }
    }
    fun claimPostSignupDailyReward(coins: Int = 100, diamonds: Int = 2, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.claimPostSignupDailyReward(coins, diamonds)
            DiamondRewardParticleController.triggerDiamondRewardBurst(
                diamondsEarned = diamonds,
                coinsEarned = coins,
                taskLabel = "Welcome Daily Reward Claimed!"
            )
            _toastEvent.emit("🎉 Welcome Bonus Claimed! +$coins Coins 🪙 & +$diamonds Diamonds 💎 credited to your wallet!")
            onComplete()
        }
    }
    fun startRewardedAdFlow() {
        _isWatchingRewardedAd.value = true
    }

    fun finishRewardedAdFlow(rewardEarned: Boolean) {
        _isWatchingRewardedAd.value = false
        if (rewardEarned) {
            viewModelScope.launch {
                val reward = repository.rewardAdWatched()
                AdManager.recordUnityRewardedDiamondGrant(reward)
                DiamondRewardParticleController.triggerDiamondRewardBurst(
                    diamondsEarned = reward,
                    coinsEarned = 15,
                    taskLabel = "Unity Ads Rewarded Video Completed!"
                )
                _toastEvent.emit("💎 +$reward Bonus Diamonds awarded for watching Unity Rewarded Ad!")
            }
        }
    }

    fun redeemDiamonds(diamonds: Int) {
        viewModelScope.launch {
            val success = repository.redeemDiamondsForVip(diamonds)
            if (success) {
                _toastEvent.emit("🎉 Congratulations! VIP Pro Plan unlocked for 30 Days via Diamond Redemption!")
                _showRedeemDialog.value = false
            } else {
                _toastEvent.emit("⚠️ Insufficient diamonds! You need 500 Diamonds for 1 Month VIP Pro.")
            }
        }
    }

    fun submitUtrPayment(plan: PlanOption, utr: String, method: String) {
        viewModelScope.launch {
            if (utr.trim().length != 12 || !utr.trim().all { it.isDigit() }) {
                _toastEvent.emit("❌ Invalid UTR! Please enter a valid 12-digit transaction number.")
                return@launch
            }
            repository.submitPaymentUtr(
                planName = plan.name,
                amountRupees = plan.priceRupees,
                utrNumber = utr.trim(),
                method = method
            )
            _showPaymentModal.value = false
            _toastEvent.emit("✅ 12-digit UTR ($utr) submitted! Admin will verify and activate VIP shortly.")
        }
    }

    fun submitSupportTicket(subject: String, category: String, message: String) {
        viewModelScope.launch {
            if (subject.isBlank() || message.isBlank()) {
                _toastEvent.emit("⚠️ Please fill in both subject and message.")
                return@launch
            }
            repository.submitSupportTicket(subject, category, message)
            _toastEvent.emit("📩 Ticket submitted successfully! Owner/Admin will review and reply soon.")
        }
    }

    fun generateViralContent(type: String, topic: String, niche: String) {
        viewModelScope.launch {
            val (canGenerate, errorMsg) = repository.checkCanGenerateAi(type)
            if (!canGenerate) {
                if (errorMsg.startsWith("EXPIRED:") || errorMsg.startsWith("LIMIT:")) {
                    _trialPaywallReason.value = errorMsg.substringAfter(": ")
                    _showTrialPaywallDialog.value = true
                }
                _toastEvent.emit("⚠️ $errorMsg")
                return@launch
            }

            _isGeneratingAi.value = true
            val safeTopic = topic.trim().ifBlank { "How to grow 100k followers fast with AI Reels" }
            val (platformLabel, toneLabel, contentTypeLabel) = when (type) {
                "VIDEO", "TEXT_TO_VIDEO" -> Triple("Instagram Reels & YouTube Shorts", "Cinematic 9:16 Video Scene Plan", "AI Text-to-Video Scene Breakdown")
                "PHOTO_TO_VIDEO" -> Triple("Instagram Reels & Shorts", "Dynamic Motion Animation", "Photo-to-Video Animation Blueprint")
                "IMAGE", "THUMBNAIL" -> Triple("YouTube & Instagram Cover", "High-CTR Visual Drama", "Thumbnail & Cover Prompt Pack")
                "TAGS", "SEO_TAGS" -> Triple("YouTube, Instagram & Shorts", "Search & Algorithm SEO", "Viral Titles, Keywords & Hashtags")
                "BULK" -> Triple("Multi-Platform 5-Part Series", "Zero-Drop Retention Series", "5-Episode Bulk Script Pack")
                "PUBLISHER" -> Triple("Instagram + YouTube + TikTok", "Auto-Publish Caption Suite", "Multi-Platform Schedule & Captions")
                "AFFILIATE" -> Triple("Meesho, Flipkart & Amazon Deals", "High-Converting Deal Pitch", "Affiliate Product Pitch & Hooks")
                else -> Triple("Instagram Reels & YouTube Shorts", "High-Energy Viral", "3-Sec Hooks & 60-Sec Script")
            }

            try {
                val aiResult = geminiService.generateSocialContent(
                    prompt = safeTopic,
                    platform = platformLabel,
                    tone = "$toneLabel • Niche: $niche",
                    contentType = contentTypeLabel,
                    targetAudience = "$niche Creators & Audience"
                )
                val formattedOutput = if (aiResult.isRealApiCall) {
                    "⚡ Powered by Live Google Gemini AI (3.8 Flash)\n\n${aiResult.fullResponse}"
                } else {
                    if (!aiResult.errorMessage.isNullOrBlank()) {
                        _toastEvent.emit("⚠️ Gemini API note: ${aiResult.errorMessage}")
                    }
                    aiResult.fullResponse
                }
                repository.recordAiGeneration(type, safeTopic, niche, formattedOutput)
                _toastEvent.emit(
                    if (aiResult.isRealApiCall) "✨ Live Gemini AI generated viral $type! (-20 Coins)"
                    else "✨ Generated viral $type! (-20 Coins)"
                )
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                _toastEvent.emit("❌ Gemini generation failed: ${e.localizedMessage ?: "Check internet connection"}")
            } finally {
                _isGeneratingAi.value = false
            }
        }
    }

    fun generate1ClickAiReel(topic: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val safeTopic = topic.trim().ifBlank { "Viral Growth Secret" }
            _isGeneratingAi.value = true
            try {
                val aiResult = geminiService.generateSocialContent(
                    prompt = safeTopic,
                    platform = "Instagram Reels & YouTube Shorts",
                    tone = "High-Energy Bilingual Viral Reel",
                    contentType = "1-Click Video Scene + 60s Script + Viral Hashtags"
                )
                val statusTag = if (aiResult.isRealApiCall) "⚡ Live Gemini 3.8 Flash" else "⚡ Smart Offline Engine"
                val fullResult = "$statusTag • Ready for Instagram & YouTube!\n\n${aiResult.fullResponse}"
                repository.recordAiGeneration("1_CLICK_AI_REEL", safeTopic, "Shorts & Reels", fullResult)
                onResult(fullResult)
                _toastEvent.emit("🎬 AI Reel Package generated for '$safeTopic'!")
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                val errMsg = "❌ AI Reel generation failed: ${e.localizedMessage ?: "Network unavailable"}"
                onResult(errMsg)
                _toastEvent.emit(errMsg)
            } finally {
                _isGeneratingAi.value = false
            }
        }
    }

    fun simulateTrialDay(day: Int) {
        viewModelScope.launch {
            repository.simulateTrialDay(day)
            val desc = if (day > 3) "Expired (Day 4+)" else "Day $day"
            _toastEvent.emit("Admin: Switched Trial to $desc")
        }
    }

    fun adminVerifyPayment(paymentId: Long, approve: Boolean, note: String) {
        viewModelScope.launch {
            repository.adminVerifyPayment(paymentId, approve, note)
            val action = if (approve) "Approved & VIP Activated" else "Rejected"
            _toastEvent.emit("Admin: Payment #$paymentId $action")
        }
    }

    fun adminReplyTicket(ticketId: Long, reply: String) {
        viewModelScope.launch {
            if (reply.isBlank()) {
                _toastEvent.emit("⚠️ Reply cannot be empty.")
                return@launch
            }
            repository.adminReplyTicket(ticketId, reply)
            _toastEvent.emit("Admin: Reply sent to user for Ticket #$ticketId")
        }
    }

    fun updateProfile(name: String, email: String, niche: String) {
        viewModelScope.launch {
            repository.updateProfile(name, email, niche)
            _toastEvent.emit("✅ Profile updated successfully")
        }
    }

    /**
     * Complete Onboarding & Save One-Time "I Agree" Legal Proof to Admin Panel
     */
    fun completeOnboarding(
        name: String,
        contact: String,
        signupMethod: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            repository.completeOnboarding(name, contact, signupMethod, timestamp)
            DiamondRewardParticleController.triggerDiamondRewardBurst(
                diamondsEarned = 10,
                coinsEarned = 250,
                taskLabel = "Welcome Creator Bonus Unlocked!"
            )
            _referralNotificationMessage.value = "🎁 Welcome Bonus Active: 10 Diamonds (₹2 Value) & 250 Coins for Shopping Hub Discounts!"
            _toastEvent.emit("🎉 Welcome to ViralBoost AI, $name! +10 Diamonds 💎 & +250 Coins 🪙 unlocked!")
            onComplete()
        }
    }

    /**
     * Log out user (resets login status so user can test onboarding or switch accounts)
     */
    fun logoutUser(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(NonCancellable) {
                GoogleAuthManager.signOut(getApplication())
                repository.logoutUser()
            }
            _toastEvent.emit("Logged out of ViralBoost AI. See you soon!")
            onComplete()
        }
    }

    fun setOwnerStatus(isOwner: Boolean) {
        viewModelScope.launch {
            repository.setOwnerStatus(isOwner)
        }
    }

    fun adminAddBonus(coins: Int, diamonds: Int) {
        viewModelScope.launch {
            if (coins != 0) repository.addCoins(coins)
            if (diamonds != 0) repository.addDiamonds(diamonds)
            if (diamonds > 0) {
                DiamondRewardParticleController.triggerDiamondRewardBurst(
                    diamondsEarned = diamonds,
                    coinsEarned = coins.coerceAtLeast(0),
                    taskLabel = "Admin Bonus Credited!"
                )
            }
            _toastEvent.emit("Admin: Added +$coins Coins and +$diamonds Diamonds")
        }
    }

    fun creditShopsyShoppingReward(diamonds: Int = 4, coins: Int = 10) {
        viewModelScope.launch {
            repository.addDiamonds(diamonds)
            repository.addCoins(coins)
            DiamondRewardParticleController.triggerDiamondRewardBurst(
                diamondsEarned = diamonds,
                coinsEarned = coins,
                taskLabel = "Task Completed! Diamond Reward Earned"
            )
            _toastEvent.emit("🛍️ Shopsy Shopping Action! +$diamonds Diamonds 💎 & +$coins Coins 🪙 credited to your wallet!")
        }
    }

    fun saveAppConfig(newConfig: AppConfig) {
        viewModelScope.launch {
            repository.updateAppConfig(newConfig)
            AdManager.syncCredentialsFromConfig(newConfig, getApplication())
            _toastEvent.emit("✅ AdMob, Unity Ads (${newConfig.unityGameId}) & Owner Credentials saved to Room DB!")
        }
    }

    /**
     * Social Media Sharing Reward: Instantly earns 10 Coins
     */
    fun shareProductToSocial(product: AffiliateProduct) {
        viewModelScope.launch {
            repository.rewardProductShare(product)
            _toastEvent.emit("Shared to Social! +10 Coins credited to your wallet 🪙")
        }
    }

    /**
     * Direct In-App Product Order: Records order and credits 3 Diamonds directly
     */
    fun placeProductOrder(
        product: AffiliateProduct,
        customerName: String,
        customerPhone: String,
        shippingAddress: String
    ) {
        viewModelScope.launch {
            val tag = appConfig.value.affiliateTag
            repository.placeProductOrder(
                product = product,
                customerName = customerName,
                customerPhone = customerPhone,
                shippingAddress = shippingAddress,
                affiliateTag = tag
            )
            DiamondRewardParticleController.triggerDiamondRewardBurst(
                diamondsEarned = 3,
                coinsEarned = 0,
                taskLabel = "Order Confirmed! +3 Diamonds Earned"
            )
            _toastEvent.emit("Order Confirmed! +3 Diamonds credited directly to your wallet 💎")
        }
    }

    /**
     * Product Purchase Reward: Credits 3 Diamonds upon direct platform checkout
     */
    fun claimProductPurchaseReward(product: AffiliateProduct) {
        viewModelScope.launch {
            repository.rewardProductPurchase(product)
            DiamondRewardParticleController.triggerDiamondRewardBurst(
                diamondsEarned = 3,
                coinsEarned = 0,
                taskLabel = "Purchase Verified! +3 Diamonds Earned"
            )
            _toastEvent.emit("Purchase Verified! +3 Diamonds credited directly to your wallet 💎")
        }
    }

    /**
     * Video Cross-Posting Engine: Records post and credits +10 Coins to user wallet
     */
    fun publishVideoCrossPost(
        title: String,
        tags: String,
        description: String,
        videoUri: String,
        platforms: List<String>,
        fullCaption: String
    ) {
        viewModelScope.launch {
            repository.recordVideoCrossPost(
                title = title,
                tags = tags,
                description = description,
                videoUri = videoUri,
                platforms = platforms,
                fullCaption = fullCaption
            )
            _toastEvent.emit("🚀 Video cross-posted to ${platforms.size} channels! +10 Coins credited 🪙")
        }
    }

    /**
     * AI Video Optimization Subscription: ₹49 for 15 Days or ₹79 for 1 Month
     */
    fun activateAiCrossPostSubscription(planName: String, priceRupees: Int, durationDays: Int) {
        viewModelScope.launch {
            repository.activateSubscriptionPlan(planName, priceRupees, durationDays)
            _toastEvent.emit("🎉 $planName Activated! AI Video Optimization Unlocked for $durationDays Days!")
        }
    }

    /**
     * 1. Instant Share Rewards:
     * When user shares their unique referral link/code, instantly credit +10 Coins and +4 Diamonds
     */
    fun shareReferralLink(context: Context, referralCode: String) {
        viewModelScope.launch {
            repository.rewardReferralShare()
            DiamondRewardParticleController.triggerDiamondRewardBurst(
                diamondsEarned = 4,
                coinsEarned = 10,
                taskLabel = "Referral Link Shared!"
            )
            _referralNotificationMessage.value = "Link Shared! Earned +10 Coins & +4 Diamonds."
            _toastEvent.emit("Referral Link Shared! +10 Coins 🪙 & +4 Diamonds 💎 credited to your wallet!")
        }
    }

    /**
     * Step 1: Friend downloads and opens app:
     * User gets +10 Diamonds instantly + Notification popup
     */
    fun simulateFriendDownload(context: Context, friendName: String = "Rohit Verma", referralCode: String) {
        viewModelScope.launch {
            repository.rewardFriendDownload(friendName, referralCode)
            DiamondRewardParticleController.triggerDiamondRewardBurst(
                diamondsEarned = 10,
                coinsEarned = 0,
                taskLabel = "Friend App Download Bonus!"
            )
            val msg = "बधाई हो! आपके फ्रेंड ने ऐप डाउनलोड कर लिया है, आपको 10 डायमंड मिल गए हैं!"
            _referralNotificationMessage.value = msg
            _friendJoinedStatus.value = "Friend ($friendName) downloaded & opened app! (+10 Diamonds credited)"
            ReferralNotificationHelper.showFriendDownloadedBonusNotification(context, friendName)
            _toastEvent.emit("💎 $msg")
            _activeReferralAlert.emit(msg)
        }
    }

    /**
     * Step 2: Friend explores app for 2 minutes:
     * User gets 1-Day Free VIP Pro Plan unlock + Notification popup
     */
    fun simulateFriendActive2MinExploration(context: Context, friendName: String = "Rohit Verma", referralCode: String) {
        viewModelScope.launch {
            val refId = repository.recordNewFriendReferral(friendName, referralCode)
            repository.verifyActiveReferral(refId, friendName)
            val msg = "बधाई हो! आपके फ्रेंड ने 2 मिनट ऐप चलाया, आपका 1 दिन का VIP प्रो अनलॉक हो गया है!"
            _referralNotificationMessage.value = msg
            _friendJoinedStatus.value = "Friend explored for 2 mins! 1-Day Free VIP Unlocked 🌟"
            ReferralNotificationHelper.showActiveExplorationVipUnlockedNotification(context, friendName)
            _toastEvent.emit("🌟 $msg")
            _activeReferralAlert.emit(msg)
        }
    }

    /**
     * Active Referral & Full-App 1-Day Free Subscription Logic:
     * Friend installs app, signs up, and actively browses for 1 to 2 minutes.
     * Unlocks 1 Day (24 hrs) Full-App Free Subscription and triggers real-time push notification + alert:
     */
    fun verifyAndClaimActiveReferral(context: Context, referralId: Long, friendName: String) {
        viewModelScope.launch {
            repository.verifyActiveReferral(referralId, friendName)
            val notificationMessage = "बधाई हो! आपके फ्रेंड ने 2 मिनट तक ऐप यूज़ किया, आपको 24 घंटे (1 दिन) के लिए फ्री वीआईपी प्रो प्लान मिल गया है!"
            _referralNotificationMessage.value = notificationMessage
            _friendJoinedStatus.value = "Friend explored for 2 mins! 1-Day Free VIP Unlocked 🌟"
            ReferralNotificationHelper.showActiveExplorationVipUnlockedNotification(context, friendName)
            _toastEvent.emit(notificationMessage)
            _activeReferralAlert.emit(notificationMessage)
        }
    }

    /**
     * Interactive Simulator:
     * Simulates a friend downloading the app, registering, and exploring for 1 to 2 minutes,
     * culminating in active verification, 24-hr VIP unlock, and instant notification.
     */
    fun simulateFriendActiveExploration(context: Context, friendName: String, referralCode: String) {
        viewModelScope.launch {
            val refId = repository.recordNewFriendReferral(friendName, referralCode)
            _toastEvent.emit("Friend '$friendName' joined via $referralCode! Exploration session started (1-2 mins) ⏳")
            kotlinx.coroutines.delay(1800)
            repository.updateReferralExploration(refId, 45)
            kotlinx.coroutines.delay(1800)
            repository.updateReferralExploration(refId, 90)
            kotlinx.coroutines.delay(1000)
            verifyAndClaimActiveReferral(context, refId, friendName)
        }
    }

    /**
     * Per-Video Reward System:
     * User completes watching a video in the community feed:
     * - Rewards user with +15 Coins and +3 Diamonds
     * - Increments the view count for the creator's video
     */
    fun rewardVideoWatchSession(videoId: Long, videoTitle: String) {
        viewModelScope.launch {
            repository.incrementVideoViews(videoId)
            repository.rewardVideoWatch(diamonds = 3, coins = 15)
            DiamondRewardParticleController.triggerDiamondRewardBurst(
                diamondsEarned = 3,
                coinsEarned = 15,
                taskLabel = "Creator Video Watched!"
            )
            _toastEvent.emit("🎉 Video completed! +15 Coins 🪙 & +3 Diamonds 💎 credited!")
        }
    }

    fun toggleFollowCreator(videoId: Long, creatorHandle: String) {
        viewModelScope.launch {
            repository.toggleFollowCreator(videoId)
            _toastEvent.emit("Updated follow status for $creatorHandle! Engagement boosted 🚀")
        }
    }

    fun incrementVideoLike(videoId: Long) {
        viewModelScope.launch {
            repository.incrementVideoLikes(videoId)
            _toastEvent.emit("❤️ Liked video! Creator engagement boosted.")
        }
    }

    /**
     * Creator Boost & Super Spotlight: Second income stream for app owner
     * Option 1: Boost with Diamonds
     */
    fun boostVideoWithDiamonds(videoId: Long, diamondsCost: Int = 50) {
        viewModelScope.launch {
            val profile = repository.getProfileOnce()
            if (profile.diamonds < diamondsCost) {
                _toastEvent.emit("Insufficient diamonds! Need $diamondsCost 💎 (You have ${profile.diamonds} 💎). Top up or watch ads.")
                return@launch
            }
            repository.addDiamonds(-diamondsCost)
            repository.boostCommunityVideo(videoId, "SUPER_SPOTLIGHT")
            _toastEvent.emit("🌟 SUPER SPOTLIGHT Activated for your video! Pinned to top of feed!")
        }
    }

    /**
     * Creator Boost & Super Spotlight:
     * Option 2: Boost with Coins
     */
    fun boostVideoWithCoins(videoId: Long, coinsCost: Int = 200) {
        viewModelScope.launch {
            val profile = repository.getProfileOnce()
            if (profile.coins < coinsCost) {
                _toastEvent.emit("Insufficient coins! Need $coinsCost 🪙 (You have ${profile.coins} 🪙).")
                return@launch
            }
            repository.addCoins(-coinsCost)
            repository.boostCommunityVideo(videoId, "CREATOR_BOOST")
            _toastEvent.emit("🚀 CREATOR BOOST Activated! Algorithm reach multiplied by 5X!")
        }
    }

    /**
     * Creator Boost & Super Spotlight:
     * Option 3: Direct Owner UPI Payment (Income Stream for App Owner: Amit2653@ptyes)
     */
    fun boostVideoWithUpi(videoId: Long, planName: String, priceRupees: Int, utr: String) {
        viewModelScope.launch {
            repository.submitPaymentUtr(
                planName = "Creator Spotlight: $planName",
                amountRupees = priceRupees,
                utrNumber = utr.trim(),
                method = "UPI_INTENT"
            )
            repository.boostCommunityVideo(videoId, "SUPER_SPOTLIGHT")
            _toastEvent.emit("⚡ Spotlight Order Submitted via UPI ($priceRupees)! Video Pinned to Super Spotlight 🌟")
        }
    }

    /**
     * Submit / Link User Video from YouTube, Instagram, or GoLiveGram
     */
    fun submitCommunityVideo(title: String, description: String, platform: String, videoType: String, videoUrl: String) {
        viewModelScope.launch {
            val profile = repository.getProfileOnce()
            val newVideo = CommunityVideoPost(
                creatorName = profile.username,
                creatorHandle = "@${profile.username.lowercase().replace(" ", "_")}",
                creatorAvatar = "🎬",
                title = title.trim(),
                description = description.trim(),
                platform = platform.trim(),
                videoType = videoType.trim(),
                videoUrl = videoUrl.trim(),
                durationSeconds = if (videoType == "SHORT") 30 else 90,
                viewsCount = 1,
                likesCount = 0,
                isFollowing = false,
                isBoosted = false,
                boostTier = "STANDARD",
                timestamp = System.currentTimeMillis()
            )
            repository.addCommunityVideo(newVideo)
            repository.rewardVideoWatch(diamonds = 1, coins = 10)
            DiamondRewardParticleController.triggerDiamondRewardBurst(
                diamondsEarned = 1,
                coinsEarned = 10,
                taskLabel = "Video Linked to Reels Hub!"
            )
            _toastEvent.emit("🎬 Video linked & submitted to Creator Reels Hub! +10 Coins 🪙 credited!")
        }
    }

    /**
     * Dedicated Wallet & Rewards Hub: Redeem 150 Diamonds for 10-Day VIP Plan (₹30 Value)
     * Rule: 10 Diamonds = ₹2 | 150 Diamonds = ₹30 Value for 10-Day VIP Plan
     */
    fun redeemDiamondsForVipSubscription() {
        viewModelScope.launch {
            val profile = repository.getProfileOnce()
            val requiredDiamondsForVip = 150
            if (profile.diamonds >= requiredDiamondsForVip) {
                repository.redeemDiamondsForVipSubscription(diamondsCost = requiredDiamondsForVip, durationDays = 10)
                val msg = "बधाई हो! आपने 150 डायमंड्स (₹30 मूल्य) से 10 दिन के लिए VIP प्रो प्लान एक्टिव कर लिया है!"
                _referralNotificationMessage.value = msg
                _toastEvent.emit("💎 $msg")
                _activeReferralAlert.emit(msg)
            } else {
                val msg = "10 दिन का VIP प्लान लेने के लिए कम से कम 150 डायमंड्स चाहिए (आपके पास अभी ${profile.diamonds} हैं)!"
                _referralNotificationMessage.value = msg
                _toastEvent.emit("⚠️ $msg")
            }
        }
    }
}
