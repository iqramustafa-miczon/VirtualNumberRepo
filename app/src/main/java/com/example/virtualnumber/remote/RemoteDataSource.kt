package com.example.virtualnumber.remote

import android.util.Log
import com.example.virtualnumber.model.ServiceSpecificCountry
import com.example.virtualnumber.model.rent.RentServicesAndCountriesResponse
import com.example.virtualnumber.model.rent.rentNumberResponse.RentNumberResponse
import com.example.virtualnumber.retrofit.SmsActivateApi
import com.example.virtualnumber.utils.AppPreferences
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class RemoteDataSource(private val smsActivateAPI: SmsActivateApi) {
    // Services
    suspend fun getServices(apiKey: String) = smsActivateAPI.getServices(apiKey)

    // Countries
    suspend fun getCountries(apiKey: String) = smsActivateAPI.getCountries(apiKey)

    // getServiceSpecificCountries
    suspend fun getServiceSpecificCountries(apiKey: String): Response<Map<String, ServiceSpecificCountry>> {
        Log.d("topCountriesCheck", "Code: ${AppPreferences.getServiceCode()}")
        return smsActivateAPI.getServiceSpecificCountries(
            apiKey,
            "getTopCountriesByService",
            AppPreferences.getServiceCode()
        )
    }

//  Rent a Virtual Number
    suspend fun getRentServicesAndCountries(apiKey: String, country: Int): Response<RentServicesAndCountriesResponse> {
        return smsActivateAPI.getRentServicesAndCountries(apiKey = apiKey, country = country)
    }

    suspend fun getNumberOnRent(
        apiKey: String,
        service: String,
        rentTime: Int = 4,
        operator: String = "any",
        country: String = "0",
        url: String = "",
        incomingCall: String = "false"
    ): RentNumberResponse {
        return smsActivateAPI.getNumberOnRent(apiKey, "getRentNumber", service, rentTime, operator, country, url, incomingCall)
    }
//

    // Number Availability
    suspend fun getNumberStatus(
        apiKey: String,
        serviceCode: String? = null,
        countryCode: String? = null,
    ) = serviceCode?.let { smsActivateAPI.getNumbersStatus(apiKey, it, countryCode) }

    // Balance
    suspend fun getBalance(apiKey: String) = smsActivateAPI.getBalance(apiKey)

    suspend fun getNumber(
        apiKey: String,
        serviceCode: String,
        countryCode: String
    ) = smsActivateAPI.getNumber(apiKey, "getNumber", serviceCode, countryCode)

    // Activation Management - Cancel Activation
    suspend fun setStatus(apiKey: String, activationId: String, activationId1: String) =
        smsActivateAPI.setStatus(apiKey, status = 8, activationId = activationId)

    // Activation Status
    suspend fun getStatus(apiKey: String,status: String, activationId: String) =
        smsActivateAPI.getStatus(apiKey, status, activationId)

    // Current Activations
    suspend fun getActiveActivations(apiKey: String) =
        smsActivateAPI.getActiveActivations(apiKey)

    // Pricing
    suspend fun getPrices(
        apiKey: String,
        serviceCode: String? = null,
        countryCode: String? = null
    ) = serviceCode?.let { smsActivateAPI.getPrices(apiKey, it, countryCode) }

    // Country Operators
    suspend fun getOperators(apiKey: String,status: String, countryCode: String) =
        smsActivateAPI.getOperators(apiKey,status, countryCode)
}
