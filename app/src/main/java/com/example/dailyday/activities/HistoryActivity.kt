package com.example.dailyday.activities

import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import com.example.dailyday.DayDecorator
import com.example.dailyday.R
import com.example.dailyday.activities.evaluation.CreateEvaluationActivity
import com.example.dailyday.activities.evaluation.EditEvaluationActivity
import com.example.dailyday.database.DatabaseEntries
import com.example.dailyday.entry.EntryDate
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.LocalDate

class HistoryActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calendarLogic() {
        val historyCalendar: MaterialCalendarView = findViewById(R.id.history_calendar_view)

        // Maximum date: today
        historyCalendar.state().edit()
            .setMaximumDate(CalendarDay.today())
            .commit()

        // Minimum date: beginning of the year
        val today = LocalDate.now()
        val firstDayOfYear = CalendarDay.from(today.year, 1, 1)
        historyCalendar.state().edit()
            .setMinimumDate(firstDayOfYear)
            .commit()

        DatabaseEntries.getLoggedInUserEvaluatedDates(auth, Firebase.database) { dates ->
            // Decorator
            val decorator = DayDecorator(auth, Firebase.database, dates, this)
            historyCalendar.addDecorators(decorator)

            // On date click
            historyCalendar.setOnDateChangedListener { _, date, _ ->
                var dateStr = date.toString()
                    .replace("CalendarDay{", "")
                    .replace("}", "")

                val dateStrSplit = dateStr.split("-")
                val year = dateStrSplit[0].toInt()
                val month = dateStrSplit[1].toInt()
                val day = dateStrSplit[2].toInt()
                val entryDate = EntryDate(year, month, day)
                if (dates.contains(entryDate)) {
                    goToActivityWithExtra(EditEvaluationActivity::class.java, "date", entryDate.toString())
                } else {
                    goToActivityWithExtra(CreateEvaluationActivity::class.java, "date", entryDate.toString())
                }
            }
        }
    }
}