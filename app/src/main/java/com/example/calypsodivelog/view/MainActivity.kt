package com.example.calypsodivelog

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.calypsodivelog.databinding.ActivityMainBinding
import com.example.calypsodivelog.viewmodel.DiveLogViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Exception

class MainActivity : FragmentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationMenuView: BottomNavigationView
    private lateinit var navController: NavController
    lateinit var viewModel: DiveLogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(DiveLogViewModel::class.java)

        bottomNavigationMenuView = findViewById(R.id.bottom_navigation)
        navController = findNavController(R.id.main_activity_container)
        // Vincula el Menu con los fragmentos del bloque de Navegacion
        // El id del item del menu tiene que ser el mismo que el id del fragment
        bottomNavigationMenuView.setupWithNavController(navController)

        // Oculta el Menu de Navegacion en las pantallas de Login y Registro
        configVisibilityBottomMenu()
    }

    // Oculta el menu en los fragmentos indicados
    private fun configVisibilityBottomMenu() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.login -> hideMenu()
                R.id.createAccount -> hideMenu()
                else -> showMenu()
            }
        }
    }

    private fun hideMenu(){
        bottomNavigationMenuView.visibility = View.GONE
    }

    private fun showMenu(){
        bottomNavigationMenuView.visibility = View.VISIBLE
    }

    // Metodo sobreescrito del Boton Atras
    override fun onBackPressed() {
       if((navController.currentDestination?.displayName ?: "null").contains("createAccount")) {
           // Si esta cargado el Fragment CreateNewAccount, mostrara el fragment de Login
           // En caso contrario, se ejecutara la pila de actiividades.
           // Por defecto, se exluyen de la pila los fragmentos de Login y de CreateNewAccount
           navController.navigate(R.id.action_createAccount_to_login)
       }else if (isDivelogDetailsFragmentShown()) {
           // Resetea el item seleccionado para evitar que el metodo 'showLastFragment()'
               // de 'DivelogRecycler', vuelva a redireccionar hacia el fragmento de 'DivelogDetails'
           super.onBackPressed()
           viewModel.resetItemSelected()
       }else {
           super.onBackPressed()
       }
    }

    // Compruba si el fragmento actual es DivelogDetails
    private fun isDivelogDetailsFragmentShown() : Boolean{
        try{
            return (findNavController(R.id.start_page_container).currentDestination?.displayName ?: "null")
                .contains("divelogDetails")
        }catch (e: Exception){}

        return false
    }
}