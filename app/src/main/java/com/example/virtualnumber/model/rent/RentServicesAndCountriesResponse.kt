package com.example.virtualnumber.model.rent

data class RentServicesAndCountriesResponse(
    val countries: Map<String, Int>,
    val operators: Map<String, String>,
    val services: Map<String, RentService>,
    val realHours: Int,
    val currency: Int
)