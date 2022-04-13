package com.example.calypsodivelog.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.calypsodivelog.model.DivelogFullResponse
import com.example.calypsodivelog.model.DivelogShortListResponse
import com.example.calypsodivelog.repository.DivelogRepository
import kotlinx.coroutines.launch

class DiveLogViewModel(app: Application): AndroidViewModel(app) {

    private val _listShort = MutableLiveData<MutableList<DivelogShortListResponse>>()
    var listShort: LiveData<MutableList<DivelogShortListResponse>> = _listShort

    private val _divelog = MutableLiveData<DivelogFullResponse>()
    var divelog: LiveData<DivelogFullResponse> = _divelog

    private val _progressBarView = MutableLiveData<Boolean>()
    var progressBarView: LiveData<Boolean> = _progressBarView

    private val _errorConnectionRecycler = MutableLiveData<Boolean>()
    var errorConnectionRecycler: LiveData<Boolean> = _errorConnectionRecycler

    private val _errorConnectionItemView = MutableLiveData<Boolean>()
    var errorConnectionItemView: LiveData<Boolean> = _errorConnectionItemView

    private val _itemSelected = MutableLiveData<DivelogShortListResponse>()
    var itemDataSelected: LiveData<DivelogShortListResponse> = _itemSelected

    fun setItemSelected(item: DivelogShortListResponse) {
        _itemSelected.value = item
    }

    private val repository = DivelogRepository()

    // Carga la lista con datos minimos para el Recycler
    fun chargeShortList(){
        _errorConnectionRecycler.value = false
        _progressBarView.value = true
        viewModelScope.launch {
            try {
                val response = repository.getShortList()

                if (response?.isSuccessful!!) {
                    _listShort.value = response.body() ?: mutableListOf<DivelogShortListResponse>()
                    _progressBarView.value = false
                } else {
                    _errorConnectionRecycler.value = true
                    _progressBarView.value = false
                }
            }catch (e : Exception){
                _errorConnectionRecycler.value = true
                _progressBarView.value = false
                Log.e("DiveLogViewModel.kt(58)", e.toString())
            }
        }
    }

    // Carga el Divelog seleccionada (por ID)
    fun chargeDivelogById(id: Int){
        _errorConnectionItemView.value = false
        _progressBarView.value = true
        viewModelScope.launch {
            try {
                val response = repository.getDivelogById(id)

                if (response?.isSuccessful!!) {
                    _divelog.value = response.body() ?: DivelogFullResponse()
                    val d = divelog.value?.idDivelog
                    Log.i("==============> ", "Datos cargados: $d")
                    _progressBarView.value = false
                } else {
                    _errorConnectionItemView.value = true
                    _progressBarView.value = false
                }
            }catch (e : Exception){
                _errorConnectionItemView.value = true
                _progressBarView.value = false
                Log.e("DiveLogViewModel.kt(83)", e.toString())
            }
        }
    }

}