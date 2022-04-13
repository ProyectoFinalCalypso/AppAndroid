package com.example.calypsodivelog.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.calypsodivelog.R
import com.example.calypsodivelog.databinding.LoginBinding


class Login : Fragment() {
    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val navController = NavHostFragment.findNavController(this)

        binding.btnLogin.setOnClickListener(){
            if(checkLogin()){
                // Login OK > Mostrar Datos
                navController.navigate(R.id.action_login_to_diveLogHome)
            }else{
                Toast.makeText(requireContext(),
                    "¡Usuario o Contraseña incorrectos!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener(){
            // Navegacion a pantalla de registro
            navController.navigate(R.id.action_login_to_create_account)
        }

        binding.btnRecoveryPassword.setOnClickListener(){
            // Formulario recuperacion contraseña
        }

        return rootView
    }

    private fun checkLogin(): Boolean{
        val name: String = binding.editTextUsername.text.toString()
        val pass: String = binding.editTextPassword.text.toString()

        //return name == "alexv" && pass == "pass"
        return true
    }
}