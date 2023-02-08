package com.example.dailyday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Date

class EvaluationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)

        auth = Firebase.auth

        // evaluate
        val evaluate = findViewById<android.widget.Button>(R.id.modify_button)
        evaluate.setOnClickListener {
            evaluate()
        }

        // print date today
        val date_today = findViewById<android.widget.TextView>(R.id.modify_date)
        date_today.text = android.text.format.DateFormat.format("MMMM d, yyyy", java.util.Date())
    }

    fun evaluate() {
        val entries = Firebase.database.getReference("users/${auth.currentUser?.uid}/entries")
        entries.get().addOnSuccessListener {
            for (entry_data in it.children) {
                val entry = entry_data.getValue(Entry::class.java)!!
                val timestamp = entry.getTimestamp()
                //if same day for timestamp
                if(DateFormat.format("MMMM d, yyyy", Date(timestamp)) == DateFormat.format("MMMM d, yyyy", Date())) {
                    Toast.makeText(this, "You have already evaluated today.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
            }
            val appreciation: String = findViewById<EditText>(R.id.evaluation_appreciation).text.toString()
            val hoursOfSleep: String = findViewById<EditText>(R.id.evaluation_hours_of_sleep).text.toString()
            val energy: String = findViewById<EditText>(R.id.evaluation_energy).text.toString()

            val appreciation_int: Int? = if (appreciation == "") null else appreciation.toInt()
            val hoursOfSleep_float: Double? = if (hoursOfSleep == "") null else hoursOfSleep.toDouble()
            val energy_int: Int? = if (energy == "") null else energy.toInt()

            var entry: Entry? = null
            try {
                entry = Entry(appreciation_int, hoursOfSleep_float, energy_int)
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            // add it to db
            val reference = Firebase.database.getReference("users/${auth.currentUser?.uid}/entries")
            reference.push().setValue(entry)

        }
    }

}