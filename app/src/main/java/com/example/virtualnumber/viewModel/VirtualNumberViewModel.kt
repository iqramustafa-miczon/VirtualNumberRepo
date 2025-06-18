package com.example.virtualnumber.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.virtualnumber.model.Country
import com.example.virtualnumber.model.ServiceEntity
import com.example.virtualnumber.model.ServiceSpecificCountry
import com.example.virtualnumber.model.Services
import com.example.virtualnumber.model.rent.RentServicesAndCountriesResponse
import com.example.virtualnumber.model.rent.rentNumberResponse.RentNumberResponse
import com.example.virtualnumber.remote.NetworkResult
import com.example.virtualnumber.repository.VirtualNumberRepository
import com.example.virtualnumber.room.CountryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VirtualNumberViewModel @Inject constructor(
    private val repository: VirtualNumberRepository,
) : ViewModel() {

    private val _countriesResponse: MutableLiveData<NetworkResult<Map<String, Country>>> =
        MutableLiveData()
    val countriesResponse: LiveData<NetworkResult<Map<String, Country>>> = _countriesResponse

    private val _topCountriesResponse: MutableLiveData<NetworkResult<Map<String, ServiceSpecificCountry>>> =
        MutableLiveData()
    val topCountriesResponse: LiveData<NetworkResult<Map<String, ServiceSpecificCountry>>> =
        _topCountriesResponse

    private val _servicesResponse: MutableLiveData<NetworkResult<Services>> =
        MutableLiveData()
    val servicesResponse: LiveData<NetworkResult<Services>> = _servicesResponse

    val countries = repository.countries

    private val _activationNumberResponse = MutableLiveData<NetworkResult<String>>()
    val activationNumberResponse: LiveData<NetworkResult<String>> = _activationNumberResponse

    private val _rentServicesResponse: MutableLiveData<NetworkResult<RentServicesAndCountriesResponse>> =
        MutableLiveData()
    val rentServicesResponse: LiveData<NetworkResult<RentServicesAndCountriesResponse>> =
        _rentServicesResponse

    private val _rentNumberResponse = MutableLiveData<NetworkResult<RentNumberResponse>>()
    val rentNumberResponse: LiveData<NetworkResult<RentNumberResponse>> = _rentNumberResponse

    fun insertCountries(countries: List<CountryEntity>) = viewModelScope.launch {
        repository.insertCountries(countries)
    }

    // --- NEW: insertServices into Room ---
    fun insertServices(services: List<ServiceEntity>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertServices(services)
    }

    // --- NEW: expose a Flow of all stored services ---
    val allServicesFromDb: Flow<List<ServiceEntity>> = repository.allServicesFromDb

    val allCountriesLiveData: LiveData<List<CountryEntity>> =
        repository.allCountriesFromDb.asLiveData()

    fun getServices(apiKey: String) = viewModelScope.launch {
        repository.getServices(apiKey).collect { values ->
            _servicesResponse.value = values
        }
    }

    fun getCountries(apiKey: String) = viewModelScope.launch {
        repository.getCountries(apiKey).collect { values ->
            _countriesResponse.value = values
        }
    }

    fun getServiceSpecificCountries(apiKey: String) = viewModelScope.launch {
        repository.getServiceSpecificCountries(apiKey).collect { values ->
            _topCountriesResponse.value = values
        }
    }

    fun getNumberForActivation(
        apiKey: String,
        serviceCode: String,
        countryCode: String
    ) = viewModelScope.launch {
        _activationNumberResponse.postValue(NetworkResult.Loading())
        try {
            val response = repository.getNumber(apiKey, serviceCode, countryCode)
            if (response.startsWith("ACCESS_NUMBER:")) {
                _activationNumberResponse.postValue(NetworkResult.Success(response))
            } else {
                _activationNumberResponse.postValue(NetworkResult.Error(response))
            }
        } catch (e: Exception) {
            _activationNumberResponse.postValue(NetworkResult.Error(e.localizedMessage ?: "Something went wrong"))
        }
    }

    //  Rent a Virtual Number
    fun getRentServicesAndCountries(apiKey: String, country: Int = 0) = viewModelScope.launch {
        repository.getRentServicesAndCountries(apiKey, country).collect {
            _rentServicesResponse.value = it
        }
    }

    fun getNumberOnRent(
        apiKey: String,
        service: String,
        rentTime: Int = 4,
        operator: String = "any",
        country: String = "0",
        url: String = "",
        incomingCall: String = "false"
    ) = viewModelScope.launch {
        _rentNumberResponse.postValue(NetworkResult.Loading())

        try {
            val response = repository.getNumberOnRent(apiKey, service, rentTime, operator, country, url, incomingCall)
            if (response.status == "success" && response.phone != null) {
                _rentNumberResponse.postValue(NetworkResult.Success(response))
            } else {
                _rentNumberResponse.postValue(NetworkResult.Error(response.errorMsg ?: response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            _rentNumberResponse.postValue(NetworkResult.Error(e.localizedMessage ?: "Something went wrong"))
        }
    }

    fun getBalance(apiKey: String, callback: (String) -> Unit) {
        viewModelScope.launch {
            val balance = repository.getBalance(apiKey)
            callback(balance)
        }
    }

    fun getNumbersStatus(
        apiKey: String,
        serviceCode: String?,
        countryCode: String?,
        callback: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val status = repository.getNumbersStatus(apiKey, serviceCode, countryCode)
            if (status != null) {
                callback(status)
            }
        }
    }

    fun setStatus(apiKey: String, status: Int, activationId: String, callback: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.setStatus(apiKey, status, activationId)
            callback(result)
        }
    }

    /*fun getStatus(apiKey: String, activationId: String, callback: (String) -> Unit) {
        viewModelScope.launch {
            val status = repository.getStatus(apiKey, activationId)
            callback(status)
        }
    }*/

    fun getActiveActivations(apiKey: String, callback: (String) -> Unit) {
        viewModelScope.launch {
            val activations = repository.getActiveActivations(apiKey)
            callback(activations)
        }
    }

    /*fun getOperators(apiKey: String, countryCode: String, callback: (String) -> Unit) {
        viewModelScope.launch {
            val operators = repository.getOperators(apiKey, countryCode)
            callback(operators)
        }
    }*/

    /*fun getPrices(
        apiKey: String,
        serviceCode: String?,
        countryCode: String?,
        callback: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val prices = repository.getPrices(apiKey, serviceCode, countryCode)
            callback(prices)
        }
    }*/
}
