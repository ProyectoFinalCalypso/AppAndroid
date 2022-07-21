package com.example.calypsodivelog.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.calypsodivelog.model.EquipmentResponse
import com.example.calypsodivelog.repository.EquipmentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EquipmentRecyclerViewModel(app: Application): AndroidViewModel(app) {

    private val _listEquipment = MutableLiveData<MutableList<EquipmentResponse>>()
    var listEquipment: LiveData<MutableList<EquipmentResponse>> = _listEquipment

    private val _progressBarView = MutableLiveData<Boolean>()
    var progressBarView: LiveData<Boolean> = _progressBarView

    private val _errorConnection = MutableLiveData<Boolean>()
    var errorConnection: LiveData<Boolean> = _errorConnection

    private val _postDoneSuccessfully = MutableLiveData<Boolean>()
    var postDoneSuccessfully: LiveData<Boolean> = _postDoneSuccessfully

    private val _postError = MutableLiveData<Boolean>()
    var postError: LiveData<Boolean> = _postError

    private val _putDoneSuccessfully = MutableLiveData<Boolean>()
    var putDoneSuccessfully: LiveData<Boolean> = _putDoneSuccessfully

    private val _putError = MutableLiveData<Boolean>()
    var putError: LiveData<Boolean> = _putError

    private val _deleteDoneSuccessfully = MutableLiveData<Boolean>()
    var deleteDoneSuccessfully: LiveData<Boolean> = _deleteDoneSuccessfully

    private val _deleteError = MutableLiveData<Boolean>()
    var deleteError: LiveData<Boolean> = _deleteError

    private val _itemSelected = MutableLiveData<EquipmentResponse>()
    var itemSelected: LiveData<EquipmentResponse> = _itemSelected

    private val _editMode = MutableLiveData<Boolean>()
    var editMode: LiveData<Boolean> = _editMode

    private val _addMode = MutableLiveData<Boolean>()
    var addMode: LiveData<Boolean> = _addMode

    private var coroutineDownloadData: Job? = null

    private val repository = EquipmentRepository()

    fun resetStatusProperties(){
        _progressBarView.value = false
        _errorConnection.value = false
        _postDoneSuccessfully.value = false
        _postError.value = false
        _putDoneSuccessfully.value = false
        _putError.value = false
        _deleteDoneSuccessfully.value = false
        _deleteError.value = false
        _editMode.value = false
        _addMode.value = false
    }

    fun setItemSelected(item: EquipmentResponse) {
        _itemSelected.value = item
    }

    fun resetItemSelected(){
        _itemSelected.value = EquipmentResponse()
    }

    fun setEditMode(status: Boolean) {
        _editMode.value = status
    }

    fun setAddMode(status: Boolean) {
        _addMode.value = status
    }

    // Carga la lista con datos minimos para el Recycler
    fun downloadEquipmentList() {
        _errorConnection.value = false
        _progressBarView.value = true

        coroutineDownloadData = viewModelScope.launch {
            try {
                val listResponse = repository.getEquipmentList()

                if (listResponse != null) {
                    _listEquipment.value = listResponse!!
                    _progressBarView.value = false
                } else {
                    _errorConnection.value = true
                    _progressBarView.value = false
                }
            }catch (e : Exception){
                _errorConnection.value = true
                _progressBarView.value = false
                Log.e("*** Error ***", "EquipmentRecyclerViewModel > Line(65): ${e.message}")
            }
        }
    }

    fun cancelDownloadEquipmentList() {
        coroutineDownloadData?.cancel()
        coroutineDownloadData = null
    }

    // Actualiza los datos del servidor
    fun postEquipment() {
        _progressBarView.value = true
        _errorConnection.value = false
        _postDoneSuccessfully.value = false
        _postError.value = false

        viewModelScope.launch {
            try {
                if (itemSelected.value != null) {
                    when (repository.postEquipment(itemSelected.value!!)) {
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
                        else ->{
                            Log.e(">>> Error POST", "EquipmentRecyclerViewModel(133). Unknown error.")
                        }
                    }
                } else {
                    _progressBarView.value = false
                }
            } catch (e: Exception) {
                _errorConnection.value = true
                _progressBarView.value = false
                Log.e("*** Error ***", "EquipmentRecyclerViewModel(142): ${e.message}")
            }
        }
    }

    // Actualiza los datos del servidor
    fun putEquipment() {
        _progressBarView.value = true
        _errorConnection.value = false
        _putDoneSuccessfully.value = false
        _putError.value = false

        viewModelScope.launch {
            try {
                if (itemSelected.value != null) {
                    when (repository.putEquipment(itemSelected.value!!)) {
                        1 -> {
                            // PUT realizado con exito
                            _progressBarView.value = false
                            _putDoneSuccessfully.value = true
                        }
                        -1 -> {
                            // Error al realizar PUT
                            _putError.value = true
                            _progressBarView.value = false
                        }
                        0 -> {
                            // Error de conexion o respuesta inesperada
                            _errorConnection.value = true
                            _progressBarView.value = false
                        }
                        else ->{
                            Log.e(">>> Error PUT", "EquipmentRecyclerViewModel(174). Unknown error.")
                        }
                    }
                } else {
                    _progressBarView.value = false
                }
            } catch (e: Exception) {
                _errorConnection.value = true
                _progressBarView.value = false
                Log.e("*** Error ***", "EquipmentRecyclerViewModel(183): ${e.message}")
            }
        }
    }

    // Elimina los datos del servidor
    fun deleteEquipment() {
        _progressBarView.value = true
        _errorConnection.value = false
        _deleteDoneSuccessfully.value = false
        _deleteError.value = false

        viewModelScope.launch {
            try {
                if (itemSelected.value != null) {
                    when (repository.deleteEquipment(itemSelected.value!!.idEquipment)) {
                        1 -> {
                            // DELETE realizado con exito
                            _progressBarView.value = false
                            _deleteDoneSuccessfully.value = true
                        }
                        -1 -> {
                            // Error al realizar DELETE
                            _deleteError.value = true
                            _progressBarView.value = false
                        }
                        0 -> {
                            // Error de conexion o respuesta inesperada
                            _errorConnection.value = true
                            _progressBarView.value = false
                        }
                        else ->{
                            Log.e(">>> Error DELETE", "EquipmentRecyclerViewModel(215). Unknown error.")
                        }
                    }
                } else {
                    _progressBarView.value = false
                }
            } catch (e: Exception) {
                _errorConnection.value = true
                _progressBarView.value = false
                Log.e("*** Error ***", "EquipmentRecyclerViewModel(224): ${e.message}")
            }
        }
    }

}