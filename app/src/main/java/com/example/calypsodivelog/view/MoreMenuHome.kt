package com.example.calypsodivelog.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.calypsodivelog.databinding.MoreMenuHomeBinding

class MoreMenuHome: Fragment() {
    private lateinit var binding: MoreMenuHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MoreMenuHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root

        return rootView
    }
}