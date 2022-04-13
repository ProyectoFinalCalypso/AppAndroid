package com.example.calypsodivelog.service

import com.example.calypsodivelog.model.DivelogFullListResponse
import com.example.calypsodivelog.model.DivelogShortListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIServiceREST {

    @GET
    suspend fun getDivelogFullListByNickname(@Url url:String): Response<MutableList<DivelogFullListResponse>>

    @GET
    suspend fun getDivelogShortListByNickname(@Url url:String): Response<MutableList<DivelogShortListResponse>>
}