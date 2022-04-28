package com.example.calypsodivelog.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.calypsodivelog.model.DivelogFullResponse
import com.example.calypsodivelog.model.DivelogModel
import com.example.calypsodivelog.model.DivelogShortListResponse
import com.example.calypsodivelog.repository.DivelogRepository
import com.example.calypsodivelog.service.ImageUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DivelogDetailsViewModel(app: Application) : AndroidViewModel(app) {

    private val _divelog = MutableLiveData<DivelogModel>()
    var divelog: LiveData<DivelogModel> = _divelog

    private val _progressBarView = MutableLiveData<Boolean>()
    var progressBarView: LiveData<Boolean> = _progressBarView

    private val _errorConnection = MutableLiveData<Boolean>()
    var errorConnection: LiveData<Boolean> = _errorConnection

    private val _editMode = MutableLiveData<Boolean>()
    var editMode: LiveData<Boolean> = _editMode

    private var coroutineDownloadData: Job? = null

    private val repository = DivelogRepository()

    fun removePhoto(item: Bitmap) {
        _divelog.value?.listPhotos?.remove(item)
    }

    fun setEditMode(status: Boolean){
        _editMode.value = status
    }

    // TODO(): Crear el metodo PUT para actualizar el divelog
    fun updateDivelogOnServer(){

    }

    // Carga el Divelog seleccionada (por ID)
    fun downloadDivelogById(id: Int) {
        _errorConnection.value = false
        _progressBarView.value = true

        coroutineDownloadData = viewModelScope.launch {
            try {
                // Devuelve NULL en caso de error de conexion
                val response = repository.getDivelogById(id)

                if (response != null) {
                    _divelog.value = convertData(response)
                    _progressBarView.value = false
                } else {
                    _errorConnection.value = true
                    _progressBarView.value = false
                }
            } catch (e: Exception) {
                _errorConnection.value = true
                _progressBarView.value = false
                Log.e("*** Error ***", "DiveLogDetailsViewModel > Line (64): ${e.message}")
            }
        }
    }

    private fun convertData(d: DivelogFullResponse): DivelogModel {
        val divelog = DivelogModel()

        divelog.avgDepth = d.avgDepth
        divelog.buddiesDivingCollection = d.buddiesDivingCollection
        divelog.buddyName = d.buddyName
        divelog.country = d.country
        divelog.decoTime = d.decoTime
        divelog.diveLength = d.diveLength
        divelog.divingCenter = d.divingCenter
        divelog.gasConsumption = d.gasConsumption
        divelog.idDivelog = d.idDivelog
        divelog.location = d.location
        divelog.maxDepth = d.maxDepth
        divelog.notes = d.notes
        divelog.numDive = d.numDive
        divelog.site = d.site
        divelog.startDateTime = d.startDateTime
        divelog.temperature = d.temperature

        if (d.photo1.isNotEmpty()) {
            divelog.listPhotos.add(ImageUtil.convertStringToBitmap(d.photo1)!!)
        }

        if (d.photo2.isNotEmpty()) {
            divelog.listPhotos.add(ImageUtil.convertStringToBitmap(d.photo2)!!)
        }

        if (d.photo3.isNotEmpty()) {
            divelog.listPhotos.add(ImageUtil.convertStringToBitmap(d.photo3)!!)
        }

        if (d.photo4.isNotEmpty()) {
            divelog.listPhotos.add(ImageUtil.convertStringToBitmap(d.photo4)!!)
        }

        return divelog
    }

    fun resetDivelog() {
        _divelog.value = DivelogModel()
        _editMode.value = false
    }

    fun cancelGetDivelogById() {
        coroutineDownloadData?.cancel()
        coroutineDownloadData = null
    }

}