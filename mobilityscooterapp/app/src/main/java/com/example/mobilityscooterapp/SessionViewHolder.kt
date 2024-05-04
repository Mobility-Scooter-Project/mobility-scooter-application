package com.example.mobilityscooterapp

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val sessionDate: TextView = view.findViewById(R.id.session_date)
    val sessionImage: ImageView = view.findViewById(R.id.session_image)
}