package com.example.calypsodivelog.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calypsodivelog.R
import com.example.calypsodivelog.databinding.EquipmentRecyclerBinding
import com.example.calypsodivelog.model.EquipmentResponse
import com.example.calypsodivelog.service.ClickListenerEquipmentList
import com.example.calypsodivelog.viewmodel.EquipmentRecyclerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat

class EquipmentRecycler : Fragment(), ClickListenerEquipmentList {
    private lateinit var viewModel: EquipmentRecyclerViewModel
    private var recyclerAdapter = EquipmentHomeAdapter(this)
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: EquipmentRecyclerBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.let {
            ViewModelProvider(it)[EquipmentRecyclerViewModel::class.java]
        }!!

        // Carga los datos si todavía no se han cargado
        if (viewModel.listEquipment.value == null) {
            viewModel.downloadEquipmentList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = EquipmentRecyclerBinding.inflate(inflater)

        // Recycler View
        recyclerView = binding.recyclerEquipmentList
        recyclerView.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = recyclerAdapter

        // Instancia de Controlador de Navegacion
        navController = NavHostFragment.findNavController(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Muestra el ultimo fragmento cargado de EquipmentDetails, si hay alguno.
        // TODO(): No funciona correctamente, al pulsar el boton 'Atras' resetea el fragment, pero no vuelve al Recycler
        //showLastFragment()

        // Observador de actualización de datos cargados
        viewModel.listEquipment.observe(viewLifecycleOwner) {
            recyclerAdapter.setItems(it)
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

        // TODO() Añadir nueva pieza de equipo
        binding.btnAddEquipment.setOnClickListener {
            viewModel.setAddMode(true)
            navController.navigate(R.id.action_equipmentRecycler_to_equipmentDetails)
        }
    }

    // TODO(): Actualiza los datos descargados -> Hacer que se actualicen solo cuando hayan cambios
    override fun onStart() {
        super.onStart()
        //viewModel.downloadEquipmentList()
    }

    // Cancela la solicictud de descarga de datos desde la API al cambiar a otro fragmento
    override fun onDetach() {
        super.onDetach()
        viewModel.cancelDownloadEquipmentList()
    }

    // Muestra el Dialgo del Error
    private fun dialogErrorConnectApi() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.connection_error_title)
            .setMessage(R.string.connection_error_message)
            .setPositiveButton(R.string.cancel, null)
            .setNeutralButton(R.string.reconnect) { _, _ -> viewModel.downloadEquipmentList() }
            .setCancelable(false)
            .show()
    }

    // TODO(): Guardar en viewModel la ID de la navegacion y comprobar si no es nula, ejecutar esa navegacion
    // De esta forma sería más universal, pudiendo guerdar cualquier navegacion con solo una comprobacion
    private fun showLastFragment() {
        if ((viewModel.itemSelected.value?.name?.length ?: 0) > 0) {
            navController.navigate(R.id.action_equipmentRecycler_to_equipmentDetails)
        }
    }

    // Funcion de la Interfaz ClickListenerEquipmentList
    override fun itemSelect(data: EquipmentResponse) {
        // Guarda la seleccion
        viewModel.setItemSelected(data)
        viewModel.setAddMode(false)
        navController.navigate(R.id.action_equipmentRecycler_to_equipmentDetails)
    }

}

/*
*************************** ADAPTER - Data List***************************************
* */
class EquipmentHomeAdapter(private val listener: ClickListenerEquipmentList) :
    RecyclerView.Adapter<EquipmentHomeHolder>() {
    private val itemList = mutableListOf<EquipmentResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentHomeHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.equipment_item_view, parent, false)
        return EquipmentHomeHolder(itemView)
    }

    override fun onBindViewHolder(holder: EquipmentHomeHolder, position: Int) {
        val item: EquipmentResponse = itemList[position]
        holder.bind(item, position)

        holder.itemView.setOnClickListener {
            // Metodo sobreescrito que está en la clase 'EquipmentRecycler'
            listener.itemSelect(itemList[position])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItems(list: MutableList<EquipmentResponse>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }
}

/*
*************************** HOLDER - Data List***************************************
* */
class EquipmentHomeHolder(val v: View) : RecyclerView.ViewHolder(v) {
    // Se inicializan los controles de la vista
    private val tvItemName: TextView = v.findViewById(R.id.itemName)
    private val tvLastCheck: TextView = v.findViewById(R.id.lastCheck)
    private val tvNextCheck: TextView = v.findViewById(R.id.nextCheck)
    private val layoutItem: ConstraintLayout = v.findViewById(R.id.layout_item_equipment)

    fun bind(e: EquipmentResponse, position: Int) {
        val p = (position + 1) % 2
        if (p != 0)
            layoutItem.setBackgroundColor(v.resources.getColor(R.color.gray1))

        // Nombre de la pieza
        tvItemName.text = e.name

        // Ultima Revision
        val lastCheck = e.lastCheckDate
        if(lastCheck != null)
            tvLastCheck.text = SimpleDateFormat("dd/MM/yyyy").format(lastCheck)
        else
            tvLastCheck.text = "--/--/--"

        // Siguiente Revision
        val nextCheck = e.getNextCkeckDate()
        if(nextCheck != null)
            tvNextCheck.text = SimpleDateFormat("dd/MM/yyyy").format(nextCheck)
        else
            tvNextCheck.text = "--/--/--"
    }
}