package com.example.virtualnumber.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualnumber.R
import com.example.virtualnumber.activity.RentNumberActivity
import com.example.virtualnumber.model.Country
import com.example.virtualnumber.utils.AppPreferences

class CountryAdapter(
    private val context: Context,
    private val onEmptyFilteredList: (Boolean) -> Unit // callback to show/hide no data
) : ListAdapter<Country, CountryAdapter.ViewHolder>(CountryDiffCallback()) {

    private var fullList: List<Country> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCountryName: TextView = itemView.findViewById(R.id.tvCountryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = getItem(position)
        holder.tvCountryName.text = country.eng

        holder.itemView.setOnClickListener {
            Log.d("country_code", country.id.toString())
            AppPreferences.setCountryCode(country.id.toString())

            Log.d("country_name", country.eng)
            AppPreferences.setCountryCode(country.eng)

            val intent = Intent(context, RentNumberActivity::class.java).apply {
                putExtra("service_code", AppPreferences.getServiceCode())
            }
            context.startActivity(intent)
        }
    }

    /**
     * Call this once to provide the full list
     */
    fun submitFullList(list: List<Country>) {
        fullList = list
        submitList(fullList)
        onEmptyFilteredList(fullList.isEmpty())
    }

    /**
     * Filters the list by the search query and calls back if empty
     */
    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.eng.contains(query, ignoreCase = true)
            }
        }

        submitList(filteredList)
        onEmptyFilteredList(filteredList.isEmpty())
    }

    class CountryDiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }
    }
}


