package com.example.virtualnumber.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Services(
    @SerializedName("services")
    val services: List<Service>,
    @SerializedName("status")
    val status: String
)