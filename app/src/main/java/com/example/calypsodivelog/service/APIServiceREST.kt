package com.example.calypsodivelog.service

import com.example.calypsodivelog.model.DivelogFullResponse
import com.example.calypsodivelog.model.DivelogShortListResponse
import com.example.calypsodivelog.model.ResponseJson
import retrofit2.Response
import retrofit2.http.*

interface APIServiceREST {

    /*
    * URL Base
    * http://localhost:8081/calypso/api/
    * http://calypso.westeurope.cloudapp.azure.com:8081/calypso/api/
    * */

    @GET("divelog/id/{id}")
    @Headers("Content-Type: application/json")
    suspend fun getDivelogById(@Path("id") id: Int): Response<DivelogFullResponse>

    @GET("divelog/{nickName}/stats")
    @Headers("Content-Type: application/json")
    suspend fun getDivelogStats(@Path("nickName") nickName: String): Response<DivelogFullResponse>

    @GET("divelog/{nickName}/shortlist")
    @Headers("Content-Type: application/json")
    suspend fun getDivelogShortListByNickname(
        @Path("nickName") nickName: String
    ): Response<MutableList<DivelogShortListResponse>>

    // TODO(): POST
    @POST("divelog/{nickName}")
    // suspend fun postDivelog(@Path("nickName") nickName: String, @Body divelog: DivelogFullResponse): Response<String>
    suspend fun postDivelog(@Path("nickName") nickname: String, @Body divelog: DivelogFullResponse): Response<ResponseJson>
}