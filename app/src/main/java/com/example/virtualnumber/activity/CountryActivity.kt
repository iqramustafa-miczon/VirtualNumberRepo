package com.example.virtualnumber.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualnumber.R
import com.example.virtualnumber.adapter.RentCountryAdapter
import com.example.virtualnumber.adapter.ServiceSpecificCountryAdapter
import com.example.virtualnumber.databinding.ActivityCountryBinding
import com.example.virtualnumber.remote.NetworkResult
import com.example.virtualnumber.utils.AppPreferences
import com.example.virtualnumber.viewModel.VirtualNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*@AndroidEntryPoint
class CountryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountryBinding
    private lateinit var adapter: CountryAdapter
    private lateinit var serviceSpecificAdapter: ServiceSpecificCountryAdapter
    private lateinit var rentCountryAdapter: RentCountryAdapter

    private val viewModel: VirtualNumberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceCode = intent.getStringExtra("serviceCode") ?: getString(R.string.unknown)
        val serviceName = intent.getStringExtra("serviceName") ?: getString(R.string.unknown)
        val serviceInfo = "$serviceName ($serviceCode)"

        //From specifically Rent Fragment
        val matchedNames: ArrayList<String>? = intent.getStringArrayListExtra("MATCHED_NAMES")
        val rentServiceCode = intent.getStringExtra("SERVICE_CODE")

        binding.tvTitle.text = serviceInfo
        AppPreferences.setServiceName(serviceName)

        binding.btnBack.setOnClickListener {
            finish()
        }

        if (!matchedNames.isNullOrEmpty()) {
            binding.loading.isVisible = false
            binding.rvCountries.layoutManager = LinearLayoutManager(this)

            rentCountryAdapter =
                RentCountryAdapter(this@CountryActivity, matchedNames, rentServiceCode.toString())
            binding.rvCountries.adapter = rentCountryAdapter

            return
        }

        if (serviceName == "Full rent") {

            Log.d("countryActivity", "In full rent block")

            binding.rvCountries.layoutManager = LinearLayoutManager(this)
            adapter = CountryAdapter(this) { isEmpty ->
                binding.tvNoData.isVisible = isEmpty
            }
            binding.rvCountries.adapter = adapter

            lifecycleScope.launch {
                viewModel.getCountries(getString(R.string.smsActivate_api_key))
                viewModel.countriesResponse.observe(this@CountryActivity) { response ->
                    when (response) {
                        is NetworkResult.Success -> {
                            binding.loading.isVisible = false
                            val countries = response.data?.values
                            if (!countries.isNullOrEmpty()) {
                                Log.d("countriesCheck", "countries list size: ${countries.size}")
                                adapter.submitList(countries.toList())
                            } else {
                                Log.d("countriesCheck", "countries list is empty or null")
                            }
                        }

                        is NetworkResult.Error -> {
                            binding.loading.isVisible = false
                            Log.d("countriesCheck", "Error: ${response.message}")
                        }

                        is NetworkResult.Loading -> {
                            Log.d("RentFragment", "Loading rent services...")
                            binding.loading.isVisible = true
                        }
                    }
                }
            }

        } else {

            Log.d("countryActivity", "In else, specific service search block")

            binding.rvCountries.layoutManager = LinearLayoutManager(this)
            serviceSpecificAdapter = ServiceSpecificCountryAdapter(emptyList())
            binding.rvCountries.adapter = serviceSpecificAdapter

            lifecycleScope.launch {
                viewModel.countries.collect { countriesList ->
                    withContext(Dispatchers.Main) {
                        binding.rvCountries.layoutManager =
                            LinearLayoutManager(this@CountryActivity)
                        serviceSpecificAdapter =
                            ServiceSpecificCountryAdapter(countriesList)
                        binding.rvCountries.adapter = serviceSpecificAdapter
                    }
                }
            }

            binding.etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    serviceSpecificAdapter.filter(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })


            lifecycleScope.launch {
                viewModel.getServiceSpecificCountries(getString(R.string.smsActivate_api_key))
                viewModel.topCountriesResponse.observe(this@CountryActivity) { response ->
                    when (response) {
                        is NetworkResult.Success -> {
                            binding.loading.isVisible = false
                            val countries = response.data?.values
                            if (!countries.isNullOrEmpty()) {
                                Log.d(
                                    "countryActivity", " countries list size: ${countries.size}"
                                )
                                serviceSpecificAdapter.submitList(countries.toList())
                            } else {
                                Log.d(
                                    "countryActivity", " countries list is empty or null"
                                )
                            }
                        }

                        is NetworkResult.Error -> {
                            binding.loading.isVisible = false
                            Log.d(
                                "countryActivity", "Error: ${response.message}"
                            )
                        }

                        is NetworkResult.Loading -> {
                            binding.loading.isVisible = true
                            Log.d("countryActivity", "Loading countries...")
                        }
                    }
                }
            }
        }
    }

}*/

@AndroidEntryPoint
class CountryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountryBinding
    private lateinit var serviceSpecificAdapter: ServiceSpecificCountryAdapter
    private lateinit var rentCountryAdapter: RentCountryAdapter

    private val viewModel: VirtualNumberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val serviceCode = intent.getStringExtra("serviceCode") ?: getString(R.string.unknown)
        val serviceName = intent.getStringExtra("serviceName") ?: getString(R.string.unknown)

        val serviceInfo = "$serviceName ($serviceCode)"

        //From specifically Rent Fragment
        val matchedNames: ArrayList<String>? = intent.getStringArrayListExtra("MATCHED_NAMES")
        val rentServiceCode = intent.getStringExtra("SERVICE_CODE")
        val rentServiceName = intent.getStringExtra("SERVICE_NAME")

        if(!rentServiceName.isNullOrEmpty())
            binding.tvTitle.text = rentServiceName
        else
            binding.tvTitle.text = serviceInfo
        AppPreferences.setServiceName(serviceName)

        if (!matchedNames.isNullOrEmpty()) {
            binding.loading.isVisible = false
            binding.rvCountries.layoutManager = LinearLayoutManager(this)

            rentCountryAdapter =
                RentCountryAdapter(this@CountryActivity, matchedNames, rentServiceCode.toString())
            binding.rvCountries.adapter = rentCountryAdapter

            return
        }

        binding.btnBack.setOnClickListener { finish() }

        serviceSpecificAdapter = ServiceSpecificCountryAdapter(emptyList())
        binding.rvCountries.layoutManager = LinearLayoutManager(this)
        binding.rvCountries.adapter = serviceSpecificAdapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                serviceSpecificAdapter.filter(s?.toString() ?: "") { isEmpty ->
                    binding.tvNoData.isVisible = isEmpty
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        lifecycleScope.launch {
            viewModel.countries.collect { countriesList ->
                withContext(Dispatchers.Main) {
                    binding.rvCountries.layoutManager =
                        LinearLayoutManager(this@CountryActivity)
//                    serviceSpecificAdapter = ServiceSpecificCountryAdapter(countriesList)
                    serviceSpecificAdapter = ServiceSpecificCountryAdapter(countriesList) { service ->
                        val requiredCoins = calculateCoins(service.price)
                        val userCoins = AppPreferences.getCoinCount()

                        if (userCoins >= requiredCoins) {
                            Log.d("CountryActivityCheck", "✅ Enough coins: $userCoins, Required: $requiredCoins")
                            lifecycleScope.launch(Dispatchers.IO) {
                                viewModel.getNumberForActivation(
                                    getString(R.string.smsActivate_api_key),
                                    serviceCode,
                                    service.countryCode.toString()
                                )
                                withContext(Dispatchers.Main) {
                                    viewModel.activationNumberResponse.observe(this@CountryActivity) { response ->
                                        when (response) {
                                            is NetworkResult.Loading -> {
                                                binding.loading.isVisible = true
                                            }

                                            is NetworkResult.Success -> {
                                                binding.loading.isVisible = false
                                                val result = response.data
                                                val parts = result?.split(":")
                                                if (parts != null) {
                                                    if (parts.size >= 3) {
                                                        val activationId = parts[1]
                                                        val rentedNumber = parts[2]
                                                        Log.d(
                                                            "RENT_NUMBER",
                                                            "NUMBER_RENTED: Activation ID: $activationId, Number: $rentedNumber"
                                                        )
                                                    } else {
                                                        Log.e("RENT_NUMBER", "Unexpected format: $result")
                                                    }
                                                }
                                            }

                                            is NetworkResult.Error -> {
                                                binding.loading.isVisible = false
                                                Log.e("RENT_NUMBER", "Error Full Response: ${response.message}")
                                                Toast.makeText(
                                                    this@CountryActivity,
                                                    "Result: ${response.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this@CountryActivity, "You need $requiredCoins coins but have $userCoins", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@CountryActivity, PremiumActivity::class.java))
                        }
                    }

                    binding.rvCountries.adapter = serviceSpecificAdapter
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getServiceSpecificCountries(getString(R.string.smsActivate_api_key))
            viewModel.topCountriesResponse.observe(this@CountryActivity) { response ->
                when (response) {
                    is NetworkResult.Success -> {

                        Log.d("CountryActivityCheck", "Success")
                        binding.loading.isVisible = false
                        val countries = response.data?.values?.toList() ?: emptyList()
                        serviceSpecificAdapter.updateServiceList(countries)

                        binding.tvNoData.isVisible = countries.isEmpty()
                    }

                    is NetworkResult.Error -> {
                        binding.loading.isVisible = false

                        Toast.makeText(
                            this@CountryActivity,
                            getString(R.string.error_loading_data),
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.d("CountryActivityCheck", response.message ?: "Failure, Error loading data")
                    }

                    is NetworkResult.Loading -> {
                        binding.loading.isVisible = true
                    }
                }
            }
        }
    }
    private fun calculateCoins(price: Double): Int {
        return when (price) {
            in 0.2..<0.5 -> 1
            in 0.5..<1.5 -> 4
            in 1.5..<4.0 -> 6
            in 4.0..<7.0 -> 8
            in 7.0..10.0 -> 10
            else -> 0
        }
    }

}
