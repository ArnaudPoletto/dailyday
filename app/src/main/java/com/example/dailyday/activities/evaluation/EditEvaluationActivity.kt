package com.example.dailyday.activities.evaluation

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.EditText
import android.widget.Toast
import com.example.dailyday.database.DatabaseEntries
import com.example.dailyday.R
import com.example.dailyday.activities.BaseActivity
import com.example.dailyday.activities.MainActivity
import com.example.dailyday.entry.Entry
import com.example.dailyday.entry.EntryScore
import com.example.dailyday.entry.EntrySleep
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class EditEvaluationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_evaluation)

        checkAlreadyEvaluated()
        autofill()

        val edit = findViewById<android.widget.Button>(R.id.edit_button)
        edit.setOnClickListener {
            edit()
        }

        // Print date today
        val dateToday = findViewById<android.widget.TextView>(R.id.modify_date)
        dateToday.text = DateFormat.format("MMMM d, yyyy", Date())
    }

    fun checkAlreadyEvaluated() {
        DatabaseEntries.loggedInUserAlreadyEvaluatedToday(
            auth,
            Firebase.database
        ) { alreadyEvaluated ->
            if (!alreadyEvaluated) {
                Toast.makeText(this, "You haven't evaluated today", Toast.LENGTH_SHORT).show()
                goToActivity(MainActivity::class.java)
            }
        }
    }

    /**
     * Autofill the form with the user's evaluation of today
     */
    fun autofill() {
        DatabaseEntries.getLoggedInUserTodayEntry(auth, Firebase.database) { entry ->
            if (entry == null) {
                return@getLoggedInUserTodayEntry
            }

            val appreciation: EntryScore? = entry.getAppreciation()
            val energy: EntryScore? = entry.getEnergy()
            val hoursOfSleep: EntrySleep? = entry.getHoursOfSleep()

            if (appreciation != null) {
                findViewById<EditText>(R.id.evaluation_appreciation).setText(
                    appreciation.getValue().toString()
                )
            }
            if (energy != null) {
                findViewById<EditText>(R.id.evaluation_energy).setText(energy.getValue().toString())
            }
            if (hoursOfSleep != null) {
                findViewById<EditText>(R.id.evaluation_hours_of_sleep).setText(
                    hoursOfSleep.getValue().toString()
                )
            }
        }
    }

    /**
     * Edit the user's evaluation of today, redirecting to the main activity
     */
    fun edit() {
        // Build modified entry
        val appreciation: String = findViewById<EditText>(R.id.evaluation_appreciation).text.toString()
        val hoursOfSleep: String = findViewById<EditText>(R.id.evaluation_hours_of_sleep).text.toString()
        val energy: String = findViewById<EditText>(R.id.evaluation_energy).text.toString()

        val appreciationValue: Int? = if (appreciation == "") null else appreciation.toInt()
        val energyValue: Int? = if (energy == "") null else energy.toInt()
        val hoursOfSleepValue: Double? = if (hoursOfSleep == "") null else hoursOfSleep.toDouble()
        val dateValue: Calendar = Calendar.getInstance()

        val modifiedEntry: Entry
        try {
            modifiedEntry = Entry(appreciationValue, energyValue, hoursOfSleepValue, dateValue)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return
        }

        // Edit it in db and return to main activity
        DatabaseEntries.editLoggedInUserEntryToDatabase(auth, Firebase.database, modifiedEntry)
        goToActivity(MainActivity::class.java)
    }
}