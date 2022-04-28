package com.example.calypsodivelog.view

import android.graphics.Bitmap
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calypsodivelog.R
import com.example.calypsodivelog.databinding.DivelogDetailsBinding
import com.example.calypsodivelog.model.DivelogModel
import com.example.calypsodivelog.service.ClickListenerPhotosDivelog
import com.example.calypsodivelog.viewmodel.DivelogDetailsViewModel
import com.example.calypsodivelog.viewmodel.DivelogRecyclerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.nio.file.Files.delete
import java.util.*

class DivelogDetails : Fragment(), ClickListenerPhotosDivelog {

    private lateinit var viewModel: DivelogDetailsViewModel
    private lateinit var viewModelRecycler: DivelogRecyclerViewModel
    private lateinit var binding: DivelogDetailsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var navController: NavController
    private var recyclerAdapter = PhotosDiveLogAdapter(this)
    private var listEditText: List<EditText> = emptyList()
    private var idDivelog: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            activity?.let {
                ViewModelProvider(it)[DivelogDetailsViewModel::class.java]
            }!!

        viewModelRecycler =
            activity?.let {
                ViewModelProvider(it)[DivelogRecyclerViewModel::class.java]
            }!!

        idDivelog = viewModelRecycler.getIdDivelogSelected()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DivelogDetailsBinding.inflate(inflater)

        // Instancia de Controlador de Navegacion
        navController = NavHostFragment.findNavController(this)

        // Recycler View
        recyclerView = binding.recyclerPhotosDivelog
        recyclerView.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = recyclerAdapter

        // Oculta el Layout Principal, para que no se vea hasta que no se carguen los datos
        binding.fragmentDataLayoutDivelogDetails.visibility = View.GONE

        // Crea el listado de los controles EditText de la vista
        listEditText = getListOfEditTextViews()

        // Configura el Estilo de los EditText
        setStyleEditText()

        // Deshabilita la Edicion del Divelog
        disableEditingDivelog()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.divelog.value?.idDivelog ?: 0 == 0) {
            // Carga el Divelog desde la API
            viewModel.downloadDivelogById(idDivelog)

        }

        // Observador de actualización de datos cargados
        viewModel.divelog.observe(viewLifecycleOwner) {
            bindDataWithView()
            recyclerAdapter.setItems(it.listPhotos)
        }

        // Observador de cambios en Progress Bar
        viewModel.progressBarView.observe(viewLifecycleOwner) { trueOrFalse ->
            binding.progressBar.isVisible = trueOrFalse
        }

        // Observador de cambios en Error producido al conectarse con la API
        viewModel.errorConnection.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse) dialogErrorConnectApi()
        }

        // Btn Edit
        binding.btnEditDivelog.setOnClickListener {
            enableEditingDivelog()
        }

        // Btn Cancel
        binding.btnCancelEditingDivelog.setOnClickListener {
            if (viewModel.editMode.value == true)
                dialogCancelEditingDivelog()
        }

        // Btn Save
        binding.btnSaveDivelog.setOnClickListener {
            viewModel.setEditMode(false)
            disableEditingDivelog()
        }

        // Btn Add Divelog
        binding.btnAddDivelog.setOnClickListener {
            if (viewModel.editMode.value == true) {
                // Avisa de que hay datos sin guardar y pregunta si desea continuar
                dialogUnsavedData()
            } else {
                navController.navigate(R.id.action_divelogDetails_to_divelogAdd)
            }
        }
    }

    // Cancela la solicictud de descarga de datos desde la API al cambiar a otro fragmento
    override fun onDetach() {
        super.onDetach()
        viewModel.cancelGetDivelogById()
    }

    // Resetea los datos del Divelog cargado en memoria al destruir el Fragmento
    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetDivelog()
    }

    private fun enableEditingDivelog() {
        // Activa el modo de Edicion
        viewModel.setEditMode(true)

        // Visualiza todos los Layouts que contienen datos
        showAllDataLayout()

        // Habilita todos los EditText del listado
        listEditText.forEach {
            it.isEnabled = true
        }

        // Visualizacion de Botones
        binding.btnSaveDivelog.visibility = View.VISIBLE
        binding.btnCancelEditingDivelog.visibility = View.VISIBLE
        binding.btnEditDivelog.visibility = View.GONE
    }

    private fun disableEditingDivelog() {
        // Deshabilita los EditText del listado
        listEditText.forEach {
            it.isEnabled = false
            it.setTextColor(resources.getColor(R.color.black))
        }

        // Visualizacion de Botones
        binding.btnSaveDivelog.visibility = View.GONE
        binding.btnCancelEditingDivelog.visibility = View.GONE
        binding.btnEditDivelog.visibility = View.VISIBLE
    }

    private fun setStyleEditText() {
        // Asigna un Estilo a los EditText del listado
        listEditText.forEach {
            if (it != binding.etNotes) {
                it.setBackgroundResource(R.drawable.border_background_custom)
            } else {
                it.setBackgroundResource(R.drawable.edit_text_notes_custom)
            }

            // Longitud maxima de caracteres del EditText
            it.maxLength(7)
        }

        // Configura el limite de caracteres según el Limite de BBDD
        setMaxLengthEditText()
    }

    // Función de extension para poner un maximo de carácteres
    private fun EditText.maxLength(maxLength: Int) {
        filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    private fun setMaxLengthEditText() {
        // Configura el limite de caracteres según el Limite de BBDD
        // TODO(): Crear un fichero con todas las longitudes de los campos (variables) para tener los datos centralizados
        binding.etBuddyName.maxLength(35)
        binding.etDivingCenter.maxLength(45)
        binding.etSite.maxLength(35)
        binding.etLocation.maxLength(35)
        binding.etCountry.maxLength(35)
        binding.etNotes.maxLength(255)
        binding.etDate.maxLength(35)
    }

    private fun getListOfEditTextViews(): List<EditText> {
        return listOf(
            binding.etDiveNumber,
            binding.etMaxDepth,
            binding.etAvgDepth,
            binding.etDiveLength,
            binding.etTemp,
            binding.etGasConsum,
            binding.etDecoTime,
            binding.etBuddyName,
            binding.etLocation,
            binding.etSite,
            binding.etDate,
            binding.etCountry,
            binding.etDivingCenter,
            binding.etNotes
        )
    }

    private fun showAllDataLayout() {
        binding.DiveNumberLinearLayout.visibility = View.VISIBLE
        binding.MaxDepthLinearLayout.visibility = View.VISIBLE
        binding.AvgDepthLinearLayout.visibility = View.VISIBLE
        binding.DiveLengthLinearLayout.visibility = View.VISIBLE
        binding.TempLinearLayout.visibility = View.VISIBLE
        binding.GasConsumLinearLayout.visibility = View.VISIBLE
        binding.DecoTimeLinearLayout.visibility = View.VISIBLE
        binding.BuddyNameLinearLayout.visibility = View.VISIBLE
        binding.LocationLinearLayout.visibility = View.VISIBLE
        binding.SiteLinearLayout.visibility = View.VISIBLE
        binding.DateLinearLayout.visibility = View.VISIBLE
        binding.CountryLinearLayout.visibility = View.VISIBLE
        binding.DivingCenterLinearLayout.visibility = View.VISIBLE
        binding.NotesLinearLayout.visibility = View.VISIBLE

        // TODO(): En caso de que no hayan foto cargadas, mostrar un boton que permita añadir fotos
        // Si hay fotos, mostratar el recycler view
    }

    // Bindea los datos de Divelog con la Vista, ocultando los campos vacios
    private fun bindDataWithView() {
        viewModel.divelog.value?.let {
            if (it.idDivelog > 0) {
                binding.fragmentDataLayoutDivelogDetails.visibility = View.VISIBLE

                if (it.numDive > 0) {
                    binding.etDiveNumber.setText(it.numDive.toString())
                } else {
                    binding.DiveNumberLinearLayout.visibility = View.GONE
                }

                if (it.maxDepth > 0) {
                    binding.etMaxDepth.setText(it.maxDepth.toString())
                } else {
                    binding.MaxDepthLinearLayout.visibility = View.GONE
                }

                if (it.avgDepth > 0) {
                    binding.etAvgDepth.setText(it.avgDepth.toString())
                } else {
                    binding.AvgDepthLinearLayout.visibility = View.GONE
                }

                if (it.diveLength > 0) {
                    binding.etDiveLength.setText(it.diveLength.toString())
                } else {
                    binding.DiveLengthLinearLayout.visibility = View.GONE
                }

                if (it.temperature > 0) {
                    binding.etTemp.setText(it.temperature.toString())
                } else {
                    binding.TempLinearLayout.visibility = View.GONE
                }

                if (it.gasConsumption > 0) {
                    binding.etGasConsum.setText(it.gasConsumption.toString())
                } else {
                    binding.GasConsumLinearLayout.visibility = View.GONE
                }

                if (it.decoTime > 0) {
                    binding.etDecoTime.setText(it.decoTime.toString())
                } else {
                    binding.DecoTimeLinearLayout.visibility = View.GONE
                }

                if (it.buddyName.isNotEmpty()) {
                    binding.etBuddyName.setText(it.buddyName)
                } else {
                    binding.BuddyNameLinearLayout.visibility = View.GONE
                }

                if (it.location.isNotEmpty()) {
                    binding.etLocation.setText(it.location)
                } else {
                    binding.LocationLinearLayout.visibility = View.GONE
                }

                if (it.site.isNotEmpty()) {
                    binding.etSite.setText(it.site)
                } else {
                    binding.SiteLinearLayout.visibility = View.GONE
                }

                if (it.startDateTime.toString().isNotEmpty()) {
                    binding.etDate.setText(it.startDateTime.toString())
                } else {
                    binding.DateLinearLayout.visibility = View.GONE
                }

                if (it.country.isNotEmpty()) {
                    binding.etCountry.setText(it.country)
                } else {
                    binding.CountryLinearLayout.visibility = View.GONE
                }

                if (it.divingCenter.isNotEmpty()) {
                    binding.etDivingCenter.setText(it.divingCenter)
                } else {
                    binding.DivingCenterLinearLayout.visibility = View.GONE
                }

                if (it.notes.isNotEmpty()) {
                    binding.etNotes.setText(it.notes)
                } else {
                    binding.NotesLinearLayout.visibility = View.GONE
                }

                if (it.listPhotos.isNotEmpty()) {
                    binding.recyclerPhotosDivelog.visibility = View.VISIBLE
                } else {
                    binding.recyclerPhotosDivelog.visibility = View.GONE
                }

            } else {
                binding.fragmentDataLayoutDivelogDetails.visibility = View.GONE
            }
        }
    }

    // Guarda los datos del formulario en ViewModel
    private fun saveDateWithView(){
        val d = DivelogModel()

        d.numDive = binding.etDiveNumber.text.toString().toInt()
        d.maxDepth = binding.etMaxDepth.text.toString().toDouble()
        d.avgDepth = binding.etAvgDepth.text.toString().toDouble()
        d.diveLength = binding.etDiveLength.text.toString().toInt()
        d.temperature = binding.etTemp.text.toString().toDouble()
        d.gasConsumption = binding.etGasConsum.text.toString().toDouble()
        d.decoTime = binding.etDecoTime.text.toString().toInt()
        d.buddyName = binding.etBuddyName.text.toString()
        d.location = binding.etLocation.text.toString()
        d.site = binding.etSite.text.toString()
        d.country = binding.etCountry.text.toString()
        d.divingCenter = binding.etDivingCenter.text.toString()
        d.notes = binding.etNotes.text.toString()

        // Date -> Tue Apr 20 09:15:00 GMT 2
        // TODO(): Recuperar la fecha desde ViewModel -> Para editar la fecha usar la herramienta de android
        val date = binding.etDate.text.toString()

        val formatter = Date()

    }

    //Dialgo de Error de Conexion con el Servidor
    private fun dialogErrorConnectApi() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.connection_error_title)
            .setMessage(R.string.connection_error_message)
            .setPositiveButton(R.string.cancel) { _, _ -> requireActivity().onBackPressed() }
            .setNeutralButton(R.string.reconnect) { _, _ ->
                viewModel.downloadDivelogById(idDivelog)
            }
            .setCancelable(false)
            .show()
    }

    // Dialogo Cancelar el Modo de Edicion
    private fun dialogCancelEditingDivelog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancelar Edicion del Divelog")
            .setMessage("¿Deseas deshacer los cambios del Divelog?")
            .setPositiveButton(R.string.yes) { _, _ ->
                // Deshabilita la edicion
                disableEditingDivelog()
                viewModel.setEditMode(false)
                // Vuelve a cargar los datos originales
                bindDataWithView()
            }
            .setNegativeButton(R.string.no, null)
            .setCancelable(false)
            .show()
    }

    // Dialogo Cancelar el Modo de Edicion
    private fun dialogUnsavedData() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("No ha guardado los cambios")
            .setMessage("¡Si continua, pude perder los datos! ¿Desea continuar?")
            .setPositiveButton(R.string.yes) { _, _ -> navController.navigate(R.id.action_divelogDetails_to_divelogAdd) }
            .setNegativeButton(R.string.no) { _, _ -> Unit }
            .setCancelable(true)
            .show()
    }

    // Muestra la foto seleccionada en un Dialogo
    private fun dialogShowPhoto(photo: Bitmap) {
        val inflater: LayoutInflater = this.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_show_photo_simple, null)

        view.findViewById<ImageView>(R.id.iv_Photo).setImageBitmap(photo)

        MaterialAlertDialogBuilder(requireContext())
            .setCancelable(true)
            .setView(view)
            .setPositiveButton(getString(R.string.close)) { _, _ -> Unit }
            .show()
    }

    // Muestra la foto seleccionada en un Dialogo con opcion de eliminarla o sustituirla por otra
    private fun dialogShowEditablePhoto(photo: Bitmap) {
        val inflater: LayoutInflater = this.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_show_photo_simple, null)

        view.findViewById<ImageView>(R.id.iv_Photo).setImageBitmap(photo)

        MaterialAlertDialogBuilder(requireContext())
            .setCancelable(true)
            .setView(view)
            .setNeutralButton(R.string.delete) { _, _ ->
                viewModel.removePhoto(photo)
                viewModel.divelog.value?.let{
                    recyclerAdapter.setItems(it.listPhotos)
                }
            }
            .setPositiveButton(R.string.replace) { _, _ -> Unit }
            .setNegativeButton(getString(R.string.close)) { _, _ -> Unit }
            .show()
    }


    override fun itemSelect(photo: Bitmap) {
        if (viewModel.editMode.value == true)
            dialogShowEditablePhoto(photo)
        else
            dialogShowPhoto(photo)
    }
}

/*
*************************** ADAPTER - Data List***************************************
* */
class PhotosDiveLogAdapter(private val listener: ClickListenerPhotosDivelog) :
    RecyclerView.Adapter<PhotosDiveLogHolder>() {
    private val itemList = mutableListOf<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosDiveLogHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.photo_item_view, parent, false)
        return PhotosDiveLogHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotosDiveLogHolder, position: Int) {
        val item: Bitmap = itemList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            // Metodo sobreescrito que está en la clase 'DiveLogRecycler'
            listener.itemSelect(itemList[position])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItems(list: MutableList<Bitmap>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }
}

/*
*************************** HOLDER - Data List***************************************
* */
class PhotosDiveLogHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val ivPhoto: ImageView = v.findViewById(R.id.imageViewPhoto)

    fun bind(photo: Bitmap) {
        ivPhoto.setImageBitmap(photo)
    }
}