package com.example.calypsodivelog.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.calypsodivelog.R
import com.example.calypsodivelog.databinding.CreateAccountBinding

class CreateNewAccount : Fragment() {
    private lateinit var binding: CreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateAccountBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateAccountBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // Navigation Controller
        val navController = NavHostFragment.findNavController(this)

        binding.btnCreateAccount.setOnClickListener{
            navController.navigate(R.id.action_create_account_to_login)
        }

        return rootView
    }


}