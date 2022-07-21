package com.example.calypsodivelog.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.calypsodivelog.R
import com.example.calypsodivelog.databinding.EquipmentDetailsBinding
import com.example.calypsodivelog.model.EquipmentResponse
import com.example.calypsodivelog.service.DateUtil
import com.example.calypsodivelog.viewmodel.EquipmentRecyclerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class EquipmentDetails : Fragment() {
    private lateinit var binding: EquipmentDetailsBinding
    private lateinit var viewModel: EquipmentRecyclerViewModel
    private lateinit var navController: NavController
    private var listEditText: List<EditText> = emptyList()
    private var listRequiredMarkersTV: List<TextView> = emptyList()
    private var equipmentEdited = EquipmentResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.let {
            ViewModelProvider(it)[EquipmentRecyclerViewModel::class.java]
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = EquipmentDetailsBinding.inflate(inflater)

        // Instancia de Controlador de Navegacion
        navController = NavHostFragment.findNavController(this)

        // Crea el listado de los controles EditText de la vista
        listEditText = getListOfEditTextViews()
        listRequiredMarkersTV = getListOfRequiredDataTVMarkers()

        // Configura el Estilo de los EditText
        setStyleEditText()

        // Deshabilita la Edicion
        disableEditingEquipment()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observador de actualización de datos cargados
        viewModel.itemSelected.observe(viewLifecycleOwner) {
            bindDataWithView()
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

        viewModel.addMode.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse) {
                activeAddEquipmentMode()
            }
        }

        viewModel.postDoneSuccessfully.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse) {
                Toast.makeText(
                    requireContext(), "Equipamiento añadido con exito", Toast.LENGTH_LONG
                ).show()

                // Vuelve al fragmento anterior actualizando los datos y reseteando los estados
                viewModel.downloadEquipmentList()
                viewModel.resetStatusProperties()
                requireActivity().onBackPressed()
            }
        }

        viewModel.postError.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse)
                Toast.makeText(
                    requireContext(),
                    "Error al guardar el Equipamiento en el Servidor",
                    Toast.LENGTH_SHORT
                ).show()
        }

        viewModel.putDoneSuccessfully.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse) {
                Toast.makeText(
                    requireContext(), "Equipamiento editado con exito", Toast.LENGTH_LONG
                ).show()

                // Vuelve al fragmento anterior actualizando los datos y reseteando los estados
                viewModel.downloadEquipmentList()
                viewModel.resetStatusProperties()
                requireActivity().onBackPressed()

            }
        }

        viewModel.putError.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse)
                Toast.makeText(
                    requireContext(),
                    "Error al editar Equipamiento en el Servidor",
                    Toast.LENGTH_SHORT
                ).show()
        }

        viewModel.deleteDoneSuccessfully.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse) {
                Toast.makeText(
                    requireContext(), "Equipamiento eliminado con exito", Toast.LENGTH_LONG
                ).show()

                // Vuelve al fragmento anterior actualizando los datos y reseteando los estados
                viewModel.downloadEquipmentList()
                viewModel.resetStatusProperties()
                requireActivity().onBackPressed()
            }
        }

        viewModel.deleteError.observe(viewLifecycleOwner) { trueOrFalse ->
            if (trueOrFalse)
                Toast.makeText(
                    requireContext(),
                    "Error al eliminar Equipamiento del Servidor",
                    Toast.LENGTH_SHORT
                ).show()
        }

        binding.btnAddEquipment.setOnClickListener {
            // TODO(): Navegar al fragmento de AddEquipment o ejecutar la logica para tranformar el layout actual
            if (viewModel.editMode.value == true)
                dialogUnsavedData()
            else {
                activeAddEquipmentMode()
            }
        }

        binding.btnDatePickerPurchase.setOnClickListener {
            showDatePickerPurchase()
        }

        binding.btnDeleteDatePurchase.setOnClickListener {
            binding.tvDatePurchase.text = ""
        }

        binding.btnDatePickerLastCheck.setOnClickListener {
            showDatePickerLastCheck()
        }

        binding.btnDeleteDateLastCheck.setOnClickListener {
            binding.tvDateLastCheck.text = ""
        }

        binding.btnRemoveEquipment.setOnClickListener {
            dialogDeleteEquipment()
        }

        binding.btnEditEquipment.setOnClickListener {
            enableEditingEquipment()
        }

        binding.btnCancelEditingEquipment.setOnClickListener {
            if (viewModel.editMode.value == true || viewModel.addMode.value == true)
                dialogCancelEditing()
        }

        binding.btnSaveEquipment.setOnClickListener {
            if (viewModel.addMode.value == true) {
                postEquipmentApiRest()
            } else {
                putEquipmentApiRest()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetStatusProperties()
        viewModel.resetItemSelected()
    }

    private fun enableEditingEquipment() {
        // Activa el modo de Edicion
        viewModel.setEditMode(true)

        // Muestra todos los Layouts ocultos
        showAllDataLayout(true)

        // Habilita todos los EditText del listado
        listEditText.forEach {
            it.isEnabled = true
        }

        // Muestra los marcadores que enseñan los campos obligatorios
        listRequiredMarkersTV.forEach {
            it.visibility = View.VISIBLE
        }

        // Visualizacion de Botones
        binding.btnSaveEquipment.visibility = View.VISIBLE
        binding.btnCancelEditingEquipment.visibility = View.VISIBLE
        binding.btnEditEquipment.visibility = View.GONE
        binding.btnRemoveEquipment.visibility = View.GONE
        binding.btnDatePickerLastCheck.visibility = View.VISIBLE
        binding.btnDatePickerPurchase.visibility = View.VISIBLE
        binding.btnDeleteDatePurchase.visibility = View.VISIBLE
        binding.btnDeleteDateLastCheck.visibility = View.VISIBLE
    }

    private fun disableEditingEquipment() {
        // Deshabilita los EditText del listado
        listEditText.forEach {
            it.isEnabled = false
            it.setTextColor(resources.getColor(R.color.black))
        }

        // Oculta los marcadores que enseñan los campos obligatorios
        listRequiredMarkersTV.forEach {
            it.visibility = View.GONE
        }

        // Visualizacion de Botones
        binding.btnSaveEquipment.visibility = View.GONE
        binding.btnCancelEditingEquipment.visibility = View.GONE
        binding.btnEditEquipment.visibility = View.VISIBLE
        binding.btnRemoveEquipment.visibility = View.VISIBLE
        binding.btnDatePickerPurchase.visibility = View.GONE
        binding.btnDatePickerLastCheck.visibility = View.GONE
        binding.btnDeleteDatePurchase.visibility = View.GONE
        binding.btnDeleteDateLastCheck.visibility = View.GONE
    }

    private fun resetAllEditText() {
        listEditText.forEach {
            it.setText("")
        }

        // En modo Add -> Resetea TextView que continen fechas
        if (viewModel.addMode.value == true) {
            binding.tvDateLastCheck.text = ""
            binding.tvDatePurchase.text = ""
        }
    }

    private fun setStyleEditText() {
        // Asigna un Estilo a los EditText del listado
        listEditText.forEach {
            it.setBackgroundResource(R.drawable.border_background_custom)

            // Longitud maxima de caracteres del EditText
            it.maxLength(5)
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
        binding.etItemName.maxLength(25)
        binding.etItemType.maxLength(15)
    }

    private fun getListOfEditTextViews(): List<EditText> {
        return listOf(
            binding.etItemName,
            binding.etItemType,
            binding.etRecCheckMonth,
            binding.etRecCheckHours,
            binding.etRecCheckDives
        )
    }

    private fun getListOfRequiredDataTVMarkers(): List<TextView> {
        return listOf(
            binding.tvRequiredData1,
            binding.tvRequiredData2,
            binding.tvRequiredData3
        )
    }

    private fun showAllDataLayout(onlyEditable: Boolean) {
        binding.fragmentDataLayoutEquipmentDetails.visibility = View.VISIBLE

        binding.nameItemLinearLayout.visibility = View.VISIBLE
        binding.itemTypeLinearLayout.visibility = View.VISIBLE
        binding.datePurchaseLinearLayout.visibility = View.VISIBLE
        binding.lastCheckDateLinearLayout.visibility = View.VISIBLE
        binding.recCheckMonthLinearLayout.visibility = View.VISIBLE
        binding.recCheckHourLinearLayout.visibility = View.VISIBLE
        binding.recCheckDivesLinearLayout.visibility = View.VISIBLE

        // Se ejecuta si onlyEditable == false
        if (!onlyEditable) {
            binding.nextCheckDateLinearLayout.visibility = View.VISIBLE
            binding.usedDivesLinearLayout.visibility = View.VISIBLE
            binding.usedDivesAfterCheckLinearLayout.visibility = View.VISIBLE
            binding.usedHoursLinearLayout.visibility = View.VISIBLE
            binding.usedHoursAfterCheckLinearLayout.visibility = View.VISIBLE
        } else {
            binding.nextCheckDateLinearLayout.visibility = View.GONE
            binding.usedDivesLinearLayout.visibility = View.GONE
            binding.usedDivesAfterCheckLinearLayout.visibility = View.GONE
            binding.usedHoursLinearLayout.visibility = View.GONE
            binding.usedHoursAfterCheckLinearLayout.visibility = View.GONE
        }
    }

    // Bindea los datos con la Vista, ocultando los campos vacios
    private fun bindDataWithView() {
        viewModel.itemSelected.value?.let {
            if (it.idEquipment > 0 || viewModel.addMode.value == true) {
                binding.fragmentDataLayoutEquipmentDetails.visibility = View.VISIBLE

                if (it.name.isNotEmpty()) {
                    binding.etItemName.setText(it.name)
                    binding.nameItemLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.nameItemLinearLayout.visibility = View.GONE
                }

                if (it.type.isNotEmpty()) {
                    binding.etItemType.setText(it.type)
                    binding.itemTypeLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.itemTypeLinearLayout.visibility = View.GONE
                }

                if (it.datePurchase != null) {
                    binding.tvDatePurchase.text = DateUtil.getDateString(it.datePurchase!!)
                    binding.datePurchaseLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.datePurchaseLinearLayout.visibility = View.GONE
                }

                if (it.lastCheckDate != null) {
                    binding.tvDateLastCheck.text = DateUtil.getDateString(it.lastCheckDate!!)
                    binding.lastCheckDateLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.lastCheckDateLinearLayout.visibility = View.GONE
                }

                if (it.getNextCkeckDate() != null) {
                    binding.tvDateNextCheck.text =
                        DateUtil.getDateString(it.getNextCkeckDate()!!)
                    binding.nextCheckDateLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.nextCheckDateLinearLayout.visibility = View.GONE
                }

                if (it.checkRecommendedMonths > 0) {
                    binding.etRecCheckMonth.setText(it.checkRecommendedMonths.toString())
                    binding.recCheckMonthLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.recCheckMonthLinearLayout.visibility = View.GONE
                }

                if (it.checkRecommendedHours > 0) {
                    binding.etRecCheckHours.setText(it.checkRecommendedHours.toString())
                    binding.recCheckHourLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.recCheckHourLinearLayout.visibility = View.GONE
                }

                if (it.checkRecommendedDives > 0) {
                    binding.etRecCheckDives.setText(it.checkRecommendedDives.toString())
                    binding.recCheckDivesLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.recCheckDivesLinearLayout.visibility = View.GONE
                }

                if (it.usedDives > 0) {
                    binding.tvUsedInDives.text = it.usedDives.toString()
                    binding.usedDivesLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.usedDivesLinearLayout.visibility = View.GONE
                }

                if (it.usedDivesAfterCheck > 0) {
                    binding.tvUsedInDivesAfterLastCheck.text = it.usedDivesAfterCheck.toString()
                    binding.usedDivesAfterCheckLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.usedDivesAfterCheckLinearLayout.visibility = View.GONE
                }

                if (it.usedHours > 0) {
                    binding.tvUsedHours.text = it.usedHours.toString()
                    binding.usedHoursLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.usedHoursLinearLayout.visibility = View.GONE
                }

                if (it.usedHoursAfterCheck > 0) {
                    binding.tvUsedHoursAfterLastCheck.text = it.usedHoursAfterCheck.toString()
                    binding.usedHoursAfterCheckLinearLayout.visibility = View.VISIBLE
                } else {
                    binding.usedHoursAfterCheckLinearLayout.visibility = View.GONE
                }
            } else {
                binding.fragmentDataLayoutEquipmentDetails.visibility = View.GONE
            }
        }

        if (viewModel.addMode.value == true || viewModel.editMode.value == true) {
            showAllDataLayout(true)
        }
    }

    private fun showDatePickerPurchase() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { view, y, m, d ->
                viewModel.itemSelected.value?.datePurchase = Date(y - 1900, m, d)
                bindDataWithView()
            },
            year, month, day
        ).show()
    }

    private fun showDatePickerLastCheck() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { view, y, m, d ->
                viewModel.itemSelected.value?.lastCheckDate = Date(y - 1900, m, d)
                bindDataWithView()
            },
            year, month, day
        ).show()
    }

    // Bindea los datos de la Vista con el Divelog
    private fun bindDataFromView(): Boolean {
        var minimumDataCheked = true
        equipmentEdited = EquipmentResponse()

        if (viewModel.addMode.value == false) {
            if (viewModel.itemSelected.value != null) {
                equipmentEdited.idEquipment = viewModel.itemSelected.value!!.idEquipment
                equipmentEdited.idUser = viewModel.itemSelected.value!!.idUser
            } else {
                minimumDataCheked = false
            }
        }

        if (binding.etItemName.text.isNotEmpty()) {
            equipmentEdited.name = binding.etItemName.text.toString()
        } else {
            minimumDataCheked = false
        }

        if (binding.etItemType.text.isNotEmpty()) {
            equipmentEdited.type = binding.etItemType.text.toString()
        } else {
            minimumDataCheked = false
        }

        if (binding.tvDatePurchase.text.isNotEmpty()) {
            equipmentEdited.datePurchase = viewModel.itemSelected.value?.datePurchase
        }

        if (binding.tvDateLastCheck.text.isNotEmpty()) {
            equipmentEdited.lastCheckDate = viewModel.itemSelected.value?.lastCheckDate
        }

        if (binding.etRecCheckMonth.text.isNotEmpty()) {
            equipmentEdited.checkRecommendedMonths = binding.etRecCheckMonth.text.toString().toInt()
        }

        if (binding.etRecCheckHours.text.isNotEmpty()) {
            equipmentEdited.checkRecommendedHours = binding.etRecCheckHours.text.toString().toInt()
        }

        if (binding.etRecCheckDives.text.isNotEmpty()) {
            equipmentEdited.checkRecommendedDives = binding.etRecCheckDives.text.toString().toInt()
        }

        return minimumDataCheked
    }

    private fun putEquipmentApiRest() {
        // Bindea los datos de la vista con ViewModel y ejecuta el metodo POST
        // bindDataWithView() -> Devuelve FALSE si no se introducen los datos minimos
        if (bindDataFromView()) {
            // Deshabilita el modo de Edicion
            viewModel.setEditMode(false)
            disableEditingEquipment()

            // Carga datos en ViewModel
            viewModel.setItemSelected(equipmentEdited)

            if (viewModel.itemSelected.value == equipmentEdited) {
                // Envia los datos de ViewModel al Servidor
                viewModel.putEquipment()
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

    private fun postEquipmentApiRest() {
        // Bindea los datos de la vista con ViewModel y ejecuta el metodo POST
        // bindDataWithView() -> Devuelve FALSE si no se introducen los datos minimos
        if (bindDataFromView()) {
            // Carga datos en ViewModel
            viewModel.setItemSelected(equipmentEdited)

            if (viewModel.itemSelected.value == equipmentEdited) {
                // Envia los datos de ViewModel al Servidor
                viewModel.postEquipment()
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

    private fun dialogMinDataMissing() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Campos vacíos")
            .setMessage("Necesita introducir los datos obligatorios para poder guardar la inmersión")
            .setPositiveButton(R.string.ok) { _, _ -> Unit }
            .setCancelable(false)
            .show()
    }

    private fun dialogErrorConnectApi() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.connection_error_title)
            .setMessage(R.string.connection_error_message)
            .setPositiveButton(R.string.cancel, null)
            .setNeutralButton(R.string.reconnect) { _, _ -> viewModel.downloadEquipmentList() }
            .setCancelable(false)
            .show()
    }

    private fun dialogDeleteEquipment() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar la pieza del equipo?")
            .setMessage("Si elimina la pieza, perdera las estadisticás y sera irreversible. ¿Deseas eliminar la pieza del equipo?")
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.deleteEquipment() }
            .setNegativeButton(R.string.no, null)
            .setCancelable(false)
            .show()
    }

    // TODO(): Mostrar el mensaje y titulo segun el estado de addMode (true or false)
    private fun dialogCancelEditing() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Cancelar la edición?")
            .setMessage("¿Deseas deshacer los cambios")
            .setPositiveButton(R.string.yes) { _, _ ->
                if (viewModel.addMode.value == true) {
                    activity?.onBackPressed()
                } else {
                    // Deshabilita la edicion
                    disableEditingEquipment()
                    viewModel.setEditMode(false)
                    resetAllEditText()
                    // Vuelve a cargar los datos originales
                    bindDataWithView()
                }
            }
            .setNegativeButton(R.string.no, null)
            .setCancelable(false)
            .show()
    }

    // Dialogo Cancelar el Modo de Edicion
    private fun dialogUnsavedData() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("No ha guardado los cambios")
            .setMessage("¡Si continua, perdera los datos sin guardar! ¿Desea continuar?")
            .setPositiveButton(R.string.yes) { _, _ -> activeAddEquipmentMode() }
            .setNegativeButton(R.string.no) { _, _ -> Unit }
            .setCancelable(true)
            .show()
    }

    private fun activeAddEquipmentMode() {
        if (viewModel.addMode.value == false) {
            viewModel.setAddMode(true)
        } else {
            viewModel.resetItemSelected()
            viewModel.itemSelected.value?.name = ""
            resetAllEditText()
            enableEditingEquipment()

            binding.btnAddEquipment.isEnabled = false
            binding.btnAddEquipment.setImageDrawable(resources.getDrawable(R.drawable.ic_menu_add_disable))
        }
    }

}