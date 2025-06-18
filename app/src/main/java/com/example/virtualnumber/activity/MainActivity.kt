package com.example.virtualnumber.activity

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.virtualnumber.R
import com.example.virtualnumber.databinding.ActivityMainBinding
import com.example.virtualnumber.fragment.RentedNumbersFragment
import com.example.virtualnumber.fragment.ServicesFragment
import com.example.virtualnumber.model.ServiceEntity
import com.example.virtualnumber.remote.NetworkResult
import com.example.virtualnumber.room.CountryEntity
import com.example.virtualnumber.utils.AppPreferences
import com.example.virtualnumber.utils.Constants.COIN_COUNT
import com.example.virtualnumber.viewModel.VirtualNumberViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var menu: Menu? = null
    private val viewModel: VirtualNumberViewModel by viewModels()
    private val premiumLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val myCoins = AppPreferences.getCoinCount()
        Log.d("myCoins", myCoins.toString())
        val formattedCoins = String.format(Locale.getDefault(), "%d", myCoins)
        binding.valueOfBalance.text = formattedCoins
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Top Bar
        setSupportActionBar(binding.toolbar)
        val window = window
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //Fragments
        setupTabs()

        enableDisableTabs(enableHistory = false, enableFavorites = false)

        lifecycleScope.launch(Dispatchers.Default) {
            delay(1500)

            withContext(Dispatchers.Main) {
                binding.tabLayout.getTabAt(0)?.select()

                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ServicesFragment())
                        .commitAllowingStateLoss()
                }

                binding.loading.visibility = View.GONE
                binding.loadingText.visibility = View.GONE
                enableDisableTabs(enableHistory = true, enableFavorites = true)
            }
        }

        val myCoins = AppPreferences.getCoinCount()
        Log.d("myCoins", myCoins.toString())
        binding.valueOfBalance.text = String.format(Locale.ENGLISH, myCoins.toString())

        viewModel.getServices(getString(R.string.smsActivate_api_key))
        viewModel.servicesResponse.observe(this@MainActivity) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.loading.isVisible = false
                    val services = response.data?.services
                    if (!services.isNullOrEmpty()) {
                        // Map API model to ServiceEntity
                        val serviceEntities = arrayListOf<ServiceEntity>()
                        for (svc in services) {
                            serviceEntities.add(
                                ServiceEntity(
                                    serviceCode = svc.code,
                                    serviceName = svc.name
                                )
                            )
                        }
                        // Insert into Room
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.insertServices(serviceEntities.toList())
                            Log.d("servicesCheck", "services saved successfully: size=${serviceEntities.size}")
                        }
                    } else {
                        Log.d("servicesCheck", "services list is empty or null")
                    }
                }
                is NetworkResult.Error -> {
                    binding.loading.isVisible = false
                    Log.d("servicesCheck", "Error: ${response.message}")
                }
                is NetworkResult.Loading -> {
                    binding.loading.isVisible = true
                }
            }
        }

        // 2) Collect services from Room and log every time the table changes
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.allServicesFromDb.collect { storedList ->
                if (storedList.isEmpty()) {
                    Log.d("servicesCheck", "→ No services in DB yet.")
                } else {
                    Log.d("servicesCheck", "→ Currently saved services (${storedList.size}):")
                    for (svcEntity in storedList) {
                        Log.d(
                            "servicesCheck",
                            "   • code=\"${svcEntity.serviceCode}\", name=\"${svcEntity.serviceName}\""
                        )
                    }
                }
            }
        }

        //save country names with country Ids
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("countriesCheck", "Starting to collect countries...")
                    Log.d("countriesCheck", "countries db list is empty")
                    viewModel.getCountries(getString(R.string.smsActivate_api_key))
                    withContext(Dispatchers.Main) {
                        viewModel.countriesResponse.observe(this@MainActivity) { response ->
                            when (response) {
                                is NetworkResult.Success -> {
                                    binding.loading.isVisible = false
                                    val allCountries = response.data?.values
                                    if (!allCountries.isNullOrEmpty()) {
                                        Log.d("countriesCheck", "countries list size: ${allCountries.size}")
                                        val countriesList = arrayListOf<CountryEntity>()
                                        for(country in allCountries) {
                                            countriesList.add(
                                                CountryEntity(
                                                    countryName = country.eng,
                                                    countryCode = country.id
                                                )
                                            )
                                        }
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            viewModel.insertCountries(countriesList.toList())
                                            Log.d("countriesCheck", "countries saved successfully: size= ${countriesList.size}")
                                        }
                                    } else {
                                        Log.d("countriesCheck", "countries list is empty or null")
                                    }
                                }

                                is NetworkResult.Error -> {
                                    binding.loading.isVisible = false
                                    Log.d("countriesCheck", "Error: ${response.message}")
                                }

                                is NetworkResult.Loading -> {
                                    Log.d("RentFragment", "Loading rent services...")
                                    binding.loading.isVisible = true
                                }
                            }
                        }
                    }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)

    }

    private fun setupTabs() {
        val titles = listOf(
            getString(R.string.services),
            getString(R.string.rented_numbers)
        )

        val icons = listOf(
            R.drawable.ic_service, // Home icon for the home tab
            R.drawable.ic_number
        )

        for (i in titles.indices) {
            val tab = binding.tabLayout.newTab()
            val customTabView =
                layoutInflater.inflate(R.layout.custom_tab_layout, binding.tabLayout, false)

            val tabIcon = customTabView.findViewById<ImageView>(R.id.tab_icon)
            val tabText = customTabView.findViewById<TextView>(R.id.tab_text)

            tabIcon.setImageResource(icons[i])
            tabText.text = titles[i]

            tab.customView = customTabView
            binding.tabLayout.addTab(tab)
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateTabStyles(tab.position)
                replaceFragment(
                    when (tab.position) {
                        0 -> {
                            ServicesFragment()
                        }

                        1 -> {
                            RentedNumbersFragment()
                        }

                        else -> {
                            ServicesFragment()
                        }
                    }
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                updateTabStyles(tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Manually select the first tab initially
        binding.tabLayout.getTabAt(0)?.select()
        updateTabStyles(0) // Update styles for the initially selected tab
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        setMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setMenu(menu: Menu?) {
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            R.id.premium -> {
                /*val intent = Intent(this, PremiumActivity::class.java)
                startActivity(intent)*/
                val intent = Intent(this, PremiumActivity::class.java)
                premiumLauncher.launch(intent)

            }

            R.id.login -> {
                val intent = Intent(this, EmailPasswordActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enableDisableTabs(enableHistory: Boolean, enableFavorites: Boolean) {
        val historyTab = binding.tabLayout.getTabAt(1)
        val favoritesTab = binding.tabLayout.getTabAt(2)

        historyTab?.let {
            it.view.isClickable = enableHistory
            it.view.alpha =
                if (enableHistory) 1f else 0.5f  // Reduce opacity to indicate it's disabled
        }

        favoritesTab?.let {
            it.view.isClickable = enableFavorites
            it.view.alpha =
                if (enableFavorites) 1f else 0.5f  // Reduce opacity to indicate it's disabled
        }
    }

    private fun updateTabStyles(selectedPosition: Int) {
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            val customView = tab?.customView
            if (customView != null) {
                val tabIcon = customView.findViewById<ImageView>(R.id.tab_icon)
                val tabText = customView.findViewById<TextView>(R.id.tab_text)

                if (i == selectedPosition) {
                    tabIcon.setColorFilter(
                        ContextCompat.getColor(this, R.color.blue),
                        PorterDuff.Mode.SRC_IN
                    )
                    tabText.setTextColor(ContextCompat.getColor(this, R.color.blue))
                } else {
                    tabIcon.clearColorFilter()
                    tabText.setTextColor(ContextCompat.getColor(this, R.color.gray))
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commitAllowingStateLoss()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

            if (currentFragment is ServicesFragment) {
                //showExitDialog()
            } else {
                replaceFragment(ServicesFragment())
                selectServicesTab()
            }
        }
    }

    fun selectServicesTab() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.getTabAt(0)?.select()
    }
}
