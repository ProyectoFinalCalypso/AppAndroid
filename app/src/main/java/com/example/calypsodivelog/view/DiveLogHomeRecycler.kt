package com.example.calypsodivelog.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calypsodivelog.R
import com.example.calypsodivelog.databinding.DiveLogFragmentRecyclerBinding
import com.example.calypsodivelog.model.DivelogShortListResponse
import com.example.calypsodivelog.viewmodel.DiveLogViewModel

class DiveLogHomeRecycler(): Fragment(){
    lateinit var viewModel: DiveLogViewModel
    private lateinit var divelogAdapter: DiveLogHomeAdapter
    private lateinit var headerAdapter: DiveLogHomeHeaderAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var binding: DiveLogFragmentRecyclerBinding

   /*
        TODO() ¿Cómo optimizar el rendimiento con la carga de datos en el Recycler?
        En caso de tener 1000 Logs de buceo, cargarlos solo cuando lo pida el Recycler
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            activity?.let {
                ViewModelProvider(it).get(DiveLogViewModel::class.java)
            }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DiveLogFragmentRecyclerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        divelogAdapter = DiveLogHomeAdapter()
        headerAdapter = DiveLogHomeHeaderAdapter()

        // Carga de datos en  los Adapters
        viewModel.listShort.value?.let {
            headerAdapter.setItems(divelogStatsHeader(it))
            divelogAdapter.setItems(it)
        }

        recyclerView = binding.recyclerDivelogList
        recyclerView.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)

        // Se actualizan los datos del Recycler en cuento el Observador detecte cambios en ViewModel
        viewModel.listShort.observe(viewLifecycleOwner) {
            headerAdapter.setItems(divelogStatsHeader(it))
            divelogAdapter.setItems(it)
        }

        // Actualiza el estado de la barra de progresso (carga de datos)
        viewModel.progressBar.observe(viewLifecycleOwner) { trueOrFalse ->
            binding.progress.isVisible = trueOrFalse
        }

        // TODO() Al tener dos listas, habrán conflictos de posicion del item al trabajar con listeners
        // solucion: viewHolder.bindingAdapterPosition¿?
        var concatAdapter = ConcatAdapter(headerAdapter, divelogAdapter)
        recyclerView.adapter = concatAdapter

        // Si no hay datos, se cargan desde la API
        viewModel.listShort.value ?: viewModel.chargeShortList()
    }

    // Procesa los datos de la lista para crear las Estadisticas ***********************************
    // TODO(): El calculo se pasara al servidor de la API y devolvera los datos finales
    private fun divelogStatsHeader(d: MutableList<DivelogShortListResponse>): DivelogShortListResponse {
        var maxDepth = 0.0
        var maxDiveLength = 0
        var notes = "Estadisticas"

        // TODO() Optimizar el buscador
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
}

/*
*************************** ADAPTER ***************************************
* */
class DiveLogHomeAdapter() : RecyclerView.Adapter<DiveLogHomeHolder>(){
    private val itemList = mutableListOf<DivelogShortListResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiveLogHomeHolder {
        var itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.dive_log_item_view, parent, false)
        val holder = DiveLogHomeHolder(itemView)
        return holder
    }

    override fun onBindViewHolder(holder: DiveLogHomeHolder, position: Int) {
        val item: DivelogShortListResponse = itemList[position]
        holder.bind(item)
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
*************************** HOLDER ***************************************
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
*************************** ADAPTER - Header ***************************************
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
        return 1
    }

    fun setItems(divelog: DivelogShortListResponse) {
        itemD = divelog
        notifyDataSetChanged()
    }
}

/*
*************************** HOLDER - Header ***************************************
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

