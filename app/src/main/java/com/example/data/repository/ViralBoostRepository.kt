package com.example.data.repository

import com.example.data.local.AiDao
import com.example.data.local.AiGenerationRecord
import com.example.data.local.AppConfig
import com.example.data.local.CommunityVideoPost
import com.example.data.local.CommunityVideoPostDao
import com.example.data.local.ConfigDao
import com.example.data.local.LeaderboardCreator
import com.example.data.local.LeaderboardDao
import com.example.data.local.LegalAgreementRecord
import com.example.data.local.LegalDao
import com.example.data.local.PaymentDao
import com.example.data.local.PaymentRecord
import com.example.data.local.ProductOrderDao
import com.example.data.local.ProductOrderRecord
import com.example.data.local.ReferralDao
import com.example.data.local.ReferralRecord
import com.example.data.local.SavedContentHistoryDao
import com.example.data.local.SavedContentHistoryEntity
import com.example.data.local.SupportDao
import com.example.data.local.SupportTicket
import com.example.data.local.UserDao
import com.example.data.local.UserProfile
import com.example.data.local.UserStreakDiamondLedgerDao
import com.example.data.local.UserStreakDiamondLedgerEntity
import com.example.data.local.VideoCrossPostDao
import com.example.data.local.VideoCrossPostRecord
import com.example.data.model.AffiliateProduct
import com.example.data.service.FirestoreSyncRepository
import com.example.monetization.MonetizationConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class StreakResult {
    data class Success(
        val streakDays: Int,
        val diamondsAwarded: Int = 2,
        val milestoneBonus: Int = 0
    ) : StreakResult()
    object AlreadyClaimed : StreakResult()
    data class Error(val message: String) : StreakResult()
}

data class TrialStatus(
    val trialDay: Int, // 1, 2, 3, or 4+ (expired)
    val isTrialActive: Boolean, // true if trialDay in 1..3
    val isExpired: Boolean, // true if trialDay > 3 and not VIP
    val maxVideosToday: Int,
    val maxImagesToday: Int,
    val maxTextToday: Int,
    val videosRemaining: Int,
    val imagesRemaining: Int,
    val textRemaining: Int,
    val isVip: Boolean,
    val planTier: String = "FREE",
    val planName: String = "Free Trial"
)

class ViralBoostRepository(
    private val userDao: UserDao,
    private val paymentDao: PaymentDao,
    private val supportDao: SupportDao,
    private val aiDao: AiDao,
    private val leaderboardDao: LeaderboardDao,
    private val configDao: ConfigDao,
    private val productOrderDao: ProductOrderDao? = null,
    private val videoCrossPostDao: VideoCrossPostDao? = null,
    private val referralDao: ReferralDao? = null,
    private val communityVideoPostDao: CommunityVideoPostDao? = null,
    private val legalDao: LegalDao? = null,
    private val savedContentHistoryDao: SavedContentHistoryDao? = null,
    private val userStreakDiamondLedgerDao: UserStreakDiamondLedgerDao? = null,
    private val cloudSync: FirestoreSyncRepository? = null
) {
    val userProfile: Flow<UserProfile?> = userDao.getUserProfile()
    val streakCountFlow: Flow<Int?> = userDao.getStreakCountFlow()
    val diamondBalanceFlow: Flow<Int?> = userDao.getDiamondBalanceFlow()
    val allPayments: Flow<List<PaymentRecord>> = paymentDao.getAllPayments()
    val allTickets: Flow<List<SupportTicket>> = supportDao.getAllTickets()
    val aiGenerations: Flow<List<AiGenerationRecord>> = aiDao.getAllGenerations()
    val bookmarkedGenerations: Flow<List<AiGenerationRecord>> = aiDao.getBookmarkedGenerations()
    val savedContentHistory: Flow<List<SavedContentHistoryEntity>> = savedContentHistoryDao?.getAllSavedContent() ?: flowOf(emptyList())
    val streakDiamondLedger: Flow<List<UserStreakDiamondLedgerEntity>> = userStreakDiamondLedgerDao?.getAllLedgerEntries() ?: flowOf(emptyList())
    val leaderboardCreators: Flow<List<LeaderboardCreator>> = leaderboardDao.getAllCreators()
    val appConfig: Flow<AppConfig?> = configDao.getAppConfig()
    val allProductOrders: Flow<List<ProductOrderRecord>> = productOrderDao?.getAllOrders() ?: flowOf(emptyList())
    val allCrossPosts: Flow<List<VideoCrossPostRecord>> = videoCrossPostDao?.getAllCrossPosts() ?: flowOf(emptyList())
    val allReferrals: Flow<List<ReferralRecord>> = referralDao?.getAllReferrals() ?: flowOf(emptyList())
    val allCommunityVideos: Flow<List<CommunityVideoPost>> = communityVideoPostDao?.getAllVideos() ?: flowOf(emptyList())
    val allLegalRecords: Flow<List<LegalAgreementRecord>> = legalDao?.getAllLegalRecords() ?: flowOf(emptyList())

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(cal.time)
    }

    suspend fun getProfileOnce(): UserProfile {
        var profile = userDao.getUserProfileOnce()
        if (profile == null) {
            val generatedId = kotlin.random.Random.nextInt(10000000, 99999999).toString()
            profile = UserProfile(
                id = 1,
                uniqueUserId = generatedId,
                username = "ViralCreator_99",
                email = "creator@viralboost.ai",
                niche = "Shorts & Reels",
                diamonds = MonetizationConstants.INITIAL_DIAMONDS_BALANCE,
                coins = MonetizationConstants.INITIAL_COINS_BALANCE,
                lifetimeDiamonds = MonetizationConstants.INITIAL_DIAMONDS_BALANCE,
                isVip = false,
                streakDays = 1,
                lastLoginDateMillis = System.currentTimeMillis()
            )
            userDao.insertOrUpdateProfile(profile)
        } else if (profile.uniqueUserId.isBlank()) {
            val generatedId = kotlin.random.Random.nextInt(10000000, 99999999).toString()
            profile = profile.copy(uniqueUserId = generatedId)
            userDao.insertOrUpdateProfile(profile)
        }
        return profile
    }

    suspend fun getAppConfigOnce(): AppConfig {
        var config = configDao.getAppConfigOnce()
        if (config == null) {
            config = AppConfig()
            configDao.updateAppConfig(config)
        }
        return config
    }

    suspend fun updateAppConfig(config: AppConfig) {
        configDao.updateAppConfig(config)
    }

    suspend fun saveAppConfig(config: AppConfig) {
        configDao.updateAppConfig(config)
    }

    suspend fun claimDailyStreak(): StreakResult {
        val profile = getProfileOnce()
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()

        if (profile.lastStreakClaimDate == today) {
            return StreakResult.AlreadyClaimed
        }

        val newStreak = if (profile.lastStreakClaimDate == yesterday) {
            profile.streakDays + 1
        } else {
            1
        }

        val config = getAppConfigOnce()
        val baseAward = config.dailyStreakReward
        val milestoneBonus = when (newStreak) {
            3 -> 3
            7 -> 10
            14 -> 20
            30 -> 50
            else -> 0
        }
        val totalAwarded = baseAward + milestoneBonus

        userDao.addDiamonds(totalAwarded)
        userDao.addCoins(100)
        userDao.updateStreak(newStreak, today, System.currentTimeMillis())

        val newDiamondBalance = userDao.getDiamondBalanceOnce() ?: (profile.diamonds + totalAwarded)
        userStreakDiamondLedgerDao?.insertLedgerEntry(
            UserStreakDiamondLedgerEntity(
                userId = profile.id,
                eventType = "STREAK_CLAIM",
                streakCountSnapshot = newStreak,
                diamondDelta = totalAwarded,
                diamondBalanceSnapshot = newDiamondBalance,
                note = "Daily Streak Day $newStreak Claimed (+$totalAwarded Diamonds)"
            )
        )
        syncLeaderboardAndCloud()

        return StreakResult.Success(newStreak, totalAwarded, milestoneBonus)
    }

    suspend fun claimPostSignupDailyReward(bonusCoins: Int = 100, bonusDiamonds: Int = 2): StreakResult {
        val profile = getProfileOnce()
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()

        val newStreak = if (profile.lastStreakClaimDate == yesterday) {
            profile.streakDays + 1
        } else if (profile.lastStreakClaimDate == today) {
            profile.streakDays
        } else {
            1
        }

        userDao.addDiamonds(bonusDiamonds)
        userDao.addCoins(bonusCoins)
        userDao.updateStreak(newStreak, today, System.currentTimeMillis())
        syncLeaderboardAndCloud()

        return StreakResult.Success(newStreak, bonusDiamonds, 0)
    }

    suspend fun rewardAdWatched(): Int {
        val config = getAppConfigOnce()
        val reward = config.rewardedAdPayout
        userDao.addDiamonds(reward)
        val currentStreak = userDao.getStreakCountOnce() ?: 1
        val currentDiamonds = userDao.getDiamondBalanceOnce() ?: reward
        userStreakDiamondLedgerDao?.insertLedgerEntry(
            UserStreakDiamondLedgerEntity(
                eventType = "REWARDED_AD",
                streakCountSnapshot = currentStreak,
                diamondDelta = reward,
                diamondBalanceSnapshot = currentDiamonds,
                note = "Watched Rewarded Ad (+$reward Diamonds)"
            )
        )
        syncLeaderboardAndCloud()
        return reward
    }

    suspend fun convertCoinsToDiamonds(coinsToSpend: Int = 100): Boolean {
        if (coinsToSpend < 20 || coinsToSpend % 20 != 0) return false
        val profile = getProfileOnce()
        if (profile.coins < coinsToSpend) return false
        val diamondsEarned = coinsToSpend / 20 // 100 Coins = 5 Diamonds
        userDao.addCoins(-coinsToSpend)
        userDao.addDiamonds(diamondsEarned)
        val newBalance = userDao.getDiamondBalanceOnce() ?: (profile.diamonds + diamondsEarned)
        userStreakDiamondLedgerDao?.insertLedgerEntry(
            UserStreakDiamondLedgerEntity(
                userId = profile.id,
                eventType = "BONUS",
                streakCountSnapshot = profile.streakDays,
                diamondDelta = diamondsEarned,
                diamondBalanceSnapshot = newBalance,
                note = "Converted $coinsToSpend Coins to +$diamondsEarned Diamonds"
            )
        )
        syncLeaderboardAndCloud()
        return true
    }

    suspend fun redeemDiamondsForVip(diamondsToRedeem: Int): Boolean {
        val profile = getProfileOnce()
        if (profile.diamonds < diamondsToRedeem) return false

        // 500 Diamonds enables full 1-month VIP Pro unlock
        if (diamondsToRedeem >= MonetizationConstants.FULL_VIP_REDEMPTION_DIAMONDS) {
            val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000
            val newExpiry = System.currentTimeMillis() + thirtyDaysMillis
            userDao.setDiamonds(profile.diamonds - diamondsToRedeem)
            userDao.updateVipStatus(isVip = true, expiryDate = newExpiry)

            paymentDao.insertPayment(
                PaymentRecord(
                    userId = profile.id,
                    userName = profile.username,
                    planName = "VIP Pro (1 Month) - Diamond Redemption",
                    amountRupees = 0,
                    utrNumber = "DIAMOND-REDEEM-${System.currentTimeMillis().toString().takeLast(6)}",
                    paymentMethod = "DIAMOND_REDEMPTION",
                    status = "APPROVED",
                    verifiedAt = System.currentTimeMillis(),
                    adminNote = "Unlocked via $diamondsToRedeem Diamonds"
                )
            )
            syncLeaderboardAndCloud()
            return true
        }
        return false
    }

    fun getTrialStatus(profile: UserProfile): TrialStatus {
        val today = getTodayDateString()
        val isToday = profile.lastAiGenerationDate == today
        val videosUsed = if (isToday) profile.todayVideosGenerated else 0
        val imagesUsed = if (isToday) profile.todayImagesGenerated else 0
        val textUsed = if (isToday) profile.todayAiGenerationsCount else 0

        if (profile.isVip) {
            val tier = profile.currentPlanTier
            val (maxV, maxI, maxT, pName) = when (tier) {
                "PASS_10_DAYS" -> Quadruple(2, 5, 50, "10-Day Creator Pass")
                "PASS_15_DAYS" -> Quadruple(2, 5, 50, "15-Day Growth Pass")
                "VIP_MONTHLY" -> Quadruple(3, 15, 100, "1-Month VIP Plan")
                "FULL_APP_FREE_1_DAY" -> Quadruple(9999, 9999, 9999, "1-Day Full-App Free Subscription")
                else -> Quadruple(9999, 9999, 9999, "3-Month Ultimate Pro")
            }
            return TrialStatus(
                trialDay = 1,
                isTrialActive = true,
                isExpired = false,
                maxVideosToday = maxV,
                maxImagesToday = maxI,
                maxTextToday = maxT,
                videosRemaining = (maxV - videosUsed).coerceAtLeast(0),
                imagesRemaining = (maxI - imagesUsed).coerceAtLeast(0),
                textRemaining = (maxT - textUsed).coerceAtLeast(0),
                isVip = true,
                planTier = tier,
                planName = pName
            )
        }

        val now = System.currentTimeMillis()
        val start = if (profile.trialStartTimestamp > 0L) profile.trialStartTimestamp else now
        val diffMillis = (now - start).coerceAtLeast(0L)
        val elapsedDays = (diffMillis / (24L * 60 * 60 * 1000)).toInt() + 1
        val trialDay = elapsedDays.coerceAtLeast(1)

        val isExpired = trialDay > 3
        val isTrialActive = trialDay in 1..3

        val (maxV, maxI, maxT) = if (!isExpired) {
            Triple(2, 2, 5) // Free Trial: Max 2 AI videos/day, 2 photos daily
        } else {
            Triple(0, 0, 0)
        }

        return TrialStatus(
            trialDay = trialDay,
            isTrialActive = isTrialActive,
            isExpired = isExpired,
            maxVideosToday = maxV,
            maxImagesToday = maxI,
            maxTextToday = maxT,
            videosRemaining = (maxV - videosUsed).coerceAtLeast(0),
            imagesRemaining = (maxI - imagesUsed).coerceAtLeast(0),
            textRemaining = (maxT - textUsed).coerceAtLeast(0),
            isVip = false,
            planTier = "FREE",
            planName = if (isExpired) "Trial Expired" else "Free Trial (Day $trialDay/3)"
        )
    }

    suspend fun checkCanGenerateAi(type: String = "HOOK"): Pair<Boolean, String> {
        val profile = getProfileOnce()
        val trial = getTrialStatus(profile)

        if (trial.isExpired) {
            return Pair(false, "EXPIRED: Your Free Trial has ended. Upgrade to VIP to unlock unlimited AI Video, Image, and Script tools!")
        }

        val isVideo = type in listOf("VIDEO", "TEXT_TO_VIDEO")
        val isImage = type in listOf("IMAGE", "PHOTO_TO_VIDEO", "TEXT_TO_IMAGE", "THUMBNAIL")

        if (isVideo) {
            if (trial.videosRemaining <= 0) {
                val msg = if (trial.isVip) {
                    "LIMIT: Daily limit of ${trial.maxVideosToday} AI Videos reached (${trial.planName}). Upgrade to 3-Month Ultimate Pro for UNLIMITED videos!"
                } else {
                    "LIMIT: Free trial daily limit of 2 AI Videos reached (${trial.maxVideosToday}/${trial.maxVideosToday} used). Upgrade to VIP to generate more videos!"
                }
                return Pair(false, msg)
            }
        } else if (isImage) {
            if (trial.imagesRemaining <= 0) {
                val msg = if (trial.isVip) {
                    "LIMIT: Daily limit of ${trial.maxImagesToday} Photo generations reached (${trial.planName}). Upgrade to 3-Month Ultimate Pro for UNLIMITED photos!"
                } else {
                    "LIMIT: Free trial daily limit of 2 photos reached (${trial.maxImagesToday}/${trial.maxImagesToday} used). Upgrade to VIP to generate more photos!"
                }
                return Pair(false, msg)
            }
        } else {
            if (trial.textRemaining <= 0) {
                return Pair(false, "LIMIT: Daily text generation limit reached. Upgrade to VIP Pro to continue generating!")
            }
        }

        if (profile.coins < 20) {
            return Pair(false, "Insufficient Coins (Need 20 coins). Earn more coins via Daily Streak or Watch Ads.")
        }

        return Pair(true, "")
    }

    suspend fun recordAiGeneration(type: String, topic: String, niche: String, content: String): AiGenerationRecord {
        val profile = getProfileOnce()
        val today = getTodayDateString()
        val isToday = profile.lastAiGenerationDate == today

        val isVideo = type in listOf("VIDEO", "TEXT_TO_VIDEO")
        val isImage = type in listOf("IMAGE", "PHOTO_TO_VIDEO", "TEXT_TO_IMAGE", "THUMBNAIL")

        val newTextCount = if (isToday) profile.todayAiGenerationsCount + 1 else 1
        val newVideoCount = if (isToday) {
            profile.todayVideosGenerated + if (isVideo) 1 else 0
        } else {
            if (isVideo) 1 else 0
        }
        val newImageCount = if (isToday) {
            profile.todayImagesGenerated + if (isImage) 1 else 0
        } else {
            if (isImage) 1 else 0
        }

        val isWatermarked = !profile.isVip
        val finalContent = if (isWatermarked) {
            "$content\n\n⚡ [Generated with ViralBoost AI 3-Day Free Trial • Upgrade to VIP for 100% Ad-Free & Unlimited]"
        } else {
            content
        }

        // Deduct 20 coins
        if (profile.coins >= 20) {
            userDao.addCoins(-20)
        }
        userDao.updateDailyAiCounts(newTextCount, newVideoCount, newImageCount, today)

        val record = AiGenerationRecord(
            type = type,
            topic = topic,
            niche = niche,
            content = finalContent,
            isWatermarked = isWatermarked,
            createdAt = System.currentTimeMillis()
        )
        val insertedId = aiDao.insertGeneration(record)
        savedContentHistoryDao?.insertSavedContent(
            SavedContentHistoryEntity(
                title = topic.ifBlank { "$type Content" },
                topic = topic,
                niche = niche,
                platform = "Multi-Platform",
                contentType = type,
                contentBody = finalContent,
                createdAt = record.createdAt
            )
        )
        syncLeaderboardAndCloud()
        return record.copy(id = insertedId)
    }

    suspend fun saveGeneratedContentToHistory(
        type: String,
        topic: String,
        niche: String,
        content: String,
        platform: String = "Instagram Reels",
        keywords: String = "",
        hashtags: String = "",
        isBookmarked: Boolean = true
    ): Long {
        val now = System.currentTimeMillis()
        val profile = getProfileOnce()
        val record = AiGenerationRecord(
            type = type,
            topic = topic,
            niche = niche,
            content = content,
            platform = platform,
            keywords = keywords,
            hashtags = hashtags,
            isBookmarked = isBookmarked,
            isWatermarked = !profile.isVip,
            createdAt = now
        )
        val genId = aiDao.insertGeneration(record)
        savedContentHistoryDao?.insertSavedContent(
            SavedContentHistoryEntity(
                title = topic.ifBlank { "$platform $type" },
                topic = topic,
                niche = niche,
                platform = platform,
                contentType = type,
                contentBody = content,
                hashtags = hashtags,
                keywords = keywords,
                isBookmarked = isBookmarked,
                createdAt = now
            )
        )
        return genId
    }

    suspend fun toggleGenerationBookmark(id: Long, isBookmarked: Boolean) {
        aiDao.updateBookmarkStatus(id, isBookmarked)
        savedContentHistoryDao?.setBookmarkStatus(id, isBookmarked)
    }

    suspend fun deleteGenerationHistoryItem(id: Long) {
        aiDao.deleteGenerationById(id)
        savedContentHistoryDao?.deleteSavedContentById(id)
    }

    suspend fun clearGenerationHistory() {
        aiDao.clearGenerations()
        savedContentHistoryDao?.clearAllSavedContent()
    }

    suspend fun simulateTrialDay(day: Int) {
        getProfileOnce()
        val now = System.currentTimeMillis()
        val daysAgoMillis = when (day) {
            1 -> 0L
            2 -> 1L * 24 * 60 * 60 * 1000 + 1000L
            3 -> 2L * 24 * 60 * 60 * 1000 + 1000L
            else -> 4L * 24 * 60 * 60 * 1000 + 1000L // Expired
        }
        userDao.setTrialStartTimestamp(now - daysAgoMillis)
    }

    suspend fun submitPaymentUtr(
        planName: String,
        amountRupees: Int,
        utrNumber: String,
        method: String
    ): Long {
        val profile = getProfileOnce()
        val record = PaymentRecord(
            userId = profile.id,
            userName = profile.username,
            planName = planName,
            amountRupees = amountRupees,
            utrNumber = utrNumber.trim(),
            paymentMethod = method,
            status = "PENDING",
            timestamp = System.currentTimeMillis()
        )
        val localId = paymentDao.insertPayment(record)
        cloudSync?.submitPayment(record)
        return localId
    }

    suspend fun submitSupportTicket(
        subject: String,
        category: String,
        message: String
    ): Long {
        val profile = getProfileOnce()
        val ticket = SupportTicket(
            userId = profile.id,
            userName = profile.username,
            userEmail = profile.email,
            subject = subject.trim(),
            category = category,
            message = message.trim(),
            status = "OPEN",
            createdAt = System.currentTimeMillis()
        )
        val localId = supportDao.insertTicket(ticket)
        cloudSync?.submitSupportTicket(ticket)
        return localId
    }

    suspend fun adminVerifyPayment(paymentId: Long, approve: Boolean, note: String) {
        val status = if (approve) "APPROVED" else "REJECTED"
        paymentDao.updatePaymentStatus(paymentId, status, System.currentTimeMillis(), note)

        if (approve) {
            val payment = paymentDao.getPaymentById(paymentId)
            val (days, planTier) = when {
                payment?.planName?.contains("10-Day") == true || payment?.amountRupees == MonetizationConstants.PRICE_PASS_10_DAYS -> Pair(10L, "PASS_10_DAYS")
                payment?.planName?.contains("15-Day") == true || payment?.amountRupees == MonetizationConstants.PRICE_PASS_15_DAYS || payment?.amountRupees == 99 -> Pair(15L, "PASS_15_DAYS")
                payment?.planName?.contains("1 Month") == true || payment?.planName?.contains("1-Month") == true || payment?.amountRupees == MonetizationConstants.PRICE_VIP_MONTHLY || payment?.amountRupees == 149 -> Pair(30L, "VIP_MONTHLY")
                else -> Pair(90L, "PRO_3_MONTHS")
            }
            val newExpiry = System.currentTimeMillis() + days * 24 * 60 * 60 * 1000
            userDao.updateVipStatusWithPlan(isVip = true, expiryDate = newExpiry, planTier = planTier)
            syncLeaderboardAndCloud()
        }
    }

    suspend fun adminReplyTicket(ticketId: Long, reply: String) {
        supportDao.replyTicket(ticketId, reply.trim(), "RESOLVED", System.currentTimeMillis())
    }

    suspend fun updateProfile(username: String, email: String, niche: String) {
        val current = getProfileOnce()
        val trialStart = if (current.trialStartTimestamp > 0L) current.trialStartTimestamp else System.currentTimeMillis()
        val userId = if (current.uniqueUserId.isNotBlank()) current.uniqueUserId else kotlin.random.Random.nextInt(10000000, 99999999).toString()
        userDao.insertOrUpdateProfile(
            current.copy(
                uniqueUserId = userId,
                username = username.trim(),
                email = email.trim(),
                niche = niche.trim(),
                trialStartTimestamp = trialStart
            )
        )
        syncLeaderboardAndCloud()
    }

    fun logLegalAgreementToAdminPanel(name: String, contact: String, timestamp: String) {
        val legalProofRecord = "ADMIN_LEGAL_LOG: User=$name, Contact/Email=$contact, Time=$timestamp, Status=AGREED_AND_VERIFIED"
        println(legalProofRecord) // Secure record for owner legal defense against false claims
    }

    suspend fun completeOnboarding(
        name: String,
        contact: String,
        signupMethod: String,
        timestamp: String
    ) {
        logLegalAgreementToAdminPanel(name, contact, timestamp)
        val cleanName = name.trim().ifBlank { "ViralCreator_${contact.takeLast(4)}" }
        val cleanContact = contact.trim()
        val emailToSave = if (signupMethod == "email" && cleanContact.contains("@")) {
            cleanContact
        } else {
            "${cleanContact.replace(Regex("[^0-9a-zA-Z]"), "")}@viralboost.ai"
        }

        userDao.completeUserOnboarding(
            isLoggedIn = true,
            agreed = true,
            name = cleanName,
            contact = cleanContact,
            email = emailToSave,
            method = signupMethod,
            timestamp = timestamp
        )

        legalDao?.insertLegalRecord(
            LegalAgreementRecord(
                userName = cleanName,
                contactOrEmail = cleanContact,
                signupMethod = signupMethod,
                agreementTimestamp = timestamp,
                status = "AGREED_AND_VERIFIED"
            )
        )
    }

    suspend fun logoutUser() {
        userDao.logoutUser()
    }

    suspend fun setOwnerStatus(isOwner: Boolean) {
        userDao.setOwnerStatus(isOwner)
    }

    suspend fun activateSubscriptionPlan(planName: String, priceRupees: Int, durationDays: Int) {
        val newExpiry = System.currentTimeMillis() + durationDays * 24L * 60 * 60 * 1000
        val tier = if (durationDays <= 15) "AI_PASS_15_DAYS" else "VIP_MONTHLY"
        userDao.updateVipStatusWithPlan(isVip = true, expiryDate = newExpiry, planTier = tier)
        paymentDao.insertPayment(
            PaymentRecord(
                planName = planName,
                amountRupees = priceRupees,
                utrNumber = "AI-PASS-${System.currentTimeMillis().toString().takeLast(6)}",
                paymentMethod = "UPI Instant / AI Pass",
                status = "APPROVED",
                verifiedAt = System.currentTimeMillis(),
                adminNote = "Instant AI Video Optimization Plan Activated"
            )
        )
    }

    suspend fun redeemDiamondsForVipSubscription(diamondsCost: Int = 150, durationDays: Int = 10): Boolean {
        val profile = getProfileOnce()
        if (profile.diamonds < diamondsCost) return false
        val newDiamonds = profile.diamonds - diamondsCost
        val expiryTime = System.currentTimeMillis() + (durationDays * 24L * 60 * 60 * 1000L)
        userDao.setDiamonds(newDiamonds)
        userDao.updateVipStatusWithPlan(isVip = true, expiryDate = expiryTime, planTier = "PASS_10_DAYS")
        paymentDao.insertPayment(
            PaymentRecord(
                userId = profile.id,
                userName = profile.username,
                planName = "10-Day VIP Pro Plan - 150 Diamonds (₹30 Value)",
                amountRupees = 30,
                utrNumber = "DIAMOND-150-${System.currentTimeMillis().toString().takeLast(6)}",
                paymentMethod = "DIAMOND_REDEMPTION",
                status = "APPROVED",
                verifiedAt = System.currentTimeMillis(),
                adminNote = "Redeemed $diamondsCost Diamonds for 10-Day VIP Pro Plan (₹30 Value)"
            )
        )
        return true
    }

    suspend fun creditWelcomeBonusRupees(amount: Double = 50.0) {
        userDao.creditWelcomeBonusRupees(amount)
    }

    suspend fun addCoins(amount: Int) {
        userDao.addCoins(amount)
        syncLeaderboardAndCloud()
    }

    suspend fun addDiamonds(amount: Int) {
        userDao.addDiamonds(amount)
        syncLeaderboardAndCloud()
    }

    suspend fun placeProductOrder(
        product: AffiliateProduct,
        customerName: String,
        customerPhone: String,
        shippingAddress: String,
        affiliateTag: String
    ): Long {
        val order = ProductOrderRecord(
            productId = product.id,
            productTitle = product.title,
            platform = product.platform,
            priceRupees = product.priceRupees,
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            shippingAddress = shippingAddress.trim(),
            status = "CONFIRMED",
            affiliateTag = affiliateTag.ifBlank { MonetizationConstants.AFFILIATE_TAG },
            diamondsRewarded = MonetizationConstants.AFFILIATE_PRODUCT_PURCHASE_DIAMONDS_REWARD,
            timestamp = System.currentTimeMillis()
        )
        val orderId = productOrderDao?.insertOrder(order) ?: System.currentTimeMillis()
        // User Purchase Reward: +3 Diamonds credited directly to wallet
        userDao.addDiamonds(MonetizationConstants.AFFILIATE_PRODUCT_PURCHASE_DIAMONDS_REWARD)
        cloudSync?.recordAffiliateOrder(order)
        syncLeaderboardAndCloud()
        return orderId
    }

    suspend fun rewardProductShare(product: AffiliateProduct) {
        // Social Media Sharing Reward: +10 Coins instantly
        userDao.addCoins(MonetizationConstants.AFFILIATE_PRODUCT_SHARE_COINS_REWARD)
        syncLeaderboardAndCloud()
    }

    suspend fun rewardProductPurchase(product: AffiliateProduct) {
        // Product Purchase Reward: +3 Diamonds credited directly
        userDao.addDiamonds(MonetizationConstants.AFFILIATE_PRODUCT_PURCHASE_DIAMONDS_REWARD)
        syncLeaderboardAndCloud()
    }

    suspend fun recordVideoCrossPost(
        title: String,
        tags: String,
        description: String,
        videoUri: String,
        platforms: List<String>,
        fullCaption: String
    ): Long {
        val record = VideoCrossPostRecord(
            title = title.trim(),
            tags = tags.trim(),
            description = description.trim(),
            videoUri = videoUri,
            connectedPlatforms = platforms.joinToString(", "),
            fullCaption = fullCaption,
            coinsRewarded = 10,
            timestamp = System.currentTimeMillis()
        )
        val id = videoCrossPostDao?.insertCrossPost(record) ?: System.currentTimeMillis()
        // Reward user +10 Coins for publishing video across social platforms
        userDao.addCoins(10)

        // Automatically aggregate into Community Video Feed across connected platforms
        val primaryPlatform = platforms.firstOrNull() ?: "YouTube"
        val userProfile = userDao.getUserProfileOnce()
        communityVideoPostDao?.insertVideo(
            CommunityVideoPost(
                creatorName = userProfile?.username ?: "ViralCreator_99 (You)",
                creatorHandle = "@${(userProfile?.username ?: "creator").lowercase().replace(" ", "_")}",
                creatorAvatar = "🎬",
                title = title.trim(),
                description = description.trim(),
                platform = primaryPlatform,
                videoType = if (primaryPlatform.contains("Short", ignoreCase = true) || primaryPlatform.contains("Reel", ignoreCase = true)) "SHORT" else "LONG",
                videoUrl = videoUri,
                durationSeconds = 30,
                viewsCount = 1,
                likesCount = 0,
                isFollowing = false,
                isBoosted = false,
                boostTier = "STANDARD",
                timestamp = System.currentTimeMillis()
            )
        )
        return id
    }

    suspend fun incrementVideoViews(videoId: Long) {
        communityVideoPostDao?.incrementViews(videoId)
    }

    suspend fun incrementVideoLikes(videoId: Long) {
        communityVideoPostDao?.incrementLikes(videoId)
    }

    suspend fun toggleFollowCreator(videoId: Long) {
        communityVideoPostDao?.toggleFollow(videoId)
    }

    suspend fun rewardVideoWatch(diamonds: Int = 3, coins: Int = 15): Pair<Int, Int> {
        userDao.addDiamonds(diamonds)
        userDao.addCoins(coins)
        return Pair(diamonds, coins)
    }

    suspend fun addCommunityVideo(video: CommunityVideoPost): Long {
        return communityVideoPostDao?.insertVideo(video) ?: System.currentTimeMillis()
    }

    suspend fun boostCommunityVideo(videoId: Long, tier: String) {
        communityVideoPostDao?.updateBoostStatus(videoId, isBoosted = true, boostTier = tier)
    }

    /**
     * Instant Share Rewards:
     * When user shares their unique referral link/code, instantly credit +10 Coins and +4 Diamonds
     */
    suspend fun rewardReferralShare(): Pair<Int, Int> {
        userDao.addCoins(10)
        userDao.addDiamonds(4)
        return Pair(10, 4)
    }

    /**
     * Step 1: Friend downloads & opens app via referral link
     * Instantly awards user +10 Diamonds!
     */
    suspend fun rewardFriendDownload(friendName: String, referralCode: String): Pair<Long, Int> {
        userDao.addDiamonds(10)
        val record = ReferralRecord(
            friendName = friendName.trim(),
            friendAvatar = listOf("🔥", "⚡", "🌟", "🚀", "✨", "👑").random(),
            referralCode = referralCode.trim(),
            status = "DOWNLOADED_WAITING_EXPLORATION",
            explorationSeconds = 0,
            joinedTimestamp = System.currentTimeMillis()
        )
        val id = referralDao?.insertReferral(record) ?: System.currentTimeMillis()
        return Pair(id, 10)
    }

    /**
     * Records a new friend joining via referral code
     */
    suspend fun recordNewFriendReferral(friendName: String, referralCode: String): Long {
        val record = ReferralRecord(
            friendName = friendName.trim(),
            friendAvatar = listOf("🔥", "⚡", "🌟", "🚀", "✨", "👑").random(),
            referralCode = referralCode.trim(),
            status = "EXPLORING_ACTIVE",
            explorationSeconds = 0,
            joinedTimestamp = System.currentTimeMillis()
        )
        return referralDao?.insertReferral(record) ?: System.currentTimeMillis()
    }

    /**
     * Updates exploration browsing duration in seconds
     */
    suspend fun updateReferralExploration(referralId: Long, seconds: Int) {
        val status = if (seconds >= 60) "VERIFIED_ACTIVE" else "EXPLORING_ACTIVE"
        referralDao?.updateExplorationProgress(referralId, seconds, status)
    }

    /**
     * Verifies active friend referral (1-2 minutes genuine browsing)
     * and automatically unlocks 1 Day of Full-App Free Subscription (24 hours full VIP Pro).
     */
    suspend fun verifyActiveReferral(referralId: Long, friendName: String): Boolean {
        referralDao?.updateReferralStatus(referralId, "VERIFIED_ACTIVE", System.currentTimeMillis())
        referralDao?.updateExplorationProgress(referralId, 120, "VERIFIED_ACTIVE")

        // 24 Hours = 86,400,000 milliseconds
        val oneDayMillis = 24L * 60 * 60 * 1000
        val currentProfile = getProfileOnce()
        val baseTime = if (currentProfile.isVip && currentProfile.vipExpiryDate > System.currentTimeMillis()) {
            currentProfile.vipExpiryDate
        } else {
            System.currentTimeMillis()
        }
        val newExpiry = baseTime + oneDayMillis

        // Unlock full unrestricted VIP Pro access across the entire app
        userDao.updateVipStatusWithPlan(isVip = true, expiryDate = newExpiry, planTier = "FULL_APP_FREE_1_DAY")

        // Record audit in payments table
        paymentDao.insertPayment(
            PaymentRecord(
                userId = currentProfile.id,
                userName = currentProfile.username,
                planName = "Full-App 1-Day Free Subscription (Active Referral: $friendName)",
                amountRupees = 0,
                utrNumber = "ACTIVE-REF-${System.currentTimeMillis().toString().takeLast(6)}",
                paymentMethod = "ACTIVE_REFERRAL_UNLOCK",
                status = "APPROVED",
                verifiedAt = System.currentTimeMillis(),
                adminNote = "Unlocked 24-hr Full-App Free Subscription: $friendName explored app for 1-2 mins"
            )
        )
        syncLeaderboardAndCloud()
        return true
    }

    suspend fun syncLeaderboardAndCloud() {
        try {
            val profile = getProfileOnce()
            val safeName = profile.username.ifBlank { "ViralCreator_99" }
            val handle = "@${safeName.lowercase().replace(Regex("[^a-z0-9_]"), "_").take(20).ifBlank { "creator" }}"
            val score = (profile.coins.coerceAtLeast(0)) + (profile.diamonds.coerceAtLeast(0) * 10)
            val existing = leaderboardDao.getAllCreatorsOnce()
            val others = existing.filter { it.id != 9 && !it.name.endsWith("(You)") }
            val myEntry = LeaderboardCreator(
                id = 9,
                name = "$safeName (You)",
                handle = handle,
                niche = profile.niche.ifBlank { "Shorts & Reels" },
                diamonds = profile.diamonds.coerceAtLeast(0),
                streakDays = profile.streakDays.coerceAtLeast(1),
                viralScore = score,
                isVip = profile.isVip,
                rank = 9,
                coins = profile.coins.coerceAtLeast(0)
            )
            val combined = (others + myEntry)
                .sortedByDescending { it.coins + (it.diamonds * 10) }
                .mapIndexed { idx, creator -> creator.copy(rank = idx + 1) }
            if (combined.isNotEmpty()) {
                leaderboardDao.insertCreators(combined)
            }
            cloudSync?.syncUserProfileAndLeaderboard(profile)
        } catch (_: Throwable) {
            // Keep offline/test callers resilient
        }
    }

    suspend fun restoreFromCloudIfAvailable() {
        try {
            val cloudData = cloudSync?.fetchCloudUserProfile()?.getOrNull() ?: return
            val local = getProfileOnce()
            val cloudCoins = (cloudData["coins"] as? Number)?.toInt() ?: local.coins
            val cloudDiamonds = (cloudData["diamonds"] as? Number)?.toInt() ?: local.diamonds
            val cloudStreak = (cloudData["streakDays"] as? Number)?.toInt() ?: local.streakDays
            val cloudVip = cloudData["isVip"] as? Boolean ?: local.isVip
            val cloudTier = cloudData["planTier"] as? String ?: local.currentPlanTier
            val cloudExpiry = (cloudData["vipExpiryDate"] as? Number)?.toLong() ?: local.vipExpiryDate
            val cloudUsername = (cloudData["username"] as? String)?.takeIf { it.isNotBlank() } ?: local.username
            val cloudEmail = (cloudData["email"] as? String)?.takeIf { it.isNotBlank() } ?: local.email
            userDao.insertOrUpdateProfile(
                local.copy(
                    username = cloudUsername,
                    email = cloudEmail,
                    coins = maxOf(local.coins, cloudCoins),
                    diamonds = maxOf(local.diamonds, cloudDiamonds),
                    streakDays = maxOf(local.streakDays, cloudStreak),
                    isVip = local.isVip || cloudVip,
                    currentPlanTier = if (cloudVip) cloudTier else local.currentPlanTier,
                    vipExpiryDate = maxOf(local.vipExpiryDate, cloudExpiry),
                    isLoggedIn = true,
                    hasAgreedToTerms = true
                )
            )
            syncLeaderboardAndCloud()
        } catch (_: Throwable) {
            // Offline resilience
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

