package com.example.hsd.di

import android.content.Context
import com.example.hsd.networking.NetworkInterceptor
import com.example.hsd.networking.NetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://storage.googleapis.com"

    @Singleton
    @Provides
    fun provideHttpClient(@ApplicationContext context: Context): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(NetworkInterceptor(context))
            .build()
    }

    @Singleton
    @Provides
    fun provideNetworkService(client: OkHttpClient): NetworkService {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetworkService::class.java)
    }

}