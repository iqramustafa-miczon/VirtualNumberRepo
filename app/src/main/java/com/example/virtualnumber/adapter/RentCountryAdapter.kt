package com.example.virtualnumber.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualnumber.activity.RentNumberActivity
import com.example.virtualnumber.databinding.ItemCountryBinding

class RentCountryAdapter(
    private val context: Context,
    private val items: List<String>,
    private val rentServiceCode: String,
) : RecyclerView.Adapter<RentCountryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCountryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCountryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = items[position]
        holder.binding.tvCountryName.text = name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RentNumberActivity::class.java).apply {
                putExtra("rent_service_code", rentServiceCode)
                putExtra("fromRentCountryAdapter", true)
            }
            context.startActivity(intent)
        }
    }
}
