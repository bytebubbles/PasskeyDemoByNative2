package com.example.passkeydemobynative2.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    
    // 修改为你的服务器地址
    //private const val BASE_URL = "http://10.0.2.2:8080/"
    //private const val BASE_URL = "http://localhost:8080/"
    private const val BASE_URL = "https://nonresilient-boundedly-aleta.ngrok-free.dev/"
    // 注意: 10.0.2.2 是 Android 模拟器访问主机 localhost 的特殊 IP
    // 如果使用真机，请使用实际的服务器 IP 地址
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val passkeysApi: PasskeysApiService = retrofit.create(PasskeysApiService::class.java)
}

