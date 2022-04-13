package com.example.calypsodivelog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.calypsodivelog.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : FragmentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationMenuView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        bottomNavigationMenuView = findViewById(R.id.bottom_navigation)
        navController = findNavController(R.id.fragment_container)
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

}