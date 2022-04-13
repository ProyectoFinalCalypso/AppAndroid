package com.example.calypsodivelog.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.calypsodivelog.databinding.PlanningHomeBinding

class PlanningHome: Fragment() {
    private lateinit var binding: PlanningHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PlanningHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root

        return rootView
    }
}