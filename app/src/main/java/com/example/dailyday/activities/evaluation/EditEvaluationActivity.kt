package com.example.dailyday.activities.evaluation

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.*
import com.example.dailyday.database.DatabaseEntries
import com.example.dailyday.R
import com.example.dailyday.activities.BaseActivity
import com.example.dailyday.activities.MainActivity
import com.example.dailyday.entry.Entry
import com.example.dailyday.entry.EntryDate
import com.example.dailyday.entry.EntryScore
import com.example.dailyday.entry.EntrySleep
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class EditEvaluationActivity : BaseActivity() {

    private var date: EntryDate = EntryDate.today()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_evaluation)

        // Get date from extra
        val dateStr = intent.getStringExtra("date")
        if (dateStr != null) {
            date = EntryDate.fromString(dateStr)
        }

        checkAlreadyEvaluated()
        autofill()

        val edit = findViewById<Button>(R.id.edit_evaluation_button)
        edit.setOnClickListener {
            edit()
        }

        // Print date today
        val dateToday = findViewById<TextView>(R.id.modify_date)
        dateToday.text = date.toString()

        // Back button
        backLogic()
    }

    private fun checkAlreadyEvaluated() {
        DatabaseEntries.loggedInUserAlreadyEvaluatedAtDate(
            auth,
            Firebase.database,
            date
        ) { alreadyEvaluated ->
            if (!alreadyEvaluated) {
                Toast.makeText(this, "You haven't evaluated at this date", Toast.LENGTH_SHORT).show()
                goToActivity(MainActivity::class.java)
            }
        }
    }

    /**
     * Implements the logic of the back button
     */
    private fun backLogic() {
        val historyBack = findViewById<Button>(R.id.edit_evaluation_back_button)
        historyBack.setOnClickListener {
            goToActivity(MainActivity::class.java)
            finish()
        }
    }

    /**
     * Autofill the form with the user's evaluation of today
     */
    private fun autofill() {
        DatabaseEntries.getLoggedInUserEntryAtDate(auth, Firebase.database, date) { entry ->
            if (entry == null) {
                return@getLoggedInUserEntryAtDate
            }

            val appreciation: EntryScore? = entry.getAppreciation()
            val energy: EntryScore? = entry.getEnergy()
            val activity: EntryScore? = entry.getActivity()
            val socialInteraction: EntryScore? = entry.getSocialInteraction()
            val stress: EntryScore? = entry.getStress()
            val hoursOfSleep: EntrySleep? = entry.getHoursOfSleep()
            val cry: Boolean? = entry.getCry()
            val outside: Boolean? = entry.getOutside()
            val alcohol: Boolean? = entry.getAlcohol()
            val important: Boolean? = entry.getImportant()
            val cryReason: String? = entry.getCryReason()

            if (appreciation != null) {
                findViewById<EditText>(R.id.evaluation_appreciation).setText(
                    appreciation.getValue().toString()
                )
            }
            if (energy != null) {
                findViewById<EditText>(R.id.evaluation_energy).setText(energy.getValue().toString())
            }
            if (activity != null) {
                findViewById<EditText>(R.id.evaluation_activity).setText(activity.getValue().toString())
            }
            if (socialInteraction != null) {
                findViewById<EditText>(R.id.evaluation_social_interaction).setText(
                    socialInteraction.getValue().toString()
                )
            }
            if (stress != null) {
                findViewById<EditText>(R.id.evaluation_stress).setText(stress.getValue().toString())
            }
            if (hoursOfSleep != null) {
                findViewById<EditText>(R.id.evaluation_hours_of_sleep).setText(
                    hoursOfSleep.getValue().toString()
                )
            }
            if (cry != null) {
                findViewById<CheckBox>(R.id.evaluation_cry).isChecked = cry
            }
            if (outside != null) {
                findViewById<CheckBox>(R.id.evaluation_outside).isChecked = outside
            }
            if (alcohol != null) {
                findViewById<CheckBox>(R.id.evaluation_alcohol).isChecked = alcohol
            }
            if (important != null) {
                findViewById<CheckBox>(R.id.evaluation_important).isChecked = important
            }
            if (cryReason != null) {
                findViewById<EditText>(R.id.evaluation_cry_reason).setText(cryReason)
            }
        }
    }

    /**
     * Edit the user's evaluation of today, redirecting to the main activity
     */
    private fun edit() {
        // Build modified entry
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

        val modifiedEntry: Entry
        try {
            modifiedEntry = Entry(
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

        // Edit it in db and return to main activity
        DatabaseEntries.editLoggedInUserEntryToDatabase(auth, Firebase.database, modifiedEntry)
        goToActivity(MainActivity::class.java)
    }
}