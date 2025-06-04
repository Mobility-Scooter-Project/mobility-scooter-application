package com.mobility.scooterapp

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class SessionAdapter(var sessions: List<Session>, val context: Context) : RecyclerView.Adapter<SessionViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = inflater.inflate(R.layout.activity_session_item, parent, false)
        return SessionViewHolder(view)
    }

    override fun getItemCount(): Int = sessions.size

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]

        holder.sessionDate.text = session.dateTimeString
        session.thumbnail_url?.let { Log.d("URL_CHECK", it) }
        Glide.with(context).load(session.thumbnail_url).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                Log.e(TAG, "Load failed", e)
                return false // allow calling onLoadFailed on the 'error' Drawable.
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: com.bumptech.glide.load.DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false // do nothing, let the image load.
            }
        }).into(holder.sessionImage)



        holder.itemView.setOnClickListener {
            val intent = Intent(context, SessionDetailActivity::class.java)
            intent.putExtra("date", session.date)
            intent.putExtra("start_time", session.start_time)
            intent.putExtra("session_length", session.session_length)
            intent.putExtra("video_url", session.video_url)
            intent.putExtra("encryptedFilePath", session.encryptedFilePath)
            intent.putExtra("estimateData", session.estimateData)
            context.startActivity(intent)
        }
    }
}
