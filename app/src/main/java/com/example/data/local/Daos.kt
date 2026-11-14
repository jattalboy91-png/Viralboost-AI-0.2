package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET diamonds = diamonds + :amount, lifetimeDiamonds = lifetimeDiamonds + CASE WHEN :amount > 0 THEN :amount ELSE 0 END WHERE id = 1")
    suspend fun addDiamonds(amount: Int)

    @Query("UPDATE user_profile SET diamonds = :newAmount WHERE id = 1")
    suspend fun setDiamonds(newAmount: Int)

    @Query("UPDATE user_profile SET coins = coins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE user_profile SET coins = :newAmount WHERE id = 1")
    suspend fun setCoins(newAmount: Int)

    @Query("UPDATE user_profile SET bonusRupees = :amount, hasClaimedWelcomeBonus = 1 WHERE id = 1")
    suspend fun creditWelcomeBonusRupees(amount: Double)

    @Query("UPDATE user_profile SET bonusRupees = bonusRupees + :amount WHERE id = 1")
    suspend fun addBonusRupees(amount: Double)

    @Query("UPDATE user_profile SET isVip = :isVip, vipExpiryDate = :expiryDate WHERE id = 1")
    suspend fun updateVipStatus(isVip: Boolean, expiryDate: Long)

    @Query("UPDATE user_profile SET isVip = :isVip, vipExpiryDate = :expiryDate, currentPlanTier = :planTier WHERE id = 1")
    suspend fun updateVipStatusWithPlan(isVip: Boolean, expiryDate: Long, planTier: String)

    @Query("SELECT streakDays FROM user_profile WHERE id = 1 LIMIT 1")
    fun getStreakCountFlow(): Flow<Int?>

    @Query("SELECT streakDays FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getStreakCountOnce(): Int?

    @Query("SELECT diamonds FROM user_profile WHERE id = 1 LIMIT 1")
    fun getDiamondBalanceFlow(): Flow<Int?>

    @Query("SELECT diamonds FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getDiamondBalanceOnce(): Int?

    @Query("UPDATE user_profile SET streakDays = streakDays + 1, lastStreakClaimDate = :claimDate, lastLoginDateMillis = :timestamp WHERE id = 1")
    suspend fun incrementStreakCount(claimDate: String, timestamp: Long)

    @Query("UPDATE user_profile SET streakDays = 1, lastStreakClaimDate = :claimDate, lastLoginDateMillis = :timestamp WHERE id = 1")
    suspend fun resetStreakCount(claimDate: String, timestamp: Long)

    @Query("UPDATE user_profile SET streakDays = :streak, lastStreakClaimDate = :claimDate, lastLoginDateMillis = :timestamp WHERE id = 1")
    suspend fun updateStreak(streak: Int, claimDate: String, timestamp: Long)

    @Query("UPDATE user_profile SET todayAiGenerationsCount = :count, lastAiGenerationDate = :date WHERE id = 1")
    suspend fun updateAiGenerationCount(count: Int, date: String)

    @Query("UPDATE user_profile SET todayAiGenerationsCount = :count, todayVideosGenerated = :videos, todayImagesGenerated = :images, lastAiGenerationDate = :date WHERE id = 1")
    suspend fun updateDailyAiCounts(count: Int, videos: Int, images: Int, date: String)

    @Query("UPDATE user_profile SET trialStartTimestamp = :timestamp WHERE id = 1")
    suspend fun setTrialStartTimestamp(timestamp: Long)

    @Query("UPDATE user_profile SET isOwner = :isOwner WHERE id = 1")
    suspend fun setOwnerStatus(isOwner: Boolean)

    @Query("UPDATE user_profile SET isLoggedIn = :isLoggedIn WHERE id = 1")
    suspend fun setLoginStatus(isLoggedIn: Boolean)

    @Query("UPDATE user_profile SET isLoggedIn = :isLoggedIn, hasAgreedToTerms = :agreed, username = :name, userContact = :contact, email = :email, authMethod = :method, legalAgreementTimestamp = :timestamp, legalAgreementStatus = 'AGREED_AND_VERIFIED' WHERE id = 1")
    suspend fun completeUserOnboarding(
        isLoggedIn: Boolean,
        agreed: Boolean,
        name: String,
        contact: String,
        email: String,
        method: String,
        timestamp: String
    )

    @Query("UPDATE user_profile SET isLoggedIn = 0 WHERE id = 1")
    suspend fun logoutUser()
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_records ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<PaymentRecord>>

    @Query("SELECT * FROM payment_records ORDER BY timestamp DESC")
    suspend fun getAllPaymentsOnce(): List<PaymentRecord>

    @Query("SELECT * FROM payment_records WHERE id = :id LIMIT 1")
    suspend fun getPaymentById(id: Long): PaymentRecord?

    @Query("SELECT * FROM payment_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserPayments(userId: Int): Flow<List<PaymentRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecord): Long

    @Query("UPDATE payment_records SET status = :status, verifiedAt = :verifiedAt, adminNote = :note WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, status: String, verifiedAt: Long, note: String)

    @Query("SELECT * FROM payment_records WHERE utrNumber = :utr LIMIT 1")
    suspend fun findByUtr(utr: String): PaymentRecord?
}

@Dao
interface SupportDao {
    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun getAllTickets(): Flow<List<SupportTicket>>

    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserTickets(userId: Int): Flow<List<SupportTicket>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicket): Long

    @Query("UPDATE support_tickets SET status = :status, adminReply = :reply, repliedAt = :repliedAt WHERE id = :ticketId")
    suspend fun replyTicket(ticketId: Long, reply: String, status: String, repliedAt: Long)
}

@Dao
interface AiDao {
    @Query("SELECT * FROM ai_generations ORDER BY createdAt DESC")
    fun getAllGenerations(): Flow<List<AiGenerationRecord>>

    @Query("SELECT * FROM ai_generations ORDER BY createdAt DESC")
    suspend fun getAllGenerationsOnce(): List<AiGenerationRecord>

    @Query("SELECT * FROM ai_generations WHERE isBookmarked = 1 ORDER BY createdAt DESC")
    fun getBookmarkedGenerations(): Flow<List<AiGenerationRecord>>

    @Query("SELECT * FROM ai_generations WHERE type = :type ORDER BY createdAt DESC")
    fun getGenerationsByType(type: String): Flow<List<AiGenerationRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneration(record: AiGenerationRecord): Long

    @Query("UPDATE ai_generations SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean)

    @Query("DELETE FROM ai_generations WHERE id = :id")
    suspend fun deleteGenerationById(id: Long)

    @Query("DELETE FROM ai_generations")
    suspend fun clearGenerations()
}

@Dao
interface SavedContentHistoryDao {
    @Query("SELECT * FROM saved_content_history ORDER BY createdAt DESC")
    fun getAllSavedContent(): Flow<List<SavedContentHistoryEntity>>

    @Query("SELECT * FROM saved_content_history ORDER BY createdAt DESC")
    suspend fun getAllSavedContentOnce(): List<SavedContentHistoryEntity>

    @Query("SELECT * FROM saved_content_history WHERE isBookmarked = 1 ORDER BY createdAt DESC")
    fun getBookmarkedSavedContent(): Flow<List<SavedContentHistoryEntity>>

    @Query("SELECT * FROM saved_content_history WHERE contentType = :contentType ORDER BY createdAt DESC")
    fun getSavedContentByType(contentType: String): Flow<List<SavedContentHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedContent(entity: SavedContentHistoryEntity): Long

    @Update
    suspend fun updateSavedContent(entity: SavedContentHistoryEntity)

    @Query("UPDATE saved_content_history SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun setBookmarkStatus(id: Long, isBookmarked: Boolean)

    @Query("DELETE FROM saved_content_history WHERE id = :id")
    suspend fun deleteSavedContentById(id: Long)

    @Query("DELETE FROM saved_content_history")
    suspend fun clearAllSavedContent()
}

@Dao
interface UserStreakDiamondLedgerDao {
    @Query("SELECT * FROM user_streak_diamond_ledger ORDER BY timestamp DESC")
    fun getAllLedgerEntries(): Flow<List<UserStreakDiamondLedgerEntity>>

    @Query("SELECT * FROM user_streak_diamond_ledger ORDER BY timestamp DESC")
    suspend fun getAllLedgerEntriesOnce(): List<UserStreakDiamondLedgerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntry(entry: UserStreakDiamondLedgerEntity): Long

    @Query("DELETE FROM user_streak_diamond_ledger")
    suspend fun clearLedger()
}

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard_creators ORDER BY rank ASC")
    fun getAllCreators(): Flow<List<LeaderboardCreator>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreators(creators: List<LeaderboardCreator>)

    @Query("DELETE FROM leaderboard_creators")
    suspend fun clearLeaderboard()
}

@Dao
interface ConfigDao {
    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    fun getAppConfig(): Flow<AppConfig?>

    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    suspend fun getAppConfigOnce(): AppConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAppConfig(config: AppConfig)
}

@Dao
interface ProductOrderDao {
    @Query("SELECT * FROM product_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<ProductOrderRecord>>

    @Query("SELECT * FROM product_orders ORDER BY timestamp DESC")
    suspend fun getAllOrdersOnce(): List<ProductOrderRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: ProductOrderRecord): Long

    @Query("SELECT COUNT(*) FROM product_orders")
    suspend fun getOrderCount(): Int
}

@Dao
interface VideoCrossPostDao {
    @Query("SELECT * FROM video_crossposts ORDER BY timestamp DESC")
    fun getAllCrossPosts(): Flow<List<VideoCrossPostRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossPost(record: VideoCrossPostRecord): Long

    @Query("SELECT COUNT(*) FROM video_crossposts")
    suspend fun getCrossPostCount(): Int
}

@Dao
interface ReferralDao {
    @Query("SELECT * FROM referral_records ORDER BY joinedTimestamp DESC")
    fun getAllReferrals(): Flow<List<ReferralRecord>>

    @Query("SELECT * FROM referral_records WHERE id = :id LIMIT 1")
    suspend fun getReferralById(id: Long): ReferralRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(record: ReferralRecord): Long

    @Update
    suspend fun updateReferral(record: ReferralRecord)

    @Query("UPDATE referral_records SET status = :status, verifiedTimestamp = :verifiedTime WHERE id = :id")
    suspend fun updateReferralStatus(id: Long, status: String, verifiedTime: Long)

    @Query("UPDATE referral_records SET explorationSeconds = :seconds, status = :status WHERE id = :id")
    suspend fun updateExplorationProgress(id: Long, seconds: Int, status: String)

    @Query("SELECT COUNT(*) FROM referral_records WHERE status = 'VERIFIED_ACTIVE'")
    suspend fun getActiveReferralCount(): Int

    @Query("SELECT COUNT(*) FROM referral_records")
    suspend fun getTotalReferralCount(): Int
}

@Dao
interface CommunityVideoPostDao {
    @Query("SELECT * FROM community_video_posts ORDER BY isBoosted DESC, timestamp DESC")
    fun getAllVideos(): Flow<List<CommunityVideoPost>>

    @Query("SELECT * FROM community_video_posts ORDER BY isBoosted DESC, timestamp DESC")
    suspend fun getAllVideosOnce(): List<CommunityVideoPost>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: CommunityVideoPost): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<CommunityVideoPost>)

    @Query("UPDATE community_video_posts SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: Long)

    @Query("UPDATE community_video_posts SET likesCount = likesCount + 1 WHERE id = :id")
    suspend fun incrementLikes(id: Long)

    @Query("UPDATE community_video_posts SET isFollowing = NOT isFollowing WHERE id = :id")
    suspend fun toggleFollow(id: Long)

    @Query("UPDATE community_video_posts SET isFollowing = :isFollowing WHERE creatorHandle = :handle")
    suspend fun setFollowByHandle(handle: String, isFollowing: Boolean)

    @Query("UPDATE community_video_posts SET isBoosted = :isBoosted, boostTier = :boostTier WHERE id = :id")
    suspend fun updateBoostStatus(id: Long, isBoosted: Boolean, boostTier: String)

    @Query("SELECT COUNT(*) FROM community_video_posts")
    suspend fun getVideoCount(): Int
}

@Dao
interface LegalDao {
    @Query("SELECT * FROM legal_agreement_logs ORDER BY id DESC")
    fun getAllLegalRecords(): Flow<List<LegalAgreementRecord>>

    @Query("SELECT * FROM legal_agreement_logs ORDER BY id DESC")
    suspend fun getAllLegalRecordsOnce(): List<LegalAgreementRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLegalRecord(record: LegalAgreementRecord): Long
}

