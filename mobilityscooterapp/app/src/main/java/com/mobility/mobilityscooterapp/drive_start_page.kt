package com.mobility.mobilityscooterapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

        val homeButton = view.findViewById<TextView>(R.id.home)
        homeButton?.setOnClickListener {
            findNavController().navigate(R.id.action_drive_start_page_to_homeFragment)
        }

        val analyticsButton = view.findViewById<TextView>(R.id.analytics_button)
        analyticsButton?.setOnClickListener {
            findNavController().navigate(R.id.action_drive_start_page_to_analytic_start_page)
        }

        val messageButton = view.findViewById<TextView>(R.id.button4)
        messageButton?.setOnClickListener {
            findNavController().navigate(R.id.action_drive_start_page_to_messages_page)
        }

        val startButton = view.findViewById<Button>(R.id.start_record_button)
        startButton?.setOnClickListener {
                if(cameraPermissions() ){
                    findNavController().navigate(R.id.action_drive_start_page_to_record_preview_activity)
                }
                else{
                        AlertDialog.Builder(requireContext())
                            .setTitle("Permissions Needed")
                            .setMessage("This app needs Camera and Storage permissions to start recording. Please grant these permissions.")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }

        }
    }
    private fun cameraPermissions():Boolean{
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

}
