package com.example.virtualnumber.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val countryCode: Int,
    val countryName: String
)

