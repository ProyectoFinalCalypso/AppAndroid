package com.example.calypsodivelog.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calypsodivelog.R
import com.example.calypsodivelog.databinding.DiveLogRecyclerBinding
import com.example.calypsodivelog.model.DivelogShortListResponse
import com.example.calypsodivelog.service.ClickListenerDivelogShortList
import com.example.calypsodivelog.viewmodel.DiveLogViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DiveLogRecycler : Fragment(), ClickListenerDivelogShortList {
    lateinit var viewModel: DiveLogViewModel
    private var divelogAdapter = DiveLogHomeAdapter(this)
    private var headerAdapter = DiveLogHomeHeaderAdapter()
    private lateinit var recyclerView : RecyclerView
    private lateinit var binding: DiveLogRecyclerBinding
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.let {
                ViewModelProvider(it).get(DiveLogViewModel::class.java)
            }!!
        // Carga los datos si todavía no se han cargado
        viewModel.listShort.value ?: viewModel.getShortList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DiveLogRecyclerBinding.inflate(inflater)

        // Recycler View
        recyclerView = binding.recyclerDivelogList
        recyclerView.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ConcatAdapter(headerAdapter, divelogAdapter)

        // Instancia de Controlador de Navegacion
        navController = NavHostFragment.findNavController(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Muestra el ultimo fragmento cargado de DivelogDetails, si hay alguno.
        showLastFragment()

        // Observador de actualización de datos cargados
        viewModel.listShort.observe(viewLifecycleOwner) {
            headerAdapter.setItems(divelogStatsHeader(it))
            divelogAdapter.setItems(it)
        }

        // Observador de cambios en Progress Bar
        viewModel.progressBarView.observe(viewLifecycleOwner) { trueOrFalse ->
            binding.progressBar.isVisible = trueOrFalse
        }

        // Observador de cambios en Error producido al conectarse con la API
        viewModel.errorConnectionRecycler.observe(viewLifecycleOwner) { trueOrFalse ->
            if(trueOrFalse){
                dialogErrorConnectApi()
            }
        }
    }

    // Procesa los datos de la lista para crear las Estadisticas ***********************************
    // TODO(): El calculo se pasara al servidor de la API y devolvera los datos finales
    private fun divelogStatsHeader(d: MutableList<DivelogShortListResponse>): DivelogShortListResponse {
        var maxDepth = 0.0
        var maxDiveLength = 0
        var notes = "Estadisticas"

        // TODO() Optimizar el algoritmo del buscador
        d.forEach() {
            if (maxDepth < it.maxDepth) maxDepth = it.maxDepth
            if (maxDiveLength < it.diveLength) maxDiveLength = it.diveLength
        }

        var dive = DivelogShortListResponse()
        dive.maxDepth = maxDepth
        dive.diveLength = maxDiveLength
        dive.location = notes

        return dive
    }

    // Muestra el Dialgo del Error
    private fun dialogErrorConnectApi(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Connection Error")
            .setMessage("Error de conexion con el servidor.")
            .setPositiveButton("Cancelar") { dialog, which -> }
            .setNeutralButton("Reconectar"){dialog, which -> viewModel.getShortList()}
            .setCancelable(false)
            .show()
    }

    // Funcion de la Interfaz ClickListenerDivelogShortList
    override fun itemSelect(data: DivelogShortListResponse) {
        // Guarda la seleccion
        viewModel.setItemSelected(data)

        navController.navigate(R.id.action_diveLogRecycler_to_divelogDetails)
    }

    private fun showLastFragment(){
        if((viewModel.itemDataSelected.value?.maxDepth ?: 0.0) > 0 )
            navController.navigate(R.id.action_diveLogRecycler_to_divelogDetails)
    }

}

/*
*************************** ADAPTER - Data List***************************************
* */
class DiveLogHomeAdapter(private val listener: ClickListenerDivelogShortList) : RecyclerView.Adapter<DiveLogHomeHolder>(){
    private val itemList = mutableListOf<DivelogShortListResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiveLogHomeHolder {
        var itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.dive_log_item_view, parent, false)
        val holder = DiveLogHomeHolder(itemView)
        return holder
    }

    override fun onBindViewHolder(holder: DiveLogHomeHolder, position: Int) {
        val item: DivelogShortListResponse = itemList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            // Metodo sobreescrito en la clase 'DiveLogRecycler'
            listener.itemSelect(itemList[position])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItems(list: MutableList<DivelogShortListResponse>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }
}

/*
*************************** HOLDER - Data List***************************************
* */
class DiveLogHomeHolder(val v: View) : RecyclerView.ViewHolder(v) {
    // Se inicializan los controles de la vista
    var tvDiveName: TextView = v.findViewById(R.id.tv_dive_name)
    var tvDate: TextView = v.findViewById(R.id.tv_date)
    var tvMaxDepth: TextView = v.findViewById(R.id.tv_max_depth)
    var tvAvgDepth: TextView = v.findViewById(R.id.tv_avg_depth)
    var tvDiveLength: TextView = v.findViewById(R.id.tv_dive_length)
    var tvTemp: TextView = v.findViewById(R.id.tv_temp)
    var tvSite: TextView = v.findViewById(R.id.tv_site)
    var tvBuddy: TextView = v.findViewById(R.id.tv_buddy)

    fun bind(dive: DivelogShortListResponse) {
        tvDiveName.text = dive.numDive.toString() + " Dive at " + dive.city
        tvDate.text = dive.startDateTime.toString()
        tvMaxDepth.text = dive.maxDepth.toString()
        tvAvgDepth.text = dive.avgDepth.toString()
        tvDiveLength.text = dive.diveLength.toString()
        tvTemp.text = dive.temperature.toString()
        tvSite.text = dive.location
        tvBuddy.text = dive.buddyName
    }
}

/*
*************************** ADAPTER - Header Stats ***************************************
* */
class DiveLogHomeHeaderAdapter() : RecyclerView.Adapter<DiveLogHomeHeaderHolder>(){
    private var itemD = DivelogShortListResponse()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiveLogHomeHeaderHolder {
        var itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.dive_log_header_view, parent, false)
        val holder = DiveLogHomeHeaderHolder(itemView)
        return holder
    }

    override fun onBindViewHolder(holder: DiveLogHomeHeaderHolder, position: Int) {
        val item: DivelogShortListResponse = itemD
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        // Si el objeto esta vacio, devuelve 0, en caso contrario devuelve 1.
        // En este caso para comprobar si esta vacio, comprueba la propiedad 'maxDepth'
        return if(itemD.maxDepth > 0) 1 else 0
    }

    fun setItems(divelog: DivelogShortListResponse) {
        itemD = divelog
        notifyDataSetChanged()
    }
}

/*
*************************** HOLDER - Header Stats ***************************************
* */
class DiveLogHomeHeaderHolder(val v: View) : RecyclerView.ViewHolder(v) {
    // Se inicializan los controles de la vista
    var tvDiveName: TextView = v.findViewById(R.id.tv_header_divelog_stats)
    var tvMaxDepth: TextView = v.findViewById(R.id.tv_max_depth_stats)
    var tvDiveLength: TextView = v.findViewById(R.id.tv_dive_max_length_stats)

    fun bind(dive: DivelogShortListResponse) {
        tvDiveName.text = dive.location
        tvMaxDepth.text = dive.maxDepth.toString()
        tvDiveLength.text = dive.diveLength.toString()
    }

}