package com.example.virtualnumber.activity

import android.os.Bundle
import android.os.WorkDuration
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.virtualnumber.R
import com.example.virtualnumber.databinding.ActivityRentNumberBinding
import com.example.virtualnumber.remote.NetworkResult
import com.example.virtualnumber.utils.AppPreferences
import com.example.virtualnumber.viewModel.VirtualNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RentNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRentNumberBinding
    private lateinit var viewModel: VirtualNumberViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[VirtualNumberViewModel::class.java]

        val serviceName = AppPreferences.getServiceName()
        val serviceCode = AppPreferences.getServiceCode()
        val countryName = AppPreferences.getCountryName()
        val countryCode = AppPreferences.getCountryCode()
        Log.d(
            "RENT_NUMBER",
            "Service Name: $serviceName Service Code: $serviceCode, Country Name: $countryName,Country Code: $countryCode"
        )

        val rentServiceCode: String = intent.getStringExtra("rent_service_code").toString()
        val isRental = intent.getBooleanExtra("fromRentCountryAdapter", false)
        Log.d("RENT_NUMBER", "rentServiceCode: $rentServiceCode")
        Log.d("RENT_NUMBER", "isFromRentTypeOrder: $isRental")

        if (isRental) {
            binding.tvServiceCode.text = rentServiceCode
            binding.head.text = rentServiceCode
            binding.tvServiceName.isVisible = false
            binding.tvCountryCode.isVisible = false
            binding.tvCountryName.isVisible = false
            binding.scrollview.isVisible = true
            binding.tvActivationInfo.isVisible = false
        } else {
            binding.tvServiceCode.text = serviceCode
            binding.tvServiceName.text = serviceName
            binding.tvCountryName.text = countryName
            binding.tvCountryCode.text = countryCode
            binding.scrollview.isVisible = false
            binding.tvActivationInfo.isVisible = true
        }

        binding.tvDuration.setOnClickListener { getNumberForRent(4, rentServiceCode, countryCode) } // 4 Hours
        binding.tvDuration2.setOnClickListener { getNumberForRent(10, rentServiceCode, countryCode) } // 10 Hours
        binding.tvDuration3.setOnClickListener { getNumberForRent(20, rentServiceCode, countryCode) } // 20 Hours
        binding.tvDuration4.setOnClickListener { getNumberForRent(360, rentServiceCode, countryCode) } // 15 Days
        binding.tvDuration5.setOnClickListener { getNumberForRent(720, rentServiceCode, countryCode) } // 1 month

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnPurchaseNumber.setOnClickListener {
            if (isRental) {
                Log.d("RENT_NUMBER", "Going to rent a virtual number.")
                getNumberForRent(12, serviceCode, countryCode)
            } else {
                Log.d("RENT_NUMBER", "Going to activate a virtual number for 20 minutes.")

                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.getNumberForActivation(
                        getString(R.string.smsActivate_api_key),
                        serviceCode,
                        countryCode
                    )
                    withContext(Dispatchers.Main) {
                        viewModel.activationNumberResponse.observe(this@RentNumberActivity) { response ->
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
                                }
                            }
                        }
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun getNumberForRent(duration: Int, serviceCode: String, countryCode: String) {
        Log.d("RENT_NUMBER", "Going to rent a virtual number.")
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getNumberOnRent(
                apiKey = getString(R.string.smsActivate_api_key),
                service = serviceCode,
                rentTime = duration,
                operator = "any",
                country = countryCode,
                url = "https://your.webhook.url",
                incomingCall = "true"
            )
            withContext(Dispatchers.Main) {
                viewModel.rentNumberResponse.observe(this@RentNumberActivity) { response ->
                    when (response) {
                        is NetworkResult.Loading -> {
                            binding.loading.isVisible = true
                        }

                        is NetworkResult.Success -> {
                            binding.loading.isVisible = false
                            val number = response.data?.phone?.number
                            val id = response.data?.phone?.id
                            val endDate = response.data?.phone?.endDate
                            Log.d(
                                "RENT_NUMBER",
                                "NUMBER RENTED: Number: $number, ID: $id, Expires: $endDate"
                            )
                        }

                        is NetworkResult.Error -> {
                            binding.loading.isVisible = false
                            Log.e("RENT_NUMBER", "Error Full Response: $response")
                            Log.e("RENT_NUMBER", "Error Message: ${response.message}")
                        }
                    }
                }
            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }
}