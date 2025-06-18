package com.example.virtualnumber.adapter

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.virtualnumber.R
import com.example.virtualnumber.fragment.ActivationsFragment
import com.example.virtualnumber.fragment.RentFragment

class TabPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val tabItems = listOf(
        TabItem(ActivationsFragment(), R.string.activations),
        TabItem(RentFragment(), R.string.rent)
    )

    private val context = fragment.requireContext()

    override fun getItemCount(): Int = tabItems.size

    override fun createFragment(position: Int): Fragment = tabItems[position].fragment

    fun getPageTitle(position: Int): String =
        context.getString(tabItems[position].titleRes)

    data class TabItem(
        val fragment: Fragment,
        @StringRes val titleRes: Int
    )
}