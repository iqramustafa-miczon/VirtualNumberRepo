package com.example.virtualnumber.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Country(
    @SerializedName("chn")
    val chn: String,
    @SerializedName("eng")
    val eng: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("multiService")
    val multiService: Int,
    @SerializedName("rent")
    val rent: Int,
    @SerializedName("retry")
    val retry: Int,
    @SerializedName("rus")
    val rus: String,
    @SerializedName("visible")
    val visible: Int
)