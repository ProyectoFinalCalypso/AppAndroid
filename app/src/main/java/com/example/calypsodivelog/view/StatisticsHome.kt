package com.example.calypsodivelog.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.calypsodivelog.databinding.StatisticsHomeBinding

class StatisticsHome: Fragment() {
    private lateinit var binding: StatisticsHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StatisticsHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root

        return rootView
    }
}