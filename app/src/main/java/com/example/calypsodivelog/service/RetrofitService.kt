package com.example.calypsodivelog.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private var retrofit: Retrofit? = null

    private const val BASE_URL = "http://10.0.2.2:8081/calypso/api/"
    //private const val BASE_URL = "http://calypso.westeurope.cloudapp.azure.com:8081/calypso/api/"

    val getRetrofit: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}