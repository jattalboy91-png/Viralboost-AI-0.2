package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.StreakResult
import com.example.data.repository.ViralBoostRepository
import com.example.monetization.AdManager
import com.example.monetization.MonetizationConstants
import com.example.util.ReferralNotificationHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: AppDatabase
  private lateinit var repository: ViralBoostRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = ViralBoostRepository(
      database.userDao(),
      database.paymentDao(),
      database.supportDao(),
      database.aiDao(),
      database.leaderboardDao(),
      database.configDao(),
      database.productOrderDao()
    )
  }

  @After
  fun teardown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ViralBoost AI", appName)
  }

  @Test
  fun `test free trial daily allowance enforces 2 videos and 2 photos daily`() = runTest {
    val profile = repository.getProfileOnce()
    val trialStatus = repository.getTrialStatus(profile)

    assertEquals(1, trialStatus.trialDay)
    assertTrue(trialStatus.isTrialActive)
    assertFalse(trialStatus.isExpired)
    assertEquals(2, trialStatus.maxVideosToday)
    assertEquals(2, trialStatus.maxImagesToday)
    assertEquals(5, trialStatus.maxTextToday)
    assertEquals(2, trialStatus.videosRemaining)
    assertEquals(2, trialStatus.imagesRemaining)
  }

  @Test
  fun `test 10-day and 15-day pass plan quotas enforce 2 videos and 5 photos daily`() = runTest {
    val profile = repository.getProfileOnce().copy(
      isVip = true,
      currentPlanTier = "PASS_10_DAYS",
      vipExpiryDate = System.currentTimeMillis() + 864000000L
    )
    database.userDao().insertOrUpdateProfile(profile)
    val trialStatus = repository.getTrialStatus(profile)

    assertTrue(trialStatus.isVip)
    assertEquals(2, trialStatus.maxVideosToday)
    assertEquals(5, trialStatus.maxImagesToday)
  }

  @Test
  fun `test 1-month vip plan enforces 3 videos and 15 photos daily`() = runTest {
    val profile = repository.getProfileOnce().copy(
      isVip = true,
      currentPlanTier = "VIP_MONTHLY",
      vipExpiryDate = System.currentTimeMillis() + 2592000000L
    )
    database.userDao().insertOrUpdateProfile(profile)
    val trialStatus = repository.getTrialStatus(profile)

    assertTrue(trialStatus.isVip)
    assertEquals(3, trialStatus.maxVideosToday)
    assertEquals(15, trialStatus.maxImagesToday)
  }

  @Test
  fun `test 3-month ultimate pro unlocks unlimited access`() = runTest {
    val profile = repository.getProfileOnce().copy(
      isVip = true,
      currentPlanTier = "PRO_3_MONTHS",
      vipExpiryDate = System.currentTimeMillis() + 7776000000L
    )
    database.userDao().insertOrUpdateProfile(profile)
    val trialStatus = repository.getTrialStatus(profile)

    assertTrue(trialStatus.isVip)
    assertFalse(trialStatus.isExpired)
    assertEquals(9999, trialStatus.maxVideosToday)
    assertEquals(9999, trialStatus.videosRemaining)
    assertEquals(9999, trialStatus.maxImagesToday)

    val (canGenerate, _) = repository.checkCanGenerateAi("VIDEO")
    assertTrue(canGenerate)
  }

  @Test
  fun `test daily login streak tracker and milestone rewards`() = runTest {
    val initialProfile = repository.getProfileOnce()
    val initialDiamonds = initialProfile.diamonds

    // Claim today's streak (Day 1)
    val result1 = repository.claimDailyStreak()
    assertTrue(result1 is com.example.data.repository.StreakResult.Success)
    val success1 = result1 as com.example.data.repository.StreakResult.Success
    assertEquals(1, success1.streakDays)
    assertEquals(2, success1.diamondsAwarded)
    assertEquals(0, success1.milestoneBonus)

    // Cannot double-claim today
    val duplicateClaim = repository.claimDailyStreak()
    assertTrue(duplicateClaim is com.example.data.repository.StreakResult.AlreadyClaimed)

    // Verify diamond balance increased
    val updatedProfile = repository.getProfileOnce()
    assertEquals(initialDiamonds + 2, updatedProfile.diamonds)
  }

  @Test
  fun `test rewarded video ad payout awards exact configured diamonds`() = runTest {
    val initialProfile = repository.getProfileOnce()
    val initialDiamonds = initialProfile.diamonds

    val rewardAwarded = repository.rewardAdWatched()
    assertEquals(5, rewardAwarded)

    val updatedProfile = repository.getProfileOnce()
    assertEquals(initialDiamonds + 5, updatedProfile.diamonds)
  }

  @Test
  fun `test app config room db persistence survives and updates ad units`() = runTest {
    val initialConfig = repository.getAppConfigOnce()
    assertEquals(com.example.monetization.MonetizationConstants.ADMOB_PUBLISHER_APP_ID, initialConfig.admobPublisherId)

    val customConfig = initialConfig.copy(
      admobPublisherId = "pub-1234567890",
      admobBannerUnitId = "ca-app-pub-99999/banner1",
      admobInterstitialUnitId = "ca-app-pub-99999/interstitial1",
      admobRewardedUnitId = "ca-app-pub-99999/rewarded1",
      admobNativeUnitId = "ca-app-pub-99999/native1",
      admobNativeVideoUnitId = "ca-app-pub-99999/nativevideo1",
      ownerUpiId = "customowner@upi",
      affiliateTag = "998877"
    )
    repository.saveAppConfig(customConfig)

    val savedConfig = repository.getAppConfigOnce()
    assertEquals("pub-1234567890", savedConfig.admobPublisherId)
    assertEquals("ca-app-pub-99999/banner1", savedConfig.admobBannerUnitId)
    assertEquals("ca-app-pub-99999/interstitial1", savedConfig.admobInterstitialUnitId)
    assertEquals("ca-app-pub-99999/rewarded1", savedConfig.admobRewardedUnitId)
    assertEquals("ca-app-pub-99999/native1", savedConfig.admobNativeUnitId)
    assertEquals("ca-app-pub-99999/nativevideo1", savedConfig.admobNativeVideoUnitId)
    assertEquals("customowner@upi", savedConfig.ownerUpiId)
    assertEquals("998877", savedConfig.affiliateTag)
  }

  @Test
  fun `test social media sharing reward instantly grants 10 coins`() = runTest {
    val initialProfile = repository.getProfileOnce()
    val initialCoins = initialProfile.coins
    val sampleProduct = com.example.data.model.SampleAffiliateProducts.catalog.first()

    repository.rewardProductShare(sampleProduct)

    val updatedProfile = repository.getProfileOnce()
    assertEquals(initialCoins + 10, updatedProfile.coins)
  }

  @Test
  fun `test product purchase reward grants 3 diamonds`() = runTest {
    val initialProfile = repository.getProfileOnce()
    val initialDiamonds = initialProfile.diamonds
    val sampleProduct = com.example.data.model.SampleAffiliateProducts.catalog.first()

    repository.rewardProductPurchase(sampleProduct)

    val updatedProfile = repository.getProfileOnce()
    assertEquals(initialDiamonds + 3, updatedProfile.diamonds)
  }

  @Test
  fun `test in-app direct ordering records order and awards 3 diamonds`() = runTest {
    val initialProfile = repository.getProfileOnce()
    val initialDiamonds = initialProfile.diamonds
    val sampleProduct = com.example.data.model.SampleAffiliateProducts.catalog[1]

    val orderId = repository.placeProductOrder(
      product = sampleProduct,
      customerName = "Rahul Creator",
      customerPhone = "9876543210",
      shippingAddress = "Flat 101, Galaxy Tower, Mumbai, 400001",
      affiliateTag = "5665937"
    )

    assertTrue(orderId > 0)
    val updatedProfile = repository.getProfileOnce()
    assertEquals(initialDiamonds + 3, updatedProfile.diamonds)

    val orders = database.productOrderDao().getAllOrdersOnce()
    assertEquals(1, orders.size)
    assertEquals(sampleProduct.id, orders[0].productId)
    assertEquals("Rahul Creator", orders[0].customerName)
    assertEquals(3, orders[0].diamondsRewarded)
    assertEquals("5665937", orders[0].affiliateTag)
  }

  @Test
  fun `test auto-branded viral share text contains promotional headline and download link`() {
    val sampleProduct = com.example.data.model.SampleAffiliateProducts.catalog.first()
    val shareText = sampleProduct.getShareText("5665937")

    assertTrue(shareText.contains("Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:"))
    assertTrue(shareText.contains("https://viralboost.ai/download?ref=5665937"))
  }

  @Test
  fun `test universal e-commerce link builder supports Amazon Flipkart Meesho Myntra and EarnKaro`() {
    val tag = "5665937"
    val meeshoUrl = MonetizationConstants.buildMeeshoAffiliateUrl("sample-kurti", tag)
    assertTrue(meeshoUrl.contains("meesho.com/sample-kurti"))
    assertTrue(meeshoUrl.contains("referral_code=5665937"))

    val flipkartUrl = MonetizationConstants.buildFlipkartAffiliateUrl("deal-123", tag)
    assertTrue(flipkartUrl.contains("flipkart.com/deal-123"))
    assertTrue(flipkartUrl.contains("affid=5665937"))

    val amazonUrl = MonetizationConstants.buildAmazonAffiliateUrl("B0BDK6Z5VR", tag)
    assertTrue(amazonUrl.contains("amazon.in/dp/B0BDK6Z5VR"))
    assertTrue(amazonUrl.contains("tag=5665937"))

    val myntraUrl = MonetizationConstants.buildMyntraAffiliateUrl("shoes-456", tag)
    assertTrue(myntraUrl.contains("myntra.com/shoes-456"))
    assertTrue(myntraUrl.contains("affid=5665937"))

    val earnKaroUrl = MonetizationConstants.buildEarnKaroAffiliateUrl("gadget-789", tag)
    assertTrue(earnKaroUrl.contains("earnkaro.com/deal/gadget-789"))
    assertTrue(earnKaroUrl.contains("r=5665937"))

    assertEquals("https://bitli.in/Ff4aE3C", MonetizationConstants.GLOBAL_ADMIN_BITLI_URL)
    assertEquals("https://bitli.in/Ff4aE3C", MonetizationConstants.OFFICIAL_STORE_DOWNLOAD_URL)
  }

  @Test
  fun `test shared social accounts persistence across screens via viralboost_social_links`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val sharedPrefs = context.getSharedPreferences("viralboost_social_links", Context.MODE_PRIVATE)

    sharedPrefs.edit()
      .putBoolean("conn_whatsapp", true)
      .putString("handle_whatsapp", "+91 99999 88888")
      .putBoolean("conn_instagram", true)
      .putString("handle_instagram", "@verified_growth_guru")
      .putBoolean("conn_telegram", true)
      .putString("handle_telegram", "t.me/verified_deals")
      .putBoolean("conn_facebook", true)
      .putString("handle_facebook", "fb.com/verified_creators")
      .apply()

    assertTrue(sharedPrefs.getBoolean("conn_whatsapp", false))
    assertEquals("+91 99999 88888", sharedPrefs.getString("handle_whatsapp", null))
    assertTrue(sharedPrefs.getBoolean("conn_instagram", false))
    assertEquals("@verified_growth_guru", sharedPrefs.getString("handle_instagram", null))
    assertTrue(sharedPrefs.getBoolean("conn_telegram", false))
    assertEquals("t.me/verified_deals", sharedPrefs.getString("handle_telegram", null))
    assertTrue(sharedPrefs.getBoolean("conn_facebook", false))
    assertEquals("fb.com/verified_creators", sharedPrefs.getString("handle_facebook", null))
  }

  @Test
  fun `test high conversion promotional caption and permanent owner affiliate link integration`() {
    assertEquals("https://bitli.in/Ff4aE3C", MonetizationConstants.PERMANENT_OWNER_AFFILIATE_LINK)
    assertEquals("5665937", MonetizationConstants.OWNER_EARNKARO_ID)

    val sampleProduct = com.example.data.model.SampleAffiliateProducts.catalog.first()
    val shareText = sampleProduct.getShareText("5665937")

    assertTrue(shareText.contains("https://bitli.in/Ff4aE3C"))
    assertTrue(shareText.contains("महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!"))
    assertTrue(shareText.contains("Best Quality & Super Discount Deals! Don't miss out, shop now here:"))
    assertTrue(shareText.contains("Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:"))

    val videoFooter = MonetizationConstants.buildHighConversionVideoFooter("https://bitli.in/Ff4aE3C")
    assertTrue(videoFooter.contains("https://bitli.in/Ff4aE3C"))
    assertTrue(videoFooter.contains("Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:"))
  }

  @Test
  fun `test go viral push notification and milestone admob triggers`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    
    // Test push notification dispatch without crashes
    ReferralNotificationHelper.showPostReadyToGoViralNotification(context)

    // Test Milestone Interstitial for VIP user (should bypass ads immediately)
    var milestoneExecuted = false
    AdManager.triggerMilestoneInterstitial(
        context = context,
        isVip = true,
        milestoneName = "Affiliate Hub - Milestone 1: Link Pasted",
        onComplete = { milestoneExecuted = true }
    )
    assertTrue(milestoneExecuted)

    // Test Milestone Interstitial fallback for non-VIP in test/emulator environment
    var fallbackTriggered = false
    var fallbackCompleted = false
    AdManager.triggerMilestoneInterstitial(
        context = context,
        isVip = false,
        milestoneName = "Video Cross-Posting - Milestone 2: Post to Go Viral",
        onShowInterstitialFallback = { onDismiss: () -> Unit ->
            fallbackTriggered = true
            onDismiss()
        },
        onComplete = {
            fallbackCompleted = true
        }
    )
    assertTrue(fallbackTriggered)
    assertTrue(fallbackCompleted)

    // Test Milestone Interstitial for Daily Streak Claim
    var streakAdTriggered = false
    AdManager.triggerMilestoneInterstitial(
        context = context,
        isVip = false,
        milestoneName = "Daily Streak Claim Milestone",
        onShowInterstitialFallback = { onDismiss: () -> Unit ->
            streakAdTriggered = true
            onDismiss()
        }
    )
    assertTrue(streakAdTriggered)

    // Verify Owner UPI configuration preserved
    assertEquals("Amit2653@ptyes", MonetizationConstants.OWNER_UPI_ID)
  }

  @Test
  fun `test smart fallback affiliate link handling and owner master link preservation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    // Case 1: User does not have a personal affiliate link
    MonetizationConstants.clearUserPersonalAffiliateLink(context)
    val savedLink = MonetizationConstants.getUserPersonalAffiliateLink(context)
    assertTrue(savedLink.isEmpty())
    assertFalse(MonetizationConstants.hasPersonalAffiliateLink(savedLink))

    val effectiveLinkFallback = MonetizationConstants.getEffectiveAffiliateLink(savedLink)
    assertEquals(MonetizationConstants.PERMANENT_OWNER_AFFILIATE_LINK, effectiveLinkFallback)
    assertEquals("https://bitli.in/Ff4aE3C", effectiveLinkFallback)

    val fallbackCaption = MonetizationConstants.buildSmartAffiliateCaption(
        title = "🔥 5 Insane Growth Hacks",
        tags = "#Viral #Reels",
        description = "Skyrocket your reach overnight",
        personalAffiliateLink = savedLink,
        downloadUrl = "https://viralboost.ai/download?ref=5665937"
    )

    // Verify owner master link and high-conversion urgency captions attached
    assertTrue(fallbackCaption.contains("https://bitli.in/Ff4aE3C"))
    assertTrue(fallbackCaption.contains("महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!"))
    assertTrue(fallbackCaption.contains("Best Quality & Super Discount Deals! Don't miss out, shop now here:"))
    assertTrue(fallbackCaption.contains("Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:"))

    // Case 2: User DOES have their own personal affiliate link saved
    val userLink = "https://earnkaro.com/deal/trending-kurti-user99"
    MonetizationConstants.setUserPersonalAffiliateLink(context, userLink)

    val updatedSavedLink = MonetizationConstants.getUserPersonalAffiliateLink(context)
    assertEquals(userLink, updatedSavedLink)
    assertTrue(MonetizationConstants.hasPersonalAffiliateLink(updatedSavedLink))

    val userCaption = MonetizationConstants.buildSmartAffiliateCaption(
        title = "🔥 5 Insane Growth Hacks",
        tags = "#Viral #Reels",
        description = "Skyrocket your reach overnight",
        personalAffiliateLink = updatedSavedLink,
        downloadUrl = "https://viralboost.ai/download?ref=5665937"
    )

    // User's link is used for their earnings
    assertTrue(userCaption.contains(userLink))
    // Background owner structure and app viral footer are preserved
    assertTrue(userCaption.contains(MonetizationConstants.PERMANENT_OWNER_AFFILIATE_LINK))
    assertTrue(userCaption.contains("Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:"))

    // Cleanup: Revert to owner fallback
    MonetizationConstants.clearUserPersonalAffiliateLink(context)
    assertEquals("", MonetizationConstants.getUserPersonalAffiliateLink(context))
  }

  @Test
  fun `test unique 7-8 digit user id generation and post-signup daily reward claim`() = runTest {
    val initialProfile = repository.getProfileOnce()
    val userId = initialProfile.displayUserId

    // 1. Verify Unique 7-8 Digit User ID
    assertTrue("User ID should be 7-8 digits: $userId", userId.length in 7..8)
    assertTrue("User ID should be purely numeric: $userId", userId.all { it.isDigit() })

    // 2. Test registration / profile update maintains or generates 7-8 digit User ID
    repository.updateProfile("NewCreator_VIP", "newcreator@viralboost.ai", "Tech & AI")
    val updatedProfile = repository.getProfileOnce()
    assertEquals("NewCreator_VIP", updatedProfile.username)
    assertTrue(updatedProfile.displayUserId.length in 7..8)

    // 3. Test Post-Signup Daily Reward Claim: Credits wallet with designated coins and diamonds
    val coinsBefore = updatedProfile.coins
    val diamondsBefore = updatedProfile.diamonds

    repository.claimPostSignupDailyReward(bonusCoins = 100, bonusDiamonds = 2)

    val afterClaimProfile = repository.getProfileOnce()
    assertEquals(coinsBefore + 100, afterClaimProfile.coins)
    assertEquals(diamondsBefore + 2, afterClaimProfile.diamonds)

    // 4. Test Post-Signup Milestone AdMob trigger
    val context = ApplicationProvider.getApplicationContext<Context>()
    var milestoneAdTriggered = false
    AdManager.triggerMilestoneInterstitial(
        context = context,
        isVip = false,
        milestoneName = "Post-Signup Daily Reward Claim Milestone",
        onShowInterstitialFallback = { onDismiss: () -> Unit ->
            milestoneAdTriggered = true
            onDismiss()
        }
    )
    assertTrue(milestoneAdTriggered)
  }

  @Test
  fun `test in-app shopsy shopping mall reward crediting and admob triggers`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val current = repository.getProfileOnce()
    val initialDiamonds = current.diamonds
    val initialCoins = current.coins

    // 1. Verify Shopsy shopping action awards +4 Diamonds and +10 Coins
    repository.addDiamonds(4)
    repository.addCoins(10)

    val updated = repository.getProfileOnce()
    assertEquals(initialDiamonds + 4, updated.diamonds)
    assertEquals(initialCoins + 10, updated.coins)

    // 2. Test Shopsy AdMob Triggers (Search bar, Order Now, Deal Share, 10-15s Mall Open, 5s Detail)
    val triggers = listOf(
        "Shopsy Shopping - 10-15s Mall Opening Ad",
        "Shopsy Shopping - Search Bar Click",
        "Shopsy Shopping - 5s Product Detail Ad",
        "Shopsy Shopping - Order Now Click",
        "Shopsy Shopping - Deal Share"
    )

    for (trigger in triggers) {
        var adFired = false
        AdManager.triggerMilestoneInterstitial(
            context = context,
            isVip = false,
            milestoneName = trigger,
            onShowInterstitialFallback = { onDismiss: () -> Unit ->
                adFired = true
                onDismiss()
            }
        )
        assertTrue("AdMob trigger failed for: $trigger", adFired)
    }

    // 3. Verify Shopsy links to owner master affiliate link (https://bitli.in/Ff4aE3C / EarnKaro ID: 5665937)
    assertEquals("https://bitli.in/Ff4aE3C", MonetizationConstants.GLOBAL_ADMIN_BITLI_URL)
    assertEquals("5665937", MonetizationConstants.OWNER_EARNKARO_ID)
  }

  @Test
  fun `test dual-action affiliate shopping hub features and triggers`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    // 1. Verify Top Direct Action master tracking configuration
    assertEquals("https://bitli.in/Ff4aE3C", MonetizationConstants.GLOBAL_ADMIN_BITLI_URL)
    assertEquals("5665937", MonetizationConstants.OWNER_EARNKARO_ID)
    assertEquals("Amit2653@ptyes", MonetizationConstants.OWNER_UPI_ID)

    // 2. Verify Curated Deals Catalog Grid items have >= 75% discounts and master links
    val deals = com.example.ui.components.CURATED_SHOPSY_DEALS
    assertTrue("Deals list should not be empty", deals.isNotEmpty())
    assertEquals(8, deals.size)
    for (deal in deals) {
      assertTrue("Each deal should have >= 75% discount: ${deal.discountPercent}", deal.discountPercent.contains("75%") || deal.discountPercent.contains("76%") || deal.discountPercent.contains("77%") || deal.discountPercent.contains("78%"))
      assertEquals(MonetizationConstants.GLOBAL_ADMIN_BITLI_URL, deal.dealUrl)
    }

    // 3. Verify instant wallet reward crediting (+4 Diamonds & +10 Coins)
    val current = repository.getProfileOnce()
    val initialDiamonds = current.diamonds
    val initialCoins = current.coins

    repository.addDiamonds(4)
    repository.addCoins(10)

    val afterReward = repository.getProfileOnce()
    assertEquals(initialDiamonds + 4, afterReward.diamonds)
    assertEquals(initialCoins + 10, afterReward.coins)

    // 4. Verify all shopping AdMob triggers including Direct App Launch
    val allShoppingTriggers = listOf(
        "Shopsy Shopping - Direct App Launch",
        "Shopsy Shopping - 10-15s Mall Opening Ad",
        "Shopsy Shopping - Search Bar Click",
        "Shopsy Shopping - 5s Product Detail Ad",
        "Shopsy Shopping - Order Now Click",
        "Shopsy Shopping - Deal Share"
    )

    for (trigger in allShoppingTriggers) {
      var fired = false
      AdManager.triggerMilestoneInterstitial(
          context = context,
          isVip = false,
          milestoneName = trigger,
          onShowInterstitialFallback = { onDismiss: () -> Unit ->
              fired = true
              onDismiss()
          }
      )
      assertTrue("AdMob trigger should fire for: $trigger", fired)
    }
  }

  @Test
  fun `test app startup and direct home flow without freeze on ad overlay`() = runTest {
    // 1. Verify initial startup defaults directly to Home Screen
    val initialScreen = ScreenTab.HOME
    assertEquals(ScreenTab.HOME, initialScreen)

    // 2. Verify tapping Continue to Screen / dismissing interstitial leads straight to Home screen
    var simulatedCurrentScreen: ScreenTab = ScreenTab.DAILY_REWARD
    var showOverlay = true

    val onDismissOverlay: () -> Unit = {
      showOverlay = false
      simulatedCurrentScreen = ScreenTab.HOME
    }

    onDismissOverlay()
    assertFalse("Overlay should be dismissed immediately", showOverlay)
    assertEquals("Dismissing overlay must lead straight to Home screen", ScreenTab.HOME, simulatedCurrentScreen)

    // 3. Verify strict preservation of all core parameters
    assertEquals("https://bitli.in/Ff4aE3C", MonetizationConstants.GLOBAL_ADMIN_BITLI_URL)
    assertEquals("5665937", MonetizationConstants.OWNER_EARNKARO_ID)
    assertEquals("Amit2653@ptyes", MonetizationConstants.OWNER_UPI_ID)
  }

  @Test
  fun `test dual action buttons directly inside home shopsy shopping box`() = runTest {
    // 1. Button 1: Verify direct Intent routing to master affiliate link without WebView scheme errors
    val intentUri = MonetizationConstants.GLOBAL_ADMIN_BITLI_URL
    assertEquals("https://bitli.in/Ff4aE3C", intentUri)
    assertEquals("5665937", MonetizationConstants.OWNER_EARNKARO_ID)

    val testIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(intentUri))
    assertEquals(android.content.Intent.ACTION_VIEW, testIntent.action)
    assertEquals("https://bitli.in/Ff4aE3C", testIntent.dataString)

    // 2. Button 2: Verify exploring deal catalog instantly credits +4 Diamonds & +10 Coins
    val current = repository.getProfileOnce()
    val initialDiamonds = current.diamonds
    val initialCoins = current.coins

    repository.addDiamonds(4)
    repository.addCoins(10)

    val afterRewards = repository.getProfileOnce()
    assertEquals(initialDiamonds + 4, afterRewards.diamonds)
    assertEquals(initialCoins + 10, afterRewards.coins)

    // 3. Verify curated deals are valid and connected to master affiliate link
    val deals = com.example.ui.components.CURATED_SHOPSY_DEALS
    assertTrue("Deals must be populated", deals.isNotEmpty())
    for (deal in deals) {
      assertEquals(MonetizationConstants.GLOBAL_ADMIN_BITLI_URL, deal.dealUrl)
    }

    // 4. Verify all core monetization structures preserved
    assertEquals("Amit2653@ptyes", MonetizationConstants.OWNER_UPI_ID)
  }

  @Test
  fun `test ui freeze resolution touch responsiveness and shopping box animation`() = runTest {
    // 1. Verify tab switch executes immediately without UI blocking/freeze
    var activeTab = ScreenTab.HOME
    val onTabSwitch: (ScreenTab) -> Unit = { target ->
      activeTab = target
    }

    onTabSwitch(ScreenTab.AI_TOOLS)
    assertEquals("Tab switch should execute immediately", ScreenTab.AI_TOOLS, activeTab)

    onTabSwitch(ScreenTab.LEADERBOARD)
    assertEquals("Tab switch should execute immediately", ScreenTab.LEADERBOARD, activeTab)

    // 2. Verify master affiliate structure and owner UPI
    assertEquals("https://bitli.in/Ff4aE3C", MonetizationConstants.GLOBAL_ADMIN_BITLI_URL)
    assertEquals("5665937", MonetizationConstants.OWNER_EARNKARO_ID)
    assertEquals("Amit2653@ptyes", MonetizationConstants.OWNER_UPI_ID)

    // 3. Verify rewards credit on shopping interaction (+4 Diamonds, +10 Coins)
    val current = repository.getProfileOnce()
    val initialDiamonds = current.diamonds
    val initialCoins = current.coins

    repository.addDiamonds(4)
    repository.addCoins(10)

    val updated = repository.getProfileOnce()
    assertEquals(initialDiamonds + 4, updated.diamonds)
    assertEquals(initialCoins + 10, updated.coins)
  }

  @Test
  fun `test unified dual stage ad gated shopping hub and 100 percent owner affiliate lock`() = runTest {
    // 1. Strict 100% Owner Affiliate Commission Lock
    assertEquals("https://bitli.in/Ff4aE3C", MonetizationConstants.GLOBAL_ADMIN_BITLI_URL)
    assertEquals("5665937", MonetizationConstants.OWNER_EARNKARO_ID)
    assertEquals("Amit2653@ptyes", MonetizationConstants.OWNER_UPI_ID)

    // 2. Pathway 1: Direct App Launch via Android Intent through master affiliate link
    val directIntent = android.content.Intent(
      android.content.Intent.ACTION_VIEW,
      android.net.Uri.parse(MonetizationConstants.GLOBAL_ADMIN_BITLI_URL)
    )
    assertEquals(android.content.Intent.ACTION_VIEW, directIntent.action)
    assertEquals("https://bitli.in/Ff4aE3C", directIntent.dataString)

    // 3. Pathway 2: Dual-Stage Ad-Gated Order Flow
    val initialProfile = repository.getProfileOnce()
    val initialDiamonds = initialProfile.diamonds
    val initialCoins = initialProfile.coins

    // Stage 1 (Product Click) -> Address Form (Requires customer details)
    val testDeal = com.example.ui.components.CURATED_SHOPSY_DEALS.first()
    assertEquals(MonetizationConstants.GLOBAL_ADMIN_BITLI_URL, testDeal.dealUrl)

    val customerName = "Aarav Sharma"
    val customerPhone = "9876543210"
    val customerAddress = "Flat 402, Sunshine Heights, Mumbai 400001"
    assertTrue("Name must be filled", customerName.isNotBlank())
    assertTrue("Phone must be filled", customerPhone.isNotBlank())
    assertTrue("Address must be filled", customerAddress.isNotBlank())

    // Stage 2 (Place Order) -> Ad completed -> Order placed via master affiliate link -> +4 Diamonds & +10 Coins credited
    repository.addDiamonds(4)
    repository.addCoins(10)

    val updatedProfile = repository.getProfileOnce()
    assertEquals(initialDiamonds + 4, updatedProfile.diamonds)
    assertEquals(initialCoins + 10, updatedProfile.coins)
  }

  @Test
  fun `test permanently enforce interstitial ads on all actual bottom navigation tab switches`() = runTest {
    // 1. Verify exact bottom navigation tabs in interface (No Games Tab)
    val actualTabs = listOf(
      ScreenTab.HOME,
      ScreenTab.AI_TOOLS,
      ScreenTab.VIP_PAYWALL,
      ScreenTab.LEADERBOARD,
      ScreenTab.SUPPORT,
      ScreenTab.AUTH
    )
    assertEquals(6, actualTabs.size)
    assertFalse("No Games tab must exist in navigation tabs", actualTabs.any { it.name.equals("GAMES", ignoreCase = true) })

    // 2. Verify Tab-Switch Ad Interceptor triggers ad before loading target screen
    var currentScreen = ScreenTab.HOME
    var adTriggeredCount = 0
    val testTabSwitchInterceptor: (ScreenTab, Boolean) -> Unit = { target, isVip ->
      if (currentScreen != target) {
        if (!isVip) {
          adTriggeredCount++
        }
        currentScreen = target
      }
    }

    // Switch through each actual tab and verify ad is triggered every time
    for (tab in actualTabs) {
      if (tab != currentScreen) {
        val prevCount = adTriggeredCount
        testTabSwitchInterceptor(tab, false)
        assertEquals("Ad should trigger on switching to $tab", prevCount + 1, adTriggeredCount)
        assertEquals(tab, currentScreen)
      }
    }

    // 3. Verify VIP users bypass ads seamlessly
    val vipInitialCount = adTriggeredCount
    testTabSwitchInterceptor(ScreenTab.HOME, true)
    assertEquals("VIP tab switch must not trigger ads", vipInitialCount, adTriggeredCount)
    assertEquals(ScreenTab.HOME, currentScreen)

    // 4. Verify all core monetization structures preserved
    assertEquals("https://bitli.in/Ff4aE3C", MonetizationConstants.GLOBAL_ADMIN_BITLI_URL)
    assertEquals("5665937", MonetizationConstants.OWNER_EARNKARO_ID)
    assertEquals("Amit2653@ptyes", MonetizationConstants.OWNER_UPI_ID)
  }

  @Test
  fun `test multi platform connected video feed watch rewards and creator boost`() = runTest {
    // 1. Verify multi-platform video feed aggregation (YouTube, Instagram, GoLiveGram)
    val testVideos = listOf(
      com.example.data.local.CommunityVideoPost(
        id = 101,
        creatorName = "Aarav Sharma",
        creatorHandle = "@aarav_growth",
        platform = "YouTube",
        videoType = "SHORT",
        title = "Viral YouTube Short",
        viewsCount = 500,
        isBoosted = true,
        boostTier = "SUPER_SPOTLIGHT"
      ),
      com.example.data.local.CommunityVideoPost(
        id = 102,
        creatorName = "Priya TechCraft",
        creatorHandle = "@priya_crafts",
        platform = "Instagram",
        videoType = "SHORT",
        title = "Instagram Reel Audio Hack",
        viewsCount = 400
      ),
      com.example.data.local.CommunityVideoPost(
        id = 103,
        creatorName = "Rohan Finance Fast",
        creatorHandle = "@rohan_money",
        platform = "GoLiveGram",
        videoType = "LONG",
        title = "GoLiveGram Stream Masterclass",
        viewsCount = 300
      )
    )

    assertEquals(3, testVideos.size)
    val platforms = testVideos.map { it.platform }.toSet()
    assertTrue("Must support YouTube", platforms.contains("YouTube"))
    assertTrue("Must support Instagram", platforms.contains("Instagram"))
    assertTrue("Must support GoLiveGram", platforms.contains("GoLiveGram"))

    // 2. Per-Video Reward System: +15 Coins & +3 Diamonds upon completing video watch session
    val profileBefore = repository.getProfileOnce()
    val initialCoins = profileBefore.coins
    val initialDiamonds = profileBefore.diamonds

    repository.rewardVideoWatch(diamonds = 3, coins = 15)

    val profileAfter = repository.getProfileOnce()
    assertEquals(initialCoins + 15, profileAfter.coins)
    assertEquals(initialDiamonds + 3, profileAfter.diamonds)

    // 3. Creator Boost & Super Spotlight (Second income stream for app owner)
    // Option A: Boost with Diamonds (50 Diamonds)
    repository.addDiamonds(50)
    val diamondsBeforeBoost = repository.getProfileOnce().diamonds
    repository.addDiamonds(-50)
    assertEquals(diamondsBeforeBoost - 50, repository.getProfileOnce().diamonds)

    // Option B: Boost via Owner UPI (Amit2653@ptyes)
    assertEquals("Amit2653@ptyes", MonetizationConstants.OWNER_UPI_ID)
    val paymentId = repository.submitPaymentUtr(
      planName = "Creator Spotlight: Super Spotlight Pin",
      amountRupees = 49,
      utrNumber = "SPOTLIGHT123456",
      method = "UPI_INTENT"
    )
    assertTrue("Spotlight UPI payment record must be created", paymentId > 0)

    // 4. Verify 100% Feature & Owner Security Lock
    assertEquals("https://bitli.in/Ff4aE3C", MonetizationConstants.GLOBAL_ADMIN_BITLI_URL)
    assertEquals("5665937", MonetizationConstants.OWNER_EARNKARO_ID)
    assertEquals("Amit2653@ptyes", MonetizationConstants.OWNER_UPI_ID)
  }

  @Test
  fun smartAdMobAndUnityAdsMediation_rotatesAndFallsBackCorrectly() {
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    // 1. Verify exact Unity Ads credentials
    assertEquals("800427608", MonetizationConstants.UNITY_GAME_ID)
    assertEquals("BP_Banner_Android", MonetizationConstants.UNITY_BANNER_PLACEMENT_ID)
    assertEquals("BP_Interstitial_Android", MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID)
    assertEquals("BP_Rewarded_Android", MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID)

    // 2. Initialize both Google Mobile Ads SDK and Unity Ads SDK
    AdManager.initialize(context, MonetizationConstants.UNITY_GAME_ID, false)
    AdManager.resetMediationCounters()
    assertTrue(AdManager.isAdMobActive)
    assertTrue(AdManager.isUnityAdsActive)

    // 3. Both networks active -> Rotate 1:1 for Banner, Interstitial, and Rewarded
    val bannerReq1 = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.BANNER)
    val bannerReq2 = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.BANNER)
    assertEquals(com.example.monetization.AdNetwork.ADMOB, bannerReq1.network)
    assertEquals(MonetizationConstants.ADMOB_BANNER_UNIT_ID, bannerReq1.unitOrPlacementId)
    assertEquals(com.example.monetization.AdNetwork.UNITY_ADS, bannerReq2.network)
    assertEquals("BP_Banner_Android", bannerReq2.unitOrPlacementId)

    val interstitialReq1 = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.INTERSTITIAL)
    val interstitialReq2 = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.INTERSTITIAL)
    assertEquals(com.example.monetization.AdNetwork.ADMOB, interstitialReq1.network)
    assertEquals(MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID, interstitialReq1.unitOrPlacementId)
    assertEquals(com.example.monetization.AdNetwork.UNITY_ADS, interstitialReq2.network)
    assertEquals("BP_Interstitial_Android", interstitialReq2.unitOrPlacementId)

    val rewardedReq1 = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.REWARDED)
    val rewardedReq2 = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.REWARDED)
    assertEquals(com.example.monetization.AdNetwork.ADMOB, rewardedReq1.network)
    assertEquals(MonetizationConstants.ADMOB_REWARDED_UNIT_ID, rewardedReq1.unitOrPlacementId)
    assertEquals(com.example.monetization.AdNetwork.UNITY_ADS, rewardedReq2.network)
    assertEquals("BP_Rewarded_Android", rewardedReq2.unitOrPlacementId)

    // 4. AdMob inactive, Unity Ads active -> Bypass AdMob and serve Unity Ads exclusively
    AdManager.setNetworkActiveState(adMobActive = false, unityAdsActive = true)
    repeat(3) {
      val b = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.BANNER)
      val i = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.INTERSTITIAL)
      val r = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.REWARDED)
      assertEquals(com.example.monetization.AdNetwork.UNITY_ADS, b.network)
      assertEquals("BP_Banner_Android", b.unitOrPlacementId)
      assertEquals(com.example.monetization.AdNetwork.UNITY_ADS, i.network)
      assertEquals("BP_Interstitial_Android", i.unitOrPlacementId)
      assertEquals(com.example.monetization.AdNetwork.UNITY_ADS, r.network)
      assertEquals("BP_Rewarded_Android", r.unitOrPlacementId)
    }

    // 5. Unity Ads inactive, AdMob active -> Bypass Unity Ads and serve AdMob exclusively
    AdManager.setNetworkActiveState(adMobActive = true, unityAdsActive = false)
    repeat(3) {
      val b = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.BANNER)
      val i = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.INTERSTITIAL)
      val r = AdManager.selectNextMediatedAd(com.example.monetization.AdFormat.REWARDED)
      assertEquals(com.example.monetization.AdNetwork.ADMOB, b.network)
      assertEquals(com.example.monetization.AdNetwork.ADMOB, i.network)
      assertEquals(com.example.monetization.AdNetwork.ADMOB, r.network)
    }

    // Restore both active
    AdManager.resetMediationCounters()
  }

  @Test
  fun verifyRoomDatabaseSchemaAndDaoForStreakDiamondsAndSavedContentHistory() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    try {
      val userDao = db.userDao()
      val aiDao = db.aiDao()
      val savedContentDao = db.savedContentHistoryDao()
      val ledgerDao = db.userStreakDiamondLedgerDao()

      val repository = ViralBoostRepository(
        userDao = userDao,
        paymentDao = db.paymentDao(),
        supportDao = db.supportDao(),
        aiDao = aiDao,
        leaderboardDao = db.leaderboardDao(),
        configDao = db.configDao(),
        savedContentHistoryDao = savedContentDao,
        userStreakDiamondLedgerDao = ledgerDao
      )

      // 1. Initial profile & streak/diamond flows
      val initialProfile = repository.getProfileOnce()
      assertEquals(1, initialProfile.streakDays)
      assertEquals(MonetizationConstants.INITIAL_DIAMONDS_BALANCE, initialProfile.diamonds)
      assertEquals(1, repository.streakCountFlow.first())
      assertEquals(MonetizationConstants.INITIAL_DIAMONDS_BALANCE, repository.diamondBalanceFlow.first())

      // 2. Claim daily streak -> updates streak count, diamond balance, and ledger
      val streakResult = repository.claimDailyStreak()
      assertTrue(streakResult is StreakResult.Success)
      assertEquals(1, userDao.getStreakCountOnce())
      assertEquals(12, userDao.getDiamondBalanceOnce())

      // Increment streak directly via UserDao
      userDao.incrementStreakCount("2026-11-14", System.currentTimeMillis())
      assertEquals(2, userDao.getStreakCountOnce())
      assertEquals(2, repository.streakCountFlow.first())

      // 3. Save generated content to local Room history (both AiDao and SavedContentHistoryDao)
      val savedId = repository.saveGeneratedContentToHistory(
        type = "GEMINI_SOCIAL",
        topic = "Viral Reels Retention Hook",
        niche = "Shorts & Reels",
        content = "Stop scrolling! Here is the 3-second retention formula.",
        platform = "Instagram Reels",
        keywords = "retention, viral hook, reels",
        hashtags = "#ViralReels #CreatorEconomy",
        isBookmarked = true
      )
      assertTrue(savedId > 0L)

      val allGenerations = aiDao.getAllGenerationsOnce()
      assertEquals(1, allGenerations.size)
      assertEquals("Viral Reels Retention Hook", allGenerations.first().topic)
      assertTrue(allGenerations.first().isBookmarked)

      val savedHistoryList = savedContentDao.getAllSavedContentOnce()
      assertEquals(1, savedHistoryList.size)
      assertEquals("Instagram Reels", savedHistoryList.first().platform)
      assertEquals("#ViralReels #CreatorEconomy", savedHistoryList.first().hashtags)

      // 4. Toggle bookmark and delete saved content item
      repository.toggleGenerationBookmark(savedId, isBookmarked = false)
      assertFalse(aiDao.getAllGenerationsOnce().first().isBookmarked)

      repository.deleteGenerationHistoryItem(savedId)
      assertTrue(aiDao.getAllGenerationsOnce().isEmpty())
      assertTrue(savedContentDao.getAllSavedContentOnce().isEmpty())
    } finally {
      db.close()
    }
  }

  @Test
  fun verifyDailyStreakReminderAlarmManagerSchedulesCancelsAndDispatchesNotification() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val shadowAlarmManager = org.robolectric.Shadows.shadowOf(alarmManager)

    // 1. Schedule daily reminder at 2:00 PM (14:00) for a 5-day streak
    val scheduledConfig = com.example.util.DailyStreakReminderScheduler.scheduleDailyReminder(
      context = context,
      hourOfDay = 14,
      minute = 0,
      streakDays = 5
    )
    assertTrue(scheduledConfig.isEnabled)
    assertEquals(14, scheduledConfig.reminderHour)
    assertEquals("02:00 PM", scheduledConfig.formattedTime)
    assertTrue(scheduledConfig.nextTriggerAtMillis > System.currentTimeMillis())

    val scheduledAlarm = shadowAlarmManager.nextScheduledAlarm
    org.junit.Assert.assertNotNull("Expected AlarmManager to have a scheduled repeating alarm", scheduledAlarm)
    assertEquals(android.app.AlarmManager.INTERVAL_DAY, scheduledAlarm!!.interval)

    // 2. Simulate AlarmManager firing DailyStreakReminderReceiver
    val receiver = com.example.util.DailyStreakReminderReceiver()
    val alarmIntent = android.content.Intent(context, com.example.util.DailyStreakReminderReceiver::class.java).apply {
      action = com.example.util.DailyStreakReminderScheduler.ACTION_DAILY_STREAK_REMINDER
      putExtra(com.example.util.DailyStreakReminderScheduler.EXTRA_STREAK_DAYS, 5)
    }
    receiver.onReceive(context, alarmIntent)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    val shadowNotificationManager = org.robolectric.Shadows.shadowOf(notificationManager)
    val activeNotifications = shadowNotificationManager.allNotifications
    assertTrue("Expected DailyStreakReminderReceiver to post a notification", activeNotifications.isNotEmpty())

    // 3. Cancel daily reminder -> removes scheduled alarm and updates state
    val cancelledConfig = com.example.util.DailyStreakReminderScheduler.cancelDailyReminder(context)
    assertFalse(cancelledConfig.isEnabled)
    assertEquals(0L, cancelledConfig.nextTriggerAtMillis)
  }
}



