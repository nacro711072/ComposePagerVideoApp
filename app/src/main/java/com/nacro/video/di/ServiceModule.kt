package com.nacro.video.di

import com.nacro.video.Const
import com.nacro.video.datasource.VideoDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataSourceModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }

    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl(Const.REMOTE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        VideoDataSource()
    }
}