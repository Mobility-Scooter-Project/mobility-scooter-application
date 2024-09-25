package com.mobility.mobilityscooterapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class drive_start_page : Fragment() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
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
            findNavController().navigate(R.id.action_drive_start_page_to_messeges_page)
        }

        val startButton = view.findViewById<Button>(R.id.start_record_button)
        startButton?.setOnClickListener {
                if(cameraPermissions() && writePermissions()){
                    findNavController().navigate(R.id.action_drive_start_page_to_record_preview_activity)
                }
                else{
                    if(cameraPermissions()==false){
                        requestCameraPermission()
                     }
                    if(writePermissions()==false){
                        requestWritePermissions()
                    }
                }

        }
//        get_permissions()
    }
    private fun cameraPermissions():Boolean{
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }
    private fun writePermissions(): Boolean{
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun readPermissions(): Boolean{
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }
    fun get_permissions(){
        var permissionLst = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissionLst.add(Manifest.permission.CAMERA)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionLst.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionLst.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionLst.size > 0) {
            requestPermissions(permissionLst.toTypedArray(), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                get_permissions()
            }
        }
    }
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 1001)
            }
            else{
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    private fun requestWritePermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1002)
        }
    }
    private fun requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1003)
        }
    }

}
