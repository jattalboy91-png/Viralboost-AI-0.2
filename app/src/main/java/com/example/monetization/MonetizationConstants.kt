package com.example.monetization

/**
 * Permanently locked admin, monetization, and affiliate credentials for ViralBoost AI.
 */
object MonetizationConstants {
    // 1. Persistent Admin Credentials
    const val OWNER_UPI_ID = "Amit2653@ptyes"
    const val OWNER_NAME = "ViralBoost AI Admin"
    const val OWNER_QR_CODE_URL = "https://i.postimg.cc/v8gdCzFm/IMG-20260926-151326.jpg"
    const val OWNER_ADMIN_EMAIL = "Amit2653@ptyes"
    const val OWNER_PASSKEY = "admin2653"

    // 2. AdMob Production & Test Credentials
    const val ADMOB_PUBLISHER_APP_ID = "ca-app-pub-3976359691019357"
    const val ADMOB_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val ADMOB_INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val ADMOB_REWARDED_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    const val ADMOB_NATIVE_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    const val ADMOB_NATIVE_VIDEO_UNIT_ID = "ca-app-pub-3940256099942544/1044960115"

    // 2B. Unity Ads Production Credentials (Game ID: 800427608)
    const val UNITY_GAME_ID = "800427608"
    const val UNITY_BANNER_PLACEMENT_ID = "BP_Banner_Android"
    const val UNITY_INTERSTITIAL_PLACEMENT_ID = "BP_Interstitial_Android"
    const val UNITY_REWARDED_PLACEMENT_ID = "BP_Rewarded_Android"

    // 3. Creator Affiliate Tag
    const val AFFILIATE_TAG = "5665937"

    // 4. Strict Economy Parameters
    const val DAILY_STREAK_DIAMOND_REWARD = 2 // Strictly fixed, no exponential scaling
    const val REWARDED_AD_DIAMOND_REWARD = 5   // Strictly fixed 5 diamonds per ad
    const val INITIAL_COINS_BALANCE = 250
    const val INITIAL_DIAMONDS_BALANCE = 10

    // Diamond Utility & Redemption Discount Values (~5 diamonds = ₹1-₹2 discount)
    const val DIAMONDS_PER_RUPEE_DISCOUNT = 3.0 // e.g. 50 diamonds = ~₹16 discount, 100 = ~₹33 discount
    const val FULL_VIP_REDEMPTION_DIAMONDS = 500 // Direct 100% redemption for 1 month VIP

    // Pricing (in INR ₹)
    const val PRICE_PASS_10_DAYS = 29
    const val PRICE_PASS_15_DAYS = 49
    const val PRICE_VIP_MONTHLY = 179
    const val PRICE_VIP_3_MONTHS_PRO = 399

    // Free Tier Daily Generation Limit
    const val FREE_TIER_MAX_DAILY_GENERATIONS = 2

    // 5. Affiliate E-Commerce Rewards & Commission Tracking
    const val AFFILIATE_PRODUCT_SHARE_COINS_REWARD = 10     // +10 Coins per social share
    const val AFFILIATE_PRODUCT_PURCHASE_DIAMONDS_REWARD = 3 // +3 Diamonds per product purchase

    // Global Admin Bitli Link & Permanent Owner Affiliate Link (EarnKaro ID: 5665937)
    const val OWNER_EARNKARO_ID = "5665937"
    const val GLOBAL_ADMIN_BITLI_URL = "https://bitli.in/Ff4aE3C"
    const val OFFICIAL_STORE_DOWNLOAD_URL = "https://bitli.in/Ff4aE3C"
    const val PERMANENT_OWNER_AFFILIATE_LINK = "https://bitli.in/Ff4aE3C"

    // High-Conversion Promotional Caption designed to drive immediate shopping traffic
    const val HIGH_CONVERSION_SHOPPING_CAPTION =
        "🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!\n" +
        "😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:\n" +
        "👉 https://bitli.in/Ff4aE3C"

    const val PREFS_NAME = "viralboost_social_links"
    const val PREF_KEY_USER_PERSONAL_AFFILIATE_LINK = "user_personal_affiliate_link"

    /**
     * Retrieves the creator's saved personal affiliate link from Affiliate Hub.
     */
    fun getUserPersonalAffiliateLink(context: android.content.Context): String {
        val sp = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        return sp.getString(PREF_KEY_USER_PERSONAL_AFFILIATE_LINK, "")?.trim().orEmpty()
    }

    /**
     * Saves creator's personal affiliate link in Affiliate Hub.
     */
    fun setUserPersonalAffiliateLink(context: android.content.Context, link: String) {
        val sp = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        sp.edit().putString(PREF_KEY_USER_PERSONAL_AFFILIATE_LINK, link.trim()).apply()
    }

    /**
     * Clears creator's personal affiliate link, reverting to owner master link fallback.
     */
    fun clearUserPersonalAffiliateLink(context: android.content.Context) {
        val sp = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        sp.edit().remove(PREF_KEY_USER_PERSONAL_AFFILIATE_LINK).apply()
    }

    /**
     * Determines whether user has a valid personal affiliate link configured.
     */
    fun hasPersonalAffiliateLink(personalLink: String?): Boolean {
        val trimmed = personalLink?.trim().orEmpty()
        return trimmed.isNotBlank() && (trimmed.startsWith("http://") || trimmed.startsWith("https://"))
    }

    /**
     * Resolves the active affiliate link:
     * - If user has their own personal link saved, returns their link for their earnings.
     * - If user does not have or has not entered a link, automatically falls back to Owner Master Link (https://bitli.in/Ff4aE3C tied to EarnKaro ID: 5665937).
     */
    fun getEffectiveAffiliateLink(personalLink: String?): String {
        return if (hasPersonalAffiliateLink(personalLink)) {
            personalLink!!.trim()
        } else {
            PERMANENT_OWNER_AFFILIATE_LINK
        }
    }

    /**
     * Builds complete high-conversion video caption:
     * - Fallback to Owner Master Link: If user has not entered/saved a personal link, attaches https://bitli.in/Ff4aE3C with high-conversion urgency hook and app footer.
     * - If user does have their own link saved, uses their link for their earnings while maintaining background owner structure.
     */
    fun buildSmartAffiliateCaption(
        title: String,
        tags: String,
        description: String,
        personalAffiliateLink: String?,
        downloadUrl: String = OFFICIAL_STORE_DOWNLOAD_URL
    ): String {
        val hasPersonal = hasPersonalAffiliateLink(personalAffiliateLink)
        val activeLink = if (hasPersonal) personalAffiliateLink!!.trim() else PERMANENT_OWNER_AFFILIATE_LINK

        return buildString {
            if (title.isNotBlank()) {
                append(title.trim())
                append("\n\n")
            }
            if (description.isNotBlank()) {
                append(description.trim())
                append("\n\n")
            }
            if (tags.isNotBlank()) {
                append(tags.trim())
                append("\n\n")
            }
            append("━━━━━━━━━━━━━━━━━━━━━━\n")
            append("🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!\n")
            append("😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:\n")
            append("👉 $activeLink\n\n")
            if (hasPersonal && activeLink != PERMANENT_OWNER_AFFILIATE_LINK) {
                append("🌟 Featured Mega Store & Bonus Deals:\n")
                append("👉 $PERMANENT_OWNER_AFFILIATE_LINK\n\n")
            }
            append("🚀 Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:\n")
            append("👉 $downloadUrl")
        }
    }

    fun buildHighConversionVideoFooter(downloadUrl: String = OFFICIAL_STORE_DOWNLOAD_URL): String {
        return buildString {
            append("🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!\n")
            append("😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:\n")
            append("👉 $PERMANENT_OWNER_AFFILIATE_LINK\n\n")
            append("🚀 Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:\n")
            append("👉 $downloadUrl")
        }
    }

    fun buildMeeshoAffiliateUrl(productSlug: String, tag: String = AFFILIATE_TAG): String {
        val cleanTag = tag.trim().ifBlank { AFFILIATE_TAG }
        return "https://www.meesho.com/$productSlug?referral_code=$cleanTag&source=viralboost_app"
    }

    fun buildFlipkartAffiliateUrl(productSlug: String, tag: String = AFFILIATE_TAG): String {
        val cleanTag = tag.trim().ifBlank { AFFILIATE_TAG }
        return "https://www.flipkart.com/$productSlug?affid=$cleanTag&affExtParam1=viralboost"
    }

    fun buildAmazonAffiliateUrl(productSlug: String, tag: String = AFFILIATE_TAG): String {
        val cleanTag = tag.trim().ifBlank { AFFILIATE_TAG }
        return "https://www.amazon.in/dp/$productSlug?tag=$cleanTag&linkCode=ll1"
    }

    fun buildMyntraAffiliateUrl(productSlug: String, tag: String = AFFILIATE_TAG): String {
        val cleanTag = tag.trim().ifBlank { AFFILIATE_TAG }
        return "https://www.myntra.com/$productSlug?affid=$cleanTag&utm_source=viralboost"
    }

    fun buildEarnKaroAffiliateUrl(productSlug: String, tag: String = AFFILIATE_TAG): String {
        val cleanTag = tag.trim().ifBlank { AFFILIATE_TAG }
        return "https://earnkaro.com/deal/$productSlug?r=$cleanTag"
    }

    const val APP_DOWNLOAD_URL = "https://viralboost.ai/download"

    fun buildAppDownloadUrl(tag: String = AFFILIATE_TAG): String {
        val cleanTag = tag.trim().ifBlank { AFFILIATE_TAG }
        return "https://viralboost.ai/download?ref=$cleanTag"
    }
}
