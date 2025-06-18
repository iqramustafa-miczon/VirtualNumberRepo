package com.example.virtualnumber.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualnumber.databinding.ItemRentServiceBinding
import com.example.virtualnumber.model.rent.RentService

class RentServiceAdapter(
    originalItems: List<Map.Entry<String, RentService>>,
    private val serviceNameMap: Map<String, String>, // new
    private val onServiceClicked: (serviceCode: String) -> Unit,
) : RecyclerView.Adapter<RentServiceAdapter.ViewHolder>() {

    private val fullList: List<Map.Entry<String, RentService>> = originalItems.toList()
    private val filteredList: MutableList<Map.Entry<String, RentService>> =
        originalItems.toMutableList()

    class ViewHolder(val binding: ItemRentServiceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRentServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (serviceCode, service) = filteredList[position]

        // Try to get service name from map, fallback to code
        val serviceName = serviceNameMap[serviceCode] ?: serviceCode.uppercase()
        holder.binding.tvServiceName.text = serviceName

      //  val cost = "Retail: ${service.retail_cost}"
        val available = "Available: ${service.quant.current}/${service.quant.total}"
        holder.binding.tvAvailable.text = available

        holder.itemView.setOnClickListener {
            onServiceClicked(serviceCode)
        }
    }

    /**
     * Call this whenever you want to replace the current displayed list
     * (e.g. from your SearchView’s TextWatcher).
     *
     * If `newList` is empty, RecyclerView will show zero items,
     * so in your Fragment you can detect that and show “No data”.
     */
    fun updateList(newList: List<Map.Entry<String, RentService>>) {
        filteredList.clear()
        filteredList.addAll(newList)
        notifyDataSetChanged()
    }

    /**
     * If you ever need to re-apply the “full” list (e.g. search query is blank),
     * you can simply call:
     *
     *     adapter.updateList(fullList)
     */
    fun resetToFullList() {
        updateList(fullList)
    }

    /**
     * Optional helper if you want to know the full list from the adapter:
     */
    fun getFullList(): List<Map.Entry<String, RentService>> = fullList
}

