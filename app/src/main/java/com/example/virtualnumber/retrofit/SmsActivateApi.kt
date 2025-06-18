package com.example.virtualnumber.retrofit

import com.example.virtualnumber.model.Country
import com.example.virtualnumber.model.Service
import com.example.virtualnumber.model.ServiceSpecificCountry
import com.example.virtualnumber.model.Services
import com.example.virtualnumber.model.rent.RentServicesAndCountriesResponse
import com.example.virtualnumber.model.rent.rentNumberResponse.RentNumberResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SmsActivateApi {

    @GET("stubs/handler_api.php")
    suspend fun getServices(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getServicesList",
    ): Response<Services>

    @GET("stubs/handler_api.php")
    suspend fun getCountries(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getCountries"
    ): Response<Map<String, Country>>

    //getTopCountries
    @GET("stubs/handler_api.php")
    suspend fun getServiceSpecificCountries(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getTopCountriesByService",
        @Query("service") service: String
    ): Response<Map<String, ServiceSpecificCountry>>

    // temporarily activate a phone number
    @GET("stubs/handler_api.php")
    suspend fun getNumber(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getNumber",
        @Query("service") serviceCode: String,
        @Query("country") countryCode: String
    ): String // Format: "ACCESS_NUMBER:12345:79123456789"


  //  Rent a Virtual Number
    @GET("stubs/handler_api.php")
    suspend fun getRentServicesAndCountries(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getRentServicesAndCountries",
        @Query("country") country: Int = 0
    ): Response<RentServicesAndCountriesResponse>

    @GET("stubs/handler_api.php")
    suspend fun getNumberOnRent(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getRentNumber",
        @Query("service") service: String,
        @Query("rent_time") rentTime: Int = 4, // default: 4 hours
        @Query("operator") operator: String = "any",
        @Query("country") country: String = "0", // Russia default
        @Query("url") url: String = "",
        @Query("incomingCall") incomingCall: String = "false"
    ): RentNumberResponse
//

    // Check available numbers count
    @GET("stubs/handler_api.php")
    suspend fun getNumbersStatus(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getNumbersStatus",
        @Query("service") serviceCode: String? = null,
        @Query("country") countryCode: String? = null,
    ): String // Format: "0:1;1:5;" (country:count)

    // Get account balance
    @GET("stubs/handler_api.php")
    suspend fun getBalance(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getBalance",
    ): String // Format: "ACCESS_BALANCE:10.5"

    // Cancel number activation
    @GET("stubs/handler_api.php")
    suspend fun setStatus(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "setStatus",
        @Query("status") status: Int, // 8 = cancel
        @Query("id") activationId: String,
    ): String // Response: "ACCESS_CANCEL"

    // Check activation status
    @GET("stubs/handler_api.php")
    suspend fun getStatus(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getStatus",
        @Query("id") activationId: String,
    ): String // Responses: "STATUS_OK:123456", "STATUS_WAIT_CODE", etc.

    // Get current activations
    @GET("stubs/handler_api.php")
    suspend fun getActiveActivations(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getActiveActivations",
    ): String // Format: "12345:79123456789:tg;..."

    // Get country operators
    @GET("stubs/handler_api.php")
    suspend fun getOperators(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getOperators",
        @Query("country") countryCode: String,
    ): String // Format: "0:Operator1;1:Operator2;..."

    // Get pricing information
    @GET("stubs/handler_api.php")
    suspend fun getPrices(
        @Query("api_key") apiKey: String,
        @Query("action") action: String = "getPrices",
        @Query("service") serviceCode: String? = null,
        @Query("country") countryCode: String? = null,
    ): String // Detailed price list

}