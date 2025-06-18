package com.example.virtualnumber.data

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class NumberDetails(
    @SerializedName("status") @Expose val status: String,
    @SerializedName("phone") @Expose val phone: String,
    @SerializedName("phone_valid") @Expose val phoneValid: Boolean,
    @SerializedName("phone_type") @Expose val phoneType: String,
    @SerializedName("phone_region") @Expose val phoneRegion: String,
    @SerializedName("country") @Expose val country: String,
    @SerializedName("country_code") @Expose val countryCode: String,
    @SerializedName("country_prefix") @Expose val countryPrefix: String,
    @SerializedName("international_number") @Expose val internationalNumber: String,
    @SerializedName("local_number") @Expose val localNumber: String,
    @SerializedName("e164") @Expose val e164: String,
    @SerializedName("carrier") @Expose val carrier: String,
)