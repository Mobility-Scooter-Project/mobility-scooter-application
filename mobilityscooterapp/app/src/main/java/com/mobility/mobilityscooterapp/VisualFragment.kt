package com.mobility.mobilityscooterapp

import android.util.TypedValue
import android.widget.TextView
import androidx.fragment.app.Fragment

import android.content.Context
abstract class BaseFragment : Fragment() {

    companion object {
        const val PREFS_NAME = "AppSettingsPrefs"
        const val FONT_SIZE_KEY = "FontSize"
        const val DEFAULT_FONT_SIZE = 18
    }

    fun getFontSizePreference(): Int {
        val sharedPref = activity?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref?.getInt(FONT_SIZE_KEY, DEFAULT_FONT_SIZE) ?: DEFAULT_FONT_SIZE
    }

    fun applyFontSizeToViews(vararg textViews: TextView?) {
        val fontSize = getFontSizePreference()
        textViews.forEach { textView ->
            textView?.let {
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
            }
        }
    }
}
