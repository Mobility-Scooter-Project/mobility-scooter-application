package com.mobility.mobilityscooterapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController


class analytic_start_page : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytic_start_page, container, false)
    }

        @SuppressLint("SuspiciousIndentation")
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            val fromHistory = arguments?.getBoolean("fromHistory", false) ?: false
            if (fromHistory) {
                val intent = Intent(activity, session_history_activity::class.java)
                startActivity(intent)
            }

//        val homeButton = view.findViewById<TextView>(R.id.home)
//            homeButton?.setOnClickListener {
//            findNavController().navigate(R.id.action_analytic_start_page_to_homeFragment)
//        }
//        val driveButton = view.findViewById<TextView>(R.id.Drive_Bottom)
//            driveButton?.setOnClickListener {
//                findNavController().navigate(R.id.action_analytic_start_page_to_drive_start_page)
//
//            }
//        val MessageButton = view.findViewById<TextView>(R.id.button4)
//            MessageButton?.setOnClickListener {
//                findNavController().navigate(R.id.action_analytic_start_page_to_messeges_page)
//            }
//
            val sessionHistory = view.findViewById<ImageView>(R.id.imageView1)
            sessionHistory?.setOnClickListener {
                val intent = Intent(activity, session_history_activity::class.java)
                startActivity(intent)
            }


    }

}