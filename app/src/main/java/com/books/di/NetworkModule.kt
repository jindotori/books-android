package com.books.di

import com.books.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@InstallIn(ActivityRetainedComponent::class)
@Module
class NetworkModule {

    @ActivityRetainedScoped
    @Provides
    fun provideApiService() : ApiService {
        return  Retrofit.Builder()
                .baseUrl("https://api.itbook.store/1.0/")
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }

    private fun createOkHttpClient() : OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder().addInterceptor(logger).build()
    }
}