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
import java.util.*

class ModifyEvaluationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_evaluation)

        auth = Firebase.auth

        autofill()

        val modify = findViewById<android.widget.Button>(R.id.modify_button)
        modify.setOnClickListener {
            modify()
        }

        // print date today
        val date_today = findViewById<android.widget.TextView>(R.id.modify_date)
        date_today.text = android.text.format.DateFormat.format("MMMM d, yyyy", java.util.Date())
    }

    fun autofill() {
        val entries = Firebase.database.getReference("users/${auth.currentUser?.uid}/entries")
        entries.get().addOnSuccessListener {
            for (entry_data in it.children) {
                val entry = entry_data.getValue(Entry::class.java)!!
                val timestamp = entry.getTimestamp()
                //if same day for timestamp
                if (DateFormat.format(
                        "MMMM d, yyyy",
                        Date(timestamp)
                    ) == DateFormat.format("MMMM d, yyyy", Date())
                ) {
                    val appreciation: Int = entry.getAppreciation()!!
                    val hoursOfSleep: Double = entry.getHoursOfSleep()!!
                    val energy: Int = entry.getEnergy()!!

                    findViewById<EditText>(R.id.evaluation_appreciation).setText(appreciation.toString())
                    findViewById<EditText>(R.id.evaluation_hours_of_sleep).setText(hoursOfSleep.toString())
                    findViewById<EditText>(R.id.evaluation_energy).setText(energy.toString())
                    return@addOnSuccessListener
                }
            }
        }
    }

    fun modify() {
        val appreciation: String = findViewById<EditText>(R.id.evaluation_appreciation).text.toString()
        val hoursOfSleep: String = findViewById<EditText>(R.id.evaluation_hours_of_sleep).text.toString()
        val energy: String = findViewById<EditText>(R.id.evaluation_energy).text.toString()

        val appreciation_int: Int? = if (appreciation == "") null else appreciation.toInt()
        val hoursOfSleep_float: Double? = if (hoursOfSleep == "") null else hoursOfSleep.toDouble()
        val energy_int: Int? = if (energy == "") null else energy.toInt()

        var modified_entry: Entry? = null
        try {
            modified_entry = Entry(appreciation_int, hoursOfSleep_float, energy_int)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return
        }

        // add it to db
        val entries = Firebase.database.getReference("users/${auth.currentUser?.uid}/entries")
        entries.get().addOnSuccessListener {
            for (entry in it.children) {
                val timestamp = entry.child("timestamp").value as Long
                //if same day for timestamp
                if (DateFormat.format(
                        "MMMM d, yyyy",
                        Date(timestamp)
                    ) == DateFormat.format("MMMM d, yyyy", Date())
                ) {
                    // update entry
                    entry.ref.setValue(modified_entry)
                    Toast.makeText(this, "Evaluation updated.", Toast.LENGTH_SHORT).show()

                    // go to main
                    val intent = android.content.Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    return@addOnSuccessListener
                }
            }
        }
    }
}