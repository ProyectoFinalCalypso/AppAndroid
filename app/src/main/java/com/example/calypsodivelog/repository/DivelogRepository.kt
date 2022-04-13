package com.example.calypsodivelog.repository

import com.example.calypsodivelog.service.APIServiceREST
import com.example.calypsodivelog.service.RetrofitService

class DivelogRepository {
    private var apiService: APIServiceREST? = null

    // TODO(): Cargar el NickName desde una CONSTANTE que se genera cuando se logea el usuario
    private val nickName = "UsuarioEjemplo1"

    init {
        apiService = RetrofitService.getRetrofit?.create(APIServiceREST::class.java)
    }

    suspend fun getDivelogById(id: Int) = apiService?.getDivelogById("divelog/id/$id")

    suspend fun getShortList() = apiService?.getDivelogShortListByNickname("divelog/$nickName/shortlist")
}