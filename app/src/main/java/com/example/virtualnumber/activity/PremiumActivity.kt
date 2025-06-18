package com.example.virtualnumber.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virtualnumber.databinding.ActivityPremiumBinding
import com.example.virtualnumber.utils.AppPreferences
import com.example.virtualnumber.utils.Constants.COIN_COUNT
import java.util.Locale

class PremiumActivity : AppCompatActivity() {

    private val binding: ActivityPremiumBinding by lazy {
        ActivityPremiumBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            coin1.setOnClickListener {
                addCoins(1)
            }
            coin4.setOnClickListener {
                addCoins(4)
            }
            coin6.setOnClickListener {
                addCoins(6)
            }
            coin8.setOnClickListener {
                addCoins(8)
            }
            coin10.setOnClickListener {
                addCoins(10)
            }
        }
    }

    private fun addCoins(newCoins: Int) {
        val currentCoins = AppPreferences.getCoinCount()
        val updatedCoins = currentCoins + newCoins
        AppPreferences.setCoinCount(updatedCoins)

        val formattedCoins = String.format(Locale.getDefault(), "%d", updatedCoins)
        binding.tvCoinsCount.text = formattedCoins

        Toast.makeText(this, "Coins Added! New Balance: $updatedCoins", Toast.LENGTH_SHORT).show()
    }
}