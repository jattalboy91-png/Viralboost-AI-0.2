package com.example.data.model

import com.example.monetization.MonetizationConstants

/**
 * Data model for trending & affordable affiliate e-commerce products
 * across Meesho, Flipkart, and Amazon.
 */
data class AffiliateProduct(
    val id: String,
    val title: String,
    val category: String, // "Kurtis & Ethnic", "Fashion & Jewelry", "Daily Utilities & Gadgets"
    val platform: String, // "Meesho", "Flipkart", "Amazon"
    val priceRupees: Int,
    val originalPriceRupees: Int,
    val discountPercent: Int,
    val rating: Double,
    val reviewCount: Int,
    val imageUrl: String,
    val productSlug: String,
    val description: String,
    val badge: String = "TRENDING", // "BESTSELLER", "HOT DEAL", "BUDGET PICK", "LIMITED"
    val isFreeDelivery: Boolean = true,
    val coinsOnShare: Int = MonetizationConstants.AFFILIATE_PRODUCT_SHARE_COINS_REWARD, // +10 Coins
    val diamondsOnPurchase: Int = MonetizationConstants.AFFILIATE_PRODUCT_PURCHASE_DIAMONDS_REWARD // +3 Diamonds
) {
    /**
     * Builds the direct affiliate purchase redirect link injecting the owner's pre-configured affiliate tag.
     */
    fun getAffiliateUrl(affiliateTag: String = MonetizationConstants.AFFILIATE_TAG): String {
        val cleanTag = affiliateTag.trim().ifBlank { MonetizationConstants.AFFILIATE_TAG }
        return when (platform.uppercase()) {
            "MEESHO" -> MonetizationConstants.buildMeeshoAffiliateUrl(productSlug, cleanTag)
            "FLIPKART" -> MonetizationConstants.buildFlipkartAffiliateUrl(productSlug, cleanTag)
            "AMAZON" -> MonetizationConstants.buildAmazonAffiliateUrl(productSlug, cleanTag)
            "MYNTRA" -> MonetizationConstants.buildMyntraAffiliateUrl(productSlug, cleanTag)
            "EARNKARO" -> MonetizationConstants.buildEarnKaroAffiliateUrl(productSlug, cleanTag)
            else -> "https://viralboost.ai/shop/$productSlug?tag=$cleanTag"
        }
    }

    /**
     * Formats high-converting viral social sharing copy with Auto-Branding footer for Instagram, Facebook, WhatsApp, Telegram, etc.
     */
    fun getShareText(affiliateTag: String = MonetizationConstants.AFFILIATE_TAG): String {
        val productUrl = getAffiliateUrl(affiliateTag)
        val downloadUrl = MonetizationConstants.buildAppDownloadUrl(affiliateTag)
        return """
            🔥 Trending Deal Alert: $title
            💰 Deal Price: ₹$priceRupees (Flat $discountPercent% OFF! MRP: ₹$originalPriceRupees)
            ⭐ Customer Rating: $rating/5.0 ($reviewCount verified reviews)
            🚚 Free Express Cash On Delivery Available!
            🛍️ Tap to Order on $platform: $productUrl

            ━━━━━━━━━━━━━━━━━━━━━━
            🔥 महासेल! सबसे कम दाम और भारी डिस्काउंट पर खरीदें - अभी क्लिक करें!
            😍 Best Quality & Super Discount Deals! Don't miss out, shop now here:
            👉 ${MonetizationConstants.PERMANENT_OWNER_AFFILIATE_LINK}

            🚀 Created with ViralBoost AI! Download the app to skyrocket your reach and start earning:
            👉 $downloadUrl
        """.trimIndent()
    }
}

/**
 * Curated catalog of trending, high-converting, affordable products
 * focusing on trending Kurtis, fashion accessories, and everyday smart utilities.
 */
object SampleAffiliateProducts {
    val catalog = listOf(
        AffiliateProduct(
            id = "KURTI_01",
            title = "Anarkali Embroidered Kurti Set with Dupatta",
            category = "Kurtis & Ethnic",
            platform = "Meesho",
            priceRupees = 349,
            originalPriceRupees = 899,
            discountPercent = 61,
            rating = 4.8,
            reviewCount = 14200,
            imageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=600&auto=format&fit=crop&q=80",
            productSlug = "anarkali-embroidered-kurti-set-meesho-deal",
            description = "Intricate floral zari embroidery on soft breathable rayon fabric. Includes matching dupatta. Perfect for festive gatherings, casual reels, and daily elegance.",
            badge = "🔥 TOP TRENDING"
        ),
        AffiliateProduct(
            id = "KURTI_02",
            title = "Pure Cotton Straight Floral Print Kurti",
            category = "Kurtis & Ethnic",
            platform = "Flipkart",
            priceRupees = 279,
            originalPriceRupees = 699,
            discountPercent = 60,
            rating = 4.7,
            reviewCount = 9450,
            imageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600&auto=format&fit=crop&q=80",
            productSlug = "pure-cotton-straight-floral-kurti-flipkart",
            description = "100% combed cotton comfort fit kurti with vibrant non-fade screen print. Breathable all-day wear with calf-length silhouette.",
            badge = "⭐ BESTSELLER"
        ),
        AffiliateProduct(
            id = "KURTI_03",
            title = "Festive Chikankari Georgette Kurta with Inner",
            category = "Kurtis & Ethnic",
            platform = "Meesho",
            priceRupees = 399,
            originalPriceRupees = 999,
            discountPercent = 60,
            rating = 4.9,
            reviewCount = 18200,
            imageUrl = "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600&auto=format&fit=crop&q=80",
            productSlug = "chikankari-georgette-kurta-meesho-viral",
            description = "Handcrafted Lucknowi Chikankari shadow work on sheer georgette with matching dyed cotton slip. Extremely popular on Instagram fashion reels.",
            badge = "💎 HOT DEAL"
        ),
        AffiliateProduct(
            id = "KURTI_04",
            title = "Daily Casual Mandarin Collar Rayon Kurti",
            category = "Kurtis & Ethnic",
            platform = "Amazon",
            priceRupees = 249,
            originalPriceRupees = 599,
            discountPercent = 58,
            rating = 4.6,
            reviewCount = 6800,
            imageUrl = "https://images.unsplash.com/photo-1518049362265-d5b2a6467637?w=600&auto=format&fit=crop&q=80",
            productSlug = "daily-mandarin-collar-rayon-kurti-amazon",
            description = "Minimalist office and college wear kurti with 3/4th roll-up sleeves and wooden button accents. Machine washable and shrink-resistant.",
            badge = "🏷️ BUDGET PICK"
        ),
        AffiliateProduct(
            id = "FASH_01",
            title = "Oxidized Silver Vintage Jhumka & Choker Set",
            category = "Fashion & Jewelry",
            platform = "Meesho",
            priceRupees = 149,
            originalPriceRupees = 399,
            discountPercent = 62,
            rating = 4.8,
            reviewCount = 22100,
            imageUrl = "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=600&auto=format&fit=crop&q=80",
            productSlug = "oxidized-silver-jhumka-choker-set-meesho",
            description = "Boho tribal style German silver polish jewelry set with intricate bells and ghungroos. Lightweight, anti-tarnish, hypoallergenic finish.",
            badge = "🔥 VIRAL ACCESSORY"
        ),
        AffiliateProduct(
            id = "FASH_02",
            title = "Korean Minimalist Crossbody Sling Bag",
            category = "Fashion & Jewelry",
            platform = "Flipkart",
            priceRupees = 289,
            originalPriceRupees = 799,
            discountPercent = 64,
            rating = 4.7,
            reviewCount = 10400,
            imageUrl = "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&auto=format&fit=crop&q=80",
            productSlug = "korean-minimalist-crossbody-sling-bag",
            description = "Premium vegan PU leather compact shoulder bag with gold-tone hardware and adjustable strap. Holds phone, keys, makeup, and wallet.",
            badge = "⭐ TRENDING"
        ),
        AffiliateProduct(
            id = "FASH_03",
            title = "Vintage Minimalist Leather Quartz Watch",
            category = "Fashion & Jewelry",
            platform = "Amazon",
            priceRupees = 199,
            originalPriceRupees = 599,
            discountPercent = 66,
            rating = 4.6,
            reviewCount = 7600,
            imageUrl = "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600&auto=format&fit=crop&q=80",
            productSlug = "vintage-minimalist-leather-quartz-watch",
            description = "Classic Roman dial with scratch-resistant mineral glass and stitched genuine leather band. Japanese quartz movement with long battery life.",
            badge = "⚡ STEAL DEAL"
        ),
        AffiliateProduct(
            id = "UTIL_01",
            title = "Rechargeable USB Mini Portable Blender (380ml)",
            category = "Daily Utilities & Gadgets",
            platform = "Flipkart",
            priceRupees = 379,
            originalPriceRupees = 999,
            discountPercent = 62,
            rating = 4.7,
            reviewCount = 13500,
            imageUrl = "https://images.unsplash.com/photo-1570222094114-d054a817e56b?w=600&auto=format&fit=crop&q=80",
            productSlug = "usb-mini-portable-smoothie-blender-380ml",
            description = "6-blade stainless steel motor blends smoothies, gym protein shakes, and baby food in 30 seconds. Type-C rechargeable with safety magnetic lock.",
            badge = "🚀 CREATOR FAVORITE"
        ),
        AffiliateProduct(
            id = "UTIL_02",
            title = "Automatic Smart Motion Sensor Night Light (Warm)",
            category = "Daily Utilities & Gadgets",
            platform = "Amazon",
            priceRupees = 189,
            originalPriceRupees = 499,
            discountPercent = 62,
            rating = 4.8,
            reviewCount = 15900,
            imageUrl = "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=600&auto=format&fit=crop&q=80",
            productSlug = "smart-motion-sensor-night-light-warm-led",
            description = "PIR infrared sensor lights up automatically within 3 meters. Magnetic mount attaches to stairs, wardrobes, studio backgrounds, and bedrooms.",
            badge = "💡 SMART LIVING"
        ),
        AffiliateProduct(
            id = "UTIL_03",
            title = "Foldable Aluminum Mobile & Tablet Desk Stand",
            category = "Daily Utilities & Gadgets",
            platform = "Meesho",
            priceRupees = 129,
            originalPriceRupees = 349,
            discountPercent = 63,
            rating = 4.9,
            reviewCount = 24800,
            imageUrl = "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=600&auto=format&fit=crop&q=80",
            productSlug = "foldable-aluminum-mobile-desk-stand-holder",
            description = "Dual-hinge 270° adjustable viewing angle with anti-slip silicone cushions. Perfect for watching reels, recording hands-free videos, and desk setups.",
            badge = "🏆 VALUE KING"
        ),
        AffiliateProduct(
            id = "UTIL_04",
            title = "Wireless Heavy Bass Magnetic Bluetooth Neckband",
            category = "Daily Utilities & Gadgets",
            platform = "Flipkart",
            priceRupees = 399,
            originalPriceRupees = 1199,
            discountPercent = 66,
            rating = 4.6,
            reviewCount = 17300,
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
            productSlug = "wireless-bass-magnetic-bluetooth-neckband",
            description = "30-hour playback with fast Type-C charging. 10mm dynamic bass drivers with ENC noise cancellation microphone for crystal-clear creator voiceovers.",
            badge = "🎧 AUDIO HIT"
        ),
        AffiliateProduct(
            id = "UTIL_05",
            title = "Dual-Sided Waterproof Studio Desk Mat (80x40cm)",
            category = "Daily Utilities & Gadgets",
            platform = "Amazon",
            priceRupees = 239,
            originalPriceRupees = 599,
            discountPercent = 60,
            rating = 4.8,
            reviewCount = 6100,
            imageUrl = "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=600&auto=format&fit=crop&q=80",
            productSlug = "dual-sided-waterproof-studio-desk-mat-80x40",
            description = "Smooth PU leather surface for precision mouse movement. Protects desk from scratches and spills. Beautiful aesthetic background for unboxing reels.",
            badge = "✨ AESTHETIC PICK"
        )
    )
}
