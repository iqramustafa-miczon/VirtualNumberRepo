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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualnumber.R
import com.example.virtualnumber.activity.CountryActivity
import com.example.virtualnumber.adapter.ServicesAdapter
import com.example.virtualnumber.databinding.FragmentActivationsBinding
import com.example.virtualnumber.model.Service
import com.example.virtualnumber.remote.NetworkResult
import com.example.virtualnumber.viewModel.VirtualNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivationsFragment : Fragment() {

    private lateinit var binding: FragmentActivationsBinding
    private lateinit var adapter: ServicesAdapter
    private var fullServiceList: List<Service> = emptyList()

    private val viewModel: VirtualNumberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActivationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ServicesAdapter { code, name ->

            if(!code.contains("full") && !name.contains("Full Rent")) {
                Log.d("activationServicesAdapterClick", "Clicked service: $code - $name")
                val intent = Intent(requireContext(), CountryActivity::class.java).apply {
                    putExtra("serviceCode", code)
                    putExtra("serviceName", name)
                }
                startActivity(intent)
            }
        }

        binding.rvActivationServices.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivationServices.adapter = adapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterServices(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Observe data
        lifecycleScope.launch {
            viewModel.getServices(getString(R.string.smsActivate_api_key))
            viewModel.servicesResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        binding.loading.isVisible = false
                        val services = response.data?.services
                        if (!services.isNullOrEmpty()) {
                            fullServiceList = services.filter {
                                !it.code.contains("full", ignoreCase = true) &&
                                        !it.name.contains("Full Rent", ignoreCase = true)
                            }
                            filterServices(binding.etSearch.text.toString()) // Apply search if any
                        } else {
                            fullServiceList = emptyList()
                            filterServices("") // Trigger no data
                        }
                    }

                    is NetworkResult.Error -> {
                        binding.loading.isVisible = false
                        Log.d("activationServicesCheck", "Error: ${response.message}")
                    }

                    is NetworkResult.Loading -> {
                        binding.loading.isVisible = true
                    }
                }
            }
        }
    }

    private fun filterServices(query: String) {
        val filteredList = if (query.isBlank()) {
            fullServiceList
        } else {
            fullServiceList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.code.contains(query, ignoreCase = true)
            }
        }

        adapter.submitList(filteredList)
        binding.rvActivationServices.isVisible = filteredList.isNotEmpty()
        binding.tvNoData.isVisible = filteredList.isEmpty()
    }
}

