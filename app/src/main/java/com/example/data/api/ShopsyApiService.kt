package com.example.data.api

import com.example.data.model.MallItem
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

/**
 * Retrofit API Service for fetching real trending deals for Shopsy Mega Mall
 * pre-tagged with Affiliate ID: 5665937.
 */
interface ShopsyApiService {
    @GET("v1/products/trending-deals?tag=5665937") // आपकी एफिलिएट आईडी वाला फीड यूआरएल
    fun getTrendingProducts(): Call<List<MallItem>>
}

/**
 * Singleton client for ShopsyApiService with Gson Converter
 */
object ShopsyApiClient {
    private const val BASE_URL = "https://viralboost.ai/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    val service: ShopsyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShopsyApiService::class.java)
    }

    /**
     * Default curated trending products pre-tagged with Affiliate ID 5665937.
     * Guaranteed instant load and reliable offline/safety fallback.
     */
    val defaultTrendingProducts: List<MallItem> = listOf(
        MallItem(
            id = 1,
            title = "Anarkali Embroidered Kurti & Dupatta Set",
            category = "Ethnic Fashion",
            price = "₹349",
            oldPrice = "₹1,499",
            discount = "77% OFF",
            rating = "★ 4.6"
        ),
        MallItem(
            id = 2,
            title = "Pro Wireless Noise-Cancelling Earbuds",
            category = "Audio & Tech",
            price = "₹499",
            oldPrice = "₹2,299",
            discount = "78% OFF",
            rating = "★ 4.8"
        ),
        MallItem(
            id = 3,
            title = "Lightweight Breathable Running Sports Shoes",
            category = "Footwear",
            price = "₹399",
            oldPrice = "₹1,599",
            discount = "75% OFF",
            rating = "★ 4.5"
        ),
        MallItem(
            id = 4,
            title = "Smart Touch Fitness Watch HR+SpO2",
            category = "Smart Gadgets",
            price = "₹599",
            oldPrice = "₹2,499",
            discount = "76% OFF",
            rating = "★ 4.7"
        ),
        MallItem(
            id = 5,
            title = "Waterproof 10-inch LED Ring Light & Tripod",
            category = "Creator Gear",
            price = "₹299",
            oldPrice = "₹1,299",
            discount = "75% OFF",
            rating = "★ 4.8"
        ),
        MallItem(
            id = 6,
            title = "3-Piece Non-Stick Granite Cookware Set",
            category = "Home & Kitchen",
            price = "₹799",
            oldPrice = "₹3,199",
            discount = "75% OFF",
            rating = "★ 4.5"
        ),
        MallItem(
            id = 7,
            title = "Casual Slim-Fit Cotton Shirts (Combo of 2)",
            category = "Ethnic Fashion",
            price = "₹499",
            oldPrice = "₹1,999",
            discount = "75% OFF",
            rating = "★ 4.4"
        ),
        MallItem(
            id = 8,
            title = "20000mAh Fast Charging Power Bank 22.5W",
            category = "Mobile Accessories",
            price = "₹649",
            oldPrice = "₹2,599",
            discount = "75% OFF",
            rating = "★ 4.9"
        ),
        MallItem(
            id = 9,
            title = "Designer Women Handbag & Sling Set",
            category = "Fashion & Bags",
            price = "₹329",
            oldPrice = "₹1,399",
            discount = "76% OFF",
            rating = "★ 4.6"
        ),
        MallItem(
            id = 10,
            title = "Professional Cordless Hair Trimmer for Men",
            category = "Electronics",
            price = "₹449",
            oldPrice = "₹1,799",
            discount = "75% OFF",
            rating = "★ 4.7"
        )
    )
}
