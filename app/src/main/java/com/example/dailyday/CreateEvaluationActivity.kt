package com.example.dailyday

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.EditText
import android.widget.Toast
import com.example.dailyday.entry.Entry
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Date

class CreateEvaluationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_evaluation)

        // Evaluate button
        val evaluate = findViewById<android.widget.Button>(R.id.edit_button)
        evaluate.setOnClickListener {
            DatabaseEntries.loggedInUserAlreadyEvaluatedToday(auth, Firebase.database) { alreadyEvaluated ->
                if (alreadyEvaluated) {
                    Toast.makeText(this, "You already evaluated today", Toast.LENGTH_SHORT).show()
                    return@loggedInUserAlreadyEvaluatedToday
                }
                evaluate()
            }
        }

        // Print date today
        val dateToday = findViewById<android.widget.TextView>(R.id.modify_date)
        dateToday.text = DateFormat.format("MMMM d, yyyy", Date())
    }

    /**
     * Builds an entry from the user input and adds it to the database, redirecting to the main activity
     */
    fun evaluate() {
        // Build entry
        val appreciation: String = findViewById<EditText>(R.id.evaluation_appreciation).text.toString()
        val hoursOfSleep: String = findViewById<EditText>(R.id.evaluation_hours_of_sleep).text.toString()
        val energy: String = findViewById<EditText>(R.id.evaluation_energy).text.toString()

        val appreciationValue: Int? = if (appreciation == "") null else appreciation.toInt()
        val energyValue: Int? = if (energy == "") null else energy.toInt()
        val hoursOfSleepValue: Double? = if (hoursOfSleep == "") null else hoursOfSleep.toDouble()
        val dateValue: Calendar = Calendar.getInstance()

        val entry: Entry
        try {
            entry = Entry(appreciationValue, energyValue, hoursOfSleepValue, dateValue)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return
        }

        // Add it to db and return to main activity
        DatabaseEntries.createLoggedInUserEntryToDatabase(auth, Firebase.database, entry)
        goToActivity(MainActivity::class.java)
    }

}