package com.example.mobilityscooterapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController

class AccessibilityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accessibility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val HomeButton = view.findViewById<TextView>(R.id.home)
        HomeButton?.setOnClickListener {
            findNavController().navigate(R.id.action_accessibilityFragment_to_homeFragment)
        }

        val DriveButton = view.findViewById<TextView>(R.id.Drive_Bottom)
        DriveButton?.setOnClickListener {
            findNavController().navigate(R.id.action_accessibilityFragment_to_drive_start_page)
        }

        val AnalyticsButton = view.findViewById<TextView>(R.id.analytics_button)
        AnalyticsButton?.setOnClickListener {
            findNavController().navigate(R.id.action_accessibilityFragment_to_analytic_start_page)
        }

        val Messagebutton = view.findViewById<TextView>(R.id.button4)
        Messagebutton?.setOnClickListener {
            findNavController().navigate(R.id.action_accessibilityFragment_to_messeges_page)
        }

    }
}
