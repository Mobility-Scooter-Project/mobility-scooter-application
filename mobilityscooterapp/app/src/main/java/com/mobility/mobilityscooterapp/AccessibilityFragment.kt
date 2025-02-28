package com.mobility.mobilityscooterapp
/**
 * AccessibilityFragment provides accessibility options for the Mobility Scooter app.
 *
 * Features:
 * - Dark Mode toggle
 * - (Planned) Font size adjustments for better readability
 *
 * This fragment allows users to enable or disable dark mode and navigate to different sections.
 * Font size adjustment functionality is currently commented out.
 */

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

/**
 * A fragment that provides accessibility settings for the app, currently only dark mode toggle
 */
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
        /**  Shared  Preferences file name */
        const val PREFS_NAME = "AppSettingsPrefs"

        /**  Key for storing font size preference */
        const val FONT_SIZE_KEY = "FontSize"

        /**  Key for storing dark mode preference */
        const val DARK_MODE_KEY = "DarkMode"

        /**  Default font size used in the app */
        const val DEFAULT_FONT_SIZE = 18
    }

    /**
     * Saves the selected font size to SharedPreferences.
     *
     * @param fontSize The new font size to save.
     */
    private fun saveFontSizePreference(fontSize: Int) {
        val sharedPref = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(FONT_SIZE_KEY, fontSize)
            apply()
        }
    }

    /**
     * Gets the current font size of the app
     *
     * @return The stored current font size or default if not found
     */
    private fun getFontSizePreference(): Int {
        val sharedPref = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref?.getInt(FONT_SIZE_KEY, DEFAULT_FONT_SIZE) ?: DEFAULT_FONT_SIZE
    }

    /**
     * Applies font size changes to the app
     *
     */
//    private fun applyFontSize(fontSize: Int) {
//        val textViews = listOf(
//            view?.findViewById<TextView>(R.id.textView4)
//        )
//        textViews.forEach { textView ->
//            textView?.let {
//                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
//            }
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch)
//        currentFontSize = getFontSizePreference()

//        val fontSizeText = view.findViewById<TextView>(R.id.font_size_text)
//        fontSizeText.text = currentFontSize.toString()
//
//        applyFontSize(currentFontSize)
//
//        view.findViewById<Button>(R.id.decrease_font_size_button).setOnClickListener {
//            if (currentFontSize > 12) { // Set a minimum font size
//                changeFontSize(-1, fontSizeText)
//            }
//        }
//
//        view.findViewById<Button>(R.id.increase_font_size_button).setOnClickListener {
//            if (currentFontSize < 30) { // Set a maximum font size
//                changeFontSize(1, fontSizeText)
//            }
//        }

        val sharedPref = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPref?.getBoolean(DARK_MODE_KEY, false) ?: false
        darkModeSwitch.isChecked = isDarkModeOn
        setDarkMode(isDarkModeOn)
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
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

        val messageButton = view.findViewById<TextView>(R.id.messages_button)
        messageButton?.setOnClickListener {
            findNavController().navigate(R.id.action_accessibilityFragment_to_messages_page)
        }
    }

    /**
     *Applies dark mode based on the key from Dark Mode
     *
     *
     */
    private fun setDarkMode(isDarkMode: Boolean) {
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
    /**
     *Applies font size changes based on the user's input of increasing or decreasing
     *
     *
     */
    private fun changeFontSize(change: Int, fontSizeText: TextView) {
        currentFontSize += change
        fontSizeText.text = currentFontSize.toString()
        saveFontSizePreference(currentFontSize) // Save the new preference
//        applyFontSize(currentFontSize) // Apply the new font size
    }

}
