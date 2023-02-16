package com.example.dailyday.activities.evaluation

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.dailyday.database.DatabaseEntries
import com.example.dailyday.R
import com.example.dailyday.activities.BaseActivity
import com.example.dailyday.activities.MainActivity
import com.example.dailyday.entry.Entry
import com.example.dailyday.entry.EntryDate
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class CreateEvaluationActivity : BaseActivity() {

    private var date: EntryDate = EntryDate.today()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_evaluation)

        // Get date from extra
        val dateStr = intent.getStringExtra("date")
        if (dateStr != null) {
            date = EntryDate.fromString(dateStr)
        }

        // Evaluate button
        val evaluate = findViewById<android.widget.Button>(R.id.create_evaluation_button)
        evaluate.setOnClickListener {
            DatabaseEntries.loggedInUserAlreadyEvaluatedAtDate(
                auth,
                Firebase.database,
                date
            ) { alreadyEvaluated ->
                if (alreadyEvaluated) {
                    Toast.makeText(this, "You already evaluated at this date", Toast.LENGTH_SHORT).show()
                    return@loggedInUserAlreadyEvaluatedAtDate
                }
                evaluate()
            }
        }

        // Print date today
        val dateToday = findViewById<android.widget.TextView>(R.id.create_date)
        dateToday.text = date.toString()

        // Back button
        backLogic()
    }

    /**
     * Implements the logic of the back button
     */
    private fun backLogic() {
        val historyBack = findViewById<Button>(R.id.create_evaluation_back_button)
        historyBack.setOnClickListener {
            goToActivity(MainActivity::class.java)
            finish()
        }
    }

    /**
     * Builds an entry from the user input and adds it to the database, redirecting to the main activity
     */
    fun evaluate() {
        // Build entry
        val appreciation: String = findViewById<EditText>(R.id.evaluation_appreciation).text.toString()
        val energy: String = findViewById<EditText>(R.id.evaluation_energy).text.toString()
        val activity: String = findViewById<EditText>(R.id.evaluation_activity).text.toString()
        val socialInteraction: String = findViewById<EditText>(R.id.evaluation_social_interaction).text.toString()
        val stress: String = findViewById<EditText>(R.id.evaluation_stress).text.toString()
        val hoursOfSleep: String = findViewById<EditText>(R.id.evaluation_hours_of_sleep).text.toString()
        val cry: Boolean = findViewById<CheckBox>(R.id.evaluation_cry).isChecked
        val outside: Boolean = findViewById<CheckBox>(R.id.evaluation_outside).isChecked
        val alcohol: Boolean = findViewById<CheckBox>(R.id.evaluation_alcohol).isChecked
        val important: Boolean = findViewById<CheckBox>(R.id.evaluation_important).isChecked
        val cryReason: String = findViewById<EditText>(R.id.evaluation_cry_reason).text.toString()

        val appreciationValue: Int? = if (appreciation == "") null else appreciation.toInt()
        val energyValue: Int? = if (energy == "") null else energy.toInt()
        val activityValue: Int? = if (activity == "") null else activity.toInt()
        val socialInteractionValue: Int? = if (socialInteraction == "") null else socialInteraction.toInt()
        val stressValue: Int? = if (stress == "") null else stress.toInt()
        val hoursOfSleepValue: Double? = if (hoursOfSleep == "") null else hoursOfSleep.toDouble()
        val cryReasonValue: String = cryReason.takeIf { cry } ?: ""
        val dateValue: Calendar = date.toCalendar()

        val entry: Entry
        try {
            entry = Entry(
                appreciationValue,
                energyValue,
                activityValue,
                socialInteractionValue,
                stressValue,
                hoursOfSleepValue,
                cry,
                outside,
                alcohol,
                important,
                cryReasonValue,
                dateValue
            )
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return
        }

        // Add it to db and return to main activity
        DatabaseEntries.createLoggedInUserEntryToDatabase(auth, Firebase.database, entry)
        goToActivity(MainActivity::class.java)
    }

}