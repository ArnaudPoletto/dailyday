package com.example.dailyday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        auth = Firebase.auth

        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            register()
        }

        val evaluateButton = findViewById<Button>(R.id.evaluation_button)
        // Modify button
        alreadyEvaluatedToday { alreadyEvaluated ->
            if (alreadyEvaluated) {
                evaluateButton.text = "Modify your day!"
            }
        }

        // Evaluate
        evaluateButton.setOnClickListener {
            alreadyEvaluatedToday { alreadyEvaluated ->
                if (alreadyEvaluated) {
                    Toast.makeText(this, "You have already evaluated today.", Toast.LENGTH_SHORT).show()
                    modify()
                } else {
                    evaluate()
                }
            }
        }
    }

    private fun alreadyEvaluatedToday(callback: (Boolean) -> Unit) {
        val entries = Firebase.database.getReference("users/${auth.currentUser?.uid}/entries")
        entries.get().addOnSuccessListener {
            var alreadyEvaluated = false
            for (entry in it.children) {
                val timestamp = entry.child("timestamp").value as Long
                //if same day for timestamp
                if (DateFormat.format(
                        "MMMM d, yyyy",
                        Date(timestamp)
                    ) == DateFormat.format("MMMM d, yyyy", Date())
                ) {
                    alreadyEvaluated = true
                }
            }
            callback(alreadyEvaluated)
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        Log.d("MainActivity", "Current user: $currentUser")

        if (currentUser == null) {
            register()
        }
    }


    fun register() {
        // go to register activity
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun login() {
        // go to register activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun evaluate() {
        // retrieve all data from database (of the user)
        // if daily data exists: show it
        // else fill it
        val intent = Intent(this, EvaluationActivity::class.java)
        startActivity(intent)
    }

    private fun modify() {
        val intent = Intent(this, ModifyEvaluationActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun logout() {
        auth.signOut()
        login()
    }
}
