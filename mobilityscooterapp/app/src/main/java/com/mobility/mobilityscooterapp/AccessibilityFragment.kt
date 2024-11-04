package com.mobility.mobilityscooterapp

import android.content.Context
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat


class AccessibilityFragment : Fragment() {
    private lateinit var darkModeSwitch: SwitchCompat
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
        const val DARK_MODE_KEY = "DarkMode"
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
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch)
        currentFontSize = getFontSizePreference()

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

        val sharedPref = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPref?.getBoolean(DARK_MODE_KEY, false) ?: false
        darkModeSwitch.isChecked = isDarkModeOn
        setDarkMode(isDarkModeOn)
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            Log.d("DarkModeToggle", "Dark Mode Checked: $isChecked")
            setDarkMode(isChecked)
            // Save dark mode preference
            sharedPref?.edit()?.putBoolean(DARK_MODE_KEY, isChecked)?.apply()
        }
        val homeButton = view.findViewById<TextView>(R.id.home)
        homeButton?.setOnClickListener {
            findNavController().navigate(R.id.action_accessibilityFragment_to_homeFragment)
        }

        val driveButton = view.findViewById<TextView>(R.id.Drive_Bottom)
        driveButton?.setOnClickListener {
            findNavController().navigate(R.id.action_accessibilityFragment_to_drive_start_page)
        }

        val analyticsButton = view.findViewById<TextView>(R.id.analytics_button)
        analyticsButton?.setOnClickListener {
            findNavController().navigate(R.id.action_accessibilityFragment_to_analytic_start_page)
        }

        val messageButton = view.findViewById<TextView>(R.id.button4)
        messageButton?.setOnClickListener {
            findNavController().navigate(R.id.action_accessibilityFragment_to_messages_page)
        }
    }
    private fun setDarkMode(isDarkMode: Boolean) {
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
    private fun changeFontSize(change: Int, fontSizeText: TextView) {
        currentFontSize += change
        fontSizeText.text = currentFontSize.toString()

        saveFontSizePreference(currentFontSize) // Save the new preference
        applyFontSize(currentFontSize) // Apply the new font size
    }

}
