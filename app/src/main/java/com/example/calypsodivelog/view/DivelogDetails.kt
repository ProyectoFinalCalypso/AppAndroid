package com.example.calypsodivelog.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.calypsodivelog.databinding.DivelogDetailsBinding
import com.example.calypsodivelog.viewmodel.DiveLogViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DivelogDetails : Fragment() {

    lateinit var viewModel: DiveLogViewModel
    private lateinit var binding: DivelogDetailsBinding
    private var idDivelog = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            activity?.let {
                ViewModelProvider(it).get(DiveLogViewModel::class.java)
            }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DivelogDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Carga el Divelog desde la API
        viewModel.itemDataSelected.observe(viewLifecycleOwner){
            idDivelog = it.idDivelog
            viewModel.chargeDivelogById(idDivelog)
        }

        // Observador de actualización de datos cargados
        viewModel.divelog.observe(viewLifecycleOwner) {
            bindDataWithView()
        }

        // Observador de cambios en Progress Bar
        viewModel.progressBarView.observe(viewLifecycleOwner) { trueOrFalse ->
            binding.progressBar.isVisible = trueOrFalse
        }

        // Observador de cambios en Error producido al conectarse con la API
        viewModel.errorConnectionItemView.observe(viewLifecycleOwner) { trueOrFalse ->
            if(trueOrFalse) dialogErrorConnectApi()
        }
    }

    private fun bindDataWithView(){
        val divelog = viewModel.divelog.value

        binding.tvIdDivelogData.text = divelog?.idDivelog.toString()
        binding.tvNumDiveData.text = divelog?.numDive.toString()
        binding.tvDateDivelogData.text = divelog?.startDateTime.toString()
        binding.tvLocationData.text = divelog?.location.toString()
    }

    // Muestra el Dialgo del Error
    private fun dialogErrorConnectApi(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Connection Error")
            .setMessage("Error de conexion con el servidor.")
            .setPositiveButton("Cancelar") { dialog, which -> }
            .setNeutralButton("Reconectar"){dialog, which -> viewModel.chargeDivelogById(idDivelog)}
            .show()
    }
}