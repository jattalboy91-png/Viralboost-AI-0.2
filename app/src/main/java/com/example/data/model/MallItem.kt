package com.example.data.model

import com.google.gson.annotations.SerializedName

/**
 * Rich Product Data Model for Shopsy-Style Mega Mall
 * Fully serialized for ShopsyApiService trending deals feed (Affiliate ID: 5665937).
 */
data class MallItem(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("title")
    val title: String = "",

    @SerializedName("category")
    val category: String = "All",

    @SerializedName("price")
    val price: String = "₹299",

    @SerializedName("oldPrice")
    val oldPrice: String = "₹999",

    @SerializedName("discount")
    val discount: String = "70% OFF",

    @SerializedName("rating")
    val rating: String = "⭐ 4.8"
)

typealias MegaMallProduct = MallItem
typealias ShoppingProduct = MallItem
