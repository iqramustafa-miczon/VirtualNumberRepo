package com.example.virtualnumber.model

import com.google.gson.annotations.SerializedName

data class ServiceSpecificCountry(
    @SerializedName("physicalTotalCount") val totalCount: Int,
    @SerializedName("physicalCountForDefaultPrice") val defaultPriceCount: Int,
    @SerializedName("physicalPriceMap") val priceMap: Map<String, Int>,
    @SerializedName("retail_price") val retailPrice: Double,
    @SerializedName("country") val countryCode: Int,
    @SerializedName("price") val price: Double,
    @SerializedName("count") val count: Int
)