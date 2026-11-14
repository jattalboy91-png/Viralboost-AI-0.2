package com.example

import com.example.monetization.AdFormat
import com.example.monetization.AdManager
import com.example.monetization.AdNetwork
import com.example.monetization.MonetizationConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdMediationUnitTest {

    @Test
    fun verifyUnityAdsAndAdMobCredentialsAndSmartMediation() {
        // 1. Verify exact Unity Ads credentials
        assertEquals("800427608", MonetizationConstants.UNITY_GAME_ID)
        assertEquals("BP_Banner_Android", MonetizationConstants.UNITY_BANNER_PLACEMENT_ID)
        assertEquals("BP_Interstitial_Android", MonetizationConstants.UNITY_INTERSTITIAL_PLACEMENT_ID)
        assertEquals("BP_Rewarded_Android", MonetizationConstants.UNITY_REWARDED_PLACEMENT_ID)

        // 2. Reset mediation state with both networks active
        AdManager.resetMediationCounters()
        assertTrue(AdManager.isAdMobActive)
        assertTrue(AdManager.isUnityAdsActive)

        // 3. Both networks active -> Rotate 1:1 (Request 1 = AdMob, Request 2 = Unity Ads)
        val banner1 = AdManager.selectNextMediatedAd(AdFormat.BANNER)
        val banner2 = AdManager.selectNextMediatedAd(AdFormat.BANNER)
        assertEquals(AdNetwork.ADMOB, banner1.network)
        assertEquals(MonetizationConstants.ADMOB_BANNER_UNIT_ID, banner1.unitOrPlacementId)
        assertEquals(AdNetwork.UNITY_ADS, banner2.network)
        assertEquals("BP_Banner_Android", banner2.unitOrPlacementId)

        val interstitial1 = AdManager.selectNextMediatedAd(AdFormat.INTERSTITIAL)
        val interstitial2 = AdManager.selectNextMediatedAd(AdFormat.INTERSTITIAL)
        assertEquals(AdNetwork.ADMOB, interstitial1.network)
        assertEquals(MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID, interstitial1.unitOrPlacementId)
        assertEquals(AdNetwork.UNITY_ADS, interstitial2.network)
        assertEquals("BP_Interstitial_Android", interstitial2.unitOrPlacementId)

        val rewarded1 = AdManager.selectNextMediatedAd(AdFormat.REWARDED)
        val rewarded2 = AdManager.selectNextMediatedAd(AdFormat.REWARDED)
        assertEquals(AdNetwork.ADMOB, rewarded1.network)
        assertEquals(MonetizationConstants.ADMOB_REWARDED_UNIT_ID, rewarded1.unitOrPlacementId)
        assertEquals(AdNetwork.UNITY_ADS, rewarded2.network)
        assertEquals("BP_Rewarded_Android", rewarded2.unitOrPlacementId)

        // 4. AdMob inactive, Unity Ads active -> Bypass AdMob and serve Unity Ads exclusively
        AdManager.setNetworkActiveState(adMobActive = false, unityAdsActive = true)
        repeat(3) {
            val b = AdManager.selectNextMediatedAd(AdFormat.BANNER)
            val i = AdManager.selectNextMediatedAd(AdFormat.INTERSTITIAL)
            val r = AdManager.selectNextMediatedAd(AdFormat.REWARDED)
            assertEquals(AdNetwork.UNITY_ADS, b.network)
            assertEquals("BP_Banner_Android", b.unitOrPlacementId)
            assertEquals(AdNetwork.UNITY_ADS, i.network)
            assertEquals("BP_Interstitial_Android", i.unitOrPlacementId)
            assertEquals(AdNetwork.UNITY_ADS, r.network)
            assertEquals("BP_Rewarded_Android", r.unitOrPlacementId)
        }

        // 5. Unity Ads inactive, AdMob active -> Bypass Unity Ads and serve AdMob exclusively
        AdManager.setNetworkActiveState(adMobActive = true, unityAdsActive = false)
        repeat(3) {
            val b = AdManager.selectNextMediatedAd(AdFormat.BANNER)
            val i = AdManager.selectNextMediatedAd(AdFormat.INTERSTITIAL)
            val r = AdManager.selectNextMediatedAd(AdFormat.REWARDED)
            assertEquals(AdNetwork.ADMOB, b.network)
            assertEquals(AdNetwork.ADMOB, i.network)
            assertEquals(AdNetwork.ADMOB, r.network)
        }

        // Restore default active state
        AdManager.resetMediationCounters()
    }

    @Test
    fun verifyDynamicRoomDbUnityConfigSyncAndFallback() {
        val defaultConfig = com.example.data.local.AppConfig()
        assertEquals("800427608", defaultConfig.unityGameId)
        assertEquals("BP_Banner_Android", defaultConfig.unityBannerPlacementId)
        assertEquals("BP_Interstitial_Android", defaultConfig.unityInterstitialPlacementId)
        assertEquals("BP_Rewarded_Android", defaultConfig.unityRewardedPlacementId)

        // Simulate owner editing and saving custom Unity Ads credentials in Admin Panel -> Room DB
        val customConfig = defaultConfig.copy(
            unityGameId = "900555111",
            unityBannerPlacementId = "Custom_Banner_Placement",
            unityInterstitialPlacementId = "Custom_Interstitial_Placement",
            unityRewardedPlacementId = "Custom_Rewarded_Placement"
        )
        AdManager.syncCredentialsFromConfig(customConfig, null)
        AdManager.resetMediationCounters()

        assertEquals("900555111", AdManager.activeUnityGameId)
        assertEquals("Custom_Banner_Placement", AdManager.activeUnityBannerPlacementId)
        assertEquals("Custom_Interstitial_Placement", AdManager.activeUnityInterstitialPlacementId)
        assertEquals("Custom_Rewarded_Placement", AdManager.activeUnityRewardedPlacementId)

        // Request 1 = AdMob, Request 2 = Unity Ads (must pick up dynamic custom IDs!)
        AdManager.selectNextMediatedAd(AdFormat.BANNER)
        val unityBannerDecision = AdManager.selectNextMediatedAd(AdFormat.BANNER)
        assertEquals(AdNetwork.UNITY_ADS, unityBannerDecision.network)
        assertEquals("900555111", unityBannerDecision.unityGameId)
        assertEquals("Custom_Banner_Placement", unityBannerDecision.unitOrPlacementId)

        AdManager.selectNextMediatedAd(AdFormat.INTERSTITIAL)
        val unityInterstitialDecision = AdManager.selectNextMediatedAd(AdFormat.INTERSTITIAL)
        assertEquals(AdNetwork.UNITY_ADS, unityInterstitialDecision.network)
        assertEquals("900555111", unityInterstitialDecision.unityGameId)
        assertEquals("Custom_Interstitial_Placement", unityInterstitialDecision.unitOrPlacementId)

        AdManager.selectNextMediatedAd(AdFormat.REWARDED)
        val unityRewardedDecision = AdManager.selectNextMediatedAd(AdFormat.REWARDED)
        assertEquals(AdNetwork.UNITY_ADS, unityRewardedDecision.network)
        assertEquals("900555111", unityRewardedDecision.unityGameId)
        assertEquals("Custom_Rewarded_Placement", unityRewardedDecision.unitOrPlacementId)

        // Restore default config
        AdManager.syncCredentialsFromConfig(defaultConfig, null)
        AdManager.resetMediationCounters()
    }

    @Test
    fun verifyMaterial3DynamicThemeSwitcherTogglesDarkAndLightMode() {
        // Default theme mode is Dark
        com.example.ui.theme.ThemeController.setDarkMode(true, null)
        assertTrue(com.example.ui.theme.ThemeController.isDarkMode)
        assertTrue(com.example.ui.theme.ThemeController.isDarkModeFlow.value)
        assertEquals(com.example.ui.theme.AppThemeMode.DARK, com.example.ui.theme.ThemeController.currentMode)
        assertEquals(com.example.ui.theme.PureDarkBackground, com.example.ui.theme.DarkBackground)

        // Toggle to Light mode
        com.example.ui.theme.ThemeController.toggleTheme(null)
        assertEquals(false, com.example.ui.theme.ThemeController.isDarkMode)
        assertEquals(false, com.example.ui.theme.ThemeController.isDarkModeFlow.value)
        assertEquals(com.example.ui.theme.AppThemeMode.LIGHT, com.example.ui.theme.ThemeController.currentMode)
        assertEquals(com.example.ui.theme.LightBackground, com.example.ui.theme.DarkBackground)

        // Toggle back to Dark mode
        com.example.ui.theme.ThemeController.toggleTheme(null)
        assertTrue(com.example.ui.theme.ThemeController.isDarkMode)
        assertTrue(com.example.ui.theme.ThemeController.isDarkModeFlow.value)
        assertEquals(com.example.ui.theme.AppThemeMode.DARK, com.example.ui.theme.ThemeController.currentMode)
        assertEquals(com.example.ui.theme.PureDarkBackground, com.example.ui.theme.DarkBackground)
    }

    @Test
    fun verifyUnityAdsRewardedDiamondGrantFlow() {
        val initialWatched = AdManager.totalUnityRewardedAdsWatched.value
        val initialDiamonds = AdManager.totalUnityRewardedDiamondsEarned.value

        AdManager.recordUnityRewardedDiamondGrant(5)

        assertEquals(initialWatched + 1, AdManager.totalUnityRewardedAdsWatched.value)
        assertEquals(initialDiamonds + 5, AdManager.totalUnityRewardedDiamondsEarned.value)
        assertEquals(
            com.example.monetization.UnityRewardedAdStatus.REWARD_GRANTED,
            AdManager.unityRewardedStatus.value
        )
    }

    @Test
    fun verifySocialShareHelperPayloads() {
        val aiShareText = com.example.util.SocialShareHelper.buildAiContentShareText(
            platform = "Instagram Reels",
            format = "Viral 60s Script",
            hook = "Stop scrolling right now!",
            bodyCaption = "Here is the 3-step blueprint.",
            callToAction = "Comment VIRAL below!",
            hashtags = listOf("#ViralBoostAI", "#ReelsGrowth")
        )
        assertTrue(aiShareText.contains("Instagram Reels"))
        assertTrue(aiShareText.contains("Stop scrolling right now!"))
        assertTrue(aiShareText.contains("#ViralBoostAI #ReelsGrowth"))

        val streakShareText = com.example.util.SocialShareHelper.buildStreakMilestoneShareText(
            streakDays = 7,
            nextMilestone = 14,
            milestoneBonus = 20,
            dailyReward = 2,
            username = "CreatorPro"
        )
        assertTrue(streakShareText.contains("7-Day Creator Streak Unlocked"))
        assertTrue(streakShareText.contains("CreatorPro"))
        assertTrue(streakShareText.contains("Next Milestone: Day 14 (+20 💎 Bonus)"))
    }
}



