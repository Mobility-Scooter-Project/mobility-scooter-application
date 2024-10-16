package com.mobility.mobilityscooterapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController

class messages_page : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val HomeButton = view.findViewById<TextView>(R.id.home)
        HomeButton?.setOnClickListener {
            findNavController().navigate(R.id.action_messages_page_to_homeFragment)
        }

        val DriveButton = view.findViewById<TextView>(R.id.Drive_Bottom)
        DriveButton?.setOnClickListener {
            findNavController().navigate(R.id.action_messages_page_to_drive_start_page)
        }

        val AnalyticsButton = view.findViewById<TextView>(R.id.analytics_button)
        AnalyticsButton?.setOnClickListener {
            findNavController().navigate(R.id.action_messages_page_to_analytic_start_page)
        }


    }

}
