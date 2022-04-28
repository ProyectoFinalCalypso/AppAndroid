package com.example.calypsodivelog.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.calypsodivelog.model.DateTimeModel
import com.example.calypsodivelog.model.DivelogFullResponse
import com.example.calypsodivelog.model.DivelogModel
import com.example.calypsodivelog.repository.DivelogRepository
import com.example.calypsodivelog.service.ImageUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class DivelogAddViewModel(app: Application) : AndroidViewModel(app) {

    private val _progressBarView = MutableLiveData<Boolean>()
    var progressBarView: LiveData<Boolean> = _progressBarView

    private val _errorConnection = MutableLiveData<Boolean>()
    var errorConnection: LiveData<Boolean> = _errorConnection

    private val _postDoneSuccessfully = MutableLiveData<Boolean>()
    var postDoneSuccessfully: LiveData<Boolean> = _postDoneSuccessfully

    private val _postError = MutableLiveData<Boolean>()
    var postError: LiveData<Boolean> = _postError

    private val _divelog = MutableLiveData<DivelogModel>()
    var divelog: LiveData<DivelogModel> = _divelog

    val dateTime = MutableLiveData<DateTimeModel>()

    private var coroutineUploadData: Job? = null

    private val repository = DivelogRepository()

    fun setDivelog(d:DivelogModel){
        _divelog.value = d
    }

    // Envia los datos al servidor
    fun uploadDivelogToServer() {
        _progressBarView.value = true
        _errorConnection.value = false
        _postDoneSuccessfully.value = false
        _postError.value = false

        coroutineUploadData = viewModelScope.launch {
            try {
                if(divelog.value != null) {
                    when (repository.uploadDivelog(convertData(divelog.value!!))) {
                        1 -> {
                            // POST realizado con exito
                            _progressBarView.value = false
                            _postDoneSuccessfully.value = true
                        }
                        -1 -> {
                            // Error al realizar POST
                            _postError.value = true
                            _progressBarView.value = false
                        }
                        0 -> {
                            // Error de conexion o respuesta inesperada
                            _errorConnection.value = true
                            _progressBarView.value = false
                        }
                    }
                }else{
                    _progressBarView.value = false
                }
            } catch (e: Exception) {
                _errorConnection.value = true
                _progressBarView.value = false
                Log.e("*** Error ***", "DivelogAddViewModel > Line (78): ${e.message}")
            }
        }
    }

    private fun convertData(d: DivelogModel): DivelogFullResponse {
        val divelog = DivelogFullResponse()

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

        if (d.listPhotos.size > 0) {
            divelog.photo1 = ImageUtil.convertBitmapToString(d.listPhotos[0])
        }

        if (d.listPhotos.size > 1) {
            divelog.photo2 = ImageUtil.convertBitmapToString(d.listPhotos[1])
        }

        if (d.listPhotos.size > 2) {
            divelog.photo3 = ImageUtil.convertBitmapToString(d.listPhotos[2])
        }

        if (d.listPhotos.size > 3) {
            divelog.photo4 = ImageUtil.convertBitmapToString(d.listPhotos[3])
        }

        return divelog
    }

    fun resetDivelogAdd() {
        _divelog.value = DivelogModel()
        _postDoneSuccessfully.value = false
    }

    fun cancelUploadDivelogToServer() {
        coroutineUploadData?.cancel()
        coroutineUploadData = null
    }

    fun getDate(): Date{
        val c = dateTime.value
        return Date(c!!.year-1900,c!!.month-1, c!!.day, c!!.hour, c!!.minute)
    }

}