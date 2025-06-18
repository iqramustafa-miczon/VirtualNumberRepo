package com.example.virtualnumber.utils

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val SELECTED_SERVICE_CODE = "selected_service_code"
    private const val SELECTED_COUNTRY_CODE = "selected_country_code"
    private const val SELECTED_SERVICE_NAME = "selected_service_name"
    private const val SELECTED_COUNTRY_NAME = "selected_country_name"
    private const val COIN_COUNT = "coin_count"

    private fun getAppSharedPreferences(): SharedPreferences {
        return App.getContext()
            .getSharedPreferences("VirtualNumberPreferences", Context.MODE_PRIVATE)
    }

    fun setServiceCode(code: String) {
        getAppSharedPreferences().edit().putString(SELECTED_SERVICE_CODE, code).apply()
    }

    fun getServiceCode(): String {
        return getAppSharedPreferences().getString(SELECTED_SERVICE_CODE, "wa") ?: "wa"
    }

    fun setCountryCode(code: String) {
        getAppSharedPreferences().edit().putString(SELECTED_COUNTRY_CODE, code).apply()
    }

    fun getCountryCode(): String {
        return getAppSharedPreferences().getString(SELECTED_COUNTRY_CODE, "pk") ?: "pk"
    }

    fun setServiceName(name: String) {
        getAppSharedPreferences().edit().putString(SELECTED_SERVICE_NAME, name).apply()
    }

    fun getServiceName(): String {
        return getAppSharedPreferences().getString(SELECTED_SERVICE_NAME, "WhatsApp") ?: "WhatsApp"
    }

    fun setCountryName(name: String) {
        getAppSharedPreferences().edit().putString(SELECTED_COUNTRY_NAME, name).apply()
    }

    fun getCountryName(): String {
        return getAppSharedPreferences().getString(SELECTED_COUNTRY_NAME, "Pakistan") ?: "Pakistan"
    }

    fun setCoinCount(count: Int) {
        getAppSharedPreferences().edit().putInt(COIN_COUNT, count).apply()
    }

    fun getCoinCount(): Int {
        return getAppSharedPreferences().getInt(COIN_COUNT, 0)
    }
}