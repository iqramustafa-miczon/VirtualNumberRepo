package com.example.virtualnumber.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.virtualnumber.model.ServiceEntity

@Database(entities = [CountryEntity::class, ServiceEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
    abstract fun serviceDao(): ServiceDao
}


