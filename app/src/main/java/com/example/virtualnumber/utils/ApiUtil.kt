package com.example.myapplication.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

// Data classes for JSON structure
data class ApiResponse(
    @SerializedName("countries") val countries: Map<String, Int>,
    @SerializedName("operators") val operators: Map<String, String>,
    @SerializedName("services") val services: Map<String, Service>
)

data class Service(
    @SerializedName("cost") val cost: Double,
    @SerializedName("retail_cost") val retailCost: String,
    @SerializedName("quant") val quant: Quantity,
    @SerializedName("search_name") val searchName: String
)

data class Quantity(
    @SerializedName("current") val current: Int,
    @SerializedName("total") val total: Int
)

// Singleton Utility class to handle data fetching and caching
// created by IQRA MUSTAFA
object JsonDataUtil {
    private var cachedData: ApiResponse? = null

    fun fetchFromApi(context: Context) {
        if (cachedData != null) return // Prevent multiple calls

        /*val inputStream = context.resources.openRawResource(R.raw.json)
        val reader = InputStreamReader(inputStream)
        cachedData = Gson().fromJson(reader, object : TypeToken<ApiResponse>() {}.type)
        reader.close()*/
    }

    fun getCountries(): Map<String, Int> {
        return cachedData?.countries ?: emptyMap()
    }

    fun getOperators(): Map<String, String> {
        return cachedData?.operators ?: emptyMap()
    }

    fun getServices(): Map<String, Service> {
        return cachedData?.services ?: emptyMap()
    }

}
