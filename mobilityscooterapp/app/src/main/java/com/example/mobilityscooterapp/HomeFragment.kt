package com.example.mobilityscooterapp

import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val driveButton = view.findViewById<Button>(R.id.Drive_Bottom)
        driveButton?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_drive_start_page)
        }


        val analyzeButton = view.findViewById<Button>(R.id.analytics_bottom)
        analyzeButton?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_analytic_start_page)
        }

        val Messagebutton = view.findViewById<Button>(R.id.messages_bottom)
        Messagebutton?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_messeges_page)
        }

        val accessibilityButton = view.findViewById<Button>(R.id.accessibility_setting_bottom)
        accessibilityButton?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_accessibilityFragment)
        }

    }
}


