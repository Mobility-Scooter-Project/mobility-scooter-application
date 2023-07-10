package com.example.mobilityscooterapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController


class drive_start_page : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drive_start_page, container, false)
    }

        @SuppressLint("SuspiciousIndentation")
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val HomeButton = view.findViewById<TextView>(R.id.home)
            HomeButton?.setOnClickListener {
                findNavController().navigate(R.id.action_drive_start_page_to_homeFragment)
            }


        val AnalyticsButton = view.findViewById<TextView>(R.id.analytics_button)
            AnalyticsButton?.setOnClickListener {
                findNavController().navigate(R.id.action_drive_start_page_to_analytic_start_page)
            }

        val MessageButton = view.findViewById<TextView>(R.id.button4)
            MessageButton?.setOnClickListener {
                findNavController().navigate(R.id.action_drive_start_page_to_messeges_page)
            }
        val startButton = view.findViewById<Button>(R.id.start_record_button)
            startButton?.setOnClickListener {
                findNavController().navigate(R.id.action_drive_start_page_to_record_preview_activity)
            }
    }
}