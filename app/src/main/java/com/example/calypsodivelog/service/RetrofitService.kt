package com.example.calypsodivelog.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitService {
    private var retrofit: Retrofit? = null

    // Local Virtual Device
    private const val BASE_URL = "http://10.0.2.2:8081/calypso/api/"

    // Local Physical Device (Port Forwarding - chrome://inspect/#devices (3000 -> localhost:8081))
    // private const val BASE_URL = "http://localhost:3000/calypso/api/"

    // Server
    //private const val BASE_URL = "http://calypso.westeurope.cloudapp.azure.com:8081/calypso/api/"

    val httpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    // 2021-12-05T10:45:00Z[UTC]
    // Formato de Fecha que espera recibir la API REST
    private val gsonDateFilter = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z[UTC]'").create()

    val getRetrofit: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gsonDateFilter))
                    .client(httpClient)
                    .build()
            }
            return retrofit
        }

}