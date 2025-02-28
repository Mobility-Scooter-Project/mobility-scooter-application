package com.mobility.mobilityscooterapp
/**
 * Analytics Start Page Fragment provides menu for looking at past recordings
 *
 * Features:
 * - Different pages
 * - (Planned) Add pictures/thumbnails
 *
 * This fragment allows users to navigate through past recordings
 *
 */
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


class AnalyticStartPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytic_start_page, container, false)
    }
    /**
     * Called when the fragment's view has been created.
     *
     * This method checks whether the fragment was launched from session history.
     * If so, it starts `SessionHistoryActivity` and closes the current activity.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState A bundle containing saved state information.
     */
        @SuppressLint("SuspiciousIndentation")
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            val fromHistory = arguments?.getBoolean("fromHistory", false) ?: false
            if (fromHistory) {
                val intent = Intent(activity, SessionHistoryActivity::class.java)
                startActivity(intent)
            }
        //Navigation buttons
        val homeButton = view.findViewById<TextView>(R.id.home)
            homeButton?.setOnClickListener {
            findNavController().navigate(R.id.action_analytic_start_page_to_homeFragment)
        }
        val driveButton = view.findViewById<TextView>(R.id.Drive_Bottom)
            driveButton?.setOnClickListener {
                findNavController().navigate(R.id.action_analytic_start_page_to_drive_start_page)

            }
        val MessageButton = view.findViewById<TextView>(R.id.messages_button)
            MessageButton?.setOnClickListener {
                findNavController().navigate(R.id.action_analytic_start_page_to_messages_page)
            }
        val sessionHistory = view.findViewById<ImageView>(R.id.imageView1)
            sessionHistory?.setOnClickListener {
                val intent = Intent(activity, SessionHistoryActivity::class.java)
                startActivity(intent)
            }

    }

}