package com.mobility.scooterapp
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobility.scooterapp.databinding.ActivitySessionHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
/**
 * SessionHistoryActivity
 * Handles showing the previous recordings to the user
 *
 * Responsibilities:
 * - Manages navigating through previous recordings in the database
 */

class SessionHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionHistoryBinding
    private lateinit var adapter: SessionAdapter
    private val sessionList = mutableListOf<Session>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = SessionAdapter(sessionList, this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        fetchSessions()
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchSessions() {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("users").document(userId!!).collection("sessions")
            .orderBy("dateTimeString", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val session = document.toObject(Session::class.java)
                    sessionList.add(session)
                }
                // update adapter
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

}