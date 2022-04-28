package com.example.calypsodivelog.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.calypsodivelog.R
import com.example.calypsodivelog.databinding.DivelogAddBinding
import com.example.calypsodivelog.model.DivelogFullResponse
import com.example.calypsodivelog.model.DivelogModel
import com.example.calypsodivelog.viewmodel.DivelogAddViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.util.*
import android.graphics.drawable.ColorDrawable

import android.widget.TimePicker

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.graphics.Color
import com.example.calypsodivelog.model.DateTimeModel
import java.text.SimpleDateFormat
import javax.xml.datatype.DatatypeConstants.MONTHS


class DivelogAdd : Fragment() {
    private lateinit var viewModel: DivelogAddViewModel
    private lateinit var binding: DivelogAddBinding
    private lateinit var navController: NavController
    private var divelogLocal = DivelogModel()
    private var listEditText: List<EditText> = emptyList()

    // TODO(): Antes de hacer POST, comprobar eque el Divelog no este vacio y que tenga los datos minimos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            activity?.let {
                ViewModelProvider(it)[DivelogAddViewModel::class.java]
            }!!
        // Toma la fecha actual para mostrarla por defecto
        viewModel.dateTime.value = DateTimeModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DivelogAddBinding.inflate(inflater)

        // Instancia de Controlador de Navegacion
        navController = NavHostFragment.findNavController(this)

        // Crea el listado de los controles EditText de la vista
        listEditText = getListOfEditTextViews()

        // Configura el Estilo de los EditText
        setStyleEditText()

        // Muestra la fecha actual en el EditText
        updateDate()

        // Deshabilita el boton de Añadir Divelog
        binding.btnAddDivelog.isEnabled = false
        binding.btnAddDivelog.setImageDrawable(resources.getDrawable(R.drawable.ic_menu_add_disable))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observador de cambios en Progress Bar
        viewModel.progressBarView.observe(viewLifecycleOwner) { trueOrFalse ->
            binding.progressBar.isVisible = trueOrFalse
        }

        // Observador de cambios en Error producido al conectarse con la API
        viewModel.errorConnection.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse) dialogErrorConnectApi()
        }

        viewModel.postDoneSuccessfully.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse) {
                Toast.makeText(
                    requireContext(), "Divelog guardado en el Servidor", Toast.LENGTH_LONG
                ).show()

                // Vuelve al fragmento anterior
                requireActivity().onBackPressed()
            }
        }

        viewModel.postError.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse)
                Toast.makeText(
                    requireContext(), "Error al guardar Divelog en el Servidor", Toast.LENGTH_SHORT
                ).show()
        }

        binding.btnSaveDivelogPost.setOnClickListener {
            // Bindea los datos de la vista con ViewModel y ejecuta el metodo POST
            // bindDataWithView() -> Devuelve FALSE si no se introducen los datos minimos
            if (bindDataWithView()) {
                // Carga datos en ViewModel
                viewModel.setDivelog(divelogLocal)

                if (viewModel.divelog.value == divelogLocal) {
                    // Envia los datos de ViewModel al Servidor
                    viewModel.uploadDivelogToServer()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error local al guardar los datos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // TODO(MEJORA): Marcar los campos obligatorios que esten vacios
                dialogMinDataMissing()
            }
        }

        binding.btnCancelDivelogPost.setOnClickListener {
            Toast.makeText(requireContext(), Date().toString(), Toast.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), viewModel.getDate().toString(), Toast.LENGTH_LONG).show()
        }

        binding.btnDatePicker.setOnClickListener{
            showDatePicker()
        }

        binding.btnTimePicker.setOnClickListener{
            showTimePicker(true)
        }
    }

    // Cancela la solicictud de descarga de datos desde la API al cambiar a otro fragmento
    override fun onDetach() {
        super.onDetach()
        viewModel.cancelUploadDivelogToServer()
    }

    // Resetea los datos del Divelog cargado en memoria al destruir el Fragmento
    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetDivelogAdd()
    }

    private fun setStyleEditText() {
        // Asigna un Estilo a los EditText del listado
        listEditText.forEach {
            if (it != binding.etNotes) {
                it.setBackgroundResource(R.drawable.border_background_custom)
            } else {
                it.setBackgroundResource(R.drawable.edit_text_notes_custom)
            }

            // Longitud maxima de caracteres del EditText por defecto
            it.maxLength(7)
        }

        binding.tvDate.setBackgroundResource(R.drawable.border_background_custom)
        binding.tvTime.setBackgroundResource(R.drawable.border_background_custom)

        // Configura el limite de caracteres según el Limite de BBDD
        setMaxLengthEditText()
    }

    // Función de extension para poner un maximo de carácteres
    private fun EditText.maxLength(maxLength: Int) {
        filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    private fun setMaxLengthEditText() {
        // Configura el limite de caracteres según el Limite de BBDD
        // TODO(MEJORA): Crear un fichero con todas las dimensiones (variables) para tener los datos centralizados
        binding.etBuddyName.maxLength(35)
        binding.etDivingCenter.maxLength(45)
        binding.etSite.maxLength(35)
        binding.etCountry.maxLength(35)
        binding.etLocation.maxLength(35)
        binding.etNotes.maxLength(255)
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
            binding.etCountry,
            binding.etDivingCenter,
            binding.etNotes
        )
    }

    // Bindea los datos de la Vista con el Divelog
    private fun bindDataWithView(): Boolean {
        // TODO(): Las fotos se añadiran directamente al ViewModel en cuanto se pulse el boton AddPhotos, crer una funcion
        var minimumDataCheked = true

        if (binding.etDiveNumber.text.isNotEmpty()) {
            divelogLocal.numDive = binding.etDiveNumber.text.toString().toInt()
        } else {
            minimumDataCheked = false
        }

        if (binding.etMaxDepth.text.isNotEmpty()) {
            divelogLocal.maxDepth = binding.etMaxDepth.text.toString().toDouble()
        } else {
            minimumDataCheked = false
        }

        if (binding.etDiveLength.text.isNotEmpty()) {
            divelogLocal.diveLength = binding.etDiveLength.text.toString().toInt()
        } else {
            minimumDataCheked = false
        }

        /*
        * DateTime Init
        * */
        if (binding.tvDate.text.isNotEmpty()) {
            // La fecha se guarda directamente en ViewModel
        } else {
            minimumDataCheked = false
        }

        if (binding.tvTime.text.isNotEmpty()) {
            // Lo hora se guarda directamente en ViewModel
        } else {
            minimumDataCheked = false
        }

        // Si se ha seleccionado tanto la fecha como la hora, recupera los datos desde el ViewModel
        if(minimumDataCheked){
            divelogLocal.startDateTime = viewModel.getDate()
        }
        /*
        * DateTime Fin
        * */

        if (binding.etAvgDepth.text.isNotEmpty())
            divelogLocal.avgDepth = binding.etAvgDepth.text.toString().toDouble()

        if (binding.etTemp.text.isNotEmpty())
            divelogLocal.temperature = binding.etTemp.text.toString().toDouble()

        if (binding.etGasConsum.text.isNotEmpty())
            divelogLocal.gasConsumption = binding.etGasConsum.text.toString().toDouble()

        if (binding.etDecoTime.text.isNotEmpty())
            divelogLocal.decoTime = binding.etDecoTime.text.toString().toInt()

        if (binding.etBuddyName.text.isNotEmpty())
            divelogLocal.buddyName = binding.etBuddyName.text.toString()

        if (binding.etLocation.text.isNotEmpty())
            divelogLocal.location = binding.etLocation.text.toString()

        if (binding.etSite.text.isNotEmpty())
            divelogLocal.site = binding.etSite.text.toString()

        if (binding.etCountry.text.isNotEmpty())
            divelogLocal.country = binding.etCountry.text.toString()

        if (binding.etDivingCenter.text.isNotEmpty())
            divelogLocal.divingCenter = binding.etDivingCenter.text.toString()

        if (binding.etNotes.text.isNotEmpty())
            divelogLocal.notes = binding.etNotes.text.toString()

        return minimumDataCheked
    }

    private fun updateDate() {
        // Muestra la fecha actual
        val c = viewModel.dateTime.value
        val date = if(c != null) "${c.day}/${c.month}/${c.year}" else ""
        binding.tvDate.setText(date)
    }

    private fun updateTime() {
        // Muestra la fecha actual
        val c = viewModel.dateTime.value
        if(c != null) {
            val h: String = if (c.hour > 9) c.hour.toString() else "0${c.hour.toString()}"
            val m: String = if (c.minute > 9) c.minute.toString() else "0${c.minute.toString()}"
            val time = "$h:$m"
            binding.tvTime.setText(time)
        }
    }

    // Muestra el Dialgo de Error de Conexion con el Servidor
    private fun dialogErrorConnectApi() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.connection_error_title)
            .setMessage(R.string.connection_error_message)
            .setPositiveButton(R.string.cancel) { _, _ -> Unit }
            .setNeutralButton(R.string.reconnect) { _, _ -> viewModel.uploadDivelogToServer() }
            .setCancelable(false)
            .show()
    }

    // Muestra el Dialgo de Error de Conexion con el Servidor
    private fun dialogMinDataMissing() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Campos vacíos")
            .setMessage("Necesita introducir los datos obligatorios para poder guardar la inmersión")
            .setPositiveButton(R.string.ok) { _, _ -> Unit }
            .setCancelable(false)
            .show()
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { view, y, m, d ->
                viewModel.dateTime.value?.year = y
                viewModel.dateTime.value?.month = m + 1
                viewModel.dateTime.value?.day = d
                updateDate()
            },
            year, month, day
        ).show()
    }

    private fun showTimePicker(is24HourView: Boolean) {
        TimePickerDialog(
            requireContext(),
            { view, h, m->
                viewModel.dateTime.value?.hour = h
                viewModel.dateTime.value?.minute = m
                updateTime()
            }, 8, 0, is24HourView
        ).show()
    }

}