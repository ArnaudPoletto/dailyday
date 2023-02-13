package com.example.dailyday.activities

import android.os.Bundle
import android.widget.Button
import com.example.dailyday.database.DatabaseEntries
import com.example.dailyday.R
import com.example.dailyday.activities.auth.RegisterActivity
import com.example.dailyday.activities.evaluation.CreateEvaluationActivity
import com.example.dailyday.activities.evaluation.EditEvaluationActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        logoutLogic()
        evaluateLogic()
    }

    override fun onStart() {
        super.onStart()

        registerLogic()
    }

    /**
     * Implements the on create logout logic
     */
    private fun logoutLogic() {
        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            logout()
        }
    }

    /**
     * Implements the on create day evaluation logic
     */
    private fun evaluateLogic() {
        val evaluateButton = findViewById<Button>(R.id.evaluation_button)
        evaluateButton.isEnabled = false

        // Modify button
        DatabaseEntries.loggedInUserAlreadyEvaluatedToday(
            auth,
            Firebase.database
        ) { alreadyEvaluated ->
            if (alreadyEvaluated) {
                evaluateButton.text = getString(R.string.edit_evaluation_button_text)
            } else {
                evaluateButton.text = getString(R.string.create_evaluation_button_text)
            }
            evaluateButton.isEnabled = true
        }

        // Choose which activity to go to
        evaluateButton.setOnClickListener {
            DatabaseEntries.loggedInUserAlreadyEvaluatedToday(
                auth,
                Firebase.database
            ) { alreadyEvaluated ->
                if (alreadyEvaluated) {
                    goToActivity(EditEvaluationActivity::class.java)
                } else {
                    goToActivity(CreateEvaluationActivity::class.java)
                }
            }
        }
    }

    /**
     * Implements the register logic
     */
    private fun registerLogic() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            goToActivity(RegisterActivity::class.java)
        }
    }
}
