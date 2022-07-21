package com.example.calypsodivelog.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.calypsodivelog.R
import com.example.calypsodivelog.databinding.AccountCreateBinding

class CreateNewAccount : Fragment() {
    private lateinit var binding: AccountCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountCreateBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // Navigation Controller
        val navController = NavHostFragment.findNavController(this)

        binding.btnCreateAccount.setOnClickListener{
            navController.navigate(R.id.action_create_account_to_login)
        }

        return rootView
    }


}