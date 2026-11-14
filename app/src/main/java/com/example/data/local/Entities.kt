package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val uniqueUserId: String = "74829156",
    val username: String = "Creator",
    val email: String = "creator@viralboost.ai",
    val niche: String = "Shorts & Reels",
    val diamonds: Int = 10,
    val coins: Int = 250,
    val bonusRupees: Double = 0.0,
    val hasClaimedWelcomeBonus: Boolean = false,
    val lifetimeDiamonds: Int = 10,
    val isVip: Boolean = false,
    val vipExpiryDate: Long = 0L,
    val streakDays: Int = 1,
    val lastLoginDateMillis: Long = System.currentTimeMillis(),
    val lastStreakClaimDate: String = "",
    val todayAiGenerationsCount: Int = 0,
    val lastAiGenerationDate: String = "",
    val isOwner: Boolean = false,
    val trialStartTimestamp: Long = System.currentTimeMillis(),
    val todayVideosGenerated: Int = 0,
    val todayImagesGenerated: Int = 0,
    val currentPlanTier: String = "FREE",
    val isLoggedIn: Boolean = false,
    val hasAgreedToTerms: Boolean = false,
    val authMethod: String = "mobile", // "mobile" or "email"
    val userContact: String = "",
    val legalAgreementTimestamp: String = "",
    val legalAgreementStatus: String = "PENDING"
) {
    val displayUserId: String
        get() = if (uniqueUserId.isNotBlank()) uniqueUserId else "${Math.abs(username.hashCode() % 90000000) + 10000000}"
}

@Entity(tableName = "legal_agreement_logs")
data class LegalAgreementRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userName: String,
    val contactOrEmail: String,
    val signupMethod: String,
    val agreementTimestamp: String,
    val status: String = "AGREED_AND_VERIFIED",
    val termsSummary: String = "I Agree to Terms & Conditions. I understand rewards and deals are provided as-is, releasing the owner from any liability or false claims."
)

@Entity(tableName = "payment_records")
data class PaymentRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Int = 1,
    val userName: String = "Creator",
    val planName: String = "VIP Pro (1 Month)",
    val amountRupees: Int = 199,
    val utrNumber: String = "",
    val paymentMethod: String = "UPI_INTENT", // UPI_INTENT or QR_CODE_MANUAL
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val timestamp: Long = System.currentTimeMillis(),
    val verifiedAt: Long = 0L,
    val adminNote: String = ""
)

@Entity(tableName = "support_tickets")
data class SupportTicket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Int = 1,
    val userName: String = "Creator",
    val userEmail: String = "creator@viralboost.ai",
    val subject: String = "",
    val category: String = "General Support",
    val message: String = "",
    val status: String = "OPEN", // OPEN, RESOLVED
    val adminReply: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val repliedAt: Long = 0L
)

@Entity(tableName = "ai_generations")
data class AiGenerationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String = "HOOK", // HOOK, SCRIPT, TAGS, AFFILIATE, GEMINI_SOCIAL
    val topic: String = "",
    val niche: String = "Reels & Shorts",
    val content: String = "",
    val platform: String = "Multi-Platform",
    val keywords: String = "",
    val hashtags: String = "",
    val isBookmarked: Boolean = false,
    val isWatermarked: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_content_history")
data class SavedContentHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String = "",
    val topic: String = "",
    val niche: String = "Shorts & Reels",
    val platform: String = "Instagram Reels",
    val contentType: String = "SCRIPT", // HOOK, SCRIPT, TAGS, CAPTION, GEMINI_POST
    val contentBody: String = "",
    val hashtags: String = "",
    val keywords: String = "",
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_streak_diamond_ledger")
data class UserStreakDiamondLedgerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Int = 1,
    val eventType: String = "STREAK_CLAIM", // STREAK_CLAIM, REWARDED_AD, REDEEM_VIP, BONUS
    val streakCountSnapshot: Int = 1,
    val diamondDelta: Int = 0,
    val diamondBalanceSnapshot: Int = 10,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "leaderboard_creators")
data class LeaderboardCreator(
    @PrimaryKey val id: Int,
    val name: String,
    val handle: String,
    val niche: String,
    val diamonds: Int,
    val streakDays: Int,
    val viralScore: Int,
    val isVip: Boolean,
    val rank: Int,
    val coins: Int = diamonds * 10
)

@Entity(tableName = "app_config")
data class AppConfig(
    @PrimaryKey val id: Int = 1,
    val ownerUpiId: String = "Amit2653@ptyes",
    val ownerName: String = "ViralBoost AI Admin",
    val ownerQrUrl: String = "https://i.postimg.cc/v8gdCzFm/IMG-20260926-151326.jpg",
    val admobPublisherId: String = "ca-app-pub-3976359691019357",
    val admobBannerUnitId: String = "ca-app-pub-3940256099942544/6300978111",
    val admobInterstitialUnitId: String = "ca-app-pub-3940256099942544/1033173712",
    val admobRewardedUnitId: String = "ca-app-pub-3940256099942544/5224354917",
    val admobNativeUnitId: String = "ca-app-pub-3940256099942544/2247696110",
    val admobNativeVideoUnitId: String = "ca-app-pub-3940256099942544/1044960115",
    val unityGameId: String = "800427608",
    val unityBannerPlacementId: String = "BP_Banner_Android",
    val unityInterstitialPlacementId: String = "BP_Interstitial_Android",
    val unityRewardedPlacementId: String = "BP_Rewarded_Android",
    val affiliateTag: String = "5665937",
    val appDownloadUrl: String = "https://viralboost.ai/download?ref=5665937",
    val dailyStreakReward: Int = 2,
    val rewardedAdPayout: Int = 5
)

@Entity(tableName = "product_orders")
data class ProductOrderRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val productId: String,
    val productTitle: String,
    val platform: String, // Meesho, Flipkart, Amazon
    val priceRupees: Int,
    val customerName: String,
    val customerPhone: String,
    val shippingAddress: String,
    val status: String = "CONFIRMED", // CONFIRMED, SHIPPED, DELIVERED
    val affiliateTag: String = "5665937",
    val diamondsRewarded: Int = 3,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "video_crossposts")
data class VideoCrossPostRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val tags: String,
    val description: String,
    val videoUri: String = "",
    val connectedPlatforms: String = "",
    val fullCaption: String = "",
    val coinsRewarded: Int = 10,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "referral_records")
data class ReferralRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val friendName: String,
    val friendAvatar: String = "👤",
    val referralCode: String,
    val status: String = "EXPLORING_ACTIVE", // "PENDING_EXPLORATION", "EXPLORING_ACTIVE", "VERIFIED_ACTIVE"
    val explorationSeconds: Int = 0, // Genuine app browsing duration (in seconds)
    val joinedTimestamp: Long = System.currentTimeMillis(),
    val verifiedTimestamp: Long = 0L,
    val rewardUnlocked: String = "1-Day Full-App Free Subscription"
)

@Entity(tableName = "community_video_posts")
data class CommunityVideoPost(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String = "🎬",
    val title: String,
    val description: String = "",
    val platform: String = "YouTube", // "YouTube", "Instagram", "GoLiveGram"
    val videoType: String = "SHORT", // "SHORT" (1 ad) or "LONG" (2 ads)
    val videoUrl: String = "",
    val durationSeconds: Int = 30,
    val viewsCount: Int = 1240,
    val likesCount: Int = 320,
    val isFollowing: Boolean = false,
    val isBoosted: Boolean = false,
    val boostTier: String = "STANDARD", // "SUPER_SPOTLIGHT", "CREATOR_BOOST", "STANDARD"
    val timestamp: Long = System.currentTimeMillis()
)

