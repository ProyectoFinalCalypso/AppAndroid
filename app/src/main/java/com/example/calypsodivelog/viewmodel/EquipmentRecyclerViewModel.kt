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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DivelogRecyclerViewModel(app: Application): AndroidViewModel(app) {

    private val _listShort = MutableLiveData<MutableList<DivelogShortListResponse>>()
    var listDivelog: LiveData<MutableList<DivelogShortListResponse>> = _listShort

    private val _divelogStats = MutableLiveData<DivelogFullResponse>()
    var divelogStats: LiveData<DivelogFullResponse> = _divelogStats

    private val _progressBarView = MutableLiveData<Boolean>()
    var progressBarView: LiveData<Boolean> = _progressBarView

    private val _errorConnection = MutableLiveData<Boolean>()
    var errorConnection: LiveData<Boolean> = _errorConnection

    private val _itemSelected = MutableLiveData<DivelogShortListResponse>()
    var itemDataSelected: LiveData<DivelogShortListResponse> = _itemSelected

    private var coroutineDownloadData: Job? = null

    fun setItemSelected(item: DivelogShortListResponse) {
        _itemSelected.value = item
    }

    fun resetItemSelected(){
        _itemSelected.value = DivelogShortListResponse()
    }

    fun getIdDivelogSelected(): Int{
        return itemDataSelected.value?.idDivelog ?: 0
    }

    private val repository = DivelogRepository()

    // Carga la lista con datos minimos para el Recycler
    fun downloadDivelogList() {
        _errorConnection.value = false
        _progressBarView.value = true

        coroutineDownloadData = viewModelScope.launch {
            try {
                val listResponse = repository.getDivelogShortList()
                val statsResponse = repository.getDivelogStats()

                if (listResponse != null && statsResponse != null) {
                    _listShort.value = listResponse!!
                    _divelogStats.value = statsResponse!!
                    _progressBarView.value = false
                } else {
                    _errorConnection.value = true
                    _progressBarView.value = false
                }
            }catch (e : Exception){
                _errorConnection.value = true
                _progressBarView.value = false
                Log.e("*** Error ***", "DivelogRecyclerViewModel > Line(69): ${e.message}")
            }
        }
    }

    fun cancelDownloadDivelogList() {
        coroutineDownloadData?.cancel()
        coroutineDownloadData = null
    }

}