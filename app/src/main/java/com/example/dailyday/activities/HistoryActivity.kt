package com.example.dailyday.activities

import android.os.Bundle
import android.widget.Button
import com.example.dailyday.EvenDayDecorator
import com.example.dailyday.R
import com.example.dailyday.database.DatabaseEntries
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class HistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        backLogic()
        calendarLogic()
    }

    /**
     * Implements the logic of the back button
     */
    private fun backLogic() {
        val historyBack = findViewById<Button>(R.id.history_back_button)
        historyBack.setOnClickListener {
            goToActivity(MainActivity::class.java)
            finish()
        }
    }

    private fun calendarLogic() {
        val historyCalendar: MaterialCalendarView = findViewById<MaterialCalendarView>(R.id.history_calendar_view)

        DatabaseEntries.getLoggedInUserEvaluatedDates(auth, Firebase.database) { dates ->
            val decorator = EvenDayDecorator(auth, Firebase.database, dates, this)
            historyCalendar.addDecorators(decorator)
        }
    }
}