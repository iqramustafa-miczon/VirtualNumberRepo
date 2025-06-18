package com.example.virtualnumber.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Service(
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String
)