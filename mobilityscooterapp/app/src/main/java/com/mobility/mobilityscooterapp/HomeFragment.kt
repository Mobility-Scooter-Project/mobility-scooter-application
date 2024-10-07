package com.mobility.mobilityscooterapp

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController


class HomeFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_home, container, false)
        }

        fun showToast(context: Context, message: String) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.custom_toast_layout, null)

        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        toastText.text = message

        with (Toast(context)) {
            duration = Toast.LENGTH_LONG
            setGravity(Gravity.CENTER, 0, 0)
            view = layout
            show()
        }
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
            findNavController().navigate(R.id.action_homeFragment_to_messages_page)
        }

        val accessibilityButton = view.findViewById<Button>(R.id.accessibility_setting_bottom)
        accessibilityButton?.setOnClickListener {
            showToast(requireContext(), "Under construction!")
            //findNavController().navigate(R.id.action_homeFragment_to_accessibilityFragment)
        }

    }


}


