package com.codeleg.dailyscope.DI

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.codeleg.dailyscope.database.local.NewsDao
import com.codeleg.dailyscope.database.network.NewsApiService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
object NewsRepositoryModule {

    private const val BASE_URL = "https://api.worldnewsapi.com/"
    private const val API_KEY = "fcdb8a062e524f8ba1d8d8889bd06e31"

    @Provides
    @Singleton
    fun provideClient() : okhttp3.OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newUrl = chain.request().url.newBuilder()
                    .addQueryParameter("api-key", API_KEY)
                    .build()

                chain.proceed(
                    chain.request().newBuilder().url(newUrl).build()
                )
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client:okhttp3.OkHttpClient): Retrofit{
       return  Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit) : NewsApiService{
        return retrofit.create(NewsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDataStore(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                application.preferencesDataStoreFile("settings")
            }
        )
    }

}