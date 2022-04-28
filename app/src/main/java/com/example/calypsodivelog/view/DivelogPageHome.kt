package com.example.calypsodivelog.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.calypsodivelog.databinding.StartPageHomeBinding

class StartPageHome: Fragment(){
    private lateinit var binding: StartPageHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = StartPageHomeBinding.inflate(inflater)

        return binding.root
    }

}

