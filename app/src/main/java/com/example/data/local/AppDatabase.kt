package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        PaymentRecord::class,
        SupportTicket::class,
        AiGenerationRecord::class,
        SavedContentHistoryEntity::class,
        UserStreakDiamondLedgerEntity::class,
        LeaderboardCreator::class,
        AppConfig::class,
        ProductOrderRecord::class,
        VideoCrossPostRecord::class,
        ReferralRecord::class,
        CommunityVideoPost::class,
        LegalAgreementRecord::class
    ],
    version = 17,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun paymentDao(): PaymentDao
    abstract fun supportDao(): SupportDao
    abstract fun aiDao(): AiDao
    abstract fun savedContentHistoryDao(): SavedContentHistoryDao
    abstract fun userStreakDiamondLedgerDao(): UserStreakDiamondLedgerDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun configDao(): ConfigDao
    abstract fun productOrderDao(): ProductOrderDao
    abstract fun videoCrossPostDao(): VideoCrossPostDao
    abstract fun referralDao(): ReferralDao
    abstract fun communityVideoPostDao(): CommunityVideoPostDao
    abstract fun legalDao(): LegalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "viralboost_ai_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val userDao = database.userDao()
            val existing = userDao.getUserProfileOnce()
            if (existing == null) {
                userDao.insertOrUpdateProfile(
                    UserProfile(
                        id = 1,
                        username = "ViralCreator_99",
                        email = "creator@viralboost.ai",
                        niche = "Shorts & Reels",
                        diamonds = 10,
                        coins = 250,
                        lifetimeDiamonds = 10,
                        isVip = false,
                        streakDays = 1,
                        lastLoginDateMillis = System.currentTimeMillis()
                    )
                )
            }

            val leaderboardDao = database.leaderboardDao()
            leaderboardDao.insertCreators(
                listOf(
                    LeaderboardCreator(1, "Aarav 'Viral' Sharma", "@aarav_growth", "Tech & AI Reels", 145, 48, 9940, true, 1, 4820),
                    LeaderboardCreator(2, "Priya TechCraft", "@priya_crafts", "DIY & Shorts", 118, 35, 8720, true, 2, 3910),
                    LeaderboardCreator(3, "Rohan Finance Fast", "@rohan_money", "Finance & Wealth", 96, 29, 7860, true, 3, 3250),
                    LeaderboardCreator(4, "Kavya Vlog Studio", "@kavya_daily", "Lifestyle & Vlogs", 82, 24, 6950, true, 4, 2840),
                    LeaderboardCreator(5, "Dev Gaming Nexus", "@dev_nexus", "Gaming Highlights", 64, 19, 5840, false, 5, 2190),
                    LeaderboardCreator(6, "Neha Comedy Bites", "@neha_bites", "Comedy & Skits", 52, 16, 5100, false, 6, 1820),
                    LeaderboardCreator(7, "Vikram Fitness Lab", "@fit_vikram", "Fitness & Diet", 41, 14, 4320, false, 7, 1430),
                    LeaderboardCreator(8, "Ananya Beauty Trend", "@ananya_glam", "Fashion & Beauty", 29, 9, 3210, false, 8, 980),
                    LeaderboardCreator(9, "ViralCreator_99 (You)", "@creator_99", "Shorts & Reels", 12, 1, 450, false, 9, 100)
                )
            )

            val configDao = database.configDao()
            val existingConfig = configDao.getAppConfigOnce()
            if (existingConfig == null) {
                configDao.updateAppConfig(AppConfig())
            }

            val referralDao = database.referralDao()
            referralDao.insertReferral(
                ReferralRecord(
                    id = 1,
                    friendName = "Rohit Verma",
                    friendAvatar = "🔥",
                    referralCode = "VIRAL5665",
                    status = "VERIFIED_ACTIVE",
                    explorationSeconds = 95,
                    joinedTimestamp = System.currentTimeMillis() - (35 * 60 * 1000),
                    verifiedTimestamp = System.currentTimeMillis() - (33 * 60 * 1000),
                    rewardUnlocked = "1-Day Full-App Free Subscription"
                )
            )
            referralDao.insertReferral(
                ReferralRecord(
                    id = 2,
                    friendName = "Simran Kaur",
                    friendAvatar = "✨",
                    referralCode = "VIRAL5665",
                    status = "EXPLORING_ACTIVE",
                    explorationSeconds = 65,
                    joinedTimestamp = System.currentTimeMillis() - (5 * 60 * 1000),
                    verifiedTimestamp = 0L,
                    rewardUnlocked = "1-Day Full-App Free Subscription"
                )
            )

            val videoDao = database.communityVideoPostDao()
            if (videoDao.getVideoCount() == 0) {
                videoDao.insertVideos(
                    listOf(
                        CommunityVideoPost(
                            id = 1,
                            creatorName = "Aarav 'Viral' Sharma",
                            creatorHandle = "@aarav_growth",
                            creatorAvatar = "🎬",
                            title = "How I Gained 100K Followers in 30 Days (AI Secrets Revealed) 🚀",
                            description = "3 algorithmic rules every YouTube Shorts & Reels creator must follow. Watch to the end for the secret prompt framework!",
                            platform = "YouTube",
                            videoType = "SHORT",
                            videoUrl = "https://youtube.com/shorts/viral_growth_aarav",
                            durationSeconds = 30,
                            viewsCount = 42100,
                            likesCount = 8400,
                            isFollowing = false,
                            isBoosted = true,
                            boostTier = "SUPER_SPOTLIGHT"
                        ),
                        CommunityVideoPost(
                            id = 2,
                            creatorName = "Priya TechCraft",
                            creatorHandle = "@priya_crafts",
                            creatorAvatar = "✨",
                            title = "Top 5 Viral Reels Audio Hacks for 10x More Reach 📈",
                            description = "Stop using dead audio! Use this ViralBoost AI matchmaker method right now for instant Instagram explore page ranking.",
                            platform = "Instagram",
                            videoType = "SHORT",
                            videoUrl = "https://instagram.com/reel/audio_growth_hack",
                            durationSeconds = 25,
                            viewsCount = 28900,
                            likesCount = 5200,
                            isFollowing = true,
                            isBoosted = false,
                            boostTier = "STANDARD"
                        ),
                        CommunityVideoPost(
                            id = 3,
                            creatorName = "Rohan Finance Fast",
                            creatorHandle = "@rohan_money",
                            creatorAvatar = "⚡",
                            title = "GoLiveGram Creator Masterclass: Zero to ₹1 Lakh/Month Affiliate Income 💰",
                            description = "Full in-depth breakdown of multi-platform audience funneling, GoLiveGram live streaming, and Shopsy/EarnKaro affiliate conversion secrets.",
                            platform = "GoLiveGram",
                            videoType = "LONG",
                            videoUrl = "https://golivegram.com/v/creator_masterclass_rohan",
                            durationSeconds = 95,
                            viewsCount = 15400,
                            likesCount = 3100,
                            isFollowing = false,
                            isBoosted = true,
                            boostTier = "SUPER_SPOTLIGHT"
                        ),
                        CommunityVideoPost(
                            id = 4,
                            creatorName = "Kavya Vlog Studio",
                            creatorHandle = "@kavya_daily",
                            creatorAvatar = "🎥",
                            title = "Day in the Life of a Full-Time AI Creator (Behind the Scenes) 🌟",
                            description = "Camera setup, lighting tricks, and one-click multi-platform cross-posting workflow to YouTube, Instagram, and GoLiveGram.",
                            platform = "YouTube",
                            videoType = "LONG",
                            videoUrl = "https://youtube.com/watch?v=kavya_studio_daily",
                            durationSeconds = 85,
                            viewsCount = 19800,
                            likesCount = 4100,
                            isFollowing = false,
                            isBoosted = false,
                            boostTier = "STANDARD"
                        ),
                        CommunityVideoPost(
                            id = 5,
                            creatorName = "Dev Gaming Nexus",
                            creatorHandle = "@dev_nexus",
                            creatorAvatar = "🎮",
                            title = "Epic Clutches & Viral Shorts Sound Effects 🔥",
                            description = "Edited using ViralBoost AI prompt scripts and auto-caption tags! Share with your gaming squad.",
                            platform = "Instagram",
                            videoType = "SHORT",
                            videoUrl = "https://instagram.com/reel/gaming_nexus_epic",
                            durationSeconds = 28,
                            viewsCount = 34100,
                            likesCount = 7300,
                            isFollowing = false,
                            isBoosted = false,
                            boostTier = "STANDARD"
                        ),
                        CommunityVideoPost(
                            id = 6,
                            creatorName = "Neha Comedy Bites",
                            creatorHandle = "@neha_bites",
                            creatorAvatar = "🎭",
                            title = "When You Drop 1 Reel and Think You're an Influencer 😂",
                            description = "Multi-streamed on GoLiveGram & Instagram Reels with ViralBoost AI One-Click broadcast!",
                            platform = "GoLiveGram",
                            videoType = "SHORT",
                            videoUrl = "https://golivegram.com/v/neha_comedy_bites_1",
                            durationSeconds = 22,
                            viewsCount = 51200,
                            likesCount = 12400,
                            isFollowing = false,
                            isBoosted = false,
                            boostTier = "STANDARD"
                        )
                    )
                )
            }
        }
    }
}
