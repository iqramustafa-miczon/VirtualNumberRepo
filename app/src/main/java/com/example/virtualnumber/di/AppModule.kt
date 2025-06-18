package com.example.virtualnumber.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.virtualnumber.remote.RemoteDataSource
import com.example.virtualnumber.repository.VirtualNumberRepository
import com.example.virtualnumber.retrofit.SmsActivateApi
import com.example.virtualnumber.room.AppDatabase
import com.example.virtualnumber.room.CountryDao
import com.example.virtualnumber.room.ServiceDao
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl("https://sms-activate.io/")
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson)) // Handle JSON
            .build()
    }

    @Provides
    @Singleton
    fun provideSmsActivateApi(retrofit: Retrofit): SmsActivateApi {
        return retrofit.create(SmsActivateApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(api: SmsActivateApi): RemoteDataSource {
        return RemoteDataSource(api)
    }

    /*@Provides
    @Singleton
    fun provideVirtualNumberRepository(remoteDataSource: RemoteDataSource): VirtualNumberRepository {
        return VirtualNumberRepository(remoteDataSource)
    }*/

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "VirtualNumberDB"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCountryDao(database: AppDatabase): CountryDao {
        return database.countryDao()
    }

    @Provides
    @Singleton
    fun provideServiceDao(database: AppDatabase): ServiceDao {
        return database.serviceDao()
    }

    @Provides
    @Singleton
    fun provideVirtualNumberRepository(
        remoteDataSource: RemoteDataSource,
        serviceDao: ServiceDao,
        countryDao: CountryDao,
    ): VirtualNumberRepository {
        return VirtualNumberRepository(remoteDataSource, serviceDao, countryDao)
    }
}
