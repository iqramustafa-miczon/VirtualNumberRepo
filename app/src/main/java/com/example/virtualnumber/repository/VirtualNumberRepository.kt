package com.example.virtualnumber.repository

import androidx.lifecycle.LiveData
import com.example.virtualnumber.model.Country
import com.example.virtualnumber.model.Service
import com.example.virtualnumber.model.ServiceEntity
import com.example.virtualnumber.model.ServiceSpecificCountry
import com.example.virtualnumber.model.Services
import com.example.virtualnumber.model.rent.RentServicesAndCountriesResponse
import com.example.virtualnumber.model.rent.rentNumberResponse.RentNumberResponse
import com.example.virtualnumber.remote.NetworkResult
import com.example.virtualnumber.remote.RemoteDataSource
import com.example.virtualnumber.retrofit.BaseApiResponse
import com.example.virtualnumber.room.CountryDao
import com.example.virtualnumber.room.CountryEntity
import com.example.virtualnumber.room.ServiceDao
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class VirtualNumberRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val serviceDao: ServiceDao,
    private val countryDao: CountryDao
) : BaseApiResponse() {

    val countries: Flow<List<CountryEntity>> = countryDao.getCountries()

    suspend fun insertCountries(countries: List<CountryEntity>) = countryDao.insertCountries(countries)

    // NEW: expose a Flow of all stored countries
    val allCountriesFromDb: Flow<List<CountryEntity>> = countryDao.getAllCountries()


    // --- NEW: insert a list of ServiceEntity into Room ---
    suspend fun insertServices(services: List<ServiceEntity>) {
        serviceDao.insertServices(services)
    }

    // --- NEW: flow of all services from DB ---
    val allServicesFromDb: Flow<List<ServiceEntity>> = serviceDao.getAllServices()

    suspend fun getServices(apiKey: String): Flow<NetworkResult<Services>> =
        flow {
            val response = safeApiCall { remoteDataSource.getServices(apiKey) }
            emit(response)
        }.flowOn(Dispatchers.IO)

    suspend fun getCountries(apiKey: String): Flow<NetworkResult<Map<String, Country>>> {
        return flow {
            val response = safeApiCall { remoteDataSource.getCountries(apiKey) }
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getServiceSpecificCountries(apiKey: String): Flow<NetworkResult<Map<String, ServiceSpecificCountry>>> {
        return flow {
            val response = safeApiCall { remoteDataSource.getServiceSpecificCountries(apiKey) }
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
// Rent a Virtual Number
    suspend fun getRentServicesAndCountries(apiKey: String, country: Int): Flow<NetworkResult<RentServicesAndCountriesResponse>> {
        return flow {
            val response = safeApiCall { remoteDataSource.getRentServicesAndCountries(apiKey, country) }
            emit(response)
        }.flowOn(Dispatchers.IO)
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
        return remoteDataSource.getNumberOnRent(apiKey, service, rentTime, operator, country, url, incomingCall)
    }
//

    suspend fun getNumber(
        apiKey: String,
        serviceCode: String,
        countryCode: String,
    ): String {
        return remoteDataSource.getNumber(apiKey, serviceCode, countryCode)
    }

    suspend fun getNumbersStatus(
        apiKey: String,
        serviceCode: String?,
        countryCode: String?,
    ): String? {
        return remoteDataSource.getNumberStatus(apiKey, serviceCode, countryCode)
    }

    suspend fun getBalance(apiKey: String): String {
        return remoteDataSource.getBalance(apiKey)
    }

    suspend fun setStatus(apiKey: String, status: Int, activationId: String): String {
        return remoteDataSource.setStatus(apiKey, status.toString(), activationId)
    }


    suspend fun getActiveActivations(apiKey: String): String {
        return remoteDataSource.getActiveActivations(apiKey)
    }
}

