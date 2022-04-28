package com.example.calypsodivelog.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calypsodivelog.R
import com.example.calypsodivelog.databinding.DiveLogRecyclerBinding
import com.example.calypsodivelog.model.DivelogFullResponse
import com.example.calypsodivelog.model.DivelogShortListResponse
import com.example.calypsodivelog.service.ClickListenerDivelogShortList
import com.example.calypsodivelog.viewmodel.DivelogRecyclerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DiveLogRecycler : Fragment(), ClickListenerDivelogShortList {
    private lateinit var viewModel: DivelogRecyclerViewModel
    private var divelogAdapter = DiveLogHomeAdapter(this)
    private var headerAdapter = DiveLogHomeHeaderAdapter()
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: DiveLogRecyclerBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.let {
            ViewModelProvider(it)[DivelogRecyclerViewModel::class.java]
        }!!

        // Carga los datos si todavía no se han cargado
        if (viewModel.listDivelog.value == null || viewModel.divelogStats.value == null) {
            viewModel.downloadDivelogList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        viewModel.listDivelog.observe(viewLifecycleOwner) {
            divelogAdapter.setItems(it)
        }
        viewModel.divelogStats.observe(viewLifecycleOwner) {
            headerAdapter.setItems(it)
        }

        // Observador de cambios en Progress Bar
        viewModel.progressBarView.observe(viewLifecycleOwner) { trueOrFalse ->
            binding.progressBar.isVisible = trueOrFalse
        }

        // Observador de cambios en Error producido al conectarse con la API
        viewModel.errorConnection.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse) {
                dialogErrorConnectApi()
            }
        }

        binding.btnAddDivelog.setOnClickListener {
            navController.navigate(R.id.action_diveLogRecycler_to_divelogAdd)
        }
    }

    // Actualiza los datos descargados
    override fun onStart() {
        super.onStart()
        viewModel.downloadDivelogList()
    }

    // Cancela la solicictud de descarga de datos desde la API al cambiar a otro fragmento
    override fun onDetach() {
        super.onDetach()
        viewModel.cancelDownloadDivelogList()
    }

    // Muestra el Dialgo del Error
    private fun dialogErrorConnectApi() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.connection_error_title)
            .setMessage(R.string.connection_error_message)
            .setPositiveButton(R.string.cancel, null)
            .setNeutralButton(R.string.reconnect) { _, _ -> viewModel.downloadDivelogList() }
            .setCancelable(false)
            .show()
    }

    // Funcion de la Interfaz ClickListenerDivelogShortList
    override fun itemSelect(data: DivelogShortListResponse) {
        // Guarda la seleccion
        viewModel.setItemSelected(data)

        navController.navigate(R.id.action_diveLogRecycler_to_divelogDetails)
    }

    private fun showLastFragment() {
        if ((viewModel.itemDataSelected.value?.maxDepth ?: 0.0) > 0)
            navController.navigate(R.id.action_diveLogRecycler_to_divelogDetails)
    }

}

/*
*************************** ADAPTER - Data List***************************************
* */
class DiveLogHomeAdapter(private val listener: ClickListenerDivelogShortList) :
    RecyclerView.Adapter<DiveLogHomeHolder>() {
    private val itemList = mutableListOf<DivelogShortListResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiveLogHomeHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.dive_log_item_view, parent, false)
        return DiveLogHomeHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiveLogHomeHolder, position: Int) {
        val item: DivelogShortListResponse = itemList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            // Metodo sobreescrito que está en la clase 'DiveLogRecycler'
            listener.itemSelect(itemList[position])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItems(list: MutableList<DivelogShortListResponse>) {
        itemList.clear()
        // Carga los datos de la lista en orden inverso
        itemList.addAll(list.asReversed())
        notifyDataSetChanged()
    }
}

/*
*************************** HOLDER - Data List***************************************
* */
class DiveLogHomeHolder(v: View) : RecyclerView.ViewHolder(v) {
    // Se inicializan los controles de la vista
    private val tvDiveName: TextView = v.findViewById(R.id.tv_dive_name)
    private val tvDate: TextView = v.findViewById(R.id.tv_date)
    private val tvMaxDepth: TextView = v.findViewById(R.id.tv_max_depth)
    private val tvAvgDepth: TextView = v.findViewById(R.id.tv_avg_depth)
    private val tvDiveLength: TextView = v.findViewById(R.id.tv_dive_length)
    private val tvTemp: TextView = v.findViewById(R.id.tv_temp)
    private val tvSite: TextView = v.findViewById(R.id.tv_site)
    private val tvBuddy: TextView = v.findViewById(R.id.tv_buddy)

    fun bind(dive: DivelogShortListResponse) {
        tvDiveName.text = dive.numDive.toString() + " Dive at " + dive.location
        tvDate.text = dive.startDateTime.toString()
        tvMaxDepth.text = dive.maxDepth.toString()
        tvAvgDepth.text = dive.avgDepth.toString()
        tvDiveLength.text = dive.diveLength.toString()
        tvTemp.text = dive.temperature.toString()
        tvSite.text = dive.site
        tvBuddy.text = dive.buddyName
    }
}

/*
*************************** ADAPTER - Header Stats ***************************************
* */
class DiveLogHomeHeaderAdapter : RecyclerView.Adapter<DiveLogHomeHeaderHolder>() {
    private var itemD = DivelogFullResponse()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiveLogHomeHeaderHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.dive_log_header_view, parent, false)
        return DiveLogHomeHeaderHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiveLogHomeHeaderHolder, position: Int) {
        val item: DivelogFullResponse = itemD
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        // Si el objeto esta vacio, devuelve 0, en caso contrario devuelve 1.
        // En este caso para comprobar si esta vacio, comprueba la propiedad 'maxDepth'
        return if (itemD.maxDepth > 0) 1 else 0
    }

    fun setItems(divelog: DivelogFullResponse) {
        itemD = divelog
        notifyDataSetChanged()
    }
}

/*
*************************** HOLDER - Header Stats ***************************************
* */
class DiveLogHomeHeaderHolder(v: View) : RecyclerView.ViewHolder(v) {
    // Se inicializan los controles de la vista
    private val tvTotalDives: TextView = v.findViewById(R.id.tv_total_dives)
    private val tvMaxDepth: TextView = v.findViewById(R.id.tv_max_depth_stats)
    private val tvDiveLength: TextView = v.findViewById(R.id.tv_dive_max_length_stats)

    fun bind(dive: DivelogFullResponse) {
        tvTotalDives.text = dive.numDive.toString()
        tvMaxDepth.text = dive.maxDepth.toString()
        tvDiveLength.text = dive.diveLength.toString()
    }

}