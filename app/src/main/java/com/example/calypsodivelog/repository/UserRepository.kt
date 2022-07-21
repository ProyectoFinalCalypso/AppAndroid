package com.example.calypsodivelog.repository

import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.calypsodivelog.model.DivelogFullResponse
import com.example.calypsodivelog.model.DivelogShortListResponse
import com.example.calypsodivelog.model.EquipmentResponse
import com.example.calypsodivelog.service.APIServiceREST
import com.example.calypsodivelog.service.RetrofitService
import com.example.calypsodivelog.service.UserInfo
import com.google.gson.Gson
import kotlin.time.Duration.Companion.minutes

class EquipmentRepository {

    /*
    * URL Base
    * http://localhost:8081/calypso/api/
    * http://calypso.westeurope.cloudapp.azure.com:8081/calypso/api/
    * */

    private var apiService: APIServiceREST? = null

    init {
        apiService = RetrofitService.getRetrofit?.create(APIServiceREST::class.java)
    }

    suspend fun getEquipmentList() : MutableList<EquipmentResponse>? {
        val response = apiService?.getAllEquipmentByNickName(UserInfo.getUser()?.nickname ?: "")

        return if (response?.isSuccessful == true) {
            response.body() ?: mutableListOf<EquipmentResponse>()
        } else {
            null
        }
    }

    suspend fun postEquipment(item: EquipmentResponse) : Int {
        val response = apiService?.postEquipment(UserInfo.getUser()?.nickname ?: "", item)

        return when (response?.body()?.status) {
            "OK" -> 1
            "ERROR" -> -1
            else -> {
                Log.e(response?.body()?.status, response?.body()?.message ?: "Error: EquipmentRepository(45) -> Null Response")
                -1
            }
        }
    }

    suspend fun putEquipment(item: EquipmentResponse) : Int {
        val response = apiService?.putEquipment(UserInfo.getUser()?.nickname ?: "", item)

        return when (response?.body()?.status) {
            "OK" -> 1
            "ERROR" -> -1
            else -> {
                Log.e(response?.body()?.status, response?.body()?.message ?: "Error: EquipmentRepository(58) -> Null Response")
                -1
            }
        }
    }

    suspend fun deleteEquipment(id: Int) : Int {
        val response = apiService?.deleteEquipment(id)

        return when (response?.body()?.status) {
            "OK" -> 1
            "ERROR" -> -1
            else -> {
                Log.e(response?.body()?.status, response?.body()?.message ?: "Error: EquipmentRepository(71) -> Null Response")
                -1
            }
        }
    }

}