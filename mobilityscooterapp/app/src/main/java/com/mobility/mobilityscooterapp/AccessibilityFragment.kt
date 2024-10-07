package com.mobility.mobilityscooterapp

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
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
        return inflater.inflate(R.layout.fragment_accessibility, container, false)
    }

    private var currentFontSize: Int = DEFAULT_FONT_SIZE

    companion object {
        const val PREFS_NAME = "AppSettingsPrefs"
        const val FONT_SIZE_KEY = "FontSize"
        const val DEFAULT_FONT_SIZE = 18
    }

    private fun saveFontSizePreference(fontSize: Int) {
        val sharedPref = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(FONT_SIZE_KEY, fontSize)
            apply()
        }
    }

    private fun getFontSizePreference(): Int {
        val sharedPref = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref?.getInt(FONT_SIZE_KEY, DEFAULT_FONT_SIZE) ?: DEFAULT_FONT_SIZE
    }

    private fun applyFontSize(fontSize: Int) {
        val textViews = listOf(
            view?.findViewById<TextView>(R.id.textView4)
        )
        textViews.forEach { textView ->
            textView?.let {
                val scaledSize = resources.displayMetrics.scaledDensity * fontSize
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentFontSize = getFontSizePreference()

        val fontSizeText = view.findViewById<TextView>(R.id.font_size_text)
        fontSizeText.text = currentFontSize.toString()

        applyFontSize(currentFontSize)

        view.findViewById<Button>(R.id.decrease_font_size_button).setOnClickListener {
            if (currentFontSize > 12) { // Set a minimum font size
                changeFontSize(-1, fontSizeText)
            }
        }

        view.findViewById<Button>(R.id.increase_font_size_button).setOnClickListener {
            if (currentFontSize < 30) { // Set a maximum font size
                changeFontSize(1, fontSizeText)
            }
        }

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
            findNavController().navigate(R.id.action_accessibilityFragment_to_messages_page)
        }
    }

    private fun changeFontSize(change: Int, fontSizeText: TextView) {
        currentFontSize += change
        fontSizeText.text = currentFontSize.toString()

        saveFontSizePreference(currentFontSize) // Save the new preference
        applyFontSize(currentFontSize) // Apply the new font size
    }
}
