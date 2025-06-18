package com.example.virtualnumber.adapter

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
import com.example.virtualnumber.activity.PremiumActivity
import com.example.virtualnumber.model.ServiceSpecificCountry
import com.example.virtualnumber.room.CountryEntity
import com.example.virtualnumber.utils.AppPreferences
import com.example.virtualnumber.utils.Constants.COIN_COUNT

class ServiceSpecificCountryAdapter(
    private var countryList: List<CountryEntity>,
    private val onItemClick: ((ServiceSpecificCountry) -> Unit)? = null
) : ListAdapter<ServiceSpecificCountry, ServiceSpecificCountryAdapter.ViewHolder>(
    ServiceSpecificCountryDiffCallback()
) {
    private var fullServiceList: List<ServiceSpecificCountry> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCountryName: TextView = itemView.findViewById(R.id.tvCountryName)
        //val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvCoins: TextView = itemView.findViewById(R.id.tvCoins)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_specific_country, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context

        val serviceCountry = getItem(position)
        val countryEntity = countryList.find { it.countryCode == serviceCountry.countryCode }

        holder.tvCountryName.text = countryEntity?.countryName ?: ""

       /* val price = "Price: ${serviceCountry.price}USD"
        holder.tvPrice.text = price*/
        val coins = calculateCoins(serviceCountry.price).toString()
        holder.tvCoins.text = coins

        /*holder.itemView.setOnClickListener {
            val requiredCoins = calculateCoins(serviceCountry.price)
            val userCoins = AppPreferences.getCoinCount()
            Log.d("ServiceSpecificCountryAdapter", "requiredCoins $requiredCoins")
            Log.d("ServiceSpecificCountryAdapter", "userCoins $userCoins")

            if (userCoins >= requiredCoins) {
                Log.d("ServiceSpecificCountryAdapter", "Coins are enough, going to buy number")

            } else {
                val intent = Intent(context, PremiumActivity::class.java)
                context.startActivity(intent)
            }
        }*/

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(serviceCountry)
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

    fun updateServiceList(newServiceList: List<ServiceSpecificCountry>) {
        fullServiceList = newServiceList
        submitList(fullServiceList)
    }

    fun submitFullList(list: List<ServiceSpecificCountry>) {
        fullServiceList = list
        submitList(fullServiceList)
    }

    fun filter(query: String, onFilterComplete: (Boolean) -> Unit) {
        val filtered = if (query.isEmpty()) {
            fullServiceList
        } else {
            fullServiceList.filter { serviceCountry ->
                val countryName =
                    countryList.find { it.countryCode == serviceCountry.countryCode }?.countryName
                        ?: ""
                countryName.contains(query, ignoreCase = true)
            }
        }

        submitList(filtered)
        onFilterComplete(filtered.isEmpty())
    }

    class ServiceSpecificCountryDiffCallback : DiffUtil.ItemCallback<ServiceSpecificCountry>() {
        override fun areItemsTheSame(
            oldItem: ServiceSpecificCountry,
            newItem: ServiceSpecificCountry,
        ): Boolean {
            return oldItem.countryCode == newItem.countryCode
        }

        override fun areContentsTheSame(
            oldItem: ServiceSpecificCountry,
            newItem: ServiceSpecificCountry,
        ): Boolean {
            return oldItem == newItem
        }
    }
}