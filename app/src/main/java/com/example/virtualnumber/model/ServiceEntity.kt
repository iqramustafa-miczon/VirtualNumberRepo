package com.example.virtualnumber.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = false)
    val serviceCode: String,

    val serviceName: String
)