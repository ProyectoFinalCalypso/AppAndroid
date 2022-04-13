package com.example.calypsodivelog.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.calypsodivelog.model.DivelogFullListResponse
import com.example.calypsodivelog.model.DivelogShortListResponse
import com.example.calypsodivelog.repository.DivelogRepository
import kotlinx.coroutines.launch

class DiveLogViewModel(app: Application): AndroidViewModel(app) {

    private val _listShort = MutableLiveData<MutableList<DivelogShortListResponse>>()
    var listShort: LiveData<MutableList<DivelogShortListResponse>> = _listShort

    private val _listFull = MutableLiveData<MutableList<DivelogFullListResponse>>()
    var listFull: LiveData<MutableList<DivelogFullListResponse>> = _listFull

    private val _progressBar = MutableLiveData<Boolean>()
    var progressBar: LiveData<Boolean> = _progressBar

    private val repository = DivelogRepository()

    // TODO(): Controlar la excepcion 'SocketTimeoutException' al fallar la conexion
    // Carga la lista con datos minimos para el Recycler
    fun chargeShortList(){
        _progressBar.value = true
        viewModelScope.launch {
            _listShort.value = repository.getShortList()?.body() ?: mutableListOf<DivelogShortListResponse>()
            _progressBar.value = false
        }
    }

    // TODO(): Controlar la excepcion 'SocketTimeoutException' al fallar la conexion
    // Carga la lista con todos los datos, incluyendo las fotos
    fun chargeFullList(){
        _progressBar.value = true
        viewModelScope.launch {
            _listFull.value = repository.getFullList()?.body() ?: mutableListOf<DivelogFullListResponse>()
            _progressBar.value = false
        }
    }

}