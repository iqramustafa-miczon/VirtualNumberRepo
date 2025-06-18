package com.example.virtualnumber.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualnumber.R
import com.example.virtualnumber.adapter.CountryAdapter
import com.example.virtualnumber.adapter.ServiceSpecificCountryAdapter
import com.example.virtualnumber.databinding.FragmentCountriesBinding
import com.example.virtualnumber.remote.NetworkResult
import com.example.virtualnumber.viewModel.VirtualNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CountriesFragment : Fragment() {

    private lateinit var binding: FragmentCountriesBinding
    private lateinit var adapter: CountryAdapter
    private lateinit var serviceSpecificAdapter: ServiceSpecificCountryAdapter

    private val viewModel: VirtualNumberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCountriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serviceCode = arguments?.getString("serviceCode") ?: "Unknown Service Code"
        val serviceName = arguments?.getString("serviceName") ?: "Unknown Service Code"
        val countryInfo = "$serviceName($serviceCode)"
        binding.tvTitle.text = countryInfo

        if (serviceName == "Full rent") {

            binding.rvCountries.layoutManager = LinearLayoutManager(requireContext())
            adapter = CountryAdapter(requireContext()){ isEmpty ->
                binding.tvNoData.isVisible = isEmpty
            }
            binding.rvCountries.adapter = adapter

            lifecycleScope.launch {
                viewModel.getCountries(getString(R.string.smsActivate_api_key))
                viewModel.countriesResponse.observe(viewLifecycleOwner) { response ->
                    when (response) {
                        is NetworkResult.Success -> {
                            binding.loading.isVisible = false
                            val countries = response.data?.values
                            if (!countries.isNullOrEmpty()) {
                                adapter.submitFullList(countries.toList())
                            }
                        }

                        is NetworkResult.Error -> {
                            binding.loading.isVisible = false
                            Log.d("countriesCheck", "Error: ${response.message}")
                        }

                        is NetworkResult.Loading -> {
                            binding.loading.isVisible = true
                        }
                    }
                }
            }

        } else {

            binding.rvCountries.layoutManager = LinearLayoutManager(requireContext())

            lifecycleScope.launch {
                viewModel.countries.collect { countries ->
                    withContext(Dispatchers.Main) {
                        serviceSpecificAdapter = ServiceSpecificCountryAdapter(countries)
                        binding.rvCountries.adapter = serviceSpecificAdapter
                    }
                }
            }

            lifecycleScope.launch {
                viewModel.getServiceSpecificCountries(getString(R.string.smsActivate_api_key))
                viewModel.topCountriesResponse.observe(viewLifecycleOwner) { response ->
                    when (response) {
                        is NetworkResult.Success -> {
                            val countries = response.data?.values
                            if (!countries.isNullOrEmpty()) {
                                serviceSpecificAdapter.submitFullList(countries.toList())
                            }
                        }

                        is NetworkResult.Error -> {
                            Log.d("topCountriesCheck", "Error: ${response.message}")
                        }

                        is NetworkResult.Loading -> {
                            binding.loading.isVisible = true
                        }
                    }
                }
            }
        }

        // Add TextWatcher for search filtering
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (serviceName == "Full rent") {
                    adapter.filter(query)
                } else {
                    serviceSpecificAdapter.filter(query) { isEmpty ->
                        binding.tvNoData.isVisible = isEmpty
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}

