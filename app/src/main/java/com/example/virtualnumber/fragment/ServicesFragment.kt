package com.example.virtualnumber.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.virtualnumber.R
import com.example.virtualnumber.adapter.CountryAdapter
import com.example.virtualnumber.adapter.ServicesAdapter
import com.example.virtualnumber.adapter.TabPagerAdapter
import com.example.virtualnumber.databinding.FragmentServiceBinding
import com.example.virtualnumber.remote.NetworkResult
import com.example.virtualnumber.viewModel.VirtualNumberViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServicesFragment : Fragment() {
    private lateinit var binding: FragmentServiceBinding
    private lateinit var tabAdapter: TabPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        tabAdapter = TabPagerAdapter(this)
        binding.viewPager.adapter = tabAdapter
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabAdapter.getPageTitle(position)
        }.attach()

        // Optional: Add custom tab styling
        binding.tabLayout.apply {
            setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.blue))
            setTabTextColors(
                ContextCompat.getColor(requireContext(), android.R.color.darker_gray),
                ContextCompat.getColor(requireContext(), R.color.blue)
            )
        }
    }
}