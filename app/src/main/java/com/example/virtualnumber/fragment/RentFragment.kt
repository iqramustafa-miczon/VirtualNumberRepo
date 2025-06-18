package com.example.virtualnumber.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualnumber.R
import com.example.virtualnumber.activity.CountryActivity
import com.example.virtualnumber.adapter.RentServiceAdapter
import com.example.virtualnumber.databinding.FragmentRentBinding
import com.example.virtualnumber.model.ServiceEntity
import com.example.virtualnumber.model.rent.RentService
import com.example.virtualnumber.remote.NetworkResult
import com.example.virtualnumber.room.CountryEntity
import com.example.virtualnumber.viewModel.VirtualNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RentFragment : Fragment() {

    private lateinit var binding: FragmentRentBinding

    private val viewModel: VirtualNumberViewModel by viewModels()

    // 1) Cache Room’s CountryEntity list
    private var cachedCountryList: List<CountryEntity> = emptyList()

    // 2) Cache the full list of rent‐services for search
    private var cachedServices: List<Map.Entry<String, RentService>> = emptyList()

    private lateinit var adapter: RentServiceAdapter

    private var cachedServiceEntities: List<ServiceEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RentFragment", "onViewCreated called")

        // 1) Observe Room’s countries
        viewModel.allCountriesLiveData.observe(viewLifecycleOwner) { countryList ->
            cachedCountryList = countryList
            Log.d("RentFragment", "DB countries loaded (${countryList.size} items).")
            countryList.forEach { entity ->
                Log.d(
                    "RentFragment",
                    "  DB Entity → code=${entity.countryCode}, name=${entity.countryName}"
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allServicesFromDb.collect { storedList ->
                    cachedServiceEntities = storedList
                    Log.d("RentFragment", "Cached ${storedList.size} services from DB")
                }
            }
        }

        viewModel.rentServicesResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    binding.loading.isVisible = true
                    Log.d("RentFragment", "Loading rent services + countries…")
                }

                is NetworkResult.Error -> {
                    binding.loading.isVisible = false
                    Log.e("RentFragment", "Error: ${result.message}")
                }

                is NetworkResult.Success -> {
                    binding.loading.isVisible = false

                    val servicesList: List<Map.Entry<String, RentService>> =
                        result.data!!.services.entries.toList()
                    cachedServices = servicesList
                    Log.d("RentFragment", "Fetched ${servicesList.size} rent services")

                    val serviceNameMap =
                        cachedServiceEntities.associate { it.serviceCode to it.serviceName }

                    adapter = RentServiceAdapter(servicesList, serviceNameMap) { serviceCode ->
                        Log.d("RentFragment", "Service clicked: $serviceCode")

                        val apiCountryIds: List<Int> = result.data.countries.values.toList()
                        Log.d("RentFragment", "  API returned country IDs: $apiCountryIds")

                        val matchedEntities: List<CountryEntity> = cachedCountryList.filter {
                            entity -> apiCountryIds.contains(entity.countryCode)
                        }
                        Log.d("RentFragment", "  Matched ${matchedEntities.size} DB entities by ID")

                        val matchedNames: List<String> = matchedEntities.map { it.countryName }
                        Log.d("RentFragment", "  Matched country names: $matchedNames")

                        val intent = Intent(requireContext(), CountryActivity::class.java).apply {
                            putStringArrayListExtra("MATCHED_NAMES", ArrayList(matchedNames))
                            putExtra("SERVICE_CODE", serviceCode)
                            val serviceName = serviceNameMap[serviceCode] ?: serviceCode.uppercase()
                            putExtra("SERVICE_NAME", serviceName)
                        }
                        startActivity(intent)
                    }

                    binding.rvRentServices.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvRentServices.adapter = adapter

                    binding.tvNoData.isVisible = servicesList.isEmpty()
                }
            }
        }

        viewModel.getRentServicesAndCountries(getString(R.string.smsActivate_api_key))
        viewModel.getCountries(getString(R.string.smsActivate_api_key))
        Log.d("RentFragment", "Requested rentServicesAndCountries and getCountries()")

        setupSearchListener()
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int,
            ) {
                // no-op
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int,
            ) {
                // no-op
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                filterServices(query)
            }
        })
    }

    private fun filterServices(query: String) {
        if (!::adapter.isInitialized) return

        // 1) If query is empty → restore full list
        if (query.isEmpty()) {
            adapter.updateList(adapter.getFullList())
            binding.tvNoData.isVisible = adapter.getFullList().isEmpty()
            return
        }

        // 2) Otherwise filter by serviceCode or by some “name” in RentService
        val filtered: List<Map.Entry<String, RentService>> =
            adapter.getFullList().filter { entry ->
                val codeMatches = entry.key.contains(query, ignoreCase = true)
                // Assume RentService has a field “name” (or adjust to whatever text you want to match)
                val nameMatches = entry.value.search_name.contains(query, ignoreCase = true)
                codeMatches || nameMatches
            }

        adapter.updateList(filtered)
        binding.tvNoData.isVisible = filtered.isEmpty()
    }
}

