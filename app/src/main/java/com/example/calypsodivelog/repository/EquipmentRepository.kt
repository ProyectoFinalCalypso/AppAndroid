package com.example.calypsodivelog.repository

import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.calypsodivelog.model.DivelogFullResponse
import com.example.calypsodivelog.model.DivelogShortListResponse
import com.example.calypsodivelog.service.APIServiceREST
import com.example.calypsodivelog.service.RetrofitService
import com.google.gson.Gson
import kotlin.time.Duration.Companion.minutes

class DivelogRepository {

    /*
    * URL Base
    * http://localhost:8081/calypso/api/
    * http://calypso.westeurope.cloudapp.azure.com:8081/calypso/api/
    * */

    private var apiService: APIServiceREST? = null

    // TODO(): Cargar el NickName desde una CONSTANTE que se genera cuando se logea el usuario
    private val nickName = "UsuarioEjemplo1"

    init {
        apiService = RetrofitService.getRetrofit?.create(APIServiceREST::class.java)
    }

    // En caso de fallo de conexion, devuelve null
    suspend fun getDivelogById(id: Int): DivelogFullResponse? {
        val response = apiService?.getDivelogById(id)

        return if (response?.isSuccessful == true) {
            response.body() ?: DivelogFullResponse()
        } else {
            null
        }
    }

    suspend fun getDivelogShortList() : MutableList<DivelogShortListResponse>? {
        val response = apiService?.getDivelogShortListByNickname(nickName)

        return if (response?.isSuccessful == true) {
            response.body() ?: mutableListOf<DivelogShortListResponse>()
        } else {
            null
        }
    }

    suspend fun getDivelogStats() : DivelogFullResponse? {
        val response = apiService?.getDivelogStats(nickName)

        return if (response?.isSuccessful == true) {
            response.body() ?: DivelogFullResponse()
        } else {
            null
        }
    }

    suspend fun uploadDivelog(divelog: DivelogFullResponse) : Int {
        // Devuelve un ResponseJson
        val response = apiService?.postDivelog(nickName, divelog)

        return when (response?.body()?.status) {
            "OK" -> 1
            "ERROR" -> -1
            else -> {
                Log.e(response?.body()?.status, response?.body()?.message ?: "Error: DivelogRepository line 68")
                0
            }
        }
    }

    suspend fun updateDivelog(divelog: DivelogFullResponse) : Int {
        // Devuelve un ResponseJson
        val response = apiService?.putDivelog(nickName, divelog)

        return when (response?.body()?.status) {
            "OK" -> 1
            "ERROR" -> -1
            else -> {
                Log.e(response?.body()?.status, response?.body()?.message ?: "Error: DivelogRepository line 82")
                0
            }
        }
    }
}